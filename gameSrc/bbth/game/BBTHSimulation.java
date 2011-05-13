package bbth.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.FloatMath;
import bbth.engine.ai.Pathfinder;
import bbth.engine.fastgraph.FastGraphGenerator;
import bbth.engine.fastgraph.Wall;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.net.simulation.Simulation;
import bbth.engine.particles.ParticleSystem;
import bbth.engine.ui.UIScrollView;
import bbth.engine.util.Bag;
import bbth.engine.util.MathUtils;
import bbth.engine.util.Timer;
import bbth.game.ai.AIController;
import bbth.game.units.Unit;
import bbth.game.units.UnitManager;
import bbth.game.units.UnitType;

public class BBTHSimulation extends Simulation implements UnitManager {
	private static final int NUM_PARTICLES = 1000;
	private static final float PARTICLE_THRESHOLD = 0.5f;

	public static final ParticleSystem PARTICLES = new ParticleSystem(
			NUM_PARTICLES, PARTICLE_THRESHOLD);
	public static final Paint PARTICLE_PAINT = new Paint();
	static {
		PARTICLE_PAINT.setStrokeWidth(2.f);
	}

	private int timestep;
	private Team team;
	public Player localPlayer, remotePlayer;
	private HashMap<Boolean, Player> playerMap;
	public Player serverPlayer, clientPlayer;
	private AIController aiController;
	private Pathfinder pathFinder;
	private FastGraphGenerator graphGen;
	private FastLineOfSightTester tester;
	private GridAcceleration accel;
	private HashSet<Unit> cachedUnits;
	private Paint paint = new Paint();
	private Bag<Unit> cachedUnitBag = new Bag<Unit>();
	private HashSet<Unit> cachedUnitSet = new HashSet<Unit>();
	public Timer accelTickTimer = new Timer();
	public Timer aiTickTimer = new Timer();
	public Timer entireTickTimer = new Timer();
	public Timer aiControllerTimer = new Timer();
	public Timer serverPlayerTimer = new Timer();
	public Timer clientPlayerTimer = new Timer();
	private static final Random random = new Random();
	private boolean serverReady;
	private boolean clientReady;

	// This is the virtual size of the game
	public static final float GAME_WIDTH = BBTHGame.WIDTH;
	public static final float GAME_HEIGHT = BBTHGame.HEIGHT;

	// Minimal length of a wall
	public static final float MIN_WALL_LENGTH = 5.f;

	// Combo constants
	public static final float UBER_UNIT_THRESHOLD = 7;
	public static final int TUTORIAL_DONE = 13;

	public BBTHSimulation(Team localTeam, LockStepProtocol protocol,
			boolean isServer) {
		// 3 fine timesteps per coarse timestep
		// coarse timestep takes 0.1 seconds
		// user inputs lag 2 coarse timesteps behind
		super(3, 0.1f, 2, protocol, isServer);

		// THIS IS IMPORTANT
		random.setSeed(0);

		serverReady = true;
		clientReady = false;

		aiController = new AIController();
		accel = new GridAcceleration(GAME_WIDTH, GAME_HEIGHT, GAME_WIDTH / 10);

		team = localTeam;
		serverPlayer = new Player(Team.SERVER, aiController, this);
		clientPlayer = new Player(Team.CLIENT, aiController, this);
		localPlayer = (team == Team.SERVER) ? serverPlayer : clientPlayer;
		remotePlayer = (team == Team.SERVER) ? clientPlayer : serverPlayer;

		playerMap = new HashMap<Boolean, Player>();
		playerMap.put(true, serverPlayer);
		playerMap.put(false, clientPlayer);

		graphGen = new FastGraphGenerator(15.0f, GAME_WIDTH, GAME_HEIGHT);
		accel.insertWalls(graphGen.walls);

		pathFinder = new Pathfinder(graphGen.graph);
		tester = new FastLineOfSightTester(15.f, accel);

		aiController.setPathfinder(pathFinder, graphGen.graph, tester, accel);
		aiController.setUpdateFraction(.10f);

		cachedUnits = new HashSet<Unit>();
	}

	public void setupSubviews(UIScrollView view) {
		localPlayer.setupSubviews(view, true);
		remotePlayer.setupSubviews(view, false);
	}

	public Unit getOpponentsMostAdvancedUnit() {
		return remotePlayer.getMostAdvancedUnit();
	}

	public UnitSelector getMyUnitSelector() {
		return localPlayer.getUnitSelector();
	}

	// Just for debugging so we know the simulation isn't stuck
	public int getTimestep() {
		return timestep;
	}

	// Only use BBTHSimulation.randInRange() for things that are supposed to
	// be synced (not particles!)
	public static float randInRange(float min, float max) {
		return (max - min) * random.nextFloat() + min;
	}

	@Override
	protected void simulateTapDown(float x, float y, boolean isServer,
			boolean isHold, boolean isOnBeat) {
		Player player = playerMap.get(isServer);

		if (isOnBeat) {
			float newcombo = player.getCombo() + 1;
			player.setCombo(newcombo);

			if (isHold) {
				player.startWall(x, y);
			} else {
				player.spawnUnit(x, y);
			}
		} else {
			player.setCombo(0);
		}
	}

	@Override
	protected void simulateTapMove(float x, float y, boolean isServer) {
		Player player = playerMap.get(isServer);

		if (!player.settingWall())
			return;
		player.updateWall(x, y);
	}

	@Override
	protected void simulateTapUp(float x, float y, boolean isServer) {
		Player player = playerMap.get(isServer);

		if (!player.settingWall())
			return;
		Wall w = player.endWall(x, y);

		// // insanity check--the below should never do anything
		if (w == null)
			return;

		if (player == remotePlayer) {
			this.generateParticlesForWall(w, player.getTeam());
		}

		addWall(w);
	}

	public void generateParticlesForWall(Wall wall, Team team) {
		int numParticles = 40;

		for (int i = 0; i < numParticles; i++) {
			float posX = wall.a.x * i / numParticles + wall.b.x
					* (numParticles - i) / numParticles;
			float posY = wall.a.y * i / numParticles + wall.b.y
					* (numParticles - i) / numParticles;
			float angle = MathUtils.randInRange(0, 2 * MathUtils.PI);
			float xVel = MathUtils.randInRange(25.f, 50.f)
					* FloatMath.cos(angle);
			float yVel = MathUtils.randInRange(25.f, 50.f)
					* FloatMath.sin(angle);

			PARTICLES.createParticle().circle().velocity(xVel, yVel)
					.shrink(0.1f, 0.15f).radius(3.0f).position(posX, posY)
					.color(team.getRandomShade());
		}
	}

	private void addWall(Wall wall) {
		graphGen.walls.add(wall);
		graphGen.compute();
		accel.clearWalls();
		accel.insertWalls(graphGen.walls);
	}

	@Override
	protected void simulateCustomEvent(float x, float y, int code,
			boolean isServer) {
		Player player = playerMap.get(isServer);

		UnitType type = UnitType.fromInt(code);
		if (type != null) {
			player.setUnitType(type);
		} else if (code == TUTORIAL_DONE) {
			if (isServer) {
				serverReady = true;
			} else {
				clientReady = true;
			}
		}
	}

	private float elapsedTime = 0;

	@Override
	protected void update(float seconds) {
		// if (!serverReady || !clientReady) {
		// return;
		// }

		entireTickTimer.start();
		timestep++;

		// update acceleration data structure
		accelTickTimer.start();
		accel.clearUnits();
		accel.insertUnits(serverPlayer.units);
		accel.insertUnits(clientPlayer.units);
		accelTickTimer.stop();

		aiTickTimer.start();

		aiControllerTimer.start();
		aiController.update();
		aiControllerTimer.stop();

		serverPlayerTimer.start();
		serverPlayer.update(seconds);
		serverPlayerTimer.stop();

		clientPlayerTimer.start();
		clientPlayer.update(seconds);
		clientPlayerTimer.stop();

		// Spawn dudes
		elapsedTime += seconds;
		if (elapsedTime > 1.f) {
			elapsedTime -= 1.f;
			remotePlayer
					.spawnUnit(randInRange(0, GAME_WIDTH), GAME_HEIGHT - 50);
		}

		aiTickTimer.stop();

		PARTICLES.tick(seconds);

		RectF sr = serverPlayer.base.getRect();
		RectF cr = clientPlayer.base.getRect();
		accel.getUnitsInAABB(sr.left, sr.top, sr.right, sr.bottom, cachedUnits);
		for (Unit u : cachedUnits) {
			if (u.getTeam() == Team.CLIENT) {
				serverPlayer.adjustHealth(-10);
			}

			this.notifyUnitDead(u);
		}
		accel.getUnitsInAABB(cr.left, cr.top, cr.right, cr.bottom, cachedUnits);
		for (Unit u : cachedUnits) {
			if (u.getTeam() == Team.SERVER) {
				clientPlayer.adjustHealth(-10);
			}

			this.notifyUnitDead(u);
		}
		entireTickTimer.stop();
	}

	private void drawGrid(Canvas canvas) {
		paint.setColor(Color.DKGRAY);

		// TODO: only draw lines on screen for speed
		for (float x = 0; x < GAME_WIDTH; x += 60) {
			canvas.drawLine(x, 0, x, GAME_HEIGHT, paint);
		}
		for (float y = 0; y < GAME_HEIGHT; y += 60) {
			canvas.drawLine(0, y, GAME_WIDTH, y, paint);
		}
	}

	public void draw(Canvas canvas) {
		drawGrid(canvas);

		localPlayer.draw(canvas);
		remotePlayer.draw(canvas);

		PARTICLES.draw(canvas, PARTICLE_PAINT);

		localPlayer.postDraw(canvas);
		remotePlayer.postDraw(canvas);
	}

	public void drawForMiniMap(Canvas canvas) {
		localPlayer.drawForMiniMap(canvas);
		remotePlayer.drawForMiniMap(canvas);
	}

	@Override
	public void notifyUnitDead(Unit unit) {
		for (int i = 0; i < 10; i++) {
			float angle = MathUtils.randInRange(0, 2 * MathUtils.PI);
			float xVel = MathUtils.randInRange(25.f, 50.f)
					* FloatMath.cos(angle);
			float yVel = MathUtils.randInRange(25.f, 50.f)
					* FloatMath.sin(angle);

			BBTHSimulation.PARTICLES.createParticle().circle()
					.velocity(xVel, yVel).shrink(0.1f, 0.15f).radius(3.0f)
					.position(unit.getX(), unit.getY())
					.color(unit.getTeam().getRandomShade());
		}

		serverPlayer.units.remove(unit);
		clientPlayer.units.remove(unit);
		aiController.removeEntity(unit);
	}

	/**
	 * WILL RETURN THE SAME BAG OVER AND OVER
	 */
	@Override
	public Bag<Unit> getUnitsInCircle(float x, float y, float r) {
		float r2 = r * r;
		cachedUnitBag.clear();
		accel.getUnitsInAABB(x - r, y - r, x + r, y + r, cachedUnitSet);
		for (Unit unit : cachedUnitSet) {
			float dx = x - unit.getX();
			float dy = y - unit.getY();
			if (dx * dx + dy * dy < r2) {
				cachedUnitBag.add(unit);
			}
		}
		return cachedUnitBag;
	}

	/**
	 * WILL RETURN THE SAME BAG OVER AND OVER
	 */
	@Override
	public Bag<Unit> getUnitsIntersectingLine(float x, float y, float x2,
			float y2) {
		cachedUnitBag.clear();

		// calculate axis vector
		float axisX = -(y2 - y);
		float axisY = x2 - x;

		// normalize axis vector
		float axisLen = FloatMath.sqrt(axisX * axisX + axisY * axisY);
		axisX /= axisLen;
		axisY /= axisLen;

		float lMin = axisX * x + axisY * y;
		float lMax = axisX * x2 + axisY * y2;
		if (lMax < lMin) {
			float temp = lMin;
			lMin = lMax;
			lMax = temp;
		}

		accel.getUnitsInAABB(Math.min(x, y), Math.min(y, y2), Math.max(x2, x2),
				Math.max(y, y2), cachedUnitSet);

		for (Unit unit : cachedUnitSet) {
			// calculate projections
			float projectedCenter = axisX * unit.getX() + axisY * unit.getY();
			float radius = unit.getRadius();
			if (!intervalsDontOverlap(projectedCenter - radius, projectedCenter
					+ radius, lMin, lMax)) {
				cachedUnitBag.add(unit);
			}
		}

		return cachedUnitBag;
	}

	private static final boolean intervalsDontOverlap(float min1, float max1,
			float min2, float max2) {
		return (min1 < min2 ? min2 - max1 : min1 - max2) > 0;
	}

	public boolean isReady() {
		// return clientReady && serverReady;
		return true;
	}

	@Override
	public void removeWall(Wall wall) {
		graphGen.walls.remove(wall);
		graphGen.compute();
		accel.clearWalls();
		accel.insertWalls(graphGen.walls);
	}

}

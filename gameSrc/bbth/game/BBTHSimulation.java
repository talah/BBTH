package bbth.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.FloatMath;
import bbth.engine.ai.Pathfinder;
import bbth.engine.fastgraph.FastGraphGenerator;
import bbth.engine.fastgraph.Wall;
import bbth.engine.net.simulation.Hash;
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
	public static enum GameState {
		WAITING_TO_START,
		IN_PROGRESS,
		SERVER_WON,
		CLIENT_WON,
		TIE,
	}
	private GameState gameState = GameState.WAITING_TO_START;

	private static final int NUM_PARTICLES = 1000;
	private static final float PARTICLE_THRESHOLD = 0.5f;

	public static final ParticleSystem PARTICLES = new ParticleSystem(
			NUM_PARTICLES, PARTICLE_THRESHOLD);
	public static final Paint PARTICLE_PAINT = new Paint();
	static {
		PARTICLE_PAINT.setStrokeWidth(2.f);
	}

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
	public static final float GAME_X = BeatTrack.BEAT_TRACK_WIDTH;
	public static final float GAME_Y = 0;
	public static final float GAME_WIDTH = BBTHGame.WIDTH
			- BeatTrack.BEAT_TRACK_WIDTH;
	public static final float GAME_HEIGHT = BBTHGame.HEIGHT;

	// Minimal length of a wall
	public static final float MIN_WALL_LENGTH = 5.f;

	// Combo constants
	public static final float UBER_UNIT_THRESHOLD = 5;
	public static final int TUTORIAL_DONE_EVENT = 13;
	public static final int MUSIC_STOPPED_EVENT = 69;

	long placement_tip_start_time;

	public BBTHSimulation(Team localTeam, LockStepProtocol protocol,
			boolean isServer) {
		// 3 fine timesteps per coarse timestep
		// coarse timestep takes 0.1 seconds
		// user inputs lag 2 coarse timesteps behind
		super(3, 0.1f, 2, protocol, isServer);

		// THIS IS IMPORTANT
		random.setSeed(0);

		aiController = new AIController();
		accel = new GridAcceleration(GAME_WIDTH, GAME_HEIGHT, GAME_WIDTH / 10);

		team = localTeam;
		serverPlayer = new Player(Team.SERVER, aiController, this,
				team == Team.SERVER);
		clientPlayer = new Player(Team.CLIENT, aiController, this,
				team == Team.CLIENT);
		localPlayer = (team == Team.SERVER) ? serverPlayer : clientPlayer;
		remotePlayer = (team == Team.SERVER) ? clientPlayer : serverPlayer;

		playerMap = new HashMap<Boolean, Player>();
		playerMap.put(true, serverPlayer);
		playerMap.put(false, clientPlayer);

		graphGen = new FastGraphGenerator(15.0f, GAME_WIDTH, GAME_HEIGHT);
		graphGen.compute();
		accel.insertWalls(graphGen.walls);

		pathFinder = new Pathfinder(graphGen.graph);
		tester = new FastLineOfSightTester(15.f, accel);

		aiController.setPathfinder(pathFinder, graphGen.graph, tester, accel);
		aiController.setUpdateFraction(.10f);

		cachedUnits = new HashSet<Unit>();
		
	}

	public GameState getGameState() {
		return gameState;
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

	// Only use BBTHSimulation.randInRange() for things that are supposed to
	// be synced (not particles!)
	public static float randInRange(float min, float max) {
		return (max - min) * random.nextFloat() + min;
	}

	@Override
	protected void simulateTapDown(float x, float y, boolean isServer,
			boolean isHold, boolean isOnBeat) {
		Player player = playerMap.get(isServer);

		if (x < 0 || y < 0)
			return;
		
		if (placement_tip_start_time == 0 && player.getMostAdvancedUnit() != null) {
			if (((isServer && y > player.getMostAdvancedUnit().getY()) || 
					(!isServer && y < player.getMostAdvancedUnit().getY()))) {
				placement_tip_start_time = System.currentTimeMillis();
			}
		}

		if (BBTHGame.DEBUG || isOnBeat) {
			if (isHold) {
				player.startWall(x, y);
			} else {
				float newcombo = player.getCombo() + 1;
				player.setCombo(newcombo);
				
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

		if (x < 0 || y < 0) {
			generateWall(player);
		} else {
			player.updateWall(x, y);
		}
	}

	@Override
	protected void simulateTapUp(float x, float y, boolean isServer) {
		Player player = playerMap.get(isServer);
		generateWall(player);
	}

	@Override
	protected void simulateCustomEvent(float x, float y, int code,
			boolean isServer) {
		Player player = playerMap.get(isServer);

		UnitType type = UnitType.fromInt(code);
		if (type != null) {
			player.setUnitType(type);
		} else if (code == TUTORIAL_DONE_EVENT) {
			if (isServer) {
				serverReady = true;
			} else {
				clientReady = true;
			}
		} else if (code == MUSIC_STOPPED_EVENT) {
			endTheGame();
		}
	}

	/**
	 * Creates a wall out of the given player, and lets the AI know about it.
	 */
	public void generateWall(Player player) {
		if (!player.settingWall())
			return;

		Wall w = player.endWall();
		if (w == null)
			return;

		if (player != localPlayer) {
			BBTHSimulation.generateParticlesForWall(w, player.getTeam());
		}

		addWall(w);
	}

	public static void generateParticlesForWall(Wall wall, Team team) {
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
	public void removeWall(Wall wall) {
		graphGen.walls.remove(wall);
		graphGen.compute();
		accel.clearWalls();
		accel.insertWalls(graphGen.walls);
	}

	@Override
	protected void update(float seconds) {
		if (!isReady()) {
			return;
		}

		if (gameState == GameState.WAITING_TO_START) {
			gameState = GameState.IN_PROGRESS;
		}

		// DON'T ADVANCE THE SIMULATION WHEN WE AREN'T PLAYING
		if (gameState != GameState.IN_PROGRESS) {
			return;
		}

		entireTickTimer.start();

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

		aiTickTimer.stop();

		PARTICLES.tick(seconds);

		RectF sr = serverPlayer.base.getRect();
		RectF cr = clientPlayer.base.getRect();
		accel.getUnitsInAABB(sr.left, sr.top, sr.right, sr.bottom, cachedUnits);
		for (Unit u : cachedUnits) {
			if (u.getTeam() == Team.CLIENT) {
				if (!BBTHGame.DEBUG) {
					serverPlayer.adjustHealth(-10);
				}

				u.takeDamage(u.getHealth());
			}
		}
		accel.getUnitsInAABB(cr.left, cr.top, cr.right, cr.bottom, cachedUnits);
		for (Unit u : cachedUnits) {
			if (u.getTeam() == Team.SERVER) {
				if (!BBTHGame.DEBUG) {
					clientPlayer.adjustHealth(-10);
				}

				u.takeDamage(u.getHealth());
			}
		}

		if (localPlayer.getHealth() <= 0 || remotePlayer.getHealth() <= 0) {
			endTheGame();
		}

		entireTickTimer.stop();
	}

	private void drawGrid(Canvas canvas) {
		paint.setColor(Color.DKGRAY);

		for (float x = 0; x < GAME_WIDTH; x += 60) {
			canvas.drawLine(x, 0, x, GAME_HEIGHT, paint);
		}
		for (float y = 0; y < GAME_HEIGHT; y += 60) {
			canvas.drawLine(0, y, GAME_WIDTH, y, paint);
		}
	}

	public void draw(Canvas canvas) {
		drawWavefronts(canvas);
		drawGrid(canvas);

		localPlayer.draw(canvas, team == Team.SERVER);
		remotePlayer.draw(canvas, team == Team.SERVER);

		PARTICLES.draw(canvas, PARTICLE_PAINT);

		if (BBTHGame.DEBUG) {
			graphGen.draw(canvas);
		}

		localPlayer.postDraw(canvas);
		remotePlayer.postDraw(canvas);
	}

	private void drawWavefronts(Canvas canvas) {
		Unit serverAdvUnit = serverPlayer.getMostAdvancedUnit();
		Unit clientAdvUnit = clientPlayer.getMostAdvancedUnit();
		float serverWavefrontY = serverAdvUnit != null ? serverAdvUnit.getY() + 10
				: 0;
		float clientWavefrontY = clientAdvUnit != null ? clientAdvUnit.getY() - 10
				: BBTHSimulation.GAME_HEIGHT;
		paint.setStyle(Style.FILL);

		// server wavefront
		paint.setColor(Team.SERVER.getWavefrontColor());
		canvas.drawRect(0, 0, BBTHSimulation.GAME_WIDTH,
				Math.min(clientWavefrontY, serverWavefrontY), paint);

		// client wavefront
		paint.setColor(Team.CLIENT.getWavefrontColor());
		canvas.drawRect(0, Math.max(clientWavefrontY, serverWavefrontY),
				BBTHSimulation.GAME_WIDTH, BBTHSimulation.GAME_HEIGHT, paint);

		// overlapped wavefronts
		if (serverWavefrontY > clientWavefrontY) {
			paint.setColor(Color.rgb(63, 0, 63));
			canvas.drawRect(0, clientWavefrontY, BBTHSimulation.GAME_WIDTH,
					serverWavefrontY, paint);
		}

		// server wavefront line
		paint.setColor(Team.SERVER.getUnitColor());
		canvas.drawLine(0, serverWavefrontY, BBTHSimulation.GAME_WIDTH, serverWavefrontY, paint);

		// client wavefront line
		paint.setColor(Team.CLIENT.getUnitColor());
		canvas.drawLine(0, clientWavefrontY, BBTHSimulation.GAME_WIDTH, clientWavefrontY, paint);
	}

	@Override
	public void notifyUnitDead(Unit unit) {
//		for (int i = 0; i < 10; i++) {
//			float angle = MathUtils.randInRange(0, 2 * MathUtils.PI);
//			float xVel = MathUtils.randInRange(25.f, 50.f)
//					* FloatMath.cos(angle);
//			float yVel = MathUtils.randInRange(25.f, 50.f)
//					* FloatMath.sin(angle);
//
//			BBTHSimulation.PARTICLES.createParticle().circle()
//					.velocity(xVel, yVel).shrink(0.1f, 0.15f).radius(3.0f)
//					.position(unit.getX(), unit.getY())
//					.color(unit.getTeam().getRandomShade());
//		}

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
		return (clientReady && serverReady);
	}

	@Override
	protected int getHash() {
		int hash = 0;
		hash = Hash.mix(hash, serverPlayer.getHash());
		hash = Hash.mix(hash, clientPlayer.getHash());
		for (int i = 0, n = graphGen.walls.size(); i < n; i++) {
			hash = Hash.mix(hash, graphGen.walls.get(i).getHash());
		}
		return hash;
	}

	public void setBothPlayersReady() {
		clientReady = serverReady = true;
	}

	private void endTheGame() {
		float serverHealth = Math.max(0, serverPlayer.getHealth());
		float clientHealth = Math.max(0, clientPlayer.getHealth());
		gameState = (serverHealth > clientHealth) ? GameState.SERVER_WON : (serverHealth < clientHealth) ? GameState.CLIENT_WON : GameState.TIE;
	}
}

package bbth.game;

import java.util.HashMap;
import java.util.HashSet;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import bbth.engine.ai.Pathfinder;
import bbth.engine.fastgraph.FastGraphGenerator;
import bbth.engine.fastgraph.Wall;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.net.simulation.Simulation;
import bbth.engine.ui.UIScrollView;
import bbth.game.ai.AIController;
import bbth.game.units.Unit;
import bbth.game.units.UnitType;

public class BBTHSimulation extends Simulation {
	private int timestep;
	private Team team;
	public Player localPlayer, remotePlayer;
	private HashMap<Boolean, Player> playerMap;
	private Player serverPlayer, clientPlayer;
	private AIController aiController;
	private Pathfinder pathFinder;
	private FastGraphGenerator graphGen;
	private FastLineOfSightTester tester;
	private GridAcceleration accel;
	private HashSet<Unit> cachedUnits;
	private Paint paint = new Paint();

	// This is the virtual size of the game
	public static final float GAME_WIDTH = BBTHGame.WIDTH;
	public static final float GAME_HEIGHT = BBTHGame.HEIGHT + 400;

	// Minimal length of a wall
	public static final float MIN_WALL_LENGTH = 5.f;

	public BBTHSimulation(Team localTeam, LockStepProtocol protocol, boolean isServer) {
		// 6 fine timesteps per coarse timestep
		// coarse timestep takes 0.1 seconds
		// user inputs lag 2 coarse timesteps behind
		super(6, 0.1f, 2, protocol, isServer);

		aiController = new AIController();
		accel = new GridAcceleration(GAME_WIDTH, GAME_HEIGHT, GAME_WIDTH / 10);

		team = localTeam;
		serverPlayer = new Player(Team.SERVER, aiController);
		clientPlayer = new Player(Team.CLIENT, aiController);
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
		aiController.setUpdateFraction(.3f);

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

	@Override
	protected void simulateTapDown(float x, float y, boolean isServer, boolean isHold, boolean isOnBeat) {
		Player player = playerMap.get(isServer);

		if (isHold) {
			player.startWall(x, y);
		} else {
			player.spawnUnit(x, y);
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

		addWall(w);
	}

	private void addWall(Wall wall) {
		graphGen.walls.add(wall);
		graphGen.compute();
		accel.clearWalls();
		accel.insertWalls(graphGen.walls);
	}

	@Override
	protected void simulateCustomEvent(int code, boolean isServer) {
		Player player = playerMap.get(isServer);

		player.setUnitType(UnitType.fromInt(code));
	}

	@Override
	protected void update(float seconds) {
		timestep++;

		// update acceleration data structure
		accel.clearUnits();
		accel.insertUnits(serverPlayer.units);
		accel.insertUnits(clientPlayer.units);

		aiController.update();
		serverPlayer.update(seconds);
		clientPlayer.update(seconds);
		RectF sr = serverPlayer.base.getRect();
		RectF cr = clientPlayer.base.getRect();

		accel.getUnitsInAABB(sr.left, sr.top, sr.right, sr.bottom, cachedUnits);
		for (Unit u : cachedUnits) {
			if (u.getTeam() == Team.CLIENT)
				serverPlayer.adjustHealth(-10);
		}

		accel.getUnitsInAABB(cr.left, cr.top, cr.right, cr.bottom, cachedUnits);
		for (Unit u : cachedUnits) {
			if (u.getTeam() == Team.SERVER)
				clientPlayer.adjustHealth(-10);
		}
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
	}

	public void drawForMiniMap(Canvas canvas) {
		localPlayer.drawForMiniMap(canvas);
		remotePlayer.drawForMiniMap(canvas);
	}
}
package bbth.game;

import java.util.HashMap;

import android.graphics.Canvas;
import android.util.Log;
import bbth.engine.ai.Pathfinder;
import bbth.engine.fastgraph.FastGraphGenerator;
import bbth.engine.fastgraph.SimpleLineOfSightTester;
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
	private HashMap<Boolean, Player> playerMap;
	private Player localPlayer, remotePlayer;
	private Player serverPlayer, clientPlayer;
	private AIController aiController;
	private Pathfinder pathFinder;
	private FastGraphGenerator graphGen;
	private SimpleLineOfSightTester tester;

	// This is the virtual size of the game
	public static final float GAME_WIDTH = BBTHGame.WIDTH;
	public static final float GAME_HEIGHT = BBTHGame.HEIGHT * 4;

	public BBTHSimulation(Team localTeam, LockStepProtocol protocol,
			boolean isServer) {
		// 6 fine timesteps per coarse timestep
		// coarse timestep takes 0.1 seconds
		// user inputs lag 2 coarse timesteps behind
		super(6, 0.1f, 2, protocol, isServer);

		aiController = new AIController();

		team = localTeam;
		serverPlayer = new Player(Team.SERVER, aiController);
		clientPlayer = new Player(Team.CLIENT, aiController);
		localPlayer = (team == Team.SERVER) ? serverPlayer : clientPlayer;
		remotePlayer = (team == Team.SERVER) ? clientPlayer : serverPlayer;

		playerMap = new HashMap<Boolean, Player>();
		playerMap.put(true, serverPlayer);
		playerMap.put(false, clientPlayer);

		graphGen = new FastGraphGenerator(15.0f, GAME_WIDTH, GAME_HEIGHT);
		pathFinder = new Pathfinder(graphGen.graph);
		tester = new SimpleLineOfSightTester(15.f);
		tester.setBounds(0, 0, GAME_WIDTH, GAME_HEIGHT);
		tester.walls = graphGen.walls;

		aiController.setPathfinder(pathFinder, graphGen.graph, tester);
		aiController.setUpdateFraction(.3f);
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
	protected void simulateTapDown(float x, float y, boolean isServer,
			boolean isHold, boolean isOnBeat) {
		Player player = playerMap.get(isServer);
		player.startWall(x, y);
		player.spawnUnit(x, y);
	}

	@Override
	protected void simulateTapMove(float x, float y, boolean isServer) {
		Player player = playerMap.get(isServer);
		
		if (!player.settingWall()) return;
		player.updateWall(x, y);
	}

	@Override
	protected void simulateTapUp(float x, float y, boolean isServer) {
		Player player = playerMap.get(isServer);

		if (!player.settingWall()) return;
		Wall w = player.endWall(x, y);
		if (w == null)
			return;

		graphGen.walls.add(w);
		graphGen.compute();
		tester.updateWalls();
	}

	@Override
	protected void simulateCustomEvent(int code, boolean isServer) {
		Player player = playerMap.get(isServer);

		player.setUnitType(UnitType.fromInt(code));
	}

	@Override
	protected void update(float seconds) {
		timestep++;

		aiController.update();
		serverPlayer.update(seconds);
		clientPlayer.update(seconds);
	}

	public void draw(Canvas canvas) {
		localPlayer.draw(canvas);
		remotePlayer.draw(canvas);
	}

	public void drawForMiniMap(Canvas canvas) {
		localPlayer.drawForMiniMap(canvas);
		remotePlayer.drawForMiniMap(canvas);
	}
}

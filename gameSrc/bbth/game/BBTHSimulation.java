package bbth.game;

import android.graphics.Canvas;
import bbth.engine.ai.Pathfinder;
import bbth.engine.fastgraph.FastGraphGenerator;
import bbth.engine.fastgraph.SimpleLineOfSightTester;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.net.simulation.Simulation;
import bbth.engine.ui.UIScrollView;
import bbth.game.ai.AIController;
import bbth.game.units.Unit;

public class BBTHSimulation extends Simulation {
	private int timestep;
	private Team team;
	private Player localPlayer, remotePlayer;
	private Player serverPlayer, clientPlayer;
	private AIController aiController;
	private Pathfinder pathFinder;
	private FastGraphGenerator graphGen;

	// This is the virtual size of the game
	public static final float GAME_WIDTH = BBTHGame.WIDTH;
	public static final float GAME_HEIGHT = BBTHGame.HEIGHT * 4;

	public BBTHSimulation(Team localTeam, LockStepProtocol protocol, boolean isServer) {
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

		graphGen = new FastGraphGenerator(15.0f, GAME_WIDTH, GAME_HEIGHT);
		pathFinder = new Pathfinder(graphGen.graph);
		SimpleLineOfSightTester tester = new SimpleLineOfSightTester(15.f);
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
	protected void simulateTapDown(float x, float y, boolean isServer, boolean isHold, boolean isOnBeat) {
		// if (!isOnBeat) return;

		if (isServer) {
			serverPlayer.spawnUnit(x, y);
		} else {
			clientPlayer.spawnUnit(x, y);
		}
	}

	@Override
	protected void simulateTapMove(float x, float y, boolean isServer) {
	}

	@Override
	protected void simulateTapUp(float x, float y, boolean isServer) {
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

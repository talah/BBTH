package bbth.game;

import android.graphics.Canvas;
import bbth.engine.ai.MapGrid;
import bbth.engine.ai.Pathfinder;
import bbth.engine.fastgraph.SimpleLineOfSightTester;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.net.simulation.Simulation;
import bbth.engine.ui.UIView;
import bbth.game.ai.AIController;
import bbth.game.units.Unit;

public class BBTHSimulation extends Simulation {
	private int timestep;
	private Team team;
	private Player localPlayer, remotePlayer;
	private Player serverPlayer, clientPlayer;
	private AIController aiController;
	private Pathfinder pathFinder;
	private MapGrid grid;

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
		
		int width = (int) BBTHGame.WIDTH;
		int height = (int) BBTHGame.HEIGHT;
		grid = new MapGrid(width, height, width / 10, height / 10);
		pathFinder = new Pathfinder(grid);
		SimpleLineOfSightTester tester = new SimpleLineOfSightTester(10);
		aiController.setPathfinder(pathFinder, grid, tester);
	}

	public void setupSubviews(UIView view) {
		localPlayer.setupSubviews(view);
		remotePlayer.setupSubviews(view);
	}

	public Unit getOpponentsMostAdvancedUnit() {
		return remotePlayer.getMostAdvancedUnit();
	}

	// Just for debugging so we know the simulation isn't stuck
	public int getTimestep() {
		return timestep;
	}

	@Override
	protected void simulateTapDown(float x, float y, boolean isServer, boolean isHold, boolean isOnBeat) {
		//if (!isOnBeat) return;
		
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
}

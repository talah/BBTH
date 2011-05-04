package bbth.game;

import android.graphics.Canvas;
import bbth.game.ai.AIController;
import bbth.net.simulation.LockStepProtocol;
import bbth.net.simulation.Simulation;
import bbth.ui.UIView;

public class BBTHSimulation extends Simulation {
	private int timestep;
	private Team team;
	private Player localPlayer, remotePlayer;
	private Player serverPlayer, clientPlayer;
	private AIController aiController;

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

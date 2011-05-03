package bbth.game;

import android.graphics.Canvas;
import bbth.net.simulation.LockStepProtocol;
import bbth.net.simulation.Simulation;
import bbth.ui.UIView;

public class BBTHSimulation extends Simulation {
	private int timestep;
	private Team team;
	private Player localPlayer, remotePlayer;
	private AIController aiController;

	public BBTHSimulation(Team localTeam, LockStepProtocol protocol) {
		// 6 fine timesteps per coarse timestep
		// coarse timestep takes 0.1 seconds
		// user inputs lag 2 coarse timesteps behind
		super(6, 0.1f, 2, protocol);

		aiController = new AIController();

		team = localTeam;
		localPlayer = new Player(team, aiController);

		if (team == Team.TEAM_0) {
			remotePlayer = new Player(Team.TEAM_1, aiController);
		} else {
			remotePlayer = new Player(Team.TEAM_0, aiController);
		}
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
	protected void simulateTapDown(float x, float y, boolean isLocal, boolean isOnBeat) {
		if (isLocal) {
			localPlayer.spawnUnit(x, y);
		} else {
			remotePlayer.spawnUnit(x, y);
		}
	}

	@Override
	protected void simulateTapMove(float x, float y, boolean isLocal) {
	}

	@Override
	protected void simulateTapUp(float x, float y, boolean isLocal) {
	}

	@Override
	protected void update(float seconds) {
		timestep++;

		aiController.update();

		if (team == Team.TEAM_1) {
			localPlayer.update(seconds);
			remotePlayer.update(seconds);
		} else {
			remotePlayer.update(seconds);
			localPlayer.update(seconds);
		}
	}

	public void draw(Canvas canvas) {
		if (team == Team.TEAM_0) {
			remotePlayer.draw(canvas);
			localPlayer.draw(canvas);
		} else {
			localPlayer.draw(canvas);
			remotePlayer.draw(canvas);
		}
	}
}

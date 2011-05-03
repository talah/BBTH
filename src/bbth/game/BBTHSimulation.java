package bbth.game;

import bbth.net.simulation.LockStepProtocol;
import bbth.net.simulation.Simulation;

public class BBTHSimulation extends Simulation {

	public BBTHSimulation(LockStepProtocol protocol) {
		super(6, 0.1f, 2, protocol);
	}

	@Override
	protected void simulateTapDown(float x, float y, boolean isLocal, boolean isOnBeat) {
	}

	@Override
	protected void simulateTapMove(float x, float y, boolean isLocal) {
	}

	@Override
	protected void simulateTapUp(float x, float y, boolean isLocal) {
	}

	@Override
	protected void update(float seconds) {
	}
}

package bbth.game;

import android.graphics.Canvas;
import bbth.net.simulation.LockStepProtocol;
import bbth.net.simulation.Simulation;

public class BBTHSimulation extends Simulation {

	private int timestep;
	private Unit test_unit;
	
	public BBTHSimulation(LockStepProtocol protocol) {
		// 6 fine timesteps per coarse timestep
		// coarse timestep takes 0.1 seconds
		// user inputs lag 2 coarse timesteps behind
		super(6, 0.1f, 2, protocol);
		
		test_unit = new Unit();
	}

	// Just for debugging so we know the simulation isn't stuck
	public int getTimestep() {
		return timestep;
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
		timestep++;
		test_unit.update(seconds);
	}
	
	public void draw(Canvas canvas) {
		test_unit.draw(canvas);
	}
}

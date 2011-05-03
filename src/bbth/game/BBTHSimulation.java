package bbth.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import bbth.net.simulation.LockStepProtocol;
import bbth.net.simulation.Simulation;

public class BBTHSimulation extends Simulation {
	private Unit test_unit;
	
	public BBTHSimulation(LockStepProtocol protocol) {
		super(6, 0.1f, 2, protocol);
		
		test_unit = new Unit();
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
		test_unit.update(seconds);
	}
	
	public void draw(Canvas canvas) {
		test_unit.draw(canvas);
	}
}

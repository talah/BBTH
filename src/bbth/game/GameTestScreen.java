package bbth.game;

import android.graphics.Canvas;
import bbth.core.GameActivity;
import bbth.net.bluetooth.Bluetooth;
import bbth.net.simulation.LockStepProtocol;
import bbth.ui.UIView;

public class GameTestScreen extends UIView {
	private BBTHSimulation sim;
	private Bluetooth bluetooth;
	private LockStepProtocol protocol;

	public GameTestScreen() {
		super(null);
		
		protocol = new LockStepProtocol();
		bluetooth = new Bluetooth(GameActivity.instance, protocol);
		
		sim = new BBTHSimulation(Team.TEAM_0, protocol);
	}

	@Override
	public void onTouchDown(float x, float y) {
		sim.simulateTapDown(x, y, true, false);
	}
	
	@Override
	public void onUpdate(float seconds) {
		sim.update(seconds);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		sim.draw(canvas);
	}
}

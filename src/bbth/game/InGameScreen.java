package bbth.game;

import android.graphics.Canvas;
import android.graphics.Paint.Align;
import bbth.net.bluetooth.Bluetooth;
import bbth.net.bluetooth.State;
import bbth.net.simulation.LockStepProtocol;
import bbth.ui.UILabel;
import bbth.ui.UIView;

public class InGameScreen extends UIView {

	private BBTHSimulation sim;
	private UILabel label;
	private Bluetooth bluetooth;

	public InGameScreen(Bluetooth bluetooth, LockStepProtocol protocol) {
		super(null);

		label = new UILabel("", null);
		label.setTextSize(10);
		label.setPosition(10, 10);
		label.setSize(BBTHGame.WIDTH - 20, 10);
		label.setTextAlign(Align.CENTER);
		addSubview(label);

		this.bluetooth = bluetooth;
		sim = new BBTHSimulation(protocol);
	}

	@Override
	public void onStop() {
		// Disconnect when we lose focus
		bluetooth.disconnect();
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	@Override
	public void onUpdate(float seconds) {
		// Show the timestep for debugging
		label.setText("" + sim.getTimestep());

		// Go back to the menu if we disconnect
		if (bluetooth.getState() != State.CONNECTED) {
			nextScreen = new GameSetupScreen();
		}

		sim.onUpdate(seconds);
	}

	@Override
	public void onTouchDown(float x, float y) {
		// TODO: set this based on the music
		boolean isOnBeat = true;

		super.onTouchDown(x, y);
		sim.recordTapDown(x, y, isOnBeat);
	}

	@Override
	public void onTouchMove(float x, float y) {
		super.onTouchMove(x, y);
		sim.recordTapMove(x, y);
	}

	@Override
	public void onTouchUp(float x, float y) {
		super.onTouchUp(x, y);
		sim.recordTapUp(x, y);
	}

	/**
	 * Stupid method necessary because of android's weird context/activity mess.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode) {
		bluetooth.onActivityResult(requestCode, resultCode);
	}
}

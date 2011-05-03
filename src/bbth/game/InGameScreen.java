package bbth.game;

import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import bbth.net.bluetooth.Bluetooth;
import bbth.net.bluetooth.State;
import bbth.net.simulation.LockStepProtocol;
import bbth.ui.UILabel;
import bbth.ui.UIScrollView;
import bbth.util.MathUtils;

public class InGameScreen extends UIScrollView {

	private BBTHSimulation sim;
	private UILabel label;
	private Bluetooth bluetooth;
	private Team team;

	public InGameScreen(Team playerTeam, Bluetooth bluetooth, LockStepProtocol protocol) {
		super(null);

		MathUtils.resetRandom(0);
		
		this.team = playerTeam;
		
		// Set up the scrolling!
		this.setSize(BBTHGame.WIDTH, BBTHGame.HEIGHT);
		this.setPosition(0, 0);
		this.setScrolls(false);
		
		// Test labels
		label = new UILabel("", null);
		label.setTextSize(10);
		label.setPosition(10, 10);
		label.setSize(BBTHGame.WIDTH - 20, 10);
		label.setTextAlign(Align.CENTER);
		addSubview(label);

		this.bluetooth = bluetooth;
		sim = new BBTHSimulation(playerTeam, protocol);
		sim.setupSubviews(this);
	}

	@Override
	public void onStop() {
		// Disconnect when we lose focus
		bluetooth.disconnect();
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// Draw a background grid thing so we know we're not hallucinating
		canvas.translate(-this.pos_x, -this.pos_y);
		RectF bounds = this._content_bounds;
		for (float f = bounds.top; f <= bounds.bottom; f += 20) {
			canvas.drawLine(bounds.left, f, bounds.right, f, this._scroll_paint);
		}
		for (float f = bounds.left; f <= bounds.right; f += 20) {
			canvas.drawLine(f, bounds.top, f, bounds.bottom, this._track_paint);
		}
		canvas.translate(this.pos_x, this.pos_y);
		
		// Draw the game
		sim.draw(canvas);
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
		
		// Center the scroll on the most advanced enemy
		Unit mostAdvanced = sim.getOpponentsMostAdvancedUnit();
		if (mostAdvanced != null) {
			this.scrollTo(mostAdvanced.getX(), mostAdvanced.getY());
		}
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

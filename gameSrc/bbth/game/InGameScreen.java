package bbth.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import bbth.engine.net.bluetooth.Bluetooth;
import bbth.engine.net.bluetooth.State;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.sound.Beat.BeatType;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UIScrollView;
import bbth.engine.util.MathUtils;
import bbth.game.BeatTrack.Song;
import bbth.game.units.Unit;

public class InGameScreen extends UIScrollView {
	private BBTHSimulation sim;
	private UILabel label;
	private Bluetooth bluetooth;
	private Team team;
	private BeatTrack beatTrack;
	private Paint paint = new Paint();
	private final RectF minimapRect;

	public InGameScreen(Team playerTeam, Bluetooth bluetooth,
			LockStepProtocol protocol) {
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
		sim = new BBTHSimulation(playerTeam, protocol, team == Team.SERVER);
		sim.setupSubviews(this);

		// Set up sound stuff
		beatTrack = new BeatTrack(Song.RETRO);
		beatTrack.startMusic();

		minimapRect = new RectF(BBTHGame.WIDTH - 40, BBTHGame.HEIGHT / 2,
				BBTHGame.WIDTH, BBTHGame.HEIGHT);
	}

	@Override
	public void onStop() {
		beatTrack.stopMusic();

		// Disconnect when we lose focus
		bluetooth.disconnect();
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// Draw the game
		canvas.translate(-this.pos_x, -this.pos_y);
		sim.draw(canvas);
		canvas.translate(this.pos_x, this.pos_y);

		// Overlay the beat track
		beatTrack.draw(canvas);

		// Overlay the unit selector
		sim.getMyUnitSelector().draw(canvas);

		// Draw minimap
		float scaleX = minimapRect.width() / BBTHSimulation.GAME_WIDTH;
		float scaleY = minimapRect.height() / BBTHSimulation.GAME_HEIGHT;
		canvas.translate(minimapRect.left, minimapRect.top);
		canvas.scale(scaleX, scaleY);
		sim.drawForMiniMap(canvas);
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.STROKE);
		canvas.drawRect(0, 0, BBTHSimulation.GAME_WIDTH,
				BBTHSimulation.GAME_HEIGHT, paint);
		canvas.drawRect(this.pos_x, this.pos_y, BBTHGame.WIDTH + this.pos_x,
				BBTHGame.HEIGHT + this.pos_y, paint);
		paint.setStyle(Style.FILL);
	}

	@Override
	public void onUpdate(float seconds) {
		// Show the timestep for debugging
		label.setText("" + sim.getTimestep());

		// Go back to the menu and stop the music if we disconnect
		if (bluetooth.getState() != State.CONNECTED) {
			beatTrack.stopMusic();
			nextScreen = new GameSetupScreen();
		}

		sim.onUpdate(seconds);
		beatTrack.refreshBeats();

		// Center the scroll on the most advanced enemy
		Unit mostAdvanced = sim.getOpponentsMostAdvancedUnit();
		if (mostAdvanced != null) {
			this.scrollTo(mostAdvanced.getX(), mostAdvanced.getY()
					- BBTHGame.HEIGHT / 2);
		}
	}

	@Override
	public void onTouchDown(float x, float y) {
		super.onTouchDown(x, y);
		
		int unitType = sim.getMyUnitSelector().checkUnitChange(x, y);
		if (unitType >= 0) {
			sim.recordCustomEvent(unitType);
			return;
		}
		
		BeatType beatType = beatTrack.checkTouch(sim, x + this.pos_x, y + this.pos_y);

		// Unpack!
		boolean isHold = (beatType == BeatType.HOLD);
		boolean isOnBeat = (beatType != BeatType.REST);

		sim.recordTapDown(x + this.pos_x, y + this.pos_y, isHold, isOnBeat);
	}

	@Override
	public void onTouchMove(float x, float y) {
		super.onTouchMove(x, y);
		sim.recordTapMove(x + this.pos_x, y + this.pos_y);
	}

	@Override
	public void onTouchUp(float x, float y) {
		super.onTouchUp(x, y);
		sim.recordTapUp(x + this.pos_x, y + this.pos_y);
	}

	/**
	 * Stupid method necessary because of android's weird context/activity mess.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode) {
		bluetooth.onActivityResult(requestCode, resultCode);
	}
}

package bbth.game;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import bbth.net.bluetooth.Bluetooth;
import bbth.net.bluetooth.State;
import bbth.net.simulation.LockStepProtocol;
import bbth.ui.UILabel;
import bbth.ui.UIScrollView;
import bbth.util.MathUtils;
import bbth.core.GameActivity;
import bbth.sound.*;
import bbth.sound.MusicPlayer.OnCompletionListener;

public class InGameScreen extends UIScrollView {

	private static final float BEAT_LINE_X = 25;
	private static final float BEAT_LINE_Y = 135;

	private BBTHSimulation sim;
	private UILabel label;
	private Bluetooth bluetooth;
	private Team team;
	private BeatTracker beatTracker;
	private int combo;
	private int score;
	private String comboStr, scoreStr;
	private BeatPattern beatPattern;
	private MusicPlayer musicPlayer;
	private List<Beat> beatsInRange;
	private Paint paint;

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
		sim = new BBTHSimulation(playerTeam, protocol, team == Team.SERVER);
		sim.setupSubviews(this);

		// Setup paint
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(10);

		// Setup music stuff
		beatPattern = new SimpleBeatPattern(385, 571, 30000);
		musicPlayer = new MusicPlayer(GameActivity.instance, R.raw.bonusroom);
		musicPlayer.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MusicPlayer mp) {
				beatTracker = new BeatTracker(musicPlayer, beatPattern);
				beatsInRange = new ArrayList<Beat>();
				mp.play();
			}
		});

		beatTracker = new BeatTracker(musicPlayer, beatPattern);
		beatsInRange = new ArrayList<Beat>();

		// Setup score stuff
		score = 0;
		scoreStr = String.valueOf(score);
		combo = 0;
		comboStr = String.valueOf(combo);

		// Start playing the music!
		musicPlayer.play();
	}

	@Override
	public void onStop() {
		musicPlayer.stop();
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

		// Draw the music section of the screen
		beatTracker.drawBeats(beatsInRange, BEAT_LINE_X, BEAT_LINE_Y, canvas, paint);
		paint.setColor(Color.WHITE);
		canvas.drawLine(0, BEAT_LINE_Y - Beat.RADIUS, 50, BEAT_LINE_Y - Beat.RADIUS, paint);
		canvas.drawLine(0, BEAT_LINE_Y + Beat.RADIUS, 50, BEAT_LINE_Y + Beat.RADIUS, paint);
		canvas.drawText(comboStr, 25, _height - 10, paint);
		// canvas.drawText(_scoreStr, 25, HEIGHT - 2, _paint);
		canvas.drawLine(50, 0, 50, _height, paint);

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

		// Get beats in range
		beatsInRange = beatTracker.getBeatsInRange(-400, 1500);
	}

	@Override
	public void onTouchDown(float x, float y) {
		boolean isOnBeat = beatTracker.onTouchDown();
		boolean isHold = false; // TODO: set this from song
		if (isOnBeat) {
			++score;
			++combo;
			scoreStr = String.valueOf(score);
			comboStr = "x" + String.valueOf(combo);
		} else {
			combo = 0;
			comboStr = "x" + String.valueOf(combo);
		}

		super.onTouchDown(x, y);
		sim.recordTapDown(x, y, isHold, isOnBeat);
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

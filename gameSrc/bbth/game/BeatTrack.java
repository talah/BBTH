package bbth.game;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import bbth.engine.core.GameActivity;
import bbth.engine.sound.Beat;
import bbth.engine.sound.BeatPattern;
import bbth.engine.sound.BeatTracker;
import bbth.engine.sound.MusicPlayer;
import bbth.engine.sound.SimpleBeatPattern;
import bbth.engine.sound.MusicPlayer.OnCompletionListener;

public class BeatTrack {
	private static final int BEAT_TRACK_WIDTH = 50;
	private static final float BEAT_LINE_X = 25;
	private static final float BEAT_LINE_Y = 135;
	private static final float BEAT_CIRCLE_RADIUS = Beat.RADIUS + BeatTracker.TOLERANCE / 10.f;

	private BeatTracker beatTracker;
	private int combo;
	private int score;
	private String comboStr, scoreStr;
	private BeatPattern beatPattern;
	private MusicPlayer musicPlayer;
	private List<Beat> beatsInRange;
	private Paint paint;
	
	public BeatTrack() {
		// Setup music stuff
		beatPattern = new SimpleBeatPattern(385, 571, 300000);
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

		// Setup paint
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextSize(10);
		paint.setStrokeWidth(2.f);
		paint.setStrokeCap(Cap.ROUND);
	}

	public void startMusic() {
		musicPlayer.play();
	}
	
	public void stopMusic() {
		musicPlayer.stop();
	}

	public void draw(Canvas canvas) {
		paint.setARGB(127, 0, 0, 0);
		canvas.drawRect(0, 0, BEAT_TRACK_WIDTH, BBTHGame.HEIGHT, paint);

		paint.setARGB(127, 255, 255, 255);
		canvas.drawLine(BEAT_LINE_X, 0, BEAT_LINE_X, BEAT_LINE_Y - BEAT_CIRCLE_RADIUS, paint);
		canvas.drawLine(BEAT_LINE_X, BEAT_LINE_Y + BEAT_CIRCLE_RADIUS, BEAT_LINE_X, BBTHGame.HEIGHT, paint);

		beatTracker.drawBeats(beatsInRange, BEAT_LINE_X, BEAT_LINE_Y, canvas, paint);
		
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.WHITE);
		canvas.drawCircle(BEAT_LINE_X, BEAT_LINE_Y, BEAT_CIRCLE_RADIUS, paint);
		paint.setStyle(Style.FILL);
		
		paint.setColor(Color.WHITE);
		// canvas.drawLine(50, 0, 50, BBTHGame.HEIGHT, paint);		
		canvas.drawText(comboStr, 25, BBTHGame.HEIGHT - 10, paint);
		// canvas.drawText(_scoreStr, 25, HEIGHT - 2, _paint);
	}

	public void refreshBeats() {
		// Get beats in range
		beatsInRange = beatTracker.getBeatsInRange(-400, 1500);
	}

	public boolean simulateTouch(BBTHSimulation sim, float x, float y) {
		Beat.BeatType beatType = beatTracker.onTouchDown();
		boolean isHold = (beatType == Beat.BeatType.HOLD);
		boolean isOnBeat = (beatType != Beat.BeatType.REST);
		if (isOnBeat) {
			++score;
			++combo;
			scoreStr = String.valueOf(score);
			comboStr = "x" + String.valueOf(combo);
		} else {
			combo = 0;
			comboStr = "x" + String.valueOf(combo);
		}

		// TODO I'd like to move this back into InGameScreen; this involves passing two booleans.
		sim.recordTapDown(x, y, isHold, isOnBeat);
		return isOnBeat;
	}
}

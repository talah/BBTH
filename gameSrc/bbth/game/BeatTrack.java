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
import bbth.engine.sound.CompositeBeatPattern;
import bbth.engine.sound.MusicPlayer;
import bbth.engine.sound.MusicPlayer.OnCompletionListener;
import bbth.engine.sound.SimpleBeatPattern;
import bbth.engine.sound.SoundManager;

/**
 * A complete beat track for a single song.  Handles music, hit and miss sounds, scoring.
 * @author jardini
 *
 */
public class BeatTrack {
	public enum Song {
		DONKEY_KONG, RETRO;
	}
	
	private static final int BEAT_TRACK_WIDTH = 50;
	private static final float BEAT_LINE_X = 25;
	private static final float BEAT_LINE_Y = 135;
	private static final float BEAT_CIRCLE_RADIUS = 2.f + BeatTracker.TOLERANCE / 10.f;

	private static final int MAX_SOUNDS = 8;
	private static final int HIT_SOUND_ID = 0;
	private static final int MISS_SOUND_ID = 1;
	
	private SoundManager soundManager;
	private BeatTracker beatTracker;
	private int combo;
	private int score;
	private String comboStr;
	// scoreStr;
	private BeatPattern beatPattern;
	private MusicPlayer musicPlayer;
	private List<Beat> beatsInRange;
	private Paint paint;
	
	public BeatTrack(Song song) {
		// Setup song-specific stuff
		switch (song) {
		case DONKEY_KONG:
			beatPattern = new SimpleBeatPattern(385, 571, 300000);
			musicPlayer = new MusicPlayer(GameActivity.instance, R.raw.bonusroom);
			break;
		case RETRO:
			BeatPattern []patterns = new BeatPattern[3];
			patterns[0] = new SimpleBeatPattern(350, 481 * 2, 4810 * 4);
			List<Beat> beats = new ArrayList<Beat>();
			for (int phrase = 0; phrase < 4; ++phrase) {
				for (int i = 0; i < 4; ++i) {
					beats.add(Beat.tap(481));
				}
				for (int i = 0; i < 2; ++i) {
					beats.add(Beat.tap(240));
					beats.add(Beat.tap(241));
				}
				for (int i = 0; i < 6; ++i) {
					beats.add(Beat.rest(481));
				}
				for (int i = 0; i < 2; ++i) {
					beats.add(Beat.hold(481 * 2));
					beats.add(Beat.rest(481 * 2));
				}
			}
			patterns[1] = new SimpleBeatPattern(beats);
			patterns[2] = new SimpleBeatPattern(0, 481 * 2, 4810 * 4);
			beatPattern = new CompositeBeatPattern(patterns);
			musicPlayer = new MusicPlayer(GameActivity.instance, R.raw.retrobit);
			break;
		}
		
		// Setup general stuff		
		musicPlayer.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MusicPlayer mp) {
				beatTracker = new BeatTracker(musicPlayer, beatPattern);
				beatsInRange = new ArrayList<Beat>();
				mp.play();
			}
		});
		
		soundManager = new SoundManager(GameActivity.instance, MAX_SOUNDS);
		soundManager.addSound(HIT_SOUND_ID, R.raw.tambourine);
		soundManager.addSound(MISS_SOUND_ID, R.raw.drum2);

		beatTracker = new BeatTracker(musicPlayer, beatPattern);
		beatsInRange = new ArrayList<Beat>();

		// Setup score stuff
		score = 0;
		// scoreStr = String.valueOf(score);
		combo = 0;
		comboStr = String.valueOf(combo);

		// Setup paint
		paint = new Paint();
		paint.setTextSize(10);
		paint.setStrokeWidth(2.f);
		paint.setStrokeCap(Cap.ROUND);
		paint.setAntiAlias(true);
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
		beatsInRange = beatTracker.getBeatsInRange(-700, 1900);
	}

	public Beat.BeatType checkTouch(BBTHSimulation sim, float x, float y) {
		Beat.BeatType beatType = beatTracker.onTouchDown();
		boolean isOnBeat = (beatType != Beat.BeatType.REST);
		if (isOnBeat) {
			soundManager.play(HIT_SOUND_ID);
			++score;
			++combo;
			// scoreStr = String.valueOf(score);
			comboStr = "x" + String.valueOf(combo);
		} else {
			soundManager.play(MISS_SOUND_ID);
			combo = 0;
			comboStr = "x" + String.valueOf(combo);
		}

		return beatType;
	}
}

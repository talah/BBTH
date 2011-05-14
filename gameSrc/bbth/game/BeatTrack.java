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
import bbth.engine.sound.BeatTracker;
import bbth.engine.sound.MusicPlayer;
import bbth.engine.sound.MusicPlayer.OnCompletionListener;
import bbth.engine.sound.SoundManager;

/**
 * A complete beat track for a single song. Handles music, hit and miss sounds,
 * scoring.
 * 
 * @author jardini
 * 
 */
public class BeatTrack {

	public static final int BEAT_TRACK_WIDTH = 50;
	public static final float BEAT_LINE_X = 25;
	public static final float BEAT_LINE_Y = BBTHGame.HEIGHT - 50;
	public static final float BEAT_CIRCLE_RADIUS = 2.f + BeatTracker.TOLERANCE / 10.f;
	
	public static final long COMBO_PULSE_TIME = 500;
	public static final long COMBO_BRAG_TIME = 2000;

	private static final int MAX_SOUNDS = 8;
	private final int HIT_SOUND_ID;
	private final int MISS_SOUND_ID;
	private final int HOLD_SOUND_ID;
	
	private Song song;
	private SoundManager soundManager;
	private BeatTracker beatTracker;
	private boolean isHolding;
	private int holdId;
	private int combo;
	private int score;
	private String comboStr;
	// scoreStr;
	private MusicPlayer musicPlayer;
	private List<Beat> beatsInRange;
	private Paint paint;
	
	private long last_combo_time;
	private float brag_text_pos;
	private boolean display_uber_brag;
	private String combo_brag_text;
	
	public BeatTrack(Song s, OnCompletionListener listener) {
		loadSong(s);
		beatsInRange = new ArrayList<Beat>();
		
		last_combo_time = 0;
		display_uber_brag = false;
		
		// Setup general stuff		
		musicPlayer.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MusicPlayer mp) {
				loadSong(song);
				beatsInRange = new ArrayList<Beat>();
				mp.play();
			}
		});
		musicPlayer.setOnCompletionListener(listener);

		soundManager = new SoundManager(GameActivity.instance, MAX_SOUNDS);
		HIT_SOUND_ID = soundManager.addSound(R.raw.tambourine);
		MISS_SOUND_ID = soundManager.addSound(R.raw.derp2);
		// TODO: Find a good hold sound
		HOLD_SOUND_ID = soundManager.addSound(R.raw.tambourine);
		isHolding = false;

		// Setup score stuff
		score = 0;
		// scoreStr = String.valueOf(score);
		combo = 0;
		comboStr = "x" + String.valueOf(combo);

		// Setup paint
		paint = new Paint();
		paint.setTextSize(20.0f);
		paint.setFakeBoldText(true);
		paint.setStrokeCap(Cap.ROUND);
		paint.setAntiAlias(true);
		
		brag_text_pos = BBTHGame.WIDTH/2.0f + BEAT_TRACK_WIDTH/2.0f - paint.measureText("COMBO " + comboStr + ": UBER UNIT!")/2.0f;
	}
	
	// loads a song and an associated beat track
	public final void loadSong(Song s) {
		song = s;
		musicPlayer = new MusicPlayer(GameActivity.instance, s.songId);
		beatTracker = new BeatTracker(musicPlayer, s.trackId);
	}

	public void startMusic() {
		musicPlayer.play();
	}

	public void stopMusic() {
		musicPlayer.stop();
	}

	public void draw(Canvas canvas) {
		// paint.setARGB(127, 0, 0, 0);
		// paint.setStrokeWidth(2.f);
		// canvas.drawRect(0, 0, BEAT_TRACK_WIDTH, BBTHGame.HEIGHT, paint);

		paint.setARGB(127, 255, 255, 255);
		paint.setStrokeWidth(2.f);
		canvas.drawLine(BEAT_LINE_X, 0, BEAT_LINE_X, BEAT_LINE_Y - BEAT_CIRCLE_RADIUS, paint);
		canvas.drawLine(BEAT_LINE_X, BEAT_LINE_Y + BEAT_CIRCLE_RADIUS, BEAT_LINE_X, BBTHGame.HEIGHT, paint);

		beatTracker.drawBeats(beatsInRange, BEAT_LINE_X, BEAT_LINE_Y, canvas, paint);

		paint.setStyle(Style.STROKE);
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(2.f);
		canvas.drawLine(BEAT_LINE_X - BEAT_TRACK_WIDTH / 2, BEAT_LINE_Y - BEAT_CIRCLE_RADIUS, BEAT_LINE_X + BEAT_TRACK_WIDTH / 2, BEAT_LINE_Y - BEAT_CIRCLE_RADIUS, paint);
		canvas.drawLine(BEAT_LINE_X - BEAT_TRACK_WIDTH / 2, BEAT_LINE_Y + BEAT_CIRCLE_RADIUS, BEAT_LINE_X + BEAT_TRACK_WIDTH / 2, BEAT_LINE_Y + BEAT_CIRCLE_RADIUS, paint);
		paint.setStyle(Style.FILL);

		paint.setColor(Color.WHITE);
		// canvas.drawLine(50, 0, 50, BBTHGame.HEIGHT, paint);
		//canvas.drawText("Combo", 3, BBTHGame.HEIGHT - 25, paint);
		long timesincecombo = System.currentTimeMillis() - last_combo_time;
		float old_text_size = paint.getTextSize();
		if (timesincecombo < COMBO_PULSE_TIME) {
			paint.setTextSize(30.0f - (timesincecombo*10.0f/COMBO_PULSE_TIME));
		}
		canvas.drawText(comboStr, 15, BBTHGame.HEIGHT - 10, paint);
		
		if (combo >= BBTHSimulation.UBER_UNIT_THRESHOLD && combo % BBTHSimulation.UBER_UNIT_THRESHOLD == 0) {
			display_uber_brag = true;
			combo_brag_text = "COMBO " + comboStr + ": UBER UNIT!";
		}
		
		if (display_uber_brag && timesincecombo < COMBO_BRAG_TIME) {
			paint.setTextSize(20.0f);
			paint.setAlpha((int) (255 - (timesincecombo/2%255)));
			canvas.drawText(combo_brag_text, brag_text_pos, BBTHGame.HEIGHT/2.0f, paint);
		} else if (display_uber_brag) {
			display_uber_brag = false;
		}
		paint.setTextSize(old_text_size);

		// canvas.drawText(_scoreStr, 25, HEIGHT - 2, _paint);
	}

	public void refreshBeats() {
		// Get beats in range
		beatsInRange = beatTracker.getBeatsInRange(-700, 6000);
	}

	public Beat.BeatType onTouchDown(float x, float y) {
		Beat.BeatType beatType = beatTracker.onTouchDown();
		boolean isOnBeat = (beatType != Beat.BeatType.REST);
		if (isOnBeat) {
			if (beatType == Beat.BeatType.HOLD){
				isHolding = true;
				holdId = soundManager.play(HOLD_SOUND_ID);
			} else {
				soundManager.play(HIT_SOUND_ID);
			}
			++score;
			// scoreStr = String.valueOf(score);
			// NOTE: Combos should also be tracked in bbthSimulation
			if (beatType != Beat.BeatType.HOLD) {
				++combo;
				last_combo_time = System.currentTimeMillis();
				comboStr = "x" + String.valueOf(combo);
			}
		} else {
			soundManager.play(MISS_SOUND_ID);
			combo = 0;
			comboStr = "x" + String.valueOf(combo);
		}

		return beatType;
	}
	
	public void onTouchUp(float x, float y) {
		if (isHolding) {
			soundManager.stop(holdId);
			isHolding = false;
		}
	}

	public float getCombo() {
		return combo;
	}

	public boolean isPlaying() {
		return musicPlayer.isPlaying();
	}
}

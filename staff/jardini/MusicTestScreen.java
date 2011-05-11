package jardini;

import static bbth.game.BBTHGame.HEIGHT;
import static bbth.game.BBTHGame.WIDTH;

import java.util.ArrayList;
import java.util.List;

import kvgishen.TitleScreen;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.FloatMath;
import bbth.engine.core.GameScreen;
import bbth.engine.particles.ParticleSystem;
import bbth.engine.sound.Beat;
import bbth.engine.sound.BeatPattern;
import bbth.engine.sound.BeatTracker;
import bbth.engine.sound.CompositeBeatPattern;
import bbth.engine.sound.MusicPlayer;
import bbth.engine.sound.MusicPlayer.OnCompletionListener;
import bbth.engine.sound.SimpleBeatPattern;
import bbth.engine.sound.SoundManager;
import bbth.engine.util.ColorUtils;
import bbth.engine.util.MathUtils;
import bbth.game.R;

/**
 * A simple test screen for music playback and tapping (I wanted this in the
 * sound package but Eclipse wasn't recognizing R.raw in there)
 * 
 * @author jardini
 */
public class MusicTestScreen extends GameScreen {
	
	private static final String MAIN_MENU_TEXT = "Menu";
	private static final String MINIMAP_TEXT = "Minimap";
	
	private static final float BEAT_LINE_X = 25;
	private static final float BEAT_LINE_Y = 135;
	
	private static final int HIT_SOUND_ID = 0;
	private static final int MISS_SOUND_ID = 1;
	
	private Paint _paint;
	private ParticleSystem _particles;
	private RectF _comboBox;
	
	private MusicPlayer _musicPlayer;
	private SoundManager _soundManager;
	private BeatTracker _beatTracker;
	private int _millisPerBeat;
	private int _score;
	private int _combo;
	private String _scoreStr, _comboStr;
	private List<Beat> _beats;
	
	public MusicTestScreen(Context context) {
		_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		_paint.setTextAlign(Align.CENTER);
		_paint.setTextSize(10);
		
		_score = _combo = 0;
		_scoreStr = String.valueOf(_score);
		_comboStr = "x" + String.valueOf(_combo);
		
		_comboBox = new RectF(0, 0, 0, 0);
		_particles = new ParticleSystem(100, 0.5f);
		
		_soundManager = new SoundManager(context, 8);
		_soundManager.addSound(HIT_SOUND_ID, R.raw.tambourine);
		_soundManager.addSound(MISS_SOUND_ID, R.raw.drum2);
		_musicPlayer = new MusicPlayer(context, R.raw.retrobit);
		_musicPlayer.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MusicPlayer mp) {
				nextScreen = new TitleScreen(null);
			}
		});
		//_millisPerBeat = 571;
		_millisPerBeat = 481;
		
		/*
		BeatPattern simplePattern1 = new SimpleBeatPattern(350, _millisPerBeat, 3500);
		BeatPattern simplePattern2 = new SimpleBeatPattern(0, _millisPerBeat, 35000);
		BeatPattern []subpatterns = { simplePattern1, simplePattern2 };
		BeatPattern compositePattern = new CompositeBeatPattern(subpatterns);
		Beat[] beats = new Beat[10];
		beats[0] = Beat.tap(571);
		beats[1] = Beat.tap(571);
		beats[2] = Beat.rest(571);
		beats[3] = Beat.tap(285);
		beats[4] = Beat.tap(286);
		beats[5] = Beat.tap(571);
		beats[6] = Beat.tap(190);
		beats[7] = Beat.tap(190);
		beats[8] = Beat.tap(191);
		beats[9] = Beat.hold(1142);
		
		BeatPattern customPattern = new SimpleBeatPattern(385, beats);
		_beatTracker = new BeatTracker(_musicPlayer, compositePattern);
		*/
		
		List<Beat> beats = new ArrayList<Beat>();
		for (int phrase = 0; phrase < 4; ++phrase) {
			for (int i = 0; i < 4; ++i) {
				beats.add(Beat.tap(481));
			}
			for (int i = 0; i < 4; ++i) {
				beats.add(Beat.tap(240));
				beats.add(Beat.tap(241));
			}
			for (int i = 0; i < 4; ++i) {
				beats.add(Beat.rest(481));
			}
		}
		
		_beatTracker = new BeatTracker(_musicPlayer, new SimpleBeatPattern(0, beats));
		
		_beats = new ArrayList<Beat>();
		_musicPlayer.play();
	}
	
	@Override
	public void onUpdate(float seconds) {
		_beats = _beatTracker.getBeatsInRange(-400, 1500);
		_particles.tick(seconds);
	}
	
	@Override
	public void onTouchDown(float x, float y) {
		if (_comboBox.contains(x, y)) {
			float xPos = (_comboBox.left + _comboBox.right) / 2;
			float yPos = (_comboBox.top + _comboBox.bottom) / 2;
			for (int i = 0; i < 90; ++i) {
				float angle = MathUtils.randInRange(0, 2 * MathUtils.PI);
				float xVel = MathUtils.randInRange(5.f, 25.f) * FloatMath.cos(angle);
				float yVel = MathUtils.randInRange(5.f, 25.f) * FloatMath.sin(angle);
				_particles.createParticle().circle().color(ColorUtils.randomHSV(0, 360, 0, 1, 0.5f, 1)).velocity(xVel, yVel).shrink(0.3f, 0.4f).radius(2.0f).position(xPos, yPos);
				_comboBox.bottom = _comboBox.top;
				_comboBox.left = _comboBox.right;
			}
		} else {
			
			Beat.BeatType beatType = _beatTracker.onTouchDown();
			boolean onBeat = (beatType != Beat.BeatType.REST);
			if (onBeat) {
				_soundManager.play(HIT_SOUND_ID);
				++_score;
				++_combo;
				_scoreStr = String.valueOf(_score);
				_comboStr = "x" + String.valueOf(_combo);
				
				if (_combo == 5) {
					_comboBox.left = MathUtils.randInRange(WIDTH * 0.25f, WIDTH * 0.7f);
					_comboBox.right = _comboBox.left + 10;
					_comboBox.top = MathUtils.randInRange(0, HEIGHT * 0.8f);
					_comboBox.bottom = _comboBox.top + 10;
				} else if (_combo > 5) {
					_comboBox.left -= 1;
					_comboBox.right += 1;
					_comboBox.top -= 1;
					_comboBox.bottom += 1;
				}
			} else {
				_soundManager.play(MISS_SOUND_ID);
				_combo = 0;
				_comboStr = "x" + String.valueOf(_combo);
				_comboBox.bottom = _comboBox.top;
				_comboBox.left = _comboBox.right;
			}
		}
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		// draw beats section
		_beatTracker.drawBeats(_beats, BEAT_LINE_X, BEAT_LINE_Y, canvas, _paint);
		_paint.setColor(Color.WHITE);
		canvas.drawLine(0, BEAT_LINE_Y - Beat.RADIUS, 50, BEAT_LINE_Y - Beat.RADIUS, _paint);
		canvas.drawLine(0, BEAT_LINE_Y + Beat.RADIUS, 50, BEAT_LINE_Y + Beat.RADIUS, _paint);
		canvas.drawText(_comboStr, 25, HEIGHT - 10, _paint);
		// canvas.drawText(_scoreStr, 25, HEIGHT - 2, _paint);
		canvas.drawLine(50, 0, 50, HEIGHT, _paint);
		
		// draw map / creatures section
		_paint.setColor(Color.YELLOW);
		canvas.drawArc(_comboBox, 0, 360, true, _paint);
		
		_paint.setColor(Color.WHITE);
		canvas.drawLine(50, HEIGHT - 20, WIDTH - 50, HEIGHT - 20, _paint);
		_paint.setStyle(Style.STROKE);
		canvas.drawCircle(100, HEIGHT - 10, 8, _paint);
		canvas.drawRect(WIDTH / 2 - 8, HEIGHT - 18, WIDTH / 2 + 8, HEIGHT - 2, _paint);
		canvas.drawLine(212, HEIGHT - 2, 228, HEIGHT - 2, _paint);
		canvas.drawLine(212, HEIGHT - 2, 220, HEIGHT - 18, _paint);
		canvas.drawLine(220, HEIGHT - 18, 228, HEIGHT - 2, _paint);
		
		// draw minimap section
		_paint.setStyle(Style.FILL);
		canvas.drawText(MAIN_MENU_TEXT, WIDTH - 25, 14, _paint);
		canvas.drawLine(WIDTH - 50, 20, WIDTH, 20, _paint);
		canvas.drawLine(WIDTH - 50, 0, WIDTH - 50, HEIGHT, _paint);
		canvas.drawText(MINIMAP_TEXT, WIDTH - 25, HEIGHT / 2, _paint);
		
		// draw particles
		_particles.draw(canvas, _paint);
	}
	
	@Override
	public void onStart() {
		_musicPlayer.play();
	}
	
	@Override
	public void onStop() {
		_musicPlayer.pause();
	}
}

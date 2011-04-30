package bbth.game;

import java.util.ArrayList;

import java.util.List;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import bbth.core.GameScreen;
import bbth.sound.BeatTracker;
import bbth.sound.MusicPlayer;
import bbth.sound.SimpleBeatPattern;
import static bbth.game.BBTHGame.*;

/**
 * A simple test screen for music playback and tapping
 * (I wanted this in the sound package but Eclipse wasn't recognizing R.raw in there)
 * @author jardini
 *
 */
public class MusicTestScreen extends GameScreen {
	
	private static final float BEAT_LINE_HEIGHT = HEIGHT * 0.75f;
	private static final float BEAT_RADIUS = 10;
	private static final float TOLERANCE = 80; // millisecond difference in what is still considered a valid touch
	
	private MusicPlayer _musicPlayer;
	private BeatTracker _beatTracker;
	private Paint _paint;
	private int _millisPerBeat;
	private int _score;
	private List<Integer> _beatOffsets;
	private String _scoreStr;
	
	public MusicTestScreen(Context context) {
		_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		_paint.setTextAlign(Align.CENTER);
		_paint.setTextSize(12);
		
		_score = 0;
		_scoreStr = String.valueOf(_score);
		
		_musicPlayer = new MusicPlayer(context, R.raw.bonusroom);
		_millisPerBeat = 571;
		_beatTracker = new BeatTracker(_musicPlayer, new SimpleBeatPattern(385, _millisPerBeat));
		_beatOffsets = new ArrayList<Integer>();
		_musicPlayer.play();
	}
	
	@Override
	public void onUpdate(float seconds) {
		_beatOffsets = _beatTracker.getBeatOffsetsInRange(-500, 1500);
	}
	
	@Override
	public void onTouchDown(float x, float y) {
		int offset = _beatTracker.getClosestBeatOffset();
		if (Math.abs(offset) < TOLERANCE) {
			++_score;
			_scoreStr = String.valueOf(_score);
		}
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		for (int i = 0; i < _beatOffsets.size(); ++i) {
			int offset = _beatOffsets.get(i);
			if (Math.abs(offset) < TOLERANCE) {
				_paint.setColor(Color.GREEN);
			} else {
				_paint.setColor(Color.RED);
			}
			canvas.drawCircle(WIDTH / 2, HEIGHT * 0.75f - _beatOffsets.get(i) / 10, BEAT_RADIUS, _paint);
		}
		_paint.setColor(Color.WHITE);
		canvas.drawLine(0, BEAT_LINE_HEIGHT - BEAT_RADIUS, WIDTH , BEAT_LINE_HEIGHT - BEAT_RADIUS, _paint);
		canvas.drawLine(0, BEAT_LINE_HEIGHT + BEAT_RADIUS, WIDTH , BEAT_LINE_HEIGHT + BEAT_RADIUS, _paint);
		canvas.drawText("Testing music...", WIDTH / 2, 10, _paint);
		canvas.drawText(_scoreStr, WIDTH / 2, HEIGHT - 10, _paint);
	}	
	
	public void onStart() {
		_musicPlayer.play();
	}
	
	public void onStop() {
		_musicPlayer.pause();
	}
}

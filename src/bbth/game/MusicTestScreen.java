package bbth.game;

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
 * A simple test screen for music playback
 * (I wanted this in the sound package but Eclipse wasn't recognizing R.raw in there)
 * @author jardini
 *
 */
public class MusicTestScreen extends GameScreen {
	
	private MusicPlayer _musicPlayer;
	private BeatTracker _beatTracker;
	private Paint _paint;
	private int _millisPerBeat;
	private int _score;
	private int _beatOffset;
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
		_beatOffset = _beatTracker.getClosestBeatOffset();
		_musicPlayer.play();
	}
	
	@Override
	public void onUpdate(float seconds) {
		_beatOffset = _beatTracker.getClosestBeatOffset();		
	}
	
	@Override
	public void onTouchDown(float x, float y) {
		int offset = _beatTracker.getClosestBeatOffset();
		if (Math.abs(offset) < 80) {
			++_score;
			_scoreStr = String.valueOf(_score);
		}
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		_paint.setColor(Color.WHITE);
		canvas.drawCircle(WIDTH / 2, HEIGHT / 2 - (_beatOffset / 10), 12, _paint);
		canvas.drawLine(0, HEIGHT / 2, WIDTH , HEIGHT / 2, _paint);
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

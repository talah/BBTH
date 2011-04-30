package bbth.game;

import java.util.ArrayList;

import java.util.List;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import bbth.core.GameScreen;
import bbth.sound.BeatPattern;
import bbth.sound.BeatTracker;
import bbth.sound.CustomBeatPattern;
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
	
	private static final String MAP_TEXT = "Map";
	private static final String MENU_TEXT = "Creatures";
	private static final String MAIN_MENU_TEXT = "Main Menu";
	private static final String MINIMAP_TEXT = "Minimap";
	
	private static final float BEAT_LINE_HEIGHT = HEIGHT * 0.75f;
	private static final float BEAT_RADIUS = 8;
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
		_paint.setTextSize(10);
		
		_score = 0;
		_scoreStr = String.valueOf(_score);
		
		_musicPlayer = new MusicPlayer(context, R.raw.bonusroom);
		_millisPerBeat = 571;
		
		int []beatLengths = { 571, 571, 571, 285, 286, 571, 190, 190, 191, 1142 };
		BeatPattern simplePattern = new SimpleBeatPattern(385, _millisPerBeat);
		BeatPattern customPattern = new CustomBeatPattern(385, beatLengths, true);
		_beatTracker = new BeatTracker(_musicPlayer, customPattern);
		
		
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
			canvas.drawCircle(25, HEIGHT * 0.75f - _beatOffsets.get(i) / 10, BEAT_RADIUS, _paint);
		}
		_paint.setColor(Color.WHITE);
		
		// draw beats section
		canvas.drawLine(0, BEAT_LINE_HEIGHT - BEAT_RADIUS, 50, BEAT_LINE_HEIGHT - BEAT_RADIUS, _paint);
		canvas.drawLine(0, BEAT_LINE_HEIGHT + BEAT_RADIUS, 50, BEAT_LINE_HEIGHT + BEAT_RADIUS, _paint);
		canvas.drawText(_scoreStr, 25, HEIGHT - 10, _paint);
		canvas.drawLine(50, 0, 50, HEIGHT, _paint);
		
		// draw map / creatures section
		canvas.drawText(MAP_TEXT, WIDTH / 2, HEIGHT / 2, _paint);
		canvas.drawLine(50, HEIGHT - 20, WIDTH - 50, HEIGHT - 20, _paint);
		canvas.drawText(MENU_TEXT, WIDTH / 2, HEIGHT - 5, _paint);
		
		// draw minimap section
		canvas.drawText(MAIN_MENU_TEXT, WIDTH - 25, 14, _paint);
		canvas.drawLine(WIDTH - 50, 20, WIDTH, 20, _paint);
		canvas.drawLine(WIDTH - 50, 0, WIDTH - 50, HEIGHT, _paint);
		canvas.drawText(MINIMAP_TEXT, WIDTH - 25, HEIGHT / 2, _paint);

	}	
	
	public void onStart() {
		_musicPlayer.play();
	}
	
	public void onStop() {
		_musicPlayer.pause();
	}
}

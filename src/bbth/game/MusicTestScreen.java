package bbth.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import bbth.core.GameScreen;
import bbth.sound.MusicPlayer;
import static bbth.game.BBTHGame.*;

/**
 * A simple test screen for music playback
 * (I wanted this in the sound package but Eclipse wasn't recognizing R.raw in there)
 * @author jardini
 *
 */
public class MusicTestScreen extends GameScreen {
	
	private MusicPlayer _musicPlayer;
	private Paint _paint;
	private float _beatsPerSecond;
	private long _songStartTime;
	
	public MusicTestScreen(Context context) {
		_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		_paint.setTextAlign(Align.CENTER);
		_paint.setTextSize(18);
		_musicPlayer = new MusicPlayer(context, R.raw.bonusroom);
		_beatsPerSecond = 1.66f;
		_musicPlayer.play();
		_songStartTime = System.currentTimeMillis();
	}
	
	@Override
	public void onUpdate(float seconds) {
		
	}
	
	@Override
	public void onTouchDown(float x, float y) {
		System.currentTimeMillis();
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		_paint.setColor(Color.WHITE);
		canvas.drawText("Testing music...", WIDTH / 2, HEIGHT / 2, _paint);
	}	
	
	
	public void onStart() {
		_musicPlayer.play();
	}
	
	public void onStop() {
		_musicPlayer.pause();
	}
}

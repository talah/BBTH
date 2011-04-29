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
 * A simple test screen for music playback.
 * (I wanted this in the sound package but Eclipse wasn't recognizing R.raw in there)
 * @author jardini
 *
 */
public class MusicTestScreen extends GameScreen {
	private MusicPlayer _musicPlayer;
	private Paint _paint;
	
	public MusicTestScreen(Context context) {
		_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		_paint.setTextAlign(Align.CENTER);
		_paint.setTextSize(18);
		_musicPlayer = new MusicPlayer(context, R.raw.bonusroom);
		_musicPlayer.play();
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		_paint.setColor(Color.WHITE);
		canvas.drawText("Testing music...", WIDTH / 2, HEIGHT / 2, _paint);
	}
}

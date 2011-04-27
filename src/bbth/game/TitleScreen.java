package bbth.game;

import bbth.core.GameScreen;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;

public class TitleScreen extends GameScreen {
	@Override
	public void onDraw(Canvas canvas) {
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(30.f);
		
		canvas.drawText("Hello, world!", 160.f, 90.f, paint);
	}
}

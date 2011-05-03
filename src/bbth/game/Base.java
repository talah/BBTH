package bbth.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import bbth.ui.UIView;

public class Base extends UIView {
	private Paint paint;
	
	public Base(Object tag) {
		super(tag);

		this.setSize(BBTHGame.WIDTH, 20);
		
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLUE);
	}

	public void onDraw(Canvas canvas) {
		canvas.drawRect(_rect, paint);
	}
}

package bbth.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import bbth.engine.ui.UIView;

public class Base extends UIView {
	private Paint paint;
	
	public Base(Object tag) {
		super(tag);

		this.setSize(BBTHGame.WIDTH, 20);
		
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLUE);
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawRect(_rect, paint);
	}
}

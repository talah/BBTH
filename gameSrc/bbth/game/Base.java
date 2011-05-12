package bbth.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import bbth.engine.ui.UIView;

public class Base extends UIView {
	public static final float BASE_HEIGHT = 20;
	private Paint paint;
	private Team team;

	public Base(Object tag, Team team) {
		super(tag);

		this.setSize(BBTHGame.WIDTH, BASE_HEIGHT);
		
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(team.getBaseColor());
		
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawRect(_rect, paint);
	}
	
	public RectF getRect()
	{
		return _rect;
	}
	
}

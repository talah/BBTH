package bbth.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import bbth.engine.ui.UIView;

public class Base extends UIView {
	private Paint paint;
	private Team team;

	public Base(Object tag, Team team) {
		super(tag);

		this.setSize(BBTHGame.WIDTH, 20);
		
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		
		if (team == Team.CLIENT) {
			paint.setColor(Color.CYAN);
		} else if (team == Team.SERVER) {
			paint.setColor(Color.MAGENTA);
		}
		
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawRect(_rect, paint);
	}
}

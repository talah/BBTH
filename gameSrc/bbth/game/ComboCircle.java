package bbth.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIView;

public class ComboCircle extends UIView {
	
	private Team team;
	public float radius;
	private Paint paint;

	public ComboCircle(Team team) {
		this.team = team;
		setAnchor(Anchor.CENTER_CENTER);
		
		paint = new Paint();
		paint.setStrokeWidth(2.0f);
		paint.setStrokeJoin(Join.ROUND);
		paint.setTextSize(20);
		paint.setAntiAlias(true);
		paint.setColor(team.getUnitColor());
		paint.setStrokeCap(Cap.ROUND);
		
		radius = -1.f;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (radius > 0) {
			canvas.drawCircle(center.x, center.y, radius, paint);
		}
	}

}

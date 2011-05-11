package bbth.game.units;

import android.graphics.Canvas;
import android.graphics.Paint;
import bbth.engine.util.MathUtils;
import bbth.game.Team;

public class UberUnit extends Unit {
	public UberUnit(Team team, Paint p) {
		super(team, p);
	}
	
	private float[] outline = { 0f, -15f, 10f, 10f,
	                            10f, 10f, -10f, 10f,
	                            -10f, 10f, 0f, -15f,
	                          };
	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		
		canvas.translate(getX(), getY());
		canvas.rotate(MathUtils.toDegrees(getHeading()) + 90);
		canvas.drawLines(outline, paint);
		
		canvas.restore();
	}

	@Override
	public UnitType getType() {
		return UnitType.UBER;
	}

	@Override
	public void drawForMiniMap(Canvas canvas) {
		canvas.drawCircle(this.getX(), this.getY(), 10, paint);
	}
}

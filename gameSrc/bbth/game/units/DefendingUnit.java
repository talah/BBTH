package bbth.game.units;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import bbth.engine.util.MathUtils;
import bbth.game.Team;

public class DefendingUnit extends Unit {
	public DefendingUnit(Team team, Paint p) {
		super(team, p);
	}

	private static final float LINE_LENGTH = 6f;

	RectF rect = new RectF(-3f, -3f, 3f, 3f);

	@Override
	public void draw(Canvas canvas) {
		canvas.save();

		canvas.translate(getX(), getY());
		canvas.rotate(MathUtils.toDegrees(getHeading()) + 90);

		canvas.drawRect(rect, paint);
		canvas.drawLine(0f, 0f, 0f, -LINE_LENGTH, paint);

		canvas.restore();
	}

	@Override
	public UnitType getType() {
		return UnitType.DEFENDING;
	}

	@Override
	public void drawForMiniMap(Canvas canvas) {
		canvas.drawCircle(this.getX(), this.getY(), 10, paint);
	}
}

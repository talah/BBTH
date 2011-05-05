package bbth.game.units;

import android.graphics.*;
import android.util.FloatMath;
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
	
}

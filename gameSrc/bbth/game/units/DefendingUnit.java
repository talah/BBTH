package bbth.game.units;

import android.graphics.*;
import bbth.engine.util.MathUtils;
import bbth.game.Team;

public class DefendingUnit extends Unit {
	public DefendingUnit(UnitManager unitManager, Team team, Paint p) {
		super(unitManager, team, p);
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
	public float getStartingHealth() {
		return 50;
	}

	@Override
	public float getRadius() {
		return 4;
	}
	
}

package bbth.game.units;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.FloatMath;
import bbth.game.Team;

public class AttackingUnit extends Unit {
	public AttackingUnit(UnitManager unitManager, Team team, Paint p) {
		super(unitManager, team, p);
	}
	
	private static final float LINE_LENGTH = 6f;
	
	@Override
	public void draw(Canvas canvas) {
		canvas.drawCircle(this.getX(), this.getY(), 4, paint);
		
		canvas.drawLine(getX(), getY(), , , paint);
	}

	@Override
	public UnitType getType() {
		return UnitType.ATTACKING;
	}

	@Override
	public float getStartingHealth() {
		return 50;
	}

	@Override
	public float getRadius() {
		return 4f;
	}
	
}

package bbth.game.units;

import android.graphics.*;
import android.util.FloatMath;
import bbth.game.Team;

public class AttackingUnit extends Unit {
	public AttackingUnit(Team team) {
		super(team);
	}
	
	private static final float LINE_LENGTH = 15f;
	
	@Override
	public void draw(Canvas canvas) {
		canvas.drawCircle(this.getX(), this.getY(), 10, paint);
		float heading = getHeading();
		canvas.drawLine(getX(), getY(), LINE_LENGTH*FloatMath.cos(heading), LINE_LENGTH*FloatMath.cos(heading), paint);
	}

	@Override
	public UnitType getType() {
		return UnitType.ATTACKING;
	}
	
}

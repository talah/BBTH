package bbth.game.units;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.FloatMath;
import bbth.game.Team;

public class AttackingUnit extends Unit {
	private static final float FIRE_RATE = .5f; // twice a second
	
	public AttackingUnit(UnitManager unitManager, Team team, Paint p) {
		super(unitManager, team, p);
	}
	
	private static final float LINE_LENGTH = 6f;
	
	@Override
	public void draw(Canvas canvas) {
		canvas.drawCircle(this.getX(), this.getY(), 4, paint);
		
		canvas.drawCircle(this.getX(), this.getY(), 4, paint);
		
//		if (target == null) {
			float heading = getHeading();
			canvas.drawLine(getX(), getY(), getX() + LINE_LENGTH*FloatMath.cos(heading), getY() + LINE_LENGTH*FloatMath.sin(heading), paint);
//		} else {
//			
//		}
	}

	boolean charging = true;
	boolean firing;
	Unit fireTarget;
	float powerLevel;
	
	@Override
	public void update(float seconds) {
		super.update(seconds);
		
		if (isDead())
			return;
		
//		if (!getStateName().equals("attacking")) {
//			fireTarget = null;
//			firing = false;
//		}
		
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
	
	@Override
	public void drawForMiniMap(Canvas canvas) {
		canvas.drawCircle(this.getX(), this.getY(), 10, paint);
	}
}

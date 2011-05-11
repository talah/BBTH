package bbth.game.units;

import android.graphics.*;
import android.graphics.Paint.Style;
import bbth.engine.util.MathUtils;
import bbth.game.Team;

public class UberUnit extends Unit {
	private static final float MAX_POWER_LEVEL = 9000f;
	
	private static final float CHARGE_RATE = 4000f;
	private static final float DISCHARGE_RATE = CHARGE_RATE * 2f/7f;
	private static final float DAMAGE_RATE = 1337f;
	
	@Override
	public float getStartingHealth() {
		return 1337f;
	}
	
	public UberUnit(UnitManager unitManager, Team team, Paint p) {
		super(unitManager, team, p);
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
		
		if (!getStateName().equals("attacking")) {
			fireTarget = null;
			firing = false;
		}
		
		if (firing) {
			if (fireTarget.isDead() || powerLevel < 0) {
System.err.println(team + ": finished firing at target: "+fireTarget+" (leftover power: "+powerLevel+")");
				powerLevel = Math.max(0f, powerLevel);
				charging = true;
				firing = false;
				fireTarget = null;
			} else {
				powerLevel -= DISCHARGE_RATE;
				float damage = DAMAGE_RATE * seconds;
				for (Unit unit : unitManager.getUnitsIntersectingLine(getX(), getY(), fireTarget.getX(), fireTarget.getY())) {
					if (team.isEnemy(unit.getTeam())) {
						unit.takeDamage(damage);
						System.err.println(team + ": dealt "+damage+" to unit: "+unit.hashCode());
					}
				}
			}
		} else {
			if (powerLevel > MAX_POWER_LEVEL) { // that is, if its power level is over 9000
				powerLevel = MAX_POWER_LEVEL;
				charging = false;
			} else {
				charging = true;
			}
			
			if (charging) {
				powerLevel += CHARGE_RATE * seconds;
			}
			
			if (!charging && target != null && !target.isDead() && getStateName().equals("attacking")) {
				firing = true;
				fireTarget = target;
System.err.println(team + ": starting to fire at target: "+fireTarget.hashCode());
			}
		}
	}

	private float[] outline = { 0f, -15f, 10f, 10f,
	                            10f, 10f, -10f, 10f,
	                            -10f, 10f, 0f, -15f,
	                          };
	private static final float POWER_CIRCLE_RADIUS = 5f;
	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		
		canvas.translate(getX(), getY());
		
		canvas.rotate(MathUtils.toDegrees(getHeading()) + 90);
		canvas.drawLines(outline, paint);
		canvas.drawCircle(0f, 0f, 5f, paint);
		
		canvas.restore();
		
		tempPaint.set(paint);
		
		paint.setColor(Color.GRAY);
		paint.setStyle(Style.FILL);
		float radius = POWER_CIRCLE_RADIUS*powerLevel/MAX_POWER_LEVEL;
		canvas.drawCircle(getX(), getY(), radius, paint);
		if (firing) {
			paint.setStrokeWidth(radius);
			canvas.drawLine(getX(), getY(), fireTarget.getX(), fireTarget.getY(), paint);
			canvas.drawCircle(fireTarget.getX(), fireTarget.getY(), radius*1.3f, paint);
			paint.setColor(Color.WHITE);
			paint.setStrokeWidth(radius*.5f);
			canvas.drawCircle(fireTarget.getX(), fireTarget.getY(), radius*.65f, paint);
			canvas.drawLine(getX(), getY(), fireTarget.getX(), fireTarget.getY(), paint);
		}
		
		paint.set(tempPaint);
	}

	@Override
	public UnitType getType() {
		return UnitType.UBER;
	}

	@Override
	public float getRadius() {
		return 10f;
	}
	
}

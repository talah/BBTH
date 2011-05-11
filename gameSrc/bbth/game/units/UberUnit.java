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
			return;
		}
		
		if (firing) {
			if (fireTarget.isDead() || powerLevel < 0) {
				powerLevel = Math.max(0f, powerLevel);
				charging = true;
				firing = false;
				fireTarget = null;
			} else {
//System.err.println(team + ": starting to fire at target: "+target.hashCode());
				powerLevel -= DISCHARGE_RATE;
				float damage = DAMAGE_RATE * seconds;
				for (Unit unit : unitManager.getUnitsIntersectingLine(getX(), getY(), target.getX(), target.getY())) {
					if (team.isEnemy(unit.getTeam())) {
						unit.takeDamage(damage);
						System.err.println(team + ": dealt "+damage+" to unit: "+unit.hashCode());
					}
				}
			}
		} else {
			if (charging) {
				if (powerLevel > MAX_POWER_LEVEL) { // that is, if its power level is over 9000
					powerLevel = MAX_POWER_LEVEL;
					charging = false;
				} else {
					powerLevel += CHARGE_RATE * seconds;
				}
			}
			
			if (!charging && target != null && !target.isDead()) {
				firing = true;
				fireTarget = target;
//System.err.println(team + ": starting to fire at target: "+target.hashCode());
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
			canvas.drawLine(getX(), getY(), target.getX(), target.getY(), paint);
			canvas.drawCircle(target.getX(), target.getY(), radius*1.3f, paint);
			paint.setColor(Color.WHITE);
			paint.setStrokeWidth(radius*.5f);
			canvas.drawCircle(target.getX(), target.getY(), radius*.65f, paint);
			canvas.drawLine(getX(), getY(), target.getX(), target.getY(), paint);
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

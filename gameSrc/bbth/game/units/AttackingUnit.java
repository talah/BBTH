package bbth.game.units;

import android.graphics.*;
import android.graphics.Paint.Style;
import android.util.FloatMath;
import bbth.engine.util.*;
import bbth.engine.util.Envelope.OutOfBoundsHandler;
import bbth.game.Team;

public class AttackingUnit extends Unit {
//	private static final float DETONATION_WITHIN_DISTANCE = 1f;
	private static final float DETONATION_WITHIN_DISTANCE = 23f;
	private static final float DETONATION_MAX_RADIUS = 40f;
	private static final float DETONATION_TIME = .3f;
	private static final float MIN_DAMAGE = 30f;
	private static final float MAX_DAMAGE = 90f;
	
	private static final Envelope DAMAGE_ENVELOPE = new Envelope(MAX_DAMAGE, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
	static {
		DAMAGE_ENVELOPE.addLinearSegment(DETONATION_MAX_RADIUS, MIN_DAMAGE);
	}
	private static final Envelope RADIUS_ENVELOPE = new Envelope(0f, OutOfBoundsHandler.RETURN_FIRST_OR_LAST);
	static {
		RADIUS_ENVELOPE.addLinearSegment(DETONATION_TIME, DETONATION_MAX_RADIUS);
	}
	
	public AttackingUnit(UnitManager unitManager, Team team, Paint p) {
		super(unitManager, team, p);
	}
	
	private static final float LINE_LENGTH = 6f;
	
	@Override
	public void draw(Canvas canvas) {
		if (detonating) {
			tempPaint.set(paint);
			
			paint.setColor(Color.rgb(231, 80, 0));
			paint.setStyle(Style.FILL);
			
			canvas.drawCircle(getX(), getY(), (float)RADIUS_ENVELOPE.getValueAtTime(detonationTime), paint);
			
			paint.set(tempPaint);
			return;
		}
		
// uncomment to draw detection radius
//tempPaint.set(paint);
//paint.setColor(Color.WHITE);
//canvas.drawCircle(this.getX(), this.getY(), DETONATION_WITHIN_DISTANCE, paint);
//paint.set(tempPaint);
		
		canvas.drawCircle(this.getX(), this.getY(), 4, paint);
		
		float heading = getHeading();
		canvas.drawLine(getX(), getY(), getX() + LINE_LENGTH*FloatMath.cos(heading), getY() + LINE_LENGTH*FloatMath.sin(heading), paint);
	}

	boolean detonating;
	float detonationTime;

	@Override
	public boolean isDead() {
		if (detonating)
			return true;
		else
			return super.isDead();
	}
	
	@Override
	public void takeDamage(float damage) {
		if (!detonating)
			super.takeDamage(damage);
	}

	@Override
	public void update(float seconds) {
		if (detonating) {
			detonationTime += seconds;
			if (detonationTime > RADIUS_ENVELOPE.getTotalLength())
				unitManager.notifyUnitDead(this);
			return;
		}
		
		// move only if not detonating
		super.update(seconds);
		
		if (isDead() || target == null || !getStateName().equals("attacking"))
			return;
		
		float x = getX();
		float y = getY();
		
		if (MathUtils.getDistSqr(x, y, target.getX(), target.getY()) < DETONATION_WITHIN_DISTANCE*DETONATION_WITHIN_DISTANCE) {
			for (Unit unit : unitManager.getUnitsInCircle(getX(), getY(), DETONATION_MAX_RADIUS)) {
				if (team.isEnemy(unit.getTeam()))
					unit.takeDamage((float) DAMAGE_ENVELOPE.getValueAtTime(MathUtils.getDist(x, y, unit.getX(), unit.getY())));
			}
			detonating = true;
		}
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

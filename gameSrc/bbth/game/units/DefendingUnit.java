package bbth.game.units;

import android.graphics.*;
import bbth.engine.util.*;
import bbth.engine.util.Envelope.OutOfBoundsHandler;
import bbth.game.Team;

public class DefendingUnit extends Unit {
	private static final float DETONATION_WITHIN_DISTANCE = 10f;
	private static final float DETONATION_MAX_RADIUS = 20f;
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
	
	public DefendingUnit(UnitManager unitManager, Team team, Paint p) {
		super(unitManager, team, p);
	}

	@Override
	public void takeDamage(float damage) {
		super.takeDamage(damage);
	}

	@Override
	public void update(float seconds) {
		super.update(seconds);
		
		if (isDead())
			return;
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
	
	@Override
	public void drawForMiniMap(Canvas canvas) {
		canvas.drawCircle(this.getX(), this.getY(), 10, paint);
	}
}

package bbth.game.units;

import android.graphics.Canvas;
import android.graphics.Paint;
import bbth.engine.fastgraph.Wall;
import bbth.engine.particles.ParticleSystem;
import bbth.game.Team;

/**
 * A wall that's also a unit.  How convenient.
 * @author jardini
 *
 */
public class WallUnit extends Unit {

	public static final float HEALTH = 10;
	
	private Wall wall;

	public WallUnit(Wall w, UnitManager m, Team team, Paint p, ParticleSystem particleSystem) {
		super(m, team, p, particleSystem);
		wall = w;
		unitManager.notifyUnitDead(this);
	}
	
	@Override
	public void update(float seconds) {
		super.update(seconds);
		takeDamage(seconds);
	}

	@Override
	public void drawChassis(Canvas canvas) {
		paint.setAlpha((int) (health * 255 / HEALTH));
		canvas.drawLine(wall.a.x, wall.a.y, wall.b.x, wall.b.y, paint);
		paint.setAlpha(255);
	}

	@Override
	public void drawForMiniMap(Canvas canvas) {
		canvas.drawLine(wall.a.x, wall.a.y, wall.b.x, wall.b.y, paint);
	}

	@Override
	public UnitType getType() {
		return UnitType.WALL;
	}

	@Override
	public float getStartingHealth() {
		return HEALTH;
	}

	@Override
	public float getRadius() {
		// this isn't a circle, so don't collide with it as a circle
		return 0;
	}

	public Wall getWall() {
		return wall;
	}

}

package bbth.game.units;

import bbth.engine.fastgraph.Wall;
import bbth.game.Team;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * A wall that's also a unit.  How convenient.
 * @author jardini
 *
 */
public class WallUnit extends Unit {

	public static final float HEALTH = 5;
	
	private Wall wall;
	private float health;
	private UnitManager manager;

	public WallUnit(Wall w, UnitManager m, Team team, Paint p) {
		super(m, team, p);
		wall = w;
		health = HEALTH;
		manager = m;
		unitManager.notifyUnitDead(this);
	}
	
	@Override
	public void update(float seconds) {
		super.update(seconds);
		health -= seconds;
		
		if (health <= 0) {
			manager.notifyUnitDead(this);
		}
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawLine(wall.a.x, wall.a.y, wall.b.x, wall.b.y, paint);
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

}

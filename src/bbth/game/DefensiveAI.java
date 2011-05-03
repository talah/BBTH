package bbth.game;

import java.util.ArrayList;

import android.graphics.PointF;
import android.util.FloatMath;
import bbth.ai.FlockRulesCalculator;
import bbth.util.MathUtils;

public class DefensiveAI extends UnitAI {
	
	private PointF m_flock_dir;

	private float max_vel_change = 0.3f;

	public DefensiveAI() {
		super();
		m_flock_dir = new PointF();
	}
	
	@Override
	public void update(Unit entity, AIController c, FlockRulesCalculator flock) {
		float xcomp = 0;
		float ycomp = 0;
		
		calculateFlocking(entity, c, flock, m_flock_dir);
		
		xcomp = m_flock_dir.x;
		ycomp = m_flock_dir.y;
		
		// Calculate somewhere to go if it's a leader.
		if (!flock.hasLeader(entity)) {
			ArrayList<Unit> enemies = c.getEnemies(entity);
			Unit enemy = getClosestEnemy(entity, enemies);
			
			if (enemy != null) {
				float angle = MathUtils.getAngle(entity.getX(), entity.getY(), enemy.getX(), enemy.getY());
				xcomp += 0.05f * FloatMath.cos(angle);
				ycomp += 0.05f * FloatMath.sin(angle);
			}
		}
		
		float wanteddir = MathUtils.getAngle(0, 0, xcomp, ycomp);
		
		float wantedchange = MathUtils.normalizeAngle(wanteddir, entity.getHeading()) - entity.getHeading();
		
		float actualchange = wantedchange;
		if (actualchange > max_vel_change) {
			actualchange = max_vel_change;
		}
		
		if (actualchange < -1.0f * max_vel_change) {
			actualchange = -1.0f * max_vel_change;
		}
		
		entity.setVelocity(0.05f, entity.getHeading() + actualchange);
	}

	private Unit getClosestEnemy(Unit entity, ArrayList<Unit> enemies) {
		float bestdist = Float.MAX_VALUE;
		Unit bestunit = null;
		int size = enemies.size();
		for (int i = 0; i < size; i++) {
			Unit enemy = enemies.get(i);
			float enemydist = MathUtils.getDistSqr(entity.getX(), entity.getY(), enemy.getX(), enemy.getY());
			if (enemydist < bestdist) {
				bestunit = enemy;
				bestdist = enemydist;
			}
		}
		
		return bestunit;
	}

}

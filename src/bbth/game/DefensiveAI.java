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
		
		calculate_flocking(entity, c, flock, m_flock_dir);
		
		xcomp = m_flock_dir.x;
		ycomp = m_flock_dir.y;
		
		// Calculate somewhere to go if it's a leader.
		if (!flock.has_leader(entity)) {
			ArrayList<Unit> enemies = c.get_enemies(entity);
			Unit enemy = get_closest_enemy(entity, enemies);
			
			if (enemy != null) {
				float angle = MathUtils.get_angle(entity.get_x(), entity.get_y(), enemy.get_x(), enemy.get_y());
				xcomp += 0.05f * FloatMath.cos(angle);
				ycomp += 0.05f * FloatMath.sin(angle);
			}
		}
		
		float wanteddir = MathUtils.get_angle(0, 0, xcomp, ycomp);
		
		float wantedchange = MathUtils.normalize_angle(wanteddir, entity.get_heading()) - entity.get_heading();
		
		float actualchange = wantedchange;
		if (actualchange > max_vel_change) {
			actualchange = max_vel_change;
		}
		
		if (actualchange < -1.0f * max_vel_change) {
			actualchange = -1.0f * max_vel_change;
		}
		
		entity.set_velocity(0.05f, entity.get_heading() + actualchange);
	}

	private Unit get_closest_enemy(Unit entity, ArrayList<Unit> enemies) {
		float bestdist = Float.MAX_VALUE;
		Unit bestunit = null;
		int size = enemies.size();
		for (int i = 0; i < size; i++) {
			Unit enemy = enemies.get(i);
			float enemydist = MathUtils.get_dist_sqr(entity.get_x(), entity.get_y(), enemy.get_x(), enemy.get_y());
			if (enemydist < bestdist) {
				bestunit = enemy;
				bestdist = enemydist;
			}
		}
		
		return bestunit;
	}

}

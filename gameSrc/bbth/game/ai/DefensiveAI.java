package bbth.game.ai;

import java.util.ArrayList;

import android.graphics.PointF;
import android.util.FloatMath;
import bbth.engine.ai.FlockRulesCalculator;
import bbth.engine.ai.MapGrid;
import bbth.engine.ai.Pathfinder;
import bbth.engine.util.MathUtils;
import bbth.game.Unit;

public class DefensiveAI extends UnitAI {
	
	private PointF m_flock_dir;

	private Pathfinder m_pathfinder;

	private MapGrid m_map_grid;
	
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
				float goal_x = enemy.getX();
				float goal_y = enemy.getY();
				if (m_pathfinder != null) {
					PointF start = m_map_grid.getBin(entity.getX() + 1.0f, entity.getY() + 1.0f);
					PointF end = m_map_grid.getBin(goal_x + 1.0f, goal_y + 1.0f);
					
					//System.out.println("Team: " + entity.getTeam() + " Start: " + entity.getX() + ", " + entity.getY() + " = " + start.x + ", " + start.y + " End: " + end.x + ", " + end.y);
					
					if (m_pathfinder.findPath(start, end)) {
						ArrayList<PointF> path = m_pathfinder.getPath();
						if (path.size() > 1) {
							PointF goal_point = path.get(1);
							//System.out.println("Next point: " + goal_point.x + ", " + goal_point.y + " = " + m_map_grid.getXPos((int)goal_point.x) + ", " + m_map_grid.getYPos((int)goal_point.y));
							// Goal point is returned as y, x, due to Pathfinder weirdness.
							goal_x = m_map_grid.getXPos((int)goal_point.y);
							goal_y = m_map_grid.getYPos((int)goal_point.x);
						}
					}
				}
				
				float angle = MathUtils.getAngle(entity.getX(), entity.getY(), goal_x, goal_y);
				float objectiveweighting = getObjectiveWeighting();
				xcomp += objectiveweighting * FloatMath.cos(angle);
				ycomp += objectiveweighting * FloatMath.sin(angle);
			}
		}
		
		float wanteddir = MathUtils.getAngle(0, 0, xcomp, ycomp);
		
		float wantedchange = MathUtils.normalizeAngle(wanteddir, entity.getHeading()) - entity.getHeading();
		
		float actualchange = wantedchange;
		float maxvelchange = getMaxVelChange();
		
		if (actualchange > maxvelchange) {
			actualchange = maxvelchange;
		}
		
		if (actualchange < -1.0f * maxvelchange) {
			actualchange = -1.0f * maxvelchange;
		}
		
		entity.setVelocity(getMaxVel() , entity.getHeading() + actualchange);
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

	public void setPathfinder(Pathfinder pathfinder, MapGrid grid) {
		m_pathfinder = pathfinder;
		m_map_grid = grid;
	}

}

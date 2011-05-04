package bbth.game;
import java.util.ArrayList;

import android.graphics.PointF;
import android.util.FloatMath;
import bbth.ai.FlockRulesCalculator;
import bbth.util.MathUtils;

public class SimpleAI extends UnitAI {

	public SimpleAI() {
		super();
	}
	
	@Override
	public void update(Unit entity, AIController c, FlockRulesCalculator flock) {
		float xcomp = 0;
		float ycomp = 0;
		
		ArrayList<Unit> enemies = c.getEnemies(entity);
		Unit enemy = getClosestEnemy(entity, enemies);
		
		if (enemy != null) {
			float angle = MathUtils.getAngle(entity.getX(), entity.getY(), enemy.getX(), enemy.getY());
			xcomp += 50.0f * FloatMath.cos(angle);
			ycomp += 50.0f * FloatMath.sin(angle);
			entity.setVelocityComponents(xcomp, ycomp);
		}
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

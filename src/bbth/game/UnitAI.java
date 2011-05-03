package bbth.game;

import android.graphics.PointF;
import bbth.ai.FlockRulesCalculator;
import bbth.entity.Movable;

public abstract class UnitAI {
	private PointF m_result;
	
	public UnitAI() {
		m_result = new PointF();
	}
	
	public abstract void update(Unit entity, AIController c, FlockRulesCalculator flock);
	
	protected void calculateFlocking(Unit entity, AIController c, FlockRulesCalculator flock, PointF result) {
		result.x = 0;
		result.y = 0;
		
		// Calculate flocking.
		flock.getCohesionComponent(entity, m_result);
		
		result.x = m_result.x * .2f / c.getWidth();
		result.y = m_result.y * .2f / c.getHeight();
		
		flock.getAlignmentComponent(entity, m_result);
		
		result.x += m_result.x;
		result.y += m_result.y;
		
		flock.getSeparationComponent(entity, 20.0f, m_result);
		
		result.x += m_result.x;
		result.y += m_result.y;
		
		result.x /= 3;
		result.y /= 3;
		
		return;
	}
}

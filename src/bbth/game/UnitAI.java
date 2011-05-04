package bbth.game;

import android.graphics.PointF;
import bbth.ai.FlockRulesCalculator;
import bbth.entity.Movable;

public abstract class UnitAI {
	private PointF m_result;
	
	private float m_max_vel_change = 0.5f;
	
	private float m_objective_weighting = 0.05f;

	private float m_max_vel = 50.0f;
	
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

	public void setMaxVelChange(float m_max_vel_change) {
		if (m_max_vel_change < 0) {
			m_max_vel_change = 0;
		}
		this.m_max_vel_change = m_max_vel_change;
	}

	public float getMaxVelChange() {
		return m_max_vel_change;
	}

	public void setObjectiveWeighting(float m_objective_weighting) {
		this.m_objective_weighting = m_objective_weighting;
	}

	public float getObjectiveWeighting() {
		return m_objective_weighting;
	}

	public void setMaxVel(float m_max_vel) {
		if (m_max_vel < 0) {
			m_max_vel = 0;
		}
		this.m_max_vel = m_max_vel;
	}

	public float getMaxVel() {
		return m_max_vel;
	}
}

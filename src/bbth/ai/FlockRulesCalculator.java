package bbth.ai;

import java.util.ArrayList;

import android.graphics.PointF;
import bbth.entity.*;
import bbth.util.MathUtils;

public class FlockRulesCalculator {
	public ArrayList<Movable> m_objects;
	
	public float m_neighbor_radius;

	private float m_view_angle;
	
	public FlockRulesCalculator() {
		m_objects = new ArrayList<Movable>();
		
		m_neighbor_radius = 100.0f;
		m_view_angle = 2 * MathUtils.PI * .75f;
	}
	
	public void add_object(Movable obj) {
		m_objects.add(obj);
	}
	
	public void remove_object(Movable obj) {
		m_objects.remove(obj);
	}
	
	public void clear() {
		m_objects.clear();
	}
	
	public void set_neighbor_radius(float r) {
		if (r < 0) {
			return;
		}
		m_neighbor_radius = r;
	}
	
	public void set_view_angle(float angle) {
		if (angle < 0) {
			return;
		}
		m_view_angle = angle;
	}
	
	public float get_cohesion_component(Movable actor, PointF result) {
		int size = m_objects.size();
		
		float point_x = 0;
		float point_y = 0;
		float count = 0;
		for (int i = 0; i < size; i++) {
			Movable other = m_objects.get(i);
			
			if (other == actor) {
				continue;
			}
			
			if (!can_see(actor, other)) {
				continue;
			}
			
			point_x += other.get_x();
			point_y += other.get_y();
			
			count++;
		}
		
		if (count == 0) {
			result.set(0, 0);
			return 0;
		}
		
		point_x /= count;
		point_y /= count;
		
		result.set(point_x - actor.get_x(), point_y - actor.get_y());
		
		return MathUtils.get_dist(actor.get_x(), actor.get_y(), point_x, point_y);
	}
	
	public float get_alignment_component(Movable actor, PointF result) {
		int size = m_objects.size();
		
		float othervel_x = 0;
		float othervel_y = 0;
		float count = 0;
		for (int i = 0; i < size; i++) {
			Movable other = m_objects.get(i);
			
			if (other == actor) {
				continue;
			}
			
			if (!can_see(actor, other)) {
				continue;
			}
			
			othervel_x += other.get_speed() * Math.cos(other.get_heading());
			othervel_y += other.get_speed() * Math.sin(other.get_heading());
			
			count++;
		}
		
		float actor_vel_x = actor.get_speed() * (float)Math.cos(actor.get_heading());
		float actor_vel_y = actor.get_speed() * (float)Math.sin(actor.get_heading());
		
		if (count == 0) {
			result.set(0, 0);
			return 0;
		}
		
		othervel_x /= count;
		othervel_y /= count;
		
		result.set(othervel_x - actor_vel_x, othervel_y - actor_vel_y);
		
		return MathUtils.get_dist(actor_vel_x, actor_vel_y, othervel_x, othervel_y);
	}
	
	public float get_separation_component(Movable actor, float desired_separation, PointF result) {
		int size = m_objects.size();
		
		float point_x = 0;
		float point_y = 0;
		float count = 0;
		for (int i = 0; i < size; i++) {
			Movable other = m_objects.get(i);
			
			if (other == actor) {
				continue;
			}
			
			if (!can_see(actor, other)) {
				continue;
			}
			
			float dist = get_dist(actor, other);
			
			if (dist > desired_separation) {
				continue;
			}
			
			float this_x = actor.get_x() - other.get_x();
			float this_y = actor.get_y() - other.get_y();
			float magnitude = (float)Math.sqrt(this_x * this_x + this_y * this_y);
			this_x /= magnitude;
			this_y /= magnitude;
			this_x /= dist;
			this_y /= dist;
			
			point_x += this_x;
			point_y += this_y;
			
			count++;
		}
		
		if (count == 0) {
			result.set(0, 0);
			return 0;
		}
		
		point_x /= count;
		point_y /= count;
		
		result.set(point_x, point_y);
		
		return (float)Math.sqrt(point_x * point_x + point_y * point_y);
	}
	
	private boolean can_see(Movable actor, Movable other) {
		float dist = get_dist(actor, other);
		
		// Check if we're in the correct radius
		if (dist > m_neighbor_radius) {
			return false;
		}
		
		// Check if we are within the view angle
		float angle = Math.abs(get_angle_offset(actor, other));
		if (angle > m_view_angle/2.0f) {
			return false;
		}
		
		return true;
	}
	
	public boolean has_leader(Movable actor) {
		int size = m_objects.size();
		
		for (int i = 0; i < size; i++) {
			Movable other = m_objects.get(i);
			
			if (other == actor) {
				continue;
			}
			
			if (!can_see(actor, other)) {
				continue;
			}
		
			// Check if we are within the view angle
			float angle = Math.abs(get_angle_offset(actor, other));
			if (angle < MathUtils.PI/3.0f) {
				return true;
			}
		}
		
		return false;
	}
	
	private float get_angle_offset(Movable actor, Movable other) {
		float absangle = MathUtils.get_angle(actor.get_x(), actor.get_y(), other.get_x(), other.get_y());
		return MathUtils.normalize_angle(absangle, actor.get_heading()) - actor.get_heading();
	}
	
	private float get_dist(Movable actor, Movable other) {
		return MathUtils.get_dist(actor.get_x(), actor.get_y(), other.get_x(), other.get_y());
	}
	
}

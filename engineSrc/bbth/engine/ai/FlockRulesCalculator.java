package bbth.engine.ai;

import java.util.ArrayList;

import android.graphics.PointF;
import android.util.FloatMath;
import bbth.engine.entity.Movable;
import bbth.engine.util.MathUtils;

public class FlockRulesCalculator {
	private static final float FRONT_VIEW_ANGLE = MathUtils.PI / 3.0f;

	public ArrayList<Movable> m_objects;

	public float m_neighbor_radius;

	private float m_view_angle;

	public FlockRulesCalculator() {
		m_objects = new ArrayList<Movable>();

		m_neighbor_radius = 30.0f;
		m_view_angle = 2 * MathUtils.PI * .65f;
	}

	public void addObject(Movable obj) {
		m_objects.add(obj);
	}

	public void removeObject(Movable obj) {
		m_objects.remove(obj);
	}

	public void clear() {
		m_objects.clear();
	}

	public void setNeighborRadius(float r) {
		if (r < 0) {
			return;
		}
		m_neighbor_radius = r;
	}

	public void setViewAngle(float angle) {
		if (angle < 0) {
			return;
		}
		m_view_angle = angle;
	}

	public float getCohesionComponent(Movable actor, PointF result) {
		int size = m_objects.size();

		float point_x = 0;
		float point_y = 0;
		float count = 0;
		for (int i = 0; i < size; i++) {
			Movable other = m_objects.get(i);

			if (other == actor) {
				continue;
			}

			if (!canSee(actor, other)) {
				continue;
			}

			point_x += other.getX();
			point_y += other.getY();

			count++;
		}

		if (count == 0) {
			result.set(0, 0);
			return 0;
		}

		point_x /= count;
		point_y /= count;

		result.set(point_x - actor.getX(), point_y - actor.getY());

		return MathUtils.getDist(actor.getX(), actor.getY(), point_x, point_y);
	}

	public float getAlignmentComponent(Movable actor, PointF result) {
		int size = m_objects.size();

		float othervel_x = 0;
		float othervel_y = 0;
		float count = 0;
		for (int i = 0; i < size; i++) {
			Movable other = m_objects.get(i);

			if (other == actor) {
				continue;
			}

			if (!canSee(actor, other)) {
				continue;
			}

			float otherspeed = other.getSpeed();
			float otherheading = other.getHeading();
			othervel_x += otherspeed * FloatMath.cos(otherheading);
			othervel_y += otherspeed * FloatMath.sin(otherheading);

			count++;
		}

		float actorspeed = actor.getSpeed();
		float actorheading = actor.getHeading();
		float actor_vel_x = actorspeed * FloatMath.cos(actorheading);
		float actor_vel_y = actorspeed * FloatMath.sin(actorheading);

		if (count == 0) {
			result.set(0, 0);
			return 0;
		}

		othervel_x /= count;
		othervel_y /= count;

		result.set(othervel_x - actor_vel_x, othervel_y - actor_vel_y);

		return MathUtils.getDist(actor_vel_x, actor_vel_y, othervel_x,
				othervel_y);
	}

	public float getSeparationComponent(Movable actor,
			float desired_separation, PointF result) {
		int size = m_objects.size();

		float point_x = 0;
		float point_y = 0;
		float count = 0;
		for (int i = 0; i < size; i++) {
			Movable other = m_objects.get(i);

			if (other == actor) {
				continue;
			}

			if (!canSee(actor, other)) {
				continue;
			}

			float dist2 = getDistSqr(actor, other);

			if (dist2 > desired_separation * desired_separation) {
				continue;
			}

			float this_x = actor.getX() - other.getX();
			float this_y = actor.getY() - other.getY();
			this_x /= dist2;
			this_y /= dist2;

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

		return FloatMath.sqrt(point_x * point_x + point_y * point_y);
	}

	private final boolean canSee(Movable actor, Movable other) {
		float dist2 = getDistSqr(actor, other);

		// Check if we're in the correct radius
		if (dist2 > m_neighbor_radius * m_neighbor_radius) {
			return false;
		}

		// Check if we are within the view angle
		float angle = Math.abs(getAngleOffset(actor, other));
		if (angle > m_view_angle / 2.0f) {
			return false;
		}

		return true;
	}

	public boolean hasLeader(Movable actor) {
		int size = m_objects.size();

		for (int i = 0; i < size; i++) {
			Movable other = m_objects.get(i);

			if (other == actor) {
				continue;
			}

			if (!canSee(actor, other)) {
				continue;
			}

			// Check if we are within the view angle
			float angle = Math.abs(getAngleOffset(actor, other));
			if (angle < FRONT_VIEW_ANGLE) {
				return true;
			}
		}

		return false;
	}

	private final float getAngleOffset(Movable actor, Movable other) {
		float absangle = MathUtils.getAngle(actor.getX(), actor.getY(),
				other.getX(), other.getY());
		return MathUtils.normalizeAngle(absangle, actor.getHeading())
				- actor.getHeading();
	}

	private final float getDistSqr(Movable actor, Movable other) {
		return MathUtils.getDistSqr(actor.getX(), actor.getY(), other.getX(),
				other.getY());
	}

}

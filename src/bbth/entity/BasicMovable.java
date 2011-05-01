package bbth.entity;

import bbth.util.MathUtils;
import android.util.FloatMath;

public class BasicMovable implements Movable {

	private float m_x;
	private float m_y;
	private float m_x_vel;
	private float m_y_vel;
	
	@Override
	public void set_position(float x, float y) {
		m_x = x;
		m_y = y;
	}

	@Override
	public void set_x(float x) {
		m_x = x;
	}

	@Override
	public void set_y(float y) {
		m_y = y;
	}

	@Override
	public float get_x() {
		return m_x;
	}

	@Override
	public float get_y() {
		return m_y;
	}

	@Override
	public void set_velocity(float vel, float dir) {
		m_x_vel = vel * FloatMath.cos(dir);
		m_y_vel = vel * FloatMath.sin(dir);
	}

	@Override
	public void set_velocity_components(float x_vel, float y_vel) {
		m_x_vel = x_vel;
		m_y_vel = y_vel;
	}

	@Override
	public void set_x_vel(float x_vel) {
		m_x_vel = x_vel;
	}

	@Override
	public void set_y_vel(float y_vel) {
		m_y_vel = y_vel;
	}

	@Override
	public float get_heading() {
		return MathUtils.get_angle(0, 0, m_x_vel, m_y_vel);
	}

	@Override
	public float get_speed() {
		return FloatMath.sqrt((m_x_vel * m_x_vel) + (m_y_vel * m_y_vel));
	}

	@Override
	public float get_x_vel() {
		return m_x_vel;
	}

	@Override
	public float get_y_vel() {
		return m_y_vel;
	}

}

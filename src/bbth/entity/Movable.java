package bbth.entity;

public interface Movable {

	public void set_position(float x, float y);
	
	public void set_x(float x);
	
	public void set_y(float y);
	
	public float get_x();
	
	public float get_y();
	
	public void set_velocity(float vel, float dir);
	
	public void set_velocity_components(float x_vel, float y_vel);
	
	public void set_x_vel(float x_vel);
	
	public void set_y_vel(float y_vel);
	
	public float get_heading();
	
	public float get_speed();
	
	public float get_x_vel();
	
	public float get_y_vel();
	
}

package bbth.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import bbth.entity.BasicMovable;
import bbth.ui.Anchor;
import bbth.ui.UIView;

/**
 * A BBTH unit is one of the little dudes that walk around on the map and kill
 * each other.
 */
public class Unit extends UIView {
	private Team team;
	private Paint paint;
	private BasicMovable controller;

	public Unit(Team team) {
		super(null);

		this.setAnchor(Anchor.CENTER_CENTER);

		this.team = team;
		this.controller = new BasicMovable();

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		switch (team) {
		case TEAM_0:
			paint.setColor(Color.RED);
			break;
		case TEAM_1:
			paint.setColor(Color.GREEN);
			break;
		}
	}

	public void setPosition(float x, float y) {
		super.setPosition(x, y);

		controller.set_position(x, y);
	}

	public void setVelocity(float vel, float dir) {
		controller.set_velocity(vel, dir);
	}

	public void update(float seconds) {
		// Derp derp, Euler
		controller.set_position(controller.get_x() + controller.get_x_vel() * seconds, controller.get_y() + controller.get_y_vel() * seconds);

		this.setPosition(controller.get_x(), controller.get_y());
	}

	public void draw(Canvas canvas) {
		canvas.drawCircle(controller.get_x(), controller.get_y(), 10, paint);
	}

	public Team getTeam() {
		return this.team;
	}

	public void setTeam(Team newteam) {
		team = newteam;
	}
}

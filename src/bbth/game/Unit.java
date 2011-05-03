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
public class Unit extends BasicMovable {
	private Team team;
	private Paint paint;
	private UIView view;

	public Unit(Team team) {
		view = new UIView(null);
		view.setAnchor(Anchor.CENTER_CENTER);

		this.team = team;

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

	public UIView getView() {
		return this.view;
	}
	
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		view.setPosition(x, y);
	}

	public void setVelocity(float vel, float dir) {
		super.setVelocity(vel, dir);
	}

	public void update(float seconds) {
		// Derp derp, Euler
		this.update(seconds);
		view.setPosition(this.getX(), this.getY());
	}

	public void draw(Canvas canvas) {
		canvas.drawCircle(this.getX(), this.getY(), 10, paint);
	}

	public Team getTeam() {
		return this.team;
	}

	public void setTeam(Team newteam) {
		team = newteam;
	}
}

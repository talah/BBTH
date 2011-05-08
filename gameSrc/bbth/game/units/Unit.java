package bbth.game.units;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import bbth.engine.entity.BasicMovable;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIView;
import bbth.game.Team;

/**
 * A BBTH unit is one of the little dudes that walk around on the map and kill
 * each other.
 */
public abstract class Unit extends BasicMovable {
	protected Team team;
	protected UIView view;
	protected Paint paint;
	protected Unit target;

	public Unit(Team team) {
		view = new UIView(null);
		view.setAnchor(Anchor.CENTER_CENTER);

		this.team = team;

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		switch (team) {
		case CLIENT:
			paint.setColor(Color.RED);
			break;
		case SERVER:
			paint.setColor(Color.GREEN);
			break;
		}
	}

	public UIView getView() {
		return this.view;
	}
	
	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		view.setPosition(x, y);
	}

	@Override
	public void setVelocity(float vel, float dir) {
		super.setVelocity(vel, dir);
	}

	@Override
	public void update(float seconds) {
		// Derp derp, Euler
		super.update(seconds);
		view.setPosition(this.getX(), this.getY());
	}

	public abstract void draw(Canvas canvas);
	public abstract UnitType getType();

	public Team getTeam() {
		return this.team;
	}

	public void setTeam(Team newteam) {
		team = newteam;
	}
	
	public void setTarget(Unit target) {
		
	}
}

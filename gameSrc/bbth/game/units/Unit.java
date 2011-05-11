package bbth.game.units;

import android.graphics.Canvas;
import android.graphics.Paint;
import bbth.engine.ai.fsm.*;
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
	private FiniteStateMachine fsm;
	private Unit target;

	public Unit(Team team, Paint p) {
		view = new UIView(this);
		view.setAnchor(Anchor.CENTER_CENTER);
//		view.setSize(4, 4);
		
		fsm = new FiniteStateMachine();
		
		this.team = team;
		
		target = null;

		paint = p;
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

	public void setFSM(FiniteStateMachine fsm) {
		this.fsm = fsm;
	}

	public FiniteStateMachine getFSM() {
		return fsm;
	}
	
	public FiniteState getState() {
		return fsm.getCurrState();
	}
	
	// If state name is "attacking," can attack current target.
	public String getStateName() {
		return fsm.getStateName();
	}

	public void setTarget(Unit target) {
		this.target = target;
	}

	public Unit getTarget() {
		return target;
	}
}

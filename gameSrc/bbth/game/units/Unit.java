package bbth.game.units;

import android.graphics.Canvas;
import android.graphics.Paint;
import bbth.engine.ai.fsm.FiniteState;
import bbth.engine.ai.fsm.FiniteStateMachine;
import bbth.engine.entity.BasicMovable;
import bbth.game.Team;

/**
 * A BBTH unit is one of the little dudes that walk around on the map and kill
 * each other.
 */
public abstract class Unit extends BasicMovable {
	protected Team team;
	protected Paint paint;
	private FiniteStateMachine fsm;
	private Unit target;

	public Unit(Team team, Paint p) {
		fsm = new FiniteStateMachine();
		this.team = team;
		target = null;
		paint = p;
	}

	public abstract void draw(Canvas canvas);

	public abstract void drawForMiniMap(Canvas canvas);

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

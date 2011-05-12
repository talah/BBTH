package bbth.game.units;

import android.graphics.*;
import android.graphics.Paint.Style;
import bbth.engine.ai.fsm.FiniteState;
import bbth.engine.ai.fsm.FiniteStateMachine;
import bbth.engine.entity.BasicMovable;
import bbth.engine.particles.ParticleSystem;
import bbth.engine.util.MathUtils;
import bbth.game.Team;

/**
 * A BBTH unit is one of the little dudes that walk around on the map and kill
 * each other.
 */
public abstract class Unit extends BasicMovable {
	protected Team team;
	protected Paint paint;
	protected ParticleSystem particleSystem;
	private FiniteStateMachine fsm;

	protected Unit target;
	protected UnitManager unitManager;
	
	protected float health = getStartingHealth();

	public Unit(UnitManager unitManager, Team team, Paint p, ParticleSystem particleSystem) {
		fsm = new FiniteStateMachine();
		this.team = team;
		this.unitManager = unitManager;

		paint = p;
	}
	
	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
	}

	// why override just to call super
//	@Override
//	public void setVelocity(float vel, float dir) {
//		super.setVelocity(vel, dir);
//	}

	public abstract void drawChassis(Canvas canvas);
	public abstract void drawForMiniMap(Canvas canvas);
	public void drawEffects(Canvas canvas) {}
	public void drawHealthBar(Canvas canvas) {
		if (isDead())
			return;
		
		tempPaint.set(paint);
		paint.setStyle(Style.FILL);
		
		float radius = getRadius();
		float border = 1f;
		
		float left = getX()-radius;
		float top = getY()-(radius*2f);
		float right = left + 2f*radius;
		float bottom = top + radius/2f;
		
		paint.setColor(Color.WHITE);
		canvas.drawRect(left-border, top-border, right+border, bottom+border, paint);
		
		paint.setColor(Color.RED);
		canvas.drawRect(left, top, right, bottom, paint);
		
		paint.setColor(Color.GREEN);
		float greenStopX = MathUtils.scale(0f, getStartingHealth(), left, right, getHealth());
		canvas.drawRect(left, top, greenStopX, bottom, paint);
		
		paint.set(tempPaint);
	}

	public abstract UnitType getType();
	public abstract float getStartingHealth();
	public abstract float getRadius();

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
	
	public float getHealth() {
		return health;
	}
	
	public boolean isDead() {
		return health <= 0f;
	}
	
	public void takeDamage(float damage) {
		if (!isDead()) {
			health -= damage;
			if (isDead()) {
				onDead();
				unitManager.notifyUnitDead(this);
			}
		}
	}
	
	protected void onDead() {
		
	}
	
	protected static Paint tempPaint = new Paint();
}

package bbth.game.units;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.FloatMath;
import bbth.engine.ai.fsm.FiniteState;
import bbth.engine.ai.fsm.FiniteStateMachine;
import bbth.engine.entity.BasicMovable;
import bbth.engine.net.simulation.Hash;
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
		this.team = team;
		this.unitManager = unitManager;
		this.particleSystem = particleSystem;
		this.paint = p;
		fsm = new FiniteStateMachine();
	}

	public abstract void drawChassis(Canvas canvas);
	public abstract void drawForMiniMap(Canvas canvas);
	public void drawEffects(Canvas canvas) {}
	public void drawHealthBar(Canvas canvas, boolean isLocal) {
		if (isDead())
			return;
		
		tempPaint.set(paint);
		paint.setStyle(Style.FILL);
		
		float radius = getRadius();
		float border = 1f;
		
		float left = getX()-radius;
		float top = (isLocal) ? getY() + (radius*2f) : getY() + (radius*2f);
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
		for (int i = 0; i < 10*getRadius(); ++i) {
			float angle = MathUtils.randInRange(0, 2 * MathUtils.PI);
			float sin = FloatMath.sin(angle);
			float cos = FloatMath.cos(angle);
			float xVel = MathUtils.randInRange(25.f, 50.f) * cos;
			float yVel = MathUtils.randInRange(25.f, 50.f) * sin;
			particleSystem.createParticle()
			.line()
			.velocity(xVel, yVel)
			.angle(angle)
			.shrink(0.1f, 0.15f)
			.radius(getRadius()*1.5f)
			.width(getRadius()/2f)
			.position(getX()+sin*2f, getY()+cos*2f)
			.color(team.getRandomShade());
		}
	}
	
	protected static Paint tempPaint = new Paint();

	public int getHash() {
		int hash = 0;
		hash = Hash.mix(hash, getX());
		hash = Hash.mix(hash, getY());
		hash = Hash.mix(hash, getXVel());
		hash = Hash.mix(hash, getYVel());
		hash = Hash.mix(hash, getHealth());
		hash = Hash.mix(hash, getType().ordinal());
		return hash;
	}
}

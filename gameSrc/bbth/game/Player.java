package bbth.game;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.util.FloatMath;
import bbth.engine.particles.ParticleSystem;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIScrollView;
import bbth.engine.ui.UIView;
import bbth.engine.util.MathUtils;
import bbth.game.ai.AIController;
import bbth.game.units.Unit;

/**
 * A player is someone who is interacting with the game.
 */
public class Player {
	private static final int NUM_PARTICLES = 200;
	private static final float PARTICLE_THRESHOLD = 0.5f;

	private Team team;
	private List<Unit> units;
	private Base base;
	private AIController aiController;
	private Paint paint;
	private UIView view;
	private ParticleSystem particles;
	private UnitSelector selector;

	public Player(Team team, AIController controller) {
		this.team = team;
		units = new ArrayList<Unit>();

		base = new Base(this, team);

		paint = new Paint();
		paint.setStrokeWidth(2.0f);
		paint.setStrokeJoin(Join.ROUND);
		paint.setTextSize(20);
		paint.setAntiAlias(true);
		paint.setColor(team.getColor());

		switch (team) {
		case CLIENT:
			base.setAnchor(Anchor.BOTTOM_LEFT);
			base.setPosition(0, BBTHSimulation.GAME_HEIGHT);
			break;

		case SERVER:
			base.setAnchor(Anchor.TOP_LEFT);
			base.setPosition(0, 0);
			break;
		}

		this.aiController = controller;

		particles = new ParticleSystem(NUM_PARTICLES, PARTICLE_THRESHOLD);
		selector = new UnitSelector(team);
	}
	
	public void setupSubviews(UIScrollView view, boolean isLocal) {
		this.view = view;

		view.addSubview(base);

		if (isLocal) {
			view.scrollTo(base.getPosition().x, base.getPosition().y);
		}
	}

	public void spawnUnit(float x, float y) {
		for (int i = 0; i < 40; ++i) {
			float angle = MathUtils.randInRange(0, 2 * MathUtils.PI);
			float xVel = MathUtils.randInRange(25.f, 50.f)
					* FloatMath.cos(angle);
			float yVel = MathUtils.randInRange(25.f, 50.f)
					* FloatMath.sin(angle);
			particles.createParticle().circle().velocity(xVel, yVel)
					.shrink(0.1f, 0.15f).radius(3.0f).position(x, y)
					.color(team.getRandomShade());
		}

		Unit newUnit = selector.getUnitType().createUnit(team, paint);
		newUnit.setPosition(x, y);
		// newUnit.setVelocity(MathUtils.randInRange(50, 100),
		// MathUtils.randInRange(0, MathUtils.TWO_PI));
		if (team == Team.SERVER) {
			newUnit.setVelocity(MathUtils.randInRange(30, 70),
					MathUtils.PI / 2.f);
		} else {
			newUnit.setVelocity(MathUtils.randInRange(30, 70),
					-MathUtils.PI / 2.f);
		}
		aiController.addEntity(newUnit);
		units.add(newUnit);
	}

	public Unit getMostAdvancedUnit() {
		Unit toReturn = null;

		for (int i = 0; i < units.size(); i++) {
			Unit curr_unit = units.get(i);

			if (toReturn == null
					|| (team == Team.SERVER && curr_unit.getY() > toReturn
							.getY())
					|| (team == Team.CLIENT && curr_unit.getY() < toReturn
							.getY())) {
				toReturn = curr_unit;
			}
		}

		return toReturn;
	}

	public void update(float seconds) {
		particles.tick(seconds);

		for (int i = 0; i < units.size(); i++) {
			Unit unit = units.get(i);

			unit.update(seconds);
			if (unit.getY() < 0 || unit.getY() > BBTHSimulation.GAME_HEIGHT) {
				units.remove(i);
				i--;
				aiController.removeEntity(unit);
			}

		}
	}

	public UnitSelector getUnitSelector() {
		return this.selector;
	}
	
	public void draw(Canvas canvas) {
		paint.setStyle(Style.STROKE);
		for (int i = 0; i < units.size(); i++) {
			units.get(i).draw(canvas);
		}

		// Derp
		paint.setStyle(Style.FILL);
		particles.draw(canvas, paint);
		paint.setColor(team.getColor());
	}
	
	public void drawForMiniMap(Canvas canvas) {
		paint.setStyle(Style.FILL);
		for (int i = 0; i < units.size(); i++) {
			units.get(i).drawForMiniMap(canvas);
		}
	}
}

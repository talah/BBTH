package bbth.game;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.util.FloatMath;
import bbth.engine.fastgraph.Wall;
import bbth.engine.particles.ParticleSystem;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIScrollView;
import bbth.engine.util.MathUtils;
import bbth.game.ai.AIController;
import bbth.game.units.Unit;
import bbth.game.units.UnitType;

/**
 * A player is someone who is interacting with the game.
 */
public class Player {
	private static final int NUM_PARTICLES = 200;
	private static final float PARTICLE_THRESHOLD = 0.5f;

	private Team team;
	public List<Unit> units;
	public Base base;
	private AIController aiController;
	private Paint paint;
	private ParticleSystem particles;
	private UnitSelector selector;
	private float _health;

	public ArrayList<Wall> walls;
	private Wall currentWall;

	public Player(Team team, AIController controller) {
		this.team = team;
		units = new ArrayList<Unit>();

		base = new Base(this, team);
		_health = 100;

		paint = new Paint();
		paint.setStrokeWidth(2.0f);
		paint.setStrokeJoin(Join.ROUND);
		paint.setTextSize(20);
		paint.setAntiAlias(true);
		paint.setColor(team.getUnitColor());
		paint.setStrokeCap(Cap.ROUND);

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

		walls = new ArrayList<Wall>();
	}

	public boolean settingWall() {
		return currentWall != null;
	}

	public void startWall(float x, float y) {
		currentWall = new Wall(x, y, x, y);
	}

	public void updateWall(float x, float y) {
		currentWall.b.set(x, y);
	}

	public Wall endWall(float x, float y) {
		currentWall.b.set(x, y);

		currentWall.updateLength();
		if (currentWall.length < BBTHSimulation.MIN_WALL_LENGTH) {
			return null;
		}

		walls.add(currentWall);

		Wall toReturn = currentWall;
		currentWall = null;
		return toReturn;
	}

	public void setUnitType(UnitType type) {
		selector.setUnitType(type);
	}

	public void setupSubviews(UIScrollView view, boolean isLocal) {
		if (isLocal) {
			view.scrollTo(base.getPosition().x, base.getPosition().y);
		}
	}

	public void spawnUnit(float x, float y) {
		for (int i = 0; i < 10; ++i) {
			float angle = MathUtils.randInRange(0, 2 * MathUtils.PI);
			float xVel = MathUtils.randInRange(25.f, 50.f)
					* FloatMath.cos(angle);
			float yVel = MathUtils.randInRange(25.f, 50.f)
					* FloatMath.sin(angle);
			particles.createParticle().circle().velocity(xVel, yVel)
					.shrink(0.1f, 0.15f).radius(3.0f).position(x, y)
					.color(team.getRandomShade());
		}

		if (true)
			throw new UnsupportedOperationException("TODO: implement a UnitManager");
		
		Unit newUnit = selector.getUnitType().createUnit(null, team, paint);
		
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
			Unit currUnit = units.get(i);
			if (toReturn == null
					|| (team == Team.SERVER && currUnit.getY() > toReturn
							.getY())
					|| (team == Team.CLIENT && currUnit.getY() < toReturn
							.getY())) {
				toReturn = currUnit;
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
		// Draw bases
		base.onDraw(canvas);

		// draw walls
		paint.setColor(team.getWallColor());
		for (int i = 0; i < walls.size(); i++) {
			Wall w = walls.get(i);
			canvas.drawLine(w.a.x, w.a.y, w.b.x, w.b.y, paint);
		}

		// // draw overlay wall
		// if (currentWall != null) {
		// paint.setColor(team.getTempWallColor());
		// canvas.drawLine(currentWall.a.x, currentWall.a.y, currentWall.b.x,
		// currentWall.b.y, paint);
		// }

		// draw units
		paint.setStyle(Style.STROKE);
		paint.setColor(team.getUnitColor());
		for (int i = 0; i < units.size(); i++) {
			units.get(i).draw(canvas);
		}

		// Derp
		paint.setStyle(Style.FILL);
		particles.draw(canvas, paint);
	}

	public void drawForMiniMap(Canvas canvas) {
		// draw units
		paint.setStyle(Style.FILL);
		paint.setColor(team.getUnitColor());
		for (int i = 0; i < units.size(); i++) {
			units.get(i).drawForMiniMap(canvas);
		}

		// draw walls
		paint.setColor(team.getWallColor());
		for (int i = 0; i < walls.size(); i++) {
			Wall w = walls.get(i);
			canvas.drawLine(w.a.x, w.a.y, w.b.x, w.b.y, paint);
		}
	}

	public float getHealth() {
		return _health;
	}

	public void resetHealth() {
		_health = 100;
	}

	public void adjustHealth(float delta) {
		_health = MathUtils.clamp(0, 100, _health + delta);
	}
}

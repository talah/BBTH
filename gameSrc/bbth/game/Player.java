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
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIScrollView;
import bbth.engine.util.MathUtils;
import bbth.game.ai.AIController;
import bbth.game.units.Unit;
import bbth.game.units.UnitManager;
import bbth.game.units.UnitType;
import bbth.game.units.WallUnit;

/**
 * A player is someone who is interacting with the game.
 */
public class Player {
	private Team team;
	public List<Unit> units;
	public Base base;
	private AIController aiController;
	private Paint paint;
	private UnitSelector selector;
	private float _health;
	private float _combo;
	private boolean _isLocal;

	public ArrayList<WallUnit> walls;
	private Wall currentWall;
	private UnitManager unitManager;

	public Player(Team team, AIController controller, UnitManager unitManager, boolean isLocal) {
		_isLocal = isLocal;
		this.team = team;
		this.unitManager = unitManager;
		units = new ArrayList<Unit>();

		base = new Base(this);
		_health = 100;
		setCombo(0);

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
		selector = new UnitSelector(team, unitManager, BBTHSimulation.PARTICLES);

		walls = new ArrayList<WallUnit>();
	}

	public Team getTeam() {
		return this.team;
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

	public Wall endWall() {
		currentWall.updateLength();
		if (currentWall.length < BBTHSimulation.MIN_WALL_LENGTH) {
			return null;
		}

		walls.add(new WallUnit(currentWall, unitManager, team, paint, BBTHSimulation.PARTICLES));

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
		// Can't spawn in front of most advanced unit.
		Unit advUnit = getMostAdvancedUnit();
		if (advUnit != null) {
			if (team == Team.CLIENT) {
				y = Math.max(y, advUnit.getY());
			} else {
				y = Math.min(y, advUnit.getY());
			}
		} else {
			if (team == Team.CLIENT) {
				y = BBTHSimulation.GAME_HEIGHT - Base.BASE_HEIGHT * 2.0f;
			} else {
				y = Base.BASE_HEIGHT * 2.0f;
			}
		}
		
		for (int i = 0; i < 10; ++i) {
			float angle = MathUtils.randInRange(0, 2 * MathUtils.PI);
			float xVel = MathUtils.randInRange(25.f, 50.f)
					* FloatMath.cos(angle);
			float yVel = MathUtils.randInRange(25.f, 50.f)
					* FloatMath.sin(angle);
			BBTHSimulation.PARTICLES.createParticle().circle()
					.velocity(xVel, yVel).shrink(0.1f, 0.15f).radius(3.0f)
					.position(x, y).color(team.getRandomShade());
		}

		Unit newUnit = null;
		if (_combo != 0 && _combo % BBTHSimulation.UBER_UNIT_THRESHOLD == 0) {
			newUnit = UnitType.UBER.createUnit(unitManager, team, paint, BBTHSimulation.PARTICLES);
		} else {
			newUnit = selector.getUnitType().createUnit(unitManager, team, paint, BBTHSimulation.PARTICLES);
		}

		newUnit.setPosition(x, y);
		if (team == Team.SERVER) {
			newUnit.setVelocity(BBTHSimulation.randInRange(30, 70),
					MathUtils.PI / 2.f);
		} else {
			newUnit.setVelocity(BBTHSimulation.randInRange(30, 70),
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
		for (int i = 0; i < units.size(); i++) {
			Unit unit = units.get(i);

			unit.update(seconds);
			if (unit.getY() < 0 || unit.getY() > BBTHSimulation.GAME_HEIGHT) {
				units.remove(i);
				i--;
				aiController.removeEntity(unit);
			}
		}

		for (int i = walls.size() - 1; i >= 0; --i) {
			walls.get(i).update(seconds);
			if (walls.get(i).isDead()) {
				removeWall(i);
			}
		}

	}

	private void removeWall(int i) {
		unitManager.removeWall(walls.get(i).getWall());
		walls.remove(i);
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
			walls.get(i).drawChassis(canvas);
		}

		// draw units
		paint.setStyle(Style.STROKE);
		paint.setColor(team.getUnitColor());
		for (int i = 0; i < units.size(); i++) {
			units.get(i).drawChassis(canvas);
		}
		for (int i = 0; i < units.size(); i++) {
			units.get(i).drawEffects(canvas);
		}
	}
	
	public void postDraw(Canvas canvas) {
		for (int i = 0; i < units.size(); i++) {
			units.get(i).drawHealthBar(canvas);
		}

		// draw my wavefront
		Unit advUnit = getMostAdvancedUnit();
		if (advUnit != null) {
			paint.setColor(team.getUnitColor());
			paint.setStyle(Style.FILL);
			float y = advUnit.getY() + (team == Team.CLIENT ? -10 : 10);
			canvas.drawLine(0, y, BBTHSimulation.GAME_WIDTH, y, paint);
		}
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
			walls.get(i).drawChassis(canvas);
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

	public void setCombo(float _combo) {
		if (_combo < 0) {
			_combo = 0;
		}
		this._combo = _combo;
	}

	public float getCombo() {
		return _combo;
	}

	public boolean isLocal() {
		return _isLocal;
	}
}

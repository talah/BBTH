package bbth.game;

import java.util.*;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.util.Log;
import bbth.engine.ui.*;
import bbth.engine.util.MathUtils;
import bbth.game.ai.AIController;
import bbth.game.units.*;

/**
 * A player is someone who is interacting with the game.
 */
public class Player {
	private Team team;
	private List<Unit> units;
	private Base base;
	private AIController aiController;
	private Paint paint;
	private UIView view;

	UnitType currentUnitType = UnitType.ATTACKING;
	
	private static float height = BBTHGame.HEIGHT * 2;

	public Player(Team team, AIController controller) {
		this.team = team;
		units = new ArrayList<Unit>();

		base = new Base(this, team);

		paint = new Paint();
		paint.setStrokeWidth(2.0f);
		paint.setStrokeJoin(Join.ROUND);
		paint.setStyle(Style.STROKE);
		paint.setTextSize(20);
		paint.setAntiAlias(true);

		switch (team) {
		case CLIENT:
			paint.setColor(Color.BLUE);
			base.setAnchor(Anchor.BOTTOM_LEFT);
			base.setPosition(0, height);
			break;

		case SERVER:
			paint.setColor(Color.RED);
			base.setAnchor(Anchor.TOP_LEFT);
			base.setPosition(0, 0);
			break;
		}

		this.aiController = controller;
	}

	public void setupSubviews(UIView view) {
		this.view = view;
		
		view.addSubview(base);
		
		for (int i = 0; i < this.units.size(); i++) {
			view.addSubview(units.get(i).getView());
		}
	}

	public void spawnUnit(float x, float y) {
		Unit newUnit = currentUnitType.createUnit(team, paint);
		newUnit.setPosition(x, y);
		// newUnit.setVelocity(MathUtils.randInRange(50, 100),
		// MathUtils.randInRange(0, MathUtils.TWO_PI));
		if (team == Team.SERVER) {
			newUnit.setVelocity(MathUtils.randInRange(30, 70), MathUtils.PI / 2.f);
		} else {
			newUnit.setVelocity(MathUtils.randInRange(30, 70), -MathUtils.PI / 2.f);
		}
		aiController.addEntity(newUnit);
		units.add(newUnit);
		
		this.view.addSubview(newUnit.getView());
	}

	public Unit getMostAdvancedUnit() {
		Unit toReturn = null;

		for (int i = 0; i < units.size(); i++) {
			Unit curr_unit = units.get(i);

			if (toReturn == null || 
					(team == Team.SERVER && curr_unit.getY() > toReturn.getY()) || 
					(team == Team.CLIENT && curr_unit.getY() < toReturn.getY())) {
				toReturn = curr_unit;
			}
		}

		return toReturn;
	}

	public void update(float seconds) {
		for (int i = 0; i < units.size(); i++) {
			Unit unit = units.get(i);
			
			unit.update(seconds);
			if (unit.getY() < 0 || unit.getY() > height) {
				units.remove(i);
				i--;
				view.removeSubview(unit.getView());
				aiController.removeEntity(unit);
			}
			
		}
	}

	public void draw(Canvas canvas) {
		for (int i = 0; i < units.size(); i++) {
			units.get(i).draw(canvas);
		}
	}
}

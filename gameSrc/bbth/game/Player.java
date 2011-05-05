package bbth.game;

import java.util.*;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import bbth.engine.ui.*;
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
	
	UnitType currentUnitType = UnitType.ATTACKING;
	
	public Player(Team team, AIController controller) {
		this.team = team;
		units = new ArrayList<Unit>();

		base = new Base(this);
		
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
			base.setPosition(0, BBTHGame.HEIGHT);
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
		view.addSubview(base);

		for (int i = 0; i < this.units.size(); i++) {
			view.addSubview(units.get(i).getView());
		}
	}

	public void spawnUnit(float x, float y) {
		Unit newUnit = currentUnitType.createUnit(team, paint);
		newUnit.setPosition(x, y);
		//u.setVelocity(MathUtils.randInRange(50, 100), MathUtils.randInRange(0, MathUtils.TWO_PI));
		aiController.addEntity(newUnit);
		units.add(newUnit);
	}

	public Unit getMostAdvancedUnit() {
		Unit toReturn = null;

		for (int i = 0; i < units.size(); i++) {
			Unit curr_unit = units.get(i);

			if (toReturn == null || toReturn.getY() < curr_unit.getY()) {
				toReturn = curr_unit;
			}
		}

		return toReturn;
	}

	public void update(float seconds) {
		for (int i = 0; i < units.size(); i++) {
			units.get(i).update(seconds);
		}
	}

	public void draw(Canvas canvas) {
		for (int i = 0; i < units.size(); i++) {
			units.get(i).draw(canvas);
		}
	}
}

package bbth.game;

import java.util.*;

import android.graphics.Canvas;
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
	
	UnitType currentUnitType = UnitType.ATTACKING;
	
	public Player(Team team, AIController controller) {
		this.team = team;
		units = new ArrayList<Unit>();

		base = new Base(this);
		switch (team) {
		case CLIENT:
			base.setAnchor(Anchor.BOTTOM_LEFT);
			base.setPosition(0, BBTHGame.HEIGHT);
			break;
			
		case SERVER:
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
		Unit u = currentUnitType.createUnit(team);
		u.setPosition(x, y);
		//u.setVelocity(MathUtils.randInRange(50, 100), MathUtils.randInRange(0, MathUtils.TWO_PI));
		aiController.addEntity(u);
		units.add(u);
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

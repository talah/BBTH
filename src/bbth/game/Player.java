package bbth.game;

import java.util.ArrayList;
import java.util.List;

import bbth.collision.Point;
import bbth.ui.Anchor;
import bbth.ui.UIView;
import bbth.util.MathUtils;

import android.graphics.Canvas;

/**
 * A player is someone who is interacting with the game.
 */
public class Player {
	private Team team;
	private List<Unit> units;
	private Base base;
	private AIController aiController;
	
	public Player(Team team, AIController controller) {
		this.team = team;
		units = new ArrayList<Unit>();

		base = new Base(this);
		switch (team) {
		case TEAM_0:
			base.setAnchor(Anchor.BOTTOM_LEFT);
			base.setPosition(0, BBTHGame.HEIGHT);
			break;
			
		case TEAM_1:
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
		Unit u = new Unit(team);
		switch (this.team) {
		case TEAM_0:
			u.setVelocity(MathUtils.randInRange(5, 10), (float) Math.PI * 3f / 2f);
			break;
			
		case TEAM_1:
			u.setVelocity(MathUtils.randInRange(-10, -5), (float) Math.PI * 3f / 2f);
			break;
		}
		u.setPosition(x, y);
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

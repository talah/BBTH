package bbth.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import bbth.game.units.Unit;
import bbth.game.units.UnitType;

public class UnitSelector {
	private static final float UNIT_SELECTOR_WIDTH = 50;
	private static final float UNIT_SELECTOR_HEIGHT = 30;
	private static final float UNIT_HEIGHT = UNIT_SELECTOR_HEIGHT / 3.f;
	
	private UnitType currentUnitType;
	private Paint paint;
	private Team team;
	private Unit attacker, defender, uberer;
	
	public UnitSelector(Team team) {
		currentUnitType = UnitType.ATTACKING;
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.team = team;
		
		attacker = UnitType.ATTACKING.createUnit(team, paint);
		//attacker.setPosition(UNIT_HEIGHT / 2);
		
		defender = UnitType.DEFENDING.createUnit(team, paint);
		
		uberer = UnitType.UBER.createUnit(team, paint);
	}

	public UnitType getUnitType() {
		return this.currentUnitType;
	}

	public void setUnitType(UnitType type) {
		this.currentUnitType = type;
	}

	public void draw(Canvas canvas) {
		paint.setARGB(127, 0, 0, 0);
		canvas.drawRect(BBTHGame.WIDTH - UNIT_SELECTOR_WIDTH, 0,
				BBTHGame.WIDTH, UNIT_SELECTOR_HEIGHT, paint);
		
		// Draw UnitType.ATTACKING
	}
}

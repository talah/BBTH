package bbth.game.units;

import android.graphics.*;
import bbth.game.Team;

public class DefendingUnit extends Unit {
	public DefendingUnit(Team team) {
		super(team);
	}
	
	private static final float LINE_LENGTH = 15f;
	
	RectF rect = new RectF(-10f, -10f, 10f, 10f);
	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		
		canvas.translate(getX(), getY());
		canvas.rotate(getHeading());
		
		canvas.drawRect(rect, paint);
		canvas.drawLine(0f, 0f, 0f, -LINE_LENGTH, paint);
		
		canvas.restore();
	}

	@Override
	public UnitType getType() {
		return UnitType.DEFENDING;
	}
	
}

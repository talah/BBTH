package bbth.game.units;

import android.graphics.*;
import bbth.game.Team;

public class UberUnit extends Unit {
	public UberUnit(Team team) {
		super(team);
	}
	
	private float[] outline = { 0f, -15f, 10f, 10f,
	                            10f, 10f, -10f, 10f,
	                            -10f, 10f, 0f, -15f,
	                          };
	@Override
	public void draw(Canvas canvas) {
		canvas.save();
		
		canvas.translate(getX(), getY());
		canvas.rotate(getHeading());
		canvas.drawPoints(outline, paint);
		
		canvas.restore();
	}

	@Override
	public UnitType getType() {
		return UnitType.UBER;
	}
	
}

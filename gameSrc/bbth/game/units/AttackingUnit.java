package bbth.game.units;

import android.graphics.Canvas;
import bbth.game.Team;

public class AttackingUnit extends Unit {

	public AttackingUnit(Team team) {
		super(team);
		// TODO Auto-generated constructor stub
	}
	
	public void draw(Canvas canvas) {
		canvas.drawCircle(this.getX(), this.getY(), 10, paint);
	}

}

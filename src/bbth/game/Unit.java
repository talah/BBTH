package bbth.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import bbth.entity.BasicMovable;
import bbth.game.Unit.Team;

/**
 * A BBTH unit is one of the little dudes that walk around on the map and kill each other.
 */
public class Unit extends BasicMovable {
	public enum Team {
		TEAM_0,
		TEAM_1;
		
		public Team getOppositeTeam() {
			if (this == TEAM_0) {
				return TEAM_1;
			} else {
				return TEAM_0;
			}
		}
	}
	
	private Team team;
	private Paint test_paint;
	
	public Unit() {
		team = Team.TEAM_0;
		test_paint = new Paint();
		test_paint.setColor(Color.RED);
	}

	public void update(float seconds) {
		this.update(seconds);
	}
	
	public void draw(Canvas canvas) {
		canvas.drawCircle(this.getX(), this.getY(), 10, test_paint);
	}
	
	public Team getTeam() {
		return this.team;
	}

	public void setTeam(Team newteam) {
		team = newteam;
	}
}

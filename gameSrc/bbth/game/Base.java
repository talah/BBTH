package bbth.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import bbth.engine.ui.UIView;

public class Base extends UIView {
	public static final float BASE_HEIGHT = 20;
	private Paint paint;
	private Team team;
	private Player player;

	public Base(Player player) {
		this.setSize(BBTHSimulation.GAME_WIDTH, BASE_HEIGHT);

		this.player = player;
		team = player.getTeam();
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextAlign(Align.CENTER);
	}

	@Override
	public void onDraw(Canvas canvas) {
		paint.setColor(team.getBaseColor());
		canvas.drawRect(_rect, paint);
		paint.setColor(Color.WHITE);
		String text = (player.isLocal() ? "Your" : "Enemy") + " health: " + (int) player.getHealth();
		canvas.drawText(text, (_rect.left + _rect.right) / 2, _rect.top + 13, paint);
	}

	public RectF getRect() {
		return _rect;
	}
}

package bbth.game;

import android.graphics.*;
import android.graphics.Paint.Align;
import bbth.engine.ui.UIView;

public class Base extends UIView {
	public static final float BASE_HEIGHT = 20;
	private Paint paint;
	private Team team;
	private Player player;
	public boolean drawFill = true;

	public Base(Player player) {
		this.setSize(BBTHSimulation.GAME_WIDTH, BASE_HEIGHT);

		this.player = player;
		team = player.getTeam();
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTextAlign(Align.CENTER);
	}

	public void draw(Canvas canvas, boolean serverDraw) {
		if (drawFill) {
			paint.setColor(team.getBaseColor());
			canvas.drawRect(_rect, paint);
		}
		paint.setColor(Color.WHITE);
		String text = (player.isLocal() ? "Your" : "Enemy") + " health: " + (int) player.getHealth();
		
		canvas.save();
		canvas.translate((_rect.left + _rect.right) / 2, _rect.top + 13);
		if (serverDraw) {
			canvas.scale(1.f, -1.f);
			canvas.translate(0, paint.getTextSize() / 2);
		}
		
		canvas.drawText(text, 0, 0, paint);
		canvas.restore();
		
	}

	@Override
	public RectF getRect() {
		return _rect;
	}
}

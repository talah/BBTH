package bbth.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.util.FloatMath;
import bbth.engine.sound.Beat;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIButton;
import bbth.engine.ui.UIButtonDelegate;
import bbth.engine.ui.UIView;

public class InteractiveTutorial extends Tutorial implements UIButtonDelegate {

	private static final Paint paint = new Paint();
	static {
		paint.setAntiAlias(true);
	}

	private abstract class Step extends UIView {
		@Override
		public boolean containsPoint(float x, float y) {
			return true; // >_>
		}
	}

	private class PlaceUnitStep extends Step {
		private static final float MIN_TIME = -6;
		private static final float MAX_TIME = 1;
		private float time = MIN_TIME;
		private Beat beat = Beat.tap(0);

		@Override
		public void onDraw(Canvas canvas) {
			paint.setColor(Color.GRAY);
			beat.draw((int) (time * 1000), BeatTrack.BEAT_LINE_X, BeatTrack.BEAT_LINE_Y, canvas, paint);

			float x = BBTHSimulation.GAME_X + BBTHSimulation.GAME_WIDTH / 2;
			float y = BBTHSimulation.GAME_Y + BBTHSimulation.GAME_HEIGHT * 0.85f;
			paint.setColor(Color.WHITE);
			paint.setTextSize(15);
			paint.setTextAlign(Align.CENTER);
			canvas.drawText("When the beat is between", x, y - 17, paint);
			canvas.drawText("the two lines, tap over", x, y, paint);
			canvas.drawText("here to create a unit", x, y + 17, paint);
			// drawArrow(canvas, 100, 100, 200, 200, 10);
		}

		@Override
		public void onUpdate(float seconds) {
			time += seconds;
			if (time > MAX_TIME) {
				time -= MAX_TIME - MIN_TIME;
			}
		}

		@Override
		public void onTouchDown(float x, float y) {
			if (x >= BBTHSimulation.GAME_X && y >= BBTHSimulation.GAME_Y + BBTHSimulation.GAME_HEIGHT / 2) {
				transition(new FinishedStep());
			}
		}
	}

	private class FinishedStep extends Step {
	}

	private Step step;
	private UIButton skipButton;

	public InteractiveTutorial() {
		skipButton = new UIButton("Skip Tutorial");
		skipButton.setAnchor(Anchor.TOP_RIGHT);
		skipButton.setSize(100, 30);
		skipButton.setPosition(BBTHGame.WIDTH - 10, 30);
		skipButton.setButtonDelegate(this);
		addSubview(skipButton);

		transition(new PlaceUnitStep());
	}

	@Override
	public boolean isFinished() {
		return step instanceof FinishedStep;
	}

	@Override
	public void onClick(UIButton button) {
		transition(new FinishedStep());
	}

	protected void transition(Step newStep) {
		if (step != null) {
			removeSubview(step);
		}
		step = newStep;
		if (step != null) {
			addSubview(step);
		}
	}

	protected static void drawArrow(Canvas canvas, float ax, float ay, float bx, float by, float r) {
		float dx = by - ay;
		float dy = bx - ax;
		float d = r / FloatMath.sqrt(dx * dx + dy * dy);
		dx *= d;
		dy *= d;
		Path path = new Path();
		path.moveTo(ax - dy, ay + dx);
		path.lineTo(bx - dy - dx, by + dx - dy);
		path.lineTo(bx - dy, by + dx);
		path.lineTo(bx + dy - dx, by - dx - dy);
		path.moveTo(ax + dy, ay - dx);
		canvas.drawPath(path, paint);
	}
}

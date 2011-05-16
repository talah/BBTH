package bbth.game;

import android.graphics.Canvas;
import android.graphics.Paint;
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

	private static abstract class Step extends UIView {
	}

	private static class PlaceUnitStep extends Step {
		private float time;
		private Beat beat = Beat.tap(0);

		@Override
		public void onDraw(Canvas canvas) {
			paint.setARGB(255, 127, 127, 127);
			beat.draw((int) (time * 1000), BeatTrack.BEAT_LINE_X, BeatTrack.BEAT_LINE_Y, canvas, paint);
		}

		@Override
		public void onUpdate(float seconds) {
			time += seconds;
			if (time > 2) {
				time -= 2;
			}
		}
	}

	private Step step;
	private UIButton skipButton;
	private boolean isFinished;

	public InteractiveTutorial() {
		skipButton = new UIButton("Skip Tutorial");
		skipButton.setAnchor(Anchor.TOP_RIGHT);
		skipButton.setSize(100, 30);
		skipButton.setPosition(BBTHGame.WIDTH - 10, 30);
		skipButton.setButtonDelegate(this);
		addSubview(skipButton);

		step = new PlaceUnitStep();
		addSubview(step);
	}

	@Override
	public boolean isFinished() {
		return isFinished;
	}

	@Override
	public void onClick(UIButton button) {
		isFinished = true;
	}
}

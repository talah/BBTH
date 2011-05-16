package bbth.game;

import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIButton;
import bbth.engine.ui.UIButtonDelegate;

public class InteractiveTutorial extends Tutorial implements UIButtonDelegate {

	private UIButton skipButton;
	private boolean isFinished;

	public InteractiveTutorial() {
		skipButton = new UIButton("Skip Tutorial", null);
		skipButton.setAnchor(Anchor.TOP_RIGHT);
		skipButton.setSize(100, 30);
		skipButton.setPosition(BBTHGame.WIDTH - 10, 30);
		skipButton.setButtonDelegate(this);
		addSubview(skipButton);
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

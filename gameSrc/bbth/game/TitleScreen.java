package bbth.game;

import bbth.engine.ui.Anchor;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UIView;

// TODO Pretty graphics
public class TitleScreen extends UIView {
	private UILabel titleBar;
	private float animDelay = 1.0f;

	public TitleScreen() {
		titleBar = new UILabel("Beat Back the Horde!", this);
		titleBar.setAnchor(Anchor.CENTER_CENTER);
		titleBar.setTextSize(20.f);
		titleBar.setPosition(160, BBTHGame.HEIGHT + 20);
		titleBar.animatePosition(160, 90, 3);
		this.addSubview(titleBar);
	}

	@Override
	public void onUpdate(float seconds) {
		super.onUpdate(seconds);

		if (!titleBar.isAnimatingPosition)
			animDelay -= seconds;
		if (animDelay <= 0)
			nextScreen = new GameSetupScreen();
	}
}

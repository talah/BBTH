package bbth.game;

import android.graphics.Paint.Align;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIButton;
import bbth.engine.ui.UIButtonDelegate;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UIView;

public class GameStatusMessageScreen extends UIView implements UIButtonDelegate {
	private UILabel message;
	private UIButton playAgain, quit;

	public GameStatusMessageScreen(String text, Object tag) {
		super(tag);

		message = new UILabel(text, tag);
		message.setAnchor(Anchor.TOP_CENTER);
		message.setPosition(BBTHGame.WIDTH / 2, 40);
		message.setTextSize(20);
		message.setTextAlign(Align.CENTER);
		this.addSubview(message);

		playAgain = new UIButton("Play Again", tag);
		playAgain.setAnchor(Anchor.BOTTOM_CENTER);
		playAgain.setPosition(BBTHGame.WIDTH / 3, 140);
		playAgain.setSize(60, 20);
		playAgain.setButtonDelegate(this);
		this.addSubview(playAgain);

		quit = new UIButton("Quit", tag);
		quit.setAnchor(Anchor.BOTTOM_CENTER);
		quit.setPosition(BBTHGame.WIDTH * 2 / 3, 140);
		quit.setSize(60, 20);
		quit.setButtonDelegate(this);
		this.addSubview(quit);

		this.setSize(BBTHGame.WIDTH, BBTHGame.HEIGHT);
	}

	@Override
	public void onTouchUp(UIView sender) {
		// Do nothing here
	}

	@Override
	public void onTouchDown(UIView sender) {
		// Do nothing here
	}

	@Override
	public void onTouchMove(UIView sender) {
		// Do nothing here
	}

	@Override
	public void onClick(UIButton button) {
		if (button == playAgain) {
			nextScreen = new GameSetupScreen();
		} else if (button == quit) {
			System.exit(0);
		}
	}

	public void setText(String text) {
		message.setText(text);
	}
}

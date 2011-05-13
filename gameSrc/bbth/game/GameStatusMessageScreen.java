package bbth.game;

import android.graphics.Paint.Align;
import bbth.engine.core.GameActivity;
import bbth.engine.net.bluetooth.Bluetooth;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIButton;
import bbth.engine.ui.UIButtonDelegate;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UIView;
import bbth.game.BeatTrack.Song;

public class GameStatusMessageScreen extends UIView implements UIButtonDelegate {
	public static class DisconnectScreen extends GameStatusMessageScreen {
		public DisconnectScreen() {
			super("You have been disconnected.", null);
		}
	}
	
	public static class WinScreen extends GameStatusMessageScreen {
		public WinScreen() {
			super("Congratulations! You win!", null);
		}
	}
	
	public static class LoseScreen extends GameStatusMessageScreen {
		public LoseScreen() {
			super("Oh noes, you lost the game :(", null);
		}
	}
	
	public static class TieScreen extends GameStatusMessageScreen {
		public TieScreen() {
			super("It's a tie!", null);
		}
	}
	
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
//			nextScreen = new GameSetupScreen();
			nextScreen = new InGameScreen(Team.SERVER, new Bluetooth(
					GameActivity.instance, new LockStepProtocol()),
					Song.DONKEY_KONG, new LockStepProtocol());
			
		} else if (button == quit) {
			System.exit(0);
		}
	}

	public void setText(String text) {
		message.setText(text);
	}
}

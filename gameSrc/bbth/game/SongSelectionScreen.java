package bbth.game;

import bbth.engine.core.GameActivity;
import bbth.engine.net.bluetooth.Bluetooth;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIButton;
import bbth.engine.ui.UIButtonDelegate;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UIScrollView;
import bbth.engine.ui.UIView;

public class SongSelectionScreen extends UIScrollView implements
		UIButtonDelegate {
	public SongSelectionScreen() {
		super(null);

		this.setSize(BBTHGame.WIDTH, BBTHGame.HEIGHT);

		UILabel title = new UILabel("Song Selection", null);
		title.setAnchor(Anchor.TOP_CENTER);
		title.setTextSize(30.f);
		title.setPosition(BBTHGame.WIDTH / 2.f, 80);
		this.addSubview(title);

		addButton(Song.DONKEY_KONG, "Donkey Kong", 0);
		addButton(Song.RETRO, "Retro", 1);
		addButton(Song.MISTAKE_THE_GETAWAY, "Mistake the Getaway", 2);
		addButton(Song.JAVLA_SLADDER, "Javla Sladder", 3);
		addButton(Song.ODINS_KRAFT, "Odin's Kraft", 4);
		addButton(Song.MIGHT_AND_MAGIC, "Might and Magic", 5);
	}

	private void addButton(Song song, String title, int idx) {
		UIButton button = new UIButton(title, song);
		button.setAnchor(Anchor.CENTER_CENTER);
		button.setSize(BBTHGame.WIDTH * 0.75f, 45);
		button.setPosition(BBTHGame.WIDTH / 2, BBTHGame.HEIGHT / 2 + (idx - 1)
				* 65);
		button.setButtonDelegate(this);
		this.addSubview(button);
	}

	@Override
	public void onTouchUp(UIView sender) {
		// Don't do anything

	}

	@Override
	public void onTouchDown(UIView sender) {
		// Don't do anything

	}

	@Override
	public void onTouchMove(UIView sender) {
		// Don't do anything

	}

	@Override
	public void onClick(UIButton button) {
		if (BBTHGame.IS_SINGLE_PLAYER) {
			LockStepProtocol lsp = new LockStepProtocol();

			nextScreen = new InGameScreen(Team.SERVER, new Bluetooth(
					GameActivity.instance, lsp), (Song) button.tag, lsp);
		} else {

		}
	}
}

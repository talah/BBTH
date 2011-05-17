package bbth.game;

import bbth.engine.net.bluetooth.Bluetooth;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.ui.*;

public class SongSelectionScreen extends UIScrollView implements
		UIButtonDelegate {
	private Team playerTeam;
	private Bluetooth bluetooth;
	private LockStepProtocol protocol;

	UINavigationController controller;
	
	boolean singlePlayer;
	
	public SongSelectionScreen(UINavigationController controller, Team playerTeam, Bluetooth bluetooth, LockStepProtocol protocol, boolean singlePlayer) {
		super(null);

		this.controller = controller;
		this.singlePlayer = singlePlayer;
		
		this.playerTeam = playerTeam;
		this.bluetooth = bluetooth;
		this.protocol = protocol;

		this.setSize(BBTHGame.WIDTH, BBTHGame.HEIGHT);

		UILabel title = new UILabel("Song Selection", null);
		title.setAnchor(Anchor.TOP_CENTER);
		title.setTextSize(30.f);
		title.setPosition(BBTHGame.WIDTH / 2.f, 80);
		this.addSubview(title);
		this.setScrollsHorizontal(false);

		int y = 0;
		this.addSubview(makeButton(Song.DERP, "Derp", y++));
		this.addSubview(makeButton(Song.DONKEY_KONG, "Donkey Kong", y++));
		this.addSubview(makeButton(Song.RETRO, "Retro", y++));
		this.addSubview(makeButton(Song.MISTAKE_THE_GETAWAY, "Mistake the Getaway", y++));
		this.addSubview(makeButton(Song.JAVLA_SLADDAR, "Javla Sladdar", y++));
		this.addSubview(makeButton(Song.ODINS_KRAFT, "Odin's Kraft", y++));
		this.addSubview(makeButton(Song.MIGHT_AND_MAGIC, "Might and Magic", y++));
		this.setContentRect(0, 0, BBTHGame.WIDTH, BBTHGame.HEIGHT / 2 + (y - 1) * 50);
	}

	private UIButton makeButton(Song song, String title, int idx) {
		UIButton button = new UIButton(title, song);
		button.setAnchor(Anchor.CENTER_CENTER);
		button.setSize(BBTHGame.WIDTH * 0.6f, 40);
		button.setPosition(BBTHGame.WIDTH / 2, BBTHGame.HEIGHT / 2 + (idx - 1) * 50);
		button.setButtonDelegate(this);
		return button;
	}

	@Override
	public void onClick(UIButton button) {
		controller.pushUnder(new InGameScreen(controller, playerTeam, bluetooth, (Song) button.tag, protocol, singlePlayer));
		controller.pop(BBTHGame.FROM_RIGHT_TRANSITION);
	}
}

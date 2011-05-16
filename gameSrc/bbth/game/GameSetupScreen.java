package bbth.game;

import android.graphics.Paint.Align;
import bbth.engine.core.GameActivity;
import bbth.engine.net.bluetooth.Bluetooth;
import bbth.engine.net.bluetooth.State;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.ui.Anchor;
import bbth.engine.ui.UIButton;
import bbth.engine.ui.UIButtonDelegate;
import bbth.engine.ui.UILabel;
import bbth.engine.ui.UINavigationController;
import bbth.engine.ui.UIView;

public class GameSetupScreen extends UIView implements UIButtonDelegate {
	private LockStepProtocol protocol;
	private Bluetooth bluetooth;

	private UIButton serverButton;
	private UIButton clientButton;
	private UIButton disconnectButton;
	private UIButton backButton;
	private UILabel label;

	private Team playerTeam;
	
	private UINavigationController controller;

	public GameSetupScreen(UINavigationController controller) {
		super(null);
		
		this.controller = controller;

		setSize(BBTHGame.WIDTH, BBTHGame.HEIGHT);
		setPosition(0, 0);

		protocol = new LockStepProtocol();
		bluetooth = new Bluetooth(GameActivity.instance, protocol);

		serverButton = new UIButton("Create a Game", null);
		serverButton.setAnchor(Anchor.CENTER_CENTER);
		serverButton.setPosition(BBTHGame.WIDTH / 2, BBTHGame.HEIGHT / 2 - 65);
		serverButton.setSize(BBTHGame.WIDTH * 0.75f, 45);
		serverButton.setButtonDelegate(this);
		addSubview(serverButton);

		clientButton = new UIButton("Join a Game", null);
		clientButton.setAnchor(Anchor.CENTER_CENTER);
		clientButton.setPosition(BBTHGame.WIDTH / 2, BBTHGame.HEIGHT / 2);
		clientButton.setSize(BBTHGame.WIDTH * 0.75f, 45);
		clientButton.setButtonDelegate(this);
		addSubview(clientButton);
		
		backButton = new UIButton("Back to Title", null);
		backButton.setAnchor(Anchor.BOTTOM_CENTER);
		backButton.setPosition(BBTHGame.WIDTH / 2, BBTHGame.HEIGHT - 20);
		backButton.setSize(BBTHGame.WIDTH * 0.75f, 45);
		backButton.setButtonDelegate(this);
		addSubview(backButton);

		disconnectButton = new UIButton("Cancel", null);
		disconnectButton.setAnchor(Anchor.CENTER_CENTER);
		disconnectButton.setPosition(BBTHGame.WIDTH / 2, BBTHGame.HEIGHT / 2 + 65);
		disconnectButton.setSize(BBTHGame.WIDTH * 0.75f, 45);
		disconnectButton.setButtonDelegate(this);
		disconnectButton.isDisabled = true;
		addSubview(disconnectButton);

		label = new UILabel("", null);
		label.setTextSize(15);
		label.setPosition(5, 30);
		label.setSize(BBTHGame.WIDTH - 10, 10);
		label.setTextAlign(Align.CENTER);
		addSubview(label);
	}

	@Override
	public void onUpdate(float seconds) {
		label.setText("Status: " + bluetooth.getString());

		if (bluetooth.getState() == State.CONNECTED) {
			if (playerTeam == Team.SERVER) {
				controller.pushUnder(new SongSelectionScreen(controller, playerTeam, bluetooth, protocol, false));
				controller.pop(BBTHGame.FADE_OUT_FADE_IN_TRANSITION);
			} else if (playerTeam == Team.CLIENT) {
				controller.pushUnder(new InGameScreen(controller, playerTeam, bluetooth, null, protocol, false));
				controller.pop(BBTHGame.FADE_OUT_FADE_IN_TRANSITION);
			}
		} else if (bluetooth.getState() == State.DISCONNECTED) {
			serverButton.isDisabled = false;
			clientButton.isDisabled = false;
			disconnectButton.isDisabled = true;
		}
	}

	@Override
	public void onClick(UIButton sender) {
		if (sender == serverButton) {
			bluetooth.listen();
			serverButton.isDisabled = true;
			clientButton.isDisabled = true;
			disconnectButton.isDisabled = false;

			playerTeam = Team.SERVER;
		} else if (sender == clientButton) {
			bluetooth.connect();
			serverButton.isDisabled = true;
			clientButton.isDisabled = true;
			disconnectButton.isDisabled = false;

			playerTeam = Team.CLIENT;
		} else if (sender == disconnectButton) {
			bluetooth.disconnect();
			serverButton.isDisabled = false;
			clientButton.isDisabled = false;
			disconnectButton.isDisabled = true;
		} else if (sender == backButton) {
			controller.pop(BBTHGame.MOVE_RIGHT_TRANSITION);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode) {
		bluetooth.onActivityResult(requestCode, resultCode);
	}
}

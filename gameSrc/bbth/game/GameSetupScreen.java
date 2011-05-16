package bbth.game;

import android.graphics.Paint.Align;
import bbth.engine.core.GameActivity;
import bbth.engine.net.bluetooth.*;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.engine.ui.*;

public class GameSetupScreen extends UIView implements UIButtonDelegate {
	private LockStepProtocol protocol;
	private Bluetooth bluetooth;

	private UIButton serverButton;
	private UIButton clientButton;
	private UIButton disconnectButton;
	private UILabel statusLabel;

	private Team playerTeam;
	
	private UINavigationController controller;
	private UILabel titleLabel;

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
		
		disconnectButton = new UIButton("Cancel", null);
		disconnectButton.setAnchor(Anchor.CENTER_CENTER);
		disconnectButton.setPosition(BBTHGame.WIDTH / 2, BBTHGame.HEIGHT / 2 + 65);
		disconnectButton.setSize(BBTHGame.WIDTH * 0.75f, 45);
		disconnectButton.setButtonDelegate(this);
		disconnectButton.isDisabled = true;
		addSubview(disconnectButton);

		titleLabel = new UILabel("Multiplayer", null);
		titleLabel.setTextSize(30.f);
		titleLabel.setAnchor(Anchor.CENTER_CENTER);
		titleLabel.setPosition(BBTHGame.WIDTH / 2, 80);
		titleLabel.setTextAlign(Align.CENTER);
		addSubview(titleLabel);
		
		statusLabel = new UILabel("", null);
		statusLabel.setTextSize(15);
		statusLabel.setItalics(true);
		statusLabel.setAnchor(Anchor.CENTER_CENTER);
		statusLabel.setPosition(BBTHGame.WIDTH / 2, 130);
		statusLabel.setSize(BBTHGame.WIDTH - 10, 10);
		statusLabel.setTextAlign(Align.CENTER);
		statusLabel.setWrapText(true);
		addSubview(statusLabel);
	}

	@Override
	public void onUpdate(float seconds) {
		String statusMessage = bluetooth.getString();
		statusLabel.setText((statusMessage == null) ? "" : statusMessage);

		if (bluetooth.getState() == State.CONNECTED) {
			if (playerTeam == Team.SERVER) {
				controller.pushUnder(new SongSelectionScreen(controller, playerTeam, bluetooth, protocol, false));
				controller.pop(BBTHGame.FROM_RIGHT_TRANSITION);
			} else if (playerTeam == Team.CLIENT) {
				controller.pushUnder(new InGameScreen(controller, playerTeam, bluetooth, null, protocol, false));
				controller.pop(BBTHGame.FROM_RIGHT_TRANSITION);
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
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode) {
		bluetooth.onActivityResult(requestCode, resultCode);
	}
}

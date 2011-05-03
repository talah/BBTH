package bbth.game;

import android.graphics.Paint.Align;
import bbth.core.GameActivity;
import bbth.net.bluetooth.Bluetooth;
import bbth.net.bluetooth.State;
import bbth.net.simulation.LockStepProtocol;
import bbth.ui.Anchor;
import bbth.ui.UIButton;
import bbth.ui.UIButtonDelegate;
import bbth.ui.UILabel;
import bbth.ui.UIView;

public class GameSetupScreen extends UIView implements UIButtonDelegate {

	private LockStepProtocol protocol;
	private Bluetooth bluetooth;

	private UIButton serverButton;
	private UIButton clientButton;
	private UIButton disconnectButton;
	private UILabel label;
	
	private Team playerTeam;
	
	public GameSetupScreen() {
		super(null);

		protocol = new LockStepProtocol();
		bluetooth = new Bluetooth(GameActivity.instance, protocol);

		serverButton = new UIButton("Server", null);
		serverButton.setAnchor(Anchor.CENTER_CENTER);
		serverButton.setPosition(BBTHGame.WIDTH / 2, BBTHGame.HEIGHT / 2 - 25);
		serverButton.setSize(100, 15);
		serverButton.delegate = this;
		addSubview(serverButton);

		clientButton = new UIButton("Client", null);
		clientButton.setAnchor(Anchor.CENTER_CENTER);
		clientButton.setPosition(BBTHGame.WIDTH / 2, BBTHGame.HEIGHT / 2);
		clientButton.setSize(100, 15);
		clientButton.delegate = this;
		addSubview(clientButton);

		disconnectButton = new UIButton("Disconnect", null);
		disconnectButton.setAnchor(Anchor.CENTER_CENTER);
		disconnectButton.setPosition(BBTHGame.WIDTH / 2, BBTHGame.HEIGHT / 2 + 25);
		disconnectButton.setSize(100, 15);
		disconnectButton.delegate = this;
		disconnectButton.isDisabled = true;
		addSubview(disconnectButton);

		label = new UILabel("", null);
		label.setTextSize(10);
		label.setPosition(10, 10);
		label.setSize(BBTHGame.WIDTH - 20, 10);
		label.setTextAlign(Align.CENTER);
		addSubview(label);
	}

	@Override
	public void onUpdate(float seconds) {
		label.setText("Status: " + bluetooth.getString());

		if (bluetooth.getState() == State.CONNECTED) {
			nextScreen = new InGameScreen(playerTeam, bluetooth, protocol);
		} else if (bluetooth.getState() == State.DISCONNECTED) {
			serverButton.isDisabled = false;
			clientButton.isDisabled = false;
			disconnectButton.isDisabled = true;
		}
	}

	@Override
	public void onTouchDown(UIView sender) {
	}

	@Override
	public void onTouchMove(UIView sender) {
	}

	@Override
	public void onTouchUp(UIView sender) {
	}

	@Override
	public void onClick(UIButton sender) {
		if (sender == serverButton) {
			bluetooth.listen();
			serverButton.isDisabled = true;
			clientButton.isDisabled = true;
			disconnectButton.isDisabled = false;
			
			playerTeam = Team.TEAM_1;
		} else if (sender == clientButton) {
			bluetooth.connect();
			serverButton.isDisabled = true;
			clientButton.isDisabled = true;
			disconnectButton.isDisabled = false;
			
			playerTeam = Team.TEAM_0;
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

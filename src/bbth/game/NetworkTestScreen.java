package bbth.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import bbth.core.GameActivity;
import bbth.net.NetworkTestSimulation;
import bbth.net.bluetooth.Bluetooth;
import bbth.net.bluetooth.State;
import bbth.net.simulation.LockStepProtocol;
import bbth.ui.UIButton;
import bbth.ui.UIButtonDelegate;
import bbth.ui.UILabel;
import bbth.ui.UIView;

public class NetworkTestScreen extends UIView implements UIButtonDelegate {

	private static final int SERVER = 1;
	private static final int CLIENT = 2;

	private Paint paint;
	private NetworkTestSimulation sim;
	private UIButton serverButton;
	private UIButton clientButton;
	private UILabel label;
	private UILabel timestepLabel;
	private Bluetooth bluetooth;
	private int type;
	private LockStepProtocol protocol;

	public NetworkTestScreen() {
		super(null);

		protocol = new LockStepProtocol();
		bluetooth = new Bluetooth(GameActivity.instance, protocol);

		paint = new Paint();
		paint.setAntiAlias(true);

		serverButton = new UIButton("Server", null);
		serverButton.setPosition(10, 10);
		serverButton.setSize(100, 15);
		serverButton.delegate = this;
		addSubview(serverButton);

		clientButton = new UIButton("Client", null);
		clientButton.setPosition(120, 10);
		clientButton.setSize(100, 15);
		clientButton.delegate = this;
		addSubview(clientButton);

		label = new UILabel("", null);
		label.setTextSize(10);
		label.setPosition(10, 40);
		label.sizeToFit();
		addSubview(label);

		timestepLabel = new UILabel("", null);
		timestepLabel.setTextSize(10);
		timestepLabel.setPosition(10, 50);
		timestepLabel.sizeToFit();
		addSubview(timestepLabel);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (sim != null) {
			sim.draw(canvas, paint);
		}
	}

	@Override
	public void onStop() {
		bluetooth.disconnect();
	}

	@Override
	public void onUpdate(float seconds) {
		if (bluetooth.getState() == State.CONNECTED && sim == null) {
			sim = new NetworkTestSimulation(protocol);
		} else if (bluetooth.getState() != State.CONNECTED && sim != null) {
			type = 0;
			clientButton.isDisabled = false;
			serverButton.isDisabled = false;
			sim = null;
		}

		switch (type) {
		case CLIENT:
			if (bluetooth.getState() == State.DISCONNECTED) {
				bluetooth.connect();
			}
			break;

		case SERVER:
			if (bluetooth.getState() == State.DISCONNECTED) {
				bluetooth.listen();
			}
			break;
		}

		label.setText("Status: " + bluetooth.getString());
		label.sizeToFit();

		if (sim != null) {
			sim.onUpdate(seconds);

			timestepLabel.setText("Timestep: " + sim.getTimestep());
			timestepLabel.sizeToFit();
		}
	}

	@Override
	public void onTouchDown(float x, float y) {
		super.onTouchDown(x, y);

		if (sim != null) {
			boolean isOnBeat = true;
			sim.recordTapDown(x, y, isOnBeat);
		}
	}

	@Override
	public void onTouchMove(float x, float y) {
		super.onTouchMove(x, y);

		if (sim != null) {
			sim.recordTapMove(x, y);
		}
	}

	@Override
	public void onTouchUp(float x, float y) {
		super.onTouchUp(x, y);

		if (sim != null) {
			sim.recordTapUp(x, y);
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
			type = SERVER;
		} else if (sender == clientButton) {
			type = CLIENT;
		}
		clientButton.isDisabled = true;
		serverButton.isDisabled = true;
	}

	/**
	 * Stupid method necessary because of android's weird context/activity mess.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode) {
		bluetooth.onActivityResult(requestCode, resultCode);
	}
}

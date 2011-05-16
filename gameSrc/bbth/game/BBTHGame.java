package bbth.game;

import android.app.Activity;
import bbth.engine.core.Game;
import bbth.engine.core.GameActivity;
import bbth.engine.net.bluetooth.Bluetooth;
import bbth.engine.net.simulation.LockStepProtocol;

public class BBTHGame extends Game {
	// This is the viewport width and height
	public static final float WIDTH = 320;
	public static final float HEIGHT = 530;
	public static final boolean DEBUG = false;
	public static final boolean IS_SINGLE_PLAYER = true;
	public static final Song SONG = Song.MISTAKE_THE_GETAWAY;

	public BBTHGame(Activity activity) {
		if (IS_SINGLE_PLAYER) {
			currentScreen = new SongSelectionScreen(Team.SERVER, new Bluetooth(
					GameActivity.instance, new LockStepProtocol()),
					new LockStepProtocol());
		} else {
			currentScreen = new GameSetupScreen();
		}
	}

	@Override
	public float getWidth() {
		return WIDTH;
	}

	@Override
	public float getHeight() {
		return HEIGHT;
	}
}

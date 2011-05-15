package bbth.game;

import android.app.Activity;
import bbth.engine.core.*;
import bbth.engine.net.bluetooth.Bluetooth;
import bbth.engine.net.simulation.LockStepProtocol;

public class BBTHGame extends Game {
	// This is the viewport width and height
	public static final float WIDTH = 320;
	public static final float HEIGHT = 530;
	public static final boolean DEBUG = true;
	public static final boolean IS_SINGLE_PLAYER = true;
	public static final Song SONG = Song.MISTAKE_THE_GETAWAY;

	public BBTHGame(Activity activity) {
		if (IS_SINGLE_PLAYER) {
			currentScreen = new SongSelectionScreen();
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

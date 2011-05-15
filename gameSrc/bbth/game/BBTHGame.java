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
//	public static final Song SONG = Song.MIGHT_AND_MAGIC;
	public static final Song SONG = Song.DONKEY_KONG;

	public BBTHGame(Activity activity) {
//		currentScreen = new TitleScreen(null);
//		currentScreen = new BBTHAITest(this);
//		currentScreen = new MusicTestScreen(activity);
//		currentScreen = new NetworkTestScreen();
//		currentScreen = new TransitionTest();
//		currentScreen = new GameSetupScreen();
//		currentScreen = new CombatTest(this);

		if (IS_SINGLE_PLAYER) {
			currentScreen = new InGameScreen(Team.SERVER, new Bluetooth(
					GameActivity.instance, new LockStepProtocol()),
					SONG, new LockStepProtocol());
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

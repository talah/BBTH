package bbth.game;

import android.app.Activity;
import bbth.engine.core.Game;
import bbth.engine.core.GameActivity;
import bbth.engine.net.bluetooth.Bluetooth;
import bbth.engine.net.simulation.LockStepProtocol;
import bbth.game.BeatTrack.Song;

public class BBTHGame extends Game {
	// This is the viewport width and height
	public static final float WIDTH = 320;
	public static final float HEIGHT = 530;
	public static final boolean DEBUG = true;
	public static final boolean IS_SINGLE_PLAYER = false;

	public BBTHGame(Activity activity) {
		// currentScreen = new TitleScreen(null);
		// currentScreen = new BBTHAITest(this);
		// currentScreen = new MusicTestScreen(activity);
		// currentScreen = new NetworkTestScreen();
		// currentScreen = new TransitionTest();
		// currentScreen = new GameSetupScreen();
		// currentScreen = new CombatTest(this);

		if (IS_SINGLE_PLAYER) {
			currentScreen = new InGameScreen(Team.SERVER, new Bluetooth(
					GameActivity.instance, new LockStepProtocol()),
					Song.DONKEY_KONG, new LockStepProtocol());
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

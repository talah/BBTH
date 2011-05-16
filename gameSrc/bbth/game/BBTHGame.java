package bbth.game;

import android.app.Activity;
import bbth.engine.core.Game;
import bbth.engine.ui.UINavigationController;

public class BBTHGame extends Game {
	// This is the viewport width and height
	public static final float WIDTH = 320;
	public static final float HEIGHT = 530;
	public static final boolean DEBUG = false;
	public static final Song SONG = Song.MISTAKE_THE_GETAWAY;

	public BBTHGame(Activity activity) {
		UINavigationController controller = new UINavigationController(null);
		currentScreen = controller;
		
		controller.push(new TitleScreen(controller));
		
//		controller.push(new BBTHAITest(this));
//		controller.push(new MusicTestScreen(activity));
//		controller.push(new NetworkTestScreen());
//		controller.push(new TransitionTest());
//		controller.push(new GameSetupScreen());
//		controller.push(new CombatTest(this));

//		if (IS_SINGLE_PLAYER) {
//			controller.push(new InGameScreen(Team.SERVER, new Bluetooth(
//					GameActivity.instance, new LockStepProtocol()),
//					SONG, new LockStepProtocol()));
//		} else {
//			controller.push(new GameSetupScreen());
//		}
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

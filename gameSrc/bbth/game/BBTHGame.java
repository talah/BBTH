package bbth.game;

import android.app.Activity;
import bbth.engine.core.Game;
import bbth.engine.ui.UINavigationController;

public class BBTHGame extends Game {
	// This is the viewport width and height
	public static final float WIDTH = 320;
	public static final float HEIGHT = 530;
	public static final boolean DEBUG = false;

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

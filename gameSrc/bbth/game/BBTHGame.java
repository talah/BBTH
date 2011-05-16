package bbth.game;

import android.app.Activity;
import bbth.engine.core.Game;
import bbth.engine.core.GameActivity;
import bbth.engine.ui.UINavigationController;

public class BBTHGame extends Game {
	// This is the viewport width and height
	public static final float WIDTH = 320;
	public static final float HEIGHT = 530;
	public static final boolean DEBUG = false;
	public static final Song SONG = Song.MISTAKE_THE_GETAWAY;
	
	private UINavigationController navController;

	public BBTHGame(Activity activity) {
		navController = new UINavigationController(null);
		currentScreen = navController;
		
		navController.push(new TitleScreen(navController));
		
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
	
	@Override
	public void onBackPressed() {
		if (!navController.pop()) {
			GameActivity.instance.finish();
		}
	}
}

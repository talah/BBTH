package bbth.game;

import android.app.Activity;
import bbth.engine.core.Game;

public class BBTHGame extends Game {
	// This is the viewport width and height
	public static final float WIDTH = 320;
	public static final float HEIGHT = 180;

	public static final GameStatusMessageScreen DISCONNECT_SCREEN = new GameStatusMessageScreen(
			"You have been disconnected", null);
	public static final GameStatusMessageScreen WIN_SCREEN = new GameStatusMessageScreen(
			"Congratulations! You won!", null);
	public static final GameStatusMessageScreen LOSE_SCREEN = new GameStatusMessageScreen(
			"Oh noes, you lost :(", null);
	public static final TitleScreen TITLE_SCREEN = new TitleScreen();

	public BBTHGame(Activity activity) {
//		currentScreen = new TitleScreen(null);
//		currentScreen = new BBTHAITest(this);
//		currentScreen = new MusicTestScreen(activity);
//		currentScreen = new NetworkTestScreen();
//		currentScreen = new TransitionTest();
//		currentScreen = new GameSetupScreen();
//		currentScreen = new CombatTest(this);
//		currentScreen = new InGameScreen(Team.SERVER, new Bluetooth(
//				GameActivity.instance, new LockStepProtocol()),
//				Song.DONKEY_KONG, new LockStepProtocol());
		
		currentScreen = new GameSetupScreen();
//		currentScreen = TITLE_SCREEN;
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

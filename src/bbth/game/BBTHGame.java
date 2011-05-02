package bbth.game;

import android.app.Activity;
import bbth.core.Game;

public class BBTHGame extends Game {

	public static final float WIDTH = 320;
	public static final float HEIGHT = 180;

	public BBTHGame(Activity activity) {
		// currentScreen = new TitleScreen(null);
		// currentScreen = new BBTHAITest(this);
		// currentScreen = new MusicTestScreen(activity);
		currentScreen = new NetworkTestScreen();
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

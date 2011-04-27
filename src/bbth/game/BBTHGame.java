package bbth.game;

import bbth.core.Game;

public class BBTHGame extends Game {

	public static final float WIDTH = 320;
	public static final float HEIGHT = 180;

	public BBTHGame() {
		currentScreen = new TitleScreen();
	}

	@Override
	public float getWidth() {
		return 1.5f;
	}

	@Override
	public float getHeight() {
		return 1.5f;
	}
}

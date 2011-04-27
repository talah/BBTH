package bbth.game;

import bbth.core.Game;
import bbth.core.GameActivity;

public class BBTHActivity extends GameActivity {

	@Override
	protected Game getGame() {
		return new BBTHGame();
	}
}

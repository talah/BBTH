package bbth.game;

import bbth.engine.core.*;

public class BBTHActivity extends GameActivity {

	@Override
	protected Game getGame() {
		return new BBTHGame(this);
	}
}

package bbth.game;

import bbth.engine.achievements.Achievements;
import bbth.engine.core.Game;
import bbth.engine.core.GameActivity;

public class BBTHActivity extends GameActivity {

	@Override
	protected Game getGame() {
		Achievements.INSTANCE.initialize(this);
		return new BBTHGame(this);
	}
}

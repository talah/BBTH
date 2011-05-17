package bbth.game.achievements.events;

import bbth.game.*;

public class UnitKilledEvent extends BBTHAchievementEvent {

	public UnitKilledEvent(Song song, Player localPlayer, boolean singleplayer, float aiDifficulty) {
		super(song, localPlayer, singleplayer, aiDifficulty);
	}

}

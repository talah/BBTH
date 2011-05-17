package bbth.game.achievements.events;

import bbth.game.*;

public class WallCreatedEvent extends BBTHAchievementEvent {
	public WallCreatedEvent(Song song, Player localPlayer, boolean singleplayer, float aiDifficulty) {
		super(song, localPlayer, singleplayer, aiDifficulty);
	}
}

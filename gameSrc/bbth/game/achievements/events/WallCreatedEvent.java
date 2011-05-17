package bbth.game.achievements.events;

import bbth.game.*;

public class WallCreatedEvent extends BBTHAchievementEvent {
	public WallCreatedEvent(Song song, Player localPlayer) {
		super(song, localPlayer);
	}
}

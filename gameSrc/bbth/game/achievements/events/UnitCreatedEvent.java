package bbth.game.achievements.events;

import bbth.game.*;

public class UnitCreatedEvent extends BBTHAchievementEvent {

	public UnitCreatedEvent(Song song, Player localPlayer) {
		super(song, localPlayer);
	}

}

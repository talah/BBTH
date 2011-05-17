package bbth.game.achievements.events;

import bbth.game.*;

public class BeatHitEvent extends BBTHAchievementEvent {

	public BeatHitEvent(Song song, Player localPlayer) {
		super(song, localPlayer);
	}

}

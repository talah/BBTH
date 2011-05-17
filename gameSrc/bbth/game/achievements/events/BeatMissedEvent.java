package bbth.game.achievements.events;

import bbth.game.*;

public class BeatMissedEvent extends BBTHAchievementEvent {

	public BeatMissedEvent(Song song, Player localPlayer, boolean singleplayer, float aiDifficulty) {
		super(song, localPlayer, singleplayer, aiDifficulty);
	}

}

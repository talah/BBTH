package bbth.game.achievements.events;

import bbth.game.*;

public class BeatHitEvent extends BBTHAchievementEvent {

	public BeatHitEvent(Song song, Player localPlayer, boolean singleplayer, float aiDifficulty) {
		super(song, localPlayer, singleplayer, aiDifficulty);
	}

}

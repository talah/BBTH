package bbth.game.achievements.events;

import bbth.game.Player;
import bbth.game.Song;

public class UpdateEvent extends BBTHAchievementEvent {

	public UpdateEvent(Song song, Player localPlayer, boolean singleplayer, float aiDifficulty) {
		super(song, localPlayer, singleplayer, aiDifficulty);
	}

}

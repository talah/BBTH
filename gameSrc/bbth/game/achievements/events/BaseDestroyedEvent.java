package bbth.game.achievements.events;

import bbth.game.*;

public class BaseDestroyedEvent extends BBTHAchievementEvent {

	public BaseDestroyedEvent(Song song, Player localPlayer, boolean singleplayer, float aiDifficulty) {
		super(song, localPlayer, singleplayer, aiDifficulty);
	}

	Player destroyedBaseOwner;
	
	public void set(Player destroyedBaseOwner) {
		this.destroyedBaseOwner = destroyedBaseOwner;
	}

	public Player getDestroyedBaseOwner() {
		return destroyedBaseOwner;
	}

}

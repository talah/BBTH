package bbth.game.achievements.events;

import bbth.game.*;

public class BaseDestroyedEvent extends BBTHAchievementEvent {

	public BaseDestroyedEvent(Song song, Player localPlayer) {
		super(song, localPlayer);
	}

	Player destroyedBaseOwner;
	
	public void set(Player destroyedBaseOwner) {
		this.destroyedBaseOwner = destroyedBaseOwner;
	}

	public Player getDestroyedBaseOwner() {
		return destroyedBaseOwner;
	}

}

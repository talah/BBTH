package bbth.game.achievements.events;

import bbth.game.*;

public class BaseDestroyedEvent extends BBTHAchievementEvent {

	public BaseDestroyedEvent(BBTHSimulation simulation, boolean singleplayer, float aiDifficulty) {
		super(simulation, singleplayer, aiDifficulty);
	}

	Player destroyedBaseOwner;
	
	public void set(Player destroyedBaseOwner) {
		this.destroyedBaseOwner = destroyedBaseOwner;
	}

	public Player getDestroyedBaseOwner() {
		return destroyedBaseOwner;
	}

}

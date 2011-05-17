package bbth.game.achievements.events;

import bbth.game.*;

public class GameEndedEvent extends BBTHAchievementEvent {

	public GameEndedEvent(Song song, Player localPlayer) {
		super(song, localPlayer);
	}
	
	boolean tie;
	Player winningPlayer;
	
	public void set(boolean tie, Player winningPlayer) {
		this.tie = tie;
		this.winningPlayer = winningPlayer;
	}

	public Player getWinningPlayer() {
		return winningPlayer;
	}

	public boolean isTie() {
		return tie;
	}

}

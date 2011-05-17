package bbth.game.achievements.events;

import bbth.game.*;

public class GameEndedEvent extends BBTHAchievementEvent {

	public GameEndedEvent(Song song, Player localPlayer, Player winningPlayer, boolean tie) {
		super(song, localPlayer);
		this.winningPlayer = winningPlayer;
		this.tie = tie;
	}
	
	boolean tie;
	Player winningPlayer;

	public Player getWinningPlayer() {
		return winningPlayer;
	}

	public boolean isTie() {
		return tie;
	}

}

package bbth.game.achievements.events;

import bbth.game.Player;
import bbth.game.Song;

public class GameEndedEvent extends BBTHAchievementEvent {

	public GameEndedEvent(Song song, Player localPlayer, boolean singleplayer, float aiDifficulty) {
		super(song, localPlayer, singleplayer, aiDifficulty);
	}
	
	boolean tie;
	Player winningPlayer;

	public Player getWinningPlayer() {
		return winningPlayer;
	}

	public boolean isTie() {
		return tie;
	}

	public void set(Player winningPlayer, boolean tie) {
		this.winningPlayer = winningPlayer;
		this.tie = tie;
	}

}

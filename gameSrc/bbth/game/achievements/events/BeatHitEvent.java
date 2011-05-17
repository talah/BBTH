package bbth.game.achievements.events;

import bbth.game.*;

public class BeatHitEvent extends BBTHAchievementEvent {
	Player hittingPlayer;

	public BeatHitEvent(Song song, Player localPlayer, boolean singleplayer, float aiDifficulty) {
		super(song, localPlayer, singleplayer, aiDifficulty);
	}
	
	public void set(Player hittingPlayer) {
		this.hittingPlayer = hittingPlayer;
	}
	
	public Player getHittingPlayer() {
		return hittingPlayer;
	}

	public float getCombo() {
		return hittingPlayer.getCombo();
	}

}

package bbth.game.achievements.events;

import bbth.game.*;

public class BBTHAchievementEvent {
	Song song;
	Player localPlayer;
	boolean singleplayer;
	float aiDifficulty;
	
	public BBTHAchievementEvent(Song song, Player localPlayer, boolean singleplayer, float aiDifficulty) {
		this.song = song;
		this.localPlayer = localPlayer;
		this.singleplayer = singleplayer;
		this.aiDifficulty = aiDifficulty;
	}

	public Song getSong() {
		return song;
	}
	
	public Player getLocalPlayer() {
		return localPlayer;
	}

	public boolean isSingleplayer() {
		return singleplayer;
	}

	public float getAiDifficulty() {
		return aiDifficulty;
	}
	
}

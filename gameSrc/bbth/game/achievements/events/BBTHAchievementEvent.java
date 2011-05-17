package bbth.game.achievements.events;

import bbth.game.*;

public class BBTHAchievementEvent {
	Song song;
	Player localPlayer;
	
	public BBTHAchievementEvent(Song song, Player localPlayer) {
		this.song = song;
		this.localPlayer = localPlayer;
	}

	public Song getSong() {
		return song;
	}
	
	public Player getLocalPlayer() {
		return localPlayer;
	}
	
}

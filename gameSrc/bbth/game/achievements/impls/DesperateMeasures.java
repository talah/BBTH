package bbth.game.achievements.impls;

import bbth.engine.achievements.AchievementInfo;
import bbth.game.achievements.BBTHAchievement;
import bbth.game.achievements.events.GameEndedEvent;

public class DesperateMeasures extends BBTHAchievement {

	public DesperateMeasures(AchievementInfo achievementInfo) {
		super(achievementInfo);
	}

	@Override
	public void gameEnded(GameEndedEvent e) {
//		BeatTrack track = e.getBeatTrack();
//		if (track == null) {
//			System.err.println("Error: no beat track.");
//			return;
//		}
//		track.
//				
//		Achievements.INSTANCE.increment(achievementInfo);
	}

	@Override
	public boolean usesGameEnded() {
		return true;
	}

	
}

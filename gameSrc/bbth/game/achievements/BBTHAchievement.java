package bbth.game.achievements;

import bbth.engine.achievements.*;

public abstract class BBTHAchievement extends Achievement {

	public BBTHAchievement(AchievementInfo achievementInfo) {
		super(achievementInfo);
	}

	public void baseDestroyed() {}
	public void gameStarted() {}
	public void gameEnded() {}
	public void unitKilled() {}
	public void unitCreated() {}
	public void beatHit() {}
	public void beatMissed() {}
	public void wallCreated() {}
	public void update() {}
}

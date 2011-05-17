package bbth.game.achievements;

import bbth.engine.achievements.*;
import bbth.game.achievements.events.*;

public abstract class BBTHAchievement extends Achievement {

	public BBTHAchievement(AchievementInfo achievementInfo) {
		super(achievementInfo);
	}

	public void baseDestroyed(BaseDestroyedEvent e) {}
	public void gameEnded(GameEndedEvent e) {}
	public void unitKilled(UnitKilledEvent e) {}
	public void unitCreated(UnitCreatedEvent e) {}
	public void beatHit(BeatHitEvent e) {}
	public void beatMissed(BeatMissedEvent e) {}
	public void wallCreated(WallCreatedEvent e) {}
	public void update(UpdateEvent e) {}

	public boolean usesBaseDestroyed() { return false; }
	public boolean usesGameEnded() { return false; }
	public boolean usesUnitKilled() { return false; }
	public boolean usesUnitCreated() { return false; }
	public boolean usesBeatHit() { return false; }
	public boolean usesBeatMissed() { return false; }
	public boolean usesWallCreated() { return false; }
	public boolean usesUpdate() { return false; }
}

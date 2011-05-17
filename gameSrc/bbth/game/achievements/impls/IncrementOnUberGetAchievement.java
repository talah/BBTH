package bbth.game.achievements.impls;

import bbth.engine.achievements.AchievementInfo;
import bbth.engine.achievements.Achievements;
import bbth.game.achievements.BBTHAchievement;
import bbth.game.achievements.events.UnitCreatedEvent;
import bbth.game.units.UnitType;

public class IncrementOnUberGetAchievement extends BBTHAchievement {

	public IncrementOnUberGetAchievement(AchievementInfo achievementInfo) {
		super(achievementInfo);
	}

	@Override
	public void unitCreated(UnitCreatedEvent e) {
		if (e.getUnitType() == UnitType.UBER && e.getLocalPlayer().getTeam() == e.getUnit().getTeam()) {
			Achievements.INSTANCE.increment(achievementInfo);
		}
	}

	@Override
	public boolean usesUnitCreated() {
		return true;
	}
	
	

}

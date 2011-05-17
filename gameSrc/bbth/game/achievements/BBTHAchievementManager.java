package bbth.game.achievements;

import bbth.engine.achievements.*;
import bbth.game.R;

public final class BBTHAchievementManager extends AchievementManager<BBTHAchievement> {
	public static final BBTHAchievementManager INSTANCE = new BBTHAchievementManager(R.xml.achievements);
	
	private BBTHAchievementManager(int achievementsResourceID) {
		super(achievementsResourceID);
for (AchievementInfo achievementInfo : infoMap.values()) {
	System.out.println("("+achievementInfo.id+") \""+achievementInfo.name+"\": "+achievementInfo.description);
}
	}
	
	public void initialize() {
		// static initializer called, yay
	}

	@Override
	protected void registerAchievements() {
		AchievementInfo info;
		
		info = infoMap.get(0);
		if (info != null) {
			achievements.add(new Escapist(info));
		}
	}

}

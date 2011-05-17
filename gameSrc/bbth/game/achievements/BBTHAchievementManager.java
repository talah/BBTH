package bbth.game.achievements;

import bbth.engine.achievements.*;
import bbth.engine.util.Bag;
import bbth.game.*;
import bbth.game.achievements.events.*;
import bbth.game.achievements.impls.SongAchievement;

public final class BBTHAchievementManager extends AchievementManager<BBTHAchievement> {
	public static final BBTHAchievementManager INSTANCE = new BBTHAchievementManager(R.xml.achievements);
	
	private BBTHAchievementManager(int achievementsResourceID) {
		super(achievementsResourceID);
for (AchievementInfo achievementInfo : infoMap.values()) {
	System.out.println("("+achievementInfo.id+") \""+achievementInfo.name+"\": "+achievementInfo.description);
}
	}
	
	public void unregisterAchievementFromEvents(BBTHAchievement achievement) {
		if (achievement.usesBaseDestroyed())
			baseDestroyedAchievements.remove(achievement);
		if (achievement.usesGameEnded())
			gameEndedAchievements.remove(achievement);
		if (achievement.usesUnitKilled())
			unitKilledAchievements.remove(achievement);
		if (achievement.usesUnitCreated())
			unitCreatedAchievements.remove(achievement);
		if (achievement.usesBeatHit())
			beatHitAchievements.remove(achievement);
		if (achievement.usesBeatMissed())
			beatMissedAchievements.remove(achievement);
		if (achievement.usesWallCreated())
			wallCreatedAchievements.remove(achievement);
		if (achievement.usesUpdate())
			updateAchievements.remove(achievement);
	}
	
	void postRegisterAchievements() {
		for (BBTHAchievement achievement : achievements) {
			if (achievement.usesBaseDestroyed())
				baseDestroyedAchievements.add(achievement);
			if (achievement.usesGameEnded())
				gameEndedAchievements.add(achievement);
			if (achievement.usesUnitKilled())
				unitKilledAchievements.add(achievement);
			if (achievement.usesUnitCreated())
				unitCreatedAchievements.add(achievement);
			if (achievement.usesBeatHit())
				beatHitAchievements.add(achievement);
			if (achievement.usesBeatMissed())
				beatMissedAchievements.add(achievement);
			if (achievement.usesWallCreated())
				wallCreatedAchievements.add(achievement);
			if (achievement.usesUpdate())
				updateAchievements.add(achievement);
		}
	}
	
	private Bag<BBTHAchievement> baseDestroyedAchievements = new Bag<BBTHAchievement>(); 
	public void notifyBaseDestroyed(BaseDestroyedEvent e) { for (BBTHAchievement achievement : achievements) achievement.baseDestroyed(e); }
	private Bag<BBTHAchievement> gameEndedAchievements = new Bag<BBTHAchievement>(); 
	public void notifyGameEnded(GameEndedEvent e) { for (BBTHAchievement achievement : achievements) achievement.gameEnded(e); }
	private Bag<BBTHAchievement> unitKilledAchievements = new Bag<BBTHAchievement>(); 
	public void notifyUnitKilled(UnitKilledEvent e) { for (BBTHAchievement achievement : achievements) achievement.unitKilled(e); }
	private Bag<BBTHAchievement> unitCreatedAchievements = new Bag<BBTHAchievement>(); 
	public void notifyUnitCreated(UnitCreatedEvent e) { for (BBTHAchievement achievement : achievements) achievement.unitCreated(e); }
	private Bag<BBTHAchievement> beatHitAchievements = new Bag<BBTHAchievement>(); 
	public void notifyBeatHit(BeatHitEvent e) { for (BBTHAchievement achievement : achievements) achievement.beatHit(e); }
	private Bag<BBTHAchievement> beatMissedAchievements = new Bag<BBTHAchievement>(); 
	public void notifyBeatMissed(BeatMissedEvent e) { for (BBTHAchievement achievement : achievements) achievement.beatMissed(e); }
	private Bag<BBTHAchievement> wallCreatedAchievements = new Bag<BBTHAchievement>(); 
	public void notifyWallCreated(WallCreatedEvent e) { for (BBTHAchievement achievement : achievements) achievement.wallCreated(e); }
	private Bag<BBTHAchievement> updateAchievements = new Bag<BBTHAchievement>(); 
	public void notifyUpdate(UpdateEvent e) { for (BBTHAchievement achievement : achievements) achievement.update(e); }
	
	public void initialize() {
		// static initializer called, yay
	}

	@Override
	protected void registerAchievements() {
		AchievementInfo info;
		
		info = infoMap.get(0);
		if (info != null) { achievements.add(new SongAchievement(info, Song.MISTAKE_THE_GETAWAY)); }
		
		info = infoMap.get(1);
		if (info != null) { achievements.add(new SongAchievement(info, Song.MIGHT_AND_MAGIC)); }
		
		info = infoMap.get(2);
		if (info != null) { achievements.add(new SongAchievement(info, Song.RETRO)); }
		
		info = infoMap.get(3);
		if (info != null) { achievements.add(new SongAchievement(info, Song.JAVLA_SLADDAR)); }
		
		info = infoMap.get(4);
		if (info != null) { achievements.add(new SongAchievement(info, Song.ODINS_KRAFT)); }
		
		postRegisterAchievements();
	}
	
}

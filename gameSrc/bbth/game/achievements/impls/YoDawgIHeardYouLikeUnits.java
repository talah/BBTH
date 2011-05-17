package bbth.game.achievements.impls;

import bbth.engine.achievements.AchievementInfo;
import bbth.engine.util.Bag;
import bbth.game.Team;
import bbth.game.achievements.BBTHAchievement;
import bbth.game.achievements.events.UnitCreatedEvent;
import bbth.game.units.Unit;

public class YoDawgIHeardYouLikeUnits extends BBTHAchievement {

	public YoDawgIHeardYouLikeUnits(AchievementInfo achievementInfo) {
		super(achievementInfo);
	}

	private static final float dist = 10f;
	
	@Override
	public void unitCreated(UnitCreatedEvent e) {
		Unit unit = e.getUnit();
		Team team = e.getLocalPlayer().getTeam(); 
		if (team == unit.getTeam()) {
			Bag<Unit> otherUnits = e.getUnitsInCircle(unit.getX(), unit.getY(), unit.getRadius());
//System.out.println("Units in small circle: "+otherUnits.size());
//otherUnits = e.getUnitsInCircle(unit.getX(), unit.getY(), unit.getRadius()*2f);
//System.out.println("Units in double circle: "+otherUnits.size());
//otherUnits = e.getUnitsInCircle(unit.getX(), unit.getY(), unit.getRadius()*4f);
//System.out.println("Units in 4x circle: "+otherUnits.size());
			for (Unit otherUnit : otherUnits) {
				if (team.isEnemy(otherUnit.getTeam())) {
					increment();
					break;
				}
			}
		}
	}

	@Override
	public boolean usesUnitCreated() {
		return true;
	}
	
	

}

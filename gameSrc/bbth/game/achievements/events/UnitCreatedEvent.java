package bbth.game.achievements.events;

import bbth.game.*;
import bbth.game.units.Unit;
import bbth.game.units.UnitType;

public class UnitCreatedEvent extends BBTHAchievementEvent {

	private Unit m_unit;

	public UnitCreatedEvent(Song song, Player localPlayer) {
		super(song, localPlayer);
	}
	
	public void set(Unit u) {
		m_unit = u;
	}

	public UnitType getUnitType() {
		return m_unit.getType();
	}

}

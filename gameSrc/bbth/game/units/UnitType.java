package bbth.game.units;

import android.graphics.Paint;
import bbth.game.Team;

public enum UnitType {
	ATTACKING {
		@Override
		public Unit createUnit(UnitManager manager, Team team, Paint p) {
			return new AttackingUnit(manager, team, p);
		}
	},
	DEFENDING {
		@Override
		public Unit createUnit(UnitManager manager, Team team, Paint p) {
			return new DefendingUnit(manager, team, p);
		}
	},
	UBER {
		@Override
		public Unit createUnit(UnitManager manager, Team team, Paint p) {
			return new UberUnit(manager, team, p);
		}
	};
	
	public abstract Unit createUnit(UnitManager manager, Team team, Paint p);
}

package bbth.game.units;

import android.graphics.Paint;
import bbth.game.Team;

public enum UnitType {
	ATTACKING {
		@Override
		public Unit createUnit(Team team, Paint p) {
			return new AttackingUnit(team, p);
		}
	},
	DEFENDING {
		@Override
		public Unit createUnit(Team team, Paint p) {
			return new DefendingUnit(team, p);
		}
	},
	UBER {
		@Override
		public Unit createUnit(Team team, Paint p) {
			return new UberUnit(team, p);
		}
	};
	
	public abstract Unit createUnit(Team team, Paint p);
}

package bbth.game.units;

import android.graphics.Paint;
import bbth.engine.fastgraph.Wall;
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
	},
	WALL {
		@Override
		public Unit createUnit(UnitManager manager, Team team, Paint p) {
			return new WallUnit(new Wall(0, 0, 0, 0), manager, team, p);
		}
	};
	
	public abstract Unit createUnit(UnitManager manager, Team team, Paint p);
	
	public static UnitType fromInt(int type) {
		switch (type) {
		case 0: return UnitType.ATTACKING;
		case 1: return UnitType.DEFENDING;
		}
		
		return null;
	}
}

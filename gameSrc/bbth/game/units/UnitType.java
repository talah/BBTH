package bbth.game.units;

import bbth.game.Team;

public enum UnitType {
	ATTACKING {
		@Override
		public Unit createUnit(Team team) {
			return new AttackingUnit(team);
		}
	},
	DEFENDING {
		@Override
		public Unit createUnit(Team team) {
			return null;
		}
	},
	UBER {
		@Override
		public Unit createUnit(Team team) {
			return null;
		}
	};
	
	public abstract Unit createUnit(Team team);
}

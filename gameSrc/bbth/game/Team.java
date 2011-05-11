package bbth.game;

import android.graphics.Color;

public enum Team {
	CLIENT {
		public int getColor() {
			return Color.BLUE;
		}
		
		public Team getOppositeTeam() {
			return Team.SERVER;
		}
	},
	SERVER {
		public int getColor() {
			return Color.RED;
		}
		
		public Team getOppositeTeam() {
			return Team.CLIENT;
		}
	};

	public abstract int getColor();
	public abstract Team getOppositeTeam();
}
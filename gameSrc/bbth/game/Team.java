package bbth.game;

import bbth.engine.util.ColorUtils;
import android.graphics.Color;

public enum Team {
	CLIENT {
		public int getColor() {
			return Color.BLUE;
		}
		
		public int getRandomShade() {
			return ColorUtils.randomHSV(180, 240, 0.75f, 1, 0.5f, 1);
		}
		
		public Team getOppositeTeam() {
			return Team.SERVER;
		}
	},
	SERVER {
		public int getColor() {
			return Color.RED;
		}
		
		public int getRandomShade() {
			return ColorUtils.randomHSV(0, 60, 0.75f, 1, 0.5f, 1));
		}

		public Team getOppositeTeam() {
			return Team.CLIENT;
		}
	};

	public abstract int getColor();
	public abstract Team getOppositeTeam();
	public abstract int getRandomShade();
}
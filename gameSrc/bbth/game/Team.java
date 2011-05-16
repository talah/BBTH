package bbth.game;

import android.graphics.Color;
import bbth.engine.util.ColorUtils;

public enum Team {
	CLIENT(Color.argb(255, 123, 160, 255), Color.argb(127, 123, 160, 255)) {
		@Override
		public int getUnitColor() {
			return Color.BLUE;
		}

		@Override
		public int getRandomShade() {
			return ColorUtils.randomHSV(180, 240, 0.75f, 1, 0.5f, 1);
		}

		@Override
		public Team getOppositeTeam() {
			return Team.SERVER;
		}
		
		@Override
		public int getBaseColor() {
			return Color.rgb(0, 0, 127);
		}
		
		@Override
		public int getWavefrontColor() {
			return Color.rgb(0, 0, 63);
		}

		@Override
		public String getColorName() {
			return "blue";
		}
	},
	SERVER(Color.argb(255, 255, 80, 71), Color.argb(127, 255, 80, 71)) {
		@Override
		public int getUnitColor() {
			return Color.RED;
		}

		@Override
		public int getRandomShade() {
			return ColorUtils.randomHSV(0, 60, 0.75f, 1, 0.5f, 1);
		}

		@Override
		public Team getOppositeTeam() {
			return Team.CLIENT;
		}
		
		@Override
		public int getBaseColor() {
			return Color.rgb(127, 0, 0);
		}
		
		@Override
		public int getWavefrontColor() {
			return Color.rgb(63, 0, 0);
		}

		@Override
		public String getColorName() {
			return "red";
		}
	};

	private int wallColor, tempWallColor;

	// Scheme: Server -- RED, Client -- BLUE
	public abstract int getUnitColor();
	public abstract int getRandomShade();
	public abstract Team getOppositeTeam();
	
	public abstract int getBaseColor();
	public abstract int getWavefrontColor();
	public abstract String getColorName();

	private Team(int wallColor, int tempWallColor) {
		this.wallColor = wallColor;
		this.tempWallColor = tempWallColor;
	}

	public int getWallColor() {
		return this.wallColor;
	}

	public int getTempWallColor() {
		return this.tempWallColor;
	}
	
	public boolean isEnemy(Team otherTeam) {
		return otherTeam != this;
	}
}
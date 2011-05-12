package bbth.game;

import bbth.engine.util.ColorUtils;
import android.graphics.Color;

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
	};

	private int wallColor, tempWallColor;

	// Scheme: Server -- RED, Client -- BLUE
	public abstract int getUnitColor();
	public abstract int getRandomShade();
	public abstract Team getOppositeTeam();

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
package bbth.game;

public enum Team {
	TEAM_0, TEAM_1;

	public Team getOppositeTeam() {
		if (this == TEAM_0) {
			return TEAM_1;
		} else {
			return TEAM_0;
		}
	}
}
package bbth.game;

public enum Team {
	CLIENT, SERVER;
	
	public Team getOppositeTeam() {
		if (this == CLIENT) {
			return SERVER;
		} else {
			return CLIENT;
		}
	}
	
	public boolean isEnemy(Team otherTeam) {
		return otherTeam != this;
	}
}
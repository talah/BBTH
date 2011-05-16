package bbth.engine.achievements;

/**
 * Represents a single achievement
 * @author Justin
 *
 */
public class Achievement {
	private final String _description;
	// Can only be unlocked by the Achievements class
	boolean _unlocked;

	public Achievement(String description) {
		_description = description;
		_unlocked = false;
	}
	
	public String getDescription() {
		return _description;
	}
	
	public boolean isUnlocked() {
		return _unlocked;
	}
}

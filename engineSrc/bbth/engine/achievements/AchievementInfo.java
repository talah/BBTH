package bbth.engine.achievements;

/**
 * Represents info about a single achievement
 * @author Justin
 *
 */
public class AchievementInfo {
	public final String description;
	public final int imageId;

	public AchievementInfo(String d, int image) {
		description = d;
		imageId = image;
	}
}

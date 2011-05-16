package bbth.engine.achievements;

import android.graphics.Bitmap;

/**
 * Represents info about a single achievement
 * @author Justin
 *
 */
public class AchievementInfo {
	public final String description;
	public final Bitmap image;

	public AchievementInfo(String d, Bitmap im) {
		description = d;
		image = im;
	}
}

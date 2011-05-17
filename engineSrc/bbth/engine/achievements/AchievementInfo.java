package bbth.engine.achievements;

import android.graphics.Bitmap;

/**
 * Represents info about a single achievement
 * @author Justin
 *
 */
public class AchievementInfo {
	public final int id;
	public final int activations;
	public final String name;
	public final String description;
	public final Bitmap image;

	public AchievementInfo(int id, String name, String description, Bitmap image) {
		this(id, 1, name, description, image);
	}
	
	public AchievementInfo(int id, int activations, String name, String description, Bitmap image) {
		this.id = id;
		this.activations = activations;
		this.name = name;
		this.description = description;
		this.image = image;
	}
}

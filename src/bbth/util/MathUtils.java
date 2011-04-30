package bbth.util;

import android.util.FloatMath;

public class MathUtils {
	private static final float PI = (float)Math.PI;
	private static final float TWO_PI = 2 * PI;
	
	public static float get_angle(float x, float y, float x2, float y2) {
		return (float)Math.atan2(y2 - y, x2 - x);
	}
	
	public static float normalize_angle(float a, float center) {
		return a - TWO_PI * FloatMath.floor((a + PI - center) / TWO_PI);
	}

	public static float get_dist(float x1, float y1, float x2, float y2) {
		return FloatMath.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}
	
	public static float clamp(float min, float max, float val) {
		return Math.max(min, Math.min(val, max));
	}
}

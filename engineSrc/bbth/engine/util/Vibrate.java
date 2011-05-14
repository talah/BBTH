package bbth.engine.util;

import bbth.engine.core.GameActivity;
import android.content.Context;
import android.os.Vibrator;

public class Vibrate {

	public static void vibrate(float seconds) {
		((Vibrator) GameActivity.instance.getSystemService(Context.VIBRATOR_SERVICE)).vibrate((long) (seconds * 1000));
	}
}

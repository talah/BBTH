package bbth.engine.sound;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Represents either a tap or a hold.
 * All times are in terms of milliseconds from the start of a song.
 * Appears to be a simple struct outside this package
 * @author jardini
 *
 */
public class Beat {
	public final static float RADIUS = 8.f;
	
	public enum BeatType {
		TAP, HOLD, REST
	}
	
	final BeatType type;
	final int duration;
	
	// start time internally used by this and BeatTracker only
	int _startTime;
	private boolean _tapped;

	private Beat(BeatType beatType, int durationMillis) {
		type = beatType;
		duration = durationMillis;
		_startTime = -1;
		_tapped = false;
	}
	
	public static Beat tap(int duration) {
		return new Beat(BeatType.TAP, duration);
	}
	
	public static Beat hold(int duration) {
		return new Beat(BeatType.HOLD, duration);
	}
	
	public static Beat rest(int duration) {
		return new Beat(BeatType.REST, duration);
	}
	
	// returns true if this noted was tapped, false if the note was missed
	boolean onTouchDown(int songTime) {
		if (type == BeatType.REST) return false;
		
		boolean changed = false;
		if (Math.abs(songTime - _startTime) < BeatTracker.TOLERANCE) {
			_tapped = true;
			changed = true;
		}
		
		return changed;
	}
	
	int getEndTime() {
		if (type == BeatType.HOLD) {
			return _startTime + duration;
		}
		return _startTime;
	}
	
	// draw the note given a location for taps
	void draw(int songTime, float x, float yMiddle, Canvas canvas, Paint paint) {
		if (type == BeatType.REST) return;
		
		int offset = songTime - _startTime;
		if (_tapped) {
			paint.setColor(Color.GREEN);
		} else if (offset > BeatTracker.TOLERANCE) {
			paint.setColor(Color.RED);
		} else {
			paint.setColor(Color.BLUE);
		}
		
		if (type == BeatType.TAP) {
			canvas.drawCircle(x, yMiddle + offset / 10, RADIUS, paint);
		} else {
			canvas.drawLine(x, yMiddle + offset / 10, x, yMiddle + (offset - duration) / 10, paint);
		}
	}
}

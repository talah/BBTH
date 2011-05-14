package bbth.engine.sound;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Represents either a tap or a hold. All times are in terms of milliseconds
 * from the start of a song. Appears to be a simple struct outside this package
 * 
 * @author jardini
 * 
 */
public class Beat {
	public final static float RADIUS = 8.f;
	private final static int OFFSET_DENOM = 12;
	private static final int TAPPED_COLOR = Color.rgb(0, 150, 0);
	private static final int INCOMING_COLOR = Color.GRAY;
	private static final int MISSED_COLOR = Color.DKGRAY;

	public enum BeatType {
		TAP,
		HOLD,
		REST
	}

	final BeatType type;
	final int duration;

	// start time internally used by this and BeatTracker only
	int _startTime;
	private boolean _tapped;

	// package private
	Beat(BeatType beatType, int durationMillis) {
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
		if (_tapped || type == BeatType.REST)
			return false;

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
		if (type == BeatType.REST)
			return;

		int offset = songTime - _startTime;
		if (_tapped) {
			paint.setColor(TAPPED_COLOR);
		} else if (offset > BeatTracker.TOLERANCE) {
			paint.setColor(MISSED_COLOR);
		} else {
			paint.setColor(INCOMING_COLOR);
		}

		if (type == BeatType.TAP) {
			canvas.drawCircle(x, yMiddle + offset / OFFSET_DENOM, RADIUS, paint);
		} else {
			paint.setStrokeWidth(RADIUS * 2);
			canvas.drawLine(x, yMiddle + offset / OFFSET_DENOM, x, yMiddle + (offset - duration) / OFFSET_DENOM, paint);
		}
	}
}

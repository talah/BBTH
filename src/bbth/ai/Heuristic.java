package bbth.ai;

import android.graphics.PointF;

public interface Heuristic {
	public int estimateHScore(PointF start, PointF goal);
}

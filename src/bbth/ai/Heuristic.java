package bbth.ai;

import android.graphics.PointF;

public interface Heuristic {
	public int estimate_h_score(PointF start, PointF goal);
}

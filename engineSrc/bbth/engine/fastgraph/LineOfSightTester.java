package bbth.engine.fastgraph;

import android.graphics.PointF;

public abstract class LineOfSightTester {

	/**
	 * Does the line segment not intersect any wall line segments?
	 */
	public abstract Wall isLineOfSightClear(PointF start, PointF end);

	public abstract Wall isLineOfSightClear(float startx, float starty, float endx, float endy);

}

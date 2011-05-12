package bbth.engine.fastgraph;

import android.graphics.PointF;

public abstract class LineOfSightTester {

	public abstract void setBounds(float min_x, float min_y, float max_x, float max_y);

	/**
	 * Does the line segment not intersect any wall line segments?
	 */
	public abstract boolean isLineOfSightClear(PointF start, PointF end);

	public abstract boolean isLineOfSightClear(float startx, float starty, float endx, float endy);

}

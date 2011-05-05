package bbth.engine.fastgraph;

import java.util.ArrayList;

import android.graphics.PointF;

public abstract class LineOfSightTester {

	public ArrayList<Wall> walls = new ArrayList<Wall>();

	/**
	 * Call this to update the acceleration data structure.
	 */
	public abstract void updateWalls();

	/**
	 * Does the line segment not intersect any wall line segments?
	 */
	public abstract boolean isLineOfSightClear(PointF start, PointF end);
}

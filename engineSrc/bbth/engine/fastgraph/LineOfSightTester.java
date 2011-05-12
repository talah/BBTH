package bbth.engine.fastgraph;

import java.util.ArrayList;

import android.graphics.PointF;

public abstract class LineOfSightTester {

	public ArrayList<Wall> walls = new ArrayList<Wall>();

	/**
	 * Call this to update the acceleration data structure.
	 */
	public abstract void updateWalls();
	
	public abstract void setBounds(float min_x, float min_y, float max_x, float max_y);

	/**
	 * Does the line segment not intersect any wall line segments?
	 */
	public abstract Wall isLineOfSightClear(PointF start, PointF end);
	
	public abstract Wall isLineOfSightClear(float startx, float starty, float endx, float endy);

}

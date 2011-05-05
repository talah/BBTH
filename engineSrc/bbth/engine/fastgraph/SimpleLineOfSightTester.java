package bbth.engine.fastgraph;

import android.graphics.PointF;

public class SimpleLineOfSightTester extends LineOfSightTester {

	public float radius;
	private float m_min_x;
	private float m_min_y;
	private float m_max_x;
	private float m_max_y;
	
	public SimpleLineOfSightTester(float f) {
		radius = f;
		m_min_x = Float.MIN_VALUE;
		m_max_x = Float.MAX_VALUE;
		m_min_y = Float.MIN_VALUE;
		m_max_y = Float.MAX_VALUE;
	}

	@Override
	public void updateWalls() {
	}

//	@Override
//	public boolean isLineOfSightClear(PointF start, PointF end) {
//		float lineX = end.x - start.x;
//		float lineY = end.y - start.y;
//
//		// Loop over walls
//		for (int i = 0, wallCount = walls.size(); i < wallCount; i++) {
//			Wall wall = walls.get(i);
//			float wallX = wall.b.x - wall.a.x;
//			float wallY = wall.b.y - wall.a.y;
//
//			// Intersect each line segment with the other infinite line
//			float divide = wallX * lineY - wallY * lineX;
//			float t_line_on_wall = ((start.x - wall.a.x) * wallY - (start.y - wall.a.y) * wallX) / divide;
//			float t_wall_on_line = ((wall.a.x - start.x) * lineY - (wall.a.y - start.y) * lineX) / -divide;
//
//			// Line of sight isn't clear if they overlap
//			float padding = radius * 0.95f / wall.length;
//			if (t_line_on_wall >= 0 && t_line_on_wall <= 1 && t_wall_on_line >= 0 - padding && t_wall_on_line <= 1 + padding) {
//				return false;
//			}
//		}
//
//		return true;
//	}
	
	public boolean isLineOfSightClear(PointF start, PointF end) {
		return isLineOfSightClear(start.x, start.y, end.x, end.y);
	}

	public boolean isLineOfSightClear(float startx, float starty, float endx, float endy) {
		if (endx < m_min_x || endx > m_max_x || endy < m_min_y || endy > m_max_y) {
			return false;
		}
		
		float lineX = endx - startx;
		float lineY = endy - starty;

		// Loop over walls
		for (int i = 0, wallCount = walls.size(); i < wallCount; i++) {
			Wall wall = walls.get(i);
			float wallX = wall.b.x - wall.a.x;
			float wallY = wall.b.y - wall.a.y;

			// Intersect each line segment with the other infinite line
			float divide = wallX * lineY - wallY * lineX;
			float t_line_on_wall = ((startx - wall.a.x) * wallY - (starty - wall.a.y) * wallX) / divide;
			float t_wall_on_line = ((wall.a.x - startx) * lineY - (wall.a.y - starty) * lineX) / -divide;

			// Line of sight isn't clear if they overlap
			float padding = radius * 0.95f / wall.length;
			if (t_line_on_wall >= 0 && t_line_on_wall <= 1 && t_wall_on_line >= 0 - padding && t_wall_on_line <= 1 + padding) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void setBounds(float min_x, float min_y, float max_x, float max_y) {
		m_min_x = min_x;
		m_min_y = min_y;
		m_max_x = max_x;
		m_max_y = max_y;
	}
}

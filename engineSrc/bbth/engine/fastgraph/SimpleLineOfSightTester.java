package bbth.engine.fastgraph;

import android.graphics.PointF;

public class SimpleLineOfSightTester extends LineOfSightTester {

	@Override
	public void updateWalls() {
	}

	@Override
	public boolean isLineOfSightClear(PointF start, PointF end) {
		float lineX = end.x - start.x;
		float lineY = end.y - start.y;

		// Loop over walls
		for (int i = 0, wallCount = walls.size(); i < wallCount; i++) {
			Wall wall = walls.get(i);
			float wallX = wall.b.x - wall.a.x;
			float wallY = wall.b.y - wall.a.y;

			// Intersect each line segment with the other infinite line
			float divide = wallX * lineY - wallY * lineX;
			float t_line_on_wall = ((start.x - wall.a.x) * wallY - (start.y - wall.a.y) * wallX) / divide;
			float t_wall_on_line = ((wall.a.x - start.x) * lineY - (wall.a.y - start.y) * lineX) / -divide;

			// Line of sight isn't clear if they overlap
			if (t_line_on_wall >= 0 && t_line_on_wall <= 1 && t_wall_on_line >= 0 && t_wall_on_line <= 1) {
				return false;
			}
		}

		return true;
	}
}

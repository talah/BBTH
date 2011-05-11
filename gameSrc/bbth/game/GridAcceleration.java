package bbth.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import bbth.game.GridAcceleration.HasPosition;

public class GridAcceleration<T extends HasPosition> {

	public static interface HasPosition {
		public float getX();

		public float getY();
	}

	private static class Cell<T> {
		public ArrayList<T> entities = new ArrayList<T>();
	}

	private int cellsInX;
	private int cellsInY;
	private float cellWidth;
	private float cellHeight;
	private ArrayList<Cell<T>> cells = new ArrayList<Cell<T>>();

	public GridAcceleration(float width, float height, float spacing) {
		cellsInX = (int) Math.ceil(width / spacing);
		cellsInY = (int) Math.ceil(height / spacing);
		cellWidth = width / cellsInX;
		cellHeight = height / cellsInY;
		for (int i = 0, n = cellsInX * cellsInY; i < n; i++) {
			cells.add(new Cell<T>());
		}
	}

	/**
	 * Remove all entities.
	 */
	public void clear() {
		for (int i = 0, n = cellsInX * cellsInY; i < n; i++) {
			cells.get(i).entities.clear();
		}
	}

	/**
	 * Adds each entity to exactly one cell. Call this after calling clear()
	 * when the positions of the entities have changed.
	 */
	public void insertUnits(List<T> entities) {
		for (int i = 0, n = entities.size(); i < n; i++) {
			T entity = entities.get(i);
			int x = Math.max(0, Math.min(cellsInX - 1, (int) (entity.getX() / cellWidth)));
			int y = Math.max(0, Math.min(cellsInY - 1, (int) (entity.getY() / cellHeight)));
			cells.get(x + y * cellsInX).entities.add(entity);
		}
	}

	/**
	 * Put all enemies whose centers potentially lie in the given axis-aligned
	 * bounding box inside the given set (they might be slightly outside,
	 * however). This will remove all existing entities in the set first.
	 */
	public void getEntitiesInAABB(float xmin_, float ymin_, float xmax_, float ymax_, HashSet<T> entities) {
		// Convert from game space to grid space
		int xmin = Math.max(0, (int) (xmin_ / cellWidth));
		int ymin = Math.max(0, (int) (ymin_ / cellHeight));
		int xmax = Math.min(cellsInX - 1, (int) (xmax_ / cellWidth));
		int ymax = Math.min(cellsInY - 1, (int) (ymax_ / cellHeight));

		// Copy entities from all cells in the AABB into moveables
		entities.clear();
		for (int y = ymin; y <= ymax; y++) {
			for (int x = xmin; x <= xmax; x++) {
				// Manually iterate over the collection for performance (instead
				// of using addAll)
				ArrayList<T> toAdd = cells.get(x + y * cellsInX).entities;
				for (int j = 0; j < toAdd.size(); j++) {
					entities.add(toAdd.get(j));
				}
			}
		}
	}
}

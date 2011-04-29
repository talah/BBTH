package bbth.ai;

import android.graphics.PointF;

public class MapGrid extends ConnectedGraph {
	private PointF[][] m_points;
	int m_width;
	int m_height;
	int m_x_size;
	int m_y_size;
	
	public MapGrid(int width, int height, int xboxes, int yboxes) {
		m_width = width;
		m_height = height;
		m_x_size = xboxes;
		m_y_size = yboxes;
		
		clear();
	}
	
	private void clear() {
		m_points = new PointF[m_y_size][m_x_size];
		
		// Initialize all the transitions.
		for (int r = 0; r < m_y_size; r++) {
			for (int c = 0; c < m_x_size; c++) {
				if (m_points[r][c] == null) {
					m_points[r][c] = new PointF(r, c);
				}
				
				PointF currpoint = m_points[r][c];
				
				for (int r2off = -1; r2off <= 1; r2off++) {
					for (int c2off = -1; c2off <= 1; c2off++) {
						if (r2off == 0 && c2off == 0 || r2off != 0 && c2off != 0) {
							continue;
						}
						
						int r2 = r + r2off;
						int c2 = c + c2off;
						
						if (r2 < 0 || r2 >= m_y_size) {
							continue;
						}
						
						if (c2 < 0 || c2 >= m_x_size) {
							continue;
						}
						
						if (m_points[r2][c2] == null) {
							m_points[r2][c2] = new PointF(r2, c2);
						}
						
						add_connection(currpoint, m_points[r2][c2]);
					}
				}
			}
		}
	}
	
	public void mark_passable(float x, float y) {
		mark_passable(get_y_bin(y), get_x_bin(x));
	}
	
	public void mark_impassable(float x, float y) {
		mark_impassable(get_y_bin(y), get_x_bin(x));
	}
	
	public void mark_passable(int r, int c) {
		PointF currpoint = m_points[r][c];
		
		for (int r2off = -1; r2off <= 1; r2off++) {
			for (int c2off = -1; c2off <= 1; c2off++) {
				if (r2off == 0 && c2off == 0 || r2off != 0 && c2off != 0) {
					continue;
				}
				
				int r2 = r + r2off;
				int c2 = c + c2off;
				
				if (r2 < 0 || r2 >= m_y_size) {
					continue;
				}
				
				if (c2 < 0 || c2 >= m_x_size) {
					continue;
				}
				
				add_connection(currpoint, m_points[r2][c2]);
			}
		}
	}
	
	public void mark_impassable(int r, int c) {
		PointF currpoint = m_points[r][c];
		
		for (int r2off = -1; r2off <= 1; r2off++) {
			for (int c2off = -1; c2off <= 1; c2off++) {
				if (r2off == 0 && c2off == 0 || r2off != 0 && c2off != 0) {
					continue;
				}
				
				int r2 = r + r2off;
				int c2 = c + c2off;
				
				if (r2 < 0 || r2 >= m_y_size) {
					continue;
				}
				
				if (c2 < 0 || c2 >= m_x_size) {
					continue;
				}
				
				remove_connection(currpoint, m_points[r2][c2]);
			}
		}
	}
	
	public PointF get_bin(float x, float y) {
		return m_points[(int)((y/m_height) * m_y_size)][(int)((x/m_width) * m_x_size)];
	}
	
	public int get_x_bin(float x) {
		return (int)((x/m_width) * m_x_size);
	}
	
	public int get_y_bin(float y) {
		return (int)((y/m_height) * m_y_size);
	}
	
	public float get_x_pos(int x_bin) {
		return ((float)x_bin)/m_x_size * m_width;
	}
	
	public float get_y_pos(int y_bin) {
		return ((float)y_bin)/m_y_size * m_height;
	}
}

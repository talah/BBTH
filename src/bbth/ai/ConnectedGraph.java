package bbth.ai;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.PointF;

public class ConnectedGraph {
	public HashMap<PointF, ArrayList<PointF>> m_connections;

	public ConnectedGraph() {
		m_connections = new HashMap<PointF, ArrayList<PointF>>();
	}
	
	public void add_connection(PointF p, PointF p2) {
		if (!m_connections.containsKey(p)) {
			m_connections.put(p, new ArrayList<PointF>());
		}
		if (!m_connections.containsKey(p2)) {
			m_connections.put(p2, new ArrayList<PointF>());
		}
		ArrayList<PointF> list = m_connections.get(p);
		list.add(p2);
	}
	
	public void add_connection(float x, float y, float x2, float y2) {
		PointF key = get_point_at_coords(x, y);
		PointF value = get_point_at_coords(x2, y2);

		if (key == null) {
			key = new PointF(x, y);
			m_connections.put(key, new ArrayList<PointF>());
		}
		
		ArrayList<PointF> list = m_connections.get(key);
		
		if (value == null) {
			value = new PointF(x2, y2);
		}
		list.add(value);
		
		if (!m_connections.containsKey(value)) {
			m_connections.put(value, new ArrayList<PointF>());
		}
	}
	
	public HashMap<PointF, ArrayList<PointF>> get_graph() {
		return m_connections;
	}
	
	public ArrayList<PointF> get_neighbors(PointF start) {
		return m_connections.get(start);
	}
	
	public ArrayList<PointF> get_neighbors(float startx, float starty) {
		PointF point = get_point_at_coords(startx, starty);
		if (point == null) {
			return null;
		}
		return m_connections.get(point);
	}
	
	public boolean contains(PointF p) {
		return m_connections.containsKey(p);
	}
	
	public PointF get_point_at_coords(float x, float y) {
		return get_point_at_coords(x, y, 0.05f, 0.05f);
	}
	
	public PointF get_point_at_coords(float x, float y, float xtolerance, float ytolerance) {
		for (PointF p : m_connections.keySet()) {
			if (Math.abs(p.x - x) < xtolerance && Math.abs(p.y - y) < ytolerance) {
				return p;
			}
		}
		
		return null;
	}
}

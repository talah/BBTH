package bbth.engine.ai;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.PointF;

public class ConnectedGraph {
	public HashMap<PointF, ArrayList<PointF>> m_connections;

	public ConnectedGraph() {
		m_connections = new HashMap<PointF, ArrayList<PointF>>();
	}
	
	public void addConnection(PointF p, PointF p2) {
		if (!m_connections.containsKey(p)) {
			m_connections.put(p, new ArrayList<PointF>());
		}
		if (!m_connections.containsKey(p2)) {
			m_connections.put(p2, new ArrayList<PointF>());
		}
		ArrayList<PointF> list = m_connections.get(p);
		list.add(p2);
	}
	
	public void addConnection(float x, float y, float x2, float y2) {
		PointF key = getPointAtCoords(x, y);
		PointF value = getPointAtCoords(x2, y2);

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
	
	public void removeConnection(PointF p, PointF p2) {
		ArrayList<PointF> list = m_connections.get(p);
		list.remove(p2);
	}
	
	public void removeConnection(float x, float y, float x2, float y2) {
		PointF key = getPointAtCoords(x, y);
		PointF value = getPointAtCoords(x2, y2);

		if (key == null) {
			return;
		}
		
		ArrayList<PointF> list = m_connections.get(key);
		
		if (value == null) {
			return;
		}
		list.remove(value);
	}
	
	public HashMap<PointF, ArrayList<PointF>> getGraph() {
		return m_connections;
	}
	
	public ArrayList<PointF> getNeighbors(PointF start) {
		return m_connections.get(start);
	}
	
	public ArrayList<PointF> getNeighbors(float startx, float starty) {
		PointF point = getPointAtCoords(startx, starty);
		if (point == null) {
			return null;
		}
		return m_connections.get(point);
	}
	
	public boolean contains(PointF p) {
		return m_connections.containsKey(p);
	}
	
	public PointF getPointAtCoords(float x, float y) {
		return getPointAtCoords(x, y, 0.05f, 0.05f);
	}
	
	public PointF getPointAtCoords(float x, float y, float xtolerance, float ytolerance) {
		for (PointF p : m_connections.keySet()) {
			if (Math.abs(p.x - x) < xtolerance && Math.abs(p.y - y) < ytolerance) {
				return p;
			}
		}
		
		return null;
	}

	public PointF getClosestNode(float x, float y) {
		float bestdist = 0;
		PointF closest = null;
		for (PointF p : m_connections.keySet()) {
			float dist = (p.x - x) * (p.x - x) + (p.y - y) * (p.y - y);
			if (closest == null || dist < bestdist) {
				closest = p;
				bestdist = dist;
			}
		}
		return closest;
	}
}

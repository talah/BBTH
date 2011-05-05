package org.processing.wiki.triangulate;

import android.graphics.PointF;

/**
 * A tuple that compares equal if it contains the same two points disregarding
 * their order.
 */
public class Edge {

	public PointF p1, p2;

	public Edge() {
		p1 = null;
		p2 = null;
	}

	public Edge(PointF p1, PointF p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	public int hashCode() {
		return p1.hashCode() ^ p2.hashCode();
	}

	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (!(other instanceof Edge))
			return false;
		Edge e = (Edge) other;
		return (p1.equals(e.p1) && p2.equals(e.p2)) || (p1.equals(e.p2) && p2.equals(e.p1));
	}
}

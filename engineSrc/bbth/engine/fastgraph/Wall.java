package bbth.engine.fastgraph;

import java.util.ArrayList;

import bbth.engine.util.MathUtils;

import android.graphics.PointF;

public class Wall {

	public PointF a;
	public PointF b;
	public float length;
	public PointF norm;

	public Wall(PointF a, PointF b) {
		norm = new PointF();
		this.a = a;
		this.b = b;
		updateLength();
	}

	public Wall(float ax, float ay, float bx, float by) {
		norm = new PointF();
		a = new PointF(ax, ay);
		b = new PointF(bx, by);
		updateLength();
	}

	public void updateLength() {
		length = MathUtils.getDist(a.x, a.y, b.x, b.y);
		float dx = b.x - a.x;
		float dy = b.y - a.y;
		norm.x = -dy / length;
		norm.y = dx / length;
	}

	public void addPoints(ArrayList<PointF> points, float radius) {
		float dx = (b.x - a.x) * radius / length;
		float dy = (b.y - a.y) * radius / length;
		points.add(new PointF(a.x - dx - dy, a.y - dy + dx));
		points.add(new PointF(a.x - dx + dy, a.y - dy - dx));
		points.add(new PointF(b.x + dx - dy, b.y + dy + dx));
		points.add(new PointF(b.x + dx + dy, b.y + dy - dx));
	}

	public float getMinX() {
		return Math.min(a.x, b.x);
	}

	public float getMinY() {
		return Math.min(a.y, b.y);
	}

	public float getMaxX() {
		return Math.max(a.x, b.x);
	}

	public float getMaxY() {
		return Math.max(a.y, b.y);
	}
}

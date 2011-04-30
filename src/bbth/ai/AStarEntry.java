package bbth.ai;

import android.graphics.PointF;

public class AStarEntry implements Comparable<AStarEntry> {
	PointF m_point;
	int m_f_score;
	int m_g_score;
	int m_h_score;
	
	public AStarEntry(PointF p) {
		m_point = p;
	}

	//@Override
	public int compareTo(AStarEntry other) {
		if (m_f_score == other.m_f_score) {
			return 0;
		}
		
		return m_f_score < other.m_f_score ? -1 : 1;
	}
}

package bbth.net.simulation;

import java.util.ArrayList;

public class LockStep implements Comparable<LockStep> {

	public int coarseTime;
	public ArrayList<Event> events = new ArrayList<Event>();

	@Override
	public int compareTo(LockStep other) {
		return coarseTime - other.coarseTime;
	}
}

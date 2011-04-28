/*
 * @(#)Envelope.java Copyright 2010 Zachary Davis. All rights reserved.
 */

package bbth.util;

import static bbth.util.Envelope.OutOfBoundsHandler.*;

public class Envelope {
	public static final class Factory {
		private Factory() {}
		
		// TODO: createSimpleEnvelope(double startValue, double endValue, int length) etc.
	}
	
	public static enum OutOfBoundsHandler {
		THROW_EXCEPTION {
			@Override
			int translateTime(int time, int totalLength) {
				if (time >=0 && time <= totalLength)
					return time;
				throw new IllegalArgumentException("time was out of bounds");
			}
		}, RETURN_FIRST_OR_LAST {
			@Override
			int translateTime(int time, int totalLength) {
				if (time < 0)
					return 0;
				if (time > totalLength)
					return totalLength;
				return time;
			}
		}, WRAP {
			@Override
			int translateTime(int time, int totalLength) {
				return time % totalLength;
			}
		};
		
		abstract int translateTime(int time, int totalLength);
	}
	
	static abstract class Entry {
		public final int endTime;
		public final int length;
		
		public Entry(int endTime, int length) {
			this.endTime = endTime;
			this.length = length;
		}
		
		public final boolean coversTime(int time) {
			return (time < endTime && time > endTime - length) || time == endTime;
		}
		
		public abstract double getValueAtTime(int time);
	}
	
	static class FlatEntry extends Entry {
		double value;

		public FlatEntry(int endTime, int length, double value) {
			super(endTime, length);
			this.value = value;
		}

		@Override
		public double getValueAtTime(int time) {
			return value;
		}
	}
	
	static class LinearEntry extends Entry {
		double slope;
		double endValue;
		
		public LinearEntry(int endTime, int length, double startValue, double endValue) {
			super(endTime, length);
			this.slope = (endValue - startValue) / length;
			this.endValue = endValue;
		}
		
		@Override
		public double getValueAtTime(int time) {
			return endValue - slope*(endTime - time); // Linear interpolation using 3 operations. Slick, no?
		}
	}
	
	Bag<Entry> entrys = new Bag<Entry>();
	int length;
	OutOfBoundsHandler outOfBoundsHandler;
	
	protected Entry getEntryAtTime(int time) {
		// binary search
		int min = 0;
		int max = entrys.size() - 1;
		int mid;
		Entry midEntry;
		while (min <= max) {
			mid = min + (max - min) / 2;
			midEntry = entrys.get(mid);
			if (midEntry.coversTime(time))
				return midEntry;
			if (time > midEntry.endTime)
				min = mid + 1;
			else
				max = mid - 1;
		}
		throw new IllegalStateException("Envelope.Entry not found");
	}
	
	public Envelope(double startValue) {
		this(startValue, THROW_EXCEPTION);
	}
	
	public Envelope(double startValue, OutOfBoundsHandler outOfBoundsHandler) {
		this.outOfBoundsHandler = outOfBoundsHandler;
		entrys.add(new FlatEntry(0,0,startValue));
	}
	
	private void checkLengthOfTime(int lengthOfTime) {
		if (lengthOfTime < 1)
			throw new IllegalArgumentException("Length of time must be at least 1");
	}
	
	private double getEndValue() {
		return entrys.getLast().getValueAtTime(length);
	}
	
	public void addFlatSegment(int lengthOfTime) {
		addFlatSegment(lengthOfTime, getEndValue());
	}
	
	public void addFlatSegment(int lengthOfTime, double value) {
		checkLengthOfTime(lengthOfTime);
		length += lengthOfTime;
		entrys.add(new FlatEntry(length, lengthOfTime, value));
	}
	
	public void addLinearSegment(int lengthOfTime, double endValue) {
		addLinearSegment(lengthOfTime, endValue, getEndValue());
	}
	
	public void addLinearSegment(int lengthOfTime, double endValue, double startValue) {
		checkLengthOfTime(lengthOfTime);
		length += lengthOfTime;
		entrys.add(new LinearEntry(length, lengthOfTime, startValue, endValue));
	}
	
	public void scaleTimes(double factor) {
		throw new UnsupportedOperationException("Envelope.scaleTimes(): Not implemented yet");
	}
	
	public void scaleTimesToTotalLength(int totalLengthOfTime) {
		throw new UnsupportedOperationException("Envelope.scaleTimesToTotalLength(): Not implemented yet");
	}
	
	public int getTotalLength() {
		return length;
	}
	
	public double getValueAtTime(int time) {
		time = outOfBoundsHandler.translateTime(time, length);
		return getEntryAtTime(time).getValueAtTime(time);
	}
	
	public double getValueAtFraction(double frac) {
		return getValueAtTime((int)Math.rint(frac*length));
	}
	
}

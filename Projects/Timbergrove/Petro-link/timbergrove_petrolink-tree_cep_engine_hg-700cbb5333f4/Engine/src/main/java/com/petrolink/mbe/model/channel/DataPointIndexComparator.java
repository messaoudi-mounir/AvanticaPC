package com.petrolink.mbe.model.channel;

import java.util.Comparator;

/**
 * A comparator that only considers the index of a DataPoint and not its value.
 * @author langj
 *
 */
public final class DataPointIndexComparator implements Comparator<DataPoint> {
	private static DataPointIndexComparator instance;
	
	@Override
	public int compare(DataPoint o1, DataPoint o2) {
		return (int) DataPoint.numericSubtract(o1, o2);
	}
	
	/**
	 * Get a singleton instance of the comparator.
	 * @return A singleton instance
	 */
	public static DataPointIndexComparator getInstance() {
		DataPointIndexComparator i = instance;
		if (i == null)
			instance = i = new DataPointIndexComparator();
		return i;
	}
}

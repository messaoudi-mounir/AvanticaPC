package com.petrolink.mbe.model.channel;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A specialized ordered array for data points that allows quick insertion to the end and removal from the front.
 * @author langj
 *
 */
@SuppressWarnings("javadoc")
public final class DataPointSet extends AbstractSet<DataPoint> {
	private final double COMPRESS_THRESHOLD = 0.5;
	private final double GROW_MULTIPLIER = 2.0;
	private final int MINIMUM_GROW_SIZE = 4;
	private final double[] EMPTY_INDICES = new double[0];
	private final DataPoint[] EMPTY_ITEMS = new DataPoint[0];
	
	private double[] indices;
	private DataPoint[] items;
	private int head;
	private int tail;
	
	public DataPointSet() {
		indices = EMPTY_INDICES;
		items = EMPTY_ITEMS;
	}

	@Override
	public boolean isEmpty() {
		return head == tail;
	}

	@Override
	public int size() {
		return tail - head;
	}

	@Override
	public Iterator<DataPoint> iterator() {
		return new Itr();
	}
	
	@Override
	public boolean contains(Object o) {
		if (o instanceof DataPoint)
			return contains((DataPoint) o);
		return false;
	}
	
	public boolean contains(DataPoint dp) {
		int idx = binarySearch(Objects.requireNonNull(dp, "dp").getIndex());
		return idx >= 0 && dp.equals(getUnsafe(idx));
	}

	public DataPoint first() {
		if (isEmpty())
			throw new NoSuchElementException();
		return items[head];
	}
	
	public DataPoint last() {
		if (isEmpty())
			throw new NoSuchElementException();
		return items[tail - 1];
	}
	
	public DataPoint pollFirst() {
		return isEmpty() ? null : remove(0);
	}
	
	public DataPoint pollLast() {
		return isEmpty() ? null : remove(size() - 1);
	}
	
	public DataPoint get(int index) {
		if (index < 0 || index >= tail - head)
			throw new IndexOutOfBoundsException();
		return items[head + index];
	}
	
	public DataPoint getUnsafe(int index) {
		return items[head + index];
	}
	
	@Override
	public boolean add(DataPoint value) {
		double ni = Objects.requireNonNull(value, "value").getIndexNumber();
		if (isEmpty() || ni > indices[tail - 1]) {
			add(tail, value);
			return true;
		}
		int aindex = binarySearchCore(ni);
		if (aindex >= 0) {
			items[aindex] = value;
			return false;
		}
		aindex = -aindex - 1;
		assert aindex >= head && aindex <= tail;
		add(aindex, value);
		return true;
	}
	
	public int binarySearch(Object index) {
		return binarySearchNumeric(DataPoint.numericValue(index));
	}
	
	public int binarySearchNumeric(double index) {
		int aindex = binarySearchCore(index);
		// Must convert absolute index to relative index
		return aindex >= 0 ? aindex - head : aindex + head;
	}
	
	/**
	 * Returns the data point with the greatest index less than or equal to the given index, or null if there is no such data point.
	 * @param index
	 * @return
	 */
	public DataPoint floor(Object index) {
		if (isEmpty())
			return null;
		int idx = binarySearch(index);
		if (idx < 0) {
			idx = (-idx) - 2;
			return idx > -1 ? getUnsafe(idx) : null;
		} else {
			return getUnsafe(idx);
		}
	}

	@Override
	public void clear() {
		Arrays.fill(items, head, tail, null);
		Arrays.fill(indices, head, tail, 0);
		head = tail = 0;
	}
	
	public int clearHead(Object toIndex, boolean inclusive) {
		if (isEmpty())
			return 0;
		int idx = binarySearch(toIndex);
		if (idx < 0)
			idx = -idx - 1;
		else if (inclusive)
			idx++;
		if (idx == 0)
			return 0;
		int newHead = head + idx;
		Arrays.fill(items, head, newHead, null);
		Arrays.fill(indices, head, newHead, 0);
		head = newHead;
		tryCompress();
		return idx;
	}
	
	public void addAllTo(Collection<DataPoint> collection) {
		int tail = this.tail;
		DataPoint[] items = this.items;
		for (int i = head; i < tail; i++)
			collection.add(items[i]);
	}
	
	@Override
	public boolean remove(Object o) {
		if (o instanceof DataPoint)
			return remove((DataPoint) o);
		return false;
	}
	
	public boolean remove(DataPoint dp) {
		int idx = binarySearch(Objects.requireNonNull(dp, "dp").getIndex());
		if (idx < 0 || !dp.equals(getUnsafe(idx)))
			return false;
		remove(idx);
		return true;
	}
	
	public DataPoint remove(int index) {
		int aindex = head + index;
		if (aindex < head || aindex >= tail)
			throw new IndexOutOfBoundsException();
		DataPoint dp = items[aindex];
		if (aindex == head) {
			// Special behavior for removing the head, no movement of items
			items[aindex] = null;
			indices[aindex] = 0;
			head++;
		}
		else if (aindex == tail - 1) {
			// Special behavior for removing the tail
			items[aindex] = null;
			indices[aindex] = 0;
			tail--;
		}
		else {
			// All other cases, need to move items
			int rightCount = tail - aindex;
			if (rightCount > 0) {
				System.arraycopy(items, aindex + 1, items, aindex, rightCount);
				System.arraycopy(indices, aindex + 1, indices, aindex, rightCount);
			}
			items[tail - 1] = null;
			indices[tail - 1] = 0;
			tail--;
		}
		tryCompress();
		return dp;
	}
    
    /**
     * Compress the array if the collection size is less than a threshold
     */
    private int tryCompress() {
		if (size() < (int) (items.length * COMPRESS_THRESHOLD))
			return compress();
		return 0;
    }
	
	/**
	 * Compress the array by moving items in the array so that the head is at index 0.
	 */
	private int compress() {
		if (isEmpty() || head == 0)
			return 0;
		int diff = head;
		System.arraycopy(items, head, items, 0, diff);
		Arrays.fill(items, tail - diff, tail, null);
		System.arraycopy(indices, head, indices, 0, diff);
		Arrays.fill(indices, tail - diff, tail, 0);
		tail -= diff;
		head = 0;
		return diff;
	}
	
	/**
	 * Grow the free space of the array either by compressing to free up space or by increasing the size of the array.
	 */
	private void grow() {
		if (tryCompress() < 1) {
			int newSize = Math.max(MINIMUM_GROW_SIZE, (int) (items.length * GROW_MULTIPLIER));
			items = Arrays.copyOf(items, newSize);
			indices = Arrays.copyOf(indices, newSize);
		}
		assert tail < items.length;
	}
	
	private void add(int aindex, DataPoint value) {
		if (tail == items.length)
			grow();
		if (aindex != tail) {
			int rightCount = tail - aindex;
			if (rightCount > 0) {
				System.arraycopy(items, aindex, items, aindex + 1, rightCount);
				System.arraycopy(indices, aindex, indices, aindex + 1, rightCount);
			}
		}
		items[aindex] = value;
		indices[aindex] = value.getIndexNumber();
		tail++;
	}
	
    private int binarySearchCore(double key) {
    	double[] a = indices;
		int low = head;
		int high = tail - 1;
		while (low <= high) {
			int mid = (low + high) >>> 1;
			double mv = a[mid];
			
			if (mv < key)
				low = mid + 1;
			else if (mv > key)
				high = mid - 1;
			else
				return mid; // key found
		}
		return -(low + 1);  // key not found.
	}
	
	private class Itr implements Iterator<DataPoint> {
		int index;
		
		@Override
		public boolean hasNext() {
			return index < size();
		}

		@Override
		public DataPoint next() {
			try {
				DataPoint v = get(index);
				index++;
				return v;
			} catch (IndexOutOfBoundsException e) {
				throw new NoSuchElementException();
			}
		}
		
		@Override
		public void remove() {
			DataPointSet.this.remove(index - 1);
		}
	}
}

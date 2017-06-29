package com.petrolink.mbe.util;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A counter that groups counts into time periods. This class is thread-safe.
 * @author langj
 *
 */
public final class TimePeriodCounter {
	private final Object lockTarget = new Object();
	private final Duration duration;
	private final Period[] periods;
	
	/**
	 * Initialize the counter with a duration and a number of periods
	 * @param count The number of periods to use
	 * @param duration The duration of each period
	 */
	public TimePeriodCounter(int count, Duration duration) {
		this.duration = duration;
		this.periods = new Period[count];
		for (int i = 0; i < count; i++)
			this.periods[i] = new Period(duration, Instant.MIN, 0);
	}
	
	/**
	 * Gets the duration of each time period.
	 * @return the duration of each time period.
	 */
	public Duration getDuration() {
		return duration;
	}
	
	/**
	 * Gets the number of time periods that are recorded.
	 * @return the number of time periods that are recorded.
	 */
	public int getCount() {
		return periods.length;
	}
	
	/**
	 * Get the valid periods based on the current time.
	 * @return A list of periods
	 */
	public List<Period> getPeriods() {
		ArrayList<Period> p = new ArrayList<>();
		getPeriods(p);
		return p;
	}
	
	/**
	 * Get the valid periods based on the current time.
	 * @param collector A collection that will receive the periods.
	 */
	public void getPeriods(Collection<Period> collector) {
		Instant min = Instant.now().minus(duration.multipliedBy(periods.length));
		
		synchronized (lockTarget) {
			for (int i = 0; i < periods.length; i++) {
				Period c = periods[i];
				if (c.startTime.compareTo(min) >= 0 && c.count != 0)
					collector.add(periods[i].clone());
			}
		}
	}
	
	/**
	 * Increment the interval counter based on the current time.
	 */
	public void increment() {
		Instant now = Instant.now();
		
		synchronized (lockTarget) {
			// Check if now fits within the rang eof the first interval counter
			Period first = periods[0];
			if (now.compareTo(first.startTime) >= 0 && now.compareTo(first.endTime) < 0) {
				first.increment();
				return;
			}
			
			// Shift counters to the right
			for (int i = periods.length - 1; i > 0; i--) {
				periods[i].copyFrom(periods[i - 1]);
			}
			
			// Create new interval in front
			first.setStartTime(now);
			first.setCount(1);
		}
	}
	
	/**
	 * A counted period of time.
	 * @author langj
	 *
	 */
	public static final class Period {
		private final Duration duration;
		private Instant startTime;
		private Instant endTime;
		private int count;
		
		Period(Duration duration, Instant startTime, int count) {
			this.duration = duration;
			this.startTime = startTime;
			this.endTime = startTime.plus(duration);
			this.count = count;
		}
		
		/**
		 * Gets the inclusive start time of this period.
		 * @return A starting instant
		 */
		public Instant getStartTime() {
			return startTime;
		}
		
		/**
		 * Gets the current count in this period.
		 * @return A count number
		 */
		public int getCount() {
			return count;
		}
		
		/**
		 * Gets the duration of this period.
		 * @return A duration
		 */
		public Duration getDuration() {
			return duration;
		}
		
		/**
		 * Gets the exclusive end time of this period.
		 * @return An ending instant
		 */
		public Instant getEndTime() {
			return endTime;
		}
		
		void setStartTime(Instant value) {
			startTime = value;
			endTime = startTime.plus(duration);
		}
		
		void setCount(int value) {
			count = value;
		}
		
		void increment() {
			count++;
		}
		
		/**
		 * Clone the current Period.
		 */
		public Period clone() {
			return new Period(duration, startTime, count);
		}
		
		void copyFrom(Period other) {
			this.startTime = other.startTime;
			this.endTime = other.endTime;
			this.count = other.count;
		}
	}
}

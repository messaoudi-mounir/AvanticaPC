package com.petrolink.mbe.alertstatus.impl;

import java.time.Instant;

/**
 * The Snooze Record
 * @author paul
 *
 */
@SuppressWarnings("javadoc")
public class SnoozeRecord {
	private final String classId;
	private final String wellId;
	private final String snoozedBy;
	private final Instant snoozedAt;
	private final Instant unSnoozeAt;  // The Instant at which the Snooze action will be unsnooze automatically is not performed before
	private String unSnoozedBy;
	private Instant unSnoozedAt;  // The actual Instant on which the snooze was un-snoozed

	public SnoozeRecord(String classId, String wellId, String snoozedBy, Instant snoozedAt, Instant unSnoozeAt) {
		this.classId = classId;
		this.wellId = wellId;
		this.snoozedBy = snoozedBy;
		this.snoozedAt = snoozedAt;
		this.unSnoozeAt = unSnoozeAt;
	}

	/**
	 * @return the unSnoozedBy
	 */
	public String getUnSnoozedBy() {
		return unSnoozedBy;
	}

	/**
	 * @param unSnoozedBy the unSnoozedBy to set
	 */
	public void setUnSnoozedBy(String unSnoozedBy) {
		this.unSnoozedBy = unSnoozedBy;
	}

	/**
	 * @return the unSnoozedAt
	 */
	public Instant getUnSnoozedAt() {
		return unSnoozedAt;
	}

	/**
	 * @param unSnoozedAt the unSnoozedAt to set
	 */
	public void setUnSnoozedAt(Instant unSnoozedAt) {
		this.unSnoozedAt = unSnoozedAt;
	}

	/**
	 * @return the classId
	 */
	public String getClassId() {
		return classId;
	}

	/**
	 * @return the wellId
	 */
	public String getWellId() {
		return wellId;
	}

	/**
	 * @return the snoozedBy
	 */
	public String getSnoozedBy() {
		return snoozedBy;
	}

	/**
	 * @return the snoozedAt
	 */
	public Instant getSnoozedAt() {
		return snoozedAt;
	}

	/**
	 * @return the unSnoozeAt
	 */
	public Instant getUnSnoozeAt() {
		return unSnoozeAt;
	}
}
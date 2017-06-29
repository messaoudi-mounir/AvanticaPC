package com.petrolink.mbe.alertstatus.impl;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

import org.slf4j.Logger;

/**
 * Automatic alert dismissal when triggering index has been too old
 * @author aristo
 *
 */
public class AutoAlertDismissal {
	private Duration eventToProcessingDeltaTime;

	/**
	 * @return the eventToProcessingDeltaTime
	 */
	public final Duration getEventToProcessingDeltaTime() {
		return eventToProcessingDeltaTime;
	}

	/**
	 * @param eventToProcessingDeltaTime the eventToProcessingDeltaTime to set
	 */
	public final void setEventToProcessingDeltaTime(Duration eventToProcessingDeltaTime) {
		this.eventToProcessingDeltaTime = eventToProcessingDeltaTime;
	}
	
	/**
	 * 
	 * @param currentTimeInstant 
	 * @param alertImpl
	 * @return 
	 */
	public final boolean isShouldBeDismissed(Instant currentTimeInstant, AlertImpl alertImpl, Logger logger) {
		if (eventToProcessingDeltaTime != null && currentTimeInstant != null) {
			String dTimString = alertImpl.getLastIndex();
			try {
				Instant alertLastIndex = OffsetDateTime.parse(dTimString).toInstant();
				Instant thersholdTime = currentTimeInstant.minus(getEventToProcessingDeltaTime());
				if (alertLastIndex.isBefore(thersholdTime)) {
					return true;
				}
			} catch (DateTimeParseException dpe) {
				if (logger != null) {
					logger.error("Failure to compare eventToProcessingDeltaTime for LastIndex {}, dismissal check skipped", dTimString, dpe);
				}
			}
			
		}
		
		return false;
	}
}

package com.petrolink.mbe.alertstatus.impl;

import static org.junit.Assert.*;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestAutoAlertDismissal {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testIsShouldBeDismissed() throws DateTimeParseException {
		AutoAlertDismissal dismissConfig = new AutoAlertDismissal();
		dismissConfig.setEventToProcessingDeltaTime(Duration.ofMinutes(15));
		
		
		Instant currentInstant = Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse("2010-01-01T15:07:00+06:00"));
		
		AlertImpl alert = new AlertImpl();
		
		alert.setLastIndex("2010-01-01T12:07:00+01:00");
		Assert.assertFalse(dismissConfig.isShouldBeDismissed(currentInstant, alert, null));
		
		alert.setLastIndex("2010-01-01T15:07:00+06:00");
		Assert.assertFalse(dismissConfig.isShouldBeDismissed(currentInstant, alert, null));
		
		
		//Same time for before threshold
		alert.setLastIndex("2010-01-01T08:36:00Z");
		Assert.assertTrue(dismissConfig.isShouldBeDismissed(currentInstant, alert, null));
		
		alert.setLastIndex("2010-01-01T14:36:00+06:00");
		Assert.assertTrue(dismissConfig.isShouldBeDismissed(currentInstant, alert, null));
		
		
		//Same time for after threshold
		alert.setLastIndex("2010-01-01T14:53:00+06:00");
		Assert.assertFalse(dismissConfig.isShouldBeDismissed(currentInstant, alert, null));
		
		alert.setLastIndex("2010-01-01T15:53:00+07:00");
		Assert.assertFalse(dismissConfig.isShouldBeDismissed(currentInstant, alert, null));
		
		alert.setLastIndex("2010-01-01T8:53:00Z");
		Assert.assertFalse(dismissConfig.isShouldBeDismissed(currentInstant, alert, null));
		
		//Depth would fail
		alert.setLastIndex("8600.0");
		Assert.assertFalse(dismissConfig.isShouldBeDismissed(currentInstant, alert, null));
	}

}

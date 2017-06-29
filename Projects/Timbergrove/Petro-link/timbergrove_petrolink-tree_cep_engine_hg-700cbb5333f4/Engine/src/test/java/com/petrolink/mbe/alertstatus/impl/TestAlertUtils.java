package com.petrolink.mbe.alertstatus.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.junit.Before;
import org.junit.Test;

import com.smartnow.alertstatus.Alert;

public class TestAlertUtils {

	@Before
	public void setUp() throws Exception {
	}

	private AlertImpl createDummyAlert(String classId) {
		AlertImpl alert = new AlertImpl();
		alert.setClassId(classId);
		return alert;
	}
	
	@Test
	public void testGetMappedAlertsFromAlerts() {
		ArrayList<Alert> alerts = new ArrayList<>();
		
		alerts.add(createDummyAlert("aaa"));
		alerts.add(createDummyAlert("bbb"));
		alerts.add(createDummyAlert("bbb"));
		alerts.add(createDummyAlert("aaa"));
		alerts.add(createDummyAlert("aaa"));
		alerts.add(createDummyAlert("aaa"));
		alerts.add(createDummyAlert("ccc"));
		
		ArrayListValuedHashMap<String, Alert> mappedList = AlertUtils.getMappedAlertsFromAlerts(alerts);
		assertTrue("Key size incorrect",3 == mappedList.keySet().size());
		assertTrue(4 == mappedList.get("aaa").size());
		assertTrue(2 == mappedList.get("bbb").size());
		
	}

}

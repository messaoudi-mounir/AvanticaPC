/**
 * 
 */
package com.petrolink.mbe.journal;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.petrolink.mbe.alertstatus.impl.AlertImpl;
import com.petrolink.mbe.model.message.AlertSimpleMetadata;

/**
 * @author aristo
 *
 */
public class TestMultiAlertJournal {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testTargetAlerts() {
		//test individual Alert
		testSingleAlert(new MultiAlertJournal("Test"));
		testMultiAlertsSameClass(new MultiAlertJournal("Test"));
		testMultiAlertsDifferentClass(new MultiAlertJournal("Test"));
		
		//test changed alert
		MultiAlertJournal journal = new MultiAlertJournal("Test");
		testSingleAlert(journal);
		testMultiAlertsSameClass(journal);
		testMultiAlertsDifferentClass(journal);
		testMultiAlertsSameClass(journal);
		testSingleAlert(journal);
	}
	
	/**
	 * test Single Alert
	 * @param journal
	 */
	private void testSingleAlert(MultiAlertJournal journal) {
		//Test Single Alert
		ArrayList<AlertSimpleMetadata> singleAlerts = new ArrayList<AlertSimpleMetadata>();
		singleAlerts.add(new AlertSimpleMetadata("aaa","ccc"));
		journal.setAlertSimpleMetadata(singleAlerts);
		assertFalse(journal.isMultipleAlerts());
		assertEquals("ccc", journal.getAlertClassId());
		
		//Via object
		AlertImpl aimpl = new AlertImpl();
		aimpl.setUuid("bbb");
		aimpl.setClassId("ddd");
		journal.setAlert(aimpl);
		assertFalse(journal.isMultipleAlerts());
		assertEquals("ddd", journal.getAlertClassId());
		assertNotNull(journal.getAlert());
	}
	
	/**
	 * Test multi Alert same class
	 * @param journal
	 */
	private void testMultiAlertsSameClass(MultiAlertJournal journal) {
		ArrayList<AlertSimpleMetadata> multiAlertsSameClass = new ArrayList<AlertSimpleMetadata>();
		multiAlertsSameClass.add(new AlertSimpleMetadata("aaa","ccc"));
		multiAlertsSameClass.add(new AlertSimpleMetadata("bbb","ccc"));
		journal.setAlertSimpleMetadata(multiAlertsSameClass);
		assertTrue(journal.isMultipleAlerts());
		assertEquals("ccc", journal.getAlertClassId());
		assertNull(journal.getAlert());
	}
	
	/**
	 * Test multi Alert different class
	 * @param journal
	 */
	public void testMultiAlertsDifferentClass(MultiAlertJournal journal) {
		ArrayList<AlertSimpleMetadata> multiAlertsDiffClass = new ArrayList<AlertSimpleMetadata>();
		multiAlertsDiffClass.add(new AlertSimpleMetadata("aaa","cc1"));
		multiAlertsDiffClass.add(new AlertSimpleMetadata("bbb","cc2"));
		journal.setAlertSimpleMetadata(multiAlertsDiffClass);
		assertTrue(journal.isMultipleAlerts());
		assertNull(journal.getAlertClassId());
		assertNull(journal.getAlert());
	}

}

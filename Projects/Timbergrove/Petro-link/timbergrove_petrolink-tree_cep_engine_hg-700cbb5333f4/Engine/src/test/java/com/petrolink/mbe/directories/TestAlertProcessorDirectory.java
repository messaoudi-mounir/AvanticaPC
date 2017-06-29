/**
 * 
 */
package com.petrolink.mbe.directories;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.rulesflow.variables.AlertClassVariable;

/**
 * @author aristo
 *
 */
public class TestAlertProcessorDirectory {

	private AlertProcessorDirectory alertProcessorDirectory;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		alertProcessorDirectory = new AlertProcessorDirectory();
	}

	/**
	 * Make sure not throwing error when trying to reload rule.
	 * This also test basic flow registration
	 */
	@Test
	public void testRuleRegistration() {
		String id = UUID.randomUUID().toString();
		String alertClassId = UUID.randomUUID().toString();
		
		RuleFlow oldFlow = new RuleFlow(); 
		oldFlow.setUniqueId(id);
		AlertClassVariable alertClassDummy1 = new AlertClassVariable();
		oldFlow.getAlertClassDependencies().put(alertClassId, alertClassDummy1);
		
		//Basic Registration
		alertProcessorDirectory.registerRule(oldFlow);
		Collection<RuleFlow> flows1 = alertProcessorDirectory.getSubscribingFlows(alertClassId);
		assertNotNull("Subscribing flow collection MAY NOT be null", flows1);
		assertTrue("Must have rule", flows1.contains(oldFlow));
		
		//Somehow a new Rule Arrived
		RuleFlow newFlow = new RuleFlow();
		newFlow.setUniqueId(id);
		AlertClassVariable alertClassDummy2 = new AlertClassVariable();
		newFlow.getAlertClassDependencies().put(alertClassId, alertClassDummy2);
		
		//Old rule must be replaced
		alertProcessorDirectory.registerRule(newFlow);
		Collection<RuleFlow> flows2 = alertProcessorDirectory.getSubscribingFlows(alertClassId);
		assertNotNull("Subscribing flow collection MAY NOT be null", flows2);
		assertTrue("Must have replaced rule", flows2.contains(newFlow));
		assertFalse("Must not have old rule", flows2.contains(oldFlow));
		
	}

}

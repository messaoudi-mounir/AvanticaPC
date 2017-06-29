/**
 * 
 */
package com.petrolink.mbe.rulesflow.evaluationstrategies;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.petrolink.mbe.cache.CacheFactory;
import com.petrolink.mbe.cache.WellCache;
import com.petrolink.mbe.cache.impl.BufferedCacheImpl;
import com.petrolink.mbe.cache.impl.LKVCacheImpl;
import com.petrolink.mbe.engine.SimpleRuleFlowEngine;
import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.smartnow.engine.exceptions.EngineException;

/**
 * @author aristo
 *
 */
public class TestWindowIndexedAlignment {
	Path xmlPath;
	SAXBuilder builder;
	
	
	/**
	 * Method to test Init Singletons components
	 */
	@BeforeClass
	public static void InitSingletonsComponents() {
		//Initialization similar to root-app-context.xml
		LKVCacheImpl lkvCache = new LKVCacheImpl();
		CacheFactory.getInstance().setLkvCache(lkvCache);
		
		BufferedCacheImpl bufferedCache = new BufferedCacheImpl();
		CacheFactory.getInstance().setBufferedCache(bufferedCache);
		
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		xmlPath = Paths.get(System.getProperty("user.dir"),"src","test","resources","Flows");
		boolean testPathAvailable  = Files.exists(xmlPath);
		assertTrue("Path Must be Available "+xmlPath.toString(), testPathAvailable);
		
		builder = new SAXBuilder();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * An example on how to load rule and feed channeldata
	 * @throws Exception
	 */
	@Test
	public void testRuleLoading() throws Exception{
		SimpleRuleFlowEngine flow = loadRule("ExampleUnitTestFlow.xml");
		
		flow.updateChannel("ROP", new DataPoint(OffsetDateTime.now(), 10.0));
	}

	/**
	 * Load a rule from default test resource
	 * @param ruleFileName
	 * @return a SimpleRuleFlowEngine
	 * @throws EngineException
	 * @throws JDOMException
	 * @throws IOException
	 */
	public SimpleRuleFlowEngine loadRule(String ruleFileName) throws EngineException, JDOMException, IOException{
		File ruleFile = xmlPath.resolve(ruleFileName).toFile();
		assertTrue("Rule Must be available"+ruleFileName, ruleFile.exists());
		
		Document ruleDocument  = (Document) builder.build(ruleFile);
		Element flowsElement = ruleDocument.getRootElement();
		Element ruleElement = flowsElement.getChild(RuleFlow.XML_ELEMENT_NAME, flowsElement.getNamespace());
		RuleFlow  rule = new RuleFlow();
		
		//Create LKV Cache Just in case needed, which is needed by variable Factory
		rule.loadMetadata(ruleElement); //Peek metadata
		UUID wellID = rule.getWellId();
		WellCache wcache = CacheFactory.getInstance().getLKVCache().getOrCreateWell(wellID);
		wcache.setName(wellID.toString());
		
		//This disable some loading mechanism so it can work in isolated unit test
		//technically we can also abuse to load well Directory here but for isolation purpose , it is not done
		rule.setLoadAlertFlowsEnabled(false);
		rule.setLoadExecutionGroupsFromWellEnabled(false);
		rule.setLoadActionsEnabled(false);
		


		//Actual Load
		rule.load(ruleElement);
		
		SimpleRuleFlowEngine flow = new SimpleRuleFlowEngine();
		flow.rule = rule;
		flow.wellCache = wcache;
		return flow;
	}
	
	
}

package com.petrolink.mbe.services;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.xflow.Flow;
import com.smartnow.engine.xflow.FlowFactory;
import com.smartnow.rabbitmq.service.management.FlowsManagementService;

/**
 * Extension of FlowsManagementService that tries to load active flows from the PVHD MBE service instead of disk.
 * @author langj
 *
 */
public class MBEFlowsManagementService extends FlowsManagementService {
	private static final Logger logger = LoggerFactory.getLogger(MBEFlowsManagementService.class);
	
	private EngineService engineService;
	private PetroVaultMbeOrchestrationService mbeService;
	private boolean requestActiveFlows;
	private String location;
	
	@Override
	public void load(Element e) throws EngineException {
		super.load(e);
		engineService = ServiceAccessor.getEngineService();
		mbeService = ServiceAccessor.getPVMBEService();
		
		String requestActiveFlowsString = e.getChildText("RequestActiveFlows", e.getNamespace());
		requestActiveFlows = requestActiveFlowsString != null ? Boolean.parseBoolean(requestActiveFlowsString) : true;
		
		// Location is private and there is no accessor so must use reflection
		try {
			Field baseLocationField = FlowsManagementService.class.getDeclaredField("location");
			baseLocationField.setAccessible(true);
			location = (String) baseLocationField.get(this);
		} catch (ReflectiveOperationException | SecurityException ex) {
			throw new EngineException("Could not get location field from FlowsManagementService", ex);
		}
	}
	
	@Override
	protected void reloadActiveFlows() {
		if (!requestActiveFlows) {
			super.reloadActiveFlows();
			return;
		}
		
		if (mbeService == null) {
			logger.warn("no MBE orchestration service is available, loading flows from disk");
			super.reloadActiveFlows();
			return;
		}
			
		List<Element> activeFlows = null;
		try {
			activeFlows = mbeService.getDefaultApiClient().getStartedRuleFlows();
		} catch (Exception e) {
			logger.error("failed to load active flows from MBE service", e);
		}
		
		// activeFlows is an empty list if the MBE call succeeded, in which case we do not fallback to disk
		if (activeFlows == null) {
			logger.warn("no flows were retrieved from MBE service, loading flows from disk");
			super.reloadActiveFlows();
			return;
		}
		
		logger.info("Loading {} active flows", activeFlows.size());
		
		for (Element e : activeFlows) {
			String uuid = e.getAttributeValue("uuid");
			String name = e.getAttributeValue("name");
			logger.info("Loading flow {} with name \"{}\"", uuid, name);
			
			List<Flow> flows;
			try {
				flows = FlowFactory.getFlow(e);
			} catch (EngineException ex) {
				logger.error("Exception while loading flow " + uuid, ex);
				continue;
			}
			
			for (Flow f : flows)
				engineService.publishFlow(f);
			
			try (FileWriter writer = new FileWriter(location + "/" + uuid + ".xml")) {
				e.detach();
				storeFlow(e, writer);
			} catch (IOException ex) {
				logger.error("Exception while writing flow XML to disk", ex);
			}
		}
	}

	private void storeFlow(Element flows, Writer writer) throws IOException {
		Document doc = new Document(flows);
		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		xmlOutput.output(doc, writer);
		writer.flush();
	}
}

package com.petrolink.mbe.actions;

import java.util.Map;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.codec.SmartNowCodec;
import com.petrolink.mbe.directories.AlertProcessorDirectory;
import com.petrolink.mbe.model.message.AlertCepEvent;
import com.petrolink.mbe.services.ServiceAccessor;
import com.petrolink.mbe.setting.ActionSource;
import com.petrolink.mbe.setting.XmlSettingParser;
import com.smartnow.alertstatus.AlertJournal;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.nodes.Node;
import com.smartnow.engine.nodes.actions.Action;

/**
 * This is for dispatching alert through. This is a TEST tool.
 * @author aristo
 *
 */
public class DispatchAlertAction extends MBEAction {

	private static Logger logger = LoggerFactory.getLogger(DispatchAlertAction.class);
	
	private ActionSource messageSource;
	private AlertProcessorDirectory alertProcessorService;
	
	@Override
	public void load(Element e, Node parent) throws EngineException {
		super.load(e, parent);
		
		alertProcessorService = ServiceAccessor.getAlertProcessorDirectory();
		Namespace ns = e.getNamespace();
		String actionName = this.toString()+" seq "+this.getSequence();
		
		//Message Detail
		Element messageElement = e.getChild("Source", ns);
		if (messageElement == null) {
			throw new EngineException(actionName + " has no Source defined");
		}
		
		messageSource = XmlSettingParser.parseActionSource(messageElement);
	}
	
	@Override
	protected int executeAction(Map<String, Object> context) throws EngineException {
		Object source = null;
		if (messageSource != null) {
			source = messageSource.getSource(context);
		}
		
		if (source != null && source instanceof AlertJournal) {
			AlertCepEvent message;
			try {
				message =  SmartNowCodec.toAlertCepEvent((AlertJournal)source);
			} catch (Exception e) {
				logger.error("Unable to encode correctly with {} and source {}", "SmartNowCodec", source, e);
				return Action.RECOVERABLE_FAIL;
			}
			
			if(message != null) {
				try {
					dispatch(message);
				} catch (Exception e) {
					logger.error("Unable to encode correctly with {} and source {}", "SmartNowCodec", source, e);
					return Action.RECOVERABLE_FAIL;
				}
			} else {
				logger.warn("Trying to publish null message");
			}
		}
		return Action.SUCCESS;
	}

	private void dispatch(AlertCepEvent message) {
		if (alertProcessorService == null) return;
		alertProcessorService.dispatch(message);
	}
	
	@Override
	protected int executeTestAction(Map<String, Object> arg0) throws EngineException {
		// TODO Auto-generated method stub
		return Action.SUCCESS;
	}

	@Override
	public void finalize(Map<String, Object> arg0) throws EngineException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(Map<String, Object> arg0) throws EngineException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Cleaning up which can be called by Ruleflow when ruleflow doing deprovision
	 */
	@Override
	public void deprovision() {
		
	}
}

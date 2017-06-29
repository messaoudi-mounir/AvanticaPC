package com.petrolink.mbe.actions;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.services.ServiceAccessor;
import com.petrolink.mbe.templates.CommandTemplate;
import com.petrolink.mbe.templates.NotificationTemplateService;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.nodes.Node;

import freemarker.template.TemplateException;

/**
 * An action that allows a command to be run on the system. The command text is templated.
 * @author langj
 *
 */
public class RunCommandAction extends MBEAction {
	private static final Logger logger = LoggerFactory.getLogger(RunCommandAction.class);
	private NotificationTemplateService templateService;
	private UUID templateUUID;
	
	@Override
	public void load(Element e, Node parent) throws EngineException {
		super.load(e, parent);

		templateService = ServiceAccessor.getNotificationTemplateService();
		if (templateService != null) {
			Element commandElement = e.getChild("CommandTemplate", e.getNamespace());
			CommandTemplate ct = new CommandTemplate();
			ct.load(commandElement);
			
			templateUUID = templateService.storeTemplate(ct);
		}
	}

	@Override
	public void init(Map<String, Object> context) throws EngineException {
		
	}

	@Override
	public void finalize(Map<String, Object> context) throws EngineException {
		
	}
	
	@Override
	protected int executeAction(Map<String, Object> context) throws EngineException {
		logger.trace("Begin execute");
		String command = processCommandTemplate(context);
		
		try {
			// TODO Allow environment to be defined in XML
			Process process = Runtime.getRuntime().exec(command);
			logger.trace("Waiting for process");
			int statusCode = process.waitFor();
			if (statusCode != 0)
				logger.warn("Executed command exited with code {}", statusCode);
		} catch (IOException e) {
			throw new EngineException("Command execution failed", e);
		} catch (InterruptedException e) {
			throw new EngineException("The wait for a command process to exit was interrupted", e);
		}
		logger.trace("Success");
		return SUCCESS;
	}
	
	@Override
	protected int executeTestAction(Map<String, Object> context) throws EngineException {
		String command = processCommandTemplate(context);
		
		logger.info("Test command: {}", command);
		
		return SUCCESS;
	}
	
	private String processCommandTemplate(Map<String, Object> context) throws EngineException {
		Map<String, Object> templateContext = createTemplateContext(context);
		try {
			return templateService.processTemplate(templateUUID, CommandTemplate.COMMAND_ID, templateContext);
		} catch (TemplateException | IOException e) {
			throw new EngineException("Template processing failed", e);
		}
	}
}

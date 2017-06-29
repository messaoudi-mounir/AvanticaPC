package com.petrolink.mbe.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.actions.ActivitiInvokeAction;
import com.petrolink.mbe.actions.AlertAcknowledgeAction;
import com.petrolink.mbe.actions.AlertAuditLog;
import com.petrolink.mbe.actions.ClosedAlertLogAction;
import com.petrolink.mbe.actions.DeleteScheduleInvokeFlow;
import com.petrolink.mbe.actions.DispatchAlertAction;
import com.petrolink.mbe.actions.EvaluationLog;
import com.petrolink.mbe.actions.EventLogAction;
import com.petrolink.mbe.actions.RunCommandAction;
import com.petrolink.mbe.actions.ScheduleInvokeFlow;
import com.petrolink.mbe.actions.SendMailAction;
import com.petrolink.mbe.actions.SendRmqMessageAction;
import com.petrolink.mbe.actions.SendSMSAction;
import com.petrolink.mbe.actions.SendUINotification;
import com.petrolink.mbe.alertstatus.impl.AlertsService;
import com.petrolink.mbe.alertstatus.impl.serializers.AlertJSONSerializer;
import com.petrolink.mbe.alertstatus.impl.serializers.AlertXMLSerializer;
import com.petrolink.mbe.alertstatus.processors.PetrolinkAutoClear;
import com.petrolink.mbe.directories.AlertProcessorDirectory;
import com.petrolink.mbe.directories.WellDirectory;
import com.petrolink.mbe.journal.InvestigateMultiJournalImpl;
import com.petrolink.mbe.metrics.MetricSystem;
import com.petrolink.mbe.parser.ChannelDataAppendedParser;
import com.petrolink.mbe.rulesflow.PetrolinkStandAloneAlertFlow;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.services.ChronosClientService;
import com.petrolink.mbe.services.EngineServiceAdapter;
import com.petrolink.mbe.services.InstrumentationService;
import com.petrolink.mbe.services.MBEFlowsManagementService;
import com.petrolink.mbe.services.PetroVaultMbeOrchestrationService;
import com.petrolink.mbe.services.PetroVaultPrincipalService;
import com.petrolink.mbe.services.PetroVaultResourceService;
import com.petrolink.mbe.services.PropertyStoreService;
import com.petrolink.mbe.services.RabbitMQChannelService;
import com.petrolink.mbe.services.ServiceAccessor;
import com.petrolink.mbe.templates.NotificationTemplateService;
import com.petrolink.mbe.triggers.ChannelDataAppendedTrigger;
import com.petrolink.mbe.triggers.PetrolinkRoutingTrigger;
import com.smartnow.alertstatus.AlertsFactory;
import com.smartnow.alertstatus.processors.AlertProcessorFactory;
import com.smartnow.alertstatus.serializers.AlertSerializerFactory;
import com.smartnow.engine.Engine;
import com.smartnow.engine.Factory;
import com.smartnow.engine.nodes.NodeFactory;
import com.smartnow.engine.parsers.ParserFactory;
import com.smartnow.engine.services.ServiceFactory;
import com.smartnow.engine.triggers.TriggerFactory;
import com.smartnow.engine.xflow.FlowFactory;
import com.smartnow.mediation.MediationEssentialsRunner;

/**
 * Primary entry point of ComplexEventProcessor Service.
 * @author Paul Solano
 *
 */
public class ComplexEventProcessor extends MediationEssentialsRunner {
	private static Logger logger = LoggerFactory.getLogger(ComplexEventProcessor.class);
	
	@SuppressWarnings("javadoc")
	public ComplexEventProcessor() {
		MetricSystem.enableLoggingMetrics();
		MetricSystem.enableJVMMetrics();
		MetricSystem.enableJMXReporting();
		//MetricSystem.enableConsoleReporting();
	}
	 
	@Override
	protected void registerContexts() {
		super.registerContexts();
		logger.info("registering context for engine {}", this.getName());
		
		ServiceAccessor.initialize(new EngineServiceAdapter(Engine.getInstance()));
		
		ParserFactory.getInstance().registerClass("petrolinkChannelDataAppended", ChannelDataAppendedParser.class);
		ServiceFactory.getInstance().registerClass("PetroVaultMbeOrchestrationService", PetroVaultMbeOrchestrationService.class);
		ServiceFactory.getInstance().registerClass("NotificationTemplateService", NotificationTemplateService.class);
		ServiceFactory.getInstance().registerClass("PetroVaultPrincipalService", PetroVaultPrincipalService.class);
		ServiceFactory.getInstance().registerClass("PetroVaultResourceService", PetroVaultResourceService.class);
		ServiceFactory.getInstance().registerClass("AlertStatusService", AlertsService.class);
		ServiceFactory.getInstance().registerClass("AlertProcessorDirectory", AlertProcessorDirectory.class);
		ServiceFactory.getInstance().registerClass("WellDirectoryService", WellDirectory.class);
		ServiceFactory.getInstance().registerClass("ChronosClientService", ChronosClientService.class);
		ServiceFactory.getInstance().registerClass("InstrumentationService", InstrumentationService.class);
		ServiceFactory.getInstance().registerClass("MBEFlowsManagementService", MBEFlowsManagementService.class);
		ServiceFactory.getInstance().registerClass("AmqpChannelService", RabbitMQChannelService.class);
		ServiceFactory.getInstance().registerClass("PropertyStoreService", PropertyStoreService.class);
		TriggerFactory.getInstance().registerClass("PetrolinkRouter", PetrolinkRoutingTrigger.class);
		TriggerFactory.getInstance().registerClass("ChannelDataAppended", ChannelDataAppendedTrigger.class);		
		FlowFactory.getInstance().registerClass(RuleFlow.XML_ELEMENT_NAME, RuleFlow.class);
		FlowFactory.getInstance().registerClass("AlertActionsFlow", PetrolinkStandAloneAlertFlow.class);
		NodeFactory.getInstance().registerClass("SendMailNotification", SendMailAction.class);
		NodeFactory.getInstance().registerClass("SendSMSNotification", SendSMSAction.class);
		NodeFactory.getInstance().registerClass("SendUINotification", SendUINotification.class);
		NodeFactory.getInstance().registerClass("SendRmqMessage", SendRmqMessageAction.class);
		NodeFactory.getInstance().registerClass("AcknowledgeAlert", AlertAcknowledgeAction.class);
		NodeFactory.getInstance().registerClass("DispatchAlertCepEvent", DispatchAlertAction.class);
		NodeFactory.getInstance().registerClass("EvaluationLog", EvaluationLog.class);
		NodeFactory.getInstance().registerClass("AlertAuditLog", AlertAuditLog.class);
		NodeFactory.getInstance().registerClass("ActivitiInvoke", ActivitiInvokeAction.class);
		NodeFactory.getInstance().registerClass("ScheduleAlertTimer", ScheduleInvokeFlow.class);
		NodeFactory.getInstance().registerClass("DeleteAlertTimer", DeleteScheduleInvokeFlow.class);
		NodeFactory.getInstance().registerClass("EventLog", EventLogAction.class);
		NodeFactory.getInstance().registerClass("ClosedAlertLog", ClosedAlertLogAction.class);
		NodeFactory.getInstance().registerClass("RunCommand", RunCommandAction.class);
		AlertsFactory.getInstance().registerContext(Factory.ContextSource.CLASSPATH, "classpath*:**/petrolink-alertstatus-ctx.xml");
		AlertsFactory.getInstance().registerClass("journal_"+InvestigateMultiJournalImpl.INVESTIGATE_SINGLE_TYPE, InvestigateMultiJournalImpl.class);
		AlertSerializerFactory.getInstance().registerClass("JSON", AlertJSONSerializer.class);
		AlertSerializerFactory.getInstance().registerClass("XML", AlertXMLSerializer.class);
		AlertProcessorFactory.getInstance().registerClass("PetrolinkAutoClear", PetrolinkAutoClear.class);
		
		addShutdownHook();
	}
	
	private void addShutdownHook() {
		logger.info("adding shutdown hook for engine {}", this.getName());
		Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
            	Engine engine = Engine.getInstance();
            	logger.info("shutting down engine {}",  engine.getName());
            	engine.setKeepRunning(false);
                try {
                    engine.join();
                } catch (InterruptedException e) {
                    logger.error("Unable to gracefully wait for engine {} to finish", engine.getName(), e);
                }
                logger.info("shutted down engine {}",  engine.getName());
            }
        });
	}
}

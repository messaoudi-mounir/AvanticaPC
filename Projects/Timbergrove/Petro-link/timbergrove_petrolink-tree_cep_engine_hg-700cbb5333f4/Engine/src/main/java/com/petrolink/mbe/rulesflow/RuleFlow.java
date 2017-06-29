package com.petrolink.mbe.rulesflow;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mvel2.MVEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Timer;
import com.petrolink.mbe.actions.MBEAction;
import com.petrolink.mbe.alertstatus.impl.AlertsDAO;
import com.petrolink.mbe.alertstatus.impl.AlertsService;
import com.petrolink.mbe.alertstatus.impl.AutoAlertDismissal;
import com.petrolink.mbe.alertstatus.impl.DelayAlertAction;
import com.petrolink.mbe.alertstatus.impl.DynamicAlertTemplate;
import com.petrolink.mbe.alertstatus.impl.RuleFlowAlertListener;
import com.petrolink.mbe.directories.WellDirectory;
import com.petrolink.mbe.directories.WellDirectory.WellDefinition;
import com.petrolink.mbe.metrics.MetricSystem;
import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.model.message.AlertCepEvent;
import com.petrolink.mbe.model.message.AlertSnapshotSummary;
import com.petrolink.mbe.propstore.PropertyGroupMap;
import com.petrolink.mbe.rulesflow.variables.AlertClassVariable;
import com.petrolink.mbe.rulesflow.variables.ChannelAlias;
import com.petrolink.mbe.rulesflow.variables.RuleBufferedChannelAlias;
import com.petrolink.mbe.rulesflow.variables.Variable;
import com.petrolink.mbe.rulesflow.variables.VariableDatum;
import com.petrolink.mbe.rulesflow.variables.VariableFactory;
import com.petrolink.mbe.services.PetroVaultMbeOrchestrationService;
import com.petrolink.mbe.services.PropertyStoreService;
import com.petrolink.mbe.services.ServiceAccessor;
import com.petrolink.mbe.services.StandardWellMetadataKeys;
import com.petrolink.mbe.setting.XmlSettingParser;
import com.petrolink.mbe.util.TimePeriodCounter;
import com.petrolink.mbe.util.UUIDHelper;
import com.smartnow.alertstatus.Alert;
import com.smartnow.alertstatus.AlertsFactory;
import com.smartnow.alertstatus.processors.AlertProcessor;
import com.smartnow.alertstatus.processors.AlertProcessorFactory;
import com.smartnow.engine.Engine;
import com.smartnow.engine.event.Event;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.executiongroups.ExecutionGroup;
import com.smartnow.engine.nodes.Node;
import com.smartnow.engine.nodes.NodeFactory;
import com.smartnow.engine.nodes.flowcontrol.FlowControlNode.FlowControlBehavior;
import com.smartnow.engine.util.EngineContext;
import com.smartnow.engine.util.MVELUtil;
import com.smartnow.engine.xflow.Flow;

import freemarker.template.Configuration;

/**
 * The primary Rule evaluation logic component
 * Rule Flow loads the Rule definition XML and creates 
 * @author paul
 *
 */
public class RuleFlow extends Flow {
	/**
	 * The XML Element Name for this flow
	 */
	public static final String XML_ELEMENT_NAME = "Rule";
	
	/**
	 * Channel UUID constant used in the Context
	 */
	public static final String CHANNEL_CTX_VARIABLE = "channelUUID";
	
	/**
	 * AlertClass UUID constant used in the Context
	 */
	public static final String ALERTCLASS_CTX_VARIABLE = "alertClassUUID";
	
	/**
	 * Index constant used in the Context
	 */
	public static final String INDEX_CTX_VARIABLE = "index";
	/**
	 * Value constant used in the Context
	 */
	public static final String VALUE_CTX_VARIABLE = "value";
	/**
	 * Well UUID constant used in the Context
	 */
	public static final String WELL_CTX_VARIABLE = "wellUUID";
	/**
	 * Content Type constant used in the Context
	 */
	public static final String CONTENTTYPE_CTX_VARIABLE = "contentType";
	/**
	 * Rule logger is private non-static to support Overloading with custom Rule Appender
	 */
	public static final String PERSISTENT_CTX_VARIABLE = "persistent";
	/**
	 * Well UUID constant used in the Context
	 */
	public static final String ALERT_STATUS_CTX_VARIABLE = "alertStatus";
	
	/**
	 * For Evaluated timestamp
	 */
	public static final String EVALUATED_TIMESTAMP = "evaluatedTimestamp";
	
	private static final String PRE_EVALUATION_TIMER_NAME = "pre-evaluation-time";
	private static final String EVALUATION_TIMER_NAME = "evaluation-time";
	private static final Timer staticPreEvaluationTimer = MetricSystem.timer(RuleFlow.class, "static", PRE_EVALUATION_TIMER_NAME);
	private static final Timer staticEvaluationTimer = MetricSystem.timer(RuleFlow.class, "static", EVALUATION_TIMER_NAME);
	
	private Logger logger = LoggerFactory.getLogger(RuleFlow.class);
	
	private String preEvaluationTimerName;
	private Timer preEvaluationTimer;
	private String evaluationTimerName;
	private Timer evaluationTimer;
	private UUID wellId;
	private UUID wellboreId;
	private UUID ruleId;
	private String ruleName;
	private final HashMap<UUID,ChannelAlias> dependencies = new HashMap<>();
	private final HashMap<String, Variable> filteringVariables = new HashMap<>();
	private final HashMap<String, Variable> conditionVariables = new HashMap<>();
	private final HashMap<String, AlertClassVariable> alertClassById = new HashMap<>();
	private final HashMap<String, AlertClassVariable> alertClassByAlias = new HashMap<>();
	private DynamicAlertTemplate alertTemplate;
	private String alertClassId;
	private String alertClassName;
	private ExecutionGroup alertActionsExecutionGroup;
	private AlertProcessor processor;
	private Serializable condition;
	private Serializable filteringCondition;
	private Instant lastEvaluationTime;
	private Object lastEvaluationIndex;
	private AutoAlertDismissal autoDimissalConfig = null;
	private DelayAlertAction alertActionDelay = null;
	private double maxGap;
	
	private static Configuration cfg;
	
	static {
       cfg = new Configuration(Configuration.VERSION_2_3_23);
       cfg.setDefaultEncoding("UTF-8");
	};
	
	/**
	 * Actions variable uses a TreeSet container of EPAction classes. It
	 * contains the actions to be executed.
	 */
	private TreeSet<Node> ruleActionNodes = new TreeSet<Node>();
	private FlowControlBehavior failControl = FlowControlBehavior.STOPFAIL;
	private EvaluationStrategy evaluationStrategy;
	private final ArrayList<String> dependentFlows = new ArrayList<>();
	private List<Flow> alertFlows = null;
	private final HashMap<String, String> properties = new HashMap<String, String>();
	private boolean loadActionsEnabled = true;
	private boolean loadAlertFlowsEnabled = true;
	private boolean loadExecutionGroupsFromWellEnabled = true;
	private final TimePeriodCounter evaluationCounter = new TimePeriodCounter(5, Duration.ofMinutes(1));
	private PropertyGroupMap persistentVariables;
	//private HashMap<UUID, DataPoint> prevDataSet = new HashMap<>();

	/**
	 * Default Constructor
	 */
	public RuleFlow() {
	}
	
	/**
	 * Load flow from xml 
	 * @param e Xml Element for the rule
	 * @return List of Flow loaded by this flow
	 * @throws EngineException
	 * @see com.smartnow.engine.xflow.Flow#load(org.jdom2.Element)
	 */
	@Override
	public List<Flow> load(Element e) throws EngineException {
		this.setLazyLoaded(false);
		List<Flow> flows = new ArrayList<Flow>();

		try {
			//Load Metadata
			loadMetadata(e);

			// Creating specific Logger
			loadSpecificLogger(e.getAttributeValue("logger"), e.getAttributeValue("logpath"));

			// Loading evaluation strategy
			loadEvaluationStrategy(e.getAttributeValue("evaluationStrategy"));

			loadProperties(e.getChild("Properties", e.getNamespace()));

			// Loading Dependencies (Cache Alias)
			loadDependencies(e.getChild("Dependencies",e.getNamespace()));

			// Loading Variables
			loadVariables(e.getChild("Variables",e.getNamespace()));

			// Loading Filtering Condition
			loadFilteringCondition(e.getChild("FilteringCondition", e.getNamespace()));

			// Loading Evalution Condition
			loadCondition(e.getChild("Condition", e.getNamespace()));

			// Load Rule level Actions
			Element ruleActionXml = e.getChild("RuleActions", e.getNamespace());
			loadActionFailControlConfiguration(ruleActionXml);
			if (loadActionsEnabled) {
				loadActions(ruleActionXml);
			}

			// Create Alert Settings
			if (loadAlertFlowsEnabled) {
				alertFlows = loadAlertSettings(e.getChild("AlertSettings", e.getNamespace()), alertActionsExecutionGroup);
			}

			flows.add(this);
			if(CollectionUtils.isNotEmpty(alertFlows)) {
				flows.addAll(alertFlows);
			}
		} catch (Exception exception) {
			if (logger.isDebugEnabled()) {
				XMLOutputter xmlOutput = new XMLOutputter();
				xmlOutput.setFormat(Format.getPrettyFormat());
				logger.debug("Failure to load RuleFlow : \n {}", xmlOutput.outputString(e));
			}
			throw new EngineException("Failure to load RuleFlow " + getUniqueId(), exception);
		}

		return flows;
	}
	
	/**
	 * Load metadata of ruleflow from the Xml Element. Use this method if you need to "peek" basic metadata, by partially load such metadata 
	 * @param e
	 */
	public void loadMetadata(Element e){
		setUniqueId(e.getAttributeValue("uuid"));

		String wellIdString = e.getAttributeValue("wellId");
		if (wellIdString == null)
			wellIdString = e.getAttributeValue("well"); // temporary backwards compatibility
		wellId = UUIDHelper.fromStringFast(wellIdString);

		String wellboreIdString = e.getAttributeValue("wellboreId");
		wellboreId = wellboreIdString != null ? UUIDHelper.fromStringFast(wellboreIdString) : null;

		ruleId = UUIDHelper.fromStringFastOpt(e.getAttributeValue("ruleId"));
		ruleName = e.getAttributeValue("ruleName");
		
		//Load execution group from well directory
		if (loadExecutionGroupsFromWellEnabled) {
			loadExecutionGroupsFromWell();
		}
	}
	
	/**
	 * Load execution groups from Well information (Well directory)
	 */
	private void loadExecutionGroupsFromWell() {
		WellDirectory wellDirectory = ServiceAccessor.getWellDirectory();
		if (wellDirectory != null) {
			WellDefinition well = wellDirectory.getOrCreateWell(wellId);
			executionGroup = well.getRulesExecutionGroup();
			alertActionsExecutionGroup = well.getAlertActionsExecutionGroup();
		} else {
			logger.warn("WellDirectory is null during initialization of rule {}", getUniqueId());
		}
	}
	
	/**
	 * Load specific logger for this rule FlowLogger_{specificLogger}, if name is "$name", logger name will be FlowLogger_{UniqueId}
	 * @param specificLogger Name of the specific logger
	 * @param specificLoggerPath
	 */
	private void loadSpecificLogger(final String specificLoggerName, final String specificLoggerPath) {
		Logger currentLogger = logger;
		if (StringUtils.isNotBlank(specificLoggerName)) {
			if ("$name".equals(specificLoggerName)) {
				String logpath = "";
				if (StringUtils.isNotBlank(specificLoggerPath)) {
					logpath =specificLoggerPath;
				}
				logger = super.createSpecificLogger("FlowLogger_" + this.getUniqueId(), logpath + this.getUniqueId());
			} else {
				logger = LoggerFactory.getLogger("FlowLogger_" + specificLoggerName);
			}
			currentLogger.info("Begin loading to specific logger {}.", logger.getName());
		} else {
			currentLogger.info("Rule {} will use default logger.", getUniqueId());
		}
	}

	/**
	 * Load Evaluation Strategy for the rule based on Rule Configuration.
	 * @param currentStrategy Evaluation strategy, the value can be "LastKnownValue"  or "IndexAlignment"
	 * @throws EngineException
	 */
	private void loadEvaluationStrategy(final String currentStrategy) throws EngineException {
		if (StringUtils.isBlank(currentStrategy)) {
			throw new EngineException("Rule MUST have evaluation strategy");
		}
		
		switch (currentStrategy) {
			case "LastKnownValue":
				this.evaluationStrategy = new LastKnownValueStrategy(this);
				break;		
			case "IndexAlignment":
				this.evaluationStrategy = new IndexAlignmentStrategy(this);
				break;
			default:
				throw new EngineException("Unknown evaluation strategy: "+currentStrategy);
		}
	}
	
	private void loadProperties(Element p) throws EngineException {
		if (p == null)
			return;
		for (Element e : p.getChildren("Entry", p.getNamespace())) {
			String key = e.getAttributeValue("key");
			String value = e.getText();
			properties.put(key, value);
		}
	}
	
	private void loadActionFailControlConfiguration(Element root) {
		String failControlAttr = root.getAttributeValue("failControl");
		if (StringUtils.isNotBlank(failControlAttr)) {
			switch (failControlAttr.toLowerCase()) {
				case "stopandthrow":
					this.failControl = FlowControlBehavior.STOPTHROW;
					break;
				case "stopandfail":
					this.failControl = FlowControlBehavior.STOPFAIL;
					break;
				case "stopandsuccess":
					this.failControl = FlowControlBehavior.STOPSUCCESS;
					break;
				case "continueandfail":
					this.failControl = FlowControlBehavior.CONTINUEFAIL;
					break;
				case "continueandsuccess":
					this.failControl = FlowControlBehavior.CONTINUESUCCESS;
					break;
				default:
					logger.warn("Rule {} has unknown failcontrol: {}.", getUniqueId(),failControlAttr);
					break;
			}
		}
	}

	private void loadActions(Element root) throws EngineException {
		

		List<Element> _nodes = root.getChildren();
		for (Element e : _nodes) {
			Node action = NodeFactory.getNode(e, this);
			boolean exists = false;
			for (Node a : ruleActionNodes) {
				if (a.getSequence() == action.getSequence()) {
					exists = true;
					break;
				}
			}

			if (exists) {
				logger.error("Duplicated action sequence for processor "
						+ this.getUniqueId() + "with sequence "
						+ Integer.toString(action.getSequence()));
				throw new EngineException(
						"Duplicated action sequence for processor "
								+ this.getUniqueId() + "with sequence "
								+ Integer.toString(action.getSequence()));
			} else {
				ruleActionNodes.add(action);
			}
		}
	}

	/**
	 * create Alert Settings handle:
	 * 	- Assignment of generic Alert parameters (eka. name)
	 *  - Creation of the Alert Template Object
	 *  - Creation of one action flow for each Alert Event
	 *  - Register Alert Events to Alert Events Listener
	 * @param alertSettings XML Element
	 * @return List of Alert processing Flows
	 * @throws EngineException 
	 */
	private List<Flow> loadAlertSettings(Element alertSettings, ExecutionGroup alertExecutionGroup) throws EngineException {
		//AlertsDAO dao = (AlertsDAO) AlertsFactory.getAlertsDAO();
		List<Flow> alertActions = new ArrayList<Flow>();
		
		alertClassId = alertSettings.getAttributeValue("class");
		alertClassName = alertSettings.getAttributeValue("className");
		
		// Temporary backwards compatibility
		if (alertClassId == null) {
			alertClassId = alertSettings.getAttributeValue("name");
			alertClassName = alertClassId;
		}
		
		if (alertClassId == null || alertClassName == null)
			throw new EngineException("class or className is null");
		
		createAlertTemplete(alertClassId, alertClassName, alertSettings.getChild("Template", alertSettings.getNamespace()));
		this.processor = AlertProcessorFactory.getAlertProcessor(alertSettings.getChild("ProcessingLogic", alertSettings.getNamespace()), this);
		
		
		Element autoAlertDismissalElement = alertSettings.getChild("AutoDismiss", alertSettings.getNamespace());
		if (autoAlertDismissalElement != null) {
			AutoAlertDismissal config = XmlSettingParser.parseAutoAlertDismissal(autoAlertDismissalElement);
			autoDimissalConfig = config;
		}
		
		DelayAlertAction delayConfig = new DelayAlertAction();
		
		for (Element actionGroup : alertSettings.getChild("Actions", alertSettings.getNamespace()).getChildren()) {
			if ((actionGroup.getChildren() != null) && (actionGroup.getChildren().size() > 0)) {
				PetrolinkAlertActionsFlow flow = new PetrolinkAlertActionsFlow(this);
				String actionName = actionGroup.getName();
				String actionGroupUniqueId = actionName + this.getUniqueId();
				flow.load(actionGroupUniqueId, alertExecutionGroup, actionGroup);
				flow.setResourceGroup(processor.getResourceGroup());
				flow.setActionGroup(actionGroup.getName());
								
				alertActions.add(flow);
				dependentFlows.add(actionGroupUniqueId);
				
				//Set delay 
				String delayString= actionGroup.getAttributeValue("delay");
				if(StringUtils.isNotBlank(delayString)){
					try{
						Long delay = Long.decode(delayString);
						delayConfig.addDelay(actionName, delay);
					} catch(NumberFormatException nfe) {
						logger.warn("Action {} has Incorrect delay setting ",actionGroupUniqueId,delayString);
					}
				}
			}
		}
		
		if (!delayConfig.isNoDelay()) {
			alertActionDelay = delayConfig;
		}
		
		return alertActions;
	}

	/**
	 * Loads the condition script
	 * @param the condition element
	 */
	private void loadCondition(Element condition) {
		this.condition = MVELUtil.compileScript(condition.getText().trim());
	}

	/**
	 * Loads filtering condition script
	 * @param the filtering condition element
	 */
	private void loadFilteringCondition(Element filteringCondition) {
		if ((filteringCondition != null) && (!"".equals(filteringCondition.getText().trim())))
			this.filteringCondition = MVELUtil.compileScript(filteringCondition.getText().trim());
		
		if (filteringCondition.getAttribute("validateDependecyAvailability") != null) {
			evaluationStrategy.setValidateDependenciesAvailability(Boolean.parseBoolean(filteringCondition.getAttributeValue("validateDependecyAvailability")));
		}
	}

	private void loadVariables(Element variables) {
		for (Element vElement : variables.getChildren()) {
			try {
				Variable v = VariableFactory.getVariable(this, vElement);
				
				if (v.getScope() == Variable.GLOBAL_SCOPE) {
					this.filteringVariables.put(v.getAlias(), v);
					this.conditionVariables.put(v.getAlias(), v);
				} else if (v.getScope() == Variable.CONDITION_SCOPE) {
					v.setScope(Variable.CONDITION_SCOPE);
					this.conditionVariables.put(v.getAlias(), v);				
				} else if (v.getScope() == Variable.FILTERING_SCOPE) {
					v.setScope(Variable.FILTERING_SCOPE);
					this.filteringVariables.put(v.getAlias(), v);
				} else {
					logger.error("Unsupported Variable scope Reference");
				}
				
			} catch (EngineException e) {
				logger.error("Error loading variable ",e);
			}
		}
		
		// Determine the maximum gap from all condition variables
		maxGap = 0;
		for (Variable var : conditionVariables.values()) {
			if (var instanceof ChannelAlias) {
				maxGap = Math.max(maxGap, ((ChannelAlias) var).getGap());
			}
		}
	}

	private void loadDependencies(Element dependencies) {
		String defaultType = dependencies.getAttributeValue("defaultType");
		for (Element dependencyElement : dependencies.getChildren()) {
			String dependencyElementName = dependencyElement.getName();
			try {
				if ("Channel".equals(dependencyElementName)) {
					ChannelAlias ca = (ChannelAlias) VariableFactory.getChannel(this, dependencyElement, defaultType);
					
					this.getDependencies().put(ca.getUuid(), ca);
					switch (ca.getScope()) {
					case Variable.GLOBAL_SCOPE:
						this.filteringVariables.put(ca.getAlias(), ca);
						this.conditionVariables.put(ca.getAlias(), ca);
						break;
					case Variable.CONDITION_SCOPE:
					case Variable.REFERENCE_SCOPE:
						this.conditionVariables.put(ca.getAlias(), ca);				
						break;
					case Variable.FILTERING_SCOPE:
						this.filteringVariables.put(ca.getAlias(), ca);
						break;
					}									
				} else if ("AlertClass".equals(dependencyElementName)){
					AlertClassVariable alertClass = new AlertClassVariable();
					alertClass.load(this, dependencyElement);
					alertClassByAlias.put(alertClass.getAlias(), alertClass); 
					alertClassById.put(alertClass.getAlertClassId(), alertClass);
				} else {
					logger.error("Unexpected element in dependencies section, ignoring it");
				}
			} catch (EngineException e) {
				logger.error("Unable to create channel alias variable", e);
			}
		}
	}

	
	
	/**
	 * Creates the Alert Template Object based on the defined parameters
	 * @param template element
	 * @throws EngineException 
	 */
	private void createAlertTemplete(String classId, String className, Element template) throws EngineException {
		alertTemplate = new DynamicAlertTemplate();
		
		
		//Doesn't work in real server (service)
		//alertTemplate.load(classId, className, template, Engine.getInstance().getBean("freemarkerConfig", Configuration.class));
		alertTemplate.load(classId, className, template, cfg);
	}
	
	/**
	 * update specified alert Alert
	 * @param cepEvent
	 */
	public void updateLocalCache(AlertCepEvent cepEvent) {
		if (cepEvent == null) return;
		
		String alertClassId = cepEvent.getAlertClassId();
		if (StringUtils.isNotBlank(alertClassId))  {
			AlertClassVariable alertvar = alertClassById.get(alertClassId);
			if (alertvar != null) {
				alertvar.update(cepEvent);
			}
		}
	}
	
	private Event generateBaseEvent() {
		Event event = new Event(executionGroup.getEventStore());
		event.setClassId(getUniqueId());
		// Creating and submitting event to corresponding ExecutionGroup
		Properties p = new Properties();
		p.setProperty(RuleFlow.WELL_CTX_VARIABLE, UUIDHelper.toStringFast(getWellId()));
		p.setProperty(RuleFlow.CONTENTTYPE_CTX_VARIABLE, "ExecutionData");
		event.setProperties(p);
		
		return event;
	}
	
	/**
	 * Create and submit an event ot this rule's default execution group.
	 * @param cepEvent AlertCepEvent
	 */
	public void submitExecuteEvent(AlertCepEvent cepEvent) {
		Event event = generateBaseEvent();
		event.getProperties().setProperty(RuleFlow.ALERTCLASS_CTX_VARIABLE, cepEvent.getAlertClassId());
		event.setContent(new EventCepEventContent(cepEvent), Event.TYPE_OBJECT);
		executionGroup.submitEvent(event);
	}
	
	/**
	 * Create and submit an event ot this rule's default execution group.
	 * @param channelId
	 * @param dp
	 */
	public void submitExecuteEvent(UUID channelId, DataPoint dp) {
		Event event = generateBaseEvent();
		event.getProperties().setProperty(RuleFlow.CHANNEL_CTX_VARIABLE, UUIDHelper.toStringFast(channelId));
		event.setContent(new EventDataPointContent(channelId, dp), Event.TYPE_OBJECT);
		executionGroup.submitEvent(event);
	}

	@Override
	public int execute(Event ev, UUID sharedObjects) throws EngineException {
		try {
			RuleFlowEvent rev = RuleFlowEvent.from(ev);
			if (logger.isDebugEnabled()) {
				logger.debug("Begin execution of rule {} caused by {}", getUniqueId(), rev.getContent());
			}
			
			// The first step is to have the evaluation strategy determine if enough data is available on the dependent
			// channels to evaluate. We might have to evaluate multiple times too.
			
			while (true) {
				try (Timer.Context tc = staticPreEvaluationTimer.time(); Timer.Context tci = preEvaluationTimer.time()) {
					if (!evaluationStrategy.preEvaluation(rev, true))
						break;
				}
				
				try (Timer.Context tc = staticEvaluationTimer.time(); Timer.Context tci = evaluationTimer.time()) {
					evaluateDataSet(ev, sharedObjects);
				}
				finally {
					// Must call this to cleanup the data set array and anything else
					evaluationStrategy.postEvaluation();
				}
			}
			
			logger.debug("Finished execution, {} events remain in queue", executionGroup.getQueueSize());
		} catch (Exception ex) {
			logger.error("Execution of rule " + getUniqueId() + " failed", ex);
			logger.debug("{} events remain in queue", executionGroup.getQueueSize());
		}
		return SUCCESS;
	}
	
	private void evaluateDataSet(final Event ev, final UUID sharedObjects) throws Exception {
		logger.trace("Preparing evaluation context");

		// Should be no current need for Event to be immutable as required info is copied elsewhere
		
		try (EngineContext evaluationContext = evaluationStrategy.prepareContext(ev, sharedObjects)) {
//			// If persistent variables are available they are added to the context here
//			if (persistentVariables != null)
//				evaluationContext.put(PERSISTENT_CTX_VARIABLE, persistentVariables);
			
			// Evaluate the condition and place the result in the context
			logger.trace("Evaluating condition");
			Object result = MVEL.executeExpression(condition, evaluationContext.getVariableResolverFactory());
			
			boolean evaluationResult = result instanceof Boolean ? (Boolean) result : false;
			evaluationContext.put("result", evaluationResult);
			evaluationContext.put(EVALUATED_TIMESTAMP, OffsetDateTime.now());
			logger.debug("Condition result: {}", result);
			
//			// Commit persistent variable changes to database
//			if (persistentVariables != null) {
//				int changes = persistentVariables.commit();
//				if (changes != 0)
//					logger.debug("Committed {} persistent variable changes", changes);
//				evaluationContext.remove(PERSISTENT_CTX_VARIABLE); // not sure if this should remain in context for now
//			}
			
			logger.trace("Executing rule action nodes");
			for (Node node : ruleActionNodes) {
				node.init(evaluationContext);
			}
			
			//int ruleActionResult = Node.SUCCESS;
			for (Node node : ruleActionNodes) {
				try {
					int nodeResult = node.execute(evaluationContext);

					if (nodeResult != Node.SUCCESS) {
						if (failControl == FlowControlBehavior.STOPFAIL) {
							//ruleActionResult = Node.FAIL;
							break;
						} else if (failControl == FlowControlBehavior.STOPSUCCESS) {
							//ruleActionResult = Node.SUCCESS;
							break;
						} else if (failControl == FlowControlBehavior.CONTINUEFAIL) {
							//ruleActionResult = Node.FAIL;
						}
					}
				} catch (EngineException e) {
					logger.error("Action {} skipped due to unhandled engine exception ", node.getName(), e);
				} catch (Exception e) {
					logger.error("Action {} skipped due to unable to execute action ", node.getName(), e);
				}
			}
			
			for (Node node : ruleActionNodes) {
				node.finalize(evaluationContext);
			}
			
			logger.trace("Recording evaluation results and processing alert");
			
			evaluationContext.put("rule", this);
			
			lastEvaluationTime = Instant.now();
			lastEvaluationIndex = evaluationContext.get(INDEX_CTX_VARIABLE);
			assert DataPoint.isValidIndexType(lastEvaluationIndex);
			evaluationCounter.increment();
							
			this.processor.processAlert(alertClassId, evaluationResult, alertTemplate, evaluationContext);
			logger.trace("Finished processing alert");
		}
	}

	/**
	 * @return the evaluationStrategy
	 */
	public EvaluationStrategy getEvaluationStrategy() {
		return evaluationStrategy;
	}

	/**
	 * @return the filteringVariables
	 */
	public Map<String, Variable> getFilteringVariables() {
		return filteringVariables;
	}
	
	/**
	 * @return the filteringCondition
	 */
	public Serializable getFilteringCondition() {
		return filteringCondition;
	}

	/**
	 * @param filteringCondition the filteringCondition to set
	 */
	public void setFilteringCondition(Serializable filteringCondition) {
		this.filteringCondition = filteringCondition;
	}
	
	/**
	 * Gets the properties defined for this RuleFlow.
	 * @return A properties map
	 */
	public Map<String, String> getProperties() {
		return properties;
	}
	
	/**
	 * Gets the evaluation counter that is incremented after every evaluation.
	 * @return the evaluation counter.
	 */
	public TimePeriodCounter getEvaluationCounter() {
		return evaluationCounter;
	}
	
	/**
	 * @return the Rule Id
	 */
	public UUID getRuleId() {
		return ruleId;
	}
	
	/**
	 * @return the Rule Name
	 */
	public String getRuleName() {
		return ruleName;
	}
	
	/**
	 * Get the well name. This is equivalent to getProperties().get(StandardWellMetadataKeys.WELL_NAME);
	 * @return The well name
	 */
	public String getWellName() {
		return properties.get(StandardWellMetadataKeys.WELL_NAME);
	}
	
	/**
	 * Get the rig name. This is equivalent to getProperties().get(StandardWellMetadataKeys.RIG_NAME);
	 * @return The rig name
	 */
	public String getRigName() {
		return properties.get(StandardWellMetadataKeys.RIG_NAME);
	}

	/**
	 * @return the conditionVariables
	 */
	public Map<String, Variable> getConditionVariables() {
		return conditionVariables;
	}

	/**
	 * @return the dependencies
	 */
	public Map<UUID,ChannelAlias> getDependencies() {
		return dependencies;
	}
	
	/**
	 * Retur Alert Class Dependencies
	 * @return Alert Class variable where the rule depends on
	 */
	public Map<String,AlertClassVariable> getAlertClassDependencies() {
		return this.alertClassById;
	}
	
	/**
	 * Retur Alert Class Dependencies
	 * @return Alert Class variable where the rule depends on
	 */
	public Map<String,AlertClassVariable> getAlertClassConditionVariable() {
		return this.alertClassByAlias;
	}

	/**
	 * @return the well ID
	 */
	public UUID getWellId() {
		return wellId;
	}
	
	/**
	 * Gets the time the last evaluation occurred. This will be null if the rule has not yet evaluated.
	 * @return An Instant or null
	 */
	public Instant getLastEvaluationTime() {
		return lastEvaluationTime;
	}
	
	/**
	 * Gets the index of the last evaluation. This will be a valid index type or null if no evaluation has occurred.
	 * @return The index of the last evaluation.
	 */
	public Object getLastEvaluationIndex() {
		return lastEvaluationIndex;
	}
	
	/**
	 * Gets the maximum of all condition channel gap values
	 * @return The maximum gap value
	 */
	public double getMaxGap() {
		return maxGap;
	}
	
	/**
	 * Updates the Rule associated channel cache based on the provided evaluation strategy. This method is thread-safe.
	 * @param channelId the Channel ID
	 * @param dp The incoming data point
	 */
	public void updateLocalCache(UUID channelId, DataPoint dp) {
		logger.trace("Update local cache for channel {} with data point {}", channelId, dp);
		evaluationStrategy.updateLocalCache(channelId, dp);
	}

	@Override
	public void cleanup() {
		// Cleaning up and removing associated flows
		logger.info("Cleanup rule {}", getUniqueId());
		
		ServiceAccessor.getWellDirectory().unregisterRule(this);
		ServiceAccessor.getAlertProcessorDirectory().unregisterRule(this);

		AlertsDAO dao = (AlertsDAO) AlertsFactory.getAlertsDAO();
		dao.unRegisterAlertClass(alertClassId);
						
		RuleFlowAlertListener listener = (RuleFlowAlertListener) AlertsFactory.getAlertListener(AlertsService.ALERT_LISTENER_NAME);
		
		int count = 0;
		for (Flow f : alertFlows) {
			if ( f instanceof PetrolinkAlertActionsFlow) {
				PetrolinkAlertActionsFlow paaf = ((PetrolinkAlertActionsFlow) f);
				count += listener.cleanupByAlertClassIdEventId(alertClassId, f.getUniqueId(), paaf.getActionGroup());
			}
		}
		logger.info("Removed {} alert flow listener for class {} of flow {} ", count, alertClassId);

		// Cleaning up Alert Channels
		for (Variable v : dependencies.values()) {
			if (v instanceof RuleBufferedChannelAlias) {
				RuleBufferedChannelAlias c = (RuleBufferedChannelAlias) v;
				
				if (!c.isCacheGlobal()) {
					c.clear();
				}
			}
		}
		
		MetricSystem.remove(preEvaluationTimerName);
		MetricSystem.remove(evaluationTimerName);
		
		logger.info("Finished cleanup");
	}

	@Override
	public void deprovision() {
		logger.info("Deprovision rule {}", getUniqueId());
		super.deprovision();
		
		AlertsDAO dao = (AlertsDAO) AlertsFactory.getAlertsDAO();
		dao.unRegisterAutoDismiss(alertClassId);
		dao.unRegisterAlertActionDelay(alertClassId);
		
		ArrayList<String> currentAlertActionFlowIds = dependentFlows;
		
		Engine curEngine = Engine.getInstance();
		if (CollectionUtils.isNotEmpty(currentAlertActionFlowIds)) {
			logger.info("Removing dependent flows");
			if (curEngine != null) {
				for (String id : currentAlertActionFlowIds) {
					logger.trace("Removing flow {}", id);
					try {
						curEngine.removeFlow(id);
					} catch (EngineException e) {
						logger.error("Unable to remove child flows", e);
					}
				}
			} else {
				logger.error("Unable to remove alertActionFlows for flow {} due to missing Engine", getUniqueId());
			}

			currentAlertActionFlowIds.clear();
		}
		
		for (Node node : ruleActionNodes) {
			if (node instanceof MBEAction) {
				MBEAction mbeAction = ((MBEAction) node);
				mbeAction.deprovision();
			}
		}
		
		if (persistentVariables != null) {
			persistentVariables.clear();
			persistentVariables.commit();
			assert persistentVariables.reload() == 0 : "not all properties in the group were cleared!";
			persistentVariables = null;
		}
		
		this.processor = null;
		logger.info("Finished deprovision");
	}

	/* (non-Javadoc)
	 * @see com.smartnow.engine.xflow.Flow#startup()
	 */
	@Override
	public void startup() throws EngineException {
		super.startup();
		
		preEvaluationTimerName = MetricSystem.name(RuleFlow.class, "inst", getUniqueId(), PRE_EVALUATION_TIMER_NAME);
		preEvaluationTimer = MetricSystem.timer(preEvaluationTimerName);
		evaluationTimerName = MetricSystem.name(RuleFlow.class, "inst", getUniqueId(), EVALUATION_TIMER_NAME);
		evaluationTimer = MetricSystem.timer(evaluationTimerName);
		
		WellDefinition wellDefinition = null;
		PetroVaultMbeOrchestrationService mbeService = ServiceAccessor.getPVMBEService();
		
		logger.debug("RuleFlow {} is getting metadata", this.getUniqueId());
		if (this.wellId != null) {
			wellDefinition = getWellDefinition();
			if (wellDefinition != null) {
				this.executionGroup = wellDefinition.getRulesExecutionGroup();
				
				for (ChannelAlias alias : this.dependencies.values()) {
					wellDefinition.registerChannel(alias.getUuid(), alias.getAlias());					
				}
			} else {
				throw new EngineException("Undefined Well with UUID " + wellId);
			}
			
			// Well and wellbore metadata is copied into this rule's property map. Their keys are noncolliding so
			// there will not be an overwrite issue.
			// Alerts will copy all entries in the rule's property map into its metadata object.
			Map<String, String> wellMetadata = null, wellboreMetadata = null;
			try {
				wellMetadata = mbeService.getWellMetadataAsync(wellId).get();
				if (wellboreId != null)
					wellboreMetadata = mbeService.getWellboreMetadataAsync(wellboreId).get();
			} catch (Exception e) {
				throw new EngineException("failed to get well and wellbore metadata", e);
			}
			
			if (wellMetadata != null) {
				properties.putAll(wellMetadata);
			} else {
				logger.warn("No Well Metadata for well {} ", wellId);
			}
			if (wellboreMetadata != null) {
				properties.putAll(wellboreMetadata);
			} else {
				if (wellboreId != null) {
					logger.warn("No Wellbore Metadata for wellboreId {} ", wellboreId);
				}
			}
		} else {
			logger.warn("Rule {} does not have well id", getUniqueId());
		}
		
		RuleFlowAlertListener listener = (RuleFlowAlertListener) AlertsFactory.getAlertListener(AlertsService.ALERT_LISTENER_NAME);
		AlertsDAO dao = (AlertsDAO) AlertsFactory.getAlertsDAO();				
		
		for (Flow flow : alertFlows) {
			switch (((PetrolinkAlertActionsFlow) flow).getActionGroup()) {
			case "OnCreateActions":
				listener.onCreate(alertClassId, flow.getUniqueId());
				break;
			case "OnUpdateActions":
				listener.onUpdate(alertClassId, flow.getUniqueId());
				break;
			case "OnInvestigateActions":
				listener.registerOnInvestigate(alertClassId, flow.getUniqueId());
				break;	
			case "OnAcknowledgeActions":
				listener.onAcknowledge(alertClassId, flow.getUniqueId());
				break;
			case "OnStatusChangeActions":
				listener.onStatusChange(alertClassId, flow.getUniqueId());
				break;
			case "OnCommentActions":
				listener.onComment(alertClassId, flow.getUniqueId());
				break;
			case "OnSnoozeActions":
				listener.onSnooze(alertClassId, flow.getUniqueId());
				break;					
			case "OnUnSnoozeActions":
				listener.onUnSnooze(alertClassId, flow.getUniqueId());
				break;
			case "OnJournalEntryActions":
				listener.onJournalEntry(alertClassId, flow.getUniqueId());
				break;					
			}			
		}
		
		dao.registerAlertClass(alertClassId);
		dao.registerAutoDismiss(alertClassId, autoDimissalConfig);
		dao.registerAlertActionDelay(alertClassId, alertActionDelay);
		
		PropertyStoreService ps = ServiceAccessor.getPropertyStoreService();
		if (ps != null ) {
			if (ps.getPropertyStore() != null) {
				persistentVariables = new PropertyGroupMap(ps.getPropertyStore(), "rf-" + getUniqueId());
			} else {
				logger.warn("Property Store is not Available even when PropertyStoreService is available");
			}
		}
		
		ServiceAccessor.getAlertProcessorDirectory().registerRule(this);
		ServiceAccessor.getWellDirectory().registerRule(this);
	}
	
	/**
	 * @return Rule related Well Definition
	 */
	public WellDefinition getWellDefinition() {
		WellDirectory wellDirectory = null;
		WellDefinition wellDefinition = null;
		
		wellDirectory = ServiceAccessor.getWellDirectory();
		if (wellDirectory != null) {
			wellDefinition = wellDirectory.getWell(this.wellId);
			return wellDefinition;
		}

		return null;
	}

	/**
	 * @return the processor
	 */
	public AlertProcessor getProcessor() {
		return processor;
	}

	/**
	 * Defines whether during loading process, the rule actions are enabled
	 * @return the loadActionsEnabled
	 */
	public final boolean isLoadActionsEnabled() {
		return loadActionsEnabled;
	}

	/**
	 * Defines whether during loading process, the rule actions are enabled
	 * @param loadActionsEnabled the loadActionsEnabled to set
	 */
	public final void setLoadActionsEnabled(boolean loadActionsEnabled) {
		this.loadActionsEnabled = loadActionsEnabled;
	}

	/**
	 * Defines whether during loading process, the Alert Flows are enabled
	 * @return the loadAlertFlowsEnabled
	 */
	public final boolean isLoadAlertFlowsEnabled() {
		return loadAlertFlowsEnabled;
	}

	/**
	 * Defines whether during loading process, the Alert Flows are enabled
	 * @param loadAlertFlowsEnabled the loadAlertFlowsEnabled to set
	 */
	public final void setLoadAlertFlowsEnabled(boolean loadAlertFlowsEnabled) {
		this.loadAlertFlowsEnabled = loadAlertFlowsEnabled;
	}

	/**
	 * Defines whether during loading process, the execution groups is loaded from specified well definitions
	 * @return the loadExecutionGroupsFromWellEnabled
	 */
	public final boolean isLoadExecutionGroupsFromWellEnabled() {
		return loadExecutionGroupsFromWellEnabled;
	}

	/**
	 * Defines whether during loading process, the execution groups is loaded from specified well definitions
	 * @param loadExecutionGroupsFromWellEnabled the loadExecutionGroupsFromWellEnabled to set
	 */
	public final void setLoadExecutionGroupsFromWellEnabled(boolean loadExecutionGroupsFromWellEnabled) {
		this.loadExecutionGroupsFromWellEnabled = loadExecutionGroupsFromWellEnabled;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
	
	/**
	 * Adds an external flow dependency to this rule
	 * @param flowId
	 */
	public void addDependentFlow(String flowId) {
		dependentFlows.add(flowId);
	}
	
	/**
	 * Get Index and Value being used for specified event
	 * @param ev
	 * @return VariableDatum containing information about  Index and Value being used for specified event
	 */
	public VariableDatum getEventPoint(Event ev){
		return getVariableDatum(this,ev);
	}
	
	/**
	 * Get Index and Value being used for specified event
	 * @param rule
	 * @param ev
	 * @return
	 */
	private static VariableDatum getVariableDatum(RuleFlow rule, Event ev) {
		Object index = null;
		Object value = null;
		Object ecObject = ev.getContent();
		if (ecObject instanceof EventDataPointContent) {
			EventDataPointContent ec = (EventDataPointContent)ecObject;
			DataPoint dp = rule.getDependencies().get(ec.getChannelId()).getValueAsDataPoint();
			if (dp != null) { 
				index = dp.getIndex();
				value = dp.getValue();
			}
		} else if (ecObject instanceof EventCepEventContent) {
			EventCepEventContent ec = (EventCepEventContent)ecObject;
			AlertCepEvent latest =   rule.getAlertClassDependencies().get(ec.getCepEvent().getAlertClassId()).getValueAsAlertCepEvent();
			if (latest != null) { 
				index = latest.getIndex();
				value = latest;
			}
		} else {
			return null;
		}
		
		if ((index != null) && (value != null)){
			VariableDatum datum = new VariableDatum();
			datum.setIndex(index);
			datum.setValue(value);
			return datum;
		}
		return null;
	}
	
	/**
	 * Generate Context for active alert
	 * @return Map containing alert Status info
	 */
	public HashMap<String, Object> generateAlertStatusContext() {
		Map<String,AlertClassVariable> alertClassVars = getAlertClassDependencies();
		
		HashMap<String, Object> alertContextes = new HashMap<>();
		if ((alertClassVars == null)||(alertClassVars.isEmpty())) { return alertContextes; }
		
		ArrayList<AlertSnapshotSummary> activeAlerts = new ArrayList<>();
		HashSet<String> activeClasses = new HashSet<>();
		for (AlertClassVariable alertClassVar : alertClassVars.values()) {
			AlertSnapshotSummary alertSummary = alertClassVar.getActiveAlert();
			if ((alertSummary != null) && (alertSummary.getStatus() == Alert.ACTIVE)) {
				activeAlerts.add(alertSummary);
				activeClasses.add(alertClassVar.getAlertClassId());
			}
		}
		alertContextes.put("activeInstances", activeAlerts);
		alertContextes.put("activeClasses", activeClasses);
		
		return alertContextes;
	}
}

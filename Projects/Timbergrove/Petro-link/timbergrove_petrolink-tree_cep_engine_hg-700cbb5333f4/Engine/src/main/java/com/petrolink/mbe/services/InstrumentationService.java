package com.petrolink.mbe.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Timer;
import com.petrolink.mbe.metrics.MetricSystem;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.util.NamespaceJDom2Writer;
import com.petrolink.mbe.util.TimePeriodCounter;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.smartnow.engine.IFlow;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.services.Service;
import com.smartnow.rabbitmq.util.RMQConnectionSettings;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.xml.JDom2Driver;
import com.thoughtworks.xstream.io.xml.JDom2Reader;

/**
 * Allows information about the CEP, such as the status of existing flows, to be queried from RMQ in a RESTful manner.
 * @author langj
 *
 */
public final class InstrumentationService extends Service {
	private static Logger logger = LoggerFactory.getLogger(InstrumentationService.class);
	private static final Timer processTimer = MetricSystem.timer(InstrumentationService.class, "request-process-time");
	private static final String XML_CONTENT_TYPE = "text/xml";
	
	//private static final String FLOW_STATUS_NOT_AVAILABLE = "NotAvailable";
	private static final String XMLNS_QUERY_STR = "http://www.petrolink.com/mbe/query";
	private static final Namespace XMLNS_QUERY = Namespace.getNamespace(XMLNS_QUERY_STR);
	
	private static final ThreadLocal<DocumentSerializer> serializer = ThreadLocal.withInitial(() -> new DocumentSerializer());
	
	private EngineService engineService;
	private RMQConnectionSettings connectionSettings;
	private ArrayList<Channel> channels = new ArrayList<>();
	private XStream xstream;
	
	@Override
	public void load(Element e) throws EngineException {
		super.load(e);

		engineService = ServiceAccessor.getEngineService();
		
		// XStream must process annotations ahead of time to avoid threading issues on deserialization
		xstream = new XStream(new JDom2Driver());
		xstream.processAnnotations(Request.class);
		xstream.processAnnotations(Response.class);
		
		Element connection = e.getChild("RMQConnectionSettings", e.getNamespace());
		connectionSettings = new RMQConnectionSettings();
		connectionSettings.load(connection);
	}
	
	@Override
	public void startService() {
		try {
			createChannels();
		} catch (Exception e) {
			logger.error("error while creating channels", e);
		}
	}
	
	@Override
	public void stopService() {
		try {
			closeChannels();
		} catch (Exception e) {
			logger.error("error while closing channels", e);
		}
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
	
	private void createChannels() throws Exception {
		ConnectionFactory factory = connectionSettings.getConnectionFactory();
		Connection connection = factory.newConnection();
		
		for (int i = 0; i < connectionSettings.getConcurrentListeners(); i++) {
			Channel channel = connection.createChannel();
			
			String queueName = connectionSettings.getQueue();
			if (queueName != null) {
				channel.queueDeclare(queueName, true, false, false, null);
			} else {
				queueName = channel.queueDeclare().getQueue();
			}
			
			channel.exchangeDeclare(connectionSettings.getExchange(), connectionSettings.getExchangeType());
			channel.queueBind(queueName, connectionSettings.getExchange(), connectionSettings.getRoutingKey());
			channel.basicQos(1);
			channel.basicConsume(queueName, true, new Consumer(channel));
			
			channels.add(channel);
		}
	}
	
	private void closeChannels() throws Exception {
		for (Channel c : channels) {
			c.close();
		}
	}
	
	private static FlowStatus getFlowStatus(IFlow flow) {
		FlowStatus o = new FlowStatus();
		o.uuid = flow.getUniqueId();
		o.type = flow.getClass().getSimpleName();
		o.status = FlowStatus.getStatusName(flow.getStatus());
		
		if (flow instanceof RuleFlow) {
			RuleFlow ruleFlow = (RuleFlow) flow;
			
			Instant lastTime = ruleFlow.getLastEvaluationTime();
			if (lastTime != null) {
				o.lastRunning = lastTime.toString();
				o.lastEvaluationIndex = ruleFlow.getLastEvaluationIndex().toString();
			}

			o.evaluationCounter = new EvaluationCounter();
			TimePeriodCounter counter = ruleFlow.getEvaluationCounter();
			List<TimePeriodCounter.Period> periods = counter.getPeriods();
			if (!periods.isEmpty()) {
				ArrayList<EvaluationCounterPeriod> xperiods = new ArrayList<>();
				
				for (TimePeriodCounter.Period p : periods) {
					EvaluationCounterPeriod xp = new EvaluationCounterPeriod();
					xp.start = p.getStartTime().toString();
					xp.end = p.getEndTime().toString();
					xp.count = p.getCount();
					xperiods.add(xp);
				}
				
				o.evaluationCounter.periods = xperiods;
			}
		}
		
		return o;
	}
	
	// Process the request to produce a response
	private byte[] processRequest(BasicProperties properties, byte[] body) throws EngineException, IOException {
		try {
			Request req = unmarshalRequest(body);
			Response resp = new Response();
			
			if (req.flowStatusListRequest != null) {
				logger.info("processing FlowStatusListRequest");
				resp.flowStatusList = processFlowStatusListRequest(req.flowStatusListRequest);
			}
			else {
				throw new EngineException("unhandled request body");
			}
			
			return marshalResponse(resp);
		}
		catch (Exception e) {
			logger.error("handled error while processing request, replying with error response", e);
			
			Response resp = new Response();
			resp.error = new Error();
			resp.error.exception = e.getClass().getName();
			resp.error.message = e.getMessage();
			resp.error.stackTrace = ExceptionUtils.getStackTrace(e);

			return marshalResponse(resp);
		}
	}
	
	private Request unmarshalRequest(byte[] request) throws JDOMException, IOException {
		Document doc = serializer.get().deserialize(request);
		return (Request) xstream.unmarshal(new JDom2Reader(doc.getRootElement()));
	}
	
	private byte[] marshalResponse(Response resp) throws IOException {
		// Instead of using toXML, marshalling is done manually in order to use NamespaceJDom2Writer which will
		// preserve our namespaces in the response
		NamespaceJDom2Writer writer = new NamespaceJDom2Writer(XMLNS_QUERY);
		xstream.marshal(resp, writer);
		Document doc = new Document((Element) writer.getTopLevelNodes().get(0));
		return serializer.get().serialize(doc);
	}
	
	// Receive FlowStatusListRequest and create FlowStatusList
	private FlowStatusList processFlowStatusListRequest(FlowStatusListRequest fslr) {
		FlowStatusList fsl = new FlowStatusList();
		fsl.statusList = new ArrayList<FlowStatus>();
		
		HashSet<String> uuidSet = fslr.uuidSet;
		HashSet<String> statusSet = fslr.statusSet;
		HashSet<String> typeSet = fslr.typeSet;
		
		for (IFlow f : engineService.getAllFlows()) {
			String status = FlowStatus.getStatusName(f.getStatus());
			String type = f.getClass().getSimpleName();
			String uuid = f.getUniqueId();
			
			if (uuidSet != null && !uuidSet.contains(uuid))
				continue;
			if (statusSet != null && !statusSet.contains(status))
				continue;
			if (typeSet != null && !typeSet.contains(type))
				continue;
			
			fsl.statusList.add(getFlowStatus(f));
		}
		
		return fsl;
	}
	
	private final class Consumer extends DefaultConsumer {
		public Consumer(Channel channel) {
			super(channel);
		}
	
		@Override
		public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
				throws IOException {
			
			try (Timer.Context tc = processTimer.time()) {
				logger.trace("received request");
				
				// Reject anything that isn't "text/xml"
				if (!XML_CONTENT_TYPE.equals(properties.getContentType())) {
					logger.error("unhandled content type: {}", properties.getContentType());
					return;
				}
				
				// Verify that reply queue is specified before we bother processing
				String replyQueue = properties.getReplyTo();
				if (StringUtils.isEmpty(replyQueue)) {
					logger.error("no reply queue specified");
					return;
				}
				
				byte[] responseBody;
				try {
					responseBody = processRequest(properties, body);
				} catch (Exception e) {
					logger.error("exception while processing request", e);
					return;
				}
				
				if (responseBody == null)
					return;
				
				logger.trace("sending response");
				
				BasicProperties responseProps = new BasicProperties.Builder()
					.appId("CEP")
					.correlationId(properties.getCorrelationId())
					.contentEncoding("utf-8")
					.contentType("text/xml")
					.build();
				
				// Publish directly to the reply queue
				getChannel().basicPublish("", replyQueue, responseProps, responseBody);
			}
		}
	}
	
	@SuppressWarnings("javadoc")
	@XStreamAlias("Request")
	public static class Request {
		@XStreamAlias("FlowStatusListRequest")
		public FlowStatusListRequest flowStatusListRequest;
	}

	@SuppressWarnings("javadoc")
	@XStreamAlias("Response")
	public static class Response {
		@XStreamAlias("Error")
		public Error error;
		
		@XStreamAlias("FlowStatusList")
		public FlowStatusList flowStatusList;
	}

	@SuppressWarnings("javadoc")
	public static class Error {
		@XStreamAsAttribute
		public String exception;
		
		@XStreamAlias("Message")
		public String message;
		
		@XStreamAlias("StackTrace")
		public String stackTrace;
	}

	@SuppressWarnings("javadoc")
	public static class FlowStatusListRequest {
		@XStreamImplicit(itemFieldName="Status")
		public HashSet<String> statusSet;
		
		@XStreamImplicit(itemFieldName="Type")
		public HashSet<String> typeSet;

		@XStreamImplicit(itemFieldName="UUID")
		public HashSet<String> uuidSet;
	}

	@SuppressWarnings("javadoc")
	public static class FlowStatusList {
		@XStreamImplicit(itemFieldName="FlowStatus")
		public List<FlowStatus> statusList;
	}

	@SuppressWarnings("javadoc")
	public static class FlowStatus {
		@XStreamAsAttribute
		public String type;

		@XStreamAsAttribute
		public String uuid;

		@XStreamAsAttribute
		public String lastEvaluationIndex;

		@XStreamAsAttribute
		public String lastRunning;

		@XStreamAsAttribute
		public String status;

		@XStreamAlias("EvaluationCounter")
		public EvaluationCounter evaluationCounter;
		
		private static final String FLOW_STATUS_DRAFT = "Draft";
		private static final String FLOW_STATUS_DEPROVISIONED = "Deprovisioned";
		private static final String FLOW_STATUS_STARTED = "Started";
		private static final String FLOW_STATUS_STOPPING = "Stopping";
		private static final String FLOW_STATUS_STOPPED = "Stopped";
		private static final String FLOW_STATUS_UNKNOWN = "unknown";
		
		public static String getStatusName(IFlow.FlowStatus status) {
			if (status == null) return null;
			if (status == IFlow.FlowStatus.ACTIVE) {
				return FLOW_STATUS_STARTED;
			} else if (status == IFlow.FlowStatus.DRAFT) {
				return FLOW_STATUS_DRAFT;
			} else if (status == IFlow.FlowStatus.INACTIVATING) {
				return FLOW_STATUS_STOPPING;
			} else if (status == IFlow.FlowStatus.INACTIVE) {
				return FLOW_STATUS_STOPPED;
			}  else if (status == IFlow.FlowStatus.DEPROVISIONED) {
				return FLOW_STATUS_DEPROVISIONED;
			} else {
				return FLOW_STATUS_UNKNOWN;
			}
		}
	}

	@SuppressWarnings("javadoc")
	public static class EvaluationCounter {
		@XStreamImplicit(itemFieldName="Period")
		public List<EvaluationCounterPeriod> periods;
	}

	@SuppressWarnings("javadoc")
	public static class EvaluationCounterPeriod {
		@XStreamAsAttribute
		public String start;

		@XStreamAsAttribute
		public String end;

		@XStreamAsAttribute
		public int count;
	}
	
	// Allows non-thread-safe XML serializers to be cached for a thread
	private static class DocumentSerializer {
		public final SAXBuilder saxBuilder = new SAXBuilder();
		public final XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
		
		Document deserialize(byte[] data) throws JDOMException, IOException {
			return saxBuilder.build(new ByteArrayInputStream(data));
		}
		
		byte[] serialize(Document doc) throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			xmlOutputter.output(doc, baos);
			return baos.toByteArray();
		}
	}
}

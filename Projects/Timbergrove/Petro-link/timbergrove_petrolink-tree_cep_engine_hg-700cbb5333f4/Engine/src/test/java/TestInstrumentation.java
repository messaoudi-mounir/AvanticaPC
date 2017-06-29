import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import com.petrolink.mbe.services.InstrumentationService.FlowStatusListRequest;
import com.petrolink.mbe.services.InstrumentationService.Request;
import com.petrolink.mbe.services.InstrumentationService.Response;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.JDom2Driver;

@SuppressWarnings("javadoc")
public class TestInstrumentation {
	
	public static void main(String[] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(args[0]);
		factory.setUsername(args[1]);
		factory.setPassword(args[2]);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		
		String queueName = channel.queueDeclare().getQueue();
		
		XStream xstream = new XStream(new JDom2Driver());
		xstream.processAnnotations(Request.class);
		xstream.processAnnotations(Response.class);
		
		String cid = UUID.randomUUID().toString();
		String appId = "test.instrumentation";
		
		BasicProperties props = new BasicProperties.Builder()
                .correlationId(cid)
                .appId(appId)
                .contentEncoding("utf-8")
                .contentType("text/xml")
                .replyTo(queueName)
                .build();
		
		FlowStatusListRequest fslr = new FlowStatusListRequest();
		fslr.typeSet = new HashSet<String>(Arrays.asList("RuleFlow"));
		Request req = new Request();
		req.flowStatusListRequest = fslr;
		
		String reqString = xstream.toXML(req);
		
		channel.basicPublish("MBE.Instrumentation", "", props, reqString.getBytes(StandardCharsets.UTF_8));
		
		// Blocking wait for single response
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(queueName, true, consumer);
		
		Delivery delivery = consumer.nextDelivery();
		
		String message = new String(delivery.getBody());
		System.out.println(message);
	}
}

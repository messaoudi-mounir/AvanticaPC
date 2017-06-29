
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.FileUtils;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

@SuppressWarnings("javadoc")
public class TestAlertStatusWebService {
	public static void main(String[] args) throws IOException, TimeoutException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(args[0]);
		factory.setUsername(args[1]);
		factory.setPassword(args[2]);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		String replyQueueName = channel.queueDeclare().getQueue();

		QueueingConsumer consumer = new QueueingConsumer(channel);
	    channel.basicConsume(replyQueueName, true, consumer);

		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		
		while (true) {
			String line = sc.nextLine();

			if ("exit".compareTo(line) == 0)
				break;

			String fname = line;
			Path filepath = Paths.get(System.getProperty("user.dir") + "/" + fname);
			
			if (Files.exists(filepath)) {
				String corrId = java.util.UUID.randomUUID().toString();

			    BasicProperties props = new BasicProperties
			                                .Builder()
			                                .correlationId(corrId)
			                                .replyTo(replyQueueName)
			                                .build();
				
				channel.exchangeDeclare(args[3], args[4]);
							
				channel.basicPublish(args[3], "", props, FileUtils.readFileToByteArray(filepath.toFile()));
			    
			    String response = null;
			    while (true) {
			        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			        if (delivery.getProperties().getCorrelationId().equals(corrId)) {
			            response = new String(delivery.getBody());
			            break;
			        }
			    }
			    
			    System.out.println(response);
			}
			
		}
	    
		channel.close();
		connection.close();
	}
}

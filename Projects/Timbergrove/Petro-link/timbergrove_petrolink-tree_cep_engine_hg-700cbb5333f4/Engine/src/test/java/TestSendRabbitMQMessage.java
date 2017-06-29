import java.util.concurrent.TimeoutException;

import org.json.JSONObject;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@SuppressWarnings("javadoc")
public class TestSendRabbitMQMessage {
	private static final String EXCHANGE_NAME = "simpleExchange";

	public static void main(String[] argv) throws java.io.IOException, TimeoutException, InterruptedException {

		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		factory.setUsername("guest");
		factory.setPassword("guest");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        // Create a messages
        JSONObject object = new JSONObject();
		object.put("index",10);
		object.put("value",2035);
		
		JSONObject attributes = new JSONObject();
		attributes.put("type", 2);
		attributes.put("persistence", true);
		
		object.put("attributes",attributes);

		channel.basicPublish(EXCHANGE_NAME, "", null, object.toString().getBytes());

		System.out.println(" [x] Sent '" + object.toString() + "'");

//		@SuppressWarnings("resource")
//		Scanner scan = new Scanner(System.in);
//
//		while (true) {
//			String l = scan.nextLine();
//			if (l.compareTo(".") == 0) {
//				System.out.println("Exiting");
//				break;
//			} else
//				Thread.sleep(10000);
//		}

		channel.close();
		connection.close();
	}
}

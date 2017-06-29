package com.petrolink.mbe.notification.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.FileUtils;

import com.petrolink.mbe.rmqrestfulsrv.client.FileBaseQueryClient;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;

@SuppressWarnings("javadoc")
public class NotificationRepositoryClient extends FileBaseQueryClient {
	public static void main(String[] args) throws IOException, TimeoutException, ShutdownSignalException,
			ConsumerCancelledException, InterruptedException {
		String helloMessage = "Notification Template Repository Client\n";
		String helpMessage = "Notification Template Repository Client\n"
				+ "Invokes the Notification Template Repository Service to add/update or delete a given Notification Tempalte";
		new NotificationRepositoryClient().setHelloMessage(helloMessage).setHelpMessage(helpMessage).execute(args);
	}

	@Override
	public void processCommand(Channel channel, String commandLine) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		String[] parts = commandLine.split(" ");

		String fname = parts[1];
		Path filepath = Paths.get(System.getProperty("user.dir") + "/" + fname);
		
		if (Files.exists(filepath)) {
			Map<String, Object> headers = new HashedMap<String, Object>();
			// adding expected headers
			
			String method = "post";
			switch (parts[0].toLowerCase()) {
			case "register":
				method = "put";
				break;
			case "update":
				method = "post";
				break;
			case "remove":
				method = "delete";
				break;
			}

			BasicProperties props = getProperties(method, "text/xml", parts[0].toLowerCase(),headers);
		    publish(channel, props, FileUtils.readFileToByteArray(filepath.toFile()));			    
			
		    String response = getReply();
		    
		    System.out.println(response);
		}		
	}
}
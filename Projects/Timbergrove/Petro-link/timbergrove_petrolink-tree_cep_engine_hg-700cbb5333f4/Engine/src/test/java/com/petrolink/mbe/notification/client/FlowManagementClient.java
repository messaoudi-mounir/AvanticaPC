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
public class FlowManagementClient extends FileBaseQueryClient {
	public static void main(String[] args) throws IOException, TimeoutException, ShutdownSignalException,
			ConsumerCancelledException, InterruptedException {
		String helloMessage = "Flow Management Service Client\n";
		String helpMessage = "Flow Management Service Client\n"
				+ "Invokes the Flow Management Service to add, update, delete, and query flows";
		new FlowManagementClient().setHelloMessage(helloMessage).setHelpMessage(helpMessage).execute(args);
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
package com.petrolink.mbe.rmqrestfulsrv.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.FileUtils;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;
import com.smartnow.rabbitmq.service.RMQRestfulServiceClient;

@SuppressWarnings("javadoc")
public abstract class FileBaseQueryClient extends RMQRestfulServiceClient {
	private String helloMessage;
	private String helpMessage;

	public FileBaseQueryClient setHelloMessage(String message) {
		this.helloMessage = message;
		return this;
	}
	
	public FileBaseQueryClient setHelpMessage(String message) {
		this.helpMessage = message;
		return this;
	}

	public void execute(String[] args) throws IOException, TimeoutException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		if ("--help".equals(args[0])) {
			System.out.print(helpMessage);
			System.out.println("Expected Parameters");
			System.out.println(" - Host/Port -- Host format can be a host name or a hostname:port");
			System.out.println(" - User");
			System.out.println(" - Password");
			System.out.println(" - Exchange");
			System.out.println(" - Exchange Type");			
			System.out.println(" - [Method] -- Supported methods are get, post, put, delete");
			System.out.println(" - [File Name] -- File name is required if method is specified");
		} else {
			validateBasicParameters(args);
			if (args.length == 7) {
				System.out.print(helloMessage);
				System.out.println("=== Invoking method " + args[5] + " with file " + args[6]);
				invokeDirect(args);

			} else {
				System.out.print(helloMessage);
				System.out.println("=== All Commands have the syntaxis <command> <file>");
				System.out.println("=== Available commands are");
				System.out.println("=== 	- register");
				System.out.println("=== 	- remove");
				System.out.println("=== Eg. register flow01.xml ");
				System.out.println("=== Exit the process by writing exit");
				invoke(args);
			}			
		}
	}

	private void invokeDirect(String[] args) throws IOException, TimeoutException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		this.channel = getConnection(args);
		
		String fname = args[6];
		Path filepath = Paths.get(System.getProperty("user.dir") + "/" + fname);

		if (Files.exists(filepath)) {
			Map<String, Object> headers = new HashedMap<String, Object>();
			// adding expected headers

			BasicProperties props = getProperties("post","text/xml",args[5].toLowerCase(), headers);
			publish(channel, props, FileUtils.readFileToByteArray(filepath.toFile()));

			String response = getReply();

			System.out.println(response);
		}

		channel.close();
		connection.close();

	}
	
	public void invoke(String[] args) throws IOException, TimeoutException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		this.channel = getConnection(args);

		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		
		while (true) {
			String line = sc.nextLine();

			if ("exit".compareTo(line.toLowerCase()) == 0)
				break;

			processCommand(channel, line);			
		}
	    
		channel.close();
		connection.close();
	}
	
	public abstract void processCommand(Channel channel, String commandLine) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException;
}
package com.petrolink.mbe.amqp;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Interface for publisher and subscriber of amqp
 * @author aristo
 *
 */
public interface IAmqpPubSub {
	/**
	 * Name of the publisher
	 * @return name
	 */
	String getName();
	
	/**
	 * Set Name of the publisher
	 * @param newName 
	 */
	void setName(String newName);
	
	/**
	 * Close channel inside the publisher
	 * @throws IOException 
	 * @throws TimeoutException 
	 */
	void close()  throws IOException, TimeoutException;
}

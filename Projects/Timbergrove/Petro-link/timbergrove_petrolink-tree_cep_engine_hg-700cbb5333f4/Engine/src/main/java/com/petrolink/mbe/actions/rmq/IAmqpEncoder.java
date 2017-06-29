package com.petrolink.mbe.actions.rmq;

import com.petrolink.mbe.amqp.AmqpMessage;
import com.petrolink.mbe.setting.GenericConfiguration;

/**
 * Interface for classes capable to convert object to AMQPMessage
 * @author aristo
 *
 */
public interface IAmqpEncoder {

	/**
	 * Encode specified object to AMQPMessage, if specified object known to this encoder. 
	 * @param object
	 * @return Null if object is not known to this encoder, otherwise will return constructed message
	 * @throws Exception
	 */
	AmqpMessage encode(Object object) throws Exception;
	
	/***
	 * Load Encoder Config
	 * @param config
	 */
	void load(GenericConfiguration config);

}
package com.petrolink.mbe.actions.rmq;

import com.petrolink.mbe.amqp.AmqpMessage;

/**
 * Interface for classes capable to convert object from AMQPMessage
 * @author aristo
 *
 */
public interface IAmqpDecoder {

	/**
	 * Encode specified AMQPMessage to objet, if specified object known to this encoder.
	 * @param message
	 * @return Decoded object
	 * @throws Exception
	 */
	Object decode(AmqpMessage message) throws Exception;

}
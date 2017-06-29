package com.petrolink.mbe.amqp;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

/**
 * Helper for AMQP operation
 * @author aristo
 *
 */
public class AmqpUtil {

	/**
	 * Declare specified exchange using specified channel
	 * @param channel Channel to be used to declare
	 * @param exchangeDef Exchange Definition to declare
	 * @throws IOException
	 */
	public static void declare(Channel channel, AmqpExchange exchangeDef) throws IOException {
		channel.exchangeDeclare(
				exchangeDef.getName(),
				exchangeDef.getType(), 
				exchangeDef.isDurable(), 
				exchangeDef.isInternal(),
				exchangeDef.isAutoDelete(),
				exchangeDef.getArguments()
				);
	}
	
	/**
	 * Declare specified queue using specified channel
	 * @param channel Channel to be used to declare
	 * @param queueDef Queue Definition to declare
	 * @throws IOException
	 */
	public static void declare(Channel channel, AmqpQueue queueDef) throws IOException {
		AMQP.Queue.DeclareOk confirm = channel.queueDeclare(queueDef.getName()
				, queueDef.isDurable()
				, queueDef.isExclusive()
				, queueDef.isAutoDelete()
				, queueDef.getArguments());
		String serverQueueName=confirm.getQueue();
		if (StringUtils.isNotBlank(serverQueueName)) {
			queueDef.setName(serverQueueName);
		}
	}
	
	/**
	 * Create argument resource string builder
	 * @param builder
	 * @param arguments
	 * @return StringBuilder being used to append
	 */
	public static StringBuilder appendArgumentResource(StringBuilder builder, Map<String, Object> arguments) {
		if (builder != null && arguments != null) {
			for(Map.Entry<String,Object> entry: arguments.entrySet()) {
				builder.append("&").append(entry.getKey()).append("=").append(entry.getValue().toString());
			}
		}
		return builder;
	}
}

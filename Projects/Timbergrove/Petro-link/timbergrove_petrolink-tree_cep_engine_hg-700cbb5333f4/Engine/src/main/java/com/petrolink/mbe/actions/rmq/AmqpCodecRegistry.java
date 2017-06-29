package com.petrolink.mbe.actions.rmq;

import org.h2.util.StringUtils;

/**
 * Registry for AMQP codec. 
 * @author aristo
 *
 */
public class AmqpCodecRegistry {
	
	/**
	 * Get Encoder with specified name
	 * @param name
	 * @return AMQP Encoder which can convert to AMP Message
	 */
	public IAmqpEncoder getEncoder(String name) {
		//TODO: This may need to be moved to use reflection or spring beans. 
		//However it needs to watchout for instances which can be shared, which one need to be new for each usage
		if (StringUtils.equals(name, AlertAmqpCodec.class.getSimpleName())) {
			return new AlertAmqpCodec();
		} else if (StringUtils.equals(name, AlertCepEventAmqpCodec.class.getSimpleName())) {
			return new AlertCepEventAmqpCodec();
		} 
		return null;
	}
	
	private static final Object singletonLock = new Object();
	private static AmqpCodecRegistry singleton;
	/**
	 * Get Singleton Instance
	 * @return Singleton of this Class
	 */
	public static AmqpCodecRegistry getInstance() {
		if (singleton  == null) {
			synchronized (singletonLock) {
				if (singleton  == null) {
					singleton = new AmqpCodecRegistry();
				}
			}
		}
		return singleton;
	}
}

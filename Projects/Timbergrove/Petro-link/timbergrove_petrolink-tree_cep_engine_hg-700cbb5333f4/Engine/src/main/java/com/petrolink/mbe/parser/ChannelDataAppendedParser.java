package com.petrolink.mbe.parser;

import java.io.IOException;
import java.util.Map;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartnow.engine.Resource;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.parsers.impl.ParserImpl;
import com.smartnow.engine.triggers.Trigger;

import Petrolink.WITSML.Events.ChannelDataAppended;

/**
 * Parser for Petrolink's Realtime Data, primarily for ChannelDataAppended.
 * @author Aristo
 *
 */
public class ChannelDataAppendedParser extends ParserImpl {
	private final Logger logger = LoggerFactory.getLogger(ChannelDataAppendedParser.class);
	private SpecificDatumReader<ChannelDataAppended> channelAppendedReader;
	private ChannelDataAppended reuseCda;
	private static final String CHANNEL_DATA_APPENDED_NAME = ChannelDataAppended.class.getName();
	
	//private DatumReader<ChannelDataChanged> channelChangedReader;
	//private ChannelDataChanged reuseCdc;
	//private static final String CHANNEL_DATA_CHANGED_NAME = ChannelDataChanged.class.getName();

	/**
	 * Contructor for the parser.
	 */
	public ChannelDataAppendedParser() {
		reloadSchema();
		reuseCda = new ChannelDataAppended();
	}
	
	/**
	 * Reload Avro Schema.
	 */
	public final void reloadSchema() {
		logger.trace("Reloading Schema");
		// Create Avro datum reader for the type
	    channelAppendedReader = new SpecificDatumReader<ChannelDataAppended>(ChannelDataAppended.getClassSchema());
	    
	    //DatumReader<ChannelDataChanged> readerCdc = 
	    //		new SpecificDatumReader<ChannelDataChanged>(ChannelDataChanged.getClassSchema());
	    //channelChangedReader = readerCdc;
	    logger.trace("Reloaded Schema");
	}

	/**
	 * Load Parser parameters from the Parser Element in the XML.
	 * @param e The parser XML Element and the parent Component Object either a Node or Trigger object
	 */
	@Override
	public void load(Element e, Resource parent) {
		// No implementation
		logger.info("Loading Configuration");
	}
	
	/**
	 * @return The Object representing the parsed source.
	 * @param context Context Dictionary
	 * @param source The source Object containing the data to be parsed
	 */
	@Override
	public final Object parse(final Map<String, Object> context, final Object source) throws EngineException {
	
		//When needed for trace logging
		if (logger.isTraceEnabled()) {
			Trigger trigger = ParserContext.getTrigger(context);
			String triggerId = "Unknown, Null Trigger found";
			if (trigger != null) { 
				triggerId = trigger.getTriggerId(); 
			}
			logger.trace("TriggerId = {} Context = {} \n Object= {}", triggerId, context, source);
		}
		
		Map<String, Object> properties = ParserContext.getAmqpBasicProperties(context);
		String sourceTypeString = AmqpBasicProperties.getType(properties);
		
		if (!(source instanceof byte[])) {
			throw new EngineException("Unexpected source data type");
		}
		byte[] inputBytes = (byte[]) source;
		
		Object parsedObject = null;
		try {
			// Create BinaryDecoder for our input byte array
			BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(inputBytes, null);

			if (CHANNEL_DATA_APPENDED_NAME.equals(sourceTypeString)) {
				ChannelDataAppended cda = channelAppendedReader.read(reuseCda, decoder);
				parsedObject = MessageConverter.toInternalModel(cda);
			
			//} else if(CHANNEL_DATA_CHANGED_NAME.equals(sourceTypeString)) {
			//	ChannelDataChanged cdc = channelChangedReader.read(reuseCdc, decoder);
			//	parsedObject = RtEventTransformer.ToInternalModel(cdc);
			} else {
				//Aristo: I suggest just ignore parsing failure , 
				//throwing exception is expensive if it happens often, 
				//This exchange has many form of messages
				//If needed , just put debug
				//throw new EngineException("Unknown incoming type "+sourceTypeString);
				logger.trace("Unknown incoming type: {}", sourceTypeString);
			}
		}
		catch (IOException e) {
			throw new EngineException("Failure during parsing " + sourceTypeString, e);
		}

		return parsedObject;
	}
}

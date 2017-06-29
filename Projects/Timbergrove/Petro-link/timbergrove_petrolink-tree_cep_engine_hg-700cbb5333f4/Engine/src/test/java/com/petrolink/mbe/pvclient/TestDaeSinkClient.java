package com.petrolink.mbe.pvclient;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import Petrolink.Microservice.DAE.ChannelDataValues;



public class TestDaeSinkClient {

	DaeSinkClient client;
	
	private BinaryDecoder decoder;
	private SpecificDatumReader<ChannelDataValues> channelDataValuesReader;
	private ChannelDataValues cdv;
	
	public TestDaeSinkClient() {
		client = new DaeSinkClient(null);
		channelDataValuesReader = new SpecificDatumReader<ChannelDataValues>(ChannelDataValues.getClassSchema());
	}
	
	@Test
	public void testModel() throws IOException {
		String testData1 = "FlRccrgSSCucuCCfgFBpEQICAICI0vyPzqIFAAAAAAAAAAAABpqZmZmZmSRAAAA=";
		byte[] bytes = Base64.decodeBase64(testData1);
		ChannelDataValues decoded = decode(bytes);
		
		byte[] reencoded =client.avroEncode(decoded);
		String reencodedString = Base64.encodeBase64String(reencoded);
		
		assertEquals(testData1, reencodedString);
	}
	
	public ChannelDataValues decode(byte[] encoded) throws IOException {
		// Reuse binary decoder and ChannelDataAppended objects
		decoder = DecoderFactory.get().binaryDecoder(encoded, decoder);
		cdv = channelDataValuesReader.read(cdv, decoder);
		return cdv;
	}

}

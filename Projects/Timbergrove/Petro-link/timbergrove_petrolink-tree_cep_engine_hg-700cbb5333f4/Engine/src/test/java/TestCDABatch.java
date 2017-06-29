import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.petrolink.mbe.actions.rmq.ChannelDataAppendedAmqpCodec;
import com.petrolink.mbe.amqp.AmqpExchange;
import com.petrolink.mbe.amqp.AmqpMessage;
import com.petrolink.mbe.amqp.AmqpPublisher;
import com.petrolink.mbe.amqp.AmqpUtil;
import com.petrolink.mbe.engine.CommonConfigForTests;
import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.util.SampleDataGenerator;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.smartnow.rabbitmq.util.RMQConnectionSettings;

/**
 * Testing ChannelData Appended batch (many rows in single update)
 * @author aristo
 *
 */
public class TestCDABatch {
	
	
	private Connection rmqConnection;
	private String rmqUri;
	private AmqpExchange exchangeDefinition;
	private AmqpPublisher publisher ;
	
	/**
	 * Constructor
	 */
	public TestCDABatch(){
		RMQConnectionSettings pvrt = CommonConfigForTests.getRealtimeChannelRMQSetting();
		AmqpExchange exchange = new AmqpExchange(pvrt.getExchange(), pvrt.getExchangeType());
		exchange.setDurable(pvrt.isDurable());
		exchangeDefinition = exchange;
		
		rmqUri = CommonConfigForTests.getRMQUri();
		
	}
	
	/**
	 * Connect to RMQ and prepares
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public void connect() throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, TimeoutException {
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUri(rmqUri);
		
		Connection connection = factory.newConnection();
		
		Channel channel = connection.createChannel();
		AmqpUtil.declare(channel, exchangeDefinition);
		publisher = new AmqpPublisher(channel,exchangeDefinition);
		rmqConnection = connection;
	}
	
	/**
	 * Send channel data appended to specified well and channel using supplied data points
	 * @param wellId
	 * @param chId
	 * @param dps
	 * @throws Exception
	 */
	public void sendCdaBatch(String wellId, String chId, List<DataPoint> dps) throws Exception {
		ChannelDataAppendedAmqpCodec converter =
				new ChannelDataAppendedAmqpCodec(wellId,chId);
		
		AmqpMessage message = converter.encodeDataPoints(dps);
		sendCdaBatch(message);
	}
	
	/**
	 * Send channel data appended message
	 * @param message
	 * @throws IOException
	 */
	public void sendCdaBatch(AmqpMessage message) throws IOException{
		publisher.publish(message);
	}
	

	
	/**
	 * Exit connections to RMQ
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public void exit() throws IOException, TimeoutException {
		publisher.close();
		rmqConnection.close();
	}

	/**
	 * Get Human readable description of dataPoints
	 * @param dps
	 * @return  Human readable description
	 */
	public static String getRangeString(List<DataPoint> dps) {
		return "List<DataPoint> range: " + dps.get(0).getIndex() + " to " + dps.get(dps.size()-1).getIndex()+ ", count:"+dps.size();
	}
	
	/**
	 * Get Human readable description of dataPoints
	 * @param blocks
	 * @return  Human readable description
	 */
	public static String getRangeString(ArrayList<List<DataPoint>> blocks) {
		List<DataPoint> firstblock = blocks.get(0);
		List<DataPoint> lastblock = blocks.get(blocks.size()-1);
		
		return "List<DataPoint> range: " + firstblock.get(0).getIndex() + " to " + lastblock.get(lastblock.size()-1).getIndex()+ ", count:"+blocks.size();
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		Double[] repeatValue1 = { 60.00, 40.00};
		Double[] repeatValue2 = { 70.00, 40.00};
		Duration stepping1 = Duration.ofSeconds(1);
		int dataCount1 = 100000;
		int blockSize1 = 600;
		Duration stepping2 = Duration.ofSeconds(1);
		int dataCount2 = 100000;
		int blockSize2 = 600;
		
		OffsetDateTime startDtim = OffsetDateTime.of(2000, 10, 10, 10, 0, 1, 0, ZoneOffset.UTC);
		
		//Default stepping
		startDtim =  OffsetDateTime.now().minus(stepping1.multipliedBy(dataCount1));
		
		//ArrayList<DataPoint> dps =  SampleDataGenerator.generateDataPoints(null, Duration.ofSeconds(1),1000,repeatValue);
		//System.out.println(getRangeString(dps));
		
		
		ArrayList<List<DataPoint>> blocks1 =  SampleDataGenerator.generateDataPointsInBlocks(startDtim, stepping1,dataCount1,repeatValue1,blockSize1);
		ArrayList<List<DataPoint>> blocks2 =  SampleDataGenerator.generateDataPointsInBlocks(startDtim, stepping2,dataCount2,repeatValue2,blockSize2);
		System.out.println(getRangeString(blocks1));
		System.out.println(getRangeString(blocks2));
		
//		int startBlock = 0;
//		int endBlock = startBlock + blockSize;
//		int maxDps = dps.size();
//		ArrayList<List<DataPoint>> blocks = new ArrayList<List<DataPoint>>();
//		while(startBlock < maxDps) {
//			if (endBlock > maxDps) endBlock = maxDps;
//			List<DataPoint> dpsBlock = dps.subList(startBlock, endBlock);
//			System.out.println("Created Block "+startBlock+" to "+endBlock);
//			blocks.add(dpsBlock);
//			startBlock = endBlock;
//			endBlock = endBlock + blockSize;
//		}
		
		
		String wellId = "81549d90-7a99-11e6-bdf4-0800200c9a66";
		String chId= "5037f119-2d45-4106-a3c7-2fed671c1dcc";
		String chId2= "6037f119-2d45-4106-a3c7-2fed671c1dcc";
		ChannelDataAppendedAmqpCodec converter =
				new ChannelDataAppendedAmqpCodec(wellId, chId);
		ChannelDataAppendedAmqpCodec converter2 =
				new ChannelDataAppendedAmqpCodec(wellId, chId2);
		
		
		
		TestCDABatch test = new TestCDABatch();
		test.connect();
		
		for (int i = 0; i < blocks1.size(); i++) {
			List<DataPoint> block1 =  blocks1.get(i);
			System.out.println(getRangeString(block1));
			AmqpMessage blockMessage1 = converter.encodeDataPoints(block1);
			test.sendCdaBatch(blockMessage1);
		}
		
		for (int i = 0; i < blocks2.size(); i++) {
			List<DataPoint> block2 =  blocks2.get(i);
			System.out.println(getRangeString(block2));
			AmqpMessage blockMessage2 = converter2.encodeDataPoints(block2);
			test.sendCdaBatch(blockMessage2);
		}
		
//		int b1Size = blocks1.size();
//		int b2Size = blocks2.size();
//		int size = Math.max(b1Size, b2Size);
//		for (int i = 0; i < size; i++) {
//			AmqpMessage blockMessage1 = null;
//			if (i < b1Size) {
//				List<DataPoint> block1 =  blocks1.get(i);
//				System.out.println(getRangeString(block1));
//				blockMessage1 = converter.encodeDataPoints(block1);
//			}
//			
//			AmqpMessage blockMessage2 = null;
//			if (i <b2Size) {
//				List<DataPoint> block2 =  blocks2.get(i);
//				System.out.println(getRangeString(block2));
//				blockMessage2 = converter2.encodeDataPoints(block2);
//			}
//			
//			if (blockMessage1 != null) {	test.sendCdaBatch(blockMessage1); }
//			if (blockMessage2 != null) {	test.sendCdaBatch(blockMessage2); }
//		}
//		
		test.exit();
	}
}

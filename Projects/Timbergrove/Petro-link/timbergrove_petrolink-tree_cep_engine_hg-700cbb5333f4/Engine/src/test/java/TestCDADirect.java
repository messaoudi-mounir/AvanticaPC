import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.collections4.map.HashedMap;

import com.petrolink.mbe.parser.MessageConverter;
import com.petrolink.mbe.util.UUIDHelper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import Petrolink.Datatypes.DataAttribute;
import Petrolink.Datatypes.DataValue;
import Petrolink.Datatypes.IndexValue;
import Petrolink.WITSML.Datatypes.ChannelDataItem;
import Petrolink.WITSML.Datatypes.DataItem;
import Petrolink.WITSML.Events.ChannelDataAppended;

/**
 * Test Channe Data Appended Directly using standard input
 * @author Paul Solano
 * @author langj
 * @author aristo
  */
public class TestCDADirect {
	private static final String EXCHANGE_NAME = "PetroVault.Realtime.Raw";
	private static final String EXCHANGE_TYPE = "topic";
	private static String defaultWellId;
	
	/**
	 * Main Application method.
	 * @param args
	 * @throws java.io.IOException 
	 * @throws TimeoutException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws java.io.IOException, TimeoutException, InterruptedException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(args[0]);
		String user, pass, wellid;
		if (args.length == 5) {
			factory.setPort(Integer.parseInt((args[1])));
			user = args[2];
			pass = args[3];
			wellid = args[4];
			defaultWellId = args[4];
		} else {
			user = args[1];
			pass = args[2];
			wellid = args[3];			
			defaultWellId = args[3];
		}
		
		factory.setUsername(user);
		factory.setPassword(pass);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);

		while (true) {
			String chId, idx, val;
			String line = sc.nextLine();

			if ("exit".compareTo(line) == 0)
				break;

			String[] lineParts = line.split(",");

			if (lineParts.length == 4) {
				wellid = lineParts[0];
				chId = lineParts[1]; 
				idx = lineParts[2]; 
				val = lineParts[3];
			} else {
				wellid = defaultWellId;
				chId = lineParts[0]; 
				idx = lineParts[1]; 
				val = lineParts[2];				
			}
			
			channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE, true);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);

			DatumWriter<ChannelDataAppended> writer = new SpecificDatumWriter<ChannelDataAppended>(
					ChannelDataAppended.getClassSchema());
			
			UUID channelId = UUID.fromString(chId);
			
			Object index = null;
			if (idx.contains("T")) {
				index = MessageConverter.toMessageModel(OffsetDateTime.parse(idx));
			} else if (idx.contains(".")) {
				index = Double.parseDouble(idx);
			} else {
				index = Long.parseLong(idx);
			}
			
			writer.write(makeChannelDataAppended(channelId, index, Double.parseDouble(val)), encoder);
			encoder.flush();
			out.close();

			byte[] serializedBytes = out.toByteArray();

			//
			// Build and send the message
			//

			Builder b = new AMQP.BasicProperties.Builder();

			b.contentType("avro/binary");
			b.type(ChannelDataAppended.class.getName()); // Petrolink.WITSML.Events.ChannelDataAppended
			Map<String, Object> headers = new HashedMap<>();
			headers.put("uuid", wellid);
			b.headers(headers);
			
			//String routingKey = "cda." + args[3] + "." + chId;
			//String routingKey = wellid + ".channel.dataappended." + chId;
			String routingKey = "'" + chId + "'.channel.dataappnded.'" + wellid + "'";
			
			System.out.println("Submitted to topic " + routingKey);
			channel.basicPublish(EXCHANGE_NAME, routingKey, b.build(), serializedBytes);
		}

		channel.close();
		connection.close();
	}

	private static ChannelDataAppended makeChannelDataAppended(UUID uuid, Object index, Double value) {
		Petrolink.Datatypes.UUID channelId = new Petrolink.Datatypes.UUID(
				UUIDHelper.toBytes(uuid));

		IndexValue iv = new IndexValue();
		iv.setItem(index);

		DataValue dv = new DataValue();
		dv.setItem(value);

		ArrayList<IndexValue> indices = new ArrayList<IndexValue>();
		indices.add(iv);

		DataItem di = new DataItem();
		di.setIndexes(indices);
		di.setValue(dv);
		di.setValueAttributes(new ArrayList<DataAttribute>());

		ChannelDataItem cdi = new ChannelDataItem();

		cdi.setId(channelId);
		cdi.setItem(di);

		ChannelDataAppended cda = new ChannelDataAppended();

		ArrayList<ChannelDataItem> cdal = new ArrayList<ChannelDataItem>();
		cdal.add(cdi);
		cda.setData(cdal);

		return cda;
	}
}

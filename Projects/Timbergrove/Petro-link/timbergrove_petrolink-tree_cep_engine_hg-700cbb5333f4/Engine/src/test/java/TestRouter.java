import java.io.ByteArrayOutputStream;
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

@SuppressWarnings("javadoc")
public class TestRouter {
	private static final String EXCHANGE_NAME = "PetroVault.Realtime.Raw";
	
	public static void main(String[] args) throws java.io.IOException, TimeoutException, InterruptedException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(args[0]);
		factory.setUsername(args[1]);
		factory.setPassword(args[2]);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);

		while (true) {
			String line = sc.nextLine();

			if ("exit".compareTo(line) == 0)
				break;

			String[] lineParts = line.split(",");
			
			channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);

			DatumWriter<ChannelDataAppended> writer = new SpecificDatumWriter<ChannelDataAppended>(
					ChannelDataAppended.getClassSchema());

			writer.write(makeChannelDataAppended(lineParts[0], Double.parseDouble(lineParts[1]),
					Double.parseDouble(lineParts[2])), encoder);
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
			headers.put("uuid", args[3]);
			b.headers(headers);

			channel.basicPublish(EXCHANGE_NAME, "", b.build(), serializedBytes);
		}

		channel.close();
		connection.close();
	}

	private static ChannelDataAppended makeChannelDataAppended(String uuid, Double index, Double value) {
		Petrolink.Datatypes.UUID channelId = new Petrolink.Datatypes.UUID(
				UUIDHelper.toBytes(UUID.fromString(uuid)));

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

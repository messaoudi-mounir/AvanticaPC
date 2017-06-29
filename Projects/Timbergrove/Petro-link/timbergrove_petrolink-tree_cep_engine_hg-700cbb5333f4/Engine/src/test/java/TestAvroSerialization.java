import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.specific.SpecificDatumWriter;

import com.smartnow.engine.event.Event;

@SuppressWarnings("javadoc")
public class TestAvroSerialization {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Schema schema = ReflectData.get().getSchema(Event.class);
		System.out.println("extracted avro schema: " + schema);

		GenericRecord ev1 = new GenericData.Record(schema);
		ev1.put("uid", "asdfsdfasdf123");
		ev1.put("classId", "type1");
		ev1.put("source", "test");
		ev1.put("timestamp", System.currentTimeMillis());
		ev1.put("retryCount", 0);
		ev1.put("contentType", 1);
		ev1.put("priority", 1);
		ev1.put("status", 1);
		ev1.put("executed", new Long(0));
		ev1.put("extUids", new ArrayList<Object>());
		ev1.put("children", new ArrayList<Object>());

		// Serialize event to disk
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);

		DatumWriter<GenericRecord> writer = new SpecificDatumWriter<GenericRecord>(schema);
		writer.write(ev1, encoder);
		encoder.flush();
		out.close();
		byte[] serializedBytes = out.toByteArray();

		System.out.println(out);

	}

}

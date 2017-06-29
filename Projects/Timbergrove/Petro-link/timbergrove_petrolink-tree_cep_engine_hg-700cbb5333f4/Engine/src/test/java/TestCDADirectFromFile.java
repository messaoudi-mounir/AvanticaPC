import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
 * Test Channe Data Appended using excel File. 
 * @author Paul Solano
 * @author langj
 * @author aristo
  */
public class TestCDADirectFromFile {
	private static final String EXCHANGE_NAME = "PetroVault.Realtime.Raw";
	private static final String EXCHANGE_TYPE = "topic";
	private static final long CYCLE_WAIT = 500;
	private static List<UUID> channelIds = new ArrayList<UUID>();

	/**
	 * Main Application method.
	 * @param args
	 * @throws java.io.IOException 
	 * @throws TimeoutException 
	 * @throws InterruptedException 
	 * @throws InvalidFormatException 
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args)
			throws java.io.IOException, TimeoutException, InterruptedException, InvalidFormatException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(args[0]);
		factory.setUsername(args[1]);
		factory.setPassword(args[2]);
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		Path xlsfile = null;
		if (args.length == 5) {
			xlsfile = Paths.get(System.getProperty("user.dir") + "/" + args[4]);
		} else {
			Scanner sc = new Scanner(System.in);
			String fname = sc.nextLine();
			xlsfile = Paths.get(System.getProperty("user.dir") + "/" + fname);
		}

		if (Files.exists(xlsfile)) {
			// Use file input stream to ensure the workbook load is read-only
			XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(xlsfile.toFile()));
			// Get first sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);

			// Reading headers
			Row hrow = sheet.getRow(0);

			for (int i = 1; i < 100; i++) {
				Cell channelId = hrow.getCell(i);
				if ((channelId == null) || (channelId.getCellType() == Cell.CELL_TYPE_BLANK)
						|| (channelId.getStringCellValue() == null)
						|| (channelId.getStringCellValue().compareTo("") == 0)) {
					break;
				} else {
					// Load Channel Ids
					channelIds.add(UUID.fromString(channelId.getStringCellValue()));
				}
			}

			// Obtain Value Types

			channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE, true);

			for (int i = 1; i < 50000; i++) {
				Row set = sheet.getRow(i);

				if (set == null)
					break;

				Cell idxCell = set.getCell(0);
				if ((idxCell == null) || (idxCell.getCellType() == Cell.CELL_TYPE_BLANK)) {
					break;
				} else if ((idxCell.getCellType() == Cell.CELL_TYPE_STRING)
						&& ((idxCell.getStringCellValue() == null) || (idxCell.getStringCellValue().equals("")))) {
					break;
				}

				Object index;
				if (idxCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					index = idxCell.getNumericCellValue();
				} else {
					String idxStringValue = idxCell.getStringCellValue();
					// Converting idx String to DateTime
					// TODO
					OffsetDateTime idxParsed = OffsetDateTime.parse(idxStringValue);

					// OffsetDateTime idx =
					// idxParsed.withOffsetSameInstant(ZoneOffset.UTC);
					//
					// long microSecondsToEpoch
					// =ChronoUnit.MICROS.between(EPOCH,idx);
					// //Aristo : Verify, showing how it works
					// //OffsetDateTime idx2 = EPOCH.plus(microSecondsToEpoch,
					// ChronoUnit.MICROS);
					// //String verfyIdx2 = idx2.toString();
					// //boolean isDateTimeEqual = idx.isEqual(idx2);
					// //In Avro Schema of petrolink Datetime, long integer
					// value representing the number of *microseconds* from the
					// epoch - Jan 1, 1970.
					// //do not use OffsetDateTime.toEpochSecond()
					// DateTime dt = new DateTime();
					// dt.setTime(microSecondsToEpoch);
					// index = dt;

					// Aristo:Helper implementation
					index = MessageConverter.toMessageModel(idxParsed);
				}

				for (int j = 1; j < channelIds.size() + 1; j++) {
					Cell channelValue = set.getCell(j);

					if ((channelValue != null) && (channelValue.getCellType() != Cell.CELL_TYPE_BLANK)
							&& (channelValue.getCellType() == Cell.CELL_TYPE_NUMERIC)) {
						Double value = channelValue.getNumericCellValue();

						Builder b = new AMQP.BasicProperties.Builder();

						b.contentType("avro/binary");
						b.type(ChannelDataAppended.class.getName()); // Petrolink.WITSML.Events.ChannelDataAppended
						Map<String, Object> headers = new HashedMap<>();
						headers.put("uuid", args[3]);
						b.headers(headers);

						System.out.println(makeChannelDataAppended(channelIds.get(j - 1), index, value).toString());
						
						//String routingKey = "cda." + args[3] + "." + channelIds.get(j - 1);
						//String routingKey = args[3] + ".channel.dataappended." + channelIds.get(j - 1);

						String routingKey = "'" + channelIds.get(j - 1) + "'.channel.dataappnded.'" + args[3] + "'";
						
						channel.basicPublish(EXCHANGE_NAME, routingKey, b.build(),
								getChannelDataAppendedBytes(channelIds.get(j - 1), index, value));
					}
				}

				Thread.sleep(CYCLE_WAIT);
			}

			workbook.close();
			System.out.println(" [x] Sent!!! ");

		}

		channel.close();
		connection.close();
	}

	private static ChannelDataAppended makeChannelDataAppended(UUID cid, Object index, Object value) {
		Petrolink.Datatypes.UUID channelId = new Petrolink.Datatypes.UUID(UUIDHelper.toBytes(cid));

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

	private static byte[] getChannelDataAppendedBytes(UUID channelId, Object index, Object value)
			throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);

		DatumWriter<ChannelDataAppended> writer = new SpecificDatumWriter<ChannelDataAppended>(
				ChannelDataAppended.getClassSchema());

		writer.write(makeChannelDataAppended(channelId, index, value), encoder);
		encoder.flush();
		out.close();

		byte[] serializedBytes = out.toByteArray();
		return serializedBytes;

	}
}

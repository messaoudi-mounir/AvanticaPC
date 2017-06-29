import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.petrolink.mbe.engine.CommonConfigForTests;
import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.pvclient.ChronosClient;
import com.petrolink.mbe.pvclient.ChronosClient.SimpleChannelMetadata;
import com.petrolink.mbe.setting.HttpBasicAuthentication;
import com.petrolink.mbe.util.DateTimeHelper;

// PVCLOUD2 Stuff
//   EVAL channel: aa76b54d-c963-4818-a69b-174352c9edf0 eml://witsml141/well(timbergrove)/wellbore(timbergrove)/log(eval01)/channel(eval)
//   Timbergrove well: bb57e0a2-65e1-41e9-b9ef-7cf4be17f4a7 eml://witsml141/well(timbergrove)

@SuppressWarnings("javadoc")
public class TestPVAPI {
	public static void main(String[] args) {
		
		HttpBasicAuthentication auth = CommonConfigForTests.getBasicAuth();
		String baseUri = CommonConfigForTests.getCommonPetroVaultUri("/HDAPI/");
		ChronosClient c = new ChronosClient(baseUri,auth );
		
		try {
			UUID channelId = UUID.fromString("aa76b54d-c963-4818-a69b-174352c9edf0");
			
			ChronosClient.SimpleChannelMetadata result = c.getChannelById(channelId);
			
			//ChannelMetadataRecord m = ApiConverter.toChannelMetadataRecord(result);
						
			System.out.println(result.getName());
			OffsetDateTime startIndex = DateTimeHelper.fromEpochMicros(1470927908000000L);
			OffsetDateTime endIndex = DateTimeHelper.fromEpochMicros(1475623288895000L);
			
			List<DataPoint> rangeResult = c.getChannelDataAsDataPoints(channelId, startIndex, endIndex);
			
			System.out.println(rangeResult != null && rangeResult.size() != 0 ? rangeResult.get(0) : "nothing!");
			//c.updateChannel(UUID.fromString("aa76b54d-c963-4818-a69b-174352c9edf0"), DateTimeHelper.toEpochMicros(OffsetDateTime.now()), 100);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

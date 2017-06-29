import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import Petrolink.SharedSet.StatusIconFillSet;

/**
 * Test Rig state parsing (StatusIconFillSet).
 * @author aristo
 */
public class TestRigStateParsing {
	/**
	 * Main Application method.
	 * @param args
	 */
	public static void main(String[] args)  {
		Path currentPath = Paths.get(System.getProperty("user.dir"));
		System.out.println("Working Directory = " + currentPath.toString() );
		
		Path activityCodePath = currentPath.resolve("src/main/resources/SharedSets/ActivityCode");
		System.out.println("Activity Code Path = " + activityCodePath.toString() );
		
		Path targetDict = activityCodePath.resolve("ActivityCode.xml");
		File targetDictFile = targetDict.toFile();
		
		try {
			
			JAXBContext jaxbContext = JAXBContext.newInstance(StatusIconFillSet.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			jaxbUnmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
			
			StatusIconFillSet result = 
					(StatusIconFillSet) jaxbUnmarshaller.unmarshal(targetDictFile);
			
			System.out.println("Found Object = " + result + " contains " + result.getStatusIconFills().size() + " fill(s) ");
			
		} catch (Exception e) {
			System.out.println("Fail to load file= " + e.toString());
		} 
		
		
		
	}
}

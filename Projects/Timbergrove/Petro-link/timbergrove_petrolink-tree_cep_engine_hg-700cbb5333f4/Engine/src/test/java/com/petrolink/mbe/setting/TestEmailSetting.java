package com.petrolink.mbe.setting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.petrolink.mbe.setting.EmailSetting.GuidAddressMappings;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.util.NamedValueResource;

/**
 * Test Email Setting.
 * @author aristo
 */
@SuppressWarnings("javadoc")
public class TestEmailSetting {
	Path xmlPath;
	SAXBuilder builder;
	
	@Before
	public void setUp() throws Exception {
		xmlPath = Paths.get(System.getProperty("user.dir"),"src","test","resources","settings");
		boolean testPathAvailable  = Files.exists(xmlPath);
		assertTrue("Path Must be Available "+xmlPath.toString(), testPathAvailable);
		
		builder = new SAXBuilder();
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testBasicNamedValueResourceUsage() {
		NamedValueResource resource = new NamedValueResource();
		resource.put("Host","localhost");
		resource.put("Port","1025");
		resource.put("User","testUser");
		resource.put("Password","testPassword");
		resource.put("sslEnabled","true");
		resource.put("sslCheckServerIdentity",false);
		resource.put("startTlsEnabled","truE");
		resource.put("startTlsRequired","True");
		
		SMTPConnectionSettings setting1 = new SMTPConnectionSettings(resource);
		assertEquals(setting1.getSmtpPort(), 1025);
		assertTrue(setting1.isSslEnabled());
		assertFalse(setting1.isSslCheckServerIdentity());
		assertTrue(setting1.isStartTlsEnabled());
		assertTrue(setting1.isStartTlsRequired());
	}
	
	@Test
	public void testBasicContentType() {
		EmailSetting setting = new EmailSetting();
		assertTrue(setting.isHtmlEmail());
		assertFalse(setting.isPlainTextEmail());
		setting.setContentType("text/plain");
		assertFalse(setting.isHtmlEmail());
		assertTrue(setting.isPlainTextEmail());
		setting.setContentType("text/html");
		assertTrue(setting.isHtmlEmail());
		assertFalse(setting.isPlainTextEmail());
		
		
	}

	@Test
	public void testComboContentType() {
		EmailSetting setting = new EmailSetting();
		
		setting.setContentType("text/plain charset=UTF-8");
		assertFalse(setting.isHtmlEmail());
		assertTrue(setting.isPlainTextEmail());
		
		setting.setContentType("text/html charset=UTF-8");
		assertTrue(setting.isHtmlEmail());
		assertFalse(setting.isPlainTextEmail());
	}
	
	@Test
	public void testMailTemplate1() throws EngineException, JDOMException, IOException {
		//template with in file connection
		EmailSetting setting = loadSmtpSetting("emailSettingMailTemplate1.xml",null);
		assertNotNull(setting);
		
		assertNotNull(setting.getConnectionSetting());
		assertTrue(setting.isHtmlEmail());
		assertTrue("The xml file must be Email",setting.getGuidRecipientsMappingMode().equals(GuidAddressMappings.EMAIL));
		
		if (setting.getMissingSettings().size() != 1) {
			for (String missingEntry : setting.getMissingSettings()) {
				System.out.println(missingEntry);
			}
			fail("Should only have one problem");
		}
	}
	
	@Test
	public void testMailTemplate2() throws EngineException, JDOMException, IOException {
		//template with reference connection
		EmailSetting setting = loadSmtpSetting("emailSettingMailTemplate2.xml",null);
		assertNotNull(setting);
		
		assertNull("The file has connection id , should not load",setting.getConnectionSetting());
		assertNotNull("The file has connection ref", setting.getConnectionSettingRef());
		assertTrue(setting.isHtmlEmail());
		assertTrue("The xml file must be Email",setting.getGuidRecipientsMappingMode().equals(GuidAddressMappings.EMAIL));
		
		if (setting.getMissingSettings().size() != 2) {
			for (String missingEntry : setting.getMissingSettings()) {
				System.out.println(missingEntry);
			}
			fail("Should only have one problem");
		}
	}
	
	@Test
	public void testMailTemplate3() throws EngineException, JDOMException, IOException {
		//Sms template
		EmailSetting setting = loadSmtpSetting("emailSettingMailTemplate3.xml",null);
		assertNotNull(setting);
		
		assertNotNull(setting.getConnectionSetting());
		assertNull("The file does not have connection ref", setting.getConnectionSettingRef());
		assertTrue(setting.isPlainTextEmail());
		assertTrue("The xml file must be SMS",setting.getGuidRecipientsMappingMode().equals(GuidAddressMappings.SMS_PHONE_EMAIL));
		
		if (setting.getMissingSettings().size() != 1) {
			for (String missingEntry : setting.getMissingSettings()) {
				System.out.println(missingEntry);
			}
			fail("Should only have one problem");
		}
		
	}
	
	/**
	 * Helper method to load xml setting for email
	 * @param fileName
	 * @param childElementName
	 * @return
	 * @throws EngineException
	 * @throws JDOMException
	 * @throws IOException
	 */
	public EmailSetting loadSmtpSetting(String fileName,String childElementName)  throws EngineException, JDOMException, IOException{
		File configFile = xmlPath.resolve(fileName).toFile();
		assertTrue("File Must be available: "+fileName, configFile.exists());
		
		Document ruleDocument  = (Document) builder.build(configFile);
		Element mainElement = null;
		Element rootElement = ruleDocument.getRootElement();
		if(childElementName != null) {
			Element childElement = rootElement.getChild(childElementName, rootElement.getNamespace());
			mainElement = childElement;
		} else {
			mainElement = rootElement;
		}
		
		return XmlSettingParser.parseEmailSetting(mainElement, null);
	}
}

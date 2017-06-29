/**
 * 
 */
package com.petrolink.mbe.actions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.petrolink.mbe.alertstatus.impl.AlertImpl;
import com.petrolink.mbe.rulesflow.RuleFlow;
import com.petrolink.mbe.setting.SendMailActionSetting;
import com.petrolink.mbe.setting.XmlSettingParser;
import com.petrolink.mbe.templates.MailTemplate;
import com.petrolink.mbe.templates.NotificationTemplateFactory;
import com.petrolink.mbe.templates.NotificationTemplateService;
import com.petrolink.mbe.util.EmailHelper;

/**
 * @author aristo
 *
 */
public class TestSendMailAction {
	private NotificationTemplateService templateService;
	private SendMailAction action;
	private final Logger logger = LoggerFactory.getLogger(TestSendMailAction.class);
	
	private boolean isSendToActualServer = false;
	
	private Element prepareXmlElement(final String config) {
		
		SAXBuilder builder = new SAXBuilder();
		Element root = null;
		try {
			Document doc = builder.build(new StringReader(config));
			root = doc.getRootElement();		

		} catch (JDOMException | IOException e) {
			logger.error("Failure Loading configuration from string " + config, e);
		}
		return root;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		//Prepare Template
		NotificationTemplateService currentTemplateService = new NotificationTemplateService();
		currentTemplateService.loadSetting(null); //no config needed for NotificationTemplateService
		currentTemplateService.startServiceFromInternalSetting();
		
		String hostname = "127.0.0.1";
		//hostname = "10.5.16.1";
		hostname = "mail3.petrolink.net";
		MailTemplate defaultTemplate = generateTemplate(null, null, hostname);
		currentTemplateService.storeTemplate(defaultTemplate);
		//If you need to have connection setting you can use
		//defaultTemplate.getModel().getConnectionSetting();
		
		//Setup Action
		SendMailAction newAction = new SendMailAction();
		newAction.setNotificationTemplateService(currentTemplateService);
		
		SendMailActionSetting config = generateActionConfig(defaultTemplate.getUUID(), "72ef7cda-11ed-4f3b-ae1d-b02356292124,sdssdfsd@test.com, aristo@petrolink.com, testtes@petrolink.com");
		newAction.loadFromSetting(config);
		newAction.setFlow(new RuleFlow()); //Dummy, to avoid bAssert error
		
		templateService = currentTemplateService;
		action = newAction;
	}
	
	private SendMailActionSetting generateActionConfig(final UUID templateUUID, final String toCsv) {
		String emailSubject = "" + templateUUID;
		String emailActionConfiguration = "<SendMailNotification sequence=\"20\" "
				+ ((templateUUID == null)? StringUtils.EMPTY : ("template=\""+ templateUUID+ "\""))
				+ ">\r\n\t\t\t\t<From><![CDATA[Event Mailflow <petrolink.notification@petrolink.com>]]></From>\r\n\t\t\t\t<To>"
				+ toCsv
				+"</To>\r\n\t\t\t\t<CC></CC>\r\n\t\t\t\t<BCC>test@test.com</BCC>\r\n\t\t\t\t<Params>\r\n\t\t\t\t\t<Param name=\"subjectParam\" type=\"String\">" 
		+ emailSubject
		+ "</Param>\r\n\t\t\t\t\t<Param name=\"p1\" type=\"Integer\">5</Param>\r\n\t\t\t\t\t<Param name=\"closing\" type=\"String\">\r\n\t\t\t\t\t<![CDATA[\r\n\t\t\t\t\t<p>\r\n\t\t\t\t\tBest Regards, <br/>\r\n\t\t\t\t\t<br/>\r\n\t\t\t\t\tPetrolink<br/>\r\n\t\t\t\t\t</p>\r\n\t\t\t\t\t]]>\r\n\t\t\t\t\t</Param>\r\n\t\t\t\t</Params>\r\n\t\t\t\t<Subject>Unit Test: ${subjectParam}</Subject>\r\n\t            <Body>\r\n\t            <![CDATA[\r\n\t            ${opening},<br/>\r\n\t            <br/>\r\n\t            "
		+ "<p>\r\n\t            This email came from MBEMailFlow."
		+ "\r\n\t            The parameters are :\r\n\t            </p>\r\n\t            <p>\r\n\t\t            P1 = ${p1} <br/> \r\n\t\t            P2 = ${p2} <br/>\r\n\t            </p>\r\n\t            <p>\r\n\t\t            If variables are missing, for example, you can do like<br/>\r\n\t\t            \r\n\t\t            P3 = ${p3! \"Unknown value\"} <br/>\r\n\t            </p>\r\n\t            <p>\r\n\t\t            Others<br/>\r\n\t\t            P3 = ${details! \"No Details\"} <br/>\r\n\t\t            P3 = ${alert! \"No Alert\"} <br/>\r\n\t            </p>\r\n\t            ${closing}\r\n\t            ]]> \r\n\t            </Body>\r\n\t\t\t</SendMailNotification>";
		
		Element e = prepareXmlElement(emailActionConfiguration);
		SendMailActionSetting actionSetting = XmlSettingParser.parseSendMailActionSetting(e, logger);
		return actionSetting;
	}
	
	private MailTemplate generateTemplate(final UUID newUUID, final String contentType, final String serverHostname) throws Exception {
		UUID uuidTemplate = newUUID == null ? UUID.randomUUID() : newUUID;
		String contentTypeXml = StringUtils.isBlank(contentType) ? StringUtils.EMPTY 
				: ("\t<ContentType>" + contentType +"</ContentType>\r\n");
		String defaultTemplateXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<MailTemplate uuid=\""
		+ uuidTemplate
		+ "\" \r\n    xmlns=\"http://www.petrolink.com/mbe/rules\"\r\n\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\r\n\txsi:schemaLocation=\"http://www.petrolink.com/mbe/rules ../../xsd/templates.xsd\">\r\n\t"
		+"<Connection ssl=\"false\" sslCheckServerIdentity =\"false\">\r\n\t\t<Host>"
		+ serverHostname
		+ "</Host>"
		+ "\r\n\t\t<Port>25</Port>\r\n\t\t<!--\r\n\t\t<User>mbe</User>\r\n\t\t<Password>mbepassword</Password>\r\n\t\t<StartTLS required=\"false\">false</StartTLS>\r\n\t\t-->\r\n\t</Connection>\r\n\t<Params>\r\n\t\t<Param name=\"p1\" type=\"Integer\">3</Param>\r\n\t\t<Param name=\"p2\" type=\"Integer\">15</Param>\r\n\t\t<Param name=\"opening\" type=\"String\">Hola</Param>\r\n\t</Params>\r\n\t<From ><![CDATA[Event Engine <mbe@petrolink.com>]]></From>\r\n\t<Subject>Some Freemarker template ${p1}</Subject>\r\n"
		+ "\t<Body>Some other Freemarker template ${p2}</Body>\r\n"
		+ contentTypeXml
		+ "</MailTemplate>";
		Element defaultTemplateElement = prepareXmlElement(defaultTemplateXml);
		return (MailTemplate) NotificationTemplateFactory.parseTemplate(defaultTemplateElement);
	}
	
	/**
	 * Ensure ContentType can be read correctly
	 * @throws Exception
	 */
	@Test
	public void ensureCanReadContentTypes() throws Exception {
		String hostname = "127.0.0.1";
		MailTemplate template = generateTemplate(null, null, hostname);
		System.out.println(template.getModel().getContentType());
		assertFalse(template.getModel().isPlainTextEmail());
		assertTrue(template.getModel().isHtmlEmail());
		assertNull(template.getModel().getContentType());
		
		template = generateTemplate(null, "text/plain", hostname);
		System.out.println(template.getModel().getContentType());
		assertTrue(template.getModel().isPlainTextEmail());
		
		template = generateTemplate(null, "text/html", hostname);
		System.out.println(template.getModel().getContentType());
		assertTrue(template.getModel().isHtmlEmail());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		
		action.finalize(null);
		action = null;
		
		templateService.stopService();
	}
	
	/**
	 * Ensure one sending to server can be done correctly.
	 * @param context
	 * @throws Exception
	 */
	public void ensureOneOperation(final HashMap<String, Object> context) throws Exception {
		HashMap<String, Object> testContext = context != null
				? context
				: new HashMap<String, Object>();
		
		
		
		if (isSendToActualServer) {
			SendMailAction.sendEmailAutoRetry(action, testContext);
		} else {
			Email testEmail = action.generateEmail(testContext, null);
			assertNotNull(testEmail);
			String emailString = EmailHelper.getRfc822Message(testEmail);
			
			System.out.println(emailString);
		}
	}
	
	/**
	 * Ensure Multiple operation Can be done correctly.
	 * @throws Exception
	 */
	@Test
	public void ensureSeveralOperations() throws Exception {
		for (int i = 0; i < 5; i++) {
			HashMap<String, Object> testContext = new HashMap<String, Object>();
			testContext.put(MBEAction.CK_ALERT, generateAlert());
			ensureOneOperation(testContext);
		}
		
	}
	
	/**
	 * Generate dummy alert (just for testing functionality)
	 * @return
	 */
	private final AlertImpl generateAlert() {
		AlertImpl newAlert = new AlertImpl();
		newAlert.setLoaded(true);

		UUID alertID = UUID.randomUUID();
		newAlert.setClassId(alertID.toString());
		newAlert.setBitDepth(100.0);
		newAlert.setHoleDepth(100.0);
		newAlert.setClassification("UnnownClassfication");
		newAlert.setName("AnonymousAlert");
		newAlert.setTally(1);
		return newAlert;
	}
	/**
	 * Test method for {@link com.petrolink.mbe.actions.SendMailAction#getInstanceId()}.
	 */
	@Test
	public final void ensureInstanceIdNotNull() {
		assertNotNull(action.getInstanceId());
	}

}

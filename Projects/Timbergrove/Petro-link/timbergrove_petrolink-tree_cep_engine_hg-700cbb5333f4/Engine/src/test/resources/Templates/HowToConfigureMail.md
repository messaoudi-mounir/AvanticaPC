
#Configuring SendMailNotification#
SendMailNotification is an action which can send specified email based on specified format to specified server. 

To configure **SendMailNotification** you need to make sure that at minimum, the following are specified in Action or corresponding Template otherwise the `SendMailNotification` doesn't work and will cause loading error.
 
- Connection, 
- From
- Subject (be aware you can use Apache Freemarker Syntax)
- Body (be aware you can use Apache Freemarker Syntax)
- recipients (at least one in To, CC, From) 


> Note: Apache Freemarker syntax allow you to use supplied engine parameters with `${parameterName!"value when not available"}`

Remember in Timbergrove engine Data flows like this:

>    Trigger (will operate Parser when needed) > Flow (which will do actions as needed) 

So you need to add your SendMailNotification in one of the actions


## Example Flow Configuration ##
As an example, the following Trigger will take data from RabbitMQ queue to MailFlow as described in eventClassId, a flow which contains actions to write to email

    <?xml version="1.0" encoding="UTF-8"?>
    <Triggers>
    	<RabbitMQTrigger name="RT-RMQLocal" eventClassId="MailFlow" active="true">
     		<Connection><![CDATA[amqp://guest:guest@127.0.0.1:5672]]></Connection>
     		<!-- default Exchange type is direct and default durability is true -->
    		<Exchange type="topic" durable="true">PetroVault.Realtime.Raw</Exchange>
    		<!-- Routing Key is optional -->
    		<RoutingKey></RoutingKey>
    		<Queue>EventEnginesQueue</Queue>
    		<Parser class="petrolinkChannelDataAppended" />
    	</RabbitMQTrigger>
    </Triggers>

## Mail template ##
Mail configuration template is pre-configured Email configuration, its structure is similar to **SendMailNotification** action parameters. Similar Element will be overridden in Flow Configuration. In this example. This template will be useful for example defining default Mail Connection as described here. You will see later parameter named p1 is overridden by the example action while p2 is not

    <MailTemplate>
    	<Connection ssl="false" sslCheckServerIdentity ="false">
    		<Host>127.0.0.1</Host>
    		<Port>25</Port>
    		<!--
    		<User>mbe</User>
    		<Password>mbepassword</Password>
    		<StartTLS required="false">false</StartTLS>
    		-->
    	</Connection>
    	<Params>
    		<Param name="p1" type="Integer">3</Param>
    		<Param name="p2" type="Integer">15</Param>
    		<Param name="opening" type="String">Hola</Param>
    	</Params>
    	<From ><![CDATA[Event Engine <mbe@petrolink.com>]]></From>
    	<Subject>Some Freemarker template ${p1}</Subject>
    	<Body>Some other Freemarker template ${p2}</Body>
    </MailTemplate>

For details on connection you need to ask your mail server admin. generally you want port 25 for general plain SMTP or StartTLS and 465 for SSL. Be aware that in  SSL/TLS the Mail Server certificate should be resolvable by Java KeyStore.

##Mail Flow and the action##
The example MailFlow is included below. The Mailflow has two actions one of them is SendMailnotification Which will use `MailTemplateLocalhost` as Optional Mail Configuration template.


 
    <Flows>
    	<ActionsFlow name="MailFlow" load="true">
    		<Actions>
    			<SendMailNotification sequence="20" template="MailTemplateLocalhost">
    				<From><![CDATA[Event Mailflow <mbe@petrolink.com>]]></From>
    				<To>user1@example.com,user2@example.com</To>
    				<CC></CC>
    				<BCC>test@test.com</BCC>
    				<Params>
    					<Param name="subjectParam" type="String">Mailflow</Param>
    					<Param name="p1" type="Integer">5</Param>
    					<Param name="closing" type="String">
    					<![CDATA[
    					<p>
    					Best Regards, <br/>
    					<br/>
    					Petrolink<br/>
    					</p>
    					]]>
    					</Param>
    				</Params>
    				<Subject>Test MBE Mail Subject ${subjectParam}</Subject>
    	            <Body>
    	            <![CDATA[
    	            ${opening},<br/>
    	            <br/>
    	            <p>
    	            This email came from MBEMailFlow.
    	            The parameters are :
    	            </p>
    	            <p>
    		            P1 = ${p1} <br/> 
    		            P2 = ${p2} <br/>
    	            </p>
    	            <p>
    		            If variables are missing, for example, you can do like<br/>
    		            P3 = ${p3} <br/>
    		            P3 = ${p3! "Unknown value"} <br/>
    	            </p>
    	            <p>
    		            Others<br/>
    		            P3 = ${details! "No Details"} <br/>
    		            P3 = ${alert! "No Alert"} <br/>
    	            </p>
    	            ${closing}
    	            ]]> 
    	            </Body>
    			</SendMailNotification>
    			<Script sequence="30">
    				System.out.println("Flow: Mailflow  completed!!")
    			</Script>
    		</Actions>
    	</ActionsFlow>
    </Flows>

Send Mail notification use HTML format for is body as you can see here, and it overrides several element from template, for examples From, Subject, and Body. One of the Params (P1) is also overridden while we also add `closing` and `subjectParam`.

> **Tip 1**: if you specify xml value with **<![CDATA[somevaluehere]]**  you can type freely without encode xml
> 
> **Tip 2**: As mentioned earlier, Subject and Body can use freemarker syntax with `${parameterName!"value when not available"}`


## Example Result ##
In RAW format the Email sent from above configuration will be
 
    Date: Wed, 21 Sep 2016 14:25:45 -0500 (CDT)
    From: Event Mailflow <mbe@petrolink.com>
    To: user1@example.com, user2@example.com
    Message-ID: <1182165103.9.1474485945019@DHTODEV06556>
    Subject: Test MBE Mail Subject Mailflow
    MIME-Version: 1.0
    Content-Type: multipart/mixed; 
    	boundary="----=_Part_8_1692632021.1474485945015"
    Bcc: test@test.com
    
    ------=_Part_8_1692632021.1474485945015
    Content-Type: text/html; charset=us-ascii
    Content-Transfer-Encoding: 7bit
    
    
    	            
    	            Hola,<br/>
    	            <br/>
    	            <p>
    	            This email came from MBEMailFlow.
    	            The parameters are :
    	            </p>
    	            <p>
    		            P1 = 5 <br/> 
    		            P2 = 15 <br/>
    	            </p>
    	            <p>
    		            If variables are missing, for example, you can do like<br/>
    		            P3 =  <br/>
    		            P3 = Unknown value <br/>
    	            </p>
    	            <p>
    		            Others<br/>
    		            P3 = NoDetails <br/>
    		            P3 = No Alert <br/>
    	            </p>
    	            
    					
    					<p>
    					Best Regards, <br/>
    					<br/>
    					Petrolink<br/>
    					</p>
    					
    					
    	             
    
    ------=_Part_8_1692632021.1474485945015-- 

## Testing Recommendation ##
During testing, if real server is not available, it is recommended to use Fake SMTP server. In windows, the recommended one is PaperCut, available from https://github.com/ChangemakerStudios/Papercut . this application allow you to see the resulting email and save the raw format as eml file, while acting as SMTP server.
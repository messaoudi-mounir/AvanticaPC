<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ page import="javax.jms.*" %>

<%@ page import="org.apache.activemq.*" %>
<%@ page import="org.json.*" %>

<% 
	
    try {
        // Create a ConnectionFactory
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        // Create a Connection
        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        Session sess = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        Destination destination = sess.createQueue("testQueue1");

        // Create a MessageProducer from the Session to the Topic or Queue
        MessageProducer producer = sess.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        TextMessage message = sess.createTextMessage(request.getParameter("inputText"));

        // Tell the producer to send the message
        System.out.println("Sending: "+request.getParameter("inputText"));
        System.out.println("Sent message: "+ message.hashCode() + " : " + Thread.currentThread().getName());
        producer.send(message);

        // Clean up
        sess.close();
        connection.close();
    }
    catch (Exception e) {
        System.out.println("Caught: " + e);
        e.printStackTrace();
    }		
%>
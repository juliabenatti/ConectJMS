package com.ctmconnect;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.brokerconfiguration.BrokerManager;


public class Listener implements MessageListener {
	private static int ackMode;
	private static String messageQueueName;
	private static String messageBrokerUrl;

	private Session session;
	private boolean transacted = false;
	private MessageProducer replyProducer;
	private static Connection connection = null;

	private Handler handler = new Handler();

	static {
		messageBrokerUrl = "tcp://localhost:61616";
		messageQueueName = "inbound.queue";
		ackMode = Session.AUTO_ACKNOWLEDGE;
	}

	public Listener() {
		BrokerManager.startBroker();
		this.setupMessageQueueConsumer();
	}

	private void setupMessageQueueConsumer() {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(messageBrokerUrl);
		// Connection connection = null;
		try {
			connection = connectionFactory.createConnection();
			connection.start();
			this.session = connection.createSession(this.transacted, ackMode);
			Destination adminQueue = this.session.createQueue(messageQueueName);

			// Setup a message producer to respond to messages System
			this.replyProducer = this.session.createProducer(null);
			this.replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			// Set up a consumer to consume messages off of the admin queue
			MessageConsumer consumer = this.session.createConsumer(adminQueue);
			consumer.setMessageListener(this);

		} catch (JMSException e) {
			System.out.println("System had issues to connect to Broker service. " + e + "\n");
		}
	}

	public void stopApplication() {
		try {
			BrokerManager.stopBroker();
			connection.close();
		} catch (JMSException e) {
		}

	}

	public void onMessage(Message message) {
		try {
			TextMessage response = this.session.createTextMessage();
			String messageText = "";

			if (message instanceof TextMessage) {
				TextMessage txtMsg = (TextMessage) message;
				messageText = txtMsg.getText();

			}

			// Handle request from System, communicating with MyLib and getting
			// response, according to the specific request
			String responseSys = handler.getResponse(messageText);

			// Set response
			response.setText(responseSys);

			// Set the correlation ID for each message sent
			response.setJMSCorrelationID(message.getJMSCorrelationID());

			System.out.println("Sending message to System " + response.getText());

			this.replyProducer.send(message.getJMSReplyTo(), response);
		} catch (JMSException e) {
			System.out.println("System had issues to connect to Broker service. " + e + "\n");
		} 
	}


	public static void main(String[] args) {
		new Listener();
	}

}
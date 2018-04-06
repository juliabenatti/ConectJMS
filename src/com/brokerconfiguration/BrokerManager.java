package com.brokerconfiguration;

import javax.jms.Session;

import org.apache.activemq.broker.BrokerService;

public class BrokerManager{

	private static int ackMode;
	private static String messageQueueName;
	private static String messageBrokerUrl;

	// This message broker is embedded
	private static BrokerService broker = new BrokerService();


	static {
		messageBrokerUrl = "tcp://localhost:61616";
		messageQueueName = "inbound.queue";
		ackMode = Session.AUTO_ACKNOWLEDGE;
	}

	public static void startBroker() {
		try {
			if (broker.isStarted()) {
				// Broker started. No action needed.
			} else {
				broker.setPersistent(false);
				broker.setUseJmx(false);
				broker.addConnector(messageBrokerUrl);
				broker.start();
			}
			
		} catch (Exception e) {
			System.out.println("Broker had an issue to start. " + e + "\n");
		}
	}

	public static void stopBroker() {
		try {
			broker.stop();
		} catch (Exception e) {
			System.out.println("Broker had an issue to stop. " + e + "\n");
		}
	}


}

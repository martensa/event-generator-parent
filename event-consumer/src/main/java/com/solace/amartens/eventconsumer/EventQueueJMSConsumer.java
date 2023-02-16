package com.solace.amartens.eventconsumer;

import java.util.concurrent.CountDownLatch;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;
import com.solacesystems.jms.SupportedProperty;

public class EventQueueJMSConsumer {

	// Latch used for synchronizing between threads
	final CountDownLatch latch = new CountDownLatch(1);

	public void run(String... args) throws Exception {

		String[] split = args[1].split("@");

		String host = args[0];
		String vpnName = split[1];
		String username = split[0];
		String password = args[2];
		String queueName = args[3];

		System.out.printf("EventQueueJMSConsumer is connecting to Solace messaging at %s...%n", host);

		// Programmatically create the connection factory using default settings
		SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
		connectionFactory.setHost(host);
		connectionFactory.setVPN(vpnName);
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);

		// Enables persistent queues or topic endpoints to be created dynamically
		// on the router, used when Session.createQueue() is called below
		connectionFactory.setDynamicDurables(true);

		// Create connection to the Solace router
		Connection connection = connectionFactory.createConnection();

		// Create a non-transacted, client ACK session.
		Session session = connection.createSession(false, SupportedProperty.SOL_CLIENT_ACKNOWLEDGE);

		System.out.printf("Connected to the Solace Message VPN '%s' with client username '%s'.%n", vpnName,
				username);

		// Create the queue programmatically and the corresponding router resource
		// will also be created dynamically because DynamicDurables is enabled.
		Queue queue = SolJmsUtility.createQueue(queueName);

		// From the session, create a consumer for the destination.
		MessageConsumer messageConsumer = session.createConsumer(queue);

		// Use the anonymous inner class for receiving messages asynchronously
		messageConsumer.setMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message message) {
				try {
					if (message instanceof TextMessage) {
						System.out.printf("TextMessage received: '%s'%n", ((TextMessage) message).getText());
					} else {
						System.out.println("Message received.");
					}
					System.out.printf("Message Content:%n%s%n", SolJmsUtility.dumpMessage(message));

					// ACK the received message manually because of the set SupportedProperty.SOL_CLIENT_ACKNOWLEDGE above
					message.acknowledge();

					//latch.countDown(); // unblock the main thread
				} catch (JMSException ex) {
					System.out.println("Error processing incoming message.");
					ex.printStackTrace();
				}
			}
		});

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("graceful shutdown...");

			try {
				connection.stop();
				messageConsumer.close();
				session.close();
				connection.close();
			}
			catch (JMSException e) {
				throw new RuntimeException(e);
			}

			latch.countDown(); // that unlocks the main thread
		}));

		// Start receiving messages
		connection.start();
		System.out.println("Awaiting messages...");
		// the main thread blocks at the next statement until a message received
		latch.await();
	}

	public static void main(String... args) throws Exception {
		if (args.length != 4 || args[1].split("@").length != 2) {
			System.out.println("Usage: EventQueueJMSConsumer <host:port> <client-username@message-vpn> <client-password> <queue name>");
			System.out.println();
			System.exit(-1);
		}
		if (args[1].split("@")[0].isEmpty()) {
			System.out.println("No client-username entered");
			System.out.println();
			System.exit(-1);
		}
		if (args[1].split("@")[1].isEmpty()) {
			System.out.println("No message-vpn entered");
			System.out.println();
			System.exit(-1);
		}
		new EventQueueJMSConsumer().run(args);
	}
}
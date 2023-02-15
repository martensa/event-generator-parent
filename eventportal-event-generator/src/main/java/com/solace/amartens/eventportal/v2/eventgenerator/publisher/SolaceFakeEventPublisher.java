package com.solace.amartens.eventportal.v2.eventgenerator.publisher;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import com.solace.amartens.eventportal.v2.entities.MessagingServiceAuthentication;
import com.solace.amartens.eventportal.v2.entities.MessagingServiceConnection;
import com.solace.amartens.eventportal.v2.entities.MessagingServiceCredential;
import com.solace.amartens.eventportal.v2.eventgenerator.handler.FakeEvent;
import com.solace.amartens.eventportal.v2.eventgenerator.handler.SolaceAsyncApiHandler;
import com.solace.opentelemetry.javaagent.jms.SolaceJmsW3CTextMapSetter;
import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;

import io.apptik.json.JsonObject;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;

@EnableAsync
public class SolaceFakeEventPublisher {
	private static final String INSTRUMENTATION_NAME = SolaceFakeEventPublisher.class.getName();

	private final OpenTelemetry openTelemetry;
	private final Tracer tracer;
	private final SolaceJmsW3CTextMapSetter solaceTextMapSetter;

	private final SolaceAsyncApiHandler solaceAsyncApiHandler;
	private final List<FakeEvent> fakeEvents;
	private final Map<String, MessagingServiceConnection> messagingServiceConnections;

	private ScheduledFakeEventPublisher scheduledFakeEventPublisher;

	protected SolaceFakeEventPublisher(Builder builder) {
		this.openTelemetry = builder.openTelemetry;
		this.tracer = openTelemetry.getTracer(INSTRUMENTATION_NAME);
		this.solaceTextMapSetter = new SolaceJmsW3CTextMapSetter();
		this.solaceAsyncApiHandler = builder.solaceAsyncApiHandler;
		this.fakeEvents = this.solaceAsyncApiHandler.generateFakeEvents();
		this.messagingServiceConnections = this.solaceAsyncApiHandler.getMessagingServiceConnections();
	}

	public static Builder builder() {
		return new Builder();
	}

	private Map<String, Connection> createConnections() {
		Map<String, Connection> connections = new HashMap<String, Connection>();

		Set<String> distinctServiceConnections = new HashSet<String>();
		for (FakeEvent fakeEvent : this.fakeEvents) {
			distinctServiceConnections.addAll(fakeEvent.getMessagingServiceConnections());
		}

		try {
			for (String serviceConnectionName : distinctServiceConnections) {
				MessagingServiceConnection messagingServiceConnection = this.messagingServiceConnections.get(serviceConnectionName);
				SolConnectionFactory solaceConnectionFactory = SolJmsUtility.createConnectionFactory();
				solaceConnectionFactory.setHost(messagingServiceConnection.getUrl());
				solaceConnectionFactory.setVPN(messagingServiceConnection.getBindings().getMsgVpn());
				List<MessagingServiceAuthentication> authentications = messagingServiceConnection.getMessagingServiceAuthentications();
				for (MessagingServiceAuthentication authentication : authentications) {
					if (authentication.getAuthenticationType().equals("userPass")) {
						List<MessagingServiceCredential> credentials = authentication.getMessagingServiceCredentials();
						for (MessagingServiceCredential credential : credentials) {
							if (credential.getCredentials().getName().equals("smfuser")) {
								solaceConnectionFactory.setUsername(credential.getCredentials().getUsername());
								solaceConnectionFactory.setPassword(credential.getCredentials().getPassword());
							}
						}
					}
				}
				connections.put(serviceConnectionName, solaceConnectionFactory.createConnection());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return connections;
	}

	private Map<String, Session> createSessions(Map<String, Connection> connections) {
		Map<String, Session> sessions = new HashMap<String, Session>();

		try {
			for (Map.Entry<String, Connection> connection : connections.entrySet()) {
				sessions.put(connection.getKey(), connection.getValue().createSession(false, Session.AUTO_ACKNOWLEDGE));
			}
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}

		return sessions;
	}

	public void start() {
		Map<String, Connection> connections = createConnections();
		Map<String, Session> sessions = createSessions(connections);
		this.scheduledFakeEventPublisher = new ScheduledFakeEventPublisher(connections, sessions, this.fakeEvents);
	}

	@Async
	@Scheduled(fixedRateString = "${solace.publisher.scheduled.fixedRate.in.milliseconds}")
	public void scheduleFixedRateTaskAsync() {
		if (this.scheduledFakeEventPublisher != null) {
			Span parentSpan = tracer.spanBuilder("scheduled_async_process").setSpanKind(SpanKind.INTERNAL).startSpan();
			parentSpan.setAttribute("PROCESS_ID", UUID.randomUUID().toString());
			try (Scope scope = parentSpan.makeCurrent()){
				this.scheduledFakeEventPublisher.sendNewFakeEvent();
			} finally {
				parentSpan.end();
			}
		}
	}

	public class ScheduledFakeEventPublisher {
		private final Map<String, Connection> connections;
		private final Map<String, Session> sessions;
		private final List<FakeEvent> fakeEvents;

		public ScheduledFakeEventPublisher(Map<String, Connection> connections, Map<String, Session> sessions, List<FakeEvent> fakeEvents) {
			this.connections = connections;
			this.sessions = sessions;
			this.fakeEvents = fakeEvents;
		}

		//@WithSpan
		public void sendNewFakeEvent() {
			for (FakeEvent event : this.fakeEvents) {
				event.generateNewFakerEventInstance();

				Span childSpan = tracer.spanBuilder("send_new_fake_event").setSpanKind(SpanKind.PRODUCER).startSpan();
				try(Scope scope = childSpan.makeCurrent()) {
					if (event.getMapSchemaPropertiesToSpan() != null) {
						JsonObject jsonObject = event.getPayload().asJsonObject();
						for (String property : event.getMapSchemaPropertiesToSpan()) {
							String type = jsonObject.get(property).getJsonType();
							if (type.equals("integer")) {
								childSpan.setAttribute(property.toUpperCase(), jsonObject.get(property).asInt());
							}
							if (type.equals("string")) {
								childSpan.setAttribute(property.toUpperCase(), jsonObject.get(property).asString());
							}
							if (type.equals("number")) {
								childSpan.setAttribute(property.toUpperCase(), jsonObject.get(property).asDouble());
							}
							if (type.equals("boolean")) {
								childSpan.setAttribute(property.toUpperCase(), jsonObject.get(property).asBoolean());
							}
						}
					}

					for (String messagingServiceConnectionName : event.getMessagingServiceConnections()) {
						Session session = this.sessions.get(messagingServiceConnectionName);

						// Create the publishing topic programmatically
						Topic topic = session.createTopic(event.getTopicStringForFakerEventInstance());

						// Create the message producer for the created topic
						MessageProducer messageProducer = session.createProducer(topic);

						// Create the message
						TextMessage message = session.createTextMessage(event.getPayload().toString());
						if (event.getMapSchemaPropertiesToHeader() != null) {
							JsonObject jsonObject = event.getPayload().asJsonObject();
							for (String property : event.getMapSchemaPropertiesToHeader()) {
								String type = jsonObject.get(property).getJsonType();
								if (type.equals("integer")) {
									message.setIntProperty(property.toUpperCase(), jsonObject.get(property).asInt());
								}
								if (type.equals("string")) {
									message.setStringProperty(property.toUpperCase(), jsonObject.get(property).asString());
								}
								if (type.equals("number")) {
									message.setDoubleProperty(property.toUpperCase(), jsonObject.get(property).asDouble());
								}
								if (type.equals("boolean")) {
									message.setBooleanProperty(property.toUpperCase(), jsonObject.get(property).asBoolean());
								}
							}
						}

						openTelemetry.getPropagators().getTextMapPropagator().inject(Context.current(), message, solaceTextMapSetter);

						System.out.printf("Sending message '%s' to topic '%s'...%n", message.getText(), topic.toString());
						// Send the message
						// NOTE: JMS Message Priority is not supported by the Solace Message Bus
						messageProducer.send(topic, message, DeliveryMode.PERSISTENT, Message.DEFAULT_PRIORITY, 10000L);
						System.out.println("Sent successfully. Exiting...");

						// Close everything in the order reversed from the opening order
						// NOTE: as the interfaces below extend AutoCloseable,
						// with them it's possible to use the "try-with-resources" Java statement
						// see details at https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
						messageProducer.close();
					}
				}catch (JMSException e) {
					throw new RuntimeException(e);
				} finally {
					childSpan.end();
				}
			}
		}

		public void close() {
			try {
				for (Map.Entry<String, Session> sessionEntry : this.sessions.entrySet()) {
					sessionEntry.getValue().close();
				}
				for (Map.Entry<String, Connection> connectionEntry : this.connections.entrySet()) {
					connectionEntry.getValue().close();
				}
			} catch (JMSException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void terminate() {
		this.scheduledFakeEventPublisher.close();
	}

	public static class Builder {
		private OpenTelemetry openTelemetry;
		private SolaceAsyncApiHandler solaceAsyncApiHandler;

		public Builder openTelemetry(OpenTelemetry openTelemetry) {
			this.openTelemetry = openTelemetry;
			return this;
		}

		public Builder solaceAsyncApiHandler(SolaceAsyncApiHandler solaceAsyncApiHandler) {
			this.solaceAsyncApiHandler = solaceAsyncApiHandler;
			return this;
		}

		public SolaceFakeEventPublisher build() {
			return new SolaceFakeEventPublisher(this);
		}
	}
}
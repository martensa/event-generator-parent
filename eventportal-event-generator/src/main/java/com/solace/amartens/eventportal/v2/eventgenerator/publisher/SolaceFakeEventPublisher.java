package com.solace.amartens.eventportal.v2.eventgenerator.publisher;

import java.util.List;
import java.util.UUID;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import com.solace.amartens.eventportal.v2.SolaceEventBrokerProperties;
import com.solace.amartens.eventportal.v2.eventgenerator.handler.FakeEvent;
import com.solace.amartens.eventportal.v2.eventgenerator.handler.SolaceAsyncApiHandler;
import com.solace.opentelemetry.javaagent.jms.SolaceJmsW3CTextMapSetter;
import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;
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
	private final Connection connection;
	private final Session session;
	private final SolaceAsyncApiHandler solaceAsyncApiHandler;
	private final List<FakeEvent> fakeEvents;
	private final OpenTelemetry openTelemetry;
	private final Tracer tracer;
	private final SolaceJmsW3CTextMapSetter solaceTextMapSetter;

	protected SolaceFakeEventPublisher(Builder builder) {
		this.solaceAsyncApiHandler = builder.solaceAsyncApiHandler;
		this.fakeEvents = this.solaceAsyncApiHandler.generateFakeEvents();
		builder.createConnection();
		this.connection = builder.connection;
		builder.createSession();
		this.session = builder.session;
		this.openTelemetry = builder.openTelemetry;
		this.tracer = this.openTelemetry.getTracer(INSTRUMENTATION_NAME);
		this.solaceTextMapSetter = new SolaceJmsW3CTextMapSetter();
	}

	public static Builder builder() {
		return new Builder();
	}

	@Async
	@Scheduled(fixedRateString = "${solace.publisher.scheduled.fixedRate.in.milliseconds}")
	public void scheduleFixedRateTaskAsync() {
		sendNewFakeEvent();
	}

	//@WithSpan
	public void sendNewFakeEvent() {
		Span span = tracer.spanBuilder("send_new_fake_event").setSpanKind(SpanKind.PRODUCER).startSpan();
		//Span span = Span.current();
		span.setAttribute("processId", UUID.randomUUID().toString());
		// Make the span the current span
		try (Scope ss = span.makeCurrent()) {
			for (FakeEvent event : this.fakeEvents) {
				event.generateNewFakerEventInstance();

				// Create the publishing topic programmatically
				Topic topic = session.createTopic(event.getTopicStringForFakerEventInstance());

				// Create the message producer for the created topic
				MessageProducer messageProducer = session.createProducer(topic);

				// Create the message
				TextMessage message = session.createTextMessage(event.getPayload().toString());
				//message.setIntProperty("PROCESS_ID", 12345);

				Context context = Context.current();
				openTelemetry.getPropagators().getTextMapPropagator().inject(context, message, solaceTextMapSetter);

				System.out.printf("Sending message '%s' to topic '%s'...%n", message.getText(), topic.toString());
				// Send the message
				// NOTE: JMS Message Priority is not supported by the Solace Message Bus
				messageProducer.send(topic, message, DeliveryMode.PERSISTENT,
						Message.DEFAULT_PRIORITY, 10000L);
				System.out.println("Sent successfully. Exiting...");

				// Close everything in the order reversed from the opening order
				// NOTE: as the interfaces below extend AutoCloseable,
				// with them it's possible to use the "try-with-resources" Java statement
				// see details at https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
				messageProducer.close();
			}
			// In this scope, the span is the current/active span
		}
		catch (JMSException e) {
			throw new RuntimeException(e);
		}
		finally {
			span.end();
		}
	}

	public void close() {
		try {
			this.session.close();
			this.connection.close();
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}

	public static class Builder {
		private SolaceEventBrokerProperties solaceEventBrokerProperties;

		private SolaceAsyncApiHandler solaceAsyncApiHandler;

		private OpenTelemetry openTelemetry;

		private Connection connection;

		private Session session;

		public Builder solaceEventBrokerProperties(SolaceEventBrokerProperties solaceEventBrokerProperties) {
			this.solaceEventBrokerProperties = solaceEventBrokerProperties;
			return this;
		}

		public Builder solaceAsyncApiHandler(SolaceAsyncApiHandler solaceAsyncApiHandler) {
			this.solaceAsyncApiHandler = solaceAsyncApiHandler;
			return this;
		}

		public Builder solaceEventGeneratorOpenTelemetry(OpenTelemetry openTelemetry) {
			this.openTelemetry = openTelemetry;
			return this;
		}

		protected void createConnection() {
			try {
				SolConnectionFactory solaceConnectionFactory = SolJmsUtility.createConnectionFactory();
				solaceConnectionFactory.setHost(this.solaceEventBrokerProperties.getHost());
				solaceConnectionFactory.setVPN(this.solaceEventBrokerProperties.getMessageVpn());
				solaceConnectionFactory.setUsername(this.solaceEventBrokerProperties.getUsername());
				solaceConnectionFactory.setPassword(this.solaceEventBrokerProperties.getPassword());
				connection = solaceConnectionFactory.createConnection();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		protected void createSession() {
			try {
				session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			} catch (JMSException e) {
				throw new RuntimeException(e);
			}
		}

		public SolaceFakeEventPublisher build() {
			return new SolaceFakeEventPublisher(this);
		}
	}
}
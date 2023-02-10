package com.solace.amartens.eventportal.v2.eventgenerator.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.arkea.asyncapi.v2.models.AsyncAPI;
import com.arkea.asyncapi.v2.models.channels.Channel;
import com.arkea.asyncapi.v2.models.messages.Message;
import com.arkea.asyncapi.v2.models.parameters.Parameter;
import com.arkea.asyncapi.v2.models.servers.Server;
import com.solace.amartens.eventportal.v2.SolaceEventPortalRestClient;
import com.solace.amartens.eventportal.v2.entities.DataObject;
import com.solace.amartens.eventportal.v2.entities.EventApiProductVersion;
import com.solace.amartens.eventportal.v2.entities.EventVersion;
import com.solace.amartens.eventportal.v2.entities.MessagingService;
import com.solace.amartens.eventportal.v2.entities.SchemaVersion;

public class SolaceAsyncApiHandler {
	private final List<AsyncAPI> asyncApis;

	private final SolaceEventPortalRestClient solaceEventPortalRestClient;

	public static Builder builder() {
		return new Builder();
	}

	protected SolaceAsyncApiHandler(Builder builder) {
		builder.validate();
		this.solaceEventPortalRestClient = builder.solaceEventPortalRestClient;
		builder.downloadAsyncApi();
		this.asyncApis = builder.asyncApis;
	}

	private List<String> getServerListForSMF(Map<String, Server> servers) {
		List<String> serverList = new ArrayList<String>();
		for (Map.Entry<String, Server> server : servers.entrySet()) {
			if (server.getKey().endsWith("smf")) {
				if (servers.get(server.getKey()+"s") == null) {
					serverList.add(server.getKey());
				}
			} else {
				serverList.add(server.getKey());
			}
		}
		return serverList;
	}

	public List<FakeEvent> generateFakeEvents() {
		List<FakeEvent> fakeEvents = new ArrayList<FakeEvent>();
		for (AsyncAPI asyncApi : asyncApis) {
			Map<String, Message> messages = asyncApi.getComponents().getMessages();

			Map<String, Server> servers = asyncApi.getServers();
			List<String> messagingServiceConnections = getServerListForSMF(servers);

			Map<String, Channel> channels = asyncApi.getChannels();
			for (Map.Entry<String, Channel> channel : channels.entrySet()) {
				String topic = channel.getKey();

				Map<String, List<?>> topicParameterEnums = new LinkedHashMap<String, List<?>>();
				Map<String, Parameter> parameters = channel.getValue().getParameters();
				if (parameters != null) {
					for (Map.Entry<String, Parameter> parameter : parameters.entrySet()) {
						if (parameter.getValue().getSchema().getEnum() != null) {
							topicParameterEnums.put(parameter.getKey(), parameter.getValue().getSchema().getEnum());
						}
					}
				}

				String messageRef = channel.getValue().getPublish().getMessage().get$ref();
				String[] messageRefLevels = messageRef.split("/");
				String messageId = messageRefLevels[messageRefLevels.length-1];

				Message message = messages.get(messageId);
				fakeEvents.add(new FakeEvent(message.getExtensions().get("x-ep-event-name").toString(), topic, topicParameterEnums, message.getContentType(), message.getExtensions().get("x-ep-event-version-id").toString(), messagingServiceConnections));
			}
		}

		for (FakeEvent event : fakeEvents) {
			DataObject<EventVersion> eventVersionDataObject = null;
			DataObject<SchemaVersion> schemaVersionDataObject = null;
			try {
				eventVersionDataObject = solaceEventPortalRestClient.getEventVersion(event.getEventVersionId());
				schemaVersionDataObject = solaceEventPortalRestClient.getSchemaVersion(eventVersionDataObject.getData().getSchemaVersionId());
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
			event.setPayloadSchema(schemaVersionDataObject.getData().getContent());
		}

		return fakeEvents;
	}

	public List<MessagingService> getMessagingServices() {
		List<MessagingService> messagingServices = new ArrayList<MessagingService>();
		for (AsyncAPI asyncApi : asyncApis) {
			String eventApiProductVersionId = asyncApi.getInfo().getExtensions().get("x-ep-event-api-product-version-id").toString();
			EventApiProductVersion eventApiProductVersion = null;
			List<MessagingService> services = null;
			try {
				eventApiProductVersion = solaceEventPortalRestClient.getEventApiProductVersion(eventApiProductVersionId);
				services = solaceEventPortalRestClient.getMessagingServices(eventApiProductVersion);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
			messagingServices.addAll(services);
		}

		return messagingServices;
	}

	public static class Builder {
		private List<AsyncAPI> asyncApis;

		private SolaceEventPortalRestClient solaceEventPortalRestClient;

		public Builder solaceEventPortalRestClient(SolaceEventPortalRestClient solaceEventPortalRestClient) {
			//assertNonNull(asyncApis, "Async Api may not be null");
			this.solaceEventPortalRestClient = solaceEventPortalRestClient;
			return this;
		}

		public SolaceAsyncApiHandler build() {
			return new SolaceAsyncApiHandler(this);
		}

		protected void downloadAsyncApi() {
			try {
				this.asyncApis = this.solaceEventPortalRestClient.getEventApiVersionsAsyncApis();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@SuppressWarnings("WeakerAccess")
		protected void validate() {
			//assertNonNull(asyncApis, "Async Api is a hard requirement and should be provided");
		}
	}
}

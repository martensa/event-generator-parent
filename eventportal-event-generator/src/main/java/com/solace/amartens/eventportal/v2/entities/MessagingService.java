package com.solace.amartens.eventportal.v2.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessagingService {
	@JsonProperty("id")
	private String id;
	@JsonProperty("eventMeshId")
	private String messagingServiceId;
	@JsonProperty("messagingServiceType")
	private String messagingServiceType;
	@JsonProperty("name")
	private String name;
	@JsonProperty("messagingServiceConnections")
	private List<MessagingServiceConnection> messagingServiceConnections;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessagingServiceId() {
		return messagingServiceId;
	}

	public void setMessagingServiceId(String messagingServiceId) {
		this.messagingServiceId = messagingServiceId;
	}

	public String getMessagingServiceType() {
		return messagingServiceType;
	}

	public void setMessagingServiceType(String messagingServiceType) {
		this.messagingServiceType = messagingServiceType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<MessagingServiceConnection> getMessagingServiceConnections() {
		return messagingServiceConnections;
	}

	public void setMessagingServiceConnections(List<MessagingServiceConnection> messagingServiceConnections) {
		this.messagingServiceConnections = messagingServiceConnections;
	}
}
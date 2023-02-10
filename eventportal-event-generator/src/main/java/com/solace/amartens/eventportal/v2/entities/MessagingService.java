package com.solace.amartens.eventportal.v2.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessagingService {
	@JsonProperty("id")
	private String id;
	@JsonProperty("messagingServiceId")
	private String messagingServiceId;
	@JsonProperty("messagingServiceName")
	private String messagingServiceName;
	@JsonProperty("supportedProtocols")
	private List<String> supportedProtocols;
	@JsonProperty("environmentId")
	private String environmentId;
	@JsonProperty("environmentName")
	private String environmentName;
	@JsonProperty("eventMeshId")
	private String eventMeshId;
	@JsonProperty("eventMeshName")
	private String eventMeshName;
	@JsonProperty("solaceCloudMessagingServiceId")
	private String solaceCloudMessagingServiceId;

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

	public String getMessagingServiceName() {
		return messagingServiceName;
	}

	public void setMessagingServiceName(String messagingServiceName) {
		this.messagingServiceName = messagingServiceName;
	}

	public List<String> getSupportedProtocols() {
		return supportedProtocols;
	}

	public void setSupportedProtocols(List<String> supportedProtocols) {
		this.supportedProtocols = supportedProtocols;
	}

	public String getEnvironmentId() {
		return environmentId;
	}

	public void setEnvironmentId(String environmentId) {
		this.environmentId = environmentId;
	}

	public String getEnvironmentName() {
		return environmentName;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}

	public String getEventMeshId() {
		return eventMeshId;
	}

	public void setEventMeshId(String eventMeshId) {
		this.eventMeshId = eventMeshId;
	}

	public String getEventMeshName() {
		return eventMeshName;
	}

	public void setEventMeshName(String eventMeshName) {
		this.eventMeshName = eventMeshName;
	}

	public String getSolaceCloudMessagingServiceId() {
		return solaceCloudMessagingServiceId;
	}

	public void setSolaceCloudMessagingServiceId(String solaceCloudMessagingServiceId) {
		this.solaceCloudMessagingServiceId = solaceCloudMessagingServiceId;
	}
}
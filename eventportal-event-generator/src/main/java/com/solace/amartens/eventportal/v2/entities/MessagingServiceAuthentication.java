package com.solace.amartens.eventportal.v2.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessagingServiceAuthentication {
	@JsonProperty("id")
	private String id;
	@JsonProperty("messagingServiceConnectionId")
	private String messagingServiceId;
	@JsonProperty("name")
	private String name;
	@JsonProperty("authenticationType")
	private String authenticationType;
	@JsonProperty("messagingServiceCredentials")
	private List<MessagingServiceCredential> messagingServiceCredentials;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthenticationType() {
		return authenticationType;
	}

	public void setAuthenticationType(String authenticationType) {
		this.authenticationType = authenticationType;
	}

	public List<MessagingServiceCredential> getMessagingServiceCredentials() {
		return messagingServiceCredentials;
	}

	public void setMessagingServiceCredentials(List<MessagingServiceCredential> messagingServiceCredentials) {
		this.messagingServiceCredentials = messagingServiceCredentials;
	}
}
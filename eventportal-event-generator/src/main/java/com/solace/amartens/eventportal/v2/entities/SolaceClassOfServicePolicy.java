package com.solace.amartens.eventportal.v2.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SolaceClassOfServicePolicy {
	@JsonProperty("id")
	private String id;
	@JsonProperty("messageDeliveryMode")
	private String messageDeliveryMode;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessageDeliveryMode() {
		return messageDeliveryMode;
	}

	public void setMessageDeliveryMode(String messageDeliveryMode) {
		this.messageDeliveryMode = messageDeliveryMode;
	}
}
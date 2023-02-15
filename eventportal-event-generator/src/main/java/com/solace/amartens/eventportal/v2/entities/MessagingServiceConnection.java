package com.solace.amartens.eventportal.v2.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessagingServiceConnection {
	@JsonProperty("id")
	private String id;
	@JsonProperty("messagingServiceId")
	private String messagingServiceId;
	@JsonProperty("name")
	private String name;
	@JsonProperty("url")
	private String url;
	@JsonProperty("protocol")
	private String protocol;
	@JsonProperty("bindings")
	private Bindings bindings;
	@JsonProperty("messagingServiceAuthentications")
	private List<MessagingServiceAuthentication> messagingServiceAuthentications;

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public Bindings getBindings() {
		return bindings;
	}

	public void setBindings(Bindings bindings) {
		this.bindings = bindings;
	}

	public List<MessagingServiceAuthentication> getMessagingServiceAuthentications() {
		return messagingServiceAuthentications;
	}

	public void setMessagingServiceAuthentications(List<MessagingServiceAuthentication> messagingServiceAuthentications) {
		this.messagingServiceAuthentications = messagingServiceAuthentications;
	}

	public static class Bindings {
		private String msgVpn;

		public String getMsgVpn() {
			return this.msgVpn;
		}

		public void setMsgVpn(String msgVpn) {
			this.msgVpn = msgVpn;
		}
	}
}
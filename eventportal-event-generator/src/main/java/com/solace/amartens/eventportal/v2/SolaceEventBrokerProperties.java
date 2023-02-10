package com.solace.amartens.eventportal.v2;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "solace.event.broker")
public class SolaceEventBrokerProperties {
	private String host;
	private String messageVpn;
	private String username;
	private String password;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getMessageVpn() {
		return messageVpn;
	}

	public void setMessageVpn(String messageVpn) {
		this.messageVpn = messageVpn;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
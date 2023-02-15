package com.solace.amartens.eventportal.v2.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessagingServiceCredential {
	@JsonProperty("id")
	private String id;
	@JsonProperty("messagingServiceAuthenticationId")
	private String messagingServiceId;
	@JsonProperty("name")
	private String name;
	@JsonProperty("credentials")
	private Credentials credentials;

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

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public static class Credentials {
		private String name;
		private String username;
		private String password;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			if (this.password.length() > 0) {
				return this.password.substring(2,this.password.length()-1);
			}

			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
}
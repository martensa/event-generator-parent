package com.solace.amartens.eventportal.v2.restapiclient.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventApiProduct {
	@JsonProperty("id")
	private String id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("applicationDomainId")
	private String applicationDomainId;
	@JsonProperty("shared")
	private boolean shared;
	@JsonProperty("numberOfVersions")
	private int numberOfVersions;
	@JsonProperty("brokerType")
	private String brokerType;
	@JsonProperty("customAttributes")
	private List<CustomAttribute> customAttributes;

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCustomAttributes(List<CustomAttribute> customAttributes) {
		this.customAttributes = customAttributes;
	}

	public List<CustomAttribute> getCustomAttributes() {
		return this.customAttributes;
	}

	public String getApplicationDomainId() {
		return applicationDomainId;
	}

	public void setApplicationDomainId(String applicationDomainId) {
		this.applicationDomainId = applicationDomainId;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	public int getNumberOfVersions() {
		return numberOfVersions;
	}

	public void setNumberOfVersions(int numberOfVersions) {
		this.numberOfVersions = numberOfVersions;
	}

	public String getBrokerType() {
		return brokerType;
	}

	public void setBrokerType(String brokerType) {
		this.brokerType = brokerType;
	}
}
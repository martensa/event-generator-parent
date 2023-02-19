package com.solace.amartens.eventportal.v2.restapiclient.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventApiProductVersion {
	@JsonProperty("id")
	private String id;
	@JsonProperty("description")
	private String description;
	@JsonProperty("eventApiProductId")
	private String eventApiProductId;
	@JsonProperty("version")
	private String version;
	@JsonProperty("customAttributes")
	private List<CustomAttribute> customAttributes;
	@JsonProperty("eventApiVersionIds")
	private List<String> eventApiVersionIds;
	@JsonProperty("plans")
	private List<Plan> plans;
	@JsonProperty("solaceMessagingServices")
	private List<GatewayMessagingService> gatewayMessagingServices;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCustomAttributes(List<CustomAttribute> customAttributes) {
		this.customAttributes = customAttributes;
	}

	public List<CustomAttribute> getCustomAttributes() {
		return this.customAttributes;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEventApiProductId() {
		return eventApiProductId;
	}

	public void setEventApiProductId(String eventApiProductId) {
		this.eventApiProductId = eventApiProductId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<String> getEventApiVersionIds() {
		return eventApiVersionIds;
	}

	public void setEventApiVersionIds(List<String> eventApiVersionIds) {
		this.eventApiVersionIds = eventApiVersionIds;
	}

	public List<Plan> getPlans() {
		return plans;
	}

	public void setPlans(List<Plan> plans) {
		this.plans = plans;
	}

	public List<GatewayMessagingService> getSolaceMessagingServices() {
		return gatewayMessagingServices;
	}

	public void setSolaceMessagingServices(List<GatewayMessagingService> gatewayMessagingServices) {
		this.gatewayMessagingServices = gatewayMessagingServices;
	}
}
package com.solace.amartens.eventportal.v2.restapiclient.entities;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventVersion {
	@JsonProperty("id")
	private String id;
	@JsonProperty("description")
	private String description;
	@JsonProperty("version")
	private String version;
	@JsonProperty("eventId")
	private String eventId;
	@JsonProperty("schemaVersionId")
	private String schemaVersionId;
	@JsonProperty("customAttributes")
	private List<CustomAttribute> customAttributes;

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

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId= eventId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSchemaVersionId() {
		return schemaVersionId;
	}

	public void setSchemaVersionId(String schemaVersionId) {
		this.schemaVersionId = schemaVersionId;
	}
}
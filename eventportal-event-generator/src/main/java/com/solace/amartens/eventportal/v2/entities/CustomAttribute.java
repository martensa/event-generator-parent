package com.solace.amartens.eventportal.v2.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomAttribute {
	@JsonProperty("customAttributeDefinitionId")
	private String id;
	@JsonProperty("customAttributeDefinitionName")
	private String name;
	@JsonProperty("value")
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
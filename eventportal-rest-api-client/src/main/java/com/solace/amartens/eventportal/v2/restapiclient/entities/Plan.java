package com.solace.amartens.eventportal.v2.restapiclient.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Plan {
	@JsonProperty("id")
	private String id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("solaceClassOfServicePolicy")
	private SolaceClassOfServicePolicy solaceClassOfServicePolicy;

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

	public void setSolaceClassOfServicePolicy(SolaceClassOfServicePolicy solaceClassOfServicePolicy) {
		this.solaceClassOfServicePolicy = solaceClassOfServicePolicy;
	}

	public SolaceClassOfServicePolicy getSolaceClassOfServicePolicy() {
		return solaceClassOfServicePolicy;
	}
}
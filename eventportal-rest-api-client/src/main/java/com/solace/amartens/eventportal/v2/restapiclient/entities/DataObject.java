package com.solace.amartens.eventportal.v2.restapiclient.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataObject<T> {
	@JsonProperty("data")
	private T data;

	public T getData() {
		return this.data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
package com.solace.amartens.eventportal.v2.restapiclient.exception;

public class EventPortalEntityIsNotFoundException extends Exception {
	public EventPortalEntityIsNotFoundException() {
		super("Required Event Portal Entity is not found.");
	}

	public EventPortalEntityIsNotFoundException(String message) {
		super(message);
	}
}
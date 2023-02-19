package com.solace.amartens.eventportal.v2.restapiclient.exception;

public class EventPortalEntityIsMissingException extends Exception {
	public EventPortalEntityIsMissingException() {
		super("Required Event Portal Entity is missing.");
	}

	public EventPortalEntityIsMissingException(String message) {
		super(message);
	}
}
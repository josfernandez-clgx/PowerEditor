package com.mindbox.pe.server.webservices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mindbox.pe.server.report.ErrorMessageStorage;

public class SimpleErrorMessageStorage implements ErrorMessageStorage {

	private final List<String> messages = new ArrayList<String>();

	@Override
	public void addErrorMessage(String message) {
		messages.add(message);
	}

	public List<String> getMessages() {
		return Collections.unmodifiableList(messages);
	}
}

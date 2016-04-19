package com.mindbox.pe.common.validate;

import java.io.Serializable;

public class MessageDetail implements Serializable {

	private static final long serialVersionUID = -1381800863750856323L;

	private final String message;
	private final Object context;

	public MessageDetail(String message, Object context) {
		this.message = message;
		this.context = context;
	}

	public final String getMessage() {
		return message;
	}

	public final Object getContext() {
		return context;
	}

	public String toString() {
		return message + " at " + context;
	}
}

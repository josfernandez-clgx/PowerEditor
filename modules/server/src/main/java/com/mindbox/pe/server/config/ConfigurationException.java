package com.mindbox.pe.server.config;

public class ConfigurationException extends RuntimeException {

	private static final long serialVersionUID = 5941699246667218031L;

	public ConfigurationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ConfigurationException(String arg0) {
		super(arg0);
	}

	public ConfigurationException(Throwable arg0) {
		super(arg0);
	}

}

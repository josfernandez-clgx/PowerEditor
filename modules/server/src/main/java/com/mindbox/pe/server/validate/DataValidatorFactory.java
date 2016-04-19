package com.mindbox.pe.server.validate;


public final class DataValidatorFactory {

	private static DataValidatorFactory instance;

	public static DataValidatorFactory getInstance() {
		if (instance == null) {
			instance = new DataValidatorFactory();
		}
		return instance;
	}

	private DataValidatorFactory() {
	}

	public DataValidator getDataValidator() {
		return new DefaultDataValidator();
	}
}

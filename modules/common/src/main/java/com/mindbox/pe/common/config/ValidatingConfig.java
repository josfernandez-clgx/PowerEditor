package com.mindbox.pe.common.config;

public interface ValidatingConfig {

	/**
	 * @throws IllegalStateException if validation fails
	 */
	void validate();
}

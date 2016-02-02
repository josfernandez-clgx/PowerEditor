package com.mindbox.pe.server.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnumerationSourceConfig {

	private String type;
	private String name;
	private boolean supportsSelector;
	private final List<ConfigParameter> parameterList = new ArrayList<ConfigParameter>();

	public void addConfigParameter(ConfigParameter configParameter) {
		parameterList.add(configParameter);
	}

	public List<ConfigParameter> getParameterList() {
		return Collections.unmodifiableList(parameterList);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSupportsSelector() {
		return supportsSelector;
	}

	public void setSupportsSelector(boolean supportsSelector) {
		this.supportsSelector = supportsSelector;
	}
}

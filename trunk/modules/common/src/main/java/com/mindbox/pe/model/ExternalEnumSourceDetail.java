package com.mindbox.pe.model;

import java.io.Serializable;

public class ExternalEnumSourceDetail implements Serializable {

	private static final long serialVersionUID = 200805301000001L;

	private final String name;
	private final boolean supportsSelector;

	public ExternalEnumSourceDetail(String name, boolean supportsSelector) {
		this.name = name;
		this.supportsSelector = supportsSelector;
	}

	public String getName() {
		return name;
	}

	public boolean isSupportsSelector() {
		return supportsSelector;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof ExternalEnumSourceDetail) {
			return name.equals(((ExternalEnumSourceDetail) obj).getName());
		}
		else {
			return false;
		}
	}

	@Override
	public String toString() {
		return getClass().getName() + "[" + name + ",selector?=" + supportsSelector + ']';
	}
}

package com.mindbox.pe.server.generator.rule;

public final class StaticFunctionArgument implements FunctionArgument {

	private String value;

	public StaticFunctionArgument(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return "StaticFuncArg[" + value + "]";
	}
}

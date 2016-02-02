package com.mindbox.pe.server.generator.rule;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractFunctionCallPattern implements FunctionCallPattern {

	private final List<FunctionArgument> argList = new LinkedList<FunctionArgument>();
	private String functionName;

	/**
	 * 
	 * @param functionName function name
	 */
	protected AbstractFunctionCallPattern(String functionName) {
		this.functionName = functionName;
	}

	public void add(FunctionArgument testPatternArgument) {
		argList.add(testPatternArgument);
	}

	public final int argSize() {
		return argList.size();
	}

	public final FunctionArgument getArgAt(int index) {
		return argList.get(index);
	}

	public final String getFunctionName() {
		return functionName;
	}

	public boolean isEmpty() {
		return argList.isEmpty();
	}

	public void remove(FunctionArgument testPatternArgument) {
		argList.remove(testPatternArgument);
	}

}

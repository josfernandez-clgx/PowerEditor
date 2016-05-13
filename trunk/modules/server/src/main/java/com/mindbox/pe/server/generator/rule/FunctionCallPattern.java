package com.mindbox.pe.server.generator.rule;


public interface FunctionCallPattern extends LHSPattern, FunctionArgument {

	void add(FunctionArgument testPatternArgument);
	
	int argSize();
	
	FunctionArgument getArgAt(int index);
	
	String getFunctionName();
	
	boolean isEmpty();
	
	void remove(FunctionArgument testPatternArgument);
}

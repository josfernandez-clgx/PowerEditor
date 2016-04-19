package com.mindbox.pe.server.generator;

public interface GeneratorErrorContainer {
	
	 void reportError(String str) throws RuleGenerationException;
}

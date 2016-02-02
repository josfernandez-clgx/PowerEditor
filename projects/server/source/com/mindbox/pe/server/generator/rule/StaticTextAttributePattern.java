package com.mindbox.pe.server.generator.rule;

public class StaticTextAttributePattern extends AbstractAttributePattern {

	StaticTextAttributePattern(String attributeName, String varName) {
		super(attributeName, varName, false, "", null);
	}

	StaticTextAttributePattern(String attributeName, String varName, String valueText) {
		super(attributeName, varName, false, valueText, null);
	}
}

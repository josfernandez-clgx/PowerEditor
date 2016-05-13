package com.mindbox.pe.model.rule;

import com.mindbox.pe.common.DomainClassProvider;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public interface FunctionParameter extends RuleElement {
	String valueString();
	String displayString(DomainClassProvider domainClassProvider);
	int index();
}

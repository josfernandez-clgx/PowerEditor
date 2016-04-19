/*
 * Created on 2004. 1. 28.
 *
 */
package com.mindbox.pe.model.rule;

import com.mindbox.pe.model.template.ColumnReferenceContainer;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public interface RuleElement extends ColumnReferenceContainer {
	String toDisplayName();
	
	String getComment();
	void setComment(String comment);
	void setName(String name);
}

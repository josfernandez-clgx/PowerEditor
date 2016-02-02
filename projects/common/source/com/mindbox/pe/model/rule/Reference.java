/*
 * Created on 2004. 1. 28.
 *
 */
package com.mindbox.pe.model.rule;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public interface Reference {
	String getClassName();
	String getAttributeName();
	void setClassName(String className);
	void setAttributeName(String attributeName);
}

/*
 * Created on 2004. 8. 9.
 */
package com.mindbox.pe.model.rule;

/**
 * @author kim
 * @author MindBox
 * @since PowerEditor 
 */
public interface ExistExpression extends LHSElement {

	String getClassName();
	
	String getObjectName();
	
	String getExcludedObjectName();
	
	CompoundLHSElement getCompoundLHSElement();
	
	void setClassName(String className);
	
	void setObjectName(String objectName);
	
	void setExcludedObjectName(String excludedObjectName);
	
	void setCompoundLHSElement(CompoundLHSElement e);
}

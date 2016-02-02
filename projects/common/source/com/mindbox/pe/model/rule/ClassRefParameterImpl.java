package com.mindbox.pe.model.rule;

import com.mindbox.pe.common.DomainClassProvider;
import com.mindbox.pe.model.DomainClass;

/**
 * Concrete implementation of {@link com.mindbox.pe.model.rule.ClassReference}.
 * @author kim
 * @author MindBox
 * @since PowerEditor 4.0
 */
class ClassRefParameterImpl extends AbstractParameter implements ClassReference {
	
	private static final long serialVersionUID = -5148692384756743660L;

	private String className;
	
	/**
	 * 
	 * @param index
	 * @param name
	 * @param className
	 */
	ClassRefParameterImpl(int index, String name, String className) {
		super(name, index);
		this.className = className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	public String getClassName() {
		return className;
	}
	
	static boolean isValueString(String str) {
		return (str != null && str.startsWith("|") && str.endsWith("|") && str.indexOf(".") == -1);
	}
	
	static String extractClassName(String str) {
		return str.substring(1,str.length()-1);
	}

	public String valueString() {
		return "|" + className +  "|";
	}

	public String toString() {
		return super.toString()+"[" + valueString() + "]";
	}
	
	public String displayString(DomainClassProvider domainClassProvider) {
		if (className != null) {
			DomainClass dc = domainClassProvider.getDomainClass(className);
			if (dc != null) {
				return "[" + dc.getDisplayLabel() + "]";
			}
		}
		return null;
	}

}

package com.mindbox.pe.model.rule;

import com.mindbox.pe.common.DomainClassProvider;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;

/**
 * Concrete implementation of {@link com.mindbox.pe.model.rule.Reference} for attribute references.
 * @author kim
 * @author MindBox
 * @since PowerEditor 4.0
 */
class AttributeRefParameterImpl extends AbstractParameter implements Reference {
	
	private static final long serialVersionUID = 3086365632158665165L;
	
	private String attributeName;
	private String className;
	
	/**
	 * Creates a new reference implementation wit specified details.
	 * @param index
	 * @param name
	 * @param attName
	 * @param className
	 */
	AttributeRefParameterImpl(int index, String name, String attName, String className) {
		super(name, index);
		this.attributeName = attName;
		this.className = className;
	}

	static boolean isValueString(String str) {
		return (str != null && str.startsWith("|") && str.endsWith("|") && str.indexOf(".") != -1);
	}
	
	static String extractClassName(String str) {
		return str.substring(1,str.indexOf("."));
	}

	static String extractAttributeName(String str) {
		return str.substring(str.indexOf(".") + 1, str.length() - 1);
	}

	/**
	 * @param attName new attribute name
	 */
	public void setAttributeName(String attName) {
		this.attributeName = attName;
	}
	public String getAttributeName() {
		return attributeName;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	public String getClassName() {
		return className;
	}

	public String valueString() {
		return "|" + className + "." + attributeName + "|";
	}

	public String toString() {
		return super.toString()+"[" + valueString() + "]";
	}
	
	public String displayString(DomainClassProvider domainClassProvider) {
		if (className != null && attributeName != null) {
			DomainClass dc = domainClassProvider.getDomainClass(className);
			if (dc != null) {
				DomainAttribute da = dc.getDomainAttribute(attributeName);
				if (da != null) return "[" + dc.getDisplayLabel() + " : " + da.getDisplayLabel() + "]";
			}
		}
		return null;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Reference) {
			return UtilBase.isSame(className, ((Reference)obj).getClassName()) && UtilBase.isSame(attributeName, ((Reference)obj).getAttributeName());
		}
		else {
			return false;
		}
	}

}

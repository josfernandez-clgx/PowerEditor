package com.mindbox.pe.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Domain attribute of a domain class.
 * This is used by the domain digester.
 * @author Geneho
 * @since PowerEditor 1.0
 */
public class DomainAttribute extends AbstractDomainAttribute {

	private static final long serialVersionUID = 2003060308453000L;


	private String deployLabel;
	private DeployType deployType;
	private boolean usedInRules;

	/** @since PowerEditor 3.2.0 */
	private final List<EnumValue> enumValueList;

	public DomainAttribute() {
		enumValueList = new ArrayList<EnumValue>();
	}

	public void addEnumValue(EnumValue enumValue) {
		synchronized (enumValueList) {
			if (!enumValueList.contains(enumValue)) {
				enumValueList.add(enumValue);
			}
		}
	}

	public EnumValue[] getEnumValues() {
		synchronized (enumValueList) {
			return enumValueList.toArray(new EnumValue[0]);
		}
	}

	/**
	 * Tests if this has at least one enum value.
	 * @return <code>true</code> if this has at least one enum value; <code>false</code>, otherwise
	 * @since PowerEditor 3.2.0
	 */
	public boolean hasEnumValue() {
		return !enumValueList.isEmpty();
	}


	/**
	 * Sets used in rules flag.
	 * @param value the flag value; 1 for <code>true</code>
	 */
	public void setAllowRuleUsage(String value) {
		usedInRules = (value != null && (value.equals("1") || Boolean.valueOf(value).booleanValue()));
	}

	public void setDeployType(DeployType s) {
		deployType = s;
	}

	public DeployType getDeployType() {
		return deployType;
	}

	/**
	 * Sets deploy type of this.
	 * Added for digester.
	 * @param type type string
	 * @since PowerEditor 3.2.0
	 */
	public void setDeployTypeString(String type) {
		setDeployType(DeployType.valueOf(type));
	}

	/**
	 * Tests if this can be used in rules.
	 * @return used in rules flag
	 */
	public boolean allowRuleUsage() {
		return usedInRules;
	}

	public void setDeployLabel(String s) {
		deployLabel = s;
	}

	public String getDeployLabel() {
		return deployLabel;
	}

	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("DomainAttribute[");
		buff.append(getName());
		buff.append(",");
		buff.append(deployType);
		buff.append(",");
		buff.append(deployLabel);
		buff.append(",");
		buff.append(usedInRules);
		buff.append(",");
		buff.append(getDisplayLabel());
		buff.append(",");
		buff.append(getContextlessLabel());
		buff.append(",view=");
		for (Iterator<DomainView> iter = domainViewList.iterator(); iter.hasNext();) {
			buff.append(iter.next());
			if (iter.hasNext()) {
				buff.append("+");
			}
		}
		buff.append("]");
		return buff.toString();
	}

}
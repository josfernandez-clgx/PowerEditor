package com.mindbox.pe.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Domain Class.
 * This is used by domain XML digester.
 * @author Geneho
 * @since PowerEditor 1.0
 */
public final class DomainClass extends AbstractDomainElement {

	private static final long serialVersionUID = 2004010190000L;

	private String deployLabel;
	private String superClassName;
	private boolean usedInRules;
	private boolean isPersistent;
	private boolean isSingleton;
	private final List<DomainAttribute> domainAttributeList;

	private final List<DomainClassLink> childClassLinkList;
	private final List<DomainTranslation> translationList;

	public DomainClass() {
		domainAttributeList = new ArrayList<DomainAttribute>();
		childClassLinkList = new ArrayList<DomainClassLink>();
		translationList = new ArrayList<DomainTranslation>();
	}

	/**
	 * Tests if the specified domain class has at least one attribute with the specified domain view.
	 * @param domainClass
	 * @return <code>true</code> if <code>domainClass</code> has at least one attribute with the specified domain view;
	 *         <code>false</code>, otherwise
	 * @since PowerEditor 3.2.0
	 */
	public boolean hasDomainViewAttribute(DomainView domainView) {
		for (DomainAttribute element : domainAttributeList) {
			if (element.hasDomainView(domainView)) {
				return true;
			}
		}
		return false;
	}

	public DomainAttribute getDomainAttribute(String attributeName) {
		if (attributeName == null) return null;
		String s1 = attributeName.toUpperCase();
		DomainAttribute domainattribute = null;
		for (int i = 0; i < domainAttributeList.size(); i++) {
			domainattribute = domainAttributeList.get(i);
			if (domainattribute.getName().equalsIgnoreCase(s1)) return domainattribute;
		}
		return null;
	}

	public void setPersistent(boolean flag) {
		isPersistent = flag;
	}

	/**
	 * Sets used in rules flag.
	 * @param value
	 */
	public void setAllowRuleUsage(String value) {
		usedInRules = (value != null && (value.equals("1") || Boolean.valueOf(value).booleanValue()));
	}

	/**
	 * Sets mutiplicity flag.
	 * @param value
	 */
	public void setHasMultiplicity(String value) {
		isSingleton = !(value != null && (value.equals("1") || Boolean.valueOf(value).booleanValue()));
	}

	public boolean isSingleton() {
		return isSingleton;
	}

	public List<DomainAttribute> getDomainAttributes() {
		return domainAttributeList;
	}

	public boolean isPersistent() {
		return isPersistent;
	}

	public void addDomainAttribute(DomainAttribute domainattribute) {
		domainAttributeList.add(domainattribute);
	}

	/**
	 * Add the specified class link to this.
	 * @param link
	 * @since PowerEditor 3.2.0
	 */
	public void addDomainClassLink(DomainClassLink link) {
		childClassLinkList.add(link);
	}

	/**
	 * Gets all domain class links in this.
	 * @return list of all domain class links
	 * @since PowerEditor 3.2.0

	 */
	public List<DomainClassLink> getDomainClassLinks() {
		return Collections.unmodifiableList(childClassLinkList);
	}

	/*
	 * Tests if this has an attribute that represents a child class.
	 */
	public boolean hasChildClassAttribute() {
		for (DomainAttribute element : domainAttributeList) {
			if (element.getDeployType() == DeployType.RELATIONSHIP) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Add the specified domain translation to this.
	 * @param translation the domain translation to add
	 * @since PowerEditor 3.2.0
	 */
	public void addDomainTranslation(DomainTranslation translation) {
		translationList.add(translation);
	}

	/**
	 * Gets all domain translations in this.
	 * @return list of domain translations
	 * @since PowerEditor 3.2.0
	 */
	public List<DomainTranslation> getDomainTranslations() {
		return Collections.unmodifiableList(translationList);
	}

	/**
	 * Finds the domain translation with the specified name.
	 * @param name the name of domain translation to find
	 * @return the domain translation, if found; <code>null</code>, otherwise
	 */
	public DomainTranslation getDomainTranslation(String name) {
		for (DomainTranslation element : translationList) {
			if (element.getName().equals(name)) {
				return element;
			}
		}
		return null;
	}

	public void setSuperClass(String s) {
		superClassName = s;
	}

	public String getSuperClass() {
		return superClassName;
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
		buff.append("DomainClass[");
		buff.append(getName());
		buff.append(",");
		buff.append(deployLabel);
		buff.append(",");
		buff.append(getDisplayLabel());
		buff.append(",");
		buff.append(usedInRules);
		buff.append(",");
		buff.append(isSingleton);
		buff.append(",noAttr=");
		buff.append(domainAttributeList.size());
		buff.append("]");
		return buff.toString();
	}

	/**
	 * Sets invariants of this from the specified domain class.
	 * This copies attributes and domain translations from the source.
	 * But, this does not remove attributes or translations of this that do not exist in the source.
	 * Attributes and translations of this that exist in the source will be updated from the source's
	 * attributes and translations.
	 * <b>Note this sets the parent class to that of the source</b>.
	 * @param source the source domain class
	 * @since PowerEditor 3.2.0
	 */
	public void copyFrom(DomainClass source) {
		setDisplayLabel(source.getDisplayLabel());
		this.deployLabel = source.deployLabel;
		this.isPersistent = source.isPersistent;
		this.isSingleton = source.isSingleton;
		this.usedInRules = source.usedInRules;
		this.superClassName = source.superClassName;
		this.childClassLinkList.clear();
		this.childClassLinkList.addAll(source.childClassLinkList);

		// add new attributes
		for (DomainAttribute element : source.domainAttributeList) {
			if (getDomainAttribute(element.getName()) == null) {
				this.domainAttributeList.add(element);
			}
			else {
				this.domainAttributeList.remove(getDomainAttribute(element.getName()));
				this.domainAttributeList.add(element);
			}
		}

		// add new translations
		for (DomainTranslation element : source.translationList) {
			if (getDomainTranslation(element.getName()) == null) {
				this.translationList.add(element);
			}
			else {
				this.translationList.remove(getDomainTranslation(element.getName()));
				this.translationList.add(element);
			}
		}
	}
}
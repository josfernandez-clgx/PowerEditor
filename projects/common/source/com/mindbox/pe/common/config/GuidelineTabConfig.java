package com.mindbox.pe.common.config;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.model.TemplateUsageType;

/**
 * Guideline Tab configuration.
 * This represents a configuration of a guideline tab, as specified in the
 * <code>PowerEditorConfiguration.xml</code> file.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.0.0
 * @see UIConfiguration
 */
public final class GuidelineTabConfig implements Serializable {

	private static final long serialVersionUID = 2003100242000L;

	private String title;
	private final List<TemplateUsageType> usageList;
	
	/**
	 * Constructs a new tab configuration with the specified title.
	 * @param title the text of the tab dipsplayed on the screen
	 */
	public GuidelineTabConfig() {
		this.usageList = new LinkedList<TemplateUsageType>();
	}
	
	public void setDisplayName(String value) {
		this.title = value;
	}

	public String toString() {
		return "GuidelineTabConfig["+title+"]";
	}
	
	/**
	 * Adds the specified usage type string to this.
	 * Method name begins with "set" for digester support.
	 * @param value
	 */
	public void setUsageType(String value) {
		usageList.add(TemplateUsageType.valueOf(value));
	}
	
	/**
	 * Gets the title of this.
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Tests if this is configured for the specified usage type.
	 * @param usageType the usage type to check
	 * @return <code>true</code> if this is configured for <code>usageType</code>; <code>false</code>, otherwise
	 */
	public boolean containsUsageType(TemplateUsageType usageType) {
		for (Iterator<TemplateUsageType> iter = usageList.iterator(); iter.hasNext();) {
			TemplateUsageType element = iter.next();
			if (element == usageType) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the privilege associate with the specified usage type.
	 * @param usageType the usage type privilege for which to return
	 * @return the privilege for the usage type
	 * @throws IllegalArgumentException if this is not configured for <code>usageType</code>
	 */
	public String getPrivilegeFor(TemplateUsageType usageType) {
		for (Iterator<TemplateUsageType> iter = usageList.iterator(); iter.hasNext();) {
			TemplateUsageType element = iter.next();
			if (element == usageType) {
				return element.getPrivilege();
			}
		}
		throw new IllegalArgumentException("Guideline " + title + " does not have usage type " + usageType);
	}

	/**
	 * Gets usage types for this.
	 * @return the usage types
	 */
	public TemplateUsageType[] getUsageTypes() {
		TemplateUsageType[] usageTypes = new TemplateUsageType[usageList.size()];
		for (int i = 0; i < usageTypes.length; i++) {
			usageTypes[i] = usageList.get(i);
		}
		return usageTypes;
	}

}

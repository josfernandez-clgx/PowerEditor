package com.mindbox.pe.common.digest;

/**
 * Represents <TemplateUsageType> elements in PowerEditorConfiguration.xml.
 * @author kim
 *
 */
public class TemplateUsageTypeDigest {

	private String name;
	private String displayName;
	private String privilege;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getPrivilege() {
		return privilege;
	}

	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}


}

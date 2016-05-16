package com.mindbox.pe.common.digest;

/**
 * Represents &lt;TemplateUsageType&gt; elements in PowerEditorConfiguration.xml.
 * @author kim
 *
 */
public class TemplateUsageTypeDigest {

	private String name;
	private String displayName;
	private String privilege;

	public String getDisplayName() {
		return displayName;
	}

	public String getName() {
		return name;
	}

	public String getPrivilege() {
		return privilege;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}


}

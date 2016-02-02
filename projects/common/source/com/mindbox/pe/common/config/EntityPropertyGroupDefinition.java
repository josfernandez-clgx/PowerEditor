package com.mindbox.pe.common.config;

import java.io.Serializable;

/**
 * Entity property group definition.
 * Represents &lt;EntityPropertyGroup&gt; tag in PowerEditorConfiguration.xml.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 4.3.1
 */
public class EntityPropertyGroupDefinition implements Serializable {

	private static final long serialVersionUID = 20050523900002L;

	private String name;
	private String displayName;

	public String getDisplayName() {
		return displayName;
	}

	public String getName() {
		return name;
	}

	public void setDisplayName(String string) {
		displayName = string;
	}

	public void setName(String string) {
		name = string;
	}


	public String toString() {
		return "PropertyGroup[" + name + ",dispName=" + displayName + "]";
	}

}
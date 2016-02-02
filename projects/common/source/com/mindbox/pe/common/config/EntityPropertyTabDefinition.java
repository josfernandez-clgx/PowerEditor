package com.mindbox.pe.common.config;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class EntityPropertyTabDefinition implements Serializable {

	public static boolean isContainedInTab(String propertyName, EntityPropertyTabDefinition[] tabDefs) {
		if (tabDefs != null && tabDefs.length > 0) {
			for (int i = 0; i < tabDefs.length; i++) {
				if (tabDefs[i].hasProperty(propertyName))
					return true;
			}
		}
		return false;
	}

	private static final long serialVersionUID = 2006061200000L;

	private String title;

	private final List<String> nameList = new LinkedList<String>();

	public EntityPropertyTabDefinition() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void addPropertyName(String name) {
		nameList.add(name);
	}

	public String[] getPropertyNames() {
		return nameList.toArray(new String[0]);
	}

	public boolean hasProperty(String name) {
		return nameList.contains(name);
	}

}

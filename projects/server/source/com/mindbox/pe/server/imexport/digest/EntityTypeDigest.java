/*
 * Created on 2004. 3. 18.
 *
 */
package com.mindbox.pe.server.imexport.digest;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class EntityTypeDigest {

	private String type = null;
	private String displayName = null;

	public EntityTypeDigest() {}

	public EntityTypeDigest(String type, String displayName) {
		this.type = type;
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getType() {
		return type;
	}

	public void setDisplayName(String string) {
		displayName = string;
	}

	public void setType(String string) {
		type = string;
	}

	public String toString() {
		return "EntityType[type=" + type + ",name=" + displayName + "]";
	}
}

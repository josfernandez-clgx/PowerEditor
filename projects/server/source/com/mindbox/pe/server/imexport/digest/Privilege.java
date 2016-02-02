/*
 * Created on 2004. 3. 19.
 *
 */
package com.mindbox.pe.server.imexport.digest;


/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class Privilege {

	private int id = -1;
	private String name = null;
	private String displayName = null;
	private String privilegeType = null;

	/**
	 * @return privilegeType
	 * @since 5.0.0
	 */
	public String getPrivilegeType() {
		return privilegeType;
	}

	/**
	 * @param string
	 * @since 5.0.0
	 */
	public void setPrivilegeType(String string) {
		privilegeType = string;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(int i) {
		id = i;
	}

	public void setName(String string) {
		name = string;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String string) {
		displayName = string;
	}
	
	public String toString() {
		return "Privilege["+id+","+name+","+displayName+","+privilegeType+"]";
	}

}

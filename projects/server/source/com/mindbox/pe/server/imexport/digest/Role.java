/*
 * Created on 2004. 3. 19.
 *
 */
package com.mindbox.pe.server.imexport.digest;

import java.util.ArrayList;
import java.util.List;

import com.mindbox.pe.common.UtilBase;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class Role {

	private final List<Integer> privilegeList = new ArrayList<Integer>();
	private int id = -1;
	private String name = null;

	public void addPrivilegeID(String privIDStr) {
		try {
			privilegeList.add(Integer.valueOf(privIDStr));
		}
		catch (Exception ex) {}
	}

	public void setPrivilegeLink(String str) {
		addPrivilegeID(str);
	}
	
	public int[] privilegeIDs() {
		return UtilBase.toIntArray(privilegeList);
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

	public String toString() {
		return "Role["+id+","+name+",priv="+UtilBase.toString(privilegeIDs())+"]";
	}
}

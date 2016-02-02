package com.mindbox.pe.server.imexport.digest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mindbox.pe.common.UtilBase;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class User {

	private final List<Integer> roleList = new ArrayList<Integer>();
	private final List<EntityIdentity> entityLinkList = new ArrayList<EntityIdentity>();
	private final List<UserPassword> passwordHistoryList = new ArrayList<UserPassword>();
	private String id = null;
	private String name = null;
	private String password = null;
	private String status = null;
	private boolean passwordChangeRequired = false;
	private int failedLoginCounter = 0;

	public int[] roleIDs() {
		return UtilBase.toIntArray(roleList);
	}

	public void addRoleID(String roleIDStr) {
		try {
			roleList.add(Integer.valueOf(roleIDStr));
		}
		catch (Exception ex) {}
	}

	public void setRoleLink(String str) {
		addRoleID(str);
	}

	public void addEntityLink(EntityIdentity ei) {
		if (ei != null) {
			entityLinkList.add(ei);
		}
	}

	public List<EntityIdentity> getEntityLinks() {
		return Collections.unmodifiableList(entityLinkList);
	}
	
	public void addUserPassword(UserPassword up) {
		if (up != null) {
			passwordHistoryList.add(up);
		}
	}
	public List<UserPassword> getUserPassword() {
		return Collections.unmodifiableList(passwordHistoryList);
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	public String getStatus() {
	   return status;
	}

	public void setId(String string) {
		id = string;
	}

	public void setName(String string) {
		name = string;
	}

	public void setPassword(String string) {
		password = string;
	}

	public void setStatus(String string) {
		status = string;
	}

	public void setStaus(String string) {
		status = string;
	}
	
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("User[");
		buff.append(id);
		buff.append("=");
		buff.append(name);
		buff.append(",");
		buff.append(password);
		buff.append(",activate?=");
		buff.append(status);
		buff.append(",roles=");
		buff.append(UtilBase.toString(roleIDs()));
		buff.append(",passwordHistoryList=");
		for (int i = 0; i < passwordHistoryList.size(); i++) {
			if (i > 0) {
				buff.append(",");
			}
			buff.append(passwordHistoryList.get(i));
		}
		buff.append("]");
		buff.append(",elink=");
		for (int i = 0; i < entityLinkList.size(); i++) {
			if (i > 0) {
				buff.append(",");
			}
			buff.append(entityLinkList.get(i));
		}
		buff.append("]");
		return buff.toString();
	}

	public int getFailedLoginCounter() {
		return failedLoginCounter;
	}

	public void setFailedLoginCounter(int failedLoginCounter) {
		this.failedLoginCounter = failedLoginCounter;
	}

	public boolean getPasswordChangeRequired() {
		return passwordChangeRequired;
	}

	public void setPasswordChangeRequired(boolean string) {
		this.passwordChangeRequired = string;
	}

}

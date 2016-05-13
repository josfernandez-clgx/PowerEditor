package com.mindbox.pe.server.model;

import java.util.Iterator;
import java.util.List;

public class GridTemplateSecurityInfo {

	public void setAllowedEditRoles(List<String> list) {
		mAllowedEditRoles = list;
	}

	public List<String> getAllowedEditRoles() {
		return mAllowedEditRoles;
	}

	public boolean allowEdit(String s) {
		boolean flag = false;
		List<String> list = getAllowedEditRoles();
		if (list == null)
			return false;
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String s1 = iterator.next();
			if (s1.equalsIgnoreCase("All") || s.equals(s1))
				return true;
		}

		return flag;
	}

	public String toString() {
		return "TemplateSecurityInfo id= "
			+ getTemplateId()
			+ "; AllowedEditRoles="
			+ getAllowedEditRoles()
			+ "; AllowedViewRoles="
			+ getAllowedViewRoles();
	}

	public GridTemplateSecurityInfo(int i, List<String> list, List<String> list1) {
		setTemplateId(i);
		setAllowedEditRoles(list);
		setAllowedViewRoles(list1);
	}

	public void setAllowedViewRoles(List<String> list) {
		mAllowedViewRoles = list;
	}

	public List<String> getAllowedViewRoles() {
		return mAllowedViewRoles;
	}

	public boolean allowView(String s) {
		boolean flag = false;
		List<String> list = getAllowedViewRoles();
		if (list == null)
			return false;
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String s1 = iterator.next();
			if (s1.equalsIgnoreCase("All") || s.equals(s1))
				return true;
		}

		return flag;
	}

	public void setTemplateId(int i) {
		mTemplateId = i;
	}

	public int getTemplateId() {
		return mTemplateId;
	}

	private int mTemplateId;
	private List<String> mAllowedEditRoles;
	private List<String> mAllowedViewRoles;
}
package com.mindbox.pe.client.applet.admin.role;

import java.util.Iterator;
import java.util.List;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.table.AbstractSelectionTableModel;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.admin.Role;

public class RoleSelectionTableModel extends AbstractSelectionTableModel<Role> {

	private static final String PRIVILEGES_SEPARATOR = ", ";

	private static String getPrivilegesString(Role role) {
		List<Privilege> list = role.getPrivileges();
		if (list == null || list.size() == 0) return "";
		Iterator<Privilege> iterator = list.iterator();
		StringBuffer stringbuffer = new StringBuffer();
		boolean flag = true;
		Privilege privilege;
		for (; iterator.hasNext(); stringbuffer.append(privilege.getDisplayString())) {
			privilege = iterator.next();
			if (!flag)
				stringbuffer.append(PRIVILEGES_SEPARATOR);
			else
				flag = false;
		}

		return stringbuffer.toString();
	}

	public RoleSelectionTableModel() {
		super(ClientUtil.getInstance().getLabel("label.name"), ClientUtil.getInstance().getLabel("label.privileges"));
	}

	@Override
	public Object getValueAt(int i, int j) {
		if (dataList == null || dataList.size() < i) {
			return null;
		}
		Role role = dataList.get(i);
		if (j < 0) return role;
		switch (j) {
		case 0: // '\0'
			return role.getName();

		case 1: // '\001'
			return getPrivilegesString(role);
		}
		return role;
	}

	void updateRole(Role role) {
		if (role == null) return;
		Role role1 = null;
		for (int i = 0; i < dataList.size(); i++) {
			Role role2 = dataList.get(i);
			if (!role2.equals(role)) continue;
			role1 = role2;
			break;
		}

		if (role1 == null) {
			addData(role);
			return;
		}
		else {
			// TT 2127 - only copy if role1 and role are not the same instance. 
			if (role1 != role) {
				role1.setName(role.getName());
				role1.setPrivileges(role.getPrivileges());
			}
			refreshData();
			return;
		}
	}

}

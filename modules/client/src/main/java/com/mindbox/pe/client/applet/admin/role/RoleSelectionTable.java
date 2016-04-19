package com.mindbox.pe.client.applet.admin.role;

import java.awt.Insets;
import java.util.List;

import com.mindbox.pe.common.ui.AbstractSortableTable;
import com.mindbox.pe.model.admin.Role;

public class RoleSelectionTable extends AbstractSortableTable<RoleSelectionTableModel, Role> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public RoleSelectionTable(RoleSelectionTableModel model) {
		super(model);
		model.setDataList(new java.util.ArrayList<Role>());
		initTable();
	}

	public int getSelectedRoleId() {
		Role role = getSelectedDataObject();
		return (role == null ? -1 : role.getId());
	}

	protected void initTable() {
		setAutoCreateColumnsFromModel(false);
		setAutoResizeMode(2);
		setRowSelectionAllowed(true);
		setRowHeight(22);
	}

	public void setRoles(List<Role> list) {
		getSelectionTableModel().setDataList(list);
	}

	public Insets getInsets() {
		return new Insets(3, 1, 1, 0);
	}

	protected void displaySelectedRowDetails() {
		getSelectedRow();
	}

	private Role getRoleAt(int rowInView) {
		Role role = null;

		if (rowInView >= 0) role = (Role) getModel().getValueAt(convertRowIndexToModel(rowInView), -1);
		return role;
	}

	public Role getSelectedRole() {
		int i = getSelectedRow();

		return getRoleAt(i);
	}

	public void removeRole(Role role) {
		getSelectionTableModel().removeData(role);
	}

	@Override
	protected void initColumns(String[] columnNames) {
		super.initColumns(columnNames);
		getColumnModel().getColumn(0).setPreferredWidth(100);
		getColumnModel().getColumn(1).setPreferredWidth(300);
	}

	public void updateRole(Role role) {
		getSelectionTableModel().updateRole(role);
	}

}

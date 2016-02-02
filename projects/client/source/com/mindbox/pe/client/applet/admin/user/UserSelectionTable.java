package com.mindbox.pe.client.applet.admin.user;

import java.awt.Insets;
import java.util.List;

import com.mindbox.pe.client.common.table.AbstractSortableTable;
import com.mindbox.pe.model.admin.UserData;

public class UserSelectionTable extends AbstractSortableTable<UserSelectionTableModel, UserData> {

	public UserSelectionTable(UserSelectionTableModel tableModel) {
		super(tableModel);
		initTable();
	}

	public Insets getInsets() {
		return new Insets(3, 1, 1, 0);
	}

	public void setUsers(List<UserData> list) {
		getSelectionTableModel().setDataList(list);
	}

	protected void displaySelectedRowDetails() {
		getSelectedRow();
	}

	public String getSelectedUserId() {
		UserData userData = getSelectedDataObject();
		return (userData == null ? null : userData.getName());
	}

	public UserData getSelectedUser() {
		return getSelectedDataObject();
	}

	public void removeUser(UserData userdata) {
		getSelectionTableModel().removeData(userdata);
	}

	@Override
	protected void initColumns(String[] columnNames) {
		super.initColumns(columnNames);
		getColumnModel().getColumn(0).setPreferredWidth(100);
		getColumnModel().getColumn(1).setPreferredWidth(200);
		getColumnModel().getColumn(2).setPreferredWidth(70);
	}

	public void updateUser(UserData userdata) {
		getSelectionTableModel().updateUser(userdata);
	}

	protected void initTable() {
		setAutoCreateColumnsFromModel(false);
		setAutoResizeMode(2);
		setRowSelectionAllowed(true);
		setRowHeight(22);
		getSelectionTableModel().setDataList(new java.util.ArrayList<UserData>());
	}
}

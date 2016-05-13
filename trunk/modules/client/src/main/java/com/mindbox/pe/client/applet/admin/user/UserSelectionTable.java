package com.mindbox.pe.client.applet.admin.user;

import java.awt.Insets;
import java.util.List;

import com.mindbox.pe.common.ui.AbstractSortableTable;
import com.mindbox.pe.model.admin.UserData;

public class UserSelectionTable extends AbstractSortableTable<UserSelectionTableModel, UserData> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public UserSelectionTable(UserSelectionTableModel tableModel) {
		super(tableModel);
		initTable();
	}

	protected void displaySelectedRowDetails() {
		getSelectedRow();
	}

	public Insets getInsets() {
		return new Insets(3, 1, 1, 0);
	}

	public UserData getSelectedUser() {
		return getSelectedDataObject();
	}

	public String getSelectedUserId() {
		UserData userData = getSelectedDataObject();
		return (userData == null ? null : userData.getName());
	}

	@Override
	protected void initColumns(String[] columnNames) {
		super.initColumns(columnNames);
		getColumnModel().getColumn(0).setPreferredWidth(100);
		getColumnModel().getColumn(1).setPreferredWidth(200);
		getColumnModel().getColumn(2).setPreferredWidth(70);
		getColumnModel().getColumn(3).setPreferredWidth(80);
	}

	protected void initTable() {
		setAutoCreateColumnsFromModel(false);
		setAutoResizeMode(2);
		setRowSelectionAllowed(true);
		setRowHeight(22);
		getSelectionTableModel().setDataList(new java.util.ArrayList<UserData>());
	}

	public void removeUser(UserData userdata) {
		getSelectionTableModel().removeData(userdata);
	}

	public void setUsers(List<UserData> list) {
		getSelectionTableModel().setDataList(list);
	}

	public void updateUser(UserData userdata) {
		getSelectionTableModel().updateUser(userdata);
	}
}

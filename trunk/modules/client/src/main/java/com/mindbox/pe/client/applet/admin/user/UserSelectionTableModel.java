package com.mindbox.pe.client.applet.admin.user;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.ui.AbstractSelectionTableModel;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.admin.UserData;

public class UserSelectionTableModel extends AbstractSelectionTableModel<UserData> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7340238169089833005L;

	public UserSelectionTableModel() {
		super(ClientUtil.getInstance().getLabel("label.userid"), ClientUtil.getInstance().getLabel("label.name"), ClientUtil.getInstance().getLabel("label.status"), ClientUtil.getInstance().getLabel(
				"label.failed.login.counter"));
	}

	private String getStatus(UserData userData) {
		if (UtilBase.asBoolean(ClientUtil.getUserInterfaceConfig().isAllowDisableEnableUser(), false)) {
			return userData.isDisabled() ? Constants.DISABLED_STATUS : userData.getStatus();
		}
		else {
			return userData.getStatus();
		}
	}

	@Override
	public Object getValueAt(int i, int j) {
		if (dataList == null || dataList.size() < i) {
			return null;
		}
		UserData userData = dataList.get(i);
		if (j < 0) {
			return userData;
		}
		switch (j) {
		case 0:
			return userData.getUserID();

		case 1:
			return userData.getName();

		case 2:
			return getStatus(userData);
		case 3:
			return userData.getFailedLoginCounter();
		default:
			return userData;
		}
	}

	void updateUser(final UserData userdata) {
		if (userdata == null) {
			return;
		}
		UserData userdata1 = null;
		for (int i = 0; i < dataList.size(); i++) {
			UserData userdata2 = dataList.get(i);
			if (!userdata2.equals(userdata)) {
				continue;
			}
			userdata1 = userdata2;
			break;
		}

		if (userdata1 == null) {
			addData(userdata);
			return;
		}
		userdata1.setName(userdata.getName());
		if (!userdata1.getStatus().equals(userdata.getStatus())) {
			userdata1.setStatus(userdata.getStatus());
		}
		userdata1.setFailedLoginCounter(userdata.getFailedLoginCounter());
		refreshData();
	}
}

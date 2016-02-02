package com.mindbox.pe.client.applet.admin.request;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.table.IDNameDescriptionObjectSelectionTableModel;
import com.mindbox.pe.model.process.ProcessRequest;


/**
 * 
 *
 * @author kim
 * @since PowerEditor  3.3.0
 */
class RequestSelectionTableModel extends IDNameDescriptionObjectSelectionTableModel<ProcessRequest> {

	public RequestSelectionTableModel() {
		super(
				ClientUtil.getInstance().getLabel("label.name.display"),
				ClientUtil.getInstance().getLabel("label.type.request"),
				ClientUtil.getInstance().getLabel("label.desc"),
				ClientUtil.getInstance().getLabel("label.phase"));
	}

	@Override
	public Object getValueAt(int row, int col) {
		ProcessRequest value = dataList.get(row);
		switch (col) {
		case 0:
			return value.getDisplayName();
		case 1:
			return value.getRequestType();
		case 2:
			return value.getDescription();
		case 3:
			return (value.getPhase() == null ? "" : value.getPhase().getDisplayName());
		default:
			return value;
		}
	}
}

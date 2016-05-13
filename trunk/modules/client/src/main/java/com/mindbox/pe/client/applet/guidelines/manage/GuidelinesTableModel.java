package com.mindbox.pe.client.applet.guidelines.manage;

import java.util.List;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.ui.AbstractSelectionTableModel;
import com.mindbox.pe.model.GridSummary;

public class GuidelinesTableModel extends AbstractSelectionTableModel<GridSummary> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private static String[] getColumnNamesToUse() {
		List<String> nameList = new java.util.ArrayList<String>();
		nameList.add("<html><body><b>" + ClientUtil.getInstance().getLabel("col.guideline.template") + "</b></body></html>");
		if (showTemplateID()) {
			nameList.add("<html><body><b>" + ClientUtil.getInstance().getLabel("col.guideline.template.id") + "</b></body></html>");
		}
		nameList.add("<html><body><b>" + ClientUtil.getInstance().getLabel("col.guideline.has.data") + "</b></body></html>");
		nameList.add("<html><body><b>" + ClientUtil.getInstance().getLabel("col.guideline.common") + "</b></body></html>");
		nameList.add("<html><body><b>" + ClientUtil.getInstance().getLabel("col.guideline.editable") + "</b></body></html>");
		nameList.add("<html><body><b>" + ClientUtil.getInstance().getLabel("col.guideline.locked") + "</b></body></html>");
		nameList.add("<html><body><b>" + ClientUtil.getInstance().getLabel("col.guideline.subcontext") + "</b></body></html>");
		return nameList.toArray(new String[0]);
	}

	private static boolean showTemplateID() {
		return ClientUtil.getUserInterfaceConfig().getGuideline() != null && UtilBase.asBoolean(ClientUtil.getUserInterfaceConfig().getGuideline().isShowTemplateID(), false);
	}

	public GuidelinesTableModel() {
		super(getColumnNamesToUse());
	}

	public Class<?> getColumnClass(int i) {
		if (showTemplateID()) {
			switch (i) {
			case 0: // '\0'
				return String.class;
			case 1: // '\0'
				return String.class;
			case 2: // '\001'
			case 3: // '\002'
			case 4: // '\003'
			case 5: // '\004'
			case 6: // '\004'
				return Boolean.class;
			}
		}
		else {
			switch (i) {
			case 0: // '\0'
				return String.class;
			case 1: // '\001'
			case 2: // '\002'
			case 3: // '\003'
			case 4: // '\004'
			case 5: // '\004'
				return Boolean.class;
			}
		}

		return Object.class;
	}

	@Override
	public Object getValueAt(int i, int j) {
		if (dataList.size() < i) {
			return null;
		}
		GridSummary gridsummary = (GridSummary) dataList.get(i);
		if (j < 0) return gridsummary;
		if (showTemplateID()) {
			switch (j) {
			case 1:
				return String.valueOf(gridsummary.getTemplateID());
			case 0:
				return gridsummary.getName() + " (" + gridsummary.getVerion() + ")";

			case 2:
				return new Boolean(gridsummary.hasGridInstantiations());

			case 3:
				return new Boolean(gridsummary.isCommon());

			case 4:
				return new Boolean(gridsummary.isEditAllowed());

			case 5:
				return new Boolean(gridsummary.isLocked());
			case 6:
				return new Boolean(gridsummary.isSubContext());
			}
		}
		else {
			switch (j) {
			case 0: // '\0'
				return gridsummary.getName() + " (" + gridsummary.getVerion() + ")";

			case 1: // '\001'
				return new Boolean(gridsummary.hasGridInstantiations());

			case 2: // '\002'
				return new Boolean(gridsummary.isCommon());

			case 3: // '\003'
				return new Boolean(gridsummary.isEditAllowed());

			case 4: // '\004'
				return new Boolean(gridsummary.isLocked());
			case 5:
				return new Boolean(gridsummary.isSubContext());
			}
		}
		return gridsummary;
	}

}
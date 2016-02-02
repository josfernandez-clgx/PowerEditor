/*
 * Created on Jun 30, 2003
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.client.common;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.comparator.UsageTypeComparator;

/**
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public final class UsageTypeComboBox extends JComboBox {
	
	public static class UsageTypeCellRenderer extends JLabel implements ListCellRenderer {

		public UsageTypeCellRenderer(String imageKey) {
			if (imageKey != null) {
				setIcon(ClientUtil.getInstance().makeImageIcon(imageKey));
			}
			setOpaque(true);
		}
		
		public Component getListCellRendererComponent(JList arg0, Object value, int index, boolean isSelected, boolean arg4) {
			if (value == null) {
				setText("");
			}
			else if (value instanceof TemplateUsageType) {
				this.setText(((TemplateUsageType)value).getDisplayName());
			}
			else {
				this.setText(value.toString());
			}
			setBackground(isSelected ? PowerEditorSwingTheme.primary3 : Color.white);
			return this;
		}
	}

	/**
	 * Creates an instance of UsageTypeCombo box and contains only those UsageType's on whose privilege, 
	 * user has Edit Template permission. 
	 * @return UsageTypeComboBox
	 */
	public static UsageTypeComboBox createInstance() {
		TemplateUsageType[] usageTypes = getAllInstancesWithEditTemplatePermission();
		Arrays.sort(usageTypes, UsageTypeComparator.getInstance());
		return new UsageTypeComboBox(usageTypes);
	}
	
	/**
	 * Returns an array that contains all UsageType's on whose privilege, user has
	 * Edit Template permission.
	 * @return TemplateUsageType[]
	 * @since 5.0.0
	 */	
	private static TemplateUsageType[] getAllInstancesWithEditTemplatePermission() {
		List<TemplateUsageType> list = new ArrayList<TemplateUsageType>();
		TemplateUsageType[] allTypes = TemplateUsageType.getAllInstances();
		for (int i = 0; i < allTypes.length; i++) {
			if (ClientUtil.checkEditTemplatePermission(allTypes[i])) {
				list.add(allTypes[i]);
			}
		}
		return list.toArray(new TemplateUsageType[0]);
	}

	private UsageTypeComboBox(TemplateUsageType[] usageTypes) {
		super(usageTypes);
		UIFactory.setLookAndFeel(this);
		setRenderer(new UsageTypeCellRenderer(null));
	}


	public TemplateUsageType getSelectedUsage() {
		Object obj = super.getSelectedItem();
		if (obj instanceof TemplateUsageType) {
			return (TemplateUsageType) obj;
		}
		else {
			return null;
		}
	}
	
	public void clearSelection() {
		setSelectedIndex(0);
	}

	public void selectUsage(TemplateUsageType usage) {
		if (usage == null) return;
		setSelectedItem(usage);
	}
}
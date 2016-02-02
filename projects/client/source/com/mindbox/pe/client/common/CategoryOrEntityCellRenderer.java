package com.mindbox.pe.client.common;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JList;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;

public class CategoryOrEntityCellRenderer extends IDNameObjectCellRenderer {

	private final ImageIcon catIcon, entityIcon;

	public CategoryOrEntityCellRenderer() {
		super(null);
		catIcon = ClientUtil.getInstance().makeImageIcon("image.node.category");
		entityIcon = ClientUtil.getInstance().makeImageIcon("image.node.entity");
	}

	public Component getListCellRendererComponent(JList arg0, Object value, int index, boolean isSelected, boolean arg4) {
		if (value != null) {
			if (value instanceof GenericEntity) {
				setIcon(entityIcon);
			}
			else if (value instanceof GenericCategory) {
				setIcon(catIcon);
			}
		}
		return super.getListCellRendererComponent(arg0, value, index, isSelected, arg4);
	}
}

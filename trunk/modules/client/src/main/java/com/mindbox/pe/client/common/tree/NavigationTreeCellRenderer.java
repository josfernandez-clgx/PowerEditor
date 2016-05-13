package com.mindbox.pe.client.common.tree;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.model.IDNameObject;

class NavigationTreeCellRenderer extends DefaultTreeCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private final ImageIcon categoryIcon = ClientUtil.getInstance().makeImageIcon("image.node.category");
	private final ImageIcon entityIcon = ClientUtil.getInstance().makeImageIcon("image.node.entity");

	NavigationTreeCellRenderer() {
		setIcon(categoryIcon);
	}

	public Component getTreeCellRendererComponent(JTree jtree, Object obj, boolean flag, boolean flag1, boolean flag2, int i, boolean flag3) {
		Object obj1 = ((DefaultMutableTreeNode) obj).getUserObject();
		if (obj1 instanceof IDNameObject) obj1 = ((IDNameObject) obj1).getName();
		if (obj instanceof GenericCategoryNode) {
			setIcon(categoryIcon);
		}
		else if (obj instanceof GenericEntityNode) {
			setIcon(entityIcon);
		}
		Component component = super.getTreeCellRendererComponent(jtree, obj1, flag, flag1, flag2, i, flag3);
		return component;
	}

	private void setIcon(ImageIcon icon) {
		setOpenIcon(icon);
		setClosedIcon(icon);
		setDisabledIcon(icon);
		setLeafIcon(icon);
	}
}
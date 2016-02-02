/*
 * Created on 2003. 12. 17.
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.mindbox.pe.client.common.tree;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.mindbox.pe.client.ClientUtil;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
public class DomainClassAttributeTreeRenderer extends DefaultTreeCellRenderer {

	private final Icon classIcon;
	private final Icon attribIcon;

	/**
	 * 
	 */
	public DomainClassAttributeTreeRenderer() {
		super();
		classIcon = ClientUtil.getInstance().makeImageIcon("image.node.domain.class");
		attribIcon = ClientUtil.getInstance().makeImageIcon("image.node.domain.attribute");
		setLeafIcon(attribIcon);
		setClosedIcon(attribIcon);
		setOpenIcon(attribIcon);
	}

	/* (non-Javadoc)
	 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	public Component getTreeCellRendererComponent(JTree arg0, Object arg1, boolean arg2, boolean arg3, boolean arg4, int arg5, boolean arg6) {
		if (arg1 instanceof AttributeTreeNode) {
			setLeafIcon(attribIcon);
			setClosedIcon(attribIcon);
			setOpenIcon(attribIcon);
		}
		else {
			setLeafIcon(classIcon);
			setClosedIcon(classIcon);
			setOpenIcon(classIcon);
		}
		return super.getTreeCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
	}

}
package com.mindbox.pe.client.common.tree;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;

/**
 * Template tree renderer.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
public class TemplateTreeRenderer extends DefaultTreeCellRenderer {

	private final Icon templateIcon, openIcon, closedIcon;

	/**
	 *
	 */
	public TemplateTreeRenderer() {
		super();
		templateIcon = ClientUtil.getInstance().makeImageIcon("image.node.template");
		closedIcon = ClientUtil.getInstance().makeImageIcon("image.node.folder.closed");
		openIcon = ClientUtil.getInstance().makeImageIcon("image.node.folder.open");
	}

	public Component getTreeCellRendererComponent(JTree arg0, Object obj, boolean selected, boolean expanded, boolean leaf, int arg5, boolean arg6) {
		setBackground((selected ? PowerEditorSwingTheme.blueShadowColor : PowerEditorSwingTheme.whiteColor));

		super.getTreeCellRendererComponent(arg0, obj, selected, expanded, leaf, arg5, arg6);
		if (obj instanceof TemplateTreeNode) {
			setIcon(templateIcon);
			if (((TemplateTreeNode) obj).getTemplate() == null) {
				setText("Error: Template not found; please refresh");
			}
			else {
				setText(((TemplateTreeNode) obj).getTemplate().getName() + " (" + ((TemplateTreeNode) obj).getTemplate().getVersion() + ")");
			}
		}
		else {
			setIcon((expanded ? openIcon : closedIcon));
		}
		return this;
	}

}
package com.mindbox.pe.client.applet.template.tree;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.rule.CompoundLHSElement;

/**
 *
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
public class RuleTreeRenderer2 extends JLabel implements TreeCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private final ImageIcon testIcon = ClientUtil.getInstance().makeImageIcon("image.node.adhoc.test");
	private final ImageIcon condIcon = ClientUtil.getInstance().makeImageIcon("image.node.adhoc.cond");

	private final ImageIcon selectedTestIcon = ClientUtil.getInstance().makeImageIcon("image.node.adhoc.select.test");
	private final ImageIcon selectedCondIcon = ClientUtil.getInstance().makeImageIcon("image.node.adhoc.select.cond");

	private final ImageIcon selectedIfIcon = ClientUtil.getInstance().makeImageIcon("image.node.adhoc.select.if");
	private final ImageIcon selectedThenIcon = ClientUtil.getInstance().makeImageIcon("image.node.adhoc.select.then");

	private final ImageIcon actionIcon = ClientUtil.getInstance().makeImageIcon("image.node.adhoc.action");
	private final ImageIcon selectedActionIcon = ClientUtil.getInstance().makeImageIcon("image.node.adhoc.select.action");
	private final ImageIcon paramIcon = ClientUtil.getInstance().makeImageIcon("image.node.adhoc.param");
	private final ImageIcon selectedParamIcon = ClientUtil.getInstance().makeImageIcon("image.node.adhoc.select.param");

	private final ImageIcon existIcon = ClientUtil.getInstance().makeImageIcon("image.node.adhoc.exist");
	private final ImageIcon selectedExistIcon = ClientUtil.getInstance().makeImageIcon("image.node.adhoc.exist");

	public RuleTreeRenderer2() {
		super();
		this.setOpaque(true);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean arg6) {

		setBackground((selected ? PowerEditorSwingTheme.blueShadowColor : PowerEditorSwingTheme.whiteColor));

		if (value instanceof IfTreeNode) {
			setIcon(selectedIfIcon);
			setText(" ");
		}
		else if (value instanceof ThenTreeNode) {
			setIcon(selectedThenIcon);
			setText(" ");
		}
		else if (value instanceof ExistTreeNode) {
			setIcon((selected ? selectedExistIcon : existIcon));
			String cn = ((ExistTreeNode) value).getExistClassName();
			DomainClass dc = (cn == null ? null : DomainModel.getInstance().getDomainClass(cn));
			StringBuilder buff = new StringBuilder((dc == null ? cn : dc.getDisplayLabel()));
			if (((ExistTreeNode) value).getExistExpression().getObjectName() != null) {
				buff.append(" ");
				buff.append(((ExistTreeNode) value).getExistExpression().getObjectName());
			}
			if (((ExistTreeNode) value).getExistExpression().getExcludedObjectName() != null) {
				buff.append(" excluding ");
				buff.append(((ExistTreeNode) value).getExistExpression().getExcludedObjectName());
			}
			buff.append(" with");
			setText(buff.toString());
		}
		else if (value instanceof LogicalOpTreeNode) {
			switch (((LogicalOpTreeNode) value).getCompoundLHSElementType()) {
			case CompoundLHSElement.TYPE_AND:
				//setIcon((selected ? selectedAndIcon : andIcon));
				setIcon(null);
				setText("<html><body><b>AND</b></body></html>");
				break;
			case CompoundLHSElement.TYPE_OR:
				//setIcon((selected ? selectedOrIcon : orIcon));
				setIcon(null);
				setText("<html><body><b>OR</b></body></html>");
				break;
			case CompoundLHSElement.TYPE_NOT:
				//setIcon((selected ? selectedNotIcon : notIcon));
				setIcon(null);
				setText("<html><body><font color=\"red\"><b>NOT</b></font></body></html>");
				break;
			}
		}
		else if (value instanceof ConditionTreeNode) {
			setIcon((selected ? selectedCondIcon : condIcon));
			setText(((AbstractRuleTreeNode) value).dispString(selected));
		}
		else if (value instanceof TestTreeNode) {
			setIcon(null);
			setIcon((selected ? selectedTestIcon : testIcon));
			setText(((TestTreeNode) value).dispString());
		}
		else if (value instanceof ActionTreeNode) {
			setIcon(null);
			setIcon((selected ? selectedActionIcon : actionIcon));
			setText(((ActionTreeNode) value).dispString());
		}
		else if (value instanceof ActionParamTreeNode) {
			setIcon(null);
			setIcon((selected ? selectedParamIcon : paramIcon));
			setText(((ActionParamTreeNode) value).dispString(selected));
		}
		else if (value instanceof AbstractRuleTreeNode) {
			setText(((AbstractRuleTreeNode) value).dispString(selected));
		}
		return this;
	}

}

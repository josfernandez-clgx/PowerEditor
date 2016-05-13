/*
 * Created on 2004. 2. 12.
 *
 */
package com.mindbox.pe.client.applet.template.tree;

import java.text.MessageFormat;

import javax.swing.tree.TreeNode;

import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.model.rule.FunctionParameter;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class ActionParamTreeNode extends AbstractRuleTreeNode {

	private static final MessageFormat FORMAT_NOT_SELECTED = new MessageFormat(
			"<html><body><b>{0}</b>&nbsp;&nbsp;:&nbsp;&nbsp;<b><font color=\"blue\">{1}</font></b></body></html>");
	private static final MessageFormat FORMAT_SELECTED = new MessageFormat("<html><body><b>{0}</b>&nbsp;&nbsp;:&nbsp;&nbsp;<b><font color=\"blue\">{1}</font></b></body></html>");

	public ActionParamTreeNode(TreeNode parent, FunctionParameter data) {
		super(parent, data);
	}

	@Override
	public String dispString(boolean selected) {
		if (super.data != null) {
			if (selected) {
				return FORMAT_SELECTED.format(
						new Object[] {
								getFunctionParameter().toDisplayName(),
								getFunctionParameter().displayString(DomainModel.getInstance()) == null ? "" : getFunctionParameter().displayString(DomainModel.getInstance()) });
			}
			else {
				return FORMAT_NOT_SELECTED.format(
						new Object[] {
								getFunctionParameter().toDisplayName(),
								getFunctionParameter().displayString(DomainModel.getInstance()) == null ? "" : getFunctionParameter().displayString(DomainModel.getInstance()) });
			}
		}
		else {
			return "";
		}
	}

	public FunctionParameter getFunctionParameter() {
		return (FunctionParameter) super.data;
	}

	public void setFunctionParameter(FunctionParameter data) {
		super.data = data;
	}
}

/*
 * Created on 2004. 1. 28.
 *
 */
package com.mindbox.pe.client.applet.template.tree;

import java.text.MessageFormat;

import javax.swing.tree.TreeNode;

import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.common.grid.MultiSelectEnumCellRenderer;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.MathExpressionValue;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.rule.Value;
import com.mindbox.pe.model.table.EnumValues;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class ConditionTreeNode extends AbstractRuleTreeNode {

	private static final MessageFormat FORMAT_NOT_SELECTED = new MessageFormat(
			"<html><body><b>{0}</b>&nbsp;&nbsp;{1}&nbsp;&nbsp;<b><font color=\"blue\">{2}</font></b></body></html>");
	private static final MessageFormat FORMAT_SELECTED = new MessageFormat("<html><body><b>{0}</b>&nbsp;&nbsp;{1}&nbsp;&nbsp;<b><font color=\"blue\">{2}</font></b></body></html>");


	public ConditionTreeNode(TreeNode parent, Condition data) {
		super(parent, data);
	}

	private String attRefDisplayString(Reference ref) {
		if (ref != null && ref.getAttributeName() != null && ref.getClassName() != null) {
			DomainClass dc = DomainModel.getInstance().getDomainClass(ref.getClassName());
			if (dc != null) {
				DomainAttribute da = dc.getDomainAttribute(ref.getAttributeName());
				if (da != null) return "[" + dc.getDisplayLabel() + " : " + da.getDisplayLabel() + "]";
			}
		}
		return "";
	}

	@Override
	public String dispString(boolean selected) {
		if (selected) {
			return FORMAT_SELECTED.format(
					new Object[] {
							attRefDisplayString(getCondition().getReference()),
							Condition.Aux.toOpStringForHTML(getCondition().getOp()),
							getCondition().isUnary() ? "" : (getCondition().getValue() == null ? "nil" : valueDisplayString(getCondition().getValue())) });
		}
		else {
			return FORMAT_NOT_SELECTED.format(
					new Object[] {
							attRefDisplayString(getCondition().getReference()),
							Condition.Aux.toOpStringForHTML(getCondition().getOp()),
							getCondition().isUnary() ? "" : (getCondition().getValue() == null ? "nil" : valueDisplayString(getCondition().getValue())) });
		}
	}


	@Override
	public boolean getAllowsChildren() {
		return false;
	}

	public Condition getCondition() {
		return (Condition) super.data;
	}

	@SuppressWarnings("unchecked")
	private String valueDisplayString(Value val) {
		if (val instanceof Reference)
			return attRefDisplayString((Reference) val);
		else if (val instanceof MathExpressionValue) {
			MathExpressionValue mathVal = (MathExpressionValue) val;
			return (mathVal.getColumnReference() == null ? (mathVal.getValue() == null ? "" : mathVal.getValue()) : mathVal.getColumnReference().toString()) + " "
					+ mathVal.getOperator() + " " + attRefDisplayString(mathVal.getAttributeReference());

		}
		else if (val instanceof EnumValues) {
			return MultiSelectEnumCellRenderer.toDisplayString((EnumValues<EnumValue>) val);
		}
		else
			return val.toString();
	}
}

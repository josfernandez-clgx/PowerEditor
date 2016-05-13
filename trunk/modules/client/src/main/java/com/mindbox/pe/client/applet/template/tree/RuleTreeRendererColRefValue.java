package com.mindbox.pe.client.applet.template.tree;

import java.awt.Component;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.common.grid.AbstractCategoryEntityCellRenderer;
import com.mindbox.pe.client.common.grid.CategoryEntityMultiSelectCellRenderer;
import com.mindbox.pe.client.common.grid.MultiSelectEnumCellRenderer;
import com.mindbox.pe.common.format.FloatFormatter;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.rule.ColumnReference;
import com.mindbox.pe.model.rule.CompoundLHSElement;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.FunctionParameter;
import com.mindbox.pe.model.rule.MathExpressionValue;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.rule.Value;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.FloatRange;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;

/**
 *
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 2.1.0
 */
public class RuleTreeRendererColRefValue extends JLabel implements TreeCellRenderer {

	private static final long serialVersionUID = -1904823220501593362L;

	private static final MessageFormat FORMAT_COND_HIGHLIGHTED = new MessageFormat("<html><body><b>{0}</b>&nbsp;&nbsp;{1}&nbsp;&nbsp;<b><font color=\"blue\">{2}</font></b></body></html>");

	private static final MessageFormat FORMAT_COND_MATH = new MessageFormat(
			"<html><body><b>{0}</b>&nbsp;&nbsp;{1}&nbsp;&nbsp;<b><font color=\"blue\">{2}</font></b>&nbsp;{3}&nbsp;<b>{4}</b></body></html>");

	private static final MessageFormat FORMAT_COND_NORMAL = new MessageFormat("<html><body>{0}&nbsp;&nbsp;{1}&nbsp;{2}</body></html>");

	private static final MessageFormat FORMAT_PARAM_HIGHLIGHTED = new MessageFormat("<html><body><b>{0}</b>&nbsp;&nbsp;:&nbsp;&nbsp;<b><font color=\"blue\">{1}</font></b></body></html>");

	private static final MessageFormat FORMAT_PARAM_UNHIGHLIGHTED = new MessageFormat("<html><body>{0}&nbsp;&nbsp;:&nbsp;&nbsp;{1}</body></html>");

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

	private List<Object> cellValueList = null;
	private GridTemplate template = null;

	/**
	 *
	 */
	public RuleTreeRendererColRefValue() {
		super();
		this.setOpaque(true);
	}

	public void setCellValues(List<Object> values) {
		this.cellValueList = values;
	}

	@SuppressWarnings("unchecked")
	public String getCellValue(int column) {
		if (cellValueList == null || cellValueList.size() < column) {
			return "";
		}
		else {
			Object value = cellValueList.get(column - 1);
			if (value == null) return "";
			if (value instanceof CategoryOrEntityValue) {
				return AbstractCategoryEntityCellRenderer.getDisplayValue((CategoryOrEntityValue) value);
			}
			else if (value instanceof CategoryOrEntityValues) {
				return CategoryEntityMultiSelectCellRenderer.getDisplayValue((CategoryOrEntityValues) value, true, true);
			}
			else if (value instanceof EnumValues) {
				return MultiSelectEnumCellRenderer.toDisplayString((EnumValues<EnumValue>) value);
			}
			else if (value instanceof EnumValue) {
				return ((EnumValue) value).getDisplayLabel();
			}
			else if (value instanceof Double) {
				AbstractTemplateColumn col = template.getColumn(column);
				ColumnDataSpecDigest colDataSpec = col.getColumnDataSpecDigest();
				FloatFormatter formatter = new FloatFormatter(colDataSpec.getPrecision());
				return formatter.format((Double) value);
			}
			else if (value instanceof FloatRange) {
				AbstractTemplateColumn col = template.getColumn(column);
				ColumnDataSpecDigest colDataSpec = col.getColumnDataSpecDigest();
				FloatRange floatRange = (FloatRange) value;
				FloatFormatter formatter = new FloatFormatter(colDataSpec.getPrecision());
				return (floatRange.isLowerValueInclusive() ? "[" : "(") + floatRange.formatValue(formatter.format(floatRange.getLowerValue())) + '-'
						+ floatRange.formatValue(formatter.format(floatRange.getUpperValue())) + (floatRange.isUpperValueInclusive() ? "]" : ")");
			}
			else {
				return value.toString();
			}
		}
	}

	public void updateCellValue(int column, Object value) {
		cellValueList.set(column - 1, value);
	}

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
			setText(((ExistTreeNode) value).getExistClassName() + " with");
		}
		else if (value instanceof LogicalOpTreeNode) {
			switch (((LogicalOpTreeNode) value).getCompoundLHSElementType()) {
			case CompoundLHSElement.TYPE_AND:
				setIcon(null);
				setText("<html><body><b>AND</b></body></html>");
				break;
			case CompoundLHSElement.TYPE_OR:
				setIcon(null);
				setText("<html><body><b>OR</b></body></html>");
				break;
			case CompoundLHSElement.TYPE_NOT:
				setIcon(null);
				setText("<html><body><font color=\"red\"><b>NOT</b></font></body></html>");
				break;
			}
		}
		else if (value instanceof ConditionTreeNode) {
			setIcon((selected ? selectedCondIcon : condIcon));
			Value condValue = ((ConditionTreeNode) value).getCondition().getValue();
			int op = ((ConditionTreeNode) value).getCondition().getOp();
			String attRefStr = attRefDisplayString(((ConditionTreeNode) value).getCondition().getReference());
			if (((ConditionTreeNode) value).getCondition().isUnary()) {
				setText(FORMAT_COND_HIGHLIGHTED.format(new Object[] { attRefStr, Condition.Aux.toOpStringForHTML(op), "" }));
			}
			else if (condValue instanceof ColumnReference) {
				setText(FORMAT_COND_HIGHLIGHTED.format(new Object[] { attRefStr, Condition.Aux.toOpStringForHTML(op), getCellValue(((ColumnReference) condValue).getColumnNo()) }));
			}
			else if (condValue instanceof MathExpressionValue) {
				setText(FORMAT_COND_MATH.format(new Object[] {
						attRefStr,
						Condition.Aux.toOpStringForHTML(op),
						getCellValue(((MathExpressionValue) condValue).getColumnReference().getColumnNo()),
						((MathExpressionValue) condValue).getOperator(),
						attRefDisplayString(((MathExpressionValue) condValue).getAttributeReference()) }));
			}
			else {
				setText(FORMAT_COND_NORMAL.format(new Object[] { attRefStr, Condition.Aux.toOpStringForHTML(op), (condValue == null ? "" : condValue.toString()) }));
			}
		}
		else if (value instanceof TestTreeNode) {
			setIcon(null);
			setIcon((selected ? selectedTestIcon : testIcon));
			setText(((TestTreeNode) value).dispString());
		}
		else if (value instanceof ActionTreeNode) {
			setIcon(null);
			setIcon((selected ? selectedActionIcon : actionIcon));
			setText(((ActionTreeNode) value).dispString(selected));
		}
		else if (value instanceof ActionParamTreeNode) {
			setIcon(null);
			setIcon((selected ? selectedParamIcon : paramIcon));
			FunctionParameter param = ((ActionParamTreeNode) value).getFunctionParameter();
			if (param instanceof ColumnReference) {
				setText(FORMAT_PARAM_HIGHLIGHTED.format(new Object[] { param.toDisplayName(), getCellValue(((ColumnReference) param).getColumnNo()) }));
			}
			else {
				setText(FORMAT_PARAM_UNHIGHLIGHTED.format(new Object[] {
						param.toDisplayName(),
						param.displayString(DomainModel.getInstance()) == null ? "" : param.displayString(DomainModel.getInstance()) }));
			}
		}
		else if (value instanceof AbstractRuleTreeNode) {
			setText(((AbstractRuleTreeNode) value).dispString(selected));
		}
		return this;
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

	public GridTemplate getTemplate() {
		return template;
	}

	public void setTemplate(GridTemplate template) {
		this.template = template;
	}


}
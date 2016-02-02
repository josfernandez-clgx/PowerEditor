package com.mindbox.pe.client.applet.template.rule;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.common.AttributeReferenceSelectField;
import com.mindbox.pe.client.common.EnumValueCellRenderer;
import com.mindbox.pe.client.common.FloatTextField;
import com.mindbox.pe.client.common.NumberTextField;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.TemplateColumnSelectField;
import com.mindbox.pe.client.common.dialog.MDateDateField;
import com.mindbox.pe.client.common.formatter.FormatterFactory;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.common.validate.DataTypeCompatibilityValidator;
import com.mindbox.pe.model.AbstractTemplateColumn;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.ColumnDataSpecDigest.EnumSourceType;
import com.mindbox.pe.model.rule.ColumnReference;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.MathExpressionValue;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.model.rule.Value;
import com.mindbox.pe.model.table.BooleanDataHelper;

/**
 * Template rule condition edit dialog.
 * @author Geneho Kim
 * @since PowerEditor 2.3.0
 */
public class ConditionEditDialog extends JPanel implements ChangeListener {

	private static final String VALUE_KEY_BOOLEAN = "BOOLEAN";
	private static final String VALUE_KEY_BOOLEAN_ALLOWNULL = "BOOLEAN-N";
	private static final String VALUE_KEY_CURRENCY = "CURRENCY";
	private static final String VALUE_KEY_CURRENCY_RANGE = "CURRENCY-R";
	private static final String VALUE_KEY_DATE = "DATE";
	private static final String VALUE_KEY_DATE_RANGE = "DATE-R";
	private static final String VALUE_KEY_ENUM = "ENUM";
	private static final String VALUE_KEY_ENUM_MULTI = "ENUM-M";
	private static final String VALUE_KEY_FLOAT = "FLOAT";
	private static final String VALUE_KEY_FLOAT_RANGE = "FLOAT-R";
	private static final String VALUE_KEY_INTEGER = "INTEGER";
	private static final String VALUE_KEY_INTEGER_RANGE = "INTEGER-R";
	private static final String VALUE_KEY_STRING = "STRING";
	private static final String VALUE_KEY_SYMBOL = "SYMBOL";

	/**
	 * Edits the condition whose original value was a reference to the specified column no.
	 * @param template the template
	 * @param condition the condition
	 * @param columnNo the column number
	 * @return the condition if modified; <code>null</code>, if no changes are made (canceled)
	 */
	public static Condition editCondition(GridTemplate template, Condition condition, int columnNo) {
		JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), true);
		dialog.setTitle("Edit Condition");
		boolean allowNull = false;
		if (columnNo != -1) {
			if (template.getColumn(columnNo) != null && template.getColumn(columnNo).getColumnDataSpecDigest().isBlankAllowed()) {
				allowNull = true;
			}
		}
		ConditionEditDialog panel = new ConditionEditDialog(template, dialog, condition, columnNo, allowNull);
		panel.disableValueTypeSelection();
		UIFactory.addToDialog(dialog, panel);


		dialog.setVisible(true);

		return panel.condition;
	}

	public static Condition editCondition(GridTemplate template, Condition condition, boolean allowValueChangeOnly) {
		JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), true);
		dialog.setTitle("Edit Condition");
		ConditionEditDialog panel = new ConditionEditDialog(template, dialog, condition, allowValueChangeOnly, false, -1);
		if (allowValueChangeOnly) {
			panel.disableValueTypeSelection();
		}
		UIFactory.addToDialog(dialog, panel);

		dialog.setVisible(true);

		return panel.condition;
	}

	public static Condition editCondition(GridTemplate template, Condition condition) {
		return editCondition(template, condition, false);
	}

	public static Condition createCondition(GridTemplate template) {
		JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), true);
		dialog.setTitle("New Condition");
		ConditionEditDialog panel = new ConditionEditDialog(template, dialog, null, false, false, -1);
		UIFactory.addToDialog(dialog, panel);

		dialog.setVisible(true);

		return panel.condition;
	}

	private static final String[] OPERATORS_ALL = new String[] {
			Condition.OPSTR_EQUAL,
			Condition.OPSTR_NOT_EQUAL,
			Condition.OPSTR_LESS,
			Condition.OPSTR_LESS_EQUAL,
			Condition.OPSTR_GREATER,
			Condition.OPSTR_GREATER_EQUAL,
			Condition.OPSTR_BETWEEN,
			Condition.OPSTR_NOT_BETWEEN,
			Condition.OPSTR_IN,
			Condition.OPSTR_NOT_IN,
			Condition.OPSTR_IS_EMPTY,
			Condition.OPSTR_IS_NOT_EMPTY,
			Condition.OPSTR_ENTITY_MATCH_FUNC,
			Condition.OPSTR_NOT_ENTITY_MATCH_FUNC,
			Condition.OPSTR_ANY_VALUE };

	private static final String[] OPERATORS_EQUALITY = new String[] {
			OPERATORS_ALL[0],
			OPERATORS_ALL[1],
			OPERATORS_ALL[10],
			OPERATORS_ALL[11] };

	private static final String[] OPERATORS_ALL_EXCEPT_NUMERIC_OPS_AND_ENTITY_TEST = new String[] {
			OPERATORS_ALL[0],
			OPERATORS_ALL[1],
			OPERATORS_ALL[8],
			OPERATORS_ALL[9],
			OPERATORS_ALL[10],
			OPERATORS_ALL[11],
			OPERATORS_ALL[14] };

	private static final String[] OPERATORS_COMPARE_BETWEEN = new String[] {
			OPERATORS_ALL[0],
			OPERATORS_ALL[1],
			OPERATORS_ALL[2],
			OPERATORS_ALL[3],
			OPERATORS_ALL[4],
			OPERATORS_ALL[5],
			OPERATORS_ALL[6],
			OPERATORS_ALL[7],
			OPERATORS_ALL[10],
			OPERATORS_ALL[11],
			OPERATORS_ALL[14] };

	private static final String[] OPERATORS_ALL_EXCEPT_ENTITY_TEST = new String[] {
			OPERATORS_ALL[0],
			OPERATORS_ALL[1],
			OPERATORS_ALL[2],
			OPERATORS_ALL[3],
			OPERATORS_ALL[4],
			OPERATORS_ALL[5],
			OPERATORS_ALL[6],
			OPERATORS_ALL[7],
			OPERATORS_ALL[8],
			OPERATORS_ALL[9],
			OPERATORS_ALL[10],
			OPERATORS_ALL[11],
			OPERATORS_ALL[14] };

	private static final String[] OPERATORS_MATH_ONLY = new String[] { "+", "-", "*", "/" };

	private final class EnumEditL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			enumButton.setEnabled(false);
			try {
				String prevValue = valueListField.getText();
				String newValue = null;
				if (enumValueList != null) {
					if (prevValue == null || prevValue.length() == 0) {
						newValue = MultiEnumEditDialog.newEnums(dialog, enumValueList);
					}
					else {
						newValue = MultiEnumEditDialog.editEnums(dialog, enumValueList, prevValue);
					}
					if (newValue != null) {
						valueListField.setText(newValue);
					}
				}
				// TT 2133
				else {
					ClientUtil.getInstance().showWarning("msg.warning.empty.enumerated.list");
					;
				}
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
			finally {
				enumButton.setEnabled(true);
			}
		}
	}

	private final class DateRangeL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			dateRangeButton.setEnabled(false);
			try {
				String prevValue = dateRangeField.getText();
				String newValue = null;
				if (prevValue == null || prevValue.length() == 0) {
					newValue = DateRangeEditDialog.newRange(dialog);
				}
				else {
					newValue = DateRangeEditDialog.editRange(dialog, prevValue);
				}
				if (newValue != null) {
					dateRangeField.setText(newValue);
				}
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
			finally {
				dateRangeButton.setEnabled(true);
			}
		}
	}

	private final class FloatRangeL implements ActionListener {

		private final boolean forCurrency;

		FloatRangeL(boolean forCurrency) {
			this.forCurrency = forCurrency;
		}

		public void actionPerformed(ActionEvent e) {
			floatRangeButton.setEnabled(false);
			try {
				String prevValue = floatRangeField.getText();
				String newValue = null;
				if (prevValue == null || prevValue.length() == 0) {
					newValue = FloatRangeEditDialog.newRange(dialog, forCurrency);
				}
				else {
					newValue = FloatRangeEditDialog.editRange(dialog, forCurrency, prevValue);
				}
				if (newValue != null) {
					if (forCurrency)
						currencyRangeField.setText(newValue);
					else
						floatRangeField.setText(newValue);
				}
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
			finally {
				floatRangeButton.setEnabled(true);
			}
		}
	}

	private final class IntegerRangeL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			integerRangeButton.setEnabled(false);
			try {
				String prevValue = integerRangeField.getText();
				String newValue = null;
				if (prevValue == null || prevValue.length() == 0) {
					newValue = IntegerRangeEditDialog.newRange(dialog);
				}
				else {
					newValue = IntegerRangeEditDialog.editRange(dialog, prevValue);
				}
				if (newValue != null) {
					integerRangeField.setText(newValue);
				}
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
			finally {
				integerRangeButton.setEnabled(true);
			}
		}
	}

	private final class OpComboL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			refreshValuePanel();
			refreshValueEnabling();
			checkReferences();
		}
	}


	private class CreateL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (updateCondition()) {
				dialog.dispose();
			}
		}
	}

	private class CancelL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			condition = null;
			dialog.dispose();
		}
	}

	private final class RadioButtonL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			refreshValueCard();
		}
	}

	private Condition condition = null;
	private final JTextField stringField, commentField;
	private final AttributeReferenceSelectField refField;
	private final JComboBox opCombo;
	private DeployType deployType = null;

	private final JFormattedTextField objectNameField;
	private final JTextField valueListField;
	private final JTextField dateRangeField;
	private final JTextField floatRangeField;
	private final JTextField currencyRangeField;
	private final JTextField integerRangeField;
	private final NumberTextField integerField;
	private final FloatTextField floatField;
	private final FloatTextField currencyField;
	private final MDateDateField dateField;
	private final JFormattedTextField symbolField;
	private final JComboBox enumCombo;
	private final JComboBox booleanCombo, booleanNullCombo;
	private final JButton enumButton;
	private final JButton dateRangeButton;
	private final JButton floatRangeButton;
	private final JButton currencyRangeButton;
	private final JButton integerRangeButton;

	private final JRadioButton valueRadioButton;
	private final JRadioButton columnRefRadioButton;
	private final JRadioButton attrRefRadioButton;
	private final JRadioButton calcRadioButton;
	private final CardLayout valueCard;
	private final JPanel valueDetailPanel;
	private final AttributeReferenceSelectField attrRefField;
	private final TemplateColumnSelectField columnRefField;

	private final AttributeReferenceSelectField calcAttrRefField;
	private final TemplateColumnSelectField calcColumnRefField;
	private final JComboBox calcOpCombo;
	private final boolean allowValueChangeOnly;

	private final CardLayout card;
	private final JPanel valuePanel;
	private JDialog dialog;
	private GridTemplate template;
	private int columnNo = -1;
	private boolean allowNull;
	private List<EnumValue> enumValueList;

	private ConditionEditDialog(GridTemplate template, JDialog dialog, Condition condition, int columnNo, boolean allowNull) {
		this(template, dialog, condition, true, allowNull, columnNo);
		if (columnNo != -1) {
			resetEnumIfNecessary(columnNo);
		}
	}

	private ConditionEditDialog(GridTemplate template, JDialog dialog, Condition condition, boolean allowValueChangeOnly,
			boolean allowNull, int columnNo) {
		this.dialog = dialog;
		this.condition = condition;
		this.allowValueChangeOnly = allowValueChangeOnly;
		this.template = template;
		this.allowNull = allowNull;
		this.columnNo = columnNo;

		attrRefField = new AttributeReferenceSelectField();
		columnRefField = new TemplateColumnSelectField(template, true);
		objectNameField = new JFormattedTextField(FormatterFactory.getSymbolFormatter());

		calcAttrRefField = new AttributeReferenceSelectField();
		calcAttrRefField.setGenericDataTypes(DataTypeCompatibilityValidator.DATA_TYPES_FOR_CALC);
		calcColumnRefField = new TemplateColumnSelectField(template, true);
		calcColumnRefField.setGenericDataTypes(DataTypeCompatibilityValidator.DATA_TYPES_FOR_CALC);
		calcOpCombo = new JComboBox(OPERATORS_MATH_ONLY);
		calcOpCombo.setSelectedIndex(0);

		RadioButtonL rbL = new RadioButtonL();
		valueRadioButton = UIFactory.createRaiodButton("label.specify.value");
		valueRadioButton.addActionListener(rbL);
		columnRefRadioButton = UIFactory.createRaiodButton("label.specify.ref.col");
		columnRefRadioButton.addActionListener(rbL);
		attrRefRadioButton = UIFactory.createRaiodButton("label.specify.ref.attr");
		attrRefRadioButton.addActionListener(rbL);
		calcRadioButton = UIFactory.createRaiodButton("label.calc.column");
		calcRadioButton.addActionListener(rbL);

		valueCard = new CardLayout();
		valueDetailPanel = new JPanel(valueCard);

		dateRangeField = new JTextField(10);
		dateRangeField.setEditable(false);
		floatRangeField = new JTextField(10);
		floatRangeField.setEditable(false);
		currencyRangeField = new JTextField(10);
		currencyRangeField.setEditable(false);
		integerRangeField = new JTextField(10);
		integerRangeField.setEditable(false);

		valueListField = new JTextField(10);
		valueListField.setEditable(false);
		integerField = new NumberTextField(10);
		floatField = new FloatTextField(10, false);
		currencyField = new FloatTextField(10, true);
		dateField = new MDateDateField(true, true);
		symbolField = new JFormattedTextField(FormatterFactory.getSymbolFormatter());
		booleanCombo = new JComboBox();
		booleanCombo.addItem("TRUE");
		booleanCombo.addItem("FALSE");

		booleanNullCombo = new JComboBox();
		booleanNullCombo.addItem(BooleanDataHelper.TRUE_VALUE);
		booleanNullCombo.addItem(BooleanDataHelper.FALSE_VALUE);
		booleanNullCombo.addItem(BooleanDataHelper.ANY_VALUE);

		enumButton = UIFactory.createButton("...", null, new EnumEditL(), null);
		refField = new AttributeReferenceSelectField();
		stringField = new JTextField(10);
		opCombo = new JComboBox();
		commentField = new JTextField(10);
		enumCombo = new JComboBox();
		enumCombo.setRenderer(new EnumValueCellRenderer());

		dateRangeButton = UIFactory.createButton("...", null, new DateRangeL(), null);
		integerRangeButton = UIFactory.createButton("...", null, new IntegerRangeL(), null);
		floatRangeButton = UIFactory.createButton("...", null, new FloatRangeL(false), null);
		currencyRangeButton = UIFactory.createButton("...", null, new FloatRangeL(true), null);

		JPanel enumPanel = UIFactory.createJPanel(new BorderLayout(0, 0));
		enumPanel.add(valueListField, BorderLayout.CENTER);
		enumPanel.add(enumButton, BorderLayout.EAST);

		JPanel dateRangePanel = UIFactory.createJPanel(new BorderLayout(0, 0));
		dateRangePanel.add(dateRangeField, BorderLayout.CENTER);
		dateRangePanel.add(dateRangeButton, BorderLayout.EAST);

		JPanel floatRangePanel = UIFactory.createJPanel(new BorderLayout(0, 0));
		floatRangePanel.add(floatRangeField, BorderLayout.CENTER);
		floatRangePanel.add(floatRangeButton, BorderLayout.EAST);

		JPanel currencyRangePanel = UIFactory.createJPanel(new BorderLayout(0, 0));
		currencyRangePanel.add(currencyRangeField, BorderLayout.CENTER);
		currencyRangePanel.add(currencyRangeButton, BorderLayout.EAST);

		JPanel integerRangePanel = UIFactory.createJPanel(new BorderLayout(0, 0));
		integerRangePanel.add(integerRangeField, BorderLayout.CENTER);
		integerRangePanel.add(integerRangeButton, BorderLayout.EAST);

		JPanel enumComboPanel = UIFactory.createBorderLayoutPanel(0, 0);
		enumComboPanel.setAlignmentY(0.0f);
		enumComboPanel.add(enumCombo, BorderLayout.NORTH);

		card = new CardLayout(0, 0);
		valuePanel = UIFactory.createJPanel(card);
		valuePanel.add(stringField, VALUE_KEY_STRING);
		valuePanel.add(enumComboPanel, VALUE_KEY_ENUM);
		valuePanel.add(enumPanel, VALUE_KEY_ENUM_MULTI);
		valuePanel.add(dateField, VALUE_KEY_DATE);
		valuePanel.add(dateRangePanel, VALUE_KEY_DATE_RANGE);
		valuePanel.add(floatField, VALUE_KEY_FLOAT);
		valuePanel.add(floatRangePanel, VALUE_KEY_FLOAT_RANGE);
		valuePanel.add(currencyField, VALUE_KEY_CURRENCY);
		valuePanel.add(currencyRangePanel, VALUE_KEY_CURRENCY_RANGE);
		valuePanel.add(integerField, VALUE_KEY_INTEGER);
		valuePanel.add(integerRangePanel, VALUE_KEY_INTEGER_RANGE);
		valuePanel.add(symbolField, VALUE_KEY_SYMBOL);
		valuePanel.add(booleanCombo, VALUE_KEY_BOOLEAN);
		valuePanel.add(booleanNullCombo, VALUE_KEY_BOOLEAN_ALLOWNULL);

		initPanel();

		valueRadioButton.setSelected(true);

		populateFields();

		if (!this.allowValueChangeOnly) {
			refField.addChangeListener(this);
			opCombo.addActionListener(new OpComboL());
		}
		setSize(500, 300);
	}

	private void disableValueTypeSelection() {
		valueRadioButton.setEnabled(false);
		valueRadioButton.setVisible(false);
		columnRefRadioButton.setEnabled(false);
		attrRefRadioButton.setEnabled(false);
		calcRadioButton.setEnabled(false);
		columnRefRadioButton.setVisible(false);
		attrRefRadioButton.setVisible(false);
		calcRadioButton.setVisible(false);
		opCombo.setEnabled(false);
		attrRefField.setEnabled(false);
		refField.setEnabled(false);
	}

	private void refreshValueCard() {
		if (valueRadioButton.isSelected()) {
			valueCard.show(valueDetailPanel, "VALUE");
		}
		else if (attrRefRadioButton.isSelected()) {
			valueCard.show(valueDetailPanel, "ATTRIBUTE");
		}
		else if (columnRefRadioButton.isSelected()) {
			valueCard.show(valueDetailPanel, "COLUMN");
		}
		else if (calcRadioButton.isSelected()) {
			valueCard.show(valueDetailPanel, "CALCULATION");
		}
	}

	private void clearValueFields() {
		stringField.setText("");
		dateField.setValue(null);
		integerField.setText("");
		floatField.setText("");
		symbolField.setText("");
		valueListField.setText("");
		integerRangeField.setText("");
		floatRangeField.setText("");
		dateRangeField.setText("");
		enumCombo.removeAllItems();
	}

	private void setAttribute(String refStr) {
		if (refStr != null) {
			// based on reference, adjust UI
			String[] names = refStr.split("\\.");
			refField.setValue(names[0], names[1]);
			setAttribute(names[0], names[1]);
		}
	}

	private void setAttribute(Reference ref) {
		if (ref != null) {
			setAttribute(ref.getClassName(), ref.getAttributeName());
		}
	}

	private void resetEnumIfNecessary(int columnNo) {
		if (template != null && template.getColumn(columnNo) != null && 
				template.getColumn(columnNo).getColumnDataSpecDigest().getEnumSourceType() == EnumSourceType.DOMAIN_ATTRIBUTE && 
				template.getColumn(columnNo).getMappedAttribute() != null) {
			if (condition.getValue() != null) {
				valueListField.setText(condition.getValue().toString());
			}
		}
	}

	private void setAttribute(String cn, String an) {
		if (cn == null || an == null) return;
		DomainClass dc = DomainModel.getInstance().getDomainClass(cn);
		if (dc != null) {
			DomainAttribute da = dc.getDomainAttribute(an);
			if (da != null) {
				this.deployType = da.getDeployType();
				if (da.hasEnumValue()) {
					this.enumValueList = Arrays.asList(da.getEnumValues());
				}
				else {
					this.enumValueList = null;
				}
			}
			else {
				this.deployType = null;
				this.enumValueList = null;
			}
		}
		else {
			deployType = null;
			enumValueList = null;
		}

		refField.setValue(cn, an);
		if (enumValueList != null) {
			enumCombo.removeAllItems();
			for (EnumValue ev : enumValueList) {
				if (ev.isActive()) {
					enumCombo.addItem(ev);
				}
			}
		}
	}

	private void refreshAvailableOps() {
		if (deployType == null) {
			setAvailableOps(OPERATORS_ALL);
		}
		else if (deployType == DeployType.BOOLEAN) {
			setAvailableOps(OPERATORS_EQUALITY);
		}
		else if (deployType == DeployType.CODE) {
			setAvailableOps(OPERATORS_ALL_EXCEPT_NUMERIC_OPS_AND_ENTITY_TEST);
		}
		else if (deployType == DeployType.STRING) {
			setAvailableOps(OPERATORS_ALL_EXCEPT_NUMERIC_OPS_AND_ENTITY_TEST);
		}
		else if (deployType == DeployType.CURRENCY) {
			setAvailableOps(OPERATORS_ALL_EXCEPT_ENTITY_TEST);
		}
		else if (deployType == DeployType.FLOAT || deployType == DeployType.PERCENT) {
			setAvailableOps(OPERATORS_ALL_EXCEPT_ENTITY_TEST);
		}
		else if (deployType == DeployType.DATE) {
			setAvailableOps(OPERATORS_COMPARE_BETWEEN);
		}
		else if (deployType == DeployType.INTEGER || deployType == DeployType.SYMBOL) {
			setAvailableOps(OPERATORS_ALL);
		}
		else if (deployType == DeployType.RELATIONSHIP) {
			setAvailableOps((enumValueList == null ? OPERATORS_EQUALITY : OPERATORS_ALL_EXCEPT_NUMERIC_OPS_AND_ENTITY_TEST));
		}
	}

	// BEGIN type checking methods

	private boolean isForEnumSingleSelect() {
		return enumValueList != null
				&& (columnNo < 1 || template.getColumn(columnNo).getColumnDataSpecDigest().getType().equals(
						ColumnDataSpecDigest.TYPE_ENUM_LIST));
	}

	private boolean isForEnumMultiSelect() {
		// TT 1912: check if column allows multiple
		if (columnNo > 0 && template != null && template.getColumn(columnNo) != null) {
			ColumnDataSpecDigest cdsd = template.getColumn(columnNo).getColumnDataSpecDigest();
			return cdsd.getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST) && cdsd.isMultiSelectAllowed();
		}
		else {
			return (opCombo.getSelectedItem() != null && (opCombo.getSelectedItem().equals(Condition.OPSTR_IN) || opCombo.getSelectedItem().equals(
					Condition.OPSTR_NOT_IN)));
		}
	}

	private boolean isForSymbol() {
		return deployType == DeployType.CODE
				|| deployType == DeployType.SYMBOL
				&& (columnNo < 1 || template.getColumn(columnNo).getColumnDataSpecDigest().getType().equals(
						ColumnDataSpecDigest.TYPE_SYMBOL));
	}

	private boolean isOpBetween() {
		return opCombo.getSelectedItem() != null
				&& (opCombo.getSelectedItem().equals(Condition.OPSTR_BETWEEN) || opCombo.getSelectedItem().equals(
						Condition.OPSTR_NOT_BETWEEN));
	}

	private boolean isOpEntityMatchFunc() {
		return opCombo.getSelectedItem() != null
				&& (opCombo.getSelectedItem().equals(Condition.OPSTR_ENTITY_MATCH_FUNC) || opCombo.getSelectedItem().equals(
						Condition.OPSTR_NOT_ENTITY_MATCH_FUNC));
	}

	private boolean isOpUnary() {
		return opCombo.getSelectedItem() != null
				&& (opCombo.getSelectedItem().equals(Condition.OPSTR_IS_EMPTY)
						|| opCombo.getSelectedItem().equals(Condition.OPSTR_IS_NOT_EMPTY) || (opCombo.getSelectedItem().equals(Condition.OPSTR_ANY_VALUE)));
	}

	private boolean isOpComparison() {
		return opCombo.getSelectedItem() != null
				&& (opCombo.getSelectedItem().equals(Condition.OPSTR_GREATER)
						|| opCombo.getSelectedItem().equals(Condition.OPSTR_GREATER_EQUAL)
						|| opCombo.getSelectedItem().equals(Condition.OPSTR_LESS) || opCombo.getSelectedItem().equals(
						Condition.OPSTR_LESS_EQUAL));
	}

	private boolean isOpEquality() {
		return opCombo.getSelectedItem() != null
				&& (opCombo.getSelectedItem().equals(Condition.OPSTR_EQUAL) || opCombo.getSelectedItem().equals(Condition.OPSTR_NOT_EQUAL));
	}

	private void setValueEnabled(boolean enabled) {
		valueRadioButton.setEnabled(enabled);
		stringField.setEnabled(enabled);
		floatField.setEnabled(enabled);
		dateField.setEnabled(enabled);
		integerField.setEnabled(enabled);
		floatField.setEnabled(enabled);
		symbolField.setEnabled(enabled);
		valueListField.setEnabled(enabled);
		integerRangeField.setEnabled(enabled);
		floatRangeField.setEnabled(enabled);
		dateRangeField.setEnabled(enabled);
		enumCombo.setEnabled(enabled);
		booleanCombo.setEnabled(enabled);
		enumButton.setEnabled(enabled);
		dateRangeButton.setEnabled(enabled);
		floatRangeButton.setEnabled(enabled);
		currencyRangeButton.setEnabled(enabled);
		integerRangeButton.setEnabled(enabled);
	}

	private void setAttributeEnabled(boolean enabled) {
		attrRefRadioButton.setEnabled(enabled);
		attrRefField.setEnabled(enabled);
	}

	private void setColumnEnabled(boolean enabled) {
		columnRefRadioButton.setEnabled(enabled);
		columnRefField.setEnabled(enabled);
	}

	private void setCalculationEnabled(boolean enabled) {
		this.calcRadioButton.setEnabled(enabled);
		this.calcOpCombo.setEnabled(enabled);
		this.calcAttrRefField.setEnabled(enabled);
		this.calcColumnRefField.setEnabled(enabled);
	}

	private void refreshValueEnabling() {
		if (deployType == null) {
			setValueEnabled(false);
			setAttributeEnabled(false);
			setColumnEnabled(false);
			setCalculationEnabled(false);
		}
		else if (isOpUnary()) {
			setValueEnabled(false);
			setAttributeEnabled(false);
			setColumnEnabled(false);
			setCalculationEnabled(false);
			valueRadioButton.doClick();
		}
		else if (isForEnumMultiSelect()) {
			setValueEnabled(true);
			setAttributeEnabled(false);
			setColumnEnabled(true);
			setCalculationEnabled(false);
			if (attrRefRadioButton.isSelected() || calcRadioButton.isSelected()) this.columnRefRadioButton.doClick();
		}
		else if (isForEnumSingleSelect()) {
			setValueEnabled(true);
			setAttributeEnabled(true);
			setColumnEnabled(true);
			setCalculationEnabled(false);
		}
		else if (isOpBetween()) {
			setValueEnabled(true);
			setAttributeEnabled(false);
			setColumnEnabled(true);
			setCalculationEnabled(false);
			if (attrRefRadioButton.isSelected() || calcRadioButton.isSelected()) this.columnRefRadioButton.doClick();
		}
		else if (isOpComparison() || isOpEquality()) {
			setValueEnabled(true);
			setAttributeEnabled(true);
			setColumnEnabled(true);
			boolean calcAllowed = UtilBase.isMember(
					DataTypeCompatibilityValidator.getGenericDataType(deployType),
					DataTypeCompatibilityValidator.DATA_TYPES_FOR_CALC);
			setCalculationEnabled(calcAllowed);
			if (!calcAllowed && calcRadioButton.isSelected()) this.columnRefRadioButton.doClick();
		}
		else if (isOpEntityMatchFunc()) {
			setValueEnabled(false);
			setAttributeEnabled(false);
			setColumnEnabled(true);
			setCalculationEnabled(false);
			if (attrRefRadioButton.isSelected() || calcRadioButton.isSelected()) this.columnRefRadioButton.doClick();
		}
		else {
			setValueEnabled(false);
			setAttributeEnabled(false);
			setColumnEnabled(false);
			setCalculationEnabled(false);
		}
	}

	private void checkReferences() {
		checkColumnReference();
		checkAttributeReference();
		checkValueString();
		filterColumns();
		filterAttributes();
	}

	private void checkColumnReference() {
		if (deployType != null) {
			ColumnReference colRef = this.columnRefField.getValue();
			if (colRef != null) {
				AbstractTemplateColumn col = template.getColumn(colRef.getColumnNo());
				if (col == null || opCombo.getSelectedItem() == null)
					columnRefField.setValue(null);
				else {
					Reference attRef = RuleElementFactory.getInstance().createReference(refField.getValue());
					int op = Condition.Aux.toOpInt((String) opCombo.getSelectedItem());
					Value colRefVal = RuleElementFactory.getInstance().createValue(columnRefField.getValue());
					String message = DataTypeCompatibilityValidator.isValid(
							attRef,
							op,
							colRefVal,
							template,
							DomainModel.getInstance(),
							true);
					if (message != null) {
						columnRefField.setValue(null);
						JOptionPane.showMessageDialog(this, message);
					}
				}
			}
		}
	}

	private void checkValueString() {
		if (deployType != null && valueRadioButton.isSelected() && !allowValueChangeOnly) {
			String valueString = getValueString();
			if (valueString != null) {
				if (opCombo.getSelectedItem() == null)
					setValueString("");
				else {
					Reference attRef = RuleElementFactory.getInstance().createReference(refField.getValue());
					int op = Condition.Aux.toOpInt((String) opCombo.getSelectedItem());
					Value colRefVal = RuleElementFactory.getInstance().createValue(valueString);
					String message = DataTypeCompatibilityValidator.isValid(
							attRef,
							op,
							colRefVal,
							template,
							DomainModel.getInstance(),
							true);
					if (message != null) {
						setValueString("");
						JOptionPane.showMessageDialog(this, message);
					}
				}
			}
		}
	}

	private void filterColumns() {
		if (deployType == null || opCombo.getSelectedItem() == null) {
			this.columnRefField.setGenericDataTypes(null);
		}
		else {
			int opType = Condition.Aux.toOpInt((String) opCombo.getSelectedItem());
			int[] legalTypes = DataTypeCompatibilityValidator.getLegalGenericDataTypes(deployType, opType);
			this.columnRefField.setGenericDataTypes(legalTypes);
			this.columnRefField.setForMembershipOperator(isForEnumMultiSelect());
			this.columnRefField.setForEntityTestFunctionOperator(isOpEntityMatchFunc());
		}
	}

	private void filterAttributes() {
		if (deployType == null || opCombo.getSelectedItem() == null) {
			this.attrRefField.filterAttributes(null);
		}
		else {
			int opType = Condition.Aux.toOpInt((String) opCombo.getSelectedItem());
			int[] legalTypes = DataTypeCompatibilityValidator.getLegalGenericDataTypes(deployType, opType);
			this.attrRefField.filterAttributes(legalTypes);
		}
	}

	private void checkAttributeReference() {
		if (deployType != null) {
			String rhsAttRefStr = attrRefField.getValue();
			if (opCombo.getSelectedItem() == null)
				attrRefField.setValue(null, null);
			else if (rhsAttRefStr != null) {
				Reference attRef = RuleElementFactory.getInstance().createReference(refField.getValue());
				int op = Condition.Aux.toOpInt((String) opCombo.getSelectedItem());
				RuleElementFactory.getInstance().createReference(rhsAttRefStr);
				Value rhsVal = RuleElementFactory.getInstance().createValue(RuleElementFactory.getInstance().createReference(rhsAttRefStr));
				String message = DataTypeCompatibilityValidator.isValid(attRef, op, rhsVal, template, DomainModel.getInstance(), true);
				if (message != null) {
					attrRefField.setValue(null, null);
					JOptionPane.showMessageDialog(this, message);
				}
			}
		}
	}

	private void refreshValuePanel() {
		if (isOpUnary()) {
			stringField.setText("Not Applicable");
			card.show(valuePanel, VALUE_KEY_STRING);
		}
		else if (isForEnumMultiSelect()) {
			// show enum selector
			card.show(valuePanel, VALUE_KEY_ENUM_MULTI);
		}
		else if (isForEnumSingleSelect()) {
			card.show(valuePanel, VALUE_KEY_ENUM);
		}
		else if (isForSymbol()) {
			card.show(valuePanel, VALUE_KEY_SYMBOL);
		}
		else {
			boolean doRange = isOpBetween();
			if (deployType == null) {
				card.show(valuePanel, VALUE_KEY_STRING);
			}
			else if (deployType == DeployType.BOOLEAN) {
				card.show(valuePanel, (allowNull ? VALUE_KEY_BOOLEAN_ALLOWNULL : VALUE_KEY_BOOLEAN));
			}
			else if (deployType == DeployType.STRING) {
				card.show(valuePanel, VALUE_KEY_STRING);
			}
			else if (deployType == DeployType.CURRENCY) {
				card.show(valuePanel, (doRange ? VALUE_KEY_CURRENCY_RANGE : VALUE_KEY_CURRENCY));
			}
			else if (deployType == DeployType.FLOAT || deployType == DeployType.PERCENT) {
				card.show(valuePanel, (doRange ? VALUE_KEY_FLOAT_RANGE : VALUE_KEY_FLOAT));
			}
			else if (deployType == DeployType.DATE) {
				card.show(valuePanel, (doRange ? VALUE_KEY_DATE_RANGE : VALUE_KEY_DATE));
			}
			else if (deployType == DeployType.INTEGER) {
				card.show(valuePanel, (doRange ? VALUE_KEY_INTEGER_RANGE : VALUE_KEY_INTEGER));
			}
			else if (deployType == DeployType.RELATIONSHIP) {
				card.show(valuePanel, VALUE_KEY_STRING);
			}
		}
	}

	private String getValueString() {
		if (!valueRadioButton.isSelected()) return null;
		if (isOpUnary()) return stringField.getText();
		if (isForEnumMultiSelect()) {
			// show enum selector
			return valueListField.getText();
		}
		else if (isForEnumSingleSelect()) {
			EnumValue enumValue = (EnumValue) enumCombo.getSelectedItem();
			return (enumValue == null ? "" : enumValue.getDisplayLabel());
		}
		else if (isForSymbol()) {
			return symbolField.getText();
		}

		boolean doRange = isOpBetween();
		if (deployType == null) {
			return stringField.getText();
		}
		else if (deployType == DeployType.BOOLEAN) {
			return (allowNull ? booleanNullCombo.getSelectedItem().toString() : booleanCombo.getSelectedItem().toString());
		}
		else if (deployType == DeployType.STRING) {
			return stringField.getText();
		}
		else if (deployType == DeployType.CURRENCY) {
			if (doRange) {
				return currencyRangeField.getText();
			}
			else {
				return currencyField.getText();
			}
		}
		else if (deployType == DeployType.FLOAT || deployType == DeployType.PERCENT) {
			if (doRange) {
				return floatRangeField.getText();
			}
			else {
				return floatField.getText();
			}
		}
		else if (deployType == DeployType.DATE) {
			if (doRange) {
				return dateRangeField.getText();
			}
			else {
				return (dateField.getDate() == null ? "" : UIConfiguration.FORMAT_DATE.format(dateField.getDate()));
			}
		}
		else if (deployType == DeployType.INTEGER) {
			if (doRange) {
				return integerRangeField.getText();
			}
			else {
				return integerField.getText();
			}
		}
		return stringField.getText();
	}

	private void setValueString(String value) {
		if (isOpUnary()) {
			stringField.setText(value);
		}
		else if (isForEnumMultiSelect()) {
			// show enum selector
			valueListField.setText(value);
		}
		else if (isForEnumSingleSelect()) {
			int index = -1;
			for (int j = 0; j < enumCombo.getItemCount(); j++) {
				EnumValue ev = (EnumValue) enumCombo.getItemAt(j);
				if (ev.getDisplayLabel().equals(value) || ev.getDeployID().toString().equals(value)) {
					index = j;
					break;
				}
			}
			enumCombo.setSelectedIndex(index);
		}
		else if (isForSymbol()) {
			symbolField.setText(value);
		}
		else {

			boolean doRange = isOpBetween();

			if (deployType == null) {
				stringField.setText(value);
			}
			else if (deployType == DeployType.BOOLEAN) {
				if (allowNull) {
					booleanNullCombo.setSelectedItem(BooleanDataHelper.toStringValue(value));
				}
				else {
					booleanCombo.setSelectedIndex((Boolean.valueOf(value).booleanValue() ? 0 : 1));
				}
			}
			else if (deployType == DeployType.STRING) {
				stringField.setText(value);
			}
			else if (deployType == DeployType.CURRENCY) {
				if (doRange) {
					currencyRangeField.setText(value);
				}
				else {
					try {
						float f = Float.valueOf(value).floatValue();
						currencyField.setValue(f);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			else if (deployType == DeployType.FLOAT || deployType == DeployType.PERCENT) {
				if (doRange) {
					floatRangeField.setText(value);
				}
				else {
					try {
						float f = Float.valueOf(value).floatValue();
						floatField.setValue(f);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			else if (deployType == DeployType.DATE) {
				if (doRange) {
					dateRangeField.setText(value);
				}
				else {
					try {
						dateField.setValue((UtilBase.isEmpty(value) ? null : UIConfiguration.FORMAT_DATE.parse(value)));
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			else if (deployType == DeployType.INTEGER) {
				if (doRange) {
					integerRangeField.setText(value);
				}
				else {
					try {
						int i = Integer.parseInt(value);
						integerField.setValue(i);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			else if (deployType == DeployType.RELATIONSHIP) {
				stringField.setText(value);
			}
		}
	}

	private void populateFields() {
		clearValueFields();
		if (condition != null) {
			try {
				DomainModel.initInstance();
			}
			catch (Exception e) {
				ClientUtil.getLogger().error("Failed to initialize domain model", e);
			}
			commentField.setText(condition.getComment());
			objectNameField.setText(condition.getObjectName());
			setAttribute(condition.getReference());
			refreshAvailableOps();

			opCombo.setSelectedItem(Condition.Aux.toOpString(condition.getOp()));
			// nullify enum if value is not applicable
			if (allowValueChangeOnly) {
				switch (condition.getOp()) {
				case Condition.OP_BETWEEN:
				case Condition.OP_GREATER:
				case Condition.OP_GREATER_EQUAL:
				case Condition.OP_LESS:
				case Condition.OP_LESS_EQUAL:
				case Condition.OP_NOT_BETWEEN:
					enumValueList = null;
					break;
				}
			}

			refreshValuePanel();

			if (condition.getValue() != null) {
				Value value = condition.getValue();
				if (value instanceof ColumnReference) {
					columnRefRadioButton.setSelected(true);
					columnRefField.setValue((ColumnReference) value);
				}
				else if (value instanceof Reference) {
					attrRefRadioButton.setSelected(true);
					attrRefField.setValue((Reference) value);
				}
				else if (value instanceof MathExpressionValue) {
					if (allowValueChangeOnly && ((MathExpressionValue) value).getValue() != null) {
						valueRadioButton.setSelected(true);
						setValueString(((MathExpressionValue) value).getValue());
					}
					else {
						calcRadioButton.setSelected(true);
						calcColumnRefField.setValue(((MathExpressionValue) value).getColumnReference());
						calcOpCombo.setSelectedItem(((MathExpressionValue) value).getOperator());
						calcAttrRefField.setValue(((MathExpressionValue) value).getAttributeReference());
					}
				}
				else {
					valueRadioButton.setSelected(true);
					setValueString(condition.getValue().toString());
				}
			}
		}
		else {
			refField.setValue(null, null);
			commentField.setText("");
			objectNameField.setText(null);
		}
		refreshValueEnabling();
		checkReferences();
		refreshValueCard();
	}

	private void setAvailableOps(String[] ops) {
		String currentOp = (String) opCombo.getSelectedItem();
		opCombo.removeAllItems();
		boolean hasCurrentOp = false;
		for (int i = 0; i < ops.length; i++) {
			if (!hasCurrentOp && ops[i].equals(currentOp)) hasCurrentOp = true;
			opCombo.addItem(ops[i]);
		}
		if (hasCurrentOp) opCombo.setSelectedItem(currentOp);
	}

	private void initPanel() {
		ButtonGroup radioGroup = new ButtonGroup();
		radioGroup.add(valueRadioButton);
		radioGroup.add(columnRefRadioButton);
		radioGroup.add(attrRefRadioButton);
		radioGroup.add(calcRadioButton);

		valueDetailPanel.add(UIFactory.createBorderLayoutPanel(0, 0), "EMPTY");

		JPanel vpanel = UIFactory.createBorderLayoutPanel(0, 0);
		vpanel.add(valuePanel);
		valueDetailPanel.add(vpanel, "VALUE");

		vpanel = UIFactory.createBorderLayoutPanel(0, 0);
		vpanel.add(attrRefField);
		valueDetailPanel.add(vpanel, "ATTRIBUTE");

		vpanel = UIFactory.createBorderLayoutPanel(0, 0);
		vpanel.add(columnRefField);
		valueDetailPanel.add(vpanel, "COLUMN");

		vpanel = UIFactory.createBorderLayoutPanel(0, 0);
		vpanel.add(UIFactory.createLabel("label.select.column.attr"), BorderLayout.NORTH);
		JPanel panel = new JPanel(new GridLayout(1, 3));
		panel.add(calcColumnRefField);
		panel.add(calcOpCombo);
		panel.add(calcAttrRefField);
		vpanel.add(panel, BorderLayout.CENTER);
		valueDetailPanel.add(vpanel, "CALCULATION");

		vpanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT));
		vpanel.add(valueRadioButton);
		vpanel.add(columnRefRadioButton);
		vpanel.add(attrRefRadioButton);
		vpanel.add(calcRadioButton);

		JPanel vpanelWrapper = UIFactory.createBorderLayoutPanel(0, 0);
		vpanelWrapper.add(vpanel, BorderLayout.NORTH);
		vpanelWrapper.add(valueDetailPanel, BorderLayout.CENTER);
		if (allowValueChangeOnly && condition != null && condition.getValue() instanceof MathExpressionValue
				&& ((MathExpressionValue) condition.getValue()).getValue() != null) {
			MathExpressionValue mathExpValue = (MathExpressionValue) condition.getValue();
			vpanelWrapper.add(
					new JLabel(" " + mathExpValue.getOperator() + "  " + mathExpValue.getAttributeReference().toString()),
					BorderLayout.SOUTH);
		}

		GridBagLayout bag = new GridBagLayout();
		setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 0.0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(4, 4, 4, 4);

		JLabel label = UIFactory.createFormLabel("label.attribute");
		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, label);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, refField);

		label = UIFactory.createFormLabel("label.operator");
		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, label);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, opCombo);

		label = UIFactory.createFormLabel("label.value");
		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, label);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, vpanelWrapper);

		JButton createButton = new JButton("Accept");
		createButton.addActionListener(new CreateL());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelL());

		JPanel buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(createButton);
		buttonPanel.add(cancelButton);

		PanelBase.addComponent(this, bag, c, new JSeparator());
		c.insets.top = 12;
		c.insets.bottom = 8;
		PanelBase.addComponent(this, bag, c, buttonPanel);
	}

	private boolean updateCondition() {
		if (refField.getValue() == null) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { "attribute" });
			return false;
		}

		if ((valueRadioButton.isSelected() && (getValueString() == null || getValueString().length() == 0))
				|| (attrRefRadioButton.isSelected() && !attrRefField.hasValue())
				|| (columnRefRadioButton.isSelected() && columnRefField.getValue() == null)) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { "value" });
			return false;
		}

		if (condition == null) {
			condition = RuleElementFactory.getInstance().createCondition();
		}
		condition.setObjectName(objectNameField.getText());
		if (!allowValueChangeOnly) {
			condition.setReference(RuleElementFactory.getInstance().createReference(refField.getValue()));

			String opStr = (String) opCombo.getSelectedItem();
			condition.setOp(Condition.Aux.toOpInt(opStr));
		}

		if (isOpUnary()) {
			condition.setValue(RuleElementFactory.getInstance().createValue(getValueString()));
		}
		else if (valueRadioButton.isSelected()) {
			if (allowValueChangeOnly && condition != null && condition.getValue() instanceof MathExpressionValue) {
				((MathExpressionValue) condition.getValue()).setValue(getValueString());
			}
			else {
				condition.setValue(RuleElementFactory.getInstance().createValue(getValueString()));
			}
		}
		else if (attrRefRadioButton.isSelected()) {
			condition.setValue(RuleElementFactory.getInstance().createValue(
					RuleElementFactory.getInstance().createReference(attrRefField.getValue())));
		}
		else if (columnRefRadioButton.isSelected()) {
			condition.setValue(RuleElementFactory.getInstance().createValue(columnRefField.getValue()));
		}
		else if (calcRadioButton.isSelected()) {
			if (calcColumnRefField.getValue() == null) {
				ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { "Column" });
				return false;
			}
			if (!calcAttrRefField.hasValue()) {
				ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { "Attribute" });
				return false;
			}
			condition.setValue(RuleElementFactory.getInstance().createValue(
					calcColumnRefField.getValue(),
					(String) calcOpCombo.getSelectedItem(),
					RuleElementFactory.getInstance().createReference(calcAttrRefField.getValue())));
		}
		return true;
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource().equals(refField)) {
			if (refField.getValue() != null) {
				setAttribute(refField.getValue());
				refreshAvailableOps();
				refreshValuePanel();
				refreshValueEnabling();
				checkReferences();
			}
		}
	}

}
package com.mindbox.pe.client.applet.template.rule;

import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.common.AttributeReferenceSelectField;
import com.mindbox.pe.client.common.DateSelectorComboField;
import com.mindbox.pe.client.common.FloatTextField;
import com.mindbox.pe.client.common.NumberTextField;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.TemplateColumnSelectField;
import com.mindbox.pe.client.common.formatter.FormatterFactory;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.common.validate.DataTypeCompatibilityValidator;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.rule.ColumnReference;
import com.mindbox.pe.model.rule.FunctionParameter;
import com.mindbox.pe.model.rule.FunctionParameterDefinition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.rule.RuleElementFactory;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
class ParameterEditDialog extends JPanel {
	
	private static final String VALUE_KEY_BOOLEAN = "BOOLEAN";
	private static final String VALUE_KEY_CURRENCY = "CURRENCY";
	private static final String VALUE_KEY_DATE = "DATE";
	private static final String VALUE_KEY_FLOAT = "FLOAT";
	private static final String VALUE_KEY_INTEGER = "INTEGER";
	private static final String VALUE_KEY_STRING = "STRING";
	private static final String VALUE_KEY_SYMBOL = "SYMBOL";


	public static FunctionParameter editFunctionParameter(GridTemplate template, FunctionParameterDefinition paramDef,
			FunctionParameter action, boolean allowValueChangeOnly) {
		JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), true);
		dialog.setTitle("Edit Action Parameter");
		ParameterEditDialog panel = new ParameterEditDialog(template, dialog, paramDef, action);
		UIFactory.addToDialog(dialog, panel);
		if (allowValueChangeOnly) {
			panel.disallowValueChange();
		}

		dialog.setVisible(true);

		return panel.parameter;
	}

	public static FunctionParameter editFunctionParameter(GridTemplate template, FunctionParameterDefinition paramDef,
			FunctionParameter action) {
		return editFunctionParameter(template, paramDef, action, false);
	}
	
	public static FunctionParameter newFunctionParameter(GridTemplate template, FunctionParameterDefinition paramDef) {
		JDialog dialog = new JDialog(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), true);
		dialog.setTitle("New Action Parameter");
		ParameterEditDialog panel = new ParameterEditDialog(template, dialog, paramDef, null);
		UIFactory.addToDialog(dialog, panel);

		dialog.setVisible(true);

		return panel.parameter;
	}

	private final class RadioButtonL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			refreshValueCard();
		}
	}

	private class AcceptL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (updateFunctionParameter()) {
				dialog.dispose();
			}
		}
	}

	private class CancelL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			parameter = null;
			dialog.dispose();
		}
	}

	private FunctionParameter parameter = null;
	private final JDialog dialog;
	private final JTextField nameField = new JTextField(10);
	private final JTextField stringField;
	private final NumberTextField integerField;
	private final FloatTextField floatField;
	private final FloatTextField currencyField;
	private final DateSelectorComboField dateField;
	private final JFormattedTextField symbolField;
	private final JComboBox booleanCombo;
	private final CardLayout card;
	private final JPanel vCardPanel;
	private final FunctionParameterDefinition paramDef;
	private final JRadioButton valueRadioButton;
	private final JRadioButton columnRefRadioButton;
	private final JRadioButton attrRefRadioButton;
	private final CardLayout valueCard;
	private final JPanel valueDetailPanel;
	private final TemplateColumnSelectField columnRefField;
	private final AttributeReferenceSelectField attrRefField;

	/**
	 * 
	 */
	private ParameterEditDialog(GridTemplate template, JDialog dialog, FunctionParameterDefinition paramDef,
			FunctionParameter parameter) {
		this.dialog = dialog;
		this.parameter = parameter;
		this.paramDef = paramDef;

		nameField.setEditable(false);

		columnRefField = new TemplateColumnSelectField(template, false);
		int[] legalTypes = DataTypeCompatibilityValidator.getLegalGenericDataTypesForParameter(paramDef.getDeployType());
		columnRefField.setGenericDataTypes(legalTypes);
		attrRefField = new AttributeReferenceSelectField();
		attrRefField.setGenericDataTypes(legalTypes);

		RadioButtonL rbL = new RadioButtonL();
		valueRadioButton = UIFactory.createRaiodButton("label.specify.value");
		valueRadioButton.addActionListener(rbL);
		columnRefRadioButton = UIFactory.createRaiodButton("label.specify.ref.col");
		columnRefRadioButton.addActionListener(rbL);
		attrRefRadioButton = UIFactory.createRaiodButton("label.specify.ref.attr");
		attrRefRadioButton.addActionListener(rbL);

		valueCard = new CardLayout();
		valueDetailPanel = new JPanel(valueCard);
		
		stringField = new JTextField(10);
		integerField = new NumberTextField(10);
		floatField = new FloatTextField(10, false);
		currencyField = new FloatTextField(10, true);
		dateField = new DateSelectorComboField(true, false, true);
		symbolField = new JFormattedTextField(FormatterFactory.getSymbolFormatter());
		booleanCombo = new JComboBox();
		booleanCombo.addItem("TRUE");
		booleanCombo.addItem("FALSE");
		
		card = new CardLayout(0, 0);
		vCardPanel = UIFactory.createJPanel(card);
		vCardPanel.add(stringField, VALUE_KEY_STRING);
		vCardPanel.add(dateField, VALUE_KEY_DATE);
		vCardPanel.add(floatField, VALUE_KEY_FLOAT);
		vCardPanel.add(currencyField, VALUE_KEY_CURRENCY);
		vCardPanel.add(integerField, VALUE_KEY_INTEGER);
		vCardPanel.add(symbolField, VALUE_KEY_SYMBOL);
		vCardPanel.add(booleanCombo, VALUE_KEY_BOOLEAN);
		
		initPanel();

		setSize(450, 280);

		// update fields
		if (paramDef != null) {
			nameField.setText(paramDef.getName());
			if (parameter != null) {
				String message = DataTypeCompatibilityValidator.isValid(parameter, paramDef, template, DomainModel.getInstance(), true);
				if (message != null) {
					JOptionPane.showMessageDialog(this,message);
					columnRefRadioButton.setSelected(true);
				}
				else {
					if (parameter instanceof ColumnReference) {
						columnRefField.setValue((ColumnReference) parameter);
						columnRefRadioButton.setSelected(true);
					} 
					else if (parameter instanceof Reference) {
						attrRefField.setValue((Reference)parameter);
						attrRefRadioButton.setSelected(true);
					}
					else {
						setValueString(parameter.valueString());
						refreshValuePanel();
						valueRadioButton.setSelected(true);
					}
				}
			}
			else columnRefRadioButton.setSelected(true);
		} 
		else columnRefRadioButton.setSelected(true);
		
		refreshValueCard();
	}

	private void disallowValueChange() {
		valueRadioButton.setSelected(true);
		valueRadioButton.setEnabled(false);
		columnRefRadioButton.setEnabled(false);
		columnRefRadioButton.setVisible(false);
		attrRefRadioButton.setEnabled(false);
		attrRefRadioButton.setVisible(false);
		refreshValueCard();
	}
	
	private void refreshValueCard() {
		if (valueRadioButton.isSelected()) {
			valueCard.show(valueDetailPanel, "VALUE");
			refreshValuePanel();
		}
		else if (columnRefRadioButton.isSelected()) {
			valueCard.show(valueDetailPanel, "COLUMN");
		}
		else if (attrRefRadioButton.isSelected()) {
			valueCard.show(valueDetailPanel, "ATTRIBUTE");
		}
	}

	private boolean updateFunctionParameter() {
		if ((valueRadioButton.isSelected() && (getValueString() == null || getValueString().length() == 0))
				|| (columnRefRadioButton.isSelected() && columnRefField.getValue() == null) ||
				(this.attrRefRadioButton.isSelected() && attrRefField.getAttributeName() == null)
				) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { "parameter value" });
			return false;
		}

		if (parameter == null) {
			if (valueRadioButton.isSelected()) {
				parameter = RuleElementFactory.getInstance().createFunctionParameter(paramDef.getID(),
						paramDef.getName(), getValueString());
			} 
			else if (columnRefRadioButton.isSelected()) {
				parameter = RuleElementFactory.getInstance().createFunctionParameter(paramDef.getID(),
						paramDef.getName(), columnRefField.getValue());
			} else if (attrRefRadioButton.isSelected()) {
				parameter = RuleElementFactory.getInstance().createAttributeRefParameter(paramDef.getID(),
						paramDef.getName(), attrRefField.getAttributeName(), attrRefField.getClassName());
			}
		}
		else {
			if (valueRadioButton.isSelected()) {
				parameter = RuleElementFactory.getInstance().updateFunctionParameter(parameter, getValueString());
			}
			else if (columnRefRadioButton.isSelected()) {
				parameter = RuleElementFactory.getInstance().updateFunctionParameter(parameter, columnRefField.getValue());
			} 
			else if (attrRefRadioButton.isSelected()) {
				parameter = RuleElementFactory.getInstance().updateAttributeRefParameter(
						parameter, attrRefField.getAttributeName(), attrRefField.getClassName());
			}
		}

		return true;
	}

	private void initPanel() {
		GridBagLayout bag = new GridBagLayout();
		setLayout(bag);

		ButtonGroup radioGroup = new ButtonGroup();
		radioGroup.add(valueRadioButton);
		radioGroup.add(columnRefRadioButton);
		radioGroup.add(attrRefRadioButton);

		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 0.0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(4, 4, 4, 4);

		JLabel label = new JLabel("Parameter Name:");
		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, label);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, nameField);

		valueDetailPanel.add(UIFactory.createBorderLayoutPanel(0, 0), "EMPTY");

		JPanel valuePanel = UIFactory.createBorderLayoutPanel(0, 0);
		valuePanel.add(vCardPanel);
		valueDetailPanel.add(valuePanel, "VALUE");

		valuePanel = UIFactory.createBorderLayoutPanel(0, 0);
		valuePanel.add(columnRefField);
		valueDetailPanel.add(valuePanel, "COLUMN");

		valuePanel = UIFactory.createBorderLayoutPanel(0, 0);
		valuePanel.add(attrRefField);
		valueDetailPanel.add(valuePanel, "ATTRIBUTE");

		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, new JLabel(""));

		JPanel vpanel = UIFactory.createJPanel(new GridLayout(3,1));
		vpanel.add(valueRadioButton);
		vpanel.add(columnRefRadioButton);
		vpanel.add(attrRefRadioButton);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, vpanel);

		label = new JLabel("Value:");
		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, label);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, valueDetailPanel);

		JButton createButton = new JButton("Accept");
		createButton.addActionListener(new AcceptL());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelL());

		JPanel buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(createButton);
		buttonPanel.add(cancelButton);

		PanelBase.addComponent(this, bag, c, new JSeparator());
		c.insets.top = 16;
		c.insets.bottom = 8;
		PanelBase.addComponent(this, bag, c, buttonPanel);
	}

	private String getValueString() {
		DeployType deployType = paramDef.getDeployType();
		if (deployType == null) {
			return stringField.getText();
		}
		else if (deployType == DeployType.BOOLEAN) {
			return booleanCombo.getSelectedItem().toString();
		}
		else if (deployType == DeployType.CODE || deployType == DeployType.SYMBOL) {
			return symbolField.getText();
		}
		else if (deployType == DeployType.STRING) {
			return stringField.getText();
		}
		else if (deployType == DeployType.CURRENCY) {
			return currencyField.getText();
		}
		else if (deployType == DeployType.FLOAT || deployType == DeployType.PERCENT) {
			return floatField.getText();
		}
		else if (deployType == DeployType.DATE) {
			return (dateField.getDate() == null ? "" : UIConfiguration.FORMAT_DATE.format(dateField.getDate()));
		}
		else if (deployType == DeployType.INTEGER) {
			return integerField.getText();
		}
		
		return stringField.getText();
	}

	private void setValueString(String value) {
		DeployType deployType = paramDef.getDeployType();
		if (deployType == DeployType.BOOLEAN) {
			booleanCombo.setSelectedIndex((Boolean.valueOf(value).booleanValue() ? 0 : 1));
		}
		else if (deployType == DeployType.CODE || deployType == DeployType.SYMBOL) {
			symbolField.setText(value);
		}
		else if (deployType == DeployType.STRING) {
			stringField.setText(value);
		}
		else if (deployType == DeployType.CURRENCY) {
			try {
				float f = Float.valueOf(value).floatValue();
				currencyField.setValue(f);
			}
			catch (Exception e) {
				currencyField.setText("");
			}
		}
		else if (deployType == DeployType.FLOAT || deployType == DeployType.PERCENT) {
			try {
				float f = Float.valueOf(value).floatValue();
				floatField.setValue(f);
			}
			catch (Exception e) {
				floatField.setText("");
			}
		}
		else if (deployType == DeployType.DATE) {
			try {
				dateField.setDate(UIConfiguration.FORMAT_DATE.parse(value));
			}
			catch (Exception e) {
				dateField.setValue(null);
			}
		}
		else if (deployType == DeployType.INTEGER) {
			try {
				int i = Integer.parseInt(value);
				integerField.setValue(i);
			}
			catch (Exception e) {
				integerField.setText("");
			}
		}
		else {
			stringField.setText(value);
		}
	}
	private void refreshValuePanel() {
		DeployType deployType = paramDef.getDeployType();
		if (deployType == null) {
			card.show(vCardPanel, VALUE_KEY_STRING);
		}
		else if (deployType == DeployType.BOOLEAN) {
			card.show(vCardPanel, VALUE_KEY_BOOLEAN);
		}
		else if (deployType == DeployType.CODE || deployType == DeployType.SYMBOL) {
			card.show(vCardPanel, VALUE_KEY_SYMBOL);
		}
		else if (deployType == DeployType.STRING) {
			card.show(vCardPanel, VALUE_KEY_STRING);
		}
		else if (deployType == DeployType.CURRENCY) {
			card.show(vCardPanel, VALUE_KEY_CURRENCY);
		}
		else if (deployType == DeployType.FLOAT || deployType == DeployType.PERCENT) {
			card.show(vCardPanel, VALUE_KEY_FLOAT);
		}
		else if (deployType == DeployType.DATE) {
			card.show(vCardPanel, VALUE_KEY_DATE);
		}
		else if (deployType == DeployType.INTEGER) {
			card.show(vCardPanel, VALUE_KEY_INTEGER);
		}
		else  {
			card.show(vCardPanel, VALUE_KEY_STRING);
		}
	}

}
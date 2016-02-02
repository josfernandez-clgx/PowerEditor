/*
 * Created on 2004. 2. 13.
 *
 */
package com.mindbox.pe.client.applet.template.rule;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.IDNameObjectComboBox;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.FunctionCall;
import com.mindbox.pe.model.rule.FunctionParameter;
import com.mindbox.pe.model.rule.FunctionTypeDefinition;
import com.mindbox.pe.model.rule.RuleAction;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.model.rule.TestCondition;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public abstract class FunctionEditDialog extends JPanel {

	public static RuleAction editRuleAction(Frame owner, TemplateUsageType usageType, RuleAction action) {
		JDialog dialog = new JDialog(owner, true);
		dialog.setTitle("Edit Rule Action");
		ActionEditDialog panel = new ActionEditDialog(dialog, usageType, action);
		UIFactory.addToDialog(dialog, panel);

		dialog.setVisible(true);

		return (RuleAction)panel.function;
	}

	public static RuleAction newRuleAction(Frame owner, TemplateUsageType usageType) {
		JDialog dialog = new JDialog(owner, true);
		dialog.setTitle("New Rule Action");
		ActionEditDialog panel = new ActionEditDialog(dialog, usageType, null);
		UIFactory.addToDialog(dialog, panel);

		dialog.setVisible(true);

		return (RuleAction)panel.function;
	}

	public static TestCondition editTestCondition(Frame owner, TestCondition test) {
		JDialog dialog = new JDialog(owner, true);
		dialog.setTitle("Edit Test Condition");
		TestConditionEditDialog panel = new TestConditionEditDialog(dialog, test);
		UIFactory.addToDialog(dialog, panel);

		dialog.setVisible(true);

		return (TestCondition)panel.function;
	}

	public static TestCondition newTestCondition(Frame owner) {
		JDialog dialog = new JDialog(owner, true);
		dialog.setTitle("New Test Condition");
		TestConditionEditDialog panel = new TestConditionEditDialog(dialog, null);
		UIFactory.addToDialog(dialog, panel);

		dialog.setVisible(true);

		return (TestCondition)panel.function;
	}

	private class AcceptL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (updateFunctionCall()) {
				//dialog.setVisible(false);
				dialog.dispose();
			}
		}
	}

	private class CancelL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			function = null;
			//dialog.setVisible(false);
			dialog.dispose();
		}
	}

	private class TypeComboL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			resetDescField();
		}
	}

	protected FunctionCall function = null;
	private final JDialog dialog;

	private final IDNameObjectComboBox typeCombo;
	private final JTextField typeDescField = new JTextField(10);

	/**
	 * 
	 */
	protected FunctionEditDialog(JDialog dialog, FunctionCall function, Object typeDeterminer) {
		this.dialog = dialog;
		this.function = function;

		typeDescField.setEditable(false);
		
		List<? extends FunctionTypeDefinition> typeList = getTypeList(typeDeterminer);
		typeCombo = new IDNameObjectComboBox(false, getIconString());
		for (Iterator<? extends FunctionTypeDefinition> iter = typeList.iterator(); iter.hasNext();) {
			FunctionTypeDefinition element = iter.next();
			typeCombo.addItem(element);
		}

		initPanel();

		setSize(380, 184);
		
		if (function != null && function.getFunctionType() != null) {
			typeCombo.setSelectedItem(function.getFunctionType());
		}
		
		resetDescField();
		
		typeCombo.addActionListener(new TypeComboL());
	}

	private void resetDescField() {
		if (typeCombo.getSelectedIndex() >= 0) {
			typeDescField.setText(((FunctionTypeDefinition)typeCombo.getSelectedItem()).getDescription());
		}
		else {
			typeDescField.setText("");
		}
	}
	
	private boolean updateFunctionCall() {
		if (typeCombo.getSelectedIndex() < 0) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { "function type" });
			return false;
		}

		List<FunctionParameter> oldParams = null; 
		FunctionTypeDefinition oldTypeDef = null;
		if (function == null) {
			function = createFunctionCallInstance();
		} else {
			if (function.size() > 0) oldParams = function.getElements();			
			oldTypeDef = function.getFunctionType();
		}

		FunctionTypeDefinition typeDef = (FunctionTypeDefinition) typeCombo.getSelectedItem();
		
		
		if (oldTypeDef == null || (!oldTypeDef.equals(typeDef))) 
			RuleElementFactory.getInstance().createParametersForFunctionCall(function, typeDef, oldParams, oldTypeDef);
		return true;
	}
	

	private void initPanel() {
		GridBagLayout bag = new GridBagLayout();
		setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 0.0;
		c.gridheight = 1;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(4, 4, 4, 4);

		JLabel label = UIFactory.createFormLabel(getFunctionTypeLabel());
		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, label);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, typeCombo);

		label = UIFactory.createFormLabel("label.desc");
		c.gridwidth = 1;
		c.weightx = 0.0;
		PanelBase.addComponent(this, bag, c, label);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		PanelBase.addComponent(this, bag, c, this.typeDescField);

		JButton createButton = new JButton("Accept");
		createButton.addActionListener(new AcceptL());
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
	
	abstract public String getIconString();
	abstract public FunctionCall createFunctionCallInstance();
	abstract public String getFunctionTypeLabel();
	abstract public List<? extends FunctionTypeDefinition> getTypeList(Object typeDeterminer);
	
}

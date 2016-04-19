package com.mindbox.pe.client.applet.action;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mseries.ui.MChangeListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.InputValidationException;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.entities.EntityManagementButtonPanel;
import com.mindbox.pe.client.common.detail.DetailChangeListener;
import com.mindbox.pe.client.common.detail.IDNameDescriptionObjectDetailPanel;
import com.mindbox.pe.client.common.event.ValueChangeEvent;
import com.mindbox.pe.client.common.event.ValueChangeListener;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.rule.FunctionParameterDefinition;
import com.mindbox.pe.model.rule.FunctionTypeDefinition;

public abstract class FunctionDetailsPanel<T extends FunctionTypeDefinition> extends IDNameDescriptionObjectDetailPanel<T, EntityManagementButtonPanel<T>> implements ValueChangeListener,
		ListSelectionListener {

	private class DetailChangeL implements DetailChangeListener {

		public void detailChanged() {
		}

		public void detailSaved() {
			try {
				ClientUtil.getParent().reloadTemplates();
			}
			catch (ServerException ex) {
				ClientUtil.getInstance().showErrorDialog(ex);
			}
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private JTextArea ruleTextArea;
	protected JTabbedPane tab;
	private FunctionParameterManagementPanel apPanel;
	private JCheckBox wrapCheckbox;

	public FunctionDetailsPanel(PeDataType et) {
		super(et);
		this.addDetailChangeListener(new DetailChangeL());
	}

	@Override
	protected void addComponents(GridBagLayout bag, GridBagConstraints c) {
		super.addComponents(bag, c);

		initComponents();

		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		addComponent(this, bag, c, tab);

		JPanel wcbPanel = UIFactory.createFlowLayoutPanel(FlowLayout.RIGHT, 0, 0);
		wcbPanel.add(wrapCheckbox);
		JPanel rulePanel = UIFactory.createBorderLayoutPanel(1, 1);
		rulePanel.add(new JScrollPane(ruleTextArea), BorderLayout.CENTER);
		rulePanel.add(wcbPanel, BorderLayout.NORTH);

		tab.addTab(ClientUtil.getInstance().getLabel("tab.deployment.rule"), rulePanel);
		tab.addTab(ClientUtil.getInstance().getLabel("tab.action.parameters"), apPanel);
	}

	@Override
	protected void addDocumentListener(final DocumentListener dl, final MChangeListener mchangeListener) {
		super.addDocumentListener(dl);
		ruleTextArea.getDocument().addDocumentListener(dl);
		apPanel.addValueChangeListener(this);
	}

	@Override
	public void clearFields() {
		super.clearFields();
		ruleTextArea.setText("");
		apPanel.clear();
	}

	abstract public T createFunctionTypeDefinition();

	protected void initComponents() {
		ruleTextArea = new JTextArea();
		ruleTextArea.setLineWrap(true);

		wrapCheckbox = new JCheckBox("Wrap Text");
		wrapCheckbox.setSelected(true);
		wrapCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ruleTextArea.setLineWrap(wrapCheckbox.isSelected());
			}
		});

		apPanel = new FunctionParameterManagementPanel(new FunctionParameterTableModel(), null);
		tab = new JTabbedPane(JTabbedPane.TOP);
		tab.setFocusable(false);
	}

	@Override
	protected void populateDetails(T object) {
		super.populateDetails(object);
		if (object != null && object instanceof FunctionTypeDefinition) {
			FunctionTypeDefinition function = (FunctionTypeDefinition) object;
			ruleTextArea.setText(function.getDeploymentRule());
			apPanel.setDataList(function.getParameterDefinitionList());
		}
	}

	@Override
	public void populateForClone(T object) {
		super.populateForClone(object);
		ruleTextArea.setText(((FunctionTypeDefinition) object).getDeploymentRule());
		FunctionTypeDefinition function = (FunctionTypeDefinition) object;
		apPanel.setDataList(function.getParameterDefinitionList());
	}

	@Override
	protected void removeDocumentListener(DocumentListener dl, final MChangeListener mchangeListener) {
		super.removeDocumentListener(dl);
		ruleTextArea.getDocument().removeDocumentListener(dl);
		apPanel.removeValueChangeListener(this);
	}

	@Override
	protected void setCurrentObjectFromFields() {
		apPanel.stopTableEditing();
		T function;
		if (currentObject == null) {
			function = createFunctionTypeDefinition();
		}
		else {
			function = currentObject;
		}
		function.setName(super.getNameFieldText());
		function.setDescription(super.getDescFieldText());
		function.setDeploymentRule(ruleTextArea.getText());
		// update parameter definitions
		function.clearParameterDefinitions();
		for (FunctionParameterDefinition parameterDefinition : apPanel.getDataList()) {
			function.addParameterDefinition(parameterDefinition);
		}
		currentObject = function;
	}

	@Override
	protected void setEnabledFields(boolean enabled) {
		super.setEnabledFields(enabled);
		ruleTextArea.setEnabled(enabled);
		apPanel.setEnabled(enabled);
	}

	@Override
	protected void validateFields() throws InputValidationException {
		apPanel.stopTableEditing();

		String deploymentRule = ruleTextArea.getText();
		if (deploymentRule == null || UtilBase.trim(deploymentRule).length() == 0) {
			throw new InputValidationException(ClientUtil.getInstance().getMessage("msg.error.deployment.rule.empty"));
		}
	}


	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		fireDetailChanged();
	}

	@Override
	public void valueChanged(ValueChangeEvent e) {
		fireDetailChanged();
	}
}

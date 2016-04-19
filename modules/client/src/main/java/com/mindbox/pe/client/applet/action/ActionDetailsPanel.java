package com.mindbox.pe.client.applet.action;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JScrollPane;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;

import mseries.ui.MChangeListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.InputValidationException;
import com.mindbox.pe.client.common.CheckList;
import com.mindbox.pe.client.common.event.ValueChangeEvent;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.ActionTypeDefinition;

public final class ActionDetailsPanel extends FunctionDetailsPanel<ActionTypeDefinition> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private CheckList usageTypeCheckList;

	public ActionDetailsPanel() {
		super(PeDataType.GUIDELINE_ACTION);
	}

	protected void addComponents(GridBagLayout bag, GridBagConstraints c) {
		super.addComponents(bag, c);
		tab.addTab(ClientUtil.getInstance().getLabel("tab.usage.types"), new JScrollPane(usageTypeCheckList));
	}

	protected void addDocumentListener(DocumentListener dl, final MChangeListener mchangeListener) {
		super.addDocumentListener(dl);
		usageTypeCheckList.addListSelectionListener(this);
	}

	public void clearFields() {
		super.clearFields();
		usageTypeCheckList.getSelectionModel().clearSelection();
	}

	public ActionTypeDefinition createFunctionTypeDefinition() {
		return new ActionTypeDefinition();
	}

	protected void initComponents() {
		super.initComponents();
		usageTypeCheckList = new CheckList();
		usageTypeCheckList.setModel(EntityModelCacheFactory.getInstance().getUsageTypeModel());
	}

	protected void populateDetails(ActionTypeDefinition object) {
		super.populateDetails(object);
		if (object != null) {
			ActionTypeDefinition action = object;
			usageTypeCheckList.getSelectionModel().clearSelection();
			TemplateUsageType[] usageTypes = action.getUsageTypes();
			for (int i = 0; i < usageTypes.length; i++) {
				usageTypeCheckList.setSelectedValue(usageTypes[i], true);
			}
		}
	}

	public void populateForClone(ActionTypeDefinition object) {
		super.populateForClone(object);
		usageTypeCheckList.getSelectionModel().clearSelection();
		ActionTypeDefinition action = object;
		TemplateUsageType[] usageTypes = action.getUsageTypes();
		for (int i = 0; i < usageTypes.length; i++) {
			usageTypeCheckList.setSelectedValue(usageTypes[i], true);
		}
	}

	protected void removeDocumentListener(DocumentListener dl, final MChangeListener mchangeListener) {
		super.removeDocumentListener(dl);
		usageTypeCheckList.removeListSelectionListener(this);
	}

	protected void setCurrentObjectFromFields() {
		super.setCurrentObjectFromFields();
		ActionTypeDefinition action = (ActionTypeDefinition) currentObject;

		action.clearUsageTypes();
		Object[] usageTypes = usageTypeCheckList.getSelectedValues();
		for (int i = 0; i < usageTypes.length; i++)
			if (usageTypes[i] instanceof TemplateUsageType) action.addUsageType((TemplateUsageType) usageTypes[i]);
	}

	protected void setEnabledFields(boolean enabled) {
		super.setEnabledFields(enabled);
		usageTypeCheckList.setEnabled(enabled);
	}

	protected void validateFields() throws InputValidationException {
		super.validateFields();
		if (usageTypeCheckList.getSelectedIndices() == null || usageTypeCheckList.getSelectedIndices().length == 0) {
			throw new InputValidationException(ClientUtil.getInstance().getMessage("msg.error.no.usage.types.for.action"));
		}
	}

	public void valueChanged(ListSelectionEvent arg0) {
		fireDetailChanged();
	}

	public void valueChanged(ValueChangeEvent e) {
		fireDetailChanged();
	}
}

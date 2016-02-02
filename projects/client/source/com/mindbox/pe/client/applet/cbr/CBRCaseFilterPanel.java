/*
 * Created on Oct 15, 2004
 */
package com.mindbox.pe.client.applet.cbr;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.entities.EntityManagementButtonPanel;
import com.mindbox.pe.client.common.CBRAttributeComboBox;
import com.mindbox.pe.client.common.NumberOrTextField;
import com.mindbox.pe.client.common.NumberTextField;
import com.mindbox.pe.client.common.filter.IDNameDescriptionObjectFilterPanel;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.CBRAttribute;
import com.mindbox.pe.model.CBRCase;
import com.mindbox.pe.model.CBRCaseBase;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.filter.CBRCaseSearchFilter;
import com.mindbox.pe.model.filter.SearchFilter;

/**
 * CBR Case filter panel.
 * @author deklerk
 */
public class CBRCaseFilterPanel extends IDNameDescriptionObjectFilterPanel<CBRCase,EntityManagementButtonPanel<CBRCase>> implements ActionListener, ChangeListener {

	private CBRAttributeComboBox aCombo;
	private JComboBox vCombo;
	private NumberOrTextField inputField1;
	private NumberTextField inputField2;
	private CBRCaseBase caseBase;
	
	public CBRCaseFilterPanel(CBRCaseSelectionPanel selectionPanel,
			boolean hideManagementButtons, CBRCaseBase caseBase) {	
		super(selectionPanel, EntityType.CBR_CASE, hideManagementButtons);
		this.caseBase = caseBase;
		aCombo.populateAttributes(caseBase.getID());
	}
	
	public CBRAttribute getCBRAttribute() {
		return this.aCombo.getSelectedCBRAttribute();
	}
	
	
	public int getCBRAttributeID() {
		CBRAttribute a = this.aCombo.getSelectedCBRAttribute();
		return a == null ? Persistent.UNASSIGNED_ID : a.getId(); 
	}
	

	
	protected void clearSearchFields() {
		super.clearSearchFields();
		this.aCombo.selectCBRAttribute(null);
		this.vCombo.setSelectedIndex(0);
		this.inputField1.setText("");
		this.inputField2.setText("");
		enableInputFields();
	}

	protected SearchFilter<CBRCase> getSearchFilterFromFields() {
		CBRCaseSearchFilter filter = new CBRCaseSearchFilter();
		filter.setNameCriterion(getNameFieldText());
		filter.setDescriptionCriterion(this.getDescFieldText());
		filter.setAttributeIDCriterion(this.getCBRAttributeID());
		filter.setValueSearchType(this.getValueSearchTypeFromCombo());
		filter.setValueSearchStringCriterion(this.inputField1.getText());
		filter.setValueSearchIntMinCriterion(this.inputField1.getIntValue());
		filter.setValueSearchIntMaxCriterion(this.inputField2.getIntValue());
		filter.setCaseBaseID(this.caseBase.getID());
		return filter;
	}

	protected void addComponents(GridBagLayout bag, GridBagConstraints c) {
		super.addComponents(bag, c);
		try {
			aCombo = CBRAttributeComboBox.createInstance(true);
		} catch (ServerException x) {
		}
		inputField1 = new NumberOrTextField(10,Constants.CBR_NULL_DATA_EQUIVALENT_VALUE,Constants.CBR_NULL_DATA_EQUIVALENT_VALUE,true);
		inputField1.setEnabled(false);
		inputField2 = new NumberTextField(10,Constants.CBR_NULL_DATA_EQUIVALENT_VALUE,Constants.CBR_NULL_DATA_EQUIVALENT_VALUE,true);
		inputField2.setEnabled(false);

		// HACK ALERT:  These strings have to match numeric constants in CBRCaseSearchFilter.java in order
		vCombo = new JComboBox(new String[] {ClientUtil.getInstance().getLabel("label.combo.case.search.any"),
			ClientUtil.getInstance().getLabel("label.combo.case.search.equal.to"),
			ClientUtil.getInstance().getLabel("label.combo.case.search.not.equal.to"),
			ClientUtil.getInstance().getLabel("label.combo.case.search.contains"),
			ClientUtil.getInstance().getLabel("label.combo.case.search.does.not.contain"),
			ClientUtil.getInstance().getLabel("label.combo.case.search.between"),
			ClientUtil.getInstance().getLabel("label.combo.case.search.not.between")});
		vCombo.addActionListener(this);
		
		c.gridwidth = 1;
		c.weightx = 1.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.cbr.attribute"));
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, aCombo);
		
		c.gridwidth = 1;
		c.weightx = 1.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.cbr.value"));
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, vCombo);
		
		c.gridwidth = 1;
		c.weightx = 1.0;
		addComponent(this, bag, c, new JLabel(" "));
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, inputField1);
		
		c.gridwidth = 1;
		c.weightx = 1.0;
		JLabel andLabel = new JLabel("and");
		andLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		addComponent(this, bag, c, andLabel);
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, inputField2);		
	}
	
	private int getValueSearchTypeFromCombo() {
		return this.vCombo.getSelectedIndex() + CBRCaseSearchFilter.ANY_VALUE;
	}
	
	private void enableInputFields() {
		switch (getValueSearchTypeFromCombo()) {
		case CBRCaseSearchFilter.ANY_VALUE:
		{
			inputField1.setEnabled(false);
			inputField2.setEnabled(false);
			break;
		}
		case CBRCaseSearchFilter.VALUE_EQUAL_TO:
		case CBRCaseSearchFilter.VALUE_NOT_EQUAL_TO:
		case CBRCaseSearchFilter.VALUE_CONTAINS:
		case CBRCaseSearchFilter.VALUE_DOES_NOT_CONTAIN:
		{
			inputField1.setEnabled(true);
			inputField1.setNumberInputMode(false);
			inputField2.setEnabled(false);
			break;
		}
		case CBRCaseSearchFilter.VALUE_BETWEEN:
		case CBRCaseSearchFilter.VALUE_NOT_BETWEEN:
		{
			inputField1.setEnabled(true);
			inputField1.setNumberInputMode(true);
			inputField2.setEnabled(true);
			break;
		}
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		enableInputFields();
	}
	
	
	public void stateChanged(ChangeEvent arg0) {
		aCombo.populateAttributes(caseBase.getID());
	}
}

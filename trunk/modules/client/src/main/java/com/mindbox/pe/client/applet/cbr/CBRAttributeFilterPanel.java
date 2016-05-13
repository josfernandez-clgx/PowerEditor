package com.mindbox.pe.client.applet.cbr;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.entities.EntityManagementButtonPanel;
import com.mindbox.pe.client.common.CBRAttributeTypeComboBox;
import com.mindbox.pe.client.common.filter.IDNameDescriptionObjectFilterPanel;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.cbr.CBRAttribute;
import com.mindbox.pe.model.cbr.CBRAttributeType;
import com.mindbox.pe.model.cbr.CBRCaseBase;
import com.mindbox.pe.model.filter.CBRAttributeSearchFilter;
import com.mindbox.pe.model.filter.SearchFilter;

/**
 * CBR attribute filter panel.
 * @author deklerk
 * @since PowerEditor 4.1.0
 *
 */
public class CBRAttributeFilterPanel extends IDNameDescriptionObjectFilterPanel<CBRAttribute, EntityManagementButtonPanel<CBRAttribute>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private CBRAttributeTypeComboBox atCombo;
	private CBRCaseBase caseBase;

	public CBRAttributeFilterPanel(CBRAttributeSelectionPanel selectionPanel, boolean hideManagementButtons, CBRCaseBase caseBase) {
		super(selectionPanel, PeDataType.CBR_ATTRIBUTE, hideManagementButtons);
		this.caseBase = caseBase;
	}

	public CBRAttributeType getCBRAttributeType() {
		return this.atCombo.getSelectedCBRAttributeType();
	}


	public int getCBRAttributeTypeID() {
		CBRAttributeType at = this.atCombo.getSelectedCBRAttributeType();
		return at == null ? Persistent.UNASSIGNED_ID : at.getId();
	}


	protected void clearSearchFields() {
		super.clearSearchFields();
		this.atCombo.selectCBRAttributeType(null);
	}

	protected SearchFilter<CBRAttribute> getSearchFilterFromFields() {
		CBRAttributeSearchFilter filter = new CBRAttributeSearchFilter();
		filter.setNameCriterion(getNameFieldText());
		filter.setDescriptionCriterion(this.getDescFieldText());
		filter.setAttributeTypeIDCriterion(this.getCBRAttributeTypeID());
		filter.setCaseBaseID(this.caseBase.getID());
		return filter;
	}


	protected void addComponents(GridBagLayout bag, GridBagConstraints c) {
		super.addComponents(bag, c);
		try {
			atCombo = CBRAttributeTypeComboBox.createInstance(true);
		}
		catch (ServerException x) {
		}
		c.gridwidth = 1;
		c.weightx = 1.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.cbr.attribute.type"));
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, atCombo);

	}

}

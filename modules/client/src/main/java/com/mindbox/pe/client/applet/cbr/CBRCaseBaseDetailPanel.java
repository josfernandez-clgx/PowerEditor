package com.mindbox.pe.client.applet.cbr;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.event.DocumentListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.InputValidationException;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.entities.EntityManagementButtonPanel;
import com.mindbox.pe.client.common.CBRCaseClassComboBox;
import com.mindbox.pe.client.common.CBRScoringFunctionComboBox;
import com.mindbox.pe.client.common.DateSelectorComboField;
import com.mindbox.pe.client.common.detail.DetailChangeListener;
import com.mindbox.pe.client.common.detail.IDNameDescriptionObjectDetailPanel;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.ui.NumberTextField;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.cbr.CBRCaseBase;
import com.mindbox.pe.model.filter.AllSearchFilter;

import mseries.ui.MChangeListener;

/**
 * CBR Case case detail panel.
 * @author deklerk
 * @since PowerEditor 4.1.0
 */
public class CBRCaseBaseDetailPanel extends IDNameDescriptionObjectDetailPanel<CBRCaseBase, EntityManagementButtonPanel<CBRCaseBase>> implements ActionListener {
	private class DetailChangeL implements DetailChangeListener {

		@Override
		public void detailChanged() {
		}

		@Override
		public void detailSaved() {
			CBRPanel.getInstance().updateFromServer();
			CBRPanel.getInstance().selectCaseBase((CBRCaseBase) currentObject);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;
	private NumberTextField matchThresholdField;
	private NumberTextField maximumMatchesField;
	private CBRCaseClassComboBox caseClassCombo;
	private CBRScoringFunctionComboBox scoringFunctionCombo;
	private JTextField namingAttributeField;
	private JTextField indexFileField;

	private DateSelectorComboField effDateField, expDateField;

	private DocumentListener documentListener = null;

	public CBRCaseBaseDetailPanel(CBRCaseBase cb) throws ServerException {
		super(PeDataType.CBR_CASE_BASE);
		this.addDetailChangeListener(new DetailChangeL());
		setBorder(UIFactory.createTitledBorder(""));

		populateFields(cb);

		this.setForViewOnly(false);

		if (cb.getId() == CBRCaseBase.UNASSIGNED_ID) fireDetailChanged();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (documentListener != null) documentListener.insertUpdate(null);
	}

	@Override
	protected void addComponents(GridBagLayout bag, GridBagConstraints c) {
		matchThresholdField = new NumberTextField(10, Constants.CBR_NULL_DATA_EQUIVALENT_VALUE, Constants.CBR_NULL_DATA_EQUIVALENT_VALUE, true);
		maximumMatchesField = new NumberTextField(10, Constants.CBR_NULL_DATA_EQUIVALENT_VALUE, Constants.CBR_NULL_DATA_EQUIVALENT_VALUE, false);
		namingAttributeField = new JTextField(30);
		indexFileField = new JTextField(30);
		try {
			effDateField = new DateSelectorComboField();
			expDateField = new DateSelectorComboField();
			caseClassCombo = CBRCaseClassComboBox.createInstance();
			scoringFunctionCombo = CBRScoringFunctionComboBox.createInstance();
		}
		catch (ServerException x) {
		}
		super.addComponents(bag, c);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.cbr.scoring.function"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, scoringFunctionCombo);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.cbr.match.threshold"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, matchThresholdField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.cbr.maximum.matches"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, maximumMatchesField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.date.activation"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, effDateField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.date.expiration"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, expDateField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.cbr.case.class"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, caseClassCombo);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.cbr.naming.attribute"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, namingAttributeField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(this, bag, c, UIFactory.createFormLabel("label.cbr.index.file"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, indexFileField);
	}

	@Override
	protected void addDocumentListener(DocumentListener dl, final MChangeListener mchangeListener) {
		super.addDocumentListener(dl);
		matchThresholdField.getDocument().addDocumentListener(dl);
		maximumMatchesField.getDocument().addDocumentListener(dl);
		namingAttributeField.getDocument().addDocumentListener(dl);
		indexFileField.getDocument().addDocumentListener(dl);
		documentListener = dl;
		effDateField.addActionListener(this);
		expDateField.addActionListener(this);
		caseClassCombo.addActionListener(this);
		scoringFunctionCombo.addActionListener(this);

	}

	@Override
	public void clearFields() {
	}

	@Override
	protected void populateDetails(CBRCaseBase object) {
		CBRCaseBase caseBase = object;
		super.populateDetails(caseBase);
		if (caseBase.getMatchThreshold() != Constants.CBR_NULL_DATA_EQUIVALENT_VALUE) this.matchThresholdField.setValue(caseBase.getMatchThreshold());
		if (caseBase.getMaximumMatches() != Constants.CBR_NULL_DATA_EQUIVALENT_VALUE) this.maximumMatchesField.setValue(caseBase.getMaximumMatches());
		this.caseClassCombo.selectCBRCaseClass(caseBase.getCaseClass());
		this.scoringFunctionCombo.selectCBRScoringFunction(caseBase.getScoringFunction());
		this.namingAttributeField.setText(caseBase.getNamingAttribute());
		this.indexFileField.setText(caseBase.getIndexFile());
		effDateField.setValue(caseBase.getEffectiveDate());
		expDateField.setValue(caseBase.getExpirationDate());
	}

	@Override
	protected void removeDocumentListener(DocumentListener dl, final MChangeListener mchangeListener) {
		super.removeDocumentListener(dl);
		matchThresholdField.getDocument().removeDocumentListener(dl);
		maximumMatchesField.getDocument().removeDocumentListener(dl);
		namingAttributeField.getDocument().removeDocumentListener(dl);
		indexFileField.getDocument().removeDocumentListener(dl);
		effDateField.removeActionListener(this);
		expDateField.removeActionListener(this);
		caseClassCombo.removeActionListener(this);
		scoringFunctionCombo.removeActionListener(this);
	}

	@Override
	protected void setCurrentObjectFromFields() {
		CBRCaseBase caseBase = currentObject;
		caseBase.setName(getNameFieldText());
		caseBase.setDescription(getDescFieldText());
		caseBase.setMatchThreshold(matchThresholdField.getIntValue());
		caseBase.setMaximumMatches(maximumMatchesField.getIntValue());
		caseBase.setCaseClass(caseClassCombo.getSelectedCBRCaseClass());
		caseBase.setScoringFunction(scoringFunctionCombo.getSelectedCBRScoringFunction());
		caseBase.setNamingAttribute(namingAttributeField.getText());
		caseBase.setIndexFile(indexFileField.getText());
		caseBase.setEffectiveDate(effDateField.getValue());
		caseBase.setExpirationDate(expDateField.getValue());
	}

	@Override
	protected void validateFields() throws InputValidationException {
		super.validateFields();
		if (UtilBase.trim(this.getNameFieldText()).length() == 0) throw new InputValidationException("A Case-Base name may not be blank.");
		CBRCaseBase caseBase = (CBRCaseBase) currentObject;
		try {
			List<CBRCaseBase> caseBaseList = ClientUtil.getCommunicator().search(new AllSearchFilter<CBRCaseBase>(PeDataType.CBR_CASE_BASE));
			Iterator<CBRCaseBase> it = caseBaseList.iterator();
			while (it.hasNext()) {
				CBRCaseBase cb = it.next();
				if (caseBase.getId() != cb.getId() && getNameFieldText().equalsIgnoreCase(cb.getName())) {
					throw new InputValidationException("A Case-Base named \"" + getNameFieldText() + "\" already exists in the database.");
				}
			}
		}
		catch (ServerException ex) {
			throw new InputValidationException("Server Error occured while validating this Case-Base");
		}
	}
}
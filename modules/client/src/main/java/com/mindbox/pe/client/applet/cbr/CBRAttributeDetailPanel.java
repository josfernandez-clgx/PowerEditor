package com.mindbox.pe.client.applet.cbr;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;

import mseries.ui.MChangeListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.InputValidationException;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.entities.EntityManagementButtonPanel;
import com.mindbox.pe.client.common.CBRAttributeTypeComboBox;
import com.mindbox.pe.client.common.CBRValueRangeComboBox;
import com.mindbox.pe.client.common.FloatTextField;
import com.mindbox.pe.client.common.PerfectNumberComboBox;
import com.mindbox.pe.client.common.detail.AbstractDetailPanel;
import com.mindbox.pe.client.common.event.ValueChangeEvent;
import com.mindbox.pe.client.common.event.ValueChangeListener;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.cbr.CBRAttribute;
import com.mindbox.pe.model.cbr.CBRAttributeType;
import com.mindbox.pe.model.cbr.CBRAttributeValue;
import com.mindbox.pe.model.cbr.CBRCase;
import com.mindbox.pe.model.cbr.CBRCaseBase;
import com.mindbox.pe.model.cbr.CBREnumeratedValue;
import com.mindbox.pe.model.cbr.CBRValueRange;
import com.mindbox.pe.model.filter.CBRCaseSearchFilter;
import com.mindbox.pe.model.filter.CBRExactNameSearchFilter;
import com.mindbox.pe.model.filter.NameSearchFilter;

/**
 * CBR attribute detail panel.
 * @author deklerk
 * @since PowerEditor 4.1.0
 *
 */
public class CBRAttributeDetailPanel extends AbstractDetailPanel<CBRAttribute, EntityManagementButtonPanel<CBRAttribute>> implements ActionListener, ValueChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6043787701825372027L;

	private CBRCaseBase caseBase;
	private DocumentListener documentListener = null;

	private JTextField nameField;
	private JTextField descField;
	private CBRAttributeTypeComboBox attributeTypeCombo;
	private CBRValueRangeComboBox valueRangeCombo;
	private PerfectNumberComboBox matchContributionCombo;
	private PerfectNumberComboBox mismatchPenaltyCombo;
	private PerfectNumberComboBox absencePenaltyCombo;
	private FloatTextField lowestValueField;
	private FloatTextField highestValueField;
	private FloatTextField matchIntervalField;
	private JTabbedPane tab;
	private CBREnumeratedValueManagementPanel valuePanel;

	/**
	 * @param cb CBR case base
	 */
	public CBRAttributeDetailPanel(CBRCaseBase cb) {
		super(PeDataType.CBR_ATTRIBUTE);
		caseBase = cb;
	}

	protected void addDocumentListener(DocumentListener dl, final MChangeListener mchangeListener) {
		nameField.getDocument().addDocumentListener(dl);
		descField.getDocument().addDocumentListener(dl);
		documentListener = dl;
		attributeTypeCombo.addActionListener(this);
		valueRangeCombo.addActionListener(this);
		this.matchContributionCombo.addActionListener(this);
		this.mismatchPenaltyCombo.addActionListener(this);
		this.absencePenaltyCombo.addActionListener(this);
		lowestValueField.getDocument().addDocumentListener(dl);
		highestValueField.getDocument().addDocumentListener(dl);
		matchIntervalField.getDocument().addDocumentListener(dl);
		valuePanel.addValueChangeListener(this);
	}

	protected void removeDocumentListener(DocumentListener dl, final MChangeListener mchangeListener) {
		nameField.getDocument().removeDocumentListener(dl);
		descField.getDocument().removeDocumentListener(dl);
		attributeTypeCombo.removeActionListener(this);
		valueRangeCombo.removeActionListener(this);
		this.matchContributionCombo.removeActionListener(this);
		this.mismatchPenaltyCombo.removeActionListener(this);
		this.absencePenaltyCombo.removeActionListener(this);
		lowestValueField.getDocument().removeDocumentListener(dl);
		highestValueField.getDocument().removeDocumentListener(dl);
		matchIntervalField.getDocument().removeDocumentListener(dl);
		valuePanel.removeValueChangeListener(this);
	}

	protected void addComponents(GridBagLayout bag, GridBagConstraints c) {
		this.nameField = new JTextField(10);
		this.descField = new JTextField(10);
		matchContributionCombo = new PerfectNumberComboBox();
		mismatchPenaltyCombo = new PerfectNumberComboBox();
		absencePenaltyCombo = new PerfectNumberComboBox();
		lowestValueField = new FloatTextField(10, false);
		highestValueField = new FloatTextField(10, false);
		matchIntervalField = new FloatTextField(10, false);
		try {
			attributeTypeCombo = CBRAttributeTypeComboBox.createInstance();
			valueRangeCombo = CBRValueRangeComboBox.createInstance();
		}
		catch (ServerException x) {
		}

		tab = new JTabbedPane(JTabbedPane.TOP);
		tab.setFocusable(false);

		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		addComponent(this, bag, c, tab);

		bag = new GridBagLayout();
		c.gridheight = 1;
		c.weighty = 0.0;

		JPanel generalPanel = UIFactory.createJPanel(bag);
		tab.addTab(ClientUtil.getInstance().getLabel("tab.cbr.attribute.general"), generalPanel);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(generalPanel, bag, c, UIFactory.createFormLabel("label.name"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(generalPanel, bag, c, nameField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(generalPanel, bag, c, UIFactory.createFormLabel("label.desc"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(generalPanel, bag, c, descField);
		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(generalPanel, bag, c, UIFactory.createFormLabel("label.cbr.attribute.type"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(generalPanel, bag, c, attributeTypeCombo);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(generalPanel, bag, c, UIFactory.createFormLabel("label.cbr.match.contribution"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(generalPanel, bag, c, matchContributionCombo);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(generalPanel, bag, c, UIFactory.createFormLabel("label.cbr.mismatch.penalty"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(generalPanel, bag, c, mismatchPenaltyCombo);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(generalPanel, bag, c, UIFactory.createFormLabel("label.cbr.absence.penalty"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(generalPanel, bag, c, absencePenaltyCombo);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(generalPanel, bag, c, UIFactory.createFormLabel("label.cbr.value.range"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(generalPanel, bag, c, valueRangeCombo);

		c.gridwidth = 2;
		c.weightx = 0.0;
		addComponent(generalPanel, bag, c, UIFactory.createFormLabel("label.cbr.int.range"));

		c.gridwidth = 2;
		c.weightx = 1.0;
		addComponent(generalPanel, bag, c, lowestValueField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(generalPanel, bag, c, UIFactory.createFormLabel("label.cbr.int.range.to"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(generalPanel, bag, c, highestValueField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(generalPanel, bag, c, UIFactory.createFormLabel("label.cbr.match.interval"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(generalPanel, bag, c, matchIntervalField);

		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		addComponent(generalPanel, bag, c, Box.createVerticalGlue());

		valuePanel = new CBREnumeratedValueManagementPanel(new CBREnumeratedValueTableModel(), null);
		tab.addTab(ClientUtil.getInstance().getLabel("tab.cbr.attribute.value"), valuePanel);
	}

	protected void setCurrentObjectFromFields() {
		if (currentObject == null) {
			currentObject = new CBRAttribute();
			((CBRAttribute) currentObject).setCaseBase(caseBase);
		}
		CBRAttribute attribute = (CBRAttribute) currentObject;
		attribute.setName(nameField.getText());
		attribute.setDescription(descField.getText());
		attribute.setAttributeType(attributeTypeCombo.getSelectedCBRAttributeType());
		attribute.setValueRange(valueRangeCombo.getSelectedCBRValueRange());
		Integer mc = (Integer) this.matchContributionCombo.getSelectedItem();
		attribute.setMatchContribution(mc == null ? Constants.CBR_NULL_DATA_EQUIVALENT_VALUE : mc.intValue());
		Integer mp = (Integer) this.mismatchPenaltyCombo.getSelectedItem();
		attribute.setMismatchPenalty(mp == null ? Constants.CBR_NULL_DATA_EQUIVALENT_VALUE : mp.intValue());
		Integer ap = (Integer) this.absencePenaltyCombo.getSelectedItem();
		attribute.setAbsencePenalty(ap == null ? Constants.CBR_NULL_DATA_EQUIVALENT_VALUE : ap.intValue());
		Double d = lowestValueField.getDoubleValue();
		attribute.setLowestValue(d == null ? Constants.CBR_NULL_DOUBLE_VALUE : d.doubleValue());
		d = highestValueField.getDoubleValue();
		attribute.setHighestValue(d == null ? Constants.CBR_NULL_DOUBLE_VALUE : d.doubleValue());
		d = matchIntervalField.getDoubleValue();
		attribute.setMatchInterval(d == null ? Constants.CBR_NULL_DOUBLE_VALUE : d.doubleValue());
		attribute.setEnumeratedValues(valuePanel.getDataList());
	}

	protected void populateDetails(CBRAttribute object) {
		CBRAttribute att = object;
		this.nameField.setText(att.getName());
		this.descField.setText(att.getDescription());
		this.attributeTypeCombo.selectCBRAttributeType(att.getAttributeType());
		this.valueRangeCombo.selectCBRValueRange(att.getValueRange());
		this.matchContributionCombo.setSelectedItem(new Integer(att.getMatchContribution()));
		this.mismatchPenaltyCombo.setSelectedItem(new Integer(att.getMismatchPenalty()));
		this.absencePenaltyCombo.setSelectedItem(new Integer(att.getAbsencePenalty()));
		if (att.getLowestValue() == Constants.CBR_NULL_DOUBLE_VALUE)
			this.lowestValueField.setText("");
		else
			this.lowestValueField.setValue(att.getLowestValue());
		if (att.getHighestValue() == Constants.CBR_NULL_DOUBLE_VALUE)
			this.highestValueField.setText("");
		else
			this.highestValueField.setValue(att.getHighestValue());
		if (att.getMatchInterval() == Constants.CBR_NULL_DOUBLE_VALUE)
			this.matchIntervalField.setText("");
		else
			this.matchIntervalField.setValue(att.getMatchInterval());
		valuePanel.setDataList(att.getEnumeratedValues());
	}

	public void populateForClone(CBRAttribute object) {
	}

	public void clearFields() {
		this.removeDocumentListener(this, this);
		this.currentObject = null;
		setForViewOnly(true);
		this.nameField.setText("");
		this.descField.setText("");
		this.attributeTypeCombo.setSelectedIndex(0);
		this.valueRangeCombo.setSelectedIndex(0);
		this.matchContributionCombo.setSelectedItem(new Integer(Constants.CBR_NULL_DATA_EQUIVALENT_VALUE));
		this.mismatchPenaltyCombo.setSelectedItem(new Integer(Constants.CBR_NULL_DATA_EQUIVALENT_VALUE));
		this.absencePenaltyCombo.setSelectedItem(new Integer(Constants.CBR_NULL_DATA_EQUIVALENT_VALUE));
		this.lowestValueField.setText("");
		this.highestValueField.setText("");
		this.matchIntervalField.setText("");
		this.valuePanel.clear();
		this.addDocumentListener(this, this);
	}

	protected void setEnabledFields(boolean enabled) {
		this.nameField.setEnabled(enabled);
		this.descField.setEnabled(enabled);
		this.attributeTypeCombo.setEnabled(enabled);
		this.valueRangeCombo.setEnabled(enabled);
		this.matchContributionCombo.setEnabled(enabled);
		this.mismatchPenaltyCombo.setEnabled(enabled);
		this.absencePenaltyCombo.setEnabled(enabled);
		this.setDependentEnabledFields(enabled);
	}

	private void setDependentEnabledFields(boolean enabled) {
		boolean enableNumericRange = enabled;
		boolean enableMatchInterval = enabled;
		boolean enableEnumeratedValuesPanel = enabled;
		if (enabled) {
			CBRAttributeType at = attributeTypeCombo.getSelectedCBRAttributeType();
			CBRValueRange r = valueRangeCombo.getSelectedCBRValueRange();
			enableNumericRange = at != null && at.getAskForNumericRange().booleanValue();
			enableMatchInterval = at != null && at.getAskForMatchInterval().booleanValue();
			enableEnumeratedValuesPanel = r != null && r.isEnumeratedValuesAllowed();
		}
		this.lowestValueField.setEnabled(enableNumericRange);
		this.highestValueField.setEnabled(enableNumericRange);
		this.matchIntervalField.setEnabled(enableMatchInterval);
		tab.setEnabledAt(1, enableEnumeratedValuesPanel);
		valuePanel.setEnabled(enableEnumeratedValuesPanel);
	}

	protected void validateFields() throws InputValidationException {
		super.validateFields();
		if (UtilBase.trim(this.nameField.getText()).length() == 0) throw new InputValidationException("An Attribute name may not be blank.");
		CBRAttribute att = currentObject;
		try {
			List<?> attList = ClientUtil.getCommunicator().search(new CBRExactNameSearchFilter(PeDataType.CBR_ATTRIBUTE, nameField.getText(), caseBase.getID()));
			if (attList.size() > 1 || (attList.size() == 1 && (att == null || ((CBRAttribute) attList.get(0)).getID() != att.getID())))
				throw new InputValidationException("An Attribute named \"" + nameField.getText() + "\" already exists for this Case-Base.");
		}
		catch (ServerException ex) {
			throw new InputValidationException("Server Error occured while validating this Attribute");
		}
		CBRAttributeType at = attributeTypeCombo.getSelectedCBRAttributeType();
		if (at != null && at.getAskForNumericRange().booleanValue() && this.lowestValueField.getValue() != null && this.highestValueField.getValue() != null
				&& this.lowestValueField.getDoubleValue().doubleValue() > this.highestValueField.getDoubleValue().doubleValue())
			throw new InputValidationException("The lower bound of the Numeric Range may not be higher than the upper bound.");
		CBRValueRange newVR = this.valueRangeCombo.getSelectedCBRValueRange();
		CBRValueRange oldVR = att == null ? null : att.getValueRange();
		boolean checkExistingCases = false;
		if (oldVR != null) {
			if (!oldVR.equals(newVR))
				checkExistingCases = true;
			else if (oldVR.isEnumeratedValuesAllowed()) {
				List<CBREnumeratedValue> oldEVList = att.getEnumeratedValues();
				List<CBREnumeratedValue> newEVList = valuePanel.getDataList();
				if (oldEVList.size() != newEVList.size())
					checkExistingCases = true;
				else {
					Iterator<CBREnumeratedValue> it = oldEVList.iterator();
					while (it.hasNext()) {
						CBREnumeratedValue ev = it.next();
						Iterator<CBREnumeratedValue> it2 = newEVList.iterator();
						boolean found = false;
						while (it2.hasNext()) {
							CBREnumeratedValue ev2 = it2.next();
							if (ev.getName().equalsIgnoreCase(ev2.getName())) {
								found = true;
								break;
							}
						}
						if (!found) {
							checkExistingCases = true;
							break;
						}
					}
				}
			}
		}
		if (checkExistingCases) {
			try {
				CBRCaseSearchFilter caseFilter = new CBRCaseSearchFilter();
				caseFilter.setCaseBaseID(caseBase.getID());
				caseFilter.setAttributeIDCriterion(att.getID());
				List<CBRCase> caseList = ClientUtil.getCommunicator().search(caseFilter);
				Iterator<CBRCase> it = caseList.iterator();
				while (it.hasNext()) {
					CBRCase c = (CBRCase) it.next();
					List<CBRAttributeValue> avList = c.getAttributeValues();
					Iterator<CBRAttributeValue> it2 = avList.iterator();
					while (it2.hasNext()) {
						CBRAttributeValue av = (CBRAttributeValue) it2.next();
						if (av.getAttribute().getID() == att.getID()) {
							if (!newVR.isConforming(av.getName(), valuePanel.getDataList()))
								throw new InputValidationException("Case \"" + c.getName() + " \" has the Attribute/Value pair \"" + av.getAttribute().getName() + "/" + av.getName()
										+ "\" not conforming to the Value Range \"" + newVR.getName() + "\"");
						}
					}
				}
			}
			catch (ServerException x) {
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == attributeTypeCombo) {
			CBRAttributeType at = this.attributeTypeCombo.getSelectedCBRAttributeType();
			this.valueRangeCombo.removeActionListener(this);
			this.valueRangeCombo.selectCBRValueRange(getDefaultValueRangeObject(at));
			this.valueRangeCombo.addActionListener(this);
		}
		this.setDependentEnabledFields(true);
		if (documentListener != null) documentListener.insertUpdate(null);
	}

	private CBRValueRange getDefaultValueRangeObject(CBRAttributeType at) {
		NameSearchFilter<CBRValueRange> nsf = new NameSearchFilter<CBRValueRange>(PeDataType.CBR_VALUE_RANGE);
		nsf.setNameCriterion(at.getDefaultValueRange());
		try {
			List<CBRValueRange> result = ClientUtil.getCommunicator().search(nsf);
			if (result != null && result.size() > 0) return result.get(0);
		}
		catch (Exception ex) {
			ClientUtil.handleRuntimeException(ex);
		}
		return null;
	}

	public void valueChanged(ValueChangeEvent e) {
		fireDetailChanged();
		if (documentListener != null) documentListener.insertUpdate(null);
	}

}

package com.mindbox.pe.client.applet.cbr;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.InputValidationException;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.entities.EntityManagementButtonPanel;
import com.mindbox.pe.client.common.DateSelectorComboField;
import com.mindbox.pe.client.common.OrderedListChoicePanel;
import com.mindbox.pe.client.common.detail.AbstractDetailPanel;
import com.mindbox.pe.client.common.event.ValueChangeEvent;
import com.mindbox.pe.client.common.event.ValueChangeListener;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.CBRAttributeValue;
import com.mindbox.pe.model.CBRCase;
import com.mindbox.pe.model.CBRCaseAction;
import com.mindbox.pe.model.CBRCaseBase;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.filter.CBRExactNameSearchFilter;

/**
 * @author deklerk
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CBRCaseDetailPanel extends AbstractDetailPanel<CBRCase, EntityManagementButtonPanel<CBRCase>> implements ActionListener,
		ValueChangeListener, ChangeListener {

	private CBRCaseBase caseBase;
	private DocumentListener documentListener = null;
	private JTextField nameField;
	private JTextField descField;
	private JTabbedPane tab;
	private CBRAttributeValueManagementPanel avPanel;
	private OrderedListChoicePanel<CBRCaseAction> actionListPanel;
	private List<CBRCaseAction> allActions;
	private DateSelectorComboField effDateField, expDateField;

	/**
	 * @param cb
	 */
	public CBRCaseDetailPanel(CBRCaseBase cb) {
		super(EntityType.CBR_CASE);
		caseBase = cb;
		avPanel.updateForCaseBase(caseBase.getId());

		allActions = new ArrayList<CBRCaseAction>();
		try {
			CBRCaseAction[] actions = ClientUtil.fetchAllCBRCaseActions();
			for (int i = 0; i < actions.length; i++)
				allActions.add(actions[i]);
		}
		catch (Exception x) {
		}
	}

	protected void addDocumentListener(DocumentListener dl) {
		nameField.getDocument().addDocumentListener(dl);
		descField.getDocument().addDocumentListener(dl);
		effDateField.addActionListener(this);
		expDateField.addActionListener(this);
		avPanel.addValueChangeListener(this);
		actionListPanel.addValueChangeListener(this);
	}

	protected void removeDocumentListener(DocumentListener dl) {
		nameField.getDocument().removeDocumentListener(dl);
		descField.getDocument().removeDocumentListener(dl);
		effDateField.removeActionListener(this);
		expDateField.removeActionListener(this);
		avPanel.removeValueChangeListener(this);
		actionListPanel.removeValueChangeListener(this);
	}

	protected void addComponents(GridBagLayout bag, GridBagConstraints c) {
		this.nameField = new JTextField(10);
		this.descField = new JTextField(10);

		effDateField = new DateSelectorComboField();
		expDateField = new DateSelectorComboField();

		tab = new JTabbedPane(JTabbedPane.TOP);
		tab.setFocusable(false);

		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		addComponent(this, bag, c, tab);

		bag = new GridBagLayout();
		c.gridheight = 1;
		c.weighty = 0.0;

		JPanel generalPanel = UIFactory.createJPanel(bag);
		tab.addTab(ClientUtil.getInstance().getLabel("tab.cbr.case.general"), generalPanel);

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
		addComponent(generalPanel, bag, c, UIFactory.createFormLabel("label.date.activation"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(generalPanel, bag, c, effDateField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		addComponent(generalPanel, bag, c, UIFactory.createFormLabel("label.date.expiration"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(generalPanel, bag, c, expDateField);

		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		addComponent(generalPanel, bag, c, Box.createVerticalGlue());

		c.gridheight = 1;
		c.weighty = 0.0;

		avPanel = new CBRAttributeValueManagementPanel(new CBRAttributeValueTableModel(), null);
		tab.addTab(ClientUtil.getInstance().getLabel("tab.cbr.case.attribute.values"), avPanel);

		actionListPanel = new OrderedListChoicePanel<CBRCaseAction>();
		tab.addTab(ClientUtil.getInstance().getLabel("tab.cbr.case.actions"), actionListPanel);
	}

	protected void setCurrentObjectFromFields() {
		if (currentObject == null) {
			currentObject = new CBRCase();
			currentObject.setCaseBase(caseBase);
		}
		CBRCase c = currentObject;
		c.setName(nameField.getText());
		c.setDescription(descField.getText());
		c.setAttributeValues(avPanel.getDataList());
		c.setCaseActions(this.actionListPanel.getSelectedObjects());
		c.setEffectiveDate(effDateField.getValue());
		c.setExpirationDate(expDateField.getValue());
	}

	protected void populateDetails(CBRCase object) {
		CBRCase c = (CBRCase) object;
		this.nameField.setText(c.getName());
		this.descField.setText(c.getDescription());
		effDateField.setValue(c.getEffectiveDate());
		expDateField.setValue(c.getExpirationDate());
		avPanel.setDataList(c.getAttributeValues());
		actionListPanel.setObjectLists(allActions, c.getCaseActions());
	}

	public void populateForClone(CBRCase object) {
	}

	public void clearFields() {
		this.currentObject = null;
		setForViewOnly(true);
		this.nameField.setText("");
		this.descField.setText("");
		this.effDateField.setDate(null);
		this.expDateField.setDate(null);
		this.avPanel.clear();
		actionListPanel.setObjectLists(allActions, new ArrayList<CBRCaseAction>());
	}

	protected void setEnabledFields(boolean enabled) {
		this.nameField.setEnabled(enabled);
		this.descField.setEnabled(enabled);
		this.effDateField.setEnabled(enabled);
		this.expDateField.setEnabled(enabled);
		avPanel.setEnabled(enabled);
		this.actionListPanel.setEnabled(enabled);
	}

	protected void validateFields() throws InputValidationException {
		super.validateFields();
		if (UtilBase.trim(this.nameField.getText()).length() == 0) throw new InputValidationException("A Case name may not be blank.");
		CBRCase c = (CBRCase) currentObject;
		try {
			List<?> caseList = ClientUtil.getCommunicator().search(
					new CBRExactNameSearchFilter(EntityType.CBR_CASE, nameField.getText(), caseBase.getID()));
			if (caseList.size() > 1 || (caseList.size() == 1 && (c == null || ((CBRCase) caseList.get(0)).getID() != c.getID())))
				throw new InputValidationException("A Case named \"" + nameField.getText() + "\" already exists for this Case-Base.");
		}
		catch (ServerException ex) {
			throw new InputValidationException("Server Error occured while validating this Case");
		}
		List<CBRAttributeValue> avList = this.avPanel.getDataList();
		Iterator<CBRAttributeValue> it = avList.iterator();
		while (it.hasNext()) {
			CBRAttributeValue av = it.next();
			if (av.getAttribute() == null) throw new InputValidationException("Attribute Value pairs must all have Attributes.");
			if (av.getName() == null || UtilBase.trim(av.getName()).length() == 0)
				throw new InputValidationException("Attribute Value pairs must all have Values.");
			if (!av.isValid()) {
				throw new InputValidationException("The value \"" + av.getName() + "\" does not conform to the "
						+ ClientUtil.getInstance().getLabel("label.cbr.value.range") + " \"" + av.getAttribute().getValueRange().getName()
						+ "\"");
			}
			Iterator<CBRAttributeValue> it2 = avList.iterator();
			while (it2.hasNext()) {
				CBRAttributeValue av2 = it2.next();
				if (av2.getAttribute() != null && av2.getName() != null && av2 != av && av2.getAttribute().equals(av.getAttribute())
						&& UtilBase.trim(av2.getName()).equalsIgnoreCase(UtilBase.trim(av.getName())))
					throw new InputValidationException(
							"Two Attribute Value pairs in the same case may not have the same Attribute and Value.");
			}
		}

	}


	public void actionPerformed(ActionEvent e) {
		fireDetailChanged();
		if (documentListener != null) documentListener.insertUpdate(null);
	}

	public void valueChanged(ValueChangeEvent e) {
		fireDetailChanged();
		if (documentListener != null) documentListener.insertUpdate(null);
	}

	public void stateChanged(ChangeEvent arg0) {
		avPanel.updateForCaseBase(caseBase.getId());
	}
}
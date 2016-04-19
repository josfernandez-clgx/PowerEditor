/*
 * Created on 2004. 8. 24.
 *
 */
package com.mindbox.pe.client.applet.template.guideline;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.client.common.IDNameObjectComboBox;
import com.mindbox.pe.client.common.UsageTypeComboBox;
import com.mindbox.pe.client.common.dialog.DialogFactory;
import com.mindbox.pe.client.common.event.Action3Adapter;
import com.mindbox.pe.common.TemplateUtil;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.exceptions.CanceledException;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.template.GridTemplate;


/**
 * @author Geneho Kim
 * @since PowerEditor 
 */
public class TemplateWizardDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public static GridTemplate createTemplate(TemplateUsageType usage) throws ServerException {
		if (usage == null) {
			ClientUtil.getInstance().showWarning("msg.warning.no.guideline.type.selected"/*, new Object[] { field}*/);
			return null;
		}
		else {
			List<ActionTypeDefinition> typeList = ClientUtil.getActionTypes(usage);
			if (typeList.size() == 0) {
				ClientUtil.getInstance().showWarning("msg.warning.no.actions.for.usage.type");
				return null;
			}
			TemplateWizardDialog dialog = new TemplateWizardDialog(usage);

			dialog.setTitle(ClientUtil.getInstance().getLabel("d.title.wizard.new.template"));
			dialog.setModal(true);
			dialog.setVisible(true);

			return dialog.template;
		}
	}

	private class FinishL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			doFinish();
		}
	}

	private class NextL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			doNext();
		}
	}

	private class BackL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			--currentStep;
			showCorrectPanel();
		}
	}

	private class CancelL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			template = null;
			dispose();
		}
	}

	private class AttributeButtonL extends Action3Adapter {

		public void deletePerformed(ActionEvent e) {
			if (attributeList.getSelectedIndex() >= 0) {
				removeFromAttributeList((String) attributeList.getSelectedValue());
			}
		}

		public void newPerformed(ActionEvent e) {
			String attrStr = DialogFactory.showAttributeSelector(null);
			if (attrStr != null && attrStr.length() > 0) {
				addToAttributeList(attrStr);
			}

		}
	}

	private class UsageComboL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (usageTypeField.getSelectedUsage() != null) {
				try {
					resetActionCombo(usageTypeField.getSelectedUsage());
				}
				catch (ServerException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private class ActionComboL implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			resetDescField();
		}
	}

	private GridTemplate template = null;
	private final JButton finishButton, backButton, nextButton, cancelButton;
	private final JTextField nameField;
	private final UsageTypeComboBox usageTypeField;
	//private final ActivationLabelComboBox labelCombo;
	private final JList attributeList;
	private final ButtonPanel attributeButtonPanel;
	private final IDNameObjectComboBox actionCombo;
	private final JTextField actionDescField = new JTextField(10);

	private final CardLayout card;
	private final JPanel detailPanel;

	private int currentStep = 1;

	private TemplateWizardDialog(TemplateUsageType usage) throws ServerException {
		super(JOptionPane.getFrameForComponent(ClientUtil.getApplet()));
		finishButton = UIFactory.createButton("Finish", null, new FinishL(), null);
		backButton = UIFactory.createButton("Back", null, new BackL(), null);
		nextButton = UIFactory.createButton("Next", null, new NextL(), null);
		cancelButton = UIFactory.createButton("Cancel", null, new CancelL(), null);

		nameField = new JTextField();
		usageTypeField = UsageTypeComboBox.createInstance();
		usageTypeField.setEditable(false);

		if (usage != null) {
			usageTypeField.selectUsage(usage);
		}
		else {
			usageTypeField.setSelectedIndex(0);
		}

		//labelCombo = ActivationLabelComboBox.createInstance();
		//labelCombo.setEditable(false);

		actionCombo = new IDNameObjectComboBox(false, "image.node.adhoc.action");
		resetActionCombo(usage);

		card = new CardLayout();
		detailPanel = UIFactory.createJPanel(card);

		attributeList = new JList(new DefaultListModel());
		attributeButtonPanel = UIFactory.create3ButtonPanel(new AttributeButtonL(), false);

		initDialog();

		finishButton.setEnabled(true);
		backButton.setEnabled(false);

		setSize(480, 400);
		UIFactory.centerize(this);

		card.show(detailPanel, "1");

		actionCombo.addActionListener(new ActionComboL());
		usageTypeField.addActionListener(new UsageComboL());
		nameField.requestFocus();
	}

	private void resetActionCombo(TemplateUsageType usage) throws ServerException {
		actionCombo.removeAllItems();
		List<ActionTypeDefinition> typeList = ClientUtil.getActionTypes(usage);
		for (Iterator<ActionTypeDefinition> iter = typeList.iterator(); iter.hasNext();) {
			ActionTypeDefinition element = iter.next();
			actionCombo.addItem(element);
		}
		resetDescField();
	}

	private void addToAttributeList(String str) {
		((DefaultListModel) attributeList.getModel()).addElement(str);
	}

	private void removeFromAttributeList(String str) {
		((DefaultListModel) attributeList.getModel()).removeElement(str);
	}

	private void initDialog() {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(2, 2, 2, 2);
		c.weighty = 0.0;
		c.gridheight = 1;

		// panel one - template name
		GridBagLayout bag = new GridBagLayout();
		JPanel panel = UIFactory.createJPanel(bag);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		UIFactory.addComponent(panel, bag, c, new JLabel("Step 1: Enter name and select activation label"));

		c.gridwidth = 1;
		c.weightx = 0.0;
		UIFactory.addComponent(panel, bag, c, UIFactory.createFormLabel("label.usage.type"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		UIFactory.addComponent(panel, bag, c, usageTypeField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		UIFactory.addComponent(panel, bag, c, UIFactory.createFormLabel("label.name"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		UIFactory.addComponent(panel, bag, c, nameField);

		/*
		 c.gridwidth = 1;
		 c.weightx = 0.0;
		 UIFactory.addComponent(panel, bag, c, UIFactory.createFormLabel("label.activation.label"));

		 c.gridwidth = GridBagConstraints.REMAINDER;
		 c.weightx = 1.0;
		 UIFactory.addComponent(panel, bag, c, labelCombo);
		 */

		detailPanel.add(panel, "1");

		// panel two - attribute selection
		bag = new GridBagLayout();
		panel = UIFactory.createJPanel(bag);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		UIFactory.addComponent(panel, bag, c, new JLabel("Step 2: Choose Attributes For Template " + nameField.getText()));

		UIFactory.addComponent(panel, bag, c, attributeButtonPanel);

		c.weighty = 1.0;
		UIFactory.addComponent(panel, bag, c, new JScrollPane(attributeList));

		detailPanel.add(panel, "2");

		c.weighty = 0.0;

		// panel three - action selection
		c.weighty = 0.0;
		bag = new GridBagLayout();
		panel = UIFactory.createJPanel(bag);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		UIFactory.addComponent(panel, bag, c, new JLabel("Step 3: Select Action For Template " + nameField.getText()));

		c.gridwidth = 1;
		c.weightx = 0.0;
		UIFactory.addComponent(panel, bag, c, UIFactory.createFormLabel("label.action"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		UIFactory.addComponent(panel, bag, c, actionCombo);

		c.gridwidth = 1;
		c.weightx = 0.0;
		UIFactory.addComponent(panel, bag, c, UIFactory.createFormLabel("label.desc"));

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		UIFactory.addComponent(panel, bag, c, actionDescField);

		detailPanel.add(panel, "3");

		// layout
		JPanel bp = UIFactory.createFlowLayoutPanelCenterAlignment(4, 4);
		bp.add(backButton);
		bp.add(nextButton);
		bp.add(new JSeparator());
		bp.add(finishButton);
		bp.add(cancelButton);

		bag = new GridBagLayout();
		this.getContentPane().setLayout(bag);

		JLabel label = UIFactory.createLabel("label.create.template");
		label.setFont(PowerEditorSwingTheme.tabFont);
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		UIFactory.addComponent(getContentPane(), bag, c, label);

		UIFactory.addComponent(getContentPane(), bag, c, new JSeparator());


		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.gridheight = 1;
		c.weighty = 1.0;
		UIFactory.addComponent(getContentPane(), bag, c, detailPanel);

		UIFactory.addComponent(getContentPane(), bag, c, Box.createVerticalGlue());

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.weighty = 0.0;
		UIFactory.addComponent(getContentPane(), bag, c, new JSeparator());

		UIFactory.addComponent(getContentPane(), bag, c, bp);
	}

	private void resetDescField() {
		if (actionCombo.getSelectedIndex() >= 0) {
			actionDescField.setText(((ActionTypeDefinition) actionCombo.getSelectedItem()).getDescription());
		}
		else {
			actionDescField.setText("");
		}
	}

	private void doNext() {
		try {
			String field = updateFromFields(false);
			if (field != null) {
				ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { field });
			}
			else {
				++currentStep;
				showCorrectPanel();
			}
		}
		catch (CanceledException ex) {

		}
	}

	private void doFinish() {
		try {
			String field = updateFromFields(true);
			if (field != null) {
				ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { field });
			}
			else {
				dispose();
			}
		}
		catch (CanceledException ex) {

		}
	}

	private void showCorrectPanel() {
		card.show(detailPanel, String.valueOf(currentStep));
		backButton.setEnabled(currentStep > 1);
		nextButton.setEnabled(currentStep < 3);
	}

	private String updateFromFields(boolean fromFinish) throws CanceledException {
		if (template == null) {
			template = new GridTemplate();
			template.setID(-1);
			template.setFitToScreen(true);
			template.setMaxNumOfRows(99);
			template.setStatus(GridTemplate.DEFAULT_STATUS_DRAFT);
		}

		if (currentStep == 1) {
			if (usageTypeField.getSelectedIndex() < 0) {
				return "usage type";
			}
			if (UIFactory.isEmpty(nameField)) {
				return "name";
			}
			// verify name is unique
			try {
				if (!ClientUtil.getCommunicator().checkNameForUniqueness(PeDataType.TEMPLATE, nameField.getText().trim())) {
					ClientUtil.getInstance().showWarning("msg.warning.not.unique", new Object[] { "The specified template name" });
					throw CanceledException.getInstance();
				}
			}
			catch (ServerException ex) {
				ClientUtil.handleRuntimeException(ex);
				throw CanceledException.getInstance();
			}
			template.setName(nameField.getText());
			template.setDescription(template.getName() + " template");
			template.setUsageType(usageTypeField.getSelectedUsage());
			for (int i = 1; i <= template.getNumColumns(); i++)
				template.removeTemplateColumn(i);
		}
		else if (currentStep == 2) {
			template.removeAllTemplateColumns();

			for (int i = 0; i < attributeList.getModel().getSize(); i++) {
				String reference = (String) attributeList.getModel().getElementAt(i);
				template.addGridTemplateColumn(TemplateUtil.generateColumnsFromAttribute(DomainModel.getInstance(), template.getUsageType(), reference, (i + 1)));
			}
			if (fromFinish) {//user hit finish in step 2, hence generate LHS of rule based on existing template columns
				TemplateUtil.generateAndSetLHSRuleDefinition(template);
			}
		}
		else if (currentStep == 3) {
			if (actionCombo.getSelectedIndex() < 0) {
				return "action";
			}

			TemplateUtil.generateAndAddColumns(template, (ActionTypeDefinition) actionCombo.getSelectedIDNameObject());
			TemplateUtil.generateAndSetRuleDefinition(template, (ActionTypeDefinition) actionCombo.getSelectedIDNameObject());
		}
		return null;
	}
}
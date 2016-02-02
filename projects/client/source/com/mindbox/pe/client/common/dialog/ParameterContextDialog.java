package com.mindbox.pe.client.common.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.validate.Validator;
import com.mindbox.pe.client.common.DateSelectorComboField;
import com.mindbox.pe.client.common.TypeEnumValueComboBox;
import com.mindbox.pe.client.common.context.GuidelineContextPanel;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.model.ParameterGrid;

/**
 * Parameter context edit dialog.
 * 
 * @author Geneho Kim
 * @author MindBox
 */
public class ParameterContextDialog extends JPanel {

	/**
	 * @param paramContext
	 * @return Object[2]{ParameterGrid,Boolean} - Boolean is set to true if expiration of the source
	 *         is to be set
	 */
	public static Object[] cloneParameterGrid(ParameterGrid originalGrid) {
        ParameterGrid newGrid = new ParameterGrid(originalGrid, originalGrid.getEffectiveDate(),
                originalGrid.getExpirationDate());
        newGrid.copyEntireContext(originalGrid);
		ParameterContextDialog dialog = new ParameterContextDialog(newGrid, true);
        dialog.statusField.setSelectedIndex(0);
        int option = -1;
        do {
            option = JOptionPane.showConfirmDialog(
                    ClientUtil.getApplet(),
                    dialog,
                    "Copy Parameter",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            
        } while (option == JOptionPane.OK_OPTION && !validate(dialog));
        
		if (option == JOptionPane.OK_OPTION) {
			if (!dialog.updateFromFields()) {
				return new Object[] { null, null };
			}
			else {
				return new Object[] { dialog.paramContext, new Boolean(dialog.adjustCheckbox.isSelected()) };
			}
		}
		else {
			return new Object[] { null, null };
		}
	}

	public static ParameterGrid newParameterGrid() {
		ParameterContextDialog dialog = new ParameterContextDialog(null, true);
        int option = -1;
        
        do {
            option = JOptionPane.showConfirmDialog(
                    ClientUtil.getApplet(),
                    dialog,
                    "New Parameter",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            
        } while (option == JOptionPane.OK_OPTION && !validate(dialog));
            

		if (option == JOptionPane.OK_OPTION) {
			if (!dialog.updateFromFields()) {
				return null;
			}
		}
		return dialog.paramContext;
	}

	private static boolean validate(ParameterContextDialog dialog) {
        if (dialog.actDateField.getDate() == null) {
            ClientUtil.getInstance().showWarning("msg.warning.no.activation.date", 
                    new Object[] { ClientUtil.getHighestStatusDisplayLabel() });
            return false;
        }else if (ClientUtil.isHighestStatus((String)dialog.statusField.getSelectedEnumValueValue()) &&
                (dialog.actDateField == null || dialog.actDateField.getDate().before(new Date()))) {
            ClientUtil.getInstance().showWarning("msg.error.statuschange.activate", 
                    new Object[] { ClientUtil.getHighestStatusDisplayLabel() });
            return false;
        } else {
            return true;
        }
    }

    /**
	 * @param paramContext
	 * @author Inna Nill
	 * @author MindBox, LLC
	 * @since PowerEditor 4.2
	 */
	public static void editParameterGrid(ParameterGrid paramContext) {
		ParameterContextDialog dialog = new ParameterContextDialog(paramContext, true);
        int option = -1;        
        do {
            option = JOptionPane.showConfirmDialog(
                    ClientUtil.getApplet(),
                    dialog,
                    "New Parameter",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            
        } while (option == JOptionPane.OK_OPTION && !validate(dialog));
		if (option == JOptionPane.OK_OPTION) {
			dialog.updateFromFields();
		}
	}

	private ParameterGrid paramContext;
	private boolean allowEdit = false;
	private final DateSelectorComboField actDateField, expDateField;
	private final TypeEnumValueComboBox statusField;
	private final JCheckBox adjustCheckbox;
	private final GuidelineContextPanel contextPanel;

	private ParameterContextDialog(ParameterGrid paramGrid, boolean allowEdit) {
		this.contextPanel = new GuidelineContextPanel("button.edit.context", true, true);
		this.paramContext = paramGrid;
		this.allowEdit = allowEdit;
		this.actDateField = new DateSelectorComboField(true, true, true);
		this.expDateField = new DateSelectorComboField(true, true, true);

		this.adjustCheckbox = UIFactory.createCheckBox("checkbox.adjust.expiration");

		this.statusField = UIFactory.createStatusComboBox(false);

		initDialog();

		if (paramContext != null) {
			contextPanel.setContextElemens(paramContext.extractGuidelineContext());
			if (!allowEdit)
				contextPanel.setEditContextEnabled(false);

			actDateField.setValue(paramContext.getEffectiveDate());
			expDateField.setValue(paramContext.getExpirationDate());

			if (paramContext.getStatus() != null) {
				statusField.selectTypeEnumValue(paramContext.getStatus());
			}
		}
		else {
			actDateField.setValue(null);
			expDateField.setValue(null);
		}
        statusField.addActionListener(new StatusComboL());
	}

	private void initDialog() {

		GridBagLayout bag = new GridBagLayout();
		this.setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, contextPanel.getJPanel());
		
		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(this, bag, c, new JLabel(ClientUtil.getInstance().getLabel("label.date.activation")));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, actDateField);

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(this, bag, c, new JLabel(ClientUtil.getInstance().getLabel("label.date.expiration")));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, expDateField);

		c.weightx = 0.0;
		c.gridwidth = 1;
		UIFactory.addComponent(this, bag, c, UIFactory.createFormLabel("label.status"));

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		UIFactory.addComponent(this, bag, c, statusField);

		if (paramContext != null && allowEdit == false) {
			UIFactory.addComponent(this, bag, c, adjustCheckbox);
			adjustCheckbox.setSelected(false);
		}
	}

	private boolean updateFromFields() {
		if (paramContext == null) {
			paramContext = new ParameterGrid(-1, -1, null, null);
		}

		String status = statusField.getSelectedEnumValueValue();
		String errMessageKey = Validator.validateActivationDateRange(actDateField.getValue(), expDateField.getValue(), status);
		if (errMessageKey != null) {
			ClientUtil.getInstance().showErrorDialog(errMessageKey, 
                    new Object[] { ClientUtil.getStatusDisplayLabel(status)});
			return false;
		}

		paramContext.setEffectiveDate(actDateField.getValue());
		paramContext.setExpirationDate(expDateField.getValue());
		paramContext.setStatus(status);
		ClientUtil.setContext(paramContext, contextPanel.getGuidelineContexts());
		return true;
	}
    
    private class StatusComboL implements ActionListener {
        public void actionPerformed(ActionEvent event) {
           String status = statusField.getSelectedEnumValueValue();
           if (!ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_EDIT_PRODUCTION_DATA) &&
                   ClientUtil.isHighestStatus(status)) {
               if (actDateField.getValue() == null ||
                       actDateField.getValue().getDate().before(new Date())) {
                   ClientUtil.getInstance().showErrorDialog("msg.error.statuschange.activate", new Object[] { ClientUtil.getHighestStatusDisplayLabel() } );
                   statusField.setSelectedIndex(0);
               } else if (!ClientUtil.getInstance().showConfirmation("msg.confirm.statuschange.parameter.hightest", 
                    new Object[] { ClientUtil.getHighestStatusDisplayLabel() } )) {
                   statusField.setSelectedIndex(0);
               }
           }
        }
    }
    

}
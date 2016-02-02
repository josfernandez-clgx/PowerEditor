package com.mindbox.pe.client.applet.guidelines.manage;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.validate.Validator;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.DateSelectorComboField;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.ProductGrid;

/**
 * Dialog for editing guideline activation dates.
 * 
 * @author Gene Kim
 * @author MindBox
 * @since PowerEditor 4.2.0
 */
public class ActivationEditorDialog extends JPanel {

	private final class ShowDateNameL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent e) {
			effDateField.refresh(dateNameCheckbox.isSelected());
			expDateField.refresh(dateNameCheckbox.isSelected());
		}
	}

	private DateSelectorComboField effDateField;
	private DateSelectorComboField expDateField;
	private JCheckBox autoAdjustCheckBox;
	private JCheckBox dateNameCheckbox;
	private Object object = null;

	public ActivationEditorDialog(Object obj) {
		this.object = obj;
		init();
	}

	ActivationEditorDialog(AbstractGridPanel gridPanel) {
		this.object = gridPanel;
		init();
	}

	private void init() {
		dateNameCheckbox = UIFactory.createCheckBox("checkbox.show.date.name");
		dateNameCheckbox.setSelected(true);
		dateNameCheckbox.addActionListener(new ShowDateNameL());

		effDateField = new DateSelectorComboField(true, true, true);
		expDateField = new DateSelectorComboField(true, true, true);

		autoAdjustCheckBox = UIFactory.createCheckBox("checkbox.adjust.auto.others");
		autoAdjustCheckBox.setForeground(PowerEditorSwingTheme.primary1);

		setLayout(new BorderLayout(5, 5));
		setBorder(BorderFactory.createEtchedBorder());

		JLabel label = null;

		GridBagLayout gridBag = new GridBagLayout();
		JPanel cPanel = new JPanel(gridBag);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(4, 4, 4, 4);
		c.weighty = 0.0;
		c.gridheight = 1;

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		UIFactory.addComponent(cPanel, gridBag, c, dateNameCheckbox);

		c.gridwidth = 1;
		c.weightx = 0.0;
		label = UIFactory.createFormLabel("label.date.activation");
		UIFactory.addComponent(cPanel, gridBag, c, label);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		UIFactory.addComponent(cPanel, gridBag, c, effDateField);

		c.gridwidth = 1;
		c.weightx = 0.0;
		label = UIFactory.createFormLabel("label.date.expiration");
		UIFactory.addComponent(cPanel, gridBag, c, label);

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		UIFactory.addComponent(cPanel, gridBag, c, expDateField);

		add(cPanel, BorderLayout.CENTER);
		add(autoAdjustCheckBox, BorderLayout.SOUTH);
	}

	public Insets getInsets() {
		return new Insets(5, 5, 5, 5);
	}

	public boolean edit(ProductGrid grid, Operation op) {
		boolean flag = false;
		if (grid != null) {
			effDateField.setValue(grid.getEffectiveDate());
			expDateField.setValue(grid.getExpirationDate());
			if (!op.equals(ActivationEditorDialog.Operation.COPY)
					&& !ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_EDIT_PRODUCTION_DATA)
					&& ClientUtil.isHighestStatus(grid.getStatus())) {
				effDateField.setEnabled(false);
				// if expiration date before now disable it
				if (grid.getExpirationDate() != null && grid.getExpirationDate().getDate().before(new Date())) {
					effDateField.setEnabled(false);
				}
			}
		}
		autoAdjustCheckBox.setVisible(op != Operation.EDIT);
		autoAdjustCheckBox.setSelected(op == Operation.COPY);

		int option = -1;
		do {
			option = JOptionPane.showConfirmDialog(ClientUtil.getApplet(), this, op.getLabel(), 2);

		} while (option == JOptionPane.OK_OPTION && !validate(this, grid, op));
		if (option == JOptionPane.OK_OPTION) {
			flag = true;
			try {
				DateSynonym dsEff = null;
				DateSynonym dsExp = null;
				dsEff = effDateField.getValue();
				dsExp = expDateField.getValue();

				// save date synonym's if there are new
				saveDateSynonymIfNecessary(dsEff);
				saveDateSynonymIfNecessary(dsExp);

				grid.setEffectiveDate(dsEff);
				grid.setExpirationDate(dsExp);
				if (autoAdjustCheckBox.isSelected()) {
					((AbstractGridPanel) this.object).adjustActivationDates(grid);
				}
			}
			catch (Exception exception) {
				exception.printStackTrace();
				ClientUtil.getInstance().showErrorDialog("msg.error.generic.service", new Object[] { exception.getMessage() });
				flag = false;
			}
		}
		return flag;
	}

	private boolean validate(ActivationEditorDialog dialog, ProductGrid grid, Operation op) {
		String status = ((AbstractGridPanel) this.object).getSelectedStatus();
		String errorMessageKey = Validator.validateActivationDateRange(effDateField.getValue(), expDateField.getValue(), status);

		List<ProductGrid> gridList = ((AbstractGridPanel) this.object).gridList;
		if (errorMessageKey == null) {
			errorMessageKey = Validator.validateDuplicateDates(
					grid,
					gridList,
					effDateField.getValue(),
					expDateField.getValue(),
					op != Operation.COPY); // not copy flag
		}
		if (errorMessageKey != null) {
			ClientUtil.getInstance().showErrorDialog(errorMessageKey, new Object[] { ClientUtil.getStatusDisplayLabel(status) });
			return false;
		}
		// do this validation AFTER checking for duplicate dates and valid date range
		if (ClientUtil.getUserSession().getUIPolicies() != null
				&& ClientUtil.getUserSession().getUIPolicies().isSequentialActivationDatesEnfored()) {
			errorMessageKey = Validator.validateGapsAndOverlapsInDates(
					ClientUtil.getUserSession().getUIPolicies().isGapsInActivationDatesAllowed(),
					grid,
					gridList,
					effDateField.getValue(),
					expDateField.getValue(),
					op == Operation.EDIT,
					autoAdjustCheckBox.isSelected()); // edit flag
			if (errorMessageKey != null) {
				ClientUtil.getInstance().showErrorDialog(errorMessageKey, new Object[] { ClientUtil.getStatusDisplayLabel(status) });
				return false;
			}
		}

		// if a production guidelne is relevant to to the auto adjust logic
		// prompt for continue
		if (autoAdjustCheckBox.isSelected()
				&& hasRelevantProductionRestrictionToAutoAdjust(
						((AbstractGridPanel) this.object).gridList,
						effDateField.getValue(),
						expDateField.getValue())
				&& !ClientUtil.getInstance().showConfirmation(
						"msg.confirm.autoadjust.production",
						new Object[] { ClientUtil.getHighestStatusDisplayLabel() })) {
			return false;
		}
		return true;
	}

	private boolean hasRelevantProductionRestrictionToAutoAdjust(List<ProductGrid> list, DateSynonym dsEff, DateSynonym dsExp) {
		// if eff date is in future no worries
		if (dsEff != null && dsEff.getDate() != null && dsEff.getDate().after(new Date())) {
			return false;
		}
		for (Iterator<ProductGrid> i = list.iterator(); i.hasNext();) {
			ProductGrid grid = (ProductGrid) i.next();
			if (hasProductionRestriction(grid.getStatus())) {
				// target starts before new one and expires after new one starts
				if (grid.getEffectiveDate() == null || grid.getEffectiveDate().before(dsEff)) {
					if (grid.getExpirationDate() == null || grid.getExpirationDate().after(dsEff)) {
						return true;
					}
				} // target starts after new one
				else if (dsExp != null && dsExp.getDate() != null) {
					if (grid.getEffectiveDate().before(dsExp)) {
						grid.setEffectiveDate(dsExp);
						return true;
					}
					if (grid.getExpirationDate() != null && grid.getExpirationDate().before(dsExp)) {
						grid.setExpirationDate(dsExp);
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean edit(List<GuidelineReportData> guidelineGridDataList) {
		boolean flag = false;

		if (hasProductionRestrictionOnActivationSelected(guidelineGridDataList)) {
			effDateField.setEnabled(false);
		}

		if (JOptionPane.showConfirmDialog(ClientUtil.getApplet(), this, ClientUtil.getInstance().getLabel("d.title.edit.activation"), 2) == 0) {
			flag = true;
			DateSynonym dsEff = null;
			DateSynonym dsExp = null;
			try {
				dsEff = effDateField.getValue();
				dsExp = expDateField.getValue();

				if (dsEff != null && dsExp != null && dsEff.after(dsExp)) {
					ClientUtil.getInstance().showErrorDialog("InvalidActivationDateRangeMsg");
					return false;
				}
				for (Iterator<GuidelineReportData> i = guidelineGridDataList.iterator(); i.hasNext();) {
					GuidelineReportData data = i.next();
					if (dsEff != null && data.getExpirationDate() != null && dsEff.after(data.getExpirationDate())) {
						ClientUtil.getInstance().showErrorDialog("msg.warning.invalid.range.activation");
						return false;
					}
					if (dsExp != null && data.getActivationDate() != null && dsExp.before(data.getActivationDate())) {
						ClientUtil.getInstance().showErrorDialog("msg.warning.invalid.range.expiration");
						return false;
					}
					if (hasProductionRestriction(data.getStatus())) {
						if (!UtilBase.isSame(dsExp, data.getExpirationDate())) {
							if (dsExp.getDate() != null && dsExp.getDate().before(new Date())) {
								ClientUtil.getInstance().showErrorDialog(
										"msg.error.cannot.change.expdate.past",
										new Object[] { ClientUtil.getHighestStatusDisplayLabel() });
								return false;
							}
							else if (data.getExpirationDate() != null && data.getExpirationDate().getDate().before(new Date())) {
								ClientUtil.getInstance().showErrorDialog(
										"msg.error.cannot.change.expdate.expired",
										new Object[] { ClientUtil.getHighestStatusDisplayLabel() });
								return false;
							}
						}
					}
				}

				ClientUtil.getCommunicator().bulkSaveGridData(guidelineGridDataList, null, dsEff, dsExp);

				// save date synonym's if there are new
				saveDateSynonymIfNecessary(dsEff);
				saveDateSynonymIfNecessary(dsExp);
			}
			catch (ServerException se) {
				// The code never reaches this part at this point. This is here in case that changes in
				// the future.
				se.printStackTrace();
				String dateStr = dsEff.toString() + (dsExp == null ? "" : "-" + dsExp.toString());
				ClientUtil.getInstance().showErrorDialog("msg.error.failure.validate", new Object[] { dateStr, se.getMessage() });
				flag = false;
			}
			catch (Exception exception) {
				exception.printStackTrace();
				ClientUtil.getInstance().showErrorDialog("msg.error.generic.service", new Object[] { exception.getMessage() });
				flag = false;
			}
		}
		return flag;
	}

	private boolean hasProductionRestrictionOnActivationSelected(List<GuidelineReportData> list) {
		for (GuidelineReportData data : list) {
			if (hasProductionRestriction(data.getStatus())) {
				return true;
			}
		}
		return false;
	}

	private void saveDateSynonymIfNecessary(DateSynonym ds) throws ServerException {
		if (ds != null && ds.getID() < 1) {
			int newID = ClientUtil.getCommunicator().save(ds, false);
			ds.setID(newID);
		}
	}

	public static class Operation {
		public static final Operation ADD = new Operation("d.title.new.activation");
		public static final Operation EDIT = new Operation("d.title.edit.activation");
		public static final Operation COPY = new Operation("d.title.copy.activation");

		private String label;

		private Operation(String labelKey) {
			this.label = ClientUtil.getInstance().getLabel(labelKey);
		}

		private String getLabel() {
			return label;
		}
	}


	private boolean hasProductionRestriction(String status) {
		return (!ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_EDIT_PRODUCTION_DATA) && ClientUtil.isHighestStatus(status));

	}
}
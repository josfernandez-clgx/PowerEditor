package com.mindbox.pe.client.common.dialog;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.common.validate.ValidationViolation;
import com.mindbox.pe.communication.ValidationException;


/**
 * Displays import results in a resizable dialog.
 * @author Geneho Kim
 * @since PowerEditor 4.4.0
 */
public class ValidationErrorReportDialog extends JPanel {

	public static void showErrors(ValidationException validationException, boolean showInvalidData) {
		ClientUtil.getInstance().showAsDialog(
				"d.title.validation.error",
				true,
				new ValidationErrorReportDialog(validationException.getViolations(), showInvalidData),
				true);
	}

	private class ViewErrorL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent event) throws Exception {
			int row = errorTable.getSelectedRow();
			if (row > -1) {
				String data = (String) errorTable.getModel().getValueAt(row, 0);
				ClientUtil.getInstance().showText("d.title.import.error", data);
			}
		}
	}

	private class CopyErrorsL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent event) throws Exception {
			StringBuffer buff = new StringBuffer();
			int rows = errorTable.getModel().getRowCount();
			for (int row = 0; row < rows; row++) {
				buff.append(errorTable.getModel().getValueAt(row, 0));
				buff.append(System.getProperty("line.separator"));
			}
			ClientUtil.placeOnClipboard(buff.toString());
		}
	}

	private final JButton viewErrorButton, copyErrorButton;
	private final JTable errorTable;

	private ValidationErrorReportDialog(List<ValidationViolation> violations, boolean showInvalidData) {
		this.viewErrorButton = UIFactory.createJButton("button.view.error", null, new ViewErrorL(), null);
		this.copyErrorButton = UIFactory.createJButton("button.copy.errors", null, new CopyErrorsL(), null);

		viewErrorButton.setEnabled(false);

		GridBagLayout bag = new GridBagLayout();
		setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;

		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;

		JPanel bp = UIFactory.createFlowLayoutPanelLeftAlignment(2, 2);
		PanelBase.addComponent(this, bag, c, bp);

		bp = UIFactory.createFlowLayoutPanelLeftAlignment(2, 2);
		bp.add(viewErrorButton);
		bp.add(copyErrorButton);
		PanelBase.addComponent(this, bag, c, bp);

		ValidationErrorTableModel errorTableModel = new ValidationErrorTableModel(showInvalidData);
		errorTableModel.setData(violations);
		
		errorTable = new JTable(errorTableModel);
		errorTable.setColumnSelectionAllowed(false);
		errorTable.setForeground(Color.red);
		errorTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		errorTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent arg0) {
				viewErrorButton.setEnabled(errorTable.getSelectedRow() >= 0);
			}
		});
		c.weighty = 0.5;
		PanelBase.addComponent(this, bag, c, new JScrollPane(errorTable));
	}
}

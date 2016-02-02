package com.mindbox.pe.client.applet.admin.imexport;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.communication.ImportResult;


/**
 * Displays import results in a resizable dialog.
 * @author Geneho Kim
 * @since PowerEditor 4.4.0
 */
public class ImportResultDialog extends JPanel {

	public static Object[][] asSingleColumn2DArray(Object[] values) {
		Object[][] result = new Object[values.length][1];
		for (int i = 0; i < values.length; i++) {
			result[i][0] = values[i];
		}
		return result;
	}

	public static void showResult(ImportResult result) {
		ClientUtil.getInstance().showAsDialog("d.title.import.results", true, new ImportResultDialog(result), true);
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

	private class ViewMessageL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent event) throws Exception {
			int row = messageTable.getSelectedRow();
			if (row > -1) {
				String data = (String) messageTable.getModel().getValueAt(row, 0);
				ClientUtil.getInstance().showText("d.title.import.message", data);
			}
		}
	}

	private class CopyMessagesL extends AbstractThreadedActionAdapter {

		public void performAction(ActionEvent event) throws Exception {
			StringBuffer buff = new StringBuffer();
			int rows = messageTable.getModel().getRowCount();
			for (int row = 0; row < rows; row++) {
				buff.append(messageTable.getModel().getValueAt(row, 0));
				buff.append(System.getProperty("line.separator"));
			}
			ClientUtil.placeOnClipboard(buff.toString());
		}
	}

	private final JButton viewErrorButton, copyErrorButton;
	private final JButton viewMessageButton, copyMessageButton;
	private final JTable messageTable, errorTable;

	private ImportResultDialog(ImportResult result) {
		this.viewErrorButton = UIFactory.createJButton("button.view.error", null, new ViewErrorL(), null);
		this.copyErrorButton = UIFactory.createJButton("button.copy.errors", null, new CopyErrorsL(), null);
		this.viewMessageButton = UIFactory.createJButton("button.view.message", null, new ViewMessageL(), null);
		this.copyMessageButton = UIFactory.createJButton("button.copy.messages", null, new CopyMessagesL(), null);

		viewErrorButton.setEnabled(false);
		viewMessageButton.setEnabled(false);

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
		bp.add(viewMessageButton);
		bp.add(copyMessageButton);
		PanelBase.addComponent(this, bag, c, bp);

		ImportErrorTableModel messageTableModel = new ImportErrorTableModel();
		messageTableModel.setData(result.getMessages());
		messageTable = new JTable(messageTableModel);
		messageTable.setColumnSelectionAllowed(false);
		messageTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		messageTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			public void valueChanged(ListSelectionEvent arg0) {
				viewMessageButton.setEnabled(messageTable.getSelectedRow() >= 0);
			}
		});
		c.weighty = 0.5;
		PanelBase.addComponent(this, bag, c, new JScrollPane(messageTable));

		if (result.hasError()) {
			c.weighty = 0.0;
			PanelBase.addComponent(this, bag, c, new JSeparator());

			bp = UIFactory.createFlowLayoutPanelLeftAlignment(2, 2);
			bp.add(viewErrorButton);
			bp.add(copyErrorButton);
			PanelBase.addComponent(this, bag, c, bp);

			JLabel errorLabel = UIFactory.createLabel("label.import.errors.warnings");
			errorLabel.setFont(PowerEditorSwingTheme.boldFont);
			errorLabel.setForeground(Color.red);
			PanelBase.addComponent(this, bag, c, errorLabel);

			ImportErrorTableModel errorTableModel = new ImportErrorTableModel();
			errorTableModel.setData(result.getErrorMessages());
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
		else {
			errorTable = null;
			PanelBase.addComponent(this, bag, c, UIFactory.createLabel("label.no.errors"));
		}

		c.weighty = 0.0;
		PanelBase.addComponent(this, bag, c, new JSeparator());
		PanelBase.addComponent(this, bag, c, UIFactory.createLabel("label.import.time", new Object[] { new Long((long) Math.ceil(result
				.getElapsedTime() / 1000.0)) }));
	}
}

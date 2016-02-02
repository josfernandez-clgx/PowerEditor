/*
 * Created on 2004. 12. 7.
 *
 */
package com.mindbox.pe.client.applet.datesynonym;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.AbstractThreadedActionAdapter;
import com.mindbox.pe.client.common.PanelBase;
import com.mindbox.pe.client.common.dialog.DateSynonymEditDialog;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.filter.AllNamedDateSynonymFilter;


/**
 * @author Geneho Kim
 * @since PowerEditor
 */
public class ManageDateSynonymTab extends PanelBase implements ChangeListener {

	private static boolean initialized = false;

	public static void reset() {
		initialized = false;
	}


	private final JButton addButton;
	private final JButton copyButton;
	private final JButton editButton;
	private final JButton deleteButton;
	private final JButton mergeButton;
	private final JButton refreshButton;
	private final DateSynonymTable table;
	private boolean readOnly;

	public ManageDateSynonymTab(boolean readOnly) {
		this.readOnly = readOnly;

		table = new DateSynonymTable(new DateSynonymTableModel());

		addButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.add"), null, new AddL(), null);
		copyButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.copy"), null, new CopyL(), null);
		editButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.edit"), null, new EditL(), null);
		mergeButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.merge"), null, new MergeL(), null);
		deleteButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.delete"), null, new DeleteL(), null);
		refreshButton = UIFactory.createButton(ClientUtil.getInstance().getLabel("button.reload"), null, new RefreshL(), null);

		initPanel();

		if (!readOnly) {
			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if ((e.getClickCount() == 2) && editButton.isEnabled()) {
						editSelectedSynonym();
					}
				}
			});
		}

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return; // ignore duplicate change events
				}

				ListSelectionModel selection = (ListSelectionModel) e.getSource();
				enableButtons(selection.getMaxSelectionIndex() - selection.getMinSelectionIndex() + 1);
			}
		});
	}

	private void editSelectedSynonym() {
		if (!readOnly) {
			DateSynonym[] userSelection = getSelectedDateSynonyms();
			if (userSelection.length > 0) {
				DateSynonym dateSynonym = userSelection[0];
				int synonymID = dateSynonym.getID();
				try {
					ClientUtil.getCommunicator().lock(synonymID, EntityType.DATE_SYNONYM);
				}
				catch (ServerException ex) {
					ClientUtil.getInstance().showErrorDialog(
							"msg.error.failure.lock",
							new Object[] { dateSynonym.getName(), ClientUtil.getInstance().getErrorMessage(ex) });
					return;
				}
				try {
					dateSynonym = DateSynonymEditDialog.editDateSynonym(
							JOptionPane.getFrameForComponent(ClientUtil.getApplet()),
							dateSynonym);
					if (dateSynonym != null) {
						int newID = ClientUtil.getCommunicator().save(dateSynonym, false);
						dateSynonym.setID(newID);
						EntityModelCacheFactory.getInstance().removeDateSynonym(dateSynonym);
						EntityModelCacheFactory.getInstance().addDateSynonym(dateSynonym);

						// TT 2029 update cache association date
						EntityModelCacheFactory.getInstance().updateCategoryAssociationDateSynonyms(dateSynonym);
						EntityModelCacheFactory.getInstance().updateEntityAssociationDateSynonyms(dateSynonym);

						table.repaint();
					}
				}
				catch (Exception ex) {
					ClientUtil.handleRuntimeException(ex);
				}
				try {
					ClientUtil.getCommunicator().unlock(synonymID, EntityType.DATE_SYNONYM);
				}
				catch (ServerException ex) {
					ClientUtil.getInstance().showErrorDialog(
							"msg.error.generic.service",
							new Object[] { ClientUtil.getInstance().getErrorMessage(ex) });
				}
			}
		}
	}

	private void reload() {
		try {
			initialized = true;
			List<DateSynonym> list = ClientUtil.getCommunicator().search(new AllNamedDateSynonymFilter());
			table.getSelectionTableModel().setDataList(list);
		}
		catch (Exception ex) {
			ClientUtil.handleRuntimeException(ex);
		}
	}

	private DateSynonym[] getSelectedDateSynonyms() {
		DateSynonym[] selectedSynonyms = new DateSynonym[table.getSelectedRowCount()];
		for (int i = 0; i < table.getSelectedRowCount(); i++) {
			selectedSynonyms[i] = getDateSynonym(table.getSelectedRows()[i]);
		}
		return selectedSynonyms;
	}

	private DateSynonym getDateSynonym(int selectedRow) {
		return (DateSynonym) table.getModel().getValueAt(table.convertRowIndexToModel(selectedRow), -1);
	}

	private void initPanel() {
		JPanel buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT));

		enableButtons(0);

		if (!readOnly) {
			buttonPanel.add(addButton);
			buttonPanel.add(copyButton);
			buttonPanel.add(editButton);
			buttonPanel.add(deleteButton);
			buttonPanel.add(mergeButton);
		}
		buttonPanel.add(new JSeparator());
		buttonPanel.add(refreshButton);
		setLayout(new BorderLayout(4, 4));
		add(buttonPanel, BorderLayout.NORTH);
		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	private class AddL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent event) throws Exception {
			DateSynonym dateSynonym = DateSynonymEditDialog.newDateSynonym(JOptionPane.getFrameForComponent(ClientUtil.getApplet()));

			if (dateSynonym != null) {
				try {
					int newID = ClientUtil.getCommunicator().save(dateSynonym, false);
					dateSynonym.setID(newID);
					table.getSelectionTableModel().addData(dateSynonym);
					EntityModelCacheFactory.getInstance().addDateSynonym(dateSynonym);
					selectDateSynonym(dateSynonym);
				}
				catch (Exception ex) {
					ClientUtil.handleRuntimeException(ex);
				}
				finally {
					enableButtons(table.getSelectedRowCount());
				}
			}
		}
	}

	private class EditL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent event) throws Exception {
			editSelectedSynonym();
			enableButtons(table.getSelectedRowCount());
		}
	}

	private class CopyL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent event) throws Exception {
			DateSynonym[] userSelection = getSelectedDateSynonyms();
			if (userSelection.length > 0) {
				DateSynonym dateSynonym = DateSynonymEditDialog.copyDateSynonym(userSelection[0]);
				if (dateSynonym != null) {
					try {
						int newID = ClientUtil.getCommunicator().save(dateSynonym, false);
						dateSynonym.setID(newID);
						table.getSelectionTableModel().addData(dateSynonym);
						EntityModelCacheFactory.getInstance().addDateSynonym(dateSynonym);
						selectDateSynonym(dateSynonym);
					}
					catch (Exception ex) {
						ClientUtil.handleRuntimeException(ex);
					}
					finally {
						enableButtons(table.getSelectedRowCount());
					}
				}
			}
		}
	}

	private class DeleteL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent event) throws Exception {
			DateSynonym[] userSelection = getSelectedDateSynonyms();
			if (userSelection.length > 0) {
				DateSynonym dateSynonym = userSelection[0];
				try {
					if (ClientUtil.getCommunicator().isInUse(dateSynonym)) {
						ClientUtil.getInstance().showWarning("msg.warning.date.synonym.used", new Object[] { dateSynonym.getName() });
					}
					else if (ClientUtil.getInstance().showConfirmation(
							"msg.question.delete.dateSynonym",
							new Object[] { dateSynonym.getName() })) {
						ClientUtil.getCommunicator().delete(dateSynonym.getID(), EntityType.DATE_SYNONYM);
						table.getSelectionTableModel().removeData(dateSynonym);
						EntityModelCacheFactory.getInstance().removeDateSynonym(dateSynonym);
					}
				}
				catch (Exception ex) {
					ClientUtil.handleRuntimeException(ex);
				}
				finally {
					enableButtons(0);
				}
			}
		}
	}

	private class RefreshL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent event) throws Exception {
			reload();
			table.refresh();
			enableButtons(0);
		}
	}

	private class MergeL extends AbstractThreadedActionAdapter {
		public void performAction(ActionEvent event) throws Exception {
			DateSynonym[] userSelection = getSelectedDateSynonyms();
			if (userSelection.length > 0) {
				DateSynonym newDateSynonym = DateSynonymEditDialog.mergeDateSynonyms(
						JOptionPane.getFrameForComponent(ClientUtil.getApplet()),
						userSelection);
				if (newDateSynonym != null) {
					try {
						ClientUtil.getCommunicator().replace(userSelection, newDateSynonym);

						EntityModelCacheFactory.getInstance().reloadAllDateSynonyms();
						table.getSelectionTableModel().setDataList(EntityModelCacheFactory.getInstance().getAllDateSynonyms());
						selectDateSynonym(newDateSynonym);
					}
					catch (Exception ex) {
						ClientUtil.handleRuntimeException(ex);
					}
					finally {
						enableButtons(table.getSelectedRowCount());
					}
				}
			}
		}
	}

	public void stateChanged(ChangeEvent e) {
		if (initialized) {
			return;
		}
		else if (((JTabbedPane) e.getSource()).getSelectedComponent().equals(this)) {
			reload();
		}
	}

	private void enableButtons(int rowsSelected) {
		if (!readOnly) {
			copyButton.setEnabled(rowsSelected == 1);
			editButton.setEnabled(rowsSelected == 1);
			deleteButton.setEnabled(rowsSelected == 1);
			mergeButton.setEnabled(rowsSelected > 1);
		}
	}

	private void selectDateSynonym(DateSynonym ds) {
		for (int i = 0; i < table.getSelectionTableModel().getRowCount(); i++) {
			DateSynonym rowData = (DateSynonym) ((DateSynonymTable) table).getValueAt(i, -1);
			if (ds.getID() == rowData.getID()) {
				int rowInView = table.convertRowIndexToView(i);
				table.getSelectionModel().setSelectionInterval(rowInView, rowInView);
				return;
			}
		}


	}

}

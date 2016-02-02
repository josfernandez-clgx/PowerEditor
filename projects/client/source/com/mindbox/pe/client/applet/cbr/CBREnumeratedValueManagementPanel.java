package com.mindbox.pe.client.applet.cbr;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.client.common.event.ValueChangeEvent;
import com.mindbox.pe.client.common.event.ValueChangeListener;
import com.mindbox.pe.model.CBREnumeratedValue;

/**
 * 
 * @since PowerEditor 1.0
 */
public class CBREnumeratedValueManagementPanel extends JPanel implements TableModelListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 20070716000001L;
	
	private static final String[] DEFAULT_LABELS =
		new String[] {
			ClientUtil.getInstance().getLabel("button.new"),
			ClientUtil.getInstance().getLabel("button.remove")};

	private final class NewL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			tableModel.addData(new CBREnumeratedValue("<New Value>"));
			table.changeSelection(table.getRowCount(), 0,false,false);
		}
	}

	private final class DeleteL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// get the selected association
			tableModel.removeDataAt(table.getSelectedRow());
		}
	}

	private class ListSelectionL implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				setEnabledSelectionAwareButtons(!lsm.isSelectionEmpty() && tableModel.isEditable());
			}
		}
	}

	private final String[] labels;
	private final CBREnumeratedValueTable table;
	private final CBREnumeratedValueTableModel tableModel;
	private JButton newButton, deleteButton;
	private final List<ValueChangeListener> changeListenerList;

	public CBREnumeratedValueManagementPanel(CBREnumeratedValueTableModel tableModel, String[] labels) {
		super();
		UIFactory.setLookAndFeel(this);
		if (labels == null || labels.length < 2) {
			this.labels = DEFAULT_LABELS;
		}
		else {
			this.labels = labels;
		}
		this.changeListenerList = new ArrayList<ValueChangeListener>();
		this.tableModel = tableModel;
		this.table = new CBREnumeratedValueTable(tableModel);

		initPanel();

		ListSelectionModel rowSM = table.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionL());
	}

	public final void addValueChangeListener(ValueChangeListener cl) {
		synchronized (changeListenerList) {
			if (!changeListenerList.contains(cl)) {
				changeListenerList.add(cl);
			}
		}
	}

	public final void removeValueChangeListener(ValueChangeListener cl) {
		synchronized (changeListenerList) {
			if (changeListenerList.contains(cl)) {
				changeListenerList.remove(cl);
			}
		}
	}

	protected final void fireValueChanged() {
		synchronized (changeListenerList) {
			for (int i = 0; i < changeListenerList.size(); i++) {
				changeListenerList.get(i).valueChanged(new ValueChangeEvent());
			}
		}
	}

	private void initPanel() {
		newButton = UIFactory.createButton(labels[0], "image.btn.small.add", new NewL(), null);
		deleteButton = UIFactory.createButton(labels[1], "image.btn.small.delete", new DeleteL(), null);

		ButtonPanel buttonPanel = new ButtonPanel(new JButton[] { newButton, deleteButton }, FlowLayout.LEFT);

		JPanel cPanel = UIFactory.createJPanel(new BorderLayout(4, 4));
		this.table.setPreferredScrollableViewportSize(new Dimension(200, 62));
		cPanel.add(new JScrollPane(this.table), BorderLayout.CENTER);

		setLayout(new BorderLayout(5, 5));
		add(buttonPanel, BorderLayout.NORTH);
		add(cPanel, BorderLayout.CENTER);

		deleteButton.setEnabled(false);
	}

	protected void setEnabledSelectionAwareButtons(boolean flag) {
		deleteButton.setEnabled(flag);
	}

	public void setEnabled(boolean flag) {
		newButton.setEnabled(flag);
		tableModel.setEditable(flag);
		table.setEnabled(flag);
		super.setEnabled(flag);
	}

	public void clear() {
		this.tableModel.removeTableModelListener(this);
		tableModel.clearDataList();
		this.tableModel.addTableModelListener(this);
		deleteButton.setEnabled(false);
	}

	public List<CBREnumeratedValue> getDataList() {
		return tableModel.getDataList();
	}

	public void setDataList(List<CBREnumeratedValue> dataList) {
		this.tableModel.removeTableModelListener(this);
		tableModel.setDataList(dataList);
		this.tableModel.addTableModelListener(this);
		deleteButton.setEnabled(false);
	}
	
	public void tableChanged(TableModelEvent event) {
		fireValueChanged();
	}
	

}

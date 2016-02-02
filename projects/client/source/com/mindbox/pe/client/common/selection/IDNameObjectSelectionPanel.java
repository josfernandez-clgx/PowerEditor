package com.mindbox.pe.client.common.selection;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mindbox.pe.client.common.ButtonPanel;
import com.mindbox.pe.client.common.table.IDNameObjectSelectionTable;
import com.mindbox.pe.model.IDNameObject;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public abstract class IDNameObjectSelectionPanel<D extends IDNameObject, B extends ButtonPanel> extends AbstractSelectionPanel<D, B> {

	private final class MouseL extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				if (!isReadOnly()) {
					getButtonPanel().handleDoubleClick();
				}
			}
		}
	}

	protected final IDNameObjectSelectionTable<?, D> selectionTable;
	private final ListSelectionListener listSelectionListener;

	public IDNameObjectSelectionPanel(String title, IDNameObjectSelectionTable<?, D> selectionTable, boolean readOnly) {
		super(title, readOnly);
		this.selectionTable = selectionTable;
		initPanel();

		if (!readOnly) {
			this.listSelectionListener = new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
						ListSelectionModel lsm = (ListSelectionModel) e.getSource();
						setEnabledSelectionAwares(!lsm.isSelectionEmpty());
					}
				}
			};
			selectionTable.getSelectionModel().addListSelectionListener(listSelectionListener);
		}
		else {
			this.listSelectionListener = null;
		}
		selectionTable.addMouseListener(new MouseL());
	}

	protected final void checkReadOnly() {
		if (isReadOnly()) {
			throw new IllegalStateException("READ-ONLY-MODE");
		}
	}

	public void add(D object) {
		checkReadOnly();

		if (object == null) throw new NullPointerException("object cannot be null");
		selectionTable.getSelectionModel().removeListSelectionListener(listSelectionListener);
		this.selectionTable.add(object);
		selectionTable.getSelectionModel().addListSelectionListener(listSelectionListener);
	}

	public void updateDisplay(int entityID) {
		int index = selectionTable.getIndexOfIDNameObjectInView(entityID);
		if (index >= 0) {
			this.selectionTable.updateRow(index);
			selectionTable.selectIDNameObject(entityID);
		}
	}

	public void selectEntity(int entityID) {
		int index = selectionTable.getIndexOfIDNameObjectInView(entityID);
		if (index >= 0) {
			selectionTable.selectIDNameObject(entityID);
		}
	}

	public final List<D> getSelectedObjects() {
		return this.selectionTable.getSelectedDataObjects();
	}

	public void populate(List<D> dataList) {
		this.selectionTable.setDataList(dataList);
		setEnabledSelectionAwares(false);
	}

	public void remove(D object) {
		checkReadOnly();

		this.selectionTable.remove(object);
		setEnabledSelectionAwares(false);
	}

	public void clearSelection() {
		selectionTable.clearSelection();
		setEnabledSelectionAwares(false);
	}

	private void initPanel() {
		GridBagLayout bag = new GridBagLayout();
		setLayout(bag);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.0;
		c.gridheight = 1;
		c.insets = new Insets(4, 4, 2, 4);

		createButtonPanel();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		addComponent(this, bag, c, getButtonPanel());

		c.insets = new Insets(1, 2, 1, 1);

		// add additional components
		c.insets = new Insets(1, 2, 1, 1);

		// add table
		this.selectionTable.setPreferredScrollableViewportSize(new Dimension(200, 60));
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1.0;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1.0;
		addComponent(this, bag, c, new JScrollPane(this.selectionTable));
	}

	// TT 2072
	public void update(D object) {
	}

}

package com.mindbox.pe.client.common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.event.ValueChangeEvent;
import com.mindbox.pe.client.common.event.ValueChangeListener;

/**
 * @author deklerk
 *
 */
public class OrderedListChoicePanel<T> extends PanelBase implements ActionListener, ListDataListener, ListSelectionListener {

	private JList unselectedJList;
	private JList selectedJList;
	private DefaultListModel unselectedModel;
	private DefaultListModel selectedModel;
	private JButton selectButton;
	private JButton selectAllButton;
	private JButton unselectButton;
	private JButton unselectAllButton;
	private JButton moveSelectionUpButton;
	private JButton moveSelectionDownButton;
	private final List<ValueChangeListener> changeListenerList;
	private boolean fireChanges = true;

	/**
	 * 
	 */
	public OrderedListChoicePanel() {
		super();
		this.changeListenerList = new ArrayList<ValueChangeListener>();
		unselectedModel = new DefaultListModel();
		selectedModel = new DefaultListModel();
		selectedModel.addListDataListener(this);
		initComponents();
		addComponents();
		updateButtonsForDataChange();
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
		if (fireChanges) {
			synchronized (changeListenerList) {
				for (int i = 0; i < changeListenerList.size(); i++) {
					changeListenerList.get(i).valueChanged(new ValueChangeEvent());
				}
			}
		}
	}

	private void initComponents() {
		unselectedJList = UIFactory.createList(unselectedModel);
		unselectedJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		unselectedJList.setLayoutOrientation(JList.VERTICAL);
		unselectedJList.setVisibleRowCount(8);
		unselectedJList.addListSelectionListener(this);
		selectedJList = UIFactory.createList(selectedModel);
		selectedJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		selectedJList.setLayoutOrientation(JList.VERTICAL);
		selectedJList.setVisibleRowCount(8);
		selectedJList.addListSelectionListener(this);
		Dimension buttonSize = new Dimension(30, 15);
		selectButton = UIFactory.createButton(">", null, this, "button.tooltip.choice.select");
		selectButton.setPreferredSize(buttonSize);
		selectAllButton = UIFactory.createButton(">>", null, this, "button.tooltip.choice.select.all");
		selectAllButton.setPreferredSize(buttonSize);
		unselectButton = UIFactory.createButton("<", null, this, "button.tooltip.choice.unselect");
		unselectButton.setPreferredSize(buttonSize);
		unselectAllButton = UIFactory.createButton("<<", null, this, "button.tooltip.choice.unselect.all");
		unselectAllButton.setPreferredSize(buttonSize);
		moveSelectionUpButton = UIFactory.createButton("^", null, this, "button.tooltip.choice.move.up");
		moveSelectionUpButton.setPreferredSize(buttonSize);
		moveSelectionDownButton = UIFactory.createButton("v", null, this, "button.tooltip.choice.move.down");
		moveSelectionDownButton.setPreferredSize(buttonSize);
	}

	private void addComponents() {
		JPanel unselectedPanel = UIFactory.createTitledPanel("Unselected items");
		unselectedPanel.setLayout(new BorderLayout());
		JScrollPane unselectedScroller = new JScrollPane(unselectedJList);
		unselectedPanel.add(unselectedScroller, BorderLayout.CENTER);

		Box box = Box.createVerticalBox();
		box.add(new JSeparator());
		box.add(selectButton);
		box.add(selectAllButton);
		box.add(unselectButton);
		box.add(unselectAllButton);
		box.add(Box.createVerticalGlue());

		JPanel westPanel = UIFactory.createBorderLayoutPanel(0, 0);
		westPanel.add(unselectedPanel, BorderLayout.CENTER);
		westPanel.add(box, BorderLayout.EAST);

		JPanel selectedPanel = UIFactory.createTitledPanel("Selected items");
		selectedPanel.setLayout(new BorderLayout());
		JScrollPane selectedScroller = new JScrollPane(selectedJList);
		selectedPanel.add(selectedScroller, BorderLayout.CENTER);

		box = Box.createVerticalBox();
		box.add(new JSeparator());
		box.add(moveSelectionUpButton);
		box.add(moveSelectionDownButton);
		box.add(Box.createVerticalGlue());

		JPanel eastPanel = UIFactory.createBorderLayoutPanel(0, 0);
		eastPanel.add(selectedPanel, BorderLayout.CENTER);
		eastPanel.add(box, BorderLayout.EAST);

		box = Box.createHorizontalBox();
		box.add(westPanel);
		box.add(eastPanel);
		setLayout(new BorderLayout(2, 2));
		add(box, BorderLayout.CENTER);
	}

	public void clear() {
		fireChanges = false;
		if (selectedModel != null) selectedModel.clear();
		if (unselectedModel != null) unselectedModel.clear();
		fireChanges = true;
		updateButtonsForDataChange();
	}

	public void setObjectLists(List<T> completeList, List<T> selectedList) {
		clear();
		fireChanges = false;
		Iterator<T> it = selectedList.iterator();
		while (it.hasNext())
			selectedModel.addElement(it.next());
		it = completeList.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (!selectedList.contains(obj)) unselectedModel.addElement(obj);
		}
		fireChanges = true;
		updateButtonsForDataChange();
	}

	@SuppressWarnings("unchecked")
	public List<T> getSelectedObjects() {
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < selectedModel.getSize(); i++)
			list.add((T) selectedModel.get(i));
		return list;
	}

	private void updateButtonsForDataChange() {
		if (selectedModel.isEmpty()) {
			this.unselectButton.setEnabled(false);
			this.unselectAllButton.setEnabled(false);
			this.moveSelectionDownButton.setEnabled(false);
			this.moveSelectionUpButton.setEnabled(false);
		}
		else {
			this.unselectAllButton.setEnabled(true);
		}
		if (unselectedModel.isEmpty()) {
			this.selectAllButton.setEnabled(false);
			this.selectButton.setEnabled(false);
		}
		else {
			this.selectAllButton.setEnabled(true);
		}
	}

	public void actionPerformed(ActionEvent e) {
		selectedJList.removeListSelectionListener(this);
		unselectedJList.removeListSelectionListener(this);
		fireChanges = false;
		if (e.getSource().equals(selectButton)) {
			Object[] selectedValues = unselectedJList.getSelectedValues();
			int insertionIndex = selectedModel.size();
			for (int i = 0; i < selectedValues.length; i++) {
				unselectedModel.removeElement(selectedValues[i]);
				selectedModel.addElement(selectedValues[i]);
			}
			selectedJList.setSelectionInterval(insertionIndex, selectedModel.size() - 1);
		}
		else if (e.getSource().equals(selectAllButton)) {
			int insertionIndex = selectedModel.size();
			while (!unselectedModel.isEmpty()) {
				selectedModel.addElement(unselectedModel.get(0));
				unselectedModel.remove(0);
			}
			selectedJList.setSelectionInterval(insertionIndex, selectedModel.size() - 1);
		}
		else if (e.getSource().equals(unselectButton)) {
			Object[] unselectedValues = selectedJList.getSelectedValues();
			for (int i = 0; i < unselectedValues.length; i++) {
				selectedModel.removeElement(unselectedValues[i]);
				unselectedModel.addElement(unselectedValues[i]);
			}
		}
		else if (e.getSource().equals(unselectAllButton)) {
			while (!selectedModel.isEmpty()) {
				unselectedModel.addElement(selectedModel.get(0));
				selectedModel.remove(0);
			}
		}
		else if (e.getSource().equals(this.moveSelectionUpButton)) {
			Object[] selectedValues = selectedJList.getSelectedValues();
			int[] selectedIndices = selectedJList.getSelectedIndices();
			int insertionIndex = Math.max(selectedIndices[0] - 1, 0);
			for (int i = 0; i < selectedValues.length; i++)
				selectedModel.removeElement(selectedValues[i]);
			for (int i = selectedValues.length; i > 0; i--)
				selectedModel.add(insertionIndex, selectedValues[i - 1]);
			selectedJList.setSelectionInterval(insertionIndex, insertionIndex + selectedValues.length - 1);
		}
		else if (e.getSource().equals(this.moveSelectionDownButton)) {

			Object[] selectedValues = selectedJList.getSelectedValues();
			int[] selectedIndices = selectedJList.getSelectedIndices();
			int insertionIndex = Math.min(selectedIndices[selectedIndices.length - 1] - selectedIndices.length + 2, selectedModel.size()
					- selectedIndices.length);
			for (int i = 0; i < selectedValues.length; i++)
				selectedModel.removeElement(selectedValues[i]);
			for (int i = selectedValues.length; i > 0; i--)
				selectedModel.add(insertionIndex, selectedValues[i - 1]);
			selectedJList.setSelectionInterval(insertionIndex, insertionIndex + selectedValues.length - 1);
		}
		updateButtonsForSelectionChange(selectedJList);
		updateButtonsForSelectionChange(unselectedJList);
		updateButtonsForDataChange();
		selectedJList.addListSelectionListener(this);
		unselectedJList.addListSelectionListener(this);
		fireChanges = true;
		fireValueChanged();
	}

	public void contentsChanged(ListDataEvent arg0) {
		fireValueChanged();
		updateButtonsForDataChange();
	}

	public void intervalAdded(ListDataEvent arg0) {
		fireValueChanged();
		updateButtonsForDataChange();
	}

	public void intervalRemoved(ListDataEvent arg0) {
		fireValueChanged();
		updateButtonsForDataChange();
	}

	private void updateButtonsForSelectionChange(Object c) {
		if (c.equals(unselectedJList)) {
			if (unselectedJList.getSelectedIndex() == -1) {
				this.selectButton.setEnabled(false);
			}
			else {
				this.selectButton.setEnabled(true);
				this.unselectButton.setEnabled(false);
				this.moveSelectionDownButton.setEnabled(false);
				this.moveSelectionUpButton.setEnabled(false);
				this.selectedJList.removeListSelectionListener(this);
				this.selectedJList.clearSelection();
				this.selectedJList.addListSelectionListener(this);
			}
		}
		else if (c.equals(selectedJList)) {
			if (selectedJList.getSelectedIndex() == -1) {
				this.unselectButton.setEnabled(false);
				this.moveSelectionDownButton.setEnabled(false);
				this.moveSelectionUpButton.setEnabled(false);
			}
			else {
				this.unselectButton.setEnabled(true);
				this.moveSelectionDownButton.setEnabled(true);
				this.moveSelectionUpButton.setEnabled(true);
				this.selectButton.setEnabled(false);
				this.unselectedJList.removeListSelectionListener(this);
				this.unselectedJList.clearSelection();
				this.unselectedJList.addListSelectionListener(this);
			}
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) return;
		this.updateButtonsForSelectionChange(e.getSource());
	}
}

/*
 * Created on 2004. 2. 5.
 *
 */
package com.mindbox.pe.client.common;

import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.comparator.UsageTypeComparator;
import com.mindbox.pe.model.process.PhaseTask;
import com.mindbox.pe.model.process.UsagePhaseTask;

/**
 * Task selection field.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.3.0
 */
public class PhaseTaskSelectField extends AbstractDropSelectField {

	private PhaseTask task = null;
	private JList taskList = null;

	public PhaseTaskSelectField() {
		super(false);
	}

	public final PhaseTask getValue() {
		return task;
	}

	public final boolean hasValue() {
		return task != null;
	}

	public final void setValue(PhaseTask task) {
		this.task = task;
		resetText();
	}

	private void resetText() {
		textField.setText((task == null ? "" : task.getName()));
	}

	private void updateFields() {
		if (taskList.getSelectedIndex() > -1) {
			task = (PhaseTask) taskList.getSelectedValue();
		}
		else {
			task = null;
		}
		resetText();
	}

	private void initTaskList() {
		if (taskList == null) {
			DefaultListModel model = new DefaultListModel();
			taskList = new JList(model);

			// populate tasks
			TemplateUsageType[] usageTypes = TemplateUsageType.getAllInstances();
			Arrays.sort(usageTypes, UsageTypeComparator.getInstance());
			for (int i = 0; i < usageTypes.length; i++) {
				model.addElement(new UsagePhaseTask(usageTypes[i]));
			}

			taskList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent arg0) {
					try {
						Thread.sleep(250);
					}
					catch (InterruptedException e) {
					}
					closeWindow();
				}
			});
		}
	}

	protected JComponent createSelectorComponent() {
		initTaskList();
		return new JScrollPane(taskList);
	}

	private int findTaskIndex(String name) {
		for (int i = 0; i < taskList.getModel().getSize(); i++) {
			if (name.equals(((PhaseTask) taskList.getModel().getElementAt(i)).getName())) {
				return i;
			}
		}
		return -1;
	}

	private void selectTask(String name) {
		int index = findTaskIndex(name);
		if (index != -1) {
			taskList.setSelectedIndex(index);
		}
		resetText();
	}

	protected void selectSelectedValues() {
		if (task != null) {
			selectTask(task.getName());
		}
	}

	protected void selectorClosed() {
		updateFields();
	}

	protected void valueDeleted() {
		task = null;
	}
}
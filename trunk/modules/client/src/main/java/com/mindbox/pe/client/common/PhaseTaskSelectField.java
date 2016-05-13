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

	private static final long serialVersionUID = -3951228734910107454L;

	private PhaseTask task = null;
	private JList<UsagePhaseTask> taskList = null;

	public PhaseTaskSelectField() {
		super(false);
	}

	@Override
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

	public final PhaseTask getValue() {
		return task;
	}

	@Override
	public final boolean hasValue() {
		return task != null;
	}

	private void initTaskList() {
		if (taskList == null) {
			DefaultListModel<UsagePhaseTask> model = new DefaultListModel<UsagePhaseTask>();
			taskList = new JList<UsagePhaseTask>(model);

			// populate tasks
			TemplateUsageType[] usageTypes = TemplateUsageType.getAllInstances();
			Arrays.sort(usageTypes, UsageTypeComparator.getInstance());
			for (int i = 0; i < usageTypes.length; i++) {
				model.addElement(new UsagePhaseTask(usageTypes[i]));
			}

			taskList.addListSelectionListener(new ListSelectionListener() {
				@Override
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

	private void resetText() {
		textField.setText((task == null ? "" : task.getName()));
	}

	@Override
	protected void selectorClosed() {
		updateFields();
	}

	@Override
	protected void selectSelectedValues() {
		if (task != null) {
			selectTask(task.getName());
		}
	}

	private void selectTask(String name) {
		int index = findTaskIndex(name);
		if (index != -1) {
			taskList.setSelectedIndex(index);
		}
		resetText();
	}

	public final void setValue(PhaseTask task) {
		this.task = task;
		resetText();
	}

	private void updateFields() {
		if (taskList.getSelectedIndex() > -1) {
			task = taskList.getSelectedValue();
		}
		else {
			task = null;
		}
		resetText();
	}

	@Override
	protected void valueDeleted() {
		task = null;
	}
}
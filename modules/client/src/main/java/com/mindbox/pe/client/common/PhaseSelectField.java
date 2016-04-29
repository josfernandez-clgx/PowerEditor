package com.mindbox.pe.client.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.comparator.PhaseComparator;
import com.mindbox.pe.model.filter.AllSearchFilter;
import com.mindbox.pe.model.process.Phase;

/**
 * Phase selection field.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 3.3.0
 */
public class PhaseSelectField extends AbstractDropSelectField {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private Phase phase = null;
	private JList<Phase> phaseList = null;
	private LinkedList<Phase> selectedPhaseList = null;
	private final boolean allowRootOnly;

	public PhaseSelectField(boolean allowRootOnly) {
		this(allowRootOnly, false);
	}

	public PhaseSelectField(boolean allowRootOnly, boolean forMultiSelect) {
		super(forMultiSelect);
		if (forMultiSelect) {
			selectedPhaseList = new LinkedList<Phase>();
		}
		this.allowRootOnly = allowRootOnly;
	}

	@Override
	protected JComponent createSelectorComponent() {
		initPhaseCombo();
		return new JScrollPane(phaseList);
	}

	private int findPhaseIndex(int phaseID) {
		for (int i = 0; i < phaseList.getModel().getSize(); i++) {
			if (phaseID == phaseList.getModel().getElementAt(i).getID()) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 
	 * @return value of this
	 * @throws IllegalStateException if this is for multiple selection. Use {@link #getValues} instead
	 */
	public final Phase getValue() {
		if (forMultiSelect) {
			throw new IllegalStateException("Not legal for multi-selection field");
		}
		return phase;
	}

	/**
	 * 
	 * @return value of this
	 * @throws IllegalStateException if this is not for multiple selection. Use {@link #getValue} instead
	 */
	public final Phase[] getValues() {
		if (!forMultiSelect) {
			throw new IllegalStateException("Not legal for single-selection field");
		}
		return selectedPhaseList.toArray(new Phase[0]);
	}

	@Override
	public final boolean hasValue() {
		return (forMultiSelect ? !selectedPhaseList.isEmpty() : phase != null);
	}

	private void initPhaseCombo() {
		if (phaseList == null) {
			DefaultListModel<Phase> model = new DefaultListModel<Phase>();
			phaseList = new JList<Phase>(model);
			phaseList.setSelectionMode((forMultiSelect ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION));

			// populate phase
			try {
				List<Phase> phases = ClientUtil.getCommunicator().search(new AllSearchFilter<Phase>(PeDataType.PROCESS_PHASE));
				Collections.sort(phases, PhaseComparator.getInstance());
				for (Iterator<Phase> iter = phases.iterator(); iter.hasNext();) {
					Phase phase = iter.next();
					if (!allowRootOnly || phase.isRoot()) {
						model.addElement(phase);
					}
				}
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}

			if (!forMultiSelect) {
				phaseList.addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent arg0) {
						try {
							Thread.sleep(200);
						}
						catch (InterruptedException e) {
						}
						closeWindow();
					}
				});
			}
		}
	}

	private void resetPhaseText() {
		if (forMultiSelect) {
			StringBuilder buff = new StringBuilder();
			for (Iterator<Phase> iter = selectedPhaseList.iterator(); iter.hasNext();) {
				Phase element = iter.next();
				buff.append(element.getDisplayName());
				if (iter.hasNext()) {
					buff.append(", ");
				}
			}
			textField.setText(buff.toString());
		}
		else {
			textField.setText((phase == null ? "" : phase.getDisplayName()));
		}
	}

	@Override
	protected void selectorClosed() {
		updateFields();
	}

	private void selectPhase(int phaseID) {
		int index = findPhaseIndex(phaseID);
		if (index != -1) {
			phaseList.setSelectedIndex(index);
		}
		resetPhaseText();
	}

	@Override
	protected void selectSelectedValues() {
		if (forMultiSelect) {
			phaseList.clearSelection();
			if (!selectedPhaseList.isEmpty()) {
				List<Integer> intList = new ArrayList<Integer>();
				for (Iterator<Phase> iter = selectedPhaseList.iterator(); iter.hasNext();) {
					Phase element = iter.next();
					int index = findPhaseIndex(element.getID());
					if (index >= 0) {
						intList.add(new Integer(index));
					}
				}
				phaseList.setSelectedIndices(UtilBase.toIntArray(intList));
				resetPhaseText();
			}
		}
		else {
			if (phase != null) {
				selectPhase(phase.getID());
			}
		}
	}

	/**
	 * 
	 * @param phase
	 * @throws IllegalStateException if this is for multiple selection. Use {@link #setValues} instead
	 */
	public final void setValue(Phase phase) {
		if (forMultiSelect) {
			throw new IllegalStateException("Not legal for multi-selection field");
		}
		this.phase = phase;
		resetPhaseText();
	}

	/**
	 * 
	 * @param phases
	 * @throws IllegalStateException if this is not for multiple selection. Use {@link #setValue} instead
	 */
	public final void setValues(Phase[] phases) {
		if (!forMultiSelect) throw new IllegalStateException("Not legal for single-selection field");
		setValues_internal(phases);
	}

	private void setValues_internal(Phase[] phases) {
		selectedPhaseList.clear();
		if (phases != null) {
			for (int i = 0; i < phases.length; i++) {
				selectedPhaseList.add(phases[i]);
			}
		}
		resetPhaseText();
	}

	private void updateFields() {
		if (forMultiSelect) {
			setValues_internal(phaseList.getSelectedValuesList().toArray(new Phase[0]));
		}
		else {
			if (phaseList.getSelectedIndex() > -1) {
				phase = phaseList.getSelectedValue();
			}
			else {
				phase = null;
			}
		}
		resetPhaseText();
	}

	@Override
	protected void valueDeleted() {
		phase = null;
		selectedPhaseList.clear();
	}

}
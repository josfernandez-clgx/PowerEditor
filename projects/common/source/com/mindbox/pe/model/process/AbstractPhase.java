package com.mindbox.pe.model.process;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.model.AbstractIDNameObject;

/**
 * Phase definition.
 * @author kim
 * @since PowerEditor 3.3.0
 */
public abstract class AbstractPhase extends AbstractIDNameObject implements Phase {

	private static final long serialVersionUID = 2004060240000L;

	private final LinkedList<Phase> phaseList = new LinkedList<Phase>();
	private final List<Phase> prerequisiteList = new ArrayList<Phase>();
	private int phaseType;
	private PhaseTask phaseTask;
	private String displayName;
	private Phase parent = null;
	private boolean disjunctivePrereqs;

	/**
	 * 
	 * @param phaseType
	 * @param id
	 * @param name
	 */
	protected AbstractPhase(int phaseType, int id, String name) {
		super(id, name);
		this.phaseType = phaseType;
	}

	protected AbstractPhase(AbstractPhase source) {
		super(source);
		copyFrom(source);
	}

	public String getAuditDescription() {
		return toString();
	}
	
	/**
	 * @return the disjunctivePrereqs
	 */
	public boolean isDisjunctivePrereqs() {
		return disjunctivePrereqs;
	}

	/**
	 * @param disjunctivePrereqs The disjunctivePrereqs to set.
	 */
	public void setDisjunctivePrereqs(boolean disjunctivePrereqs) {
		this.disjunctivePrereqs = disjunctivePrereqs;
	}

	public final boolean isRoot() {
		return parent == null;
	}

	public final boolean hasSubPhases() {
		return !phaseList.isEmpty();
	}

	public final void copyFrom(Phase phase) {
		setName(phase.getName());
		this.displayName = phase.getDisplayName();
		this.phaseTask = phase.getPhaseTask();
		this.phaseType = phase.getPhaseType();
		this.parent = phase.getParent();
		this.disjunctivePrereqs = phase.isDisjunctivePrereqs();

		this.phaseList.clear();
		Phase[] phases = phase.getSubPhases();
		for (int i = 0; i < phases.length; i++) {
			this.phaseList.add(phases[i]);
		}

		this.prerequisiteList.clear();
		phases = phase.getPrerequisites();
		for (int i = 0; i < phases.length; i++) {
			this.prerequisiteList.add(phases[i]);
		}
	}

	/**
	 * @return the phaseTask
	 */
	public final PhaseTask getPhaseTask() {
		return phaseTask;
	}

	public final int getPhaseType() {
		return phaseType;
	}

	/**
	 * @param phaseTask The phaseTask to set.
	 */
	public final void setPhaseTask(PhaseTask phaseTask) {
		this.phaseTask = phaseTask;
	}

	public final boolean hasPhaseTask() {
		return phaseTask != null;
	}

	public final Phase getParent() {
		return parent;
	}

	public final void setParent(Phase parent) {
		this.parent = parent;
	}

	public final void addSubPhase(Phase phase) {
		synchronized (phaseList) {
			if (!phaseList.contains(phase)) {
				phaseList.addLast(phase);
			}
		}
	}

	public final Phase[] getSubPhases() {
		return (Phase[]) phaseList.toArray(new Phase[0]);
	}

	public final void removeSubPhase(Phase phase) {
		synchronized (phaseList) {
			if (phaseList.contains(phase)) {
				phaseList.remove(phase);
			}
		}
	}

	public String toString() {
		return displayName;
	}

	/**
	 * @return the displayName
	 */
	public final String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName The displayName to set.
	 */
	public final void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public final void setName(String name) {
		super.setName(name);
	}

	public final void addPrerequisite(Phase phase) {
		synchronized (prerequisiteList) {
			if (!prerequisiteList.contains(phase)) {
				prerequisiteList.add(phase);
			}
		}
	}

	public final boolean hasPrerequisites() {
		return !prerequisiteList.isEmpty();
	}

	public final Phase[] getPrerequisites() {
		return prerequisiteList.toArray(new Phase[0]);
	}

	public final void removePrerequisite(Phase phase) {
		synchronized (prerequisiteList) {
			if (prerequisiteList.contains(phase)) {
				prerequisiteList.remove(phase);
			}
		}
	}

	public void setPrerequisites(Phase[] phases) {
		synchronized (prerequisiteList) {
			prerequisiteList.clear();
			for (int i = 0; i < phases.length; i++) {
				prerequisiteList.add(phases[i]);
			}
		}
	}
}
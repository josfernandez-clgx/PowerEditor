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

	protected AbstractPhase(AbstractPhase source) {
		super(source);
		copyFrom(source);
	}

	/**
	 * 
	 * @param phaseType phaseType
	 * @param id id
	 * @param name name
	 */
	protected AbstractPhase(int phaseType, int id, String name) {
		super(id, name);
		this.phaseType = phaseType;
	}

	@Override
	public final void addPrerequisite(Phase phase) {
		synchronized (prerequisiteList) {
			if (!prerequisiteList.contains(phase)) {
				prerequisiteList.add(phase);
			}
		}
	}

	@Override
	public final void addSubPhase(Phase phase) {
		synchronized (phaseList) {
			if (!phaseList.contains(phase)) {
				phaseList.addLast(phase);
			}
		}
	}

	@Override
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

	@Override
	public String getAuditDescription() {
		return toString();
	}

	/**
	 * @return the displayName
	 */
	@Override
	public final String getDisplayName() {
		return displayName;
	}

	@Override
	public final Phase getParent() {
		return parent;
	}

	/**
	 * @return the phaseTask
	 */
	@Override
	public final PhaseTask getPhaseTask() {
		return phaseTask;
	}

	@Override
	public final int getPhaseType() {
		return phaseType;
	}

	@Override
	public final Phase[] getPrerequisites() {
		return prerequisiteList.toArray(new Phase[0]);
	}

	@Override
	public final Phase[] getSubPhases() {
		return (Phase[]) phaseList.toArray(new Phase[0]);
	}

	@Override
	public final boolean hasPhaseTask() {
		return phaseTask != null;
	}

	@Override
	public final boolean hasPrerequisites() {
		return !prerequisiteList.isEmpty();
	}

	@Override
	public final boolean hasSubPhases() {
		return !phaseList.isEmpty();
	}

	/**
	 * @return the disjunctivePrereqs
	 */
	@Override
	public boolean isDisjunctivePrereqs() {
		return disjunctivePrereqs;
	}

	@Override
	public final boolean isRoot() {
		return parent == null;
	}

	@Override
	public final void removePrerequisite(Phase phase) {
		synchronized (prerequisiteList) {
			if (prerequisiteList.contains(phase)) {
				prerequisiteList.remove(phase);
			}
		}
	}

	@Override
	public final void removeSubPhase(Phase phase) {
		synchronized (phaseList) {
			if (phaseList.contains(phase)) {
				phaseList.remove(phase);
			}
		}
	}

	/**
	 * @param disjunctivePrereqs The disjunctivePrereqs to set.
	 */
	@Override
	public void setDisjunctivePrereqs(boolean disjunctivePrereqs) {
		this.disjunctivePrereqs = disjunctivePrereqs;
	}

	/**
	 * @param displayName The displayName to set.
	 */
	@Override
	public final void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public final void setName(String name) {
		super.setName(name);
	}

	@Override
	public final void setParent(Phase parent) {
		this.parent = parent;
	}

	/**
	 * @param phaseTask The phaseTask to set.
	 */
	@Override
	public final void setPhaseTask(PhaseTask phaseTask) {
		this.phaseTask = phaseTask;
	}

	@Override
	public void setPrerequisites(Phase[] phases) {
		synchronized (prerequisiteList) {
			prerequisiteList.clear();
			for (int i = 0; i < phases.length; i++) {
				prerequisiteList.add(phases[i]);
			}
		}
	}

	@Override
	public String toString() {
		return displayName;
	}
}
package com.mindbox.pe.model.process;

import com.mindbox.pe.model.Auditable;
import com.mindbox.pe.model.Persistent;


/**
 * Phase definition.
 *
 * @author kim
 * @since PowerEditor 3.3.0
 */
public interface Phase extends Persistent, Auditable {

	void setID(int id);
	int getPhaseType();
	
	boolean isRoot();
	
	void copyFrom(Phase phase);
	
	String getName();
	void setName(String name);
	
	String getDisplayName();
	void setDisplayName(String dispName);
	
	PhaseTask getPhaseTask();
	boolean hasPhaseTask();
	void setPhaseTask(PhaseTask task);
	
	Phase getParent();
	void setParent(Phase parent);
	
	void addSubPhase(Phase phase);
	Phase[] getSubPhases();
	boolean hasSubPhases();
	//int numberOfSubPhases();
	void removeSubPhase(Phase phase);
	
	void addPrerequisite(Phase phase);
	Phase[] getPrerequisites();
	boolean hasPrerequisites();
	void removePrerequisite(Phase phase);
	void setPrerequisites(Phase[] phases);
	
	boolean isDisjunctivePrereqs();
	void setDisjunctivePrereqs(boolean flag);
	
}

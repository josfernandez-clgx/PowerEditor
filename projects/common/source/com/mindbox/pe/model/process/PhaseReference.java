/*
 * Created on 2004. 6. 29.
 */
package com.mindbox.pe.model.process;

import com.mindbox.pe.model.Auditable;


/**
 * 
 *
 * @author kim
 * @since PowerEditor  
 */
public class PhaseReference extends AbstractPhase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2007051600001L;
	
	private Phase referecePhase;

	/**
	 * @param phaseType
	 * @param id
	 * @param name
	 */
	PhaseReference(int phaseType, int id, String name) {
		super(phaseType, id, name);
	}

	private PhaseReference(PhaseReference source) {
		super(source);
		this.referecePhase = source.referecePhase;
	}

	public Auditable deepCopy() {
		return new PhaseReference(this);
	}

	/**
	 * @return Returns the referecePhase.
	 */
	public Phase getReferecePhase() {
		return referecePhase;
	}

	/**
	 * @param referecePhase The referecePhase to set.
	 */
	public void setReferecePhase(Phase referecePhase) {
		this.referecePhase = referecePhase;
	}


}
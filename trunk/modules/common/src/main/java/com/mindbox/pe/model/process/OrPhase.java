/*
 * Created on 2004. 6. 25.
 */
package com.mindbox.pe.model.process;

import com.mindbox.pe.model.Auditable;


/**
 * Or Phase definition.
 * All phases in this are executed concurrently.
 * @author kim
 * @since PowerEditor 3.3.0
 */
public class OrPhase extends AbstractPhase {

	private static final long serialVersionUID = 2004060240010L;

	/**
	 * @param type
	 * @param id
	 * @param name
	 */
	OrPhase(int type, int id, String name) {
		super(type, id, name);
	}

	private OrPhase(OrPhase source) {
		super(source);
	}

	public Auditable deepCopy() {
		return new OrPhase(this);
	}

	public String toString() {
		return "OrPhase" + super.toString();
	}
}

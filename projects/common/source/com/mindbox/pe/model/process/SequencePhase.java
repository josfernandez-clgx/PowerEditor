/*
 * Created on 2004. 6. 24.
 */
package com.mindbox.pe.model.process;

import com.mindbox.pe.model.Auditable;


/**
 * Sequence phase implementation.
 * All phases in this will be executed sequencially.
 * @author kim
 * @since PowerEditor 3.3.0
 */
public class SequencePhase extends AbstractPhase {

	private static final long serialVersionUID = 2004060240012L;

	/**
	 * @param type
	 * @param id
	 * @param name
	 */
	SequencePhase(int type, int id, String name) {
		super(type, id, name);
	}

	private SequencePhase(SequencePhase source) {
		super(source);
	}

	public Auditable deepCopy() {
		return new SequencePhase(this);
	}

}

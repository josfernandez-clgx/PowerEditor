/*
 * Created on 2004. 6. 25.
 */
package com.mindbox.pe.model.process;


/**
 * Factory for phase objects.
 *
 * @author kim
 * @since PowerEditor  3.3.0
 */
public class PhaseFactory {

	/** Sequence phase type. */
	public static final int TYPE_SEQUENCE = 1;

	/** OR phase type. */
	public static final int TYPE_OR = 2;

	/** Reference phase type. */
	public static final int TYPE_REFERENCE = 9;

	/**
	 * Creates a new phase with the specified parameters.
	 * @param phaseType phase type. Must be {@link #TYPE_OR} or {@link #TYPE_SEQUENCE}.
	 * @param phaseID phaseID
	 * @param name name
	 * @param dispName dispName
	 * @return phase object
	 * @throws IllegalArgumentException if <code>phaseType</code> is invalid
	 */
	public static Phase createPhase(int phaseType, int phaseID, String name, String dispName) {
		Phase phase = null;
		switch (phaseType) {
		case TYPE_OR:
			phase = new OrPhase(TYPE_OR, phaseID, name);
			break;
		case TYPE_SEQUENCE:
			phase = new SequencePhase(TYPE_SEQUENCE, phaseID, name);
			break;
		case TYPE_REFERENCE:
			phase = new PhaseReference(TYPE_REFERENCE, phaseID, name);
			break;
		default:
			throw new IllegalArgumentException("Invalid phaseType: " + phaseType);
		}
		phase.setDisplayName(dispName);
		return phase;
	}

	private PhaseFactory() {
	}

}

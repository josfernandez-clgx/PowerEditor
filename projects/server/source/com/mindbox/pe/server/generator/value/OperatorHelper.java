package com.mindbox.pe.server.generator.value;

import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.model.TimeSlice;

/**
 * Helper for generating value string formatted for LHS pattern.
 * This handles a particular set of operators.
 * @author Geneho Kim
 *
 */
interface OperatorHelper {

	/**
	 * Formats the specified value for the specified arguments.
	 * @param value
	 * @param op
	 * @param attribVarName
	 * @param asString
	 * @param reference
	 * @param timeSlice
	 * @return
	 * @throws RuleGenerationException on error
	 */
	ValueAndComment formatForPattern(Object value, int op, String attribVarName, boolean asString, Reference reference, TimeSlice timeSlice) throws RuleGenerationException;
}

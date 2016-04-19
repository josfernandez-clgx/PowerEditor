package com.mindbox.pe.common;

import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.TestTypeDefinition;

public interface GuidelineActionProvider {

	/**
	 * Gets the action type definition for the specified action type id.
	 * This should not throw an exception.
	 * @param id
	 * @return null, if not found or on error
	 */
	ActionTypeDefinition getActionType(int id);

	/**
	 * Gets the test type definition for the specified test type id.
	 * This should not throw an exception.
	 * @param id
	 * @return null, if not found or on error
	 */
	TestTypeDefinition getTestType(int id);
}

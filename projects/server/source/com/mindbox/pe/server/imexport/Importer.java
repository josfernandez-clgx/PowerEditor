package com.mindbox.pe.server.imexport;

import java.util.Map;

import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.communication.ImportResult;
import com.mindbox.pe.server.model.User;

interface Importer<T> {

	/**
	 * Imports data from the specified object holder map and adds statistics to the specified import result object.
	 * @param objectHolderMap
	 * @param importResult import result to add import details/statictics.
	 * @param merge merge flag
	 * @param user user who initiated the import request
	 * @param optionalData optional data
	 * @throws ImportException on error
	 */
	void importData(Map<String, DigestedObjectHolder> objectHolderMap, ImportResult importResult, boolean merge, User user, T optionalData) throws ImportException;
}

package com.mindbox.pe.server.imexport;

import com.mindbox.pe.communication.ImportResult;
import com.mindbox.pe.server.model.User;

interface Importer<T, O> {

	/**
	 * Imports data from the specified object holder map and adds statistics to the specified import result object.
	 * @param dataToImport data to import
	 * @param importResult import result to add import details/statictics.
	 * @param merge merge flag
	 * @param user user who initiated the import request
	 * @param optionalData optional data
	 * @throws ImportException on error
	 */
	void importData(T dataToImport, ImportResult importResult, boolean merge, User user, O optionalData) throws ImportException;
}

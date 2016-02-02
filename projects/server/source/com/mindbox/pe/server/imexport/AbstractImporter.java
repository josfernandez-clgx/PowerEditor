package com.mindbox.pe.server.imexport;

import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.common.validate.ValidationViolation;
import com.mindbox.pe.communication.ImportResult;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.model.User;

/**
 * Abstract implementation of {@link Importer}.
 * <br/>
 * <b>Note:</b>
 * Concrete implementations of this are NOT re-usable. To use them,
 * create a new instance and then invoke {@link #importData(Map, ImportResult)} once,
 * and then discard the object.
 * <p>
 * This is not thread safe.
 * @author kim
 *
 */
abstract class AbstractImporter<T> implements Importer<T> {

	protected final Logger logger;
	protected final ImportBusinessLogic importBusinessLogic;
	protected boolean merge;
	protected ImportResult importResult;
	protected User user;

	protected AbstractImporter(ImportBusinessLogic importBusinessLogic) {
		this.importBusinessLogic = importBusinessLogic;
		logger = Logger.getLogger(getClass());
	}

	@Override
	public final void importData(Map<String, DigestedObjectHolder> objectHolderMap, ImportResult importResult, boolean merge, User user, T optionalData)
			throws ImportException {
		this.importResult = importResult;
		this.merge = merge;
		this.user = user;
		for (String filename : objectHolderMap.keySet()) {
			processData(filename, objectHolderMap.get(filename), optionalData);
		}
	}

	protected abstract void processData(String filename, DigestedObjectHolder objectHolder, T optionalData) throws ImportException;

	void addErrors(Object context, DataValidationFailedException ex) {
		for (ValidationViolation violation : ex.getViolations()) {
			addError(context, violation.toErrorString());
		}
	}

	void addError(Object context, ImportException ex) {
		addError(context, ex.getMessage());
	}

	void addError(Object context, String message) {
		assert importResult != null;
		importResult.addErrorMessage(message, context);
	}

}

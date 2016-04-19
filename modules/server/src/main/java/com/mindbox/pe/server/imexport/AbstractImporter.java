package com.mindbox.pe.server.imexport;

import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;

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
abstract class AbstractImporter<T, O> implements Importer<T, O> {

	private static String toErrorString(ValidationViolation violation) {
		StringBuilder buff = new StringBuilder();
		buff.append(violation.getMessage());
		if (violation.getCauses() != null && !violation.getCauses().isEmpty()) {
			buff.append(". Cause: ");
			for (ValidationViolation cause : violation.getCauses()) {
				if (cause != null) {
					buff.append(toErrorString(cause));
					buff.append("; ");
				}
			}
		}
		return buff.toString();
	}

	protected final Logger logger;
	protected final ImportBusinessLogic importBusinessLogic;
	protected boolean merge;
	protected ImportResult importResult;
	protected User user;

	protected AbstractImporter(ImportBusinessLogic importBusinessLogic) {
		this.importBusinessLogic = importBusinessLogic;
		logger = Logger.getLogger(getClass());
	}

	void addError(Serializable context, ImportException ex) {
		addError(context, ex.getMessage());
	}

	void addError(Serializable context, String message) {
		assert importResult != null;
		importResult.addErrorMessage(message, context);
	}

	void addErrors(Serializable context, DataValidationFailedException ex) {
		for (ValidationViolation violation : ex.getViolations()) {
			addError(context, toErrorString(violation));
		}
	}

	@Override
	public final void importData(T dataToImport, ImportResult importResult, boolean merge, User user, O optionalData) throws ImportException {
		this.importResult = importResult;
		this.merge = merge;
		this.user = user;
		processData(dataToImport, optionalData);
	}

	protected abstract void processData(T dataToImport, O optionalData) throws ImportException;

}

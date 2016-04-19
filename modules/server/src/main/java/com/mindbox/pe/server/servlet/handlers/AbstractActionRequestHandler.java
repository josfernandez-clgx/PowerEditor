package com.mindbox.pe.server.servlet.handlers;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.mindbox.pe.communication.ErrorResponse;
import com.mindbox.pe.communication.RequestComm;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.communication.ValidationErrorResponse;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.imexport.ExportException;
import com.mindbox.pe.server.imexport.ImportException;
import com.mindbox.pe.server.model.LockException;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;

public abstract class AbstractActionRequestHandler<T extends RequestComm<?>> implements IRequestCommHandler<T> {

	protected final Logger logger;

	protected AbstractActionRequestHandler() {
		this.logger = Logger.getLogger(getClass());
	}

	@Override
	public ResponseComm serviceRequest(T request, HttpServletRequest httpservletrequest) {
		try {
			return handleRequest(request, httpservletrequest);
		}
		catch (DataValidationFailedException e) {
			return generateErrorResponse(e);
		}
		catch (ServletActionException e) {
			return generateErrorResponse(e);
		}
		catch (ServerException e) {
			return generateErrorResponse(e);
		}
		catch (LockException e) {
			return generateErrorResponse(e);
		}
	}

	/**
	 * Method that provides core processing of the request.
	 * @param requestcomm
	 * @param httpservletrequest
	 * @return
	 */
	protected abstract ResponseComm handleRequest(T requestcomm, HttpServletRequest httpservletrequest)
			throws DataValidationFailedException, LockException, ServletActionException, ServerException;

	protected final ErrorResponse generateErrorResponse(LockException lockexception) {
		logger.warn("Could not lock as required", lockexception);
		return new ErrorResponse("LockError", "LockFailureMsg", new Object[] { lockexception.getLockedBy() });
	}

	protected final ErrorResponse generateErrorResponse(ServletActionException ex) {
		logger.error("Servlet action error", ex);
		ErrorResponse errorresponsecomm = new ErrorResponse(ErrorResponse.SERVER_ERROR, ex.getResourceKey(), ex.getResourceParams());
		return errorresponsecomm;
	}

	protected final ErrorResponse generateErrorResponse(ServerException ex) {
		logger.error("Server error", ex);
		ErrorResponse errorresponsecomm = new ErrorResponse(
				ErrorResponse.SERVER_ERROR,
				"msg.error.generic.service",
				new Object[] { ex.getMessage() });

		return errorresponsecomm;
	}

	protected final ErrorResponse generateErrorResponse(ImportException ex) {
		logger.error("Import error", ex);
		ErrorResponse errorresponsecomm = new ErrorResponse(
				ErrorResponse.SERVER_ERROR,
				"msg.error.generic.service",
				new Object[] { ex.getMessage() });
		return errorresponsecomm;
	}

	protected final ErrorResponse generateErrorResponse(ExportException ex) {
		logger.error("Export error", ex);
		ErrorResponse errorresponsecomm = new ErrorResponse(
				ErrorResponse.SERVER_ERROR,
				"msg.error.generic.service",
				new Object[] { ex.getMessage() });
		return errorresponsecomm;
	}

	protected final ErrorResponse generateAuthorizationFailureResponse() {
		return new ErrorResponse(ErrorResponse.AUTHORIZATION_FAILURE, "AuthorizationFailureMsg", null);
	}

	protected final ErrorResponse generateErrorResponse(DataValidationFailedException ex) {
		logger.info("Validation error", ex);
		return new ValidationErrorResponse(ex.getMessage(), ex.getViolations());
	}

	/**
	 * Convenience method to get the user object
	 * @param s
	 * @return
	 */
	protected final User getUser(String userId) {
		return SecurityCacheManager.getInstance().getUser(userId);
	}
}
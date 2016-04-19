package com.mindbox.pe.communication;

import java.io.BufferedOutputStream;
import java.net.URL;
import java.net.URLConnection;

import com.mindbox.pe.common.timeout.TimeOutController;

/**
 * T is the expected response class, that must be an instance of {@link ResponseComm}.
 * @author Geneho Kim
 * @since PowerEditor
 */
public abstract class RequestComm<T extends ResponseComm> extends SapphireComm<T> {

	private static final long serialVersionUID = 2003051917438001L;
	private static String servletURL = null;

	public static void setServletURL(String s) {
		servletURL = s;
	}

	/**
	 * Validates the specified response.
	 * Note: This must not be invoked from the server.
	 * @param responsecomm the response to check
	 * @throws ServerException if the response is an error response
	 * @throws CommunicationException if the reponse is not of the expected type
	 */
	private void checkForError(final ResponseComm responsecomm) throws ServerException {
		if (responsecomm instanceof ErrorResponse) {
			final ErrorResponse errorresponsecomm = ErrorResponse.class.cast(responsecomm);

			if (errorresponsecomm.getErrorType().equals(ErrorResponse.AUTHENTICATION_FAILURE)) {
				throw new AuthenticaionFailedException(errorresponsecomm);
			}
			else if (errorresponsecomm.getErrorType().equals(ErrorResponse.AUTHORIZATION_FAILURE)) {
				throw new AuthorizationFailedException(errorresponsecomm);
			}
			else if (errorresponsecomm.getErrorType().equals(ErrorResponse.LOCK_ERROR) || errorresponsecomm.getErrorType().equals("LockFailureMsg"))
				throw new LockException(errorresponsecomm.getErrorResourceKey(), errorresponsecomm.getErrorResourceParams());
			else if (errorresponsecomm.getErrorType().equals(ErrorResponse.VALIDATION_ERROR)) {
				throw new ValidationException(errorresponsecomm.getViolations());
			}
			else if (errorresponsecomm.getErrorType().equals(ErrorResponse.SERVER_RESTARTED_ERROR))
				throw new OperationFailedException(errorresponsecomm);
			else if (errorresponsecomm.getErrorType().equals(ErrorResponse.SERVER_ERROR))
				throw new OperationFailedException(errorresponsecomm);
			else {
				throw new OperationFailedException("UnknownErrorMsg", null);
			}
		}
	}

	/**
	 * Sends this request to the server and returns a response.
	 * Note: This must not be invoked from the server.
	 * @return the response from the server. Never <code>null</code>
	 * @throws CommunicationException
	 */
	@SuppressWarnings("unchecked")
	public synchronized T sendRequest(final TimeOutController timeOutController) throws ServerException {
		assert (servletURL == null) : "SERVLET URL NOT SET FROM APPLET!";
		try {
			final URL url = new URL(servletURL);
			final URLConnection urlconnection = url.openConnection();
			urlconnection.setDoInput(true);
			urlconnection.setDoOutput(true);
			urlconnection.setUseCaches(false);
			urlconnection.setDefaultUseCaches(false);
			urlconnection.setRequestProperty("Content-Type", "application/octet-stream");
			final BufferedOutputStream bufferedoutputstream = new BufferedOutputStream(urlconnection.getOutputStream());
			serializeOut(bufferedoutputstream);

			if (timeOutController != null) {
				timeOutController.restartTimer();
			}

			final ResponseComm responsecomm = serializeIn(urlconnection.getInputStream());
			checkForError(responsecomm);

			return (T) responsecomm;
		}
		catch (ServerException ex) {
			throw ex;
		}
		catch (Exception exception) {
			exception.printStackTrace(System.err);
			throw new CommunicationException(exception.getMessage());
		}
	}
}
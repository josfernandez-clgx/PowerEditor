package com.mindbox.pe.communication;

import java.io.BufferedOutputStream;
import java.net.URL;
import java.net.URLConnection;

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
	 * Sends this request to the server and returns a response.
	 * Note: This must not be invoked from the server.
	 * @return the response from the server. Never <code>null</code>
	 * @throws CommunicationException
	 */
	public synchronized T sendRequest() throws ServerException {
		assert (servletURL == null) : "SERVLET URL NOT SET FROM APPLET!";
		try {
			URL url = new URL(servletURL);
			URLConnection urlconnection = url.openConnection();
			urlconnection.setDoInput(true);
			urlconnection.setDoOutput(true);
			urlconnection.setUseCaches(false);
			urlconnection.setDefaultUseCaches(false);
			urlconnection.setRequestProperty("Content-Type", "application/octet-stream");
			BufferedOutputStream bufferedoutputstream = new BufferedOutputStream(urlconnection.getOutputStream());
			serializeOut(bufferedoutputstream);

			T responsecomm = serializeIn(urlconnection.getInputStream());
			checkForError(responsecomm);
			return responsecomm;
		}
		catch (ServerException ex) {
			throw ex;
		}
		catch (Exception exception) {
			System.err.println("Exception occurred while communicating with the server");
			exception.printStackTrace(System.err);
			throw new CommunicationException(exception.getMessage());
		}
	}

	/**
	 * Validates the specified response.
	 * Note: This must not be invoked from the server.
	 * @param responsecomm the response to check
	 * @throws ServerException if the response is an error response
	 * @throws CommunicationException if the reponse is not of the expected type
	 */
	private void checkForError(T responsecomm) throws ServerException {
		if (responsecomm instanceof ErrorResponse) {
			ErrorResponse errorresponsecomm = (ErrorResponse) responsecomm;

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
}
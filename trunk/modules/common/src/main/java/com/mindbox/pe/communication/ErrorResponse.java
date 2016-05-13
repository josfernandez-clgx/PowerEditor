package com.mindbox.pe.communication;

import java.util.List;

import com.mindbox.pe.common.validate.ValidationViolation;



/**
 * Response to report errors.
 * There are two flavors of ErrorResponse, available via two constructors:
 * {@link #ErrorResponse(String, String)} and {@link #ErrorResponse(String, String, Object[])}. 
 * Be sure to use the correct one.
 * @author Geneho Kim
 * @since PowerEditor
 */
public class ErrorResponse extends AbstractSimpleResponse {

	public static final String AUTHENTICATION_FAILURE = "AuthenticationFailure";
	public static final String AUTHORIZATION_FAILURE = "AuthorizationFailure";
	public static final String INVALID_REQUEST_ERROR = "InvalidRequestError";
	public static final String LOCK_ERROR = "LockError";
	public static final String SERVER_RESTARTED_ERROR = "ServerRestartError";
	public static final String SERVER_ERROR = "ServerError";
	public static final String UNKNOWN_ERROR = "UnknownError";
	public static final String VALIDATION_ERROR = "ValidationError";


	private static final long serialVersionUID = 2003052312099009L;

	private final String errorType;
	private final String errorResourceId;
	private final Object errorResourceParams[];
	private final String errorMsg;

	/**
	 * Creates new error response that contains message key and error message.
	 * The specified error message shall be displayed to the user as is.
	 * @param errorType error type; must be one of message type constants
	 * @param message error message
	 */
	public ErrorResponse(String errorType, String message) {
		this(errorType, message, null, null);
	}

	/**
	 * Creates new error response that uses messages in resource bundle.
	 * Message constructed using resource key and resource parameters shall be displayed to the user
	 * @param errorType  error type; must be one of message type constants
	 * @param resourceKey message key
	 * @param resourceParams parameters to the message; all elements must implement <code>java.io.Serializable</code>; can be <code>null</code>
	 */
	public ErrorResponse(String errorType, String resourceKey, Object[] resourceParams) {
		this(errorType, null, resourceKey, resourceParams);
	}

	private ErrorResponse(String errorType, String message, String resourceKey, Object[] resourceParams) {
		this.errorType = errorType;
		this.errorMsg = message;
		this.errorResourceId = resourceKey;
		this.errorResourceParams = resourceParams;
	}

	public List<ValidationViolation> getViolations() {
		return null;
	}
	
	public boolean hasMessageResource() {
		return (errorResourceId != null);
	}

	public String getErrorMessage() {
		return errorMsg;
	}

	public String getErrorType() {
		return errorType;
	}

	public String getErrorResourceKey() {
		return errorResourceId;
	}

	public Object[] getErrorResourceParams() {
		return errorResourceParams;
	}

	public String toString() {
		return "ErrorResponse[" + errorType + ";key=" + errorResourceId + ",msg=" + errorMsg + "]";
	}

}
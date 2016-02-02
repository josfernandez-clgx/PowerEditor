package com.mindbox.pe.communication;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public class CommunicationException extends OperationFailedException {

	private static final long serialVersionUID = -7859530365362523084L;

	/**
	 * @param reason reason for communication error
	 */
	public CommunicationException(String reason) {
		super("msg.error.generic.service", new Object[]{reason});
	}

}

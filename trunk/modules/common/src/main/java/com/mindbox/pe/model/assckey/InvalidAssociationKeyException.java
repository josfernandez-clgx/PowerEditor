package com.mindbox.pe.model.assckey;

/**
 * Indicates that an invalid instance of {@link AssociationKey} is detected.
 * @author Geneho Kim
 * @since 5.1.0
 */
public class InvalidAssociationKeyException extends RuntimeException {

	private static final long serialVersionUID = 20061206100002L;
	
	public InvalidAssociationKeyException() {
		super();
	}

	public InvalidAssociationKeyException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public InvalidAssociationKeyException(String arg0) {
		super(arg0);
	}

	public InvalidAssociationKeyException(Throwable arg0) {
		super(arg0);
	}

}

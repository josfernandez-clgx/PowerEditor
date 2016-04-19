package com.mindbox.pe.server.report;


/**
 * Runtime report exception.
 * @author Geneho Kim
 * @since PowerEditor 4.1.1
 */
public class RuntimeReportException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 822942104954136493L;

	public RuntimeReportException(String msg) {
		super(msg);
	}
	
	public RuntimeReportException(Throwable t) {
		super(t);
	}
}

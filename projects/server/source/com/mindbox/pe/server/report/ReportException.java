package com.mindbox.pe.server.report;

import com.mindbox.pe.server.ServerException;


/**
 * Thrown to indicate an error related to reporting.
 * @author Geneho Kim
 * @since PowerEditor 4.1.1
 */
public class ReportException extends ServerException {

	private static final long serialVersionUID = -7269939922729190727L;

	public ReportException(String msg) {
		super(msg);
	}
}

/*
 * Created on 2005. 1. 24.
 *
 */
package com.mindbox.pe.communication;


/**
 * Response to import request.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public class ImportResponse extends ResponseComm {

	private static final long serialVersionUID = 2005012409571000L;

	private final ImportResult importResult;

	public ImportResponse(ImportResult importResult) {
		if (importResult == null) throw new NullPointerException("importResult cannot be null");
		this.importResult = importResult;
	}

	public ImportResult getImportResult() {
		return importResult;
	}
}
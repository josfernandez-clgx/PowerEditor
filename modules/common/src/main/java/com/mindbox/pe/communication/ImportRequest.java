package com.mindbox.pe.communication;

import com.mindbox.pe.model.ImportSpec;


/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class ImportRequest extends SessionRequest<ImportResponse> {

	private static final long serialVersionUID = 20060123700001L;

	private final ImportSpec importSpec;

	public ImportRequest(String userID, String sessionID, ImportSpec importSpec) {
		super(userID, sessionID);
		this.importSpec = importSpec;
	}

	public ImportSpec getImportSpec() {
		return importSpec;
	}
}

package com.mindbox.pe.server.servlet.handlers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.mindbox.pe.communication.ImportRequest;
import com.mindbox.pe.communication.ImportResponse;
import com.mindbox.pe.communication.ResponseComm;
import com.mindbox.pe.model.ImportSpec;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.imexport.ImportException;

/**
 * @author Geneho Kim
 * @author MindBox
 */
public class ImportRequestHandler extends AbstractSessionRequestHandler<ImportRequest> {

	public ResponseComm handleRequest(ImportRequest request, HttpServletRequest httpservletrequest) {
		long startTime = System.currentTimeMillis();

		ImportSpec importSpec = request.getImportSpec();
		String[] filenames = importSpec.getFilenames();
		try {
			switch (importSpec.getImportRequestType()) {
			case ImportSpec.IMPORT_ADHOC_RULES_REQUEST: {
				ImportResponse response = new ImportResponse(BizActionCoordinator.getInstance().importAdHocRuleData(
						importSpec.getContent(filenames[0]),
						importSpec.getContent(filenames[1]),
						getUser(request.getUserID())));
				response.getImportResult().setElapsedTime(System.currentTimeMillis() - startTime);
				return response;
			}
			case ImportSpec.IMPORT_ADHOC_ACTIONS_REQUEST: {
				ImportResponse response = new ImportResponse(BizActionCoordinator.getInstance().importAdHocActionData(
						importSpec.getContent(filenames[0]),
						getUser(request.getUserID())));
				response.getImportResult().setElapsedTime(System.currentTimeMillis() - startTime);
				return response;
			}
			default: {
				ImportResponse response = new ImportResponse(BizActionCoordinator.getInstance().importData(
						importSpec,
						getUser(request.getUserID())));
				response.getImportResult().setElapsedTime(System.currentTimeMillis() - startTime);
				return response;
			}
			}
		}
		catch (ImportException ex) {
			logger.error("Faield to import", ex);
			return generateErrorResponse(ex);
		}
		catch (IOException e) {
			logger.error("IO Error on import", e);
			return generateErrorResponse(new ImportException(e.getMessage()));
		}
	}

}
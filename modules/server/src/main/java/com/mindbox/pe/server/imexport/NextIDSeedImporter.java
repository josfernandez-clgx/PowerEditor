package com.mindbox.pe.server.imexport;

import java.util.List;

import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.db.DBIdGenerator;
import com.mindbox.pe.xsd.data.NextIDDataElement;
import com.mindbox.pe.xsd.data.NextIDDataElement.NextId;

final class NextIDSeedImporter extends AbstractImporter<NextIDDataElement, String> {

	private static final String asErrorContext(final NextId nextId) {
		return String.format("NextId[type=%s,seed=%s]", nextId.getType(), nextId.getSeed());
	}

	protected NextIDSeedImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	@Override
	protected void processData(NextIDDataElement dataToImport, String optionalData) throws ImportException {
		int count = processNextIDSeeds(dataToImport.getNextId());
		if (count > 0) {
			importResult.addMessage("  Imported " + count + " next ID seeds", "");
		}
	}

	private int processNextIDSeeds(List<NextId> list) {
		int count = 0;
		for (final NextId nextId : list) {
			// TT 1773: only use SEQUENTIAL, Filter and Grid
			if (DBIdGenerator.isSupportedIdType(nextId.getType())) {
				try {
					logger.debug("processNextIDSeeds: saving " + nextId);
					importBusinessLogic.importNextID(nextId);
					++count;
				}
				catch (ImportException ex) {
					addError(asErrorContext(nextId), ex);
				}
				catch (DataValidationFailedException ex) {
					addErrors(asErrorContext(nextId), ex);
				}
			}
		}
		return count;
	}
}

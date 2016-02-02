package com.mindbox.pe.server.imexport;

import java.util.List;

import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.db.DBIdGenerator;
import com.mindbox.pe.server.imexport.digest.NextIDSeed;

final class NextIDSeedImporter extends AbstractImporter<String> {

	protected NextIDSeedImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	@Override
	protected void processData(String filename, DigestedObjectHolder objectHolder, String optionalData) throws ImportException {
		int count = processNextIDSeeds(objectHolder.getObjects(NextIDSeed.class));
		if (count > 0) importResult.addMessage("  Imported " + count + " next ID seeds", "File: " + filename);
	}

	private int processNextIDSeeds(List<NextIDSeed> list) {
		int count = 0;
		for (int i = 0; i < list.size(); i++) {
			NextIDSeed nextIDSeed = list.get(i);
			// TT 1773: only use SEQUENTIAL, Filter and Grid
			if (DBIdGenerator.isSupportedIdType(nextIDSeed.getType())) {
				try {
					logger.debug("processNextIDSeeds: saving " + nextIDSeed);
					importBusinessLogic.importNextID(nextIDSeed);
					++count;
				}
				catch (ImportException ex) {
					addError(nextIDSeed, ex);
				}
				catch (DataValidationFailedException ex) {
					addErrors(nextIDSeed, ex);
				}
			}
		}
		return count;
	}
}

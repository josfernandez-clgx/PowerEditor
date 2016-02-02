package com.mindbox.pe.server.imexport;

import java.util.Iterator;
import java.util.List;

import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.imexport.digest.Entity;

final class CompatilibytLinkImporter extends AbstractImporter<EntityImportOptionalData> {

	protected CompatilibytLinkImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	@Override
	protected void processData(String filename, DigestedObjectHolder objectHolder, EntityImportOptionalData optionalData)
			throws ImportException {
		int count = processCompatibilityLinks(objectHolder, optionalData);
		if (count > 0) {
			importResult.addMessage("  Imported " + count + " compatibility data", "File: " + filename);
		}
	}

	private int processCompatibilityLinks(DigestedObjectHolder objectHolder, EntityImportOptionalData optionalData) throws ImportException {
		logger.debug(">>> processCompatibilityLinks: " + objectHolder);
		int count = 0;
		List<Entity> list = objectHolder.getObjects(Entity.class);
		for (Entity entity : list) {
			if (entity.isImported()) {
				GenericEntityType type = GenericEntityType.forName(entity.getType());
				if (type != null) {
					List<GenericEntityCompatibilityData> compList;
					try {
						compList = ObjectConverter.extractCompabilityLinks(
								type,
								entity,
								merge,
								optionalData.getEntityIDMap(),
								optionalData.getDateSynonymIDMap(),
								optionalData.getReplacementDateSynonymProvider(),
								user);
						count += importCompatibilityLinks(compList);
					}
					catch (DataValidationFailedException e) {
						addErrors(entity, e);
					}
				}
			}
		}
		logger.debug("<<< processCompatibilityLinks: " + count);
		return count;
	}

	private int importCompatibilityLinks(List<GenericEntityCompatibilityData> compList) {
		int count = 0;
		for (Iterator<GenericEntityCompatibilityData> iter = compList.iterator(); iter.hasNext();) {
			GenericEntityCompatibilityData element = iter.next();
			try {
				importBusinessLogic.importEntityCompatibilityData(element, user);
				++count;
			}
			catch (ImportException ex) {
				addError(element, ex);
			}
			catch (DataValidationFailedException ex) {
				addErrors(element, ex);
			}
		}
		return count;
	}

}

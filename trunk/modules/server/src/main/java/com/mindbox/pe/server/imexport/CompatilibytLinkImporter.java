package com.mindbox.pe.server.imexport;

import java.util.Iterator;
import java.util.List;

import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.xsd.data.EntityDataElement;

final class CompatilibytLinkImporter extends AbstractImporter<EntityDataElement, EntityImportOptionalData> {

	protected CompatilibytLinkImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	private int importCompatibilityLinks(List<GenericEntityCompatibilityData> compList) {
		int count = 0;
		for (Iterator<GenericEntityCompatibilityData> iter = compList.iterator(); iter.hasNext();) {
			final GenericEntityCompatibilityData element = iter.next();
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

	private int processCompatibilityLinks(final List<com.mindbox.pe.xsd.data.EntityDataElement.Entity> entityList, final EntityImportOptionalData optionalData) throws ImportException {
		int count = 0;
		for (com.mindbox.pe.xsd.data.EntityDataElement.Entity entity : entityList) {
			if (optionalData.isImported(entity.getId())) {
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
						addErrors(EntityDataImporter.asErrorContext(entity), e);
					}
				}
			}
		}
		logger.debug("<<< processCompatibilityLinks: " + count);
		return count;
	}

	@Override
	protected void processData(EntityDataElement dataToImport, EntityImportOptionalData optionalData) throws ImportException {
		int count = processCompatibilityLinks(dataToImport.getEntity(), optionalData);
		if (count > 0) {
			importResult.addMessage("  Imported " + count + " compatibility data", "");
		}
	}

}

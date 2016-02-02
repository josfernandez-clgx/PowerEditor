package com.mindbox.pe.server.imexport;

import java.util.List;

import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.model.comparator.IDObjectComparator;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.TestTypeDefinition;

final class GuidelineActionTestConditionImporter extends AbstractImporter<TemplateImportOptionalData> {

	protected GuidelineActionTestConditionImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	@Override
	protected void processData(String filename, DigestedObjectHolder objectHolder, TemplateImportOptionalData optionalData)
			throws ImportException {
		int[] counts = processActionsTestConditions(objectHolder, optionalData);
		if (counts[0] > 0) {
			importResult.addMessage("  Imported " + counts[0] + " guideline actions", "File: " + filename);
		}
		if (counts[1] > 0) {
			importResult.addMessage("  Imported " + counts[1] + " guideline test conditions", "File: " + filename);
		}
	}

	private int[] processActionsTestConditions(DigestedObjectHolder objectHolder, TemplateImportOptionalData optionalData)
			throws ImportException {
		logger.debug(">>> processActionsTestConditions: merge=" + merge);
		int actionCount = 0;
		int testCount = 0;
		try {
			// [1] import guideline actions, if any
			List<ActionTypeDefinition> actionList = objectHolder.getObjects(
					ActionTypeDefinition.class,
					new IDObjectComparator<ActionTypeDefinition>());
			for (ActionTypeDefinition element : actionList) {
				try {
					importBusinessLogic.importActionTypeDefinition(element, merge, optionalData.getActionIDMap(), user, true);
					++actionCount;
				}
				catch (Exception ex) {
					logger.error("Failed to import guideline action: " + element, ex);
					addError(element, new ImportException(ex.getMessage()));
				}
			}

			// [2] import test codnitions, if any
			List<TestTypeDefinition> testList = objectHolder.getObjects(
					TestTypeDefinition.class,
					new IDObjectComparator<TestTypeDefinition>());
			for (TestTypeDefinition element : testList) {
				try {
					importBusinessLogic.importTestTypeDefinition(element, merge, optionalData.getActionIDMap(), user, true);
					++testCount;
				}
				catch (Exception ex) {
					logger.error("Failed to import test condition: " + element, ex);
					addError(element, new ImportException(ex.getMessage()));
				}
			}
			return new int[] { actionCount, testCount };
		}
		catch (Exception ex) {
			logger.error("Failed to import", ex);
			throw new ImportException(ex.getMessage());
		}
	}

}

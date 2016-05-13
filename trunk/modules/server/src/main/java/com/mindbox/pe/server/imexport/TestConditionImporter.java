package com.mindbox.pe.server.imexport;

import com.mindbox.pe.xsd.data.TestConditionDataElement;
import com.mindbox.pe.xsd.data.TestConditionDataElement.TestCondition;

final class TestConditionImporter extends AbstractImporter<TestConditionDataElement, TemplateImportOptionalData> {

	private static String asErrorContext(final TestCondition testCondition) {
		return String.format("TestCondition[id=%s,name=%s]", testCondition.getId(), testCondition.getName());
	}

	protected TestConditionImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	@Override
	protected void processData(final TestConditionDataElement dataToImport, final TemplateImportOptionalData optionalData) throws ImportException {
		if (dataToImport != null && dataToImport.getTestCondition() != null) {
			int count = processTestConditions(dataToImport, optionalData);
			if (count > 0) {
				importResult.addMessage("  Imported " + count + " test conditions", "");
			}
		}
		else {
			logger.info("No test condition to import.");
		}
	}

	private int processTestConditions(final TestConditionDataElement testConditionDataElement, final TemplateImportOptionalData optionalData) throws ImportException {
		logger.debug(">>> processTestConditions: merge=" + merge);
		int count = 0;
		try {
			for (TestCondition element : testConditionDataElement.getTestCondition()) {
				try {
					importBusinessLogic.importTestCondition(element, merge, optionalData.getActionIDMap(), user, true);
					++count;
				}
				catch (Exception ex) {
					logger.error("Failed to import guideline action: " + element, ex);
					addError(asErrorContext(element), new ImportException(ex.getMessage()));
				}
			}
			return count;
		}
		catch (Exception ex) {
			logger.error("Failed to import", ex);
			throw new ImportException(ex.getMessage());
		}
	}

}

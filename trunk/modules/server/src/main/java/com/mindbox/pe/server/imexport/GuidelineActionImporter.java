package com.mindbox.pe.server.imexport;

import com.mindbox.pe.xsd.data.GuidelineActionDataElement;
import com.mindbox.pe.xsd.data.GuidelineActionDataElement.GuidelineAction;

final class GuidelineActionImporter extends AbstractImporter<GuidelineActionDataElement, TemplateImportOptionalData> {

	private static String asErrorContext(final GuidelineAction guidelineAction) {
		return String.format("GuidelineAction[id=%s,name=%s]", guidelineAction.getId(), guidelineAction.getName());
	}

	protected GuidelineActionImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	@Override
	protected void processData(final GuidelineActionDataElement dataToImport, final TemplateImportOptionalData optionalData) throws ImportException {
		if (dataToImport != null && dataToImport.getGuidelineAction() != null) {
			int count = processGuidelineActions(dataToImport, optionalData);
			if (count > 0) {
				importResult.addMessage("  Imported " + count + " guideline actions", "");
			}
		}
		else {
			logger.info("No guideline action to import.");
		}
	}

	private int processGuidelineActions(final GuidelineActionDataElement guidelineActionDataElement, final TemplateImportOptionalData optionalData) throws ImportException {
		if (logger.isDebugEnabled()) {
			logger.debug(">>> processGuidelineActions: merge=" + merge);
		}
		try {
			int count = 0;
			for (GuidelineAction element : guidelineActionDataElement.getGuidelineAction()) {
				try {
					importBusinessLogic.importGuidelineAction(element, merge, optionalData.getActionIDMap(), user, true);
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

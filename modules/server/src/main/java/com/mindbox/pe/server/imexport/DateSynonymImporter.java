package com.mindbox.pe.server.imexport;

import java.util.List;
import java.util.Map;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.xsd.data.DateDataElement;
import com.mindbox.pe.xsd.data.DateDataElement.DateElement;

final class DateSynonymImporter extends AbstractImporter<DateDataElement, Map<Integer, Integer>> {

	private static String asErrorContext(final DateElement dateElement) {
		return String.format("Date[id=%s,date=%s]", dateElement.getId(), dateElement.getDate());
	}

	protected DateSynonymImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	@Override
	protected void processData(final DateDataElement dataToImport, final Map<Integer, Integer> dateSynonymIDMap) throws ImportException {
		final int count = processDateSynonyms(dataToImport.getDateElement(), dateSynonymIDMap);
		if (count > 0) {
			importResult.addMessage("  Imported " + count + " date synonyms", "");
		}
	}

	private int processDateSynonyms(final List<DateElement> list, final Map<Integer, Integer> dateSynonymIDMap) {
		int count = 0;
		for (final DateElement dateElement : list) {
			try {
				// if the date synonym has no date, throw exception
				if (dateElement.getDate() == null) {
					throw new ImportException("Failed to import; data validation failed: date cannot be empty");
				}

				final DateSynonym existingDS = DateSynonymManager.getInstance().getDateSynonym(dateElement.getName(), dateElement.getDate());
				if (existingDS != null) {
					addError(
							asErrorContext(dateElement),
							new ImportException(String.format("Skipped date synonym %s because the date synonym %s already exists with the same date", asErrorContext(dateElement), existingDS)));
					dateSynonymIDMap.put(dateElement.getId(), existingDS.getID());
				}
				else {
					int originalID = dateElement.getId();

					int id = importBusinessLogic.importDateSynonym(dateElement, merge, user);
					if (id != originalID) {
						dateSynonymIDMap.put(new Integer(originalID), new Integer(id));
					}
					++count;
				}
			}
			catch (ImportException ex) {
				addError(asErrorContext(dateElement), ex);
			}
			catch (DataValidationFailedException ex) {
				addErrors(asErrorContext(dateElement), ex);
			}
		}
		return count;
	}

}

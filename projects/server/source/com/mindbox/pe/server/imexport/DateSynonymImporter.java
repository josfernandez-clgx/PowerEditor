package com.mindbox.pe.server.imexport;

import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.cache.DateSynonymManager;

final class DateSynonymImporter extends AbstractImporter<Map<Integer, Integer>> {

	protected DateSynonymImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	@Override
	protected void processData(String filename, DigestedObjectHolder objectHolder, Map<Integer, Integer> dateSynonymIDMap)
			throws ImportException {
		int count = processDateSynonyms(objectHolder.getObjects(DateSynonym.class), dateSynonymIDMap);
		if (count > 0) {
			importResult.addMessage("  Imported " + count + " date synonyms", "File: " + filename);
		}
	}

	private int processDateSynonyms(List<DateSynonym> list, Map<Integer, Integer> dateSynonymIDMap) {
		int count = 0;
		for (int i = 0; i < list.size(); i++) {
			DateSynonym dateSynonym = list.get(i);
			try {
				// if the date synonym has no date, throw exception
				if (dateSynonym.getDate() == null)
					throw new ImportException("Failed to import; data validation failed: date cannot be empty");

				DateSynonym existingDS = DateSynonymManager.getInstance().getDateSynonym(dateSynonym.getName(), dateSynonym.getDate());
				if (existingDS != null) {
					addError(dateSynonym, new ImportException("Skipped date synonym " + dateSynonym + " because the date synonym "
							+ existingDS + " already exists with the same date"));
					dateSynonymIDMap.put(new Integer(dateSynonym.getID()), new Integer(existingDS.getID()));
				}
				else {
					int originalID = dateSynonym.getID();
					int id = importBusinessLogic.importDateSynonym(dateSynonym, user);
					if (id != originalID) {
						dateSynonymIDMap.put(new Integer(originalID), new Integer(id));
					}
					++count;
				}
			}
			catch (ImportException ex) {
				addError(dateSynonym, ex);
			}
			catch (DataValidationFailedException ex) {
				addErrors(dateSynonym, ex);
			}
		}
		return count;
	}

}

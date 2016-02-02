package com.mindbox.pe.server.imexport;

import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.model.CBRAttribute;
import com.mindbox.pe.model.CBRAttributeValue;
import com.mindbox.pe.model.CBRCaseAction;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.server.cache.CBRManager;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.imexport.digest.CBRCaseBaseDigest;
import com.mindbox.pe.server.imexport.digest.CBRCaseDigest;

final class CBRDataImporter extends AbstractImporter<CBRImportOptionalData> {

	private static int getMappedCaseBaseId(Map<String, Integer> cbrDataIdMap, int id) {
		if (cbrDataIdMap.containsKey("casebase-" + id)) {
			return cbrDataIdMap.get("casebase-" + id);
		}
		else {
			return id;
		}
	}

	protected CBRDataImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	@Override
	protected void processData(String filename, DigestedObjectHolder objectHolder, CBRImportOptionalData optionalData)
			throws ImportException {
		int count = processCaseBases(
				objectHolder.getObjects(CBRCaseBaseDigest.class),
				optionalData.getCbrDataIdMap(),
				optionalData.getDateSynonymIDMap());
		if (count > 0) {
			importResult.addMessage("  Imported " + count + " CBR case base(s)", "File: " + filename);
		}
		count = processCases(
				objectHolder.getObjects(CBRCaseDigest.class),
				optionalData.getCbrDataIdMap(),
				optionalData.getDateSynonymIDMap());
		if (count > 0) {
			importResult.addMessage("  Imported " + count + " CBR case(s)", "File: " + filename);
		}
		count = processAttributes(objectHolder.getObjects(CBRAttribute.class), optionalData.getCbrDataIdMap());
		if (count > 0) {
			importResult.addMessage("  Imported " + count + " CBR attribute(s)", "File: " + filename);
		}
	}

	private int processCaseBases(List<CBRCaseBaseDigest> caseBaseList, Map<String, Integer> cbrDataIdMap,
			Map<Integer, Integer> dateSynonymIDMap) throws ImportException {
		int count = 0;
		for (CBRCaseBaseDigest caseBase : caseBaseList) {
			if (caseBase.getCaseClass() == null) {
				addError(caseBase.toString(), "case class is required.");
			}
			else if (caseBase.getCaseClass().getId() == -1) {
				addError(caseBase.toString(), "No case class of id " + caseBase.getCaseClass().getSymbol() + " found");
			}
			else if (caseBase.getScoringFunction() == null) {
				addError(caseBase.toString(), "scoring function is required.");
			}
			else if (caseBase.getScoringFunction().getId() == -1) {
				addError(caseBase.toString(), "No scoring function of id " + caseBase.getScoringFunction().getSymbol() + " found");
			}
			else {
				boolean hasError = false;
				// set effective and expirate dates
				int effectiveDateId = caseBase.getActivationDates() == null || !caseBase.getActivationDates().hasEffectiveDateID()
						? -1
						: caseBase.getActivationDates().getEffectiveDateID();
				if (effectiveDateId != -1) {
					DateSynonym effectiveDateSynonym = DateSynonymManager.getInstance().getDateSynonym(
							ObjectConverter.findMappedDateSynonymID(effectiveDateId, dateSynonymIDMap));
					// If no activation date, add error
					if (effectiveDateSynonym == null) {
						addError("Effective date of " + caseBase, "No date synonym with id " + effectiveDateId + " found");
						if (!hasError) hasError = true;
					}
					else {
						caseBase.setEffectiveDate(effectiveDateSynonym);
					}
				}
				int expirationDateId = caseBase.getActivationDates() == null || !caseBase.getActivationDates().hasExpirationDateID()
						? -1
						: caseBase.getActivationDates().getExpirationDateID();
				if (expirationDateId != -1) {
					DateSynonym expirationDateSynonym = DateSynonymManager.getInstance().getDateSynonym(
							ObjectConverter.findMappedDateSynonymID(expirationDateId, dateSynonymIDMap));
					// If no activation date, add error
					if (expirationDateSynonym == null) {
						addError("Expiration date of " + caseBase, "No date synonym with id " + expirationDateId + " found");
						if (!hasError) hasError = true;
					}
					else {
						caseBase.setExpirationDate(expirationDateSynonym);
					}
				}
				if (!hasError) {
					importBusinessLogic.importCBRCaseBase(caseBase.asCaseBase(), merge, cbrDataIdMap, user);
					++count;
				}
			}
		}
		return count;
	}

	private int processCases(List<CBRCaseDigest> caseList, Map<String, Integer> cbrDataIdMap, Map<Integer, Integer> dateSynonymIDMap)
			throws ImportException {
		int count = 0;
		for (CBRCaseDigest cbrCase : caseList) {
			if (cbrCase.getCaseBase() == null) {
				addError(cbrCase.toString(), "case base is required.");
			}
			else {
				// find the correct case base for merge
				int specifiedCaseBaseId = (cbrCase.getCaseBase().getId() == -1)
						? Integer.parseInt(cbrCase.getCaseBase().getName())
						: cbrCase.getCaseBase().getId();
				if (merge) {
					cbrCase.setCaseBase(CBRManager.getInstance().getCBRCaseBase(getMappedCaseBaseId(cbrDataIdMap, specifiedCaseBaseId)));
				}

				if (cbrCase.getCaseBase() == null || cbrCase.getCaseBase().getId() == -1) {
					addError(cbrCase.toString(), "No case base of id " + specifiedCaseBaseId + " found");
				}
				else {
					boolean hasError = false;
					// check case actions
					for (CBRCaseAction action : cbrCase.getCaseActions()) {
						if (action.getId() == -1) {
							addError(cbrCase.toString(), "No case action of id " + action.getSymbol() + " found");
							if (!hasError) hasError = true;
						}
					}
					// check attribute id of attribute values
					for (CBRAttributeValue attributeValue : cbrCase.getAttributeValues()) {
						if (attributeValue.getAttribute().getId() == -1) {
							addError(attributeValue + " in " + cbrCase, "No attribute of id " + attributeValue.getAttribute().getName()
									+ " found");
							if (!hasError) hasError = true;
						}
					}
					// set effective and expirate dates
					int effectiveDateId = cbrCase.getActivationDates() == null || !cbrCase.getActivationDates().hasEffectiveDateID()
							? -1
							: cbrCase.getActivationDates().getEffectiveDateID();
					if (effectiveDateId != -1) {
						DateSynonym effectiveDateSynonym = DateSynonymManager.getInstance().getDateSynonym(
								ObjectConverter.findMappedDateSynonymID(effectiveDateId, dateSynonymIDMap));
						// If no activation date, add error
						if (effectiveDateSynonym == null) {
							addError("Effective date of " + cbrCase, "No date synonym with id " + effectiveDateId + " found");
							if (!hasError) hasError = true;
						}
						else {
							cbrCase.setEffectiveDate(effectiveDateSynonym);
						}
					}
					int expirationDateId = cbrCase.getActivationDates() == null || !cbrCase.getActivationDates().hasExpirationDateID()
							? -1
							: cbrCase.getActivationDates().getExpirationDateID();
					if (expirationDateId != -1) {
						DateSynonym expirationDateSynonym = DateSynonymManager.getInstance().getDateSynonym(
								ObjectConverter.findMappedDateSynonymID(expirationDateId, dateSynonymIDMap));
						// If no activation date, add error
						if (expirationDateSynonym == null) {
							addError("Expiration date of " + cbrCase, "No date synonym with id " + expirationDateId + " found");
							if (!hasError) hasError = true;
						}
						else {
							cbrCase.setExpirationDate(expirationDateSynonym);
						}
					}
					if (!hasError) {
						importBusinessLogic.importCBRCase(cbrCase.asCBRCase(), merge, cbrDataIdMap, user);
						++count;
					}
				}
			}
		}
		return count;
	}

	private int processAttributes(List<CBRAttribute> attributeList, Map<String, Integer> cbrDataIdMap) throws ImportException {
		int count = 0;
		for (CBRAttribute attribute : attributeList) {
			if (attribute.getAttributeType() == null) {
				addError(attribute, "attribute type is required.");
			}
			else if (attribute.getAttributeType().getId() == -1) {
				addError(attribute, "No attribute type of id " + attribute.getAttributeType().getSymbol() + " found");
			}
			else if (attribute.getCaseBase() == null) {
				addError(attribute, "case base is required.");
			}
			else if (attribute.getValueRange() != null && attribute.getValueRange().getId() == -1) {
				addError(attribute, "No value range of id " + attribute.getValueRange().getSymbol() + " found");
			}
			else {
				// find the correct case base for merge
				int specifiedCaseBaseId = (attribute.getCaseBase().getId() == -1)
						? Integer.parseInt(attribute.getCaseBase().getName())
						: attribute.getCaseBase().getId();
				if (merge) {
					attribute.setCaseBase(CBRManager.getInstance().getCBRCaseBase(getMappedCaseBaseId(cbrDataIdMap, specifiedCaseBaseId)));
				}

				if (attribute.getCaseBase() == null || attribute.getCaseBase().getId() == -1) {
					addError(attribute, "No case base of id " + specifiedCaseBaseId + " found");
				}
				else {
					importBusinessLogic.importCBRAttribute(attribute, merge, cbrDataIdMap, user);
					++count;
				}
			}
		}
		return count;
	}
}

package com.mindbox.pe.server.imexport;

import java.util.List;
import java.util.Map;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.cbr.CBRCaseBase;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.cache.CBRManager;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.xsd.data.CBRAttributeElement;
import com.mindbox.pe.xsd.data.CBRAttributeValueElement;
import com.mindbox.pe.xsd.data.CBRCaseBaseElement;
import com.mindbox.pe.xsd.data.CBRCaseElement;
import com.mindbox.pe.xsd.data.CBRCaseElement.CaseActions.CaseAction;
import com.mindbox.pe.xsd.data.CBRDataElement;

final class CBRDataImporter extends AbstractImporter<CBRDataElement, CBRImportOptionalData> {

	private static int getMappedCaseBaseId(Map<String, Integer> cbrDataIdMap, int id) {
		if (cbrDataIdMap.containsKey("casebase-" + id)) {
			return cbrDataIdMap.get("casebase-" + id);
		}
		else {
			return id;
		}
	}

	private static int getMappedAttributeId(Map<String, Integer> cbrDataIdMap, int id) {
		if (cbrDataIdMap.containsKey("attribute-" + id)) {
			return cbrDataIdMap.get("attribute-" + id);
		}
		else {
			return id;
		}
	}

	private static String asErrorContext(final CBRAttributeElement attributeElement) {
		return String.format("CBRAttribute[id=%s]", attributeElement.getId());
	}

	private static String asErrorContext(final CBRCaseBaseElement caseBaseElement) {
		return String.format("CBRCaseBase[id=%s,name=%s]", caseBaseElement.getId(), caseBaseElement.getName());
	}

	private static String asErrorContext(final CBRCaseElement caseElement) {
		return String.format("CBRCase[id=%s,name=%s]", caseElement.getId(), caseElement.getName());
	}

	protected CBRDataImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	private int processAttributes(final List<CBRAttributeElement> attributeList, final Map<String, Integer> cbrDataIdMap) throws ImportException {
		int count = 0;
		for (final CBRAttributeElement attribute : attributeList) {
			if (attribute.getAttributeType() == null) {
				addError(asErrorContext(attribute), "attribute type is required.");
			}
			else if (attribute.getAttributeType().getId() == -1) {
				addError(asErrorContext(attribute), "No attribute type of id " + attribute.getAttributeType().getId() + " found");
			}
			else if (attribute.getCaseBase() == null) {
				addError(asErrorContext(attribute), "case base is required.");
			}
			else if (attribute.getValueRange() != null && attribute.getValueRange().getId() == -1) {
				addError(asErrorContext(attribute), "No value range of id " + attribute.getValueRange().getId() + " found");
			}
			else {
				// find the correct case base for merge
				final int specifiedCaseBaseId = attribute.getCaseBase().getId();
				final CBRCaseBase cbrCaseBase = specifiedCaseBaseId == -1 ? null : CBRManager.getInstance().getCBRCaseBase(
						merge ? getMappedCaseBaseId(cbrDataIdMap, specifiedCaseBaseId) : specifiedCaseBaseId);
				if (cbrCaseBase == null) {
					addError(asErrorContext(attribute), "No case base of id " + specifiedCaseBaseId + " found");
				}
				else {
					attribute.getCaseBase().setId(cbrCaseBase.getId());

					try {
						importBusinessLogic.importCBRAttribute(attribute, merge, cbrDataIdMap, user);
						++count;
					}
					catch (ImportException ex) {
						addError(asErrorContext(attribute), ex);
					}
					catch (DataValidationFailedException ex) {
						addErrors(asErrorContext(attribute), ex);
					}

				}
			}
		}
		return count;
	}

	private int processCaseBases(final List<CBRCaseBaseElement> caseBaseList, final Map<String, Integer> cbrDataIdMap, final Map<Integer, Integer> dateSynonymIDMap) throws ImportException {
		int count = 0;
		for (final CBRCaseBaseElement caseBase : caseBaseList) {
			if (caseBase.getCaseClass() == null) {
				addError(asErrorContext(caseBase), "case class is required.");
			}
			else if (caseBase.getCaseClass().getId() == -1) {
				addError(asErrorContext(caseBase), "No case class of id " + caseBase.getCaseClass().getId() + " found");
			}
			else if (caseBase.getScoringFunction() == null) {
				addError(asErrorContext(caseBase), "scoring function is required.");
			}
			else if (caseBase.getScoringFunction().getId() == -1) {
				addError(asErrorContext(caseBase), "No scoring function of id " + caseBase.getScoringFunction().getId() + " found");
			}
			else {
				boolean hasError = false;
				// set effective and expirate dates
				final int effectiveDateId = caseBase.getActivationDates() == null || caseBase.getActivationDates().getEffectiveDateID() == null
						? -1
						: caseBase.getActivationDates().getEffectiveDateID();
				if (effectiveDateId != -1) {
					final DateSynonym effectiveDateSynonym = DateSynonymManager.getInstance().getDateSynonym(ObjectConverter.findMappedDateSynonymID(effectiveDateId, dateSynonymIDMap));
					// If no activation date, add error
					if (effectiveDateSynonym == null) {
						addError("Effective date of " + caseBase, "No date synonym with id " + effectiveDateId + " found");
						if (!hasError) {
							hasError = true;
						}
					}
					else {
						caseBase.getActivationDates().setEffectiveDateID(effectiveDateSynonym.getId());
					}
				}
				final int expirationDateId = caseBase.getActivationDates() == null || caseBase.getActivationDates().getExpirationDateID() == null
						? -1
						: caseBase.getActivationDates().getExpirationDateID();
				if (expirationDateId != -1) {
					final DateSynonym expirationDateSynonym = DateSynonymManager.getInstance().getDateSynonym(ObjectConverter.findMappedDateSynonymID(expirationDateId, dateSynonymIDMap));
					// If no activation date, add error
					if (expirationDateSynonym == null) {
						addError("Expiration date of " + caseBase, "No date synonym with id " + expirationDateId + " found");
						if (!hasError) {
							hasError = true;
						}
					}
					else {
						caseBase.getActivationDates().setExpirationDateID(expirationDateSynonym.getId());
					}
				}
				if (!hasError) {
					try {
						importBusinessLogic.importCBRCaseBase(caseBase, merge, cbrDataIdMap, user);
						++count;
					}
					catch (ImportException ex) {
						addError(asErrorContext(caseBase), ex);
					}
					catch (DataValidationFailedException ex) {
						addErrors(asErrorContext(caseBase), ex);
					}

				}
			}
		}
		return count;
	}

	private int processCases(final List<CBRCaseElement> caseList, final Map<String, Integer> cbrDataIdMap, final Map<Integer, Integer> dateSynonymIDMap) throws ImportException {
		int count = 0;
		for (final CBRCaseElement cbrCase : caseList) {
			if (cbrCase.getCaseBase() == null) {
				addError(asErrorContext(cbrCase), "case base is required.");
			}
			else {
				// find the correct case base for merge
				final int specifiedCaseBaseId = cbrCase.getCaseBase().getId();
				final CBRCaseBase cbrCaseBase = specifiedCaseBaseId < 1 ? null : CBRManager.getInstance().getCBRCaseBase(
						(merge ? getMappedCaseBaseId(cbrDataIdMap, specifiedCaseBaseId) : specifiedCaseBaseId));

				if (cbrCaseBase == null) {
					addError(asErrorContext(cbrCase), "No case base of id " + specifiedCaseBaseId + " found");
				}
				else {
					cbrCase.getCaseBase().setId(cbrCaseBase.getId());
					boolean hasError = false;
					// check case actions
					if (cbrCase.getCaseActions() != null) {
						for (CaseAction action : cbrCase.getCaseActions().getCaseAction()) {
							if (action.getId() == null || action.getId() == -1) {
								addError(asErrorContext(cbrCase), "No case action of id " + action.getId() + " found");
								if (!hasError) {
									hasError = true;
								}
							}
						}
					}

					// check attribute id of attribute values
					if (cbrCase.getAttributeValues() != null) {
						for (CBRAttributeValueElement attributeValue : cbrCase.getAttributeValues().getAttributeValue()) {
							final Integer specifiedAttributeId = (attributeValue.getAttribute() == null ? null : attributeValue.getAttribute().getId());
							if (specifiedAttributeId == null || specifiedAttributeId == -1) {
								addError(attributeValue + " in " + cbrCase, "No attribute of id " + specifiedAttributeId + " found");
								if (!hasError) {
									hasError = true;
								}
							}
							else if (merge) {
								attributeValue.getAttribute().setId(CBRManager.getInstance().getCBRAttribute(getMappedAttributeId(cbrDataIdMap, specifiedAttributeId)).getId());
							}
						}
					}

					// set effective and expirate dates
					final int effectiveDateId = cbrCase.getActivationDates() == null || cbrCase.getActivationDates().getEffectiveDateID() == null
							? -1
							: cbrCase.getActivationDates().getEffectiveDateID();
					if (effectiveDateId != -1) {
						DateSynonym effectiveDateSynonym = DateSynonymManager.getInstance().getDateSynonym(ObjectConverter.findMappedDateSynonymID(effectiveDateId, dateSynonymIDMap));
						// If no activation date, add error
						if (effectiveDateSynonym == null) {
							addError("Effective date of " + cbrCase, "No date synonym with id " + effectiveDateId + " found");
							if (!hasError) {
								hasError = true;
							}
						}
						else {
							cbrCase.getActivationDates().setEffectiveDateID(effectiveDateSynonym.getId());
						}
					}
					final int expirationDateId = cbrCase.getActivationDates() == null || cbrCase.getActivationDates().getExpirationDateID() == null
							? -1
							: cbrCase.getActivationDates().getExpirationDateID();
					if (expirationDateId != -1) {
						DateSynonym expirationDateSynonym = DateSynonymManager.getInstance().getDateSynonym(ObjectConverter.findMappedDateSynonymID(expirationDateId, dateSynonymIDMap));
						// If no activation date, add error
						if (expirationDateSynonym == null) {
							addError("Expiration date of " + cbrCase, "No date synonym with id " + expirationDateId + " found");
							if (!hasError) {
								hasError = true;
							}
						}
						else {
							cbrCase.getActivationDates().setExpirationDateID(expirationDateSynonym.getId());
						}
					}

					if (!hasError) {
						try {
							importBusinessLogic.importCBRCase(cbrCase, merge, cbrDataIdMap, user);
							++count;
						}
						catch (ImportException ex) {
							addError(asErrorContext(cbrCase), ex);
						}
						catch (DataValidationFailedException ex) {
							addErrors(asErrorContext(cbrCase), ex);
						}
					}
				}
			}
		}
		return count;
	}

	@Override
	protected void processData(final CBRDataElement dataToImport, final CBRImportOptionalData optionalData) throws ImportException {
		int count;
		if (dataToImport != null && dataToImport.getCbrCaseBase() != null) {
			count = processCaseBases(dataToImport.getCbrCaseBase(), optionalData.getCbrDataIdMap(), optionalData.getDateSynonymIDMap());
			if (count > 0) {
				importResult.addMessage("  Imported " + count + " CBR case base(s)", "");
			}
		}
		else {
			logger.info("No CBR case base to import");
		}

		if (dataToImport != null && dataToImport.getCbrAttribute() != null) {
			count = processAttributes(dataToImport.getCbrAttribute(), optionalData.getCbrDataIdMap());
			if (count > 0) {
				importResult.addMessage("  Imported " + count + " CBR attribute(s)", "");
			}
		}
		else {
			logger.info("No CBR attribute to import");
		}
		if (dataToImport != null && dataToImport.getCbrCase() != null) {
			count = processCases(dataToImport.getCbrCase(), optionalData.getCbrDataIdMap(), optionalData.getDateSynonymIDMap());
			if (count > 0) {
				importResult.addMessage("  Imported " + count + " CBR case(s)", "");
			}
		}
		else {
			logger.info("No CBR case to import");
		}
	}
}

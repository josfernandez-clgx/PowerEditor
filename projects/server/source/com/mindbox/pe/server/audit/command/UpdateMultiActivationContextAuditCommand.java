package com.mindbox.pe.server.audit.command;

import java.util.Date;
import java.util.Map;

import com.mindbox.pe.common.ContextUtil;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.AbstractGrid;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.server.Util;
import com.mindbox.pe.server.audit.AuditConstants;
import com.mindbox.pe.server.audit.AuditException;
import com.mindbox.pe.server.audit.AuditStorage;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.spi.ServiceException;
import com.mindbox.pe.server.spi.audit.AuditEventType;

public final class UpdateMultiActivationContextAuditCommand<T extends AbstractGrid<?>> extends AbstractAuditCommandWithUser {

	/**
	 * @param id category or entity ID
	 * @param genericEntityType type
	 * @param b if for entity
	 * @return the entity name + description in paranteses
	 */
	private static String getEntityOrCatDescription(int id, GenericEntityType type, boolean forEntity) {
		String description = "";
		if (forEntity) {
			description = EntityManager.getInstance().getEntity(type, id).getName();
		}
		else {
			description = EntityManager.getInstance().getGenericCategory(type.getCategoryType(), id).getName();
		}
		return description + " (" + type.getDisplayName() + ")";
	}

	private Map<T,GuidelineContext[]> gridOldContextMap;
	private GuidelineContext[] newContexts;

	public UpdateMultiActivationContextAuditCommand(Map<T,GuidelineContext[]> gridOldContextMap, GuidelineContext[] newContexts, Date date, String userID) {
		super(AuditEventType.KB_MOD, date, userID);
		this.gridOldContextMap = gridOldContextMap;
		this.newContexts = newContexts;
		if (this.newContexts == null) this.newContexts = new GuidelineContext[0];
	}

	public String getDescription() {
		return "Update Context event for multiple grids, newContext=" + Util.toString(newContexts);
	}

	protected void buildAuditData(AuditStorage auditStorage, AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		for (Map.Entry<T,GuidelineContext[]> element : gridOldContextMap.entrySet()) {
			GuidelineContext[] oldContexts = element.getValue();
			addAuditKBMaster(
					element.getKey(),
					(oldContexts == null ? new GuidelineContext[0] : oldContexts),
					auditStorage,
					auditDataBuilder);
		}
	}

	private void addAuditKBMaster(T grid, GuidelineContext[] oldContexts, AuditStorage auditStorage,
			AuditDataBuilder auditDataBuilder) throws AuditException, ServiceException {
		if (GuidelineContext.isIdentical(oldContexts, newContexts)) return;
		auditDataBuilder.insertAuditMasterLog(auditStorage.getNextAuditID(), (grid.isParameterGrid()
				? AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION
				: AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION), grid.getID());
		// find contexts of same type, find differences
		for (int i = 0; i < newContexts.length; i++) {
			GuidelineContext newContext = newContexts[i];
			boolean foundMatch = false;
			for (int j = 0; j < oldContexts.length && !foundMatch; j++) {
				GuidelineContext oldContext = oldContexts[j];
				if (ContextUtil.contextOfSameType(newContext, oldContext)) {
					foundMatch = true;
					if (!newContext.isContainedIn(new GuidelineContext[] { oldContext })) { // they are different
						// log differences. first find added ones
						for (int k = 0; k < newContext.getIDs().length; k++) {
							if (!UtilBase.isMember(newContext.getIDs()[k], oldContext.getIDs())) {
								int kbAuditDetailID = auditStorage.getNextAuditID();
								auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(
										kbAuditDetailID,
										AuditConstants.KB_MOD_TYPE_ADD_CONTEXT_ELEMENT,
										"Added context element "
												+ getEntityOrCatDescription(
														newContext.getIDs()[k],
														newContext.getGenericEntityTypeForContext(),
														!newContext.hasCategoryContext()) + " to " + grid.getAuditDescription());
								auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
										kbAuditDetailID,
										(newContext.hasCategoryContext()
												? AuditConstants.KB_ELEMENT_TYPE_CATEGORY_ID
												: AuditConstants.KB_ELEMENT_TYPE_ENTITY_ID),
										CategoryOrEntityValue.asString(
												newContext.getGenericEntityTypeForContext(),
												!newContext.hasCategoryContext(),
												newContext.getIDs()[k]));
							}
						}
						// log differences. now find ones removed
						for (int k = 0; k < oldContext.getIDs().length; k++) {
							if (!UtilBase.isMember(oldContext.getIDs()[k], newContext.getIDs())) {
								int kbAuditDetailID = auditStorage.getNextAuditID();
								auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(
										kbAuditDetailID,
										AuditConstants.KB_MOD_TYPE_REMOVE_CONTEXT_ELEMENT,
										"Removed context element "
												+ getEntityOrCatDescription(
														oldContext.getIDs()[k],
														oldContext.getGenericEntityTypeForContext(),
														!oldContext.hasCategoryContext()) + " to " + grid.getAuditDescription());
								auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
										kbAuditDetailID,
										(oldContext.hasCategoryContext()
												? AuditConstants.KB_ELEMENT_TYPE_CATEGORY_ID
												: AuditConstants.KB_ELEMENT_TYPE_ENTITY_ID),
										CategoryOrEntityValue.asString(
												oldContext.getGenericEntityTypeForContext(),
												!newContext.hasCategoryContext(),
												oldContext.getIDs()[k]));
							}
						}

					}
				}
			}
			if (!foundMatch) { // new context type added
				for (int j = 0; j < newContext.getIDs().length; j++) {
					int kbAuditDetailID = auditStorage.getNextAuditID();
					auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(
							kbAuditDetailID,
							AuditConstants.KB_MOD_TYPE_ADD_CONTEXT_ELEMENT,
							"Added context element "
									+ getEntityOrCatDescription(
											newContext.getIDs()[j],
											newContext.getGenericEntityTypeForContext(),
											!newContext.hasCategoryContext()) + " to " + grid.getAuditDescription());
					auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
							kbAuditDetailID,
							(newContext.hasCategoryContext()
									? AuditConstants.KB_ELEMENT_TYPE_CATEGORY_ID
									: AuditConstants.KB_ELEMENT_TYPE_ENTITY_ID),
							CategoryOrEntityValue.asString(
									newContext.getGenericEntityTypeForContext(),
									!newContext.hasCategoryContext(),
									newContext.getIDs()[j]));
				}
			}
		}
		// look for removed contexts
		for (int i = 0; i < oldContexts.length; i++) {
			GuidelineContext oldContext = oldContexts[i];
			boolean foundMatch = false;
			for (int j = 0; j < newContexts.length; j++) {
				GuidelineContext newContext = newContexts[j];
				if (ContextUtil.contextOfSameType(oldContext, newContext)) {
					foundMatch = true;
					break;
				}
			}
			if (!foundMatch) { // context type removed
				int kbAuditDetailID = auditStorage.getNextAuditID();
				for (int j = 0; j < oldContext.getIDs().length; j++) {
					auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(
							kbAuditDetailID,
							AuditConstants.KB_MOD_TYPE_REMOVE_CONTEXT_ELEMENT,
							"Removed context element "
									+ getEntityOrCatDescription(
											oldContext.getIDs()[j],
											oldContext.getGenericEntityTypeForContext(),
											!oldContext.hasCategoryContext()) + " to " + grid.getAuditDescription());
					auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
							kbAuditDetailID,
							(oldContext.hasCategoryContext()
									? AuditConstants.KB_ELEMENT_TYPE_CATEGORY_ID
									: AuditConstants.KB_ELEMENT_TYPE_ENTITY_ID),
							CategoryOrEntityValue.asString(
									oldContext.getGenericEntityTypeForContext(),
									!oldContext.hasCategoryContext(),
									oldContext.getIDs()[j]));
				}

			}
		}
		logger.debug("... execute: audit data built. storing audit data ...");
	}
}

package com.mindbox.pe.server.imexport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.imexport.digest.CategoryDigest;
import com.mindbox.pe.server.imexport.digest.Entity;
import com.mindbox.pe.server.imexport.digest.Parent;
import com.mindbox.pe.server.servlet.ServletActionException;

final class EntityDataImporter extends AbstractImporter<EntityImportOptionalData> {

	private static boolean isParentYetToBeProcessed(CategoryDigest categoryDigest, List<CategoryDigest> categoryDigestList, int startIndex,
			List<CategoryDigest> postProcessList) {
		assert (startIndex > -1);
		if (startIndex >= categoryDigestList.size()) return false;
		// build parent id list first
		List<Integer> parentIDList = new ArrayList<Integer>();
		for (Iterator<Parent> parentIter = categoryDigest.getParents().iterator(); parentIter.hasNext();) {
			Parent parent = parentIter.next();
			parentIDList.add(new Integer(parent.getId()));
		}
		// for backward compatiblity
		if (parentIDList.isEmpty() && categoryDigest.getParentID() > -1) {
			parentIDList.add(new Integer(categoryDigest.getParentID()));
		}
		int[] parentIDs = UtilBase.toIntArray(parentIDList);
		parentIDList = null;
		for (int i = startIndex; i < categoryDigestList.size(); i++) {
			CategoryDigest siblingDigest = categoryDigestList.get(i);
			if (siblingDigest.getType().equals(categoryDigest.getType()) && UtilBase.isMember(siblingDigest.getId(), parentIDs)) {
				return true;
			}
		}
		if (postProcessList != null && !postProcessList.isEmpty()) {
			for (Iterator<CategoryDigest> iter = postProcessList.iterator(); iter.hasNext();) {
				CategoryDigest postProcessCategoryDigest = iter.next();
				if (postProcessCategoryDigest.getType().equals(categoryDigest.getType())
						&& UtilBase.isMember(postProcessCategoryDigest.getId(), parentIDs)) {
					return true;
				}
			}
		}
		return false;
	}

	protected EntityDataImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	@Override
	protected void processData(String filename, DigestedObjectHolder objectHolder, EntityImportOptionalData optionalData)
			throws ImportException {
		List<CategoryDigest> categoryDigestList = objectHolder.getObjects(CategoryDigest.class);
		int count = processGenericCategories(categoryDigestList, optionalData);
		// Find hard-coded product generic entity type for processing "Entity" elements for category
		GenericEntityType productType = GenericEntityType.forName("product");
		if (productType != null) {
			count += processGenericCategoriesFromEntity(productType, objectHolder.getObjects(Entity.class), optionalData);
		}
		count += processEntities(objectHolder.getObjects(Entity.class), optionalData);
		if (count > 0) {
			importResult.addMessage("  Imported " + count + " entities and categories", "File: " + filename);
		}
	}

	private int processEntities(List<Entity> list, EntityImportOptionalData optionalData) {
		logger.debug(">>> processEntities: " + list.size());
		int count = 0;
		for (int i = 0; i < list.size(); i++) {
			Entity entity = list.get(i);
			try {
				logger.debug("Entity" + i + " = " + entity);
				GenericEntityType type = GenericEntityType.forName(entity.getType());
				if (type != null) {
					importBusinessLogic.importEntity(ObjectConverter.asGenericEntity(
							type,
							entity,
							merge,
							optionalData.getEntityIDMap(),
							optionalData.getDateSynonymIDMap(),
							optionalData.getReplacementDateSynonymProvider()), merge, user);
					entity.markImported();
					++count;
					// per TT 2030: remove existing compatibility for the entity if appropriate
					try {
						if (!merge) {
							importBusinessLogic.deleteAllEntityCompatibility(type, entity.getId());
						}
					}
					catch (ServletActionException ex) {
						addError(entity, ex.getMessage());
					}
				}
			}
			catch (ImportException ex) {
				addError(entity.toString(), ex);
			}
			catch (DataValidationFailedException ex) {
				addErrors(entity.toString(), ex);
			}
		}
		return count;
	}

	private int processGenericCategories(List<CategoryDigest> list, EntityImportOptionalData optionalData) {
		logger.debug(">>> processGenericCategories: " + list.size());
		List<CategoryDigest> listToUse = new LinkedList<CategoryDigest>();
		listToUse.addAll(list);
		int count = 0;
		// listForReprocess only contains non-root categories whose parent appear after themselves
		List<CategoryDigest> listForReprocess = new LinkedList<CategoryDigest>();
		for (int i = 0; i < listToUse.size(); i++) {
			CategoryDigest categoryDigest = listToUse.get(i);
			try {
				if (categoryDigest.isRoot()) {
					try {
						// always update display value of root category, TT 1706
						importBusinessLogic.updateRootCategoryDuringImport(
								categoryDigest.getType(),
								categoryDigest.getProperty("name"),
								user);
						++count;
					}
					catch (ImportException ex) {
						addError(categoryDigest.toString(), ex);
					}
				}
				else {
					// Make sure parents are imported before children
					if (isParentYetToBeProcessed(categoryDigest, listToUse, i + 1, listForReprocess)) {
						listForReprocess.add(categoryDigest);
					}
					else {
						importAsGenericCategory(categoryDigest, optionalData);
						++count;
					}
				}
			}
			catch (ImportException ex) {
				addError(categoryDigest.toString(), ex);
			}
			catch (DataValidationFailedException ex) {
				addErrors(categoryDigest.toString(), ex);
			}
		}
		if (!listForReprocess.isEmpty()) {
			count += reprocessGenericCategories(listForReprocess, optionalData);
		}
		return count;
	}

	/**
	 * 
	 * @param list list of {@link CategoryDigest}
	 * @param merge
	 * @param entityIDMap
	 * @return
	 */
	private int reprocessGenericCategories(List<CategoryDigest> list, EntityImportOptionalData optionalData) {
		logger.debug(">>> reprocessGenericCategories: " + list.size());
		List<CategoryDigest> listForReprocess = new LinkedList<CategoryDigest>();
		int count = 0;
		for (int i = 0; i < list.size(); i++) {
			CategoryDigest categoryDigest = list.get(i);
			if (isParentYetToBeProcessed(categoryDigest, list, i + 1, listForReprocess)) {
				listForReprocess.add(categoryDigest);
			}
			else {
				try {
					importAsGenericCategory(categoryDigest, optionalData);
					++count;
				}
				catch (ImportException ex) {
					addError(categoryDigest.toString(), ex);
				}
				catch (DataValidationFailedException ex) {
					addErrors(categoryDigest.toString(), ex);
				}
			}
		}
		if (!list.isEmpty()) {
			count += reprocessGenericCategories(listForReprocess, optionalData);
		}
		return count;
	}

	private void importAsGenericCategory(CategoryDigest categoryDigest, EntityImportOptionalData optionalData) throws ImportException,
			DataValidationFailedException {
		importBusinessLogic.importCategory(ObjectConverter.asGenericCategory(
				categoryDigest,
				merge,
				optionalData.getEntityIDMap(),
				optionalData.getDateSynonymIDMap(),
				optionalData.getReplacementDateSynonymProvider()), merge, user);
	}

	private int processGenericCategoriesFromEntity(GenericEntityType entityType, List<Entity> list, EntityImportOptionalData optionalData) {
		logger.debug(">>> processGenericCategories: " + list.size());
		int count = 0;
		for (int i = 0; i < list.size(); i++) {
			Entity entity = list.get(i);
			try {
				if (entity.getType().equals("category")) {
					// do not process the very root category
					if (entity.getParentID() > 0) {
						importBusinessLogic.importCategory(ObjectConverter.asGenericCategory(
								entityType,
								entity,
								merge,
								optionalData.getEntityIDMap(),
								optionalData.getReplacementDateSynonymProvider()), merge, user);
						++count;
					}
				}
			}
			catch (ImportException ex) {
				addError(entity.toString(), ex);
			}
			catch (DataValidationFailedException ex) {
				addErrors(entity.toString(), ex);
			}
		}
		return count;
	}


}

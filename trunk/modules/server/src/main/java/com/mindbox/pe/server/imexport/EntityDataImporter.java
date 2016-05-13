package com.mindbox.pe.server.imexport;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.server.imexport.ObjectConverter.asGenericCategory;
import static com.mindbox.pe.server.imexport.ObjectConverter.asGenericEntity;
import static com.mindbox.pe.server.imexport.ObjectConverter.getProperty;
import static com.mindbox.pe.server.imexport.ObjectConverter.isRoot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.servlet.ServletActionException;
import com.mindbox.pe.xsd.data.EntityDataElement;
import com.mindbox.pe.xsd.data.EntityDataElement.Category;
import com.mindbox.pe.xsd.data.EntityDataElement.Entity;

final class EntityDataImporter extends AbstractImporter<EntityDataElement, EntityImportOptionalData> {

	private static String asErrorContext(final Category category) {
		return String.format("Category[type=%s,id=%s]", category.getType(), category.getId());
	}

	static String asErrorContext(final Entity entity) {
		return String.format("Entity[type=%s,id=%s]", entity.getType(), entity.getId());
	}


	private static boolean isParentYetToBeProcessed(Category category, List<Category> categoryList, int startIndex, List<Category> postProcessList) {
		assert (startIndex > -1);
		if (startIndex >= categoryList.size()) {
			return false;
		}
		// build parent id list first
		final List<Integer> parentIDList = new ArrayList<Integer>();
		for (final com.mindbox.pe.xsd.data.EntityDataElement.Category.Parent parent : category.getParent()) {
			parentIDList.add(new Integer(parent.getParentID()));
		}

		int[] parentIDs = UtilBase.toIntArray(parentIDList);

		for (int i = startIndex; i < categoryList.size(); i++) {
			final Category siblingCategory = categoryList.get(i);
			if (siblingCategory.getType().equals(category.getType()) && UtilBase.isMember(siblingCategory.getId(), parentIDs)) {
				return true;
			}
		}
		if (postProcessList != null && !postProcessList.isEmpty()) {
			for (final Category postProcessCategory : postProcessList) {
				if (postProcessCategory.getType().equals(category.getType()) && UtilBase.isMember(postProcessCategory.getId(), parentIDs)) {
					return true;
				}
			}
		}
		return false;
	}

	protected EntityDataImporter(ImportBusinessLogic importBusinessLogic) {
		super(importBusinessLogic);
	}

	private void importAsGenericCategory(final Category category, final EntityImportOptionalData optionalData) throws ImportException, DataValidationFailedException {
		importBusinessLogic.importCategory(
				asGenericCategory(category, merge, optionalData.getEntityIDMap(), optionalData.getDateSynonymIDMap(), optionalData.getReplacementDateSynonymProvider()),
				merge,
				user);
	}

	@Override
	protected void processData(final EntityDataElement dataToImport, final EntityImportOptionalData optionalData) throws ImportException {
		int count = processGenericCategories(dataToImport.getCategory(), optionalData);
		// Find hard-coded product generic entity type for processing "Entity" elements for category
		GenericEntityType productType = GenericEntityType.forName("product");
		if (productType != null) {
			count += processGenericCategoriesFromEntity(productType, dataToImport.getEntity(), optionalData);
		}
		count += processEntities(dataToImport.getEntity(), optionalData);
		if (count > 0) {
			importResult.addMessage("  Imported " + count + " entities and categories", "");
		}
	}

	private int processEntities(List<com.mindbox.pe.xsd.data.EntityDataElement.Entity> list, final EntityImportOptionalData optionalData) {
		logDebug(logger, ">>> processEntities: %s", list.size());
		int count = 0;
		for (final com.mindbox.pe.xsd.data.EntityDataElement.Entity entity : list) {
			try {
				logDebug(logger, "processing %s", entity);
				GenericEntityType type = GenericEntityType.forName(entity.getType());
				if (type != null) {
					final GenericEntity genericEntity = asGenericEntity(
							type,
							entity,
							merge,
							optionalData.getEntityIDMap(),
							optionalData.getDateSynonymIDMap(),
							optionalData.getReplacementDateSynonymProvider());

					BizActionCoordinator.validateData(genericEntity);

					importBusinessLogic.importEntity(genericEntity, merge, user);
					optionalData.markAsImported(entity.getId());
					++count;
					// per TT 2030: remove existing compatibility for the entity if appropriate
					try {
						if (!merge) {
							importBusinessLogic.deleteAllEntityCompatibility(type, entity.getId());
						}
					}
					catch (ServletActionException ex) {
						addError(asErrorContext(entity), ex.getMessage());
					}
				}
			}
			catch (ImportException ex) {
				addError(asErrorContext(entity), ex);
			}
			catch (DataValidationFailedException ex) {
				addErrors(asErrorContext(entity), ex);
			}
		}
		return count;
	}

	private int processGenericCategories(final List<Category> list, final EntityImportOptionalData optionalData) {
		logDebug(logger, ">>> processGenericCategories: " + list.size());
		final List<Category> listToUse = new LinkedList<Category>(list);
		int count = 0;
		// listForReprocess only contains non-root categories whose parent appear after themselves
		final List<Category> listForReprocess = new LinkedList<Category>();
		for (int i = 0; i < listToUse.size(); i++) {
			final Category category = listToUse.get(i);
			try {
				if (isRoot(category)) {
					try {
						// always update display value of root category, TT 1706
						importBusinessLogic.updateRootCategoryDuringImport(category.getType(), getProperty(category, "name"), user);
						++count;
					}
					catch (ImportException ex) {
						addError(asErrorContext(category), ex);
					}
				}
				else {
					// Make sure parents are imported before children
					if (isParentYetToBeProcessed(category, listToUse, i + 1, listForReprocess)) {
						listForReprocess.add(category);
					}
					else {
						importAsGenericCategory(category, optionalData);
						++count;
					}
				}
			}
			catch (ImportException ex) {
				addError(asErrorContext(category), ex);
			}
			catch (DataValidationFailedException ex) {
				addErrors(asErrorContext(category), ex);
			}
		}
		if (!listForReprocess.isEmpty()) {
			count += reprocessGenericCategories(listForReprocess, optionalData);
		}
		return count;
	}

	private int processGenericCategoriesFromEntity(GenericEntityType entityType, List<com.mindbox.pe.xsd.data.EntityDataElement.Entity> list, EntityImportOptionalData optionalData) {
		logDebug(logger, ">>> processGenericCategories: %s", list.size());
		int count = 0;
		for (final com.mindbox.pe.xsd.data.EntityDataElement.Entity entity : list) {
			try {
				if (entity.getType().equals("category")) {
					// do not process the very root category
					if (entity.getParentID() > 0) {
						importBusinessLogic.importCategory(asGenericCategory(entityType, entity, merge, optionalData.getEntityIDMap(), optionalData.getReplacementDateSynonymProvider()), merge, user);
						++count;
					}
				}
			}
			catch (ImportException ex) {
				addError(asErrorContext(entity), ex);
			}
			catch (DataValidationFailedException ex) {
				addErrors(asErrorContext(entity), ex);
			}
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
	private int reprocessGenericCategories(final List<Category> list, final EntityImportOptionalData optionalData) {
		logDebug(logger, ">>> reprocessGenericCategories: %s", list.size());
		final List<Category> listForReprocess = new LinkedList<Category>();
		int count = 0;
		for (int i = 0; i < list.size(); i++) {
			Category category = list.get(i);
			if (isParentYetToBeProcessed(category, list, i + 1, listForReprocess)) {
				listForReprocess.add(category);
			}
			else {
				try {
					importAsGenericCategory(category, optionalData);
					++count;
				}
				catch (ImportException ex) {
					addError(asErrorContext(category), ex);
				}
				catch (DataValidationFailedException ex) {
					addErrors(asErrorContext(category), ex);
				}
			}
		}
		if (!list.isEmpty()) {
			count += reprocessGenericCategories(listForReprocess, optionalData);
		}
		return count;
	}
}

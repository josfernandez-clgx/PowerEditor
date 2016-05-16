package com.mindbox.pe.server.bizlogic;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.LogUtil.logError;
import static com.mindbox.pe.common.LogUtil.logInfo;
import static com.mindbox.pe.server.imexport.ObjectConverter.asActionTypeDefinition;
import static com.mindbox.pe.server.imexport.ObjectConverter.asCbrAttribute;
import static com.mindbox.pe.server.imexport.ObjectConverter.asCbrCase;
import static com.mindbox.pe.server.imexport.ObjectConverter.asCbrCaseBase;
import static com.mindbox.pe.server.imexport.ObjectConverter.asDateSynonym;
import static com.mindbox.pe.server.imexport.ObjectConverter.asTestTypeDefinition;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.model.cbr.CBRAttribute;
import com.mindbox.pe.model.cbr.CBRCase;
import com.mindbox.pe.model.cbr.CBRCaseBase;
import com.mindbox.pe.model.exceptions.SapphireException;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.TestTypeDefinition;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.server.ServerContextUtil;
import com.mindbox.pe.server.Util;
import com.mindbox.pe.server.audit.AuditLogger;
import com.mindbox.pe.server.cache.CBRManager;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.LockManager;
import com.mindbox.pe.server.cache.ParameterManager;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.db.DBConnectionManager;
import com.mindbox.pe.server.db.DBIdGenerator;
import com.mindbox.pe.server.db.updaters.DateSynonymUpdater;
import com.mindbox.pe.server.db.updaters.GenericEntityUpdater;
import com.mindbox.pe.server.db.updaters.GuidelineActionUpdater;
import com.mindbox.pe.server.db.updaters.GuidelineTemplateUpdater;
import com.mindbox.pe.server.db.updaters.GuidelineTestConditionUpdater;
import com.mindbox.pe.server.db.updaters.ParameterUpdater;
import com.mindbox.pe.server.imexport.ImportBusinessLogic;
import com.mindbox.pe.server.imexport.ImportException;
import com.mindbox.pe.server.model.LockException;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletActionException;
import com.mindbox.pe.server.spi.ServiceProviderFactory;
import com.mindbox.pe.server.spi.UserManagementProvider;
import com.mindbox.pe.xsd.config.CategoryType;
import com.mindbox.pe.xsd.data.CBRAttributeElement;
import com.mindbox.pe.xsd.data.CBRCaseBaseElement;
import com.mindbox.pe.xsd.data.CBRCaseElement;
import com.mindbox.pe.xsd.data.DateDataElement.DateElement;
import com.mindbox.pe.xsd.data.GuidelineActionDataElement.GuidelineAction;
import com.mindbox.pe.xsd.data.NextIDDataElement.NextId;
import com.mindbox.pe.xsd.data.TestConditionDataElement.TestCondition;
import com.mindbox.server.parser.jtb.rule.ParseException;

public class DefaultImportBusinessLogic implements ImportBusinessLogic {

	private static final Logger LOG = Logger.getLogger(DefaultImportBusinessLogic.class);

	private final BizActionCoordinator bizActionCoordinator;
	private final ParameterUpdater parameterUpdater = new ParameterUpdater();
	private final UserManagementProvider userDataUpdater;

	public DefaultImportBusinessLogic(BizActionCoordinator bizActionCoordinator) {
		super();
		this.bizActionCoordinator = bizActionCoordinator;
		this.userDataUpdater = ServiceProviderFactory.getUserManagementProvider();
	}

	@Override
	public void deleteAllEntityCompatibility(GenericEntityType type, int entityID) throws ServletActionException {
		try {
			ServiceProviderFactory.getPEDataUpdater().getGenericEntityDataUpdater().deleteAllEntityCompatibility(type.getID(), entityID);
			EntityManager.getInstance().removeAllEntityCompatibility(type, entityID);
		}
		catch (Exception ex) {
			throw new ServletActionException("ServerError", "Failed to delete the generic entity from DB: " + ex.getMessage());
		}
	}

	@Override
	public void importCategory(GenericCategory category, boolean merge, User user) throws ImportException, DataValidationFailedException {
		logInfo(LOG, ">>> importCategory(GenericCategory) with %s", category);
		BizActionCoordinator.validateData(category);
		int categoryID = category.getID();
		try {
			if (!merge && categoryID != -1 && EntityManager.getInstance().hasGenericCategory(category.getType(), categoryID)) {
				bizActionCoordinator.update(category, user);
			}
			else {
				// 1. insert category row into DB
				GenericEntityUpdater genericEntityUpdater = new GenericEntityUpdater();
				MutableTimedAssociationKey[] keys = BizActionCoordinator.extractParentAssociations(category);
				genericEntityUpdater.addCategory(categoryID, category.getName(), category.getType(), keys);

				logInfo(LOG, ">>> importCategory: DB updated; keys.size=%d. inserting into cache...", (keys == null ? 0 : keys.length));
				// 2. insert category into server cache
				EntityManager.getInstance().addGenericEntityCategory(category.getType(), categoryID, category.getName());
				EntityManager.getInstance().addParentAssociations(category);

				if (user != null) {
					AuditLogger.getInstance().logImport(category, user.getUserID());
				}
			}
		}
		catch (Exception ex) {
			logError(LOG, ex, "Failed to import generic category %s", category);
			throw new ImportException("Failed to store the generic category into the DB: " + ex.getMessage());
		}
	}

	@Override
	public void importCBRAttribute(CBRAttributeElement attribute, boolean merge, final Map<String, Integer> cbrDataIdMap, final User user)
			throws DataValidationFailedException, ImportException {
		int id = attribute.getId();
		try {
			final CBRAttribute cbrAttribute = asCbrAttribute(attribute);
			BizActionCoordinator.validateData(cbrAttribute);

			if (!merge && id != -1 && CBRManager.getInstance().getCBRAttribute(id) != null) {
				bizActionCoordinator.update(cbrAttribute, user);
			}
			else {
				int oldID = attribute.getId();
				int newID = bizActionCoordinator.insert(cbrAttribute, user);
				cbrDataIdMap.put("attribute-" + oldID, newID);
			}
		}
		catch (DataValidationFailedException e) {
			throw e;
		}
		catch (Exception ex) {
			logError(LOG, ex, "Failed to import CBR attribute %s", attribute);
			throw new ImportException("Failed to import the CBR attribute" + attribute + ": " + ex.getMessage());
		}
	}

	@Override
	public void importCBRCase(CBRCaseElement cbrCaseElement, boolean merge, Map<String, Integer> cbrDataIdMap, User user) throws ImportException, DataValidationFailedException {
		int id = cbrCaseElement.getId();
		try {
			final CBRCase cbrCase = asCbrCase(cbrCaseElement);
			BizActionCoordinator.validateData(cbrCase);

			if (!merge && id != -1 && CBRManager.getInstance().getCBRCase(id) != null) {
				bizActionCoordinator.update(cbrCase, user);
			}
			else {
				int oldID = cbrCaseElement.getId();
				int newID = bizActionCoordinator.insert(cbrCase, user);
				cbrDataIdMap.put("case-" + oldID, newID);
			}
		}
		catch (DataValidationFailedException e) {
			throw e;
		}
		catch (Exception ex) {
			logError(LOG, ex, "Failed to import CBR case %s", cbrCaseElement);
			throw new ImportException("Failed to import the CBR case" + cbrCaseElement + ": " + ex.getMessage());
		}
	}

	@Override
	public void importCBRCaseBase(CBRCaseBaseElement caseBaseElement, boolean merge, final Map<String, Integer> cbrDataIdMap, final User user)
			throws ImportException, DataValidationFailedException {
		int id = caseBaseElement.getId();
		try {
			final CBRCaseBase cbrCaseBase = asCbrCaseBase(caseBaseElement);
			BizActionCoordinator.validateData(cbrCaseBase);

			if (!merge && id != -1 && CBRManager.getInstance().getCBRCaseBase(id) != null) {
				bizActionCoordinator.update(cbrCaseBase, user);
			}
			else {
				int oldID = caseBaseElement.getId();
				int newID = bizActionCoordinator.insert(cbrCaseBase, user);
				cbrDataIdMap.put("casebase-" + oldID, newID);
			}
		}
		catch (DataValidationFailedException e) {
			throw e;
		}
		catch (Exception ex) {
			logError(LOG, ex, "Failed to import case base %s", caseBaseElement);
			throw new ImportException("Failed to import the case base " + caseBaseElement + ": " + ex.getMessage());
		}
	}

	@Override
	public int importDateSynonym(DateElement dateSynonymElement, boolean merge, User user) throws ImportException, DataValidationFailedException {
		logDebug(LOG, ">>> importDateSynonym with %s", dateSynonymElement);
		final DateSynonym dateSynonym = asDateSynonym(dateSynonymElement);
		BizActionCoordinator.validateData(dateSynonym);

		int synonymID = dateSynonym.getId();
		try {
			DateSynonymUpdater updater = new DateSynonymUpdater();
			if (!merge && DateSynonymManager.getInstance().getDateSynonym(synonymID) != null) {
				bizActionCoordinator.lockEntity(PeDataType.DATE_SYNONYM, synonymID, user);
				updater.updateDateSynonym(synonymID, dateSynonym.getName(), dateSynonym.getDescription(), dateSynonym.getDate(), true);
				DateSynonymManager.getInstance().update(dateSynonym);
				bizActionCoordinator.unlockEntity(PeDataType.DATE_SYNONYM, synonymID, user);
			}
			else {
				synonymID = (merge ? DBIdGenerator.getInstance().nextSequentialID() : dateSynonym.getID());
				logDebug(LOG, "inserting new date Synonym = %s", synonymID);
				updater.insertDateSynonym(synonymID, dateSynonym.getName(), dateSynonym.getDescription(), dateSynonym.getDate(), true);
				dateSynonym.setID(synonymID);
				logDebug(LOG, "    inserted into DB. inserting into the cache");
				DateSynonymManager.getInstance().insert(dateSynonym);
			}
		}
		catch (SQLException ex) {
			logError(LOG, ex, "Failed to import date synonym %s", dateSynonym);
			throw new ImportException("failed to update DB: " + ex.getLocalizedMessage());
		}
		catch (SapphireException ex) {
			logError(LOG, ex, "Failed to import " + dateSynonym, ex);
			throw new ImportException("Failed to import " + dateSynonym + ": " + ex.getMessage());
		}
		return synonymID;
	}

	/**
	 * 
	 * @param entity entity
	 * @param user user
	 * @throws ImportException on error
	 * @since PowerEditor 3.0.0
	 */
	@Override
	public void importEntity(GenericEntity entity, boolean merge, User user) throws ImportException, DataValidationFailedException {
		logDebug(LOG, ">>> importEntity(GenericEntity): %s, merge?=%b", entity, merge);
		BizActionCoordinator.validateData(entity);

		int entityID = entity.getID();
		int categoryType = BizActionCoordinator.getCategoryTypeID(entity.getType());
		try {
			if (!merge && EntityManager.getInstance().hasGenericEntity(entity.getType(), entityID)) {
				logInfo(LOG, "    importEntity: entity with the same id already exists. Updating existing entity...");
				// 1. check lock
				LockManager.getInstance().getExistingLock(entity.getType(), entityID, user);

				// 2. update generic entity DB row
				GenericEntity entityFromCache = EntityManager.getInstance().getEntity(entity.getType(), entityID);

				ServiceProviderFactory.getPEDataUpdater().getGenericEntityDataUpdater().updateGenericEntity(
						entityID,
						entity.getType().getID(),
						entity.getName(),
						entity.getParentID(),
						entity.getPropertyMap(),
						categoryType,
						BizActionCoordinator.extractCategoryAssociations(entity));

				EntityManager.getInstance().updateCache(entity);

				AuditLogger.getInstance().logUpdate(entity, entityFromCache, user.getUserID());
			}
			else {
				logInfo(LOG, "    importEntity: inserting as new with id %s", entityID);
				// 1. insert generic entity row into DB
				ServiceProviderFactory.getPEDataUpdater().getGenericEntityDataUpdater().insertGenericEntity(
						entityID,
						entity.getType().getID(),
						entity.getName(),
						entity.getParentID(),
						entity.getPropertyMap(),
						categoryType,
						BizActionCoordinator.extractCategoryAssociations(entity));

				EntityManager.getInstance().addGenericEntity(entityID, entity.getType().getID(), entity.getName(), entity.getParentID(), entity.getPropertyMap());

				AuditLogger.getInstance().logImport(entity, user.getUserID());
			}
			logDebug(LOG, "<<< importEntity(GenericEntity)");
		}
		catch (SQLException ex) {
			logError(LOG, ex, "Failed to import generic entiy %s", entity);
			throw new ImportException("failed to updated DB: " + ex.getLocalizedMessage());
		}
		catch (LockException ex) {
			throw new ImportException("Failed to lock the entity - locked by " + ex.getLockedBy());
		}
	}

	@Override
	public void importEntityCompatibilityData(GenericEntityCompatibilityData compData, User user) throws ImportException, DataValidationFailedException {
		logDebug(LOG, ">>> importEntity: %s", compData);
		BizActionCoordinator.validateData(compData);
		try {
			if (EntityManager.getInstance().isCached(compData) == null) {
				ServiceProviderFactory.getPEDataUpdater().getGenericEntityDataUpdater().insertEntityCompatibility(
						compData.getSourceType().getID(),
						compData.getSourceID(),
						compData.getGenericEntityType().getID(),
						compData.getAssociableID(),
						compData.getEffectiveDate(),
						compData.getExpirationDate());

				EntityManager.getInstance().insertEntityCompatibility(compData);
				AuditLogger.getInstance().logImport(compData, user.getUserID());
			}
			else {
				String ids = compData.getSourceType() + "." + compData.getSourceID() + " and " + compData.getGenericEntityType() + "." + compData.getAssociableID();
				LOG.warn("Failed to save compatibility, duplicate found for " + ids);
				throw new ImportException("Warning: Duplicate compatibility found for " + ids + ".  Only the first was saved.");
			}
		}
		catch (ImportException ie) {
			throw ie;
		}
		catch (Exception ex) {
			logError(LOG, ex, "Failed to save %s", compData);
			throw new ImportException("Failed to store the compatibility into the DB: " + ex.getMessage());
		}
	}

	@Override
	public void importGridData(int templateID, List<ProductGrid> grids, User user) throws ImportException {
		GridActionCoordinator.getInstance().importGridData(templateID, grids, user);
	}

	@Override
	public void importGuidelineAction(GuidelineAction guidelineAction, boolean merge, Map<String, Integer> actionIDMap, User user, boolean updateIfExist)
			throws ServletActionException, ParseException, DataValidationFailedException {
		logDebug(LOG, ">>> importGuidelineAction: %s,merge?=%b", guidelineAction, merge);
		final ActionTypeDefinition actionDef = asActionTypeDefinition(guidelineAction);

		if (merge) {
			try {
				int newID = DBIdGenerator.getInstance().nextSequentialID();
				actionIDMap.put("action:" + actionDef.getId(), newID);
				logDebug(LOG, "    importActionTypeDefinition: new action id = %s", newID);
				actionDef.setID(newID);
			}
			catch (SapphireException ex) {
				logError(LOG, ex, "Failed to generate new id for action %s", actionDef);
				throw new ServletActionException("msg.error.failure.import.action", ex.getMessage());
			}
		}

		BizActionCoordinator.validateData(actionDef);

		if (!merge && GuidelineFunctionManager.getInstance().getActionTypeDefinition(actionDef.getID()) != null) {
			if (updateIfExist) {
				bizActionCoordinator.update(actionDef, user);
			}
			else {
				throw new ServletActionException("msg.error.failure.import.action", "Action Definition already exists");
			}
		}
		else {
			try {
				logDebug(LOG, "    creating new action: %s", actionDef.getID());

				// 1. insert action into server cache
				GuidelineFunctionManager.getInstance().insertActionTypeDefinition(actionDef);

				// 2. insert action row into DB
				GuidelineActionUpdater updater = new GuidelineActionUpdater();
				updater.insertAction(
						actionDef.getID(),
						actionDef.getName(),
						actionDef.getDescription(),
						actionDef.getDeploymentRule(),
						actionDef.getParameterDefinitions(),
						BizActionCoordinator.toStringRepresentation(actionDef.getUsageTypes()));

				// audit log new action
				AuditLogger.getInstance().logImport(actionDef, user.getUserID());
			}
			catch (SQLException ex) {
				logError(LOG, ex, "DB error in importGuidelineAction: %s", guidelineAction);
				// this is required as we attempt to insert the action into the cache
				// before attempt to persist it in DB
				if (GuidelineFunctionManager.getInstance().getActionTypeDefinition(actionDef.getID()) != null) {
					GuidelineFunctionManager.getInstance().removeActionTypeDefinition(actionDef.getID());
				}
				throw new ServletActionException("ServerError", "Failed to store the action into the DB: " + ex.getMessage());
			}
		}
	}

	@Override
	public void importNextID(final NextId nextId) throws ImportException, DataValidationFailedException {
		logDebug(LOG, ">>> importNextID(NextIDSeed) with %s", nextId);
		try {
			final int currentValue = DBIdGenerator.getInstance().getCurrentIdForImport(nextId.getType());
			if (currentValue < nextId.getSeed()) {
				DBIdGenerator.getInstance().setNextID(nextId.getType(), nextId.getSeed(), nextId.getCache());
			}
		}
		catch (SQLException ex) {
			logError(LOG, ex, "Failed to import next-id (type=%s,seed=%s)", nextId.getType(), nextId.getSeed());
			throw new ImportException("failed to updated DB: " + ex.getLocalizedMessage());
		}
	}

	@Override
	public void importParameterGrid(ParameterGrid parameterGrid, User user) throws ImportException {
		logDebug(LOG, ">>> importParameterGrid(ParameterContext) with %s for %s", parameterGrid, user);
		logDebug(LOG, "importParameterGrid(ParameterContext) cell values = %s", parameterGrid.getCellValues());
		try {
			int gridID = parameterGrid.getID();
			if (gridID == -1 || ParameterManager.getInstance().getGrid(gridID) == null) {
				if (gridID == -1) {
					int newGridID = DBIdGenerator.getInstance().nextSequentialID();
					parameterGrid.setID(newGridID);
					logDebug(LOG, "importParameterGrid(ParameterContext): newGridID = %s", parameterGrid.getID());
				}
				logDebug(LOG, "importParameterGrid(ParameterContext): inserting a new row into DB as " + gridID);
				parameterUpdater.insertGrid(
						parameterGrid.getID(),
						parameterGrid.getTemplateID(),
						parameterGrid.getCellValues(),
						parameterGrid.getNumRows(),
						parameterGrid.getEffectiveDate(),
						parameterGrid.getExpirationDate(),
						parameterGrid.getStatus(),
						ServerContextUtil.extractGenericEntityIdentities(parameterGrid),
						ServerContextUtil.extractGenericCategoryIdentities(parameterGrid));
				logDebug(LOG, "importParameterGrid(ParameterContext): inserting the parameter grid into cache...");
				ParameterManager.getInstance().insertIntoCache(
						parameterGrid.getID(),
						parameterGrid.getTemplateID(),
						parameterGrid.getCellValues(),
						parameterGrid.getNumRows(),
						parameterGrid.getEffectiveDate(),
						parameterGrid.getExpirationDate(),
						parameterGrid.getStatus(),
						parameterGrid.extractGuidelineContext());
				AuditLogger.getInstance().logImport(parameterGrid, user.getUserID());
			}
			else {
				LockManager.getInstance().getExistingParameterGridLock(parameterGrid.getID(), user);
				logDebug(LOG, "importParameterGrid(ParameterContext): lock passed...");
				logDebug(LOG, "importParameterGrid(ParameterContext): updating parameter grid row...");
				parameterUpdater.updateGrid(
						gridID,
						parameterGrid.getTemplateID(),
						parameterGrid.getCellValues(),
						parameterGrid.getNumRows(),
						parameterGrid.getEffectiveDate(),
						parameterGrid.getExpirationDate(),
						parameterGrid.getStatus(),
						ServerContextUtil.extractGenericEntityIdentities(parameterGrid),
						ServerContextUtil.extractGenericCategoryIdentities(parameterGrid));
				logDebug(LOG, "importParameterGrid(ParameterContext): updating cache...");
				ParameterGrid cachedParamGrid = ParameterManager.getInstance().getGrid(parameterGrid.getID());
				ParameterManager.getInstance().updateCache(parameterGrid);
				AuditLogger.getInstance().logUpdate(parameterGrid, cachedParamGrid, user.getUserID());
			}
			logDebug(LOG, "<<< importParameterGrid(ParameterContext): %s", gridID);
		}
		catch (SQLException ex) {
			logError(LOG, ex, "Failed to import parameter grid %s", parameterGrid);
			throw new ImportException("failed to updated DB: " + ex.getLocalizedMessage());
		}
		catch (LockException ex) {
			throw new ImportException("failed to lock - locked by " + ex.getLockedBy());
		}
		catch (SapphireException ex) {
			logError(LOG, ex, "Failed to import parameter grid %s", parameterGrid);
			throw new ImportException("failed to generate new ID: " + ex.getLocalizedMessage());
		}
	}

	/**
	 * Imports role 
	 * @param role role
	 * @param unknownPrivsForRole unknownPrivsForRole
	 * @param user user
	 * @throws ImportException on error
	 */
	@Override
	public void importRole(Role role, List<String> unknownPrivsForRole, User user) throws ImportException {
		int roleID = role.getID();
		try {
			if (SecurityCacheManager.getInstance().getRole(roleID) == null) {
				logDebug(LOG, "save(Role): new role ID = %s", role.getID());
				if (!unknownPrivsForRole.isEmpty()) {//some exported privileges do not exist now. should be used mainly for going from 4.5.x to 5.0.0
					userDataUpdater.insertRoleWithUnknownPrivileges(roleID, role.getName(), BizActionCoordinator.toIDs(role.getPrivileges()), unknownPrivsForRole);
				}
				else {// normal insert of roles during normal export
					userDataUpdater.insertRole(roleID, role.getName(), BizActionCoordinator.toIDs(role.getPrivileges()));
				}

				SecurityCacheManager.getInstance().insertCache(role);
				logDebug(LOG, "save(Role): inserted into cache. from cache = %s", SecurityCacheManager.getInstance().getRole(roleID));
				AuditLogger.getInstance().logNew(role, user.getUserID());
			}
			else {
				LockManager.getInstance().getExistingLock(PeDataType.ROLE, roleID, user);
				if (!unknownPrivsForRole.isEmpty()) {//some exported privileges do not exist now. should be used mainly for going from 4.5.x to 5.0.0
					userDataUpdater.updateRoleWithUnknownPrivileges(roleID, role.getName(), BizActionCoordinator.toIDs(role.getPrivileges()), unknownPrivsForRole);
				}
				else {// normal update of roles during normal export
					userDataUpdater.updateRole(roleID, role.getName(), BizActionCoordinator.toIDs(role.getPrivileges()));
				}
				AuditLogger.getInstance().logUpdate(role, SecurityCacheManager.getInstance().getRole(roleID), user.getUserID());
				SecurityCacheManager.getInstance().updateCache(SecurityCacheManager.getInstance().getRole(roleID), role);
			}
		}
		catch (SQLException ex) {
			logError(LOG, ex, "Failed to import role %s", role);
			throw new ImportException("failed to updated DB: " + ex.getLocalizedMessage());
		}
		catch (LockException ex) {
			throw new ImportException("failed to lock - locked by " + ex.getLockedBy());
		}
	}

	/**
	 * This updates it if the specified template already exists.
	 * 
	 * @param template template
	 * @param user user
	 * @throws ServletActionException on error
	 */
	@Override
	public void importTemplate(GridTemplate template, boolean merge, Map<Integer, Integer> idMap, User user) throws ServletActionException {
		// 1. prepare the template for import
		logDebug(LOG, "    processing template for import: %s,merge=%b", template.getID(), merge);
		if (merge) {
			int templateID;
			try {
				templateID = DBIdGenerator.getInstance().nextGridID();
				idMap.put(new Integer(template.getID()), new Integer(templateID));
				template.setID(templateID);
				logDebug(LOG, "    importTemplate: template id set to %s", templateID);
			}
			catch (SapphireException e) {
				throw new ServletActionException("ServerError", e.toString());
			}
		}

		prepareTemplateForImport(template);

		if (GuidelineTemplateManager.getInstance().getTemplate(template.getID()) != null) {
			bizActionCoordinator.update(template, user);
		}
		else {
			try {
				// 2. insert template into DB
				GuidelineTemplateUpdater updater = new GuidelineTemplateUpdater();
				Connection conn = null;
				try {
					conn = DBConnectionManager.getInstance().getConnection();
					conn.setAutoCommit(false);
					updater.insertTemplate(conn, template);
					conn.commit();

					// 3. insert template into server cache
					GuidelineTemplateManager.getInstance().addTemplate(template);

					AuditLogger.getInstance().logImport(template, user.getUserID());
				}
				catch (Exception ex) {
					logError(LOG, ex, "Error while creating template %s", template);
					if (conn != null) {
						conn.rollback();
					}
					throw new ServletActionException("ServerError", "Failed to store the template into the DB: " + ex.getMessage());
				}
				finally {
					DBConnectionManager.getInstance().freeConnection(conn);
				}
			}
			catch (SQLException ex) {
				logError(LOG, ex, "Failed to persist imported template %s", template.getID());
				throw new ServletActionException("ServerError", "Failed to store the template into the DB: " + ex.getMessage());
			}
		}
	}

	@Override
	public void importTestCondition(TestCondition testCondition, boolean merge, Map<String, Integer> actionIDMap, User user, boolean updateIfExist)
			throws ServletActionException, ParseException, DataValidationFailedException {
		logDebug(LOG, ">>> importTestTypeDefinition:%s,merge=%b", testCondition, merge);
		final TestTypeDefinition testDef = asTestTypeDefinition(testCondition);
		if (merge) {
			try {
				int newID = DBIdGenerator.getInstance().nextSequentialID();
				actionIDMap.put("test:" + testDef.getId(), newID);
				logDebug(LOG, "    importTestTypeDefinition: new test id = %s", newID);
				testDef.setID(newID);
			}
			catch (SapphireException ex) {
				logError(LOG, ex, "Failed to generate new id for test %s", testDef);
				throw new ServletActionException("msg.error.failure.import.test", ex.getMessage());
			}
		}

		BizActionCoordinator.validateData(testDef);

		if (!merge && GuidelineFunctionManager.getInstance().getTestTypeDefinition(testDef.getID()) != null) {
			if (updateIfExist) {
				bizActionCoordinator.update(testDef, user);
			}
			else {
				throw new ServletActionException("msg.error.failure.import.test", "Test Definition already exists");
			}
		}
		else {
			try {
				logDebug(LOG, "    creating new test: %s", testDef.getID());

				// 1. insert test into server cache
				GuidelineFunctionManager.getInstance().insertTestTypeDefinition(testDef);

				// 2. insert test row into DB
				GuidelineTestConditionUpdater updater = new GuidelineTestConditionUpdater();
				updater.insertTest(testDef.getID(), testDef.getName(), testDef.getDescription(), testDef.getDeploymentRule(), testDef.getParameterDefinitions());

				// audit log new test condition
				AuditLogger.getInstance().logImport(testDef, user.getUserID());
			}
			catch (SQLException ex) {
				// this is required as we attempt to insert the test into the cache
				// before attempt to persist it in DB
				if (GuidelineFunctionManager.getInstance().getTestTypeDefinition(testDef.getID()) != null) {
					GuidelineFunctionManager.getInstance().removeTestTypeDefinition(testDef.getID());
				}
				throw new ServletActionException("ServerError", "Failed to store the test into the DB: " + ex.getMessage());
			}
		}
	}

	@Override
	public void importUser(User userToSave, User requester) throws ImportException {
		logDebug(LOG, ">>> importUser(User) with %s", userToSave);
		String userID = userToSave.getUserID();
		try {
			User cachedUser = SecurityCacheManager.getInstance().getUser(userID);
			if (cachedUser == null) {
				logDebug(LOG, "importUser(User): inserting new user...");
				userDataUpdater.insertUser(
						userID,
						userToSave.getName(),
						userToSave.getStatus(),
						userToSave.getPasswordChangeRequired(),
						userToSave.getFailedLoginCounter(),
						BizActionCoordinator.toIDs(userToSave.getRoles()),
						userToSave.getPasswordHistory(),
						requester.getUserID());
				logInfo(LOG, "User not found in cache. inserting into cache...");
				SecurityCacheManager.getInstance().insertCache(userToSave.asUserData());
				AuditLogger.getInstance().logImport(userToSave, requester.getUserID());
			}
			else {
				LockManager.getInstance().getExistingLock(PeDataType.USER_DATA, userID, requester);
				logDebug(LOG, "importUser(User): updating user table...");
				userDataUpdater.updateUser(
						userID,
						userToSave.getName(),
						userToSave.getStatus(),
						userToSave.getPasswordChangeRequired(),
						userToSave.getFailedLoginCounter(),
						BizActionCoordinator.toIDs(userToSave.getRoles()),
						userToSave.getPasswordHistory(),
						requester.getUserID());
				logDebug(LOG, "save(User): user updated. updating cache");
				AuditLogger.getInstance().logUpdate(userToSave, cachedUser, requester.getUserID());
				SecurityCacheManager.getInstance().updateCache(cachedUser, userToSave.asUserData());
			}
		}
		catch (SQLException ex) {
			logError(LOG, ex, "Failed to import user %s", userToSave);
			throw new ImportException("failed to updated DB: " + ex.getLocalizedMessage());
		}
		catch (LockException ex) {
			throw new ImportException("failed to lock - locked by " + ex.getLockedBy());
		}
	}

	private void prepareTemplateForImport(GridTemplate template) {
		logDebug(LOG, ">>> prepareTemplateForImport: %s", template);
		if (Util.isEmpty(template.getStatus())) {
			template.setStatus(GridTemplate.DEFAULT_STATUS_DRAFT);
		}
		logDebug(LOG, "<<< prepareTemplateForImport");
	}

	/**
	 * This method is called only during Import. Root categories are created by PE during start up, if nothing existed in the DB.
	 * This method updates the root categories display value from the import file
	 * @author vineet khosla
	 * @since PowerEditor 5.0.0
	 * @param type type
	 * @param newName newName
	 * @param user user
	 * @throws ImportException on error
	 */
	@Override
	public void updateRootCategoryDuringImport(String type, String newName, User user) throws ImportException {
		final CategoryType ctd = ConfigurationManager.getInstance().getEntityConfigHelper().getCategoryDefinition(GenericEntityType.forName(type));
		if (ctd == null) {
			throw new ImportException("No category defined for entity type " + type);
		}
		try {
			// 1. get root category
			GenericCategory rootCategoryFromCache = (GenericCategory) EntityManager.getInstance().getGenericCategoryRoot(ctd.getTypeID().intValue());
			if (!rootCategoryFromCache.getName().equals(newName)) {
				logDebug(LOG, ">>> update root(GenericCategory) with %s", newName);

				// 2. update category DB row
				GenericEntityUpdater genericEntityUpdater = new GenericEntityUpdater();
				genericEntityUpdater.updateCategoryName(rootCategoryFromCache.getID(), rootCategoryFromCache.getType(), newName);

				logDebug(LOG, "    updated root category. udating cache...");

				// 3. update cache
				rootCategoryFromCache.setName(newName);
				// audit with the same before and after
				AuditLogger.getInstance().logImport(rootCategoryFromCache, user.getUserID());
			}
		}
		catch (SQLException ex) {
			logError(LOG, ex, "DB error in updateRootCategoryDuringImport: %s, %s", type, newName);
			throw new ImportException("Failed to update the root category into the DB: " + ex.getMessage());
		}
	}

	@Override
	public void validateTemplateForImport(GridTemplate template, boolean checkColumnNames, boolean merge) throws ImportException {
		if (UtilBase.isEmpty(template.getName())) {
			throw new ImportException("Template " + template.getID() + " has no name.");
		}
		// TT 2000
		// check for unique template name
		boolean templateExists = (GuidelineTemplateManager.getInstance().getTemplate(template.getID()) != null);
		if (merge || !templateExists) {
			if (!bizActionCoordinator.isUniqueTemplateNameAndVersion(template.getName(), template.getVersion())) {
				throw new ImportException(
						String.format("Template with name %s and version %s already exists (ID was %s).", template.getName(), template.getVersion(), template.getID()));
			}
		}

		final List<String> columnNameList = new ArrayList<String>();
		for (int c = 1; c <= template.getNumColumns(); c++) {
			AbstractTemplateColumn column = template.getColumn(c);
			if (column == null) {
				throw new ImportException("Template " + template.getID() + " has no column for column number " + c);
			}
			if (checkColumnNames) {
				if (UtilBase.isEmpty(column.getName())) {
					throw new ImportException("Column " + column.getColumnNumber() + " of template " + template.getID() + " has no name.");
				}
				else if (columnNameList.contains(column.getName())) {
					throw new ImportException("Template " + template.getID() + " has duplicate column names, '" + column.getName() + "'.");
				}
				else {
					columnNameList.add(column.getName());
				}
			}
		}
	}

}

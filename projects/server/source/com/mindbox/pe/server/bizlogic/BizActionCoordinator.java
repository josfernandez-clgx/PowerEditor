package com.mindbox.pe.server.bizlogic;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.MutableBoolean;
import com.mindbox.pe.common.TemplateUtil;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.CategoryTypeDefinition;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.common.validate.ValidationViolation;
import com.mindbox.pe.communication.ImportResult;
import com.mindbox.pe.communication.ValidationException;
import com.mindbox.pe.model.AbstractIDNameDescriptionObject;
import com.mindbox.pe.model.AbstractIDObject;
import com.mindbox.pe.model.AbstractTemplateColumn;
import com.mindbox.pe.model.CBRAttribute;
import com.mindbox.pe.model.CBRAttributeValue;
import com.mindbox.pe.model.CBRCase;
import com.mindbox.pe.model.CBRCaseBase;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.ImportSpec;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.TemplateMessageDigest;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.admin.Role;
import com.mindbox.pe.model.admin.UserData;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;
import com.mindbox.pe.model.exceptions.SapphireException;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.process.Phase;
import com.mindbox.pe.model.process.PhaseReference;
import com.mindbox.pe.model.process.ProcessRequest;
import com.mindbox.pe.model.report.AbstractReportSpec;
import com.mindbox.pe.model.report.CustomReportSpec;
import com.mindbox.pe.model.report.GuidelineReportSpec;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleSet;
import com.mindbox.pe.model.rule.TestCondition;
import com.mindbox.pe.model.rule.TestTypeDefinition;
import com.mindbox.pe.server.RuleDefinitionUtil;
import com.mindbox.pe.server.ServerContextUtil;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.Util;
import com.mindbox.pe.server.audit.AuditLogger;
import com.mindbox.pe.server.cache.CBRManager;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.LockManager;
import com.mindbox.pe.server.cache.ParameterManager;
import com.mindbox.pe.server.cache.ProcessManager;
import com.mindbox.pe.server.cache.ReportSpecManager;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.cache.SessionManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.db.DBCBRUtil;
import com.mindbox.pe.server.db.DBConnectionManager;
import com.mindbox.pe.server.db.DBIdGenerator;
import com.mindbox.pe.server.db.DBUtil;
import com.mindbox.pe.server.db.DateSynonymReferenceUpdater;
import com.mindbox.pe.server.db.loaders.GuidelineTemplateLoader;
import com.mindbox.pe.server.db.updaters.CBRAttributeUpdater;
import com.mindbox.pe.server.db.updaters.CBRCaseBaseUpdater;
import com.mindbox.pe.server.db.updaters.CBRCaseUpdater;
import com.mindbox.pe.server.db.updaters.DateSynonymUpdater;
import com.mindbox.pe.server.db.updaters.GenericEntityUpdater;
import com.mindbox.pe.server.db.updaters.GridUpdater;
import com.mindbox.pe.server.db.updaters.GuidelineActionUpdater;
import com.mindbox.pe.server.db.updaters.GuidelineTemplateUpdater;
import com.mindbox.pe.server.db.updaters.GuidelineTestConditionUpdater;
import com.mindbox.pe.server.db.updaters.ParameterUpdater;
import com.mindbox.pe.server.db.updaters.ProcessUpdater;
import com.mindbox.pe.server.db.updaters.UserSecurityUpdater;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.imexport.ExportException;
import com.mindbox.pe.server.imexport.ExportService;
import com.mindbox.pe.server.imexport.ImportBusinessLogic;
import com.mindbox.pe.server.imexport.ImportException;
import com.mindbox.pe.server.imexport.ImportService;
import com.mindbox.pe.server.imexport.digest.NextIDSeed;
import com.mindbox.pe.server.model.GenericEntityIdentity;
import com.mindbox.pe.server.model.LockException;
import com.mindbox.pe.server.model.ServerStats;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.report.ReportGenerator;
import com.mindbox.pe.server.servlet.Loader;
import com.mindbox.pe.server.servlet.ServletActionException;
import com.mindbox.pe.server.spi.ServiceProviderFactory;
import com.mindbox.pe.server.spi.UserManagementProvider;
import com.mindbox.pe.server.validate.DataValidator;
import com.mindbox.pe.server.validate.DataValidatorFactory;
import com.mindbox.server.parser.jtb.rule.ParseException;
import com.mindbox.server.parser.jtb.rule.RuleParser;
import com.mindbox.server.parser.jtb.rule.syntaxtree.DeploymentRule;

/**
 * Corrdinates tasks required to fulfill a business action among server components. This is mainly
 * used by Servlet handlers.
 * 
 * @since PowerEditor 1.0
 */
public class BizActionCoordinator implements ImportBusinessLogic {

	private static BizActionCoordinator instance;

	public static BizActionCoordinator getInstance() {
		if (instance == null) {
			instance = new BizActionCoordinator();
		}
		return instance;
	}

	public static DateSynonym getDateSynonymAndCreateIfNecessary(Date date, User user) throws ServletActionException,
			DataValidationFailedException {
		if (date == null) return null;
		DateSynonym ds = DateSynonymManager.getInstance().getDateSynonym(date);
		return (ds == null ? createNewDateSynonym(date, user) : ds);
	}

	/**
	 * Creates a new named date synonym for the specified date. This saves the new date synonym in
	 * DB and in server cache.
	 * 
	 * @param date
	 *            date for the date synonym
	 * @param user
	 *            user
	 * @return newly created date synonym with proper unique id
	 * @throws ServletActionException
	 *             on error
	 */
	public static DateSynonym createNewDateSynonym(Date date, User user) throws ServletActionException, DataValidationFailedException {
		if (date == null) return null;
		DateSynonym ds = new DateSynonym(
				-1,
				UIConfiguration.FORMAT_DATE_TIME_SEC.format(date),
				UIConfiguration.FORMAT_DATE_TIME_SEC.format(date) + " created by PowerEditor",
				date);
		int newID = BizActionCoordinator.getInstance().save(ds, user);
		ds.setID(newID);
		return ds;

	}

	public static int[] extractIDs(Persistent[] array) {
		if (array == null) return null;
		int[] ids = new int[array.length];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = array[i].getID();
		}
		return ids;
	}

	/**
	 * @param ruleset
	 * @return GenericEntityIdentity
	 * @since 3.0.0
	 */
	public static GenericEntityIdentity extractIdentity(RuleSet ruleset) {
		GenericEntityIdentity entityIdentity = null;
		if (ruleset.hasGenericEntityContext()) {
			GenericEntityType[] types = ruleset.getGenericEntityTypesInUse();
			entityIdentity = new GenericEntityIdentity(types[0].getID(), ruleset.getGenericEntityID(types[0]));
		}
		return entityIdentity;
	}

	private static int getCategoryTypeID(GenericEntityType type) {
		CategoryTypeDefinition catDef = ConfigurationManager.getInstance().getEntityConfiguration().getCategoryDefinition(type);
		return (catDef == null ? -1 : catDef.getTypeID());
	}

	private static <T extends AbstractIDObject> int[] toIDs(List<T> objectList) {
		int[] ids = new int[objectList.size()];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = objectList.get(i).getID();
		}
		return ids;
	}

	private static String[] toStringRepresentation(TemplateUsageType[] usages) {
		if (usages == null) return null;
		String[] strs = new String[usages.length];
		for (int i = 0; i < strs.length; i++) {
			strs[i] = usages[i].toString();
		}
		return strs;
	}

	private static MutableTimedAssociationKey[] extractParentAssociations(GenericCategory category) {
		List<MutableTimedAssociationKey> list = new ArrayList<MutableTimedAssociationKey>();
		for (Iterator<MutableTimedAssociationKey> iter = category.getParentKeyIterator(); iter.hasNext();) {
			MutableTimedAssociationKey element = iter.next();
			list.add(element);
		}
		return list.toArray(new MutableTimedAssociationKey[0]);
	}

	private static MutableTimedAssociationKey[] extractCategoryAssociations(GenericEntity entity) {
		List<MutableTimedAssociationKey> list = new ArrayList<MutableTimedAssociationKey>();
		for (Iterator<MutableTimedAssociationKey> iter = entity.getCategoryIterator(); iter.hasNext();) {
			MutableTimedAssociationKey element = iter.next();
			list.add(element);
		}
		return list.toArray(new MutableTimedAssociationKey[0]);
	}

	/**
	 *  Several BizActionCoordinator methods want to manage transaction boundaries explicitly, 
	 *  Use {@link #getLocallyManagedConnection()}, {@link java.sql.Connection#commit()}, 
	 *  {@link #rollBackLocallyManagedConnection(Connection)}, and {@link #freeLocallyManagedConnection(Connection)}
	 *  for this purpose using the following idiom:
	 *  
	 *  <code>
	 *     Connection conn = null;
	 *     try {
	 *        conn = getLocallyManagedConnection();
	 *        // do some stuff
	 *        conn.commit();
	 *     } 
	 *     catch (anything that might go wrong) {
	 *        rollbackLocallyManagedConnection(conn);
	 *        // log, rethrow
	 *     }
	 *     finally {
	 *        freeLocallyManagedConnection(conn);
	 *     }
	 * </code>
	 */
	static Connection getLocallyManagedConnection() throws ServletActionException {
		Connection conn = null;
		try {
			conn = DBConnectionManager.getInstance().getConnection();
			if (conn == null) {
				throw new ServletActionException("ServerError", "Data Connection could not be obtained.");
			}
			conn.setAutoCommit(false);
			return conn;
		}
		catch (SQLException e) {
			Logger.getLogger("com.mindbox.server.db").error("Failed to obtain a database connection.", e);
			DBConnectionManager.getInstance().freeConnection(conn);
			throw new ServletActionException("ServerError", "Failed to obtain a database connection.");
		}
	}

	/** @see {@link #getLocallyManagedConnection()} */
	static void freeLocallyManagedConnection(Connection conn) throws ServletActionException {
		DBConnectionManager.getInstance().freeConnection(conn);
	}

	/** @see {@link #getLocallyManagedConnection()} */
	private static final void rollBackLocallyManagedConnection(Connection conn) {
		DBUtil.rollBackLocallyManagedConnection(conn);
	}

	/**
	 * Convenience method to wrap validation violations with a {@link DataValidationFailedException}.
	 * @param objectToValidate
	 * @throws DataValidationFailedException
	 * @see ValidationViolation
	 * @see DataValidator
	 */
	public static final void validateData(Object objectToValidate) throws DataValidationFailedException {
		List<ValidationViolation> violations = DataValidatorFactory.getInstance().getDataValidator().validate(objectToValidate);
		if (!UtilBase.isEmpty(violations)) {
			throw new DataValidationFailedException(violations);
		}
	}

	private final Logger logger = Logger.getLogger(BizActionCoordinator.class);
	private final UserManagementProvider userDataUpdater;
	private final ParameterUpdater parameterUpdater;
	private Date serverStartDate = null;

	/**
	 * 
	 */
	private BizActionCoordinator() {
		super();
		this.userDataUpdater = ServiceProviderFactory.getUserManagementProvider();
		this.parameterUpdater = new ParameterUpdater();
	}

	public void validateTemplateForImport(GridTemplate template, boolean checkColumnNames, boolean merge) throws ImportException {
		if (UtilBase.isEmpty(template.getName())) throw new ImportException("Template " + template.getID() + " has no name.");
		// TT 2000
		// check for unique template name
		boolean templateExists = (GuidelineTemplateManager.getInstance().getTemplate(template.getID()) != null);
		if (merge || !templateExists) {
			// TODO [GKIM] Merge with 6.0 branch
			if (!isUniqueTemplateNameAndVersion(template.getName(), template.getVersion())) {
				throw new ImportException(String.format(
						"Template with name %s and version %s already exists (ID was %s).",
						template.getName(),
						template.getVersion(),
						template.getID()));
			}
		}

		List<String> columnNameList = new ArrayList<String>();
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

	// TODO [GKIM] Merge with 6.0 branch

	public boolean isUniqueTemplateNameAndVersion(String templateName, String templateVersion) {
		List<GridTemplate> templatesWithSameName = GuidelineTemplateManager.getInstance().getTemplatesByName(templateName);
		if (templatesWithSameName != null && !templatesWithSameName.isEmpty()) {
			for (GridTemplate gridTemplate : templatesWithSameName) {
				if (gridTemplate.getVersion() != null && gridTemplate.getVersion().equals(templateVersion)) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean isUniqueName(EntityType entityType, String name) {
		// Add more types as necessary
		if (entityType == EntityType.TEMPLATE) {
			return GuidelineTemplateManager.getInstance().getTemplatesByName(name).isEmpty();
		}
		else if (entityType == EntityType.DATE_SYNONYM) {
			return !DateSynonymManager.getInstance().hasDateSynonymWithName(name);
		}
		else if (entityType == EntityType.ROLE) {
			return SecurityCacheManager.getInstance().getRole(name) == null;
		}
		else if (entityType == EntityType.USER_DATA) {
			return SecurityCacheManager.getInstance().getUser(name) == null;
		}
		return true;
	}

	public void setServerStartDate(Date serverStartDate) {
		this.serverStartDate = serverStartDate;
	}

	public synchronized void reloadConfiguration() throws ServerException {
		// [1] block incoming requests to server
		ServerControl.setStatusToReloading();
		try {
			// [2] refresh cache, including user info.
			Loader.loadToCache(false);

			// [3] mark existing session for re-login
			SessionManager.getInstance().markAllSessionForRefresh();
		}
		finally {
			// [4] release block
			ServerControl.setStatusToRunning();
		}
	}

	public synchronized List<UserData> reloadUserData() throws ServerException {
		// [1] block incoming requests to server
		ServerControl.setStatusToReloading();
		try {
			// [2] refresh cache
			Loader.loadUserData();
			List<UserData> userList = new ArrayList<UserData>();
			for (Iterator<User> iter = SecurityCacheManager.getInstance().getUsers(); iter.hasNext();) {
				userList.add(iter.next().asUserData());
			}
			return userList;
		}
		finally {
			// [3] release block
			ServerControl.setStatusToRunning();
		}
	}

	public void importEntityCompatibilityData(GenericEntityCompatibilityData compData, User user) throws ImportException,
			DataValidationFailedException {
		logger.debug(">>> importEntity: " + compData);
		validateData(compData);
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
				String ids = compData.getSourceType() + "." + compData.getSourceID() + " and " + compData.getGenericEntityType() + "."
						+ compData.getAssociableID();
				logger.warn("Failed to save compatibility, duplicate found for " + ids);
				throw new ImportException("Warning: Duplicate compatibility found for " + ids + ".  Only the first was saved.");
			}
		}
		catch (ImportException ie) {
			throw ie;
		}
		catch (Exception ex) {
			logger.error("Failed to save " + compData, ex);
			throw new ImportException("Failed to store the compatibility into the DB: " + ex.getMessage());
		}
	}

	/** @throws DataValidationFailedException 
	 * @since 3.0.0 */
	public void save(GenericEntityCompatibilityData newCompData, User user) throws ServletActionException, DataValidationFailedException {
		// TBD: Place to fix TT 1262
		validateData(newCompData);
		try {
			GenericEntityCompatibilityData copyOfCache = EntityManager.getInstance().isCached(newCompData);
			if (copyOfCache != null) {
				copyOfCache.setEffectiveDate(newCompData.getEffectiveDate());
				copyOfCache.setExpirationDate(newCompData.getExpirationDate());

				ServiceProviderFactory.getPEDataUpdater().getGenericEntityDataUpdater().updateEntityCompatibility(
						copyOfCache.getSourceType().getID(),
						copyOfCache.getSourceID(),
						copyOfCache.getGenericEntityType().getID(),
						copyOfCache.getAssociableID(),
						copyOfCache.getEffectiveDate(),
						copyOfCache.getExpirationDate());

				EntityManager.getInstance().updateEntityCompatibility(copyOfCache);
				AuditLogger.getInstance().logUpdate(newCompData, copyOfCache, user.getUserID());
			}
			else {
				ServiceProviderFactory.getPEDataUpdater().getGenericEntityDataUpdater().insertEntityCompatibility(
						newCompData.getSourceType().getID(),
						newCompData.getSourceID(),
						newCompData.getGenericEntityType().getID(),
						newCompData.getAssociableID(),
						newCompData.getEffectiveDate(),
						newCompData.getExpirationDate());

				EntityManager.getInstance().insertEntityCompatibility(newCompData);
				AuditLogger.getInstance().logNew(newCompData, user.getUserID());
			}
		}
		catch (SQLException ex) {
			logger.error("Failed to save " + newCompData, ex);
			throw new ServletActionException("ServerError", "Failed to stor the compatibility into the DB: " + ex.getMessage());
		}
	}

	/**
	 * Deletes a compatiblity. First finds the cached compatibility which will contain the correct sequence
	 * of keys which is nessesssry for the DB operation.  
	 * @param compData
	 * @param user
	 * @throws ServletActionException
	 * @since 3.0.0 
	 */
	public void delete(GenericEntityCompatibilityData compData, User user) throws ServletActionException {
		GenericEntityCompatibilityData cachedCompData = EntityManager.getInstance().isCached(compData);
		if (cachedCompData == null) {
			logger.error("Failed to delete " + compData + ". Unable to locate in cache");
			throw new ServletActionException("ServerError", "Failed to delete compatibility. Unable to locate compatibility in cache.");
		}
		try {
			ServiceProviderFactory.getPEDataUpdater().getGenericEntityDataUpdater().deleteEntityCompatibility(
					cachedCompData.getSourceType().getID(),
					cachedCompData.getSourceID(),
					cachedCompData.getGenericEntityType().getID(),
					cachedCompData.getAssociableID());

			EntityManager.getInstance().removeEntityCompatibility(
					cachedCompData.getSourceType(),
					cachedCompData.getSourceID(),
					cachedCompData.getGenericEntityType(),
					cachedCompData.getAssociableID());
			AuditLogger.getInstance().logDelete(compData, user.getUserID());
		}
		catch (SQLException ex) {
			logger.error("Failed to delete " + cachedCompData, ex);
			throw new ServletActionException("ServerError", "Failed to delete compatibility from the DB: " + ex.getMessage());
		}
	}

	/**
	 * @param type1
	 * @param type2
	 * @return list of compatible entities
	 * @throws ServletActionException
	 * @since 3.0.0
	 */
	public List<GenericEntityCompatibilityData> fetchCompatibilityData(GenericEntityType type1, GenericEntityType type2)
			throws ServletActionException {
		return EntityManager.getInstance().getAllCrossCompatibilities(type1, type2);
	}

	public Long getNextRuleID() throws ServletActionException {
		try {
			return new Long(DBIdGenerator.getInstance().nextRuleID());
		}
		catch (SapphireException e) {
			throw new ServletActionException("ServerError", e.getMessage());
		}
	}

	/**
	 * Gets a new id for merge for the specified object class.
	 * 
	 * @return new id
	 * @throws SapphireException
	 *             on error
	 */
	public int generateNewIDForMerge() throws SapphireException {
		return DBIdGenerator.getInstance().nextSequentialID();
	}

	public void importCategory(GenericCategory category, boolean merge, User user) throws ImportException, DataValidationFailedException {
		logger.info(">>> importCategory(GenericCategory) with " + category);
		validateData(category);
		int categoryID = category.getID();
		try {
			if (!merge && categoryID != -1 && EntityManager.getInstance().hasGenericCategory(category.getType(), categoryID)) {
				update(category, user);
			}
			else {
				// 1. insert category row into DB
				GenericEntityUpdater genericEntityUpdater = new GenericEntityUpdater();
				MutableTimedAssociationKey[] keys = extractParentAssociations(category);
				genericEntityUpdater.addCategory(categoryID, category.getName(), category.getType(), keys);

				if (logger.isInfoEnabled())
					logger.info(">>> importCategory: DB updated; keys.size=" + (keys == null ? 0 : keys.length)
							+ ". inserting into cache...");
				// 2. insert category into server cache
				EntityManager.getInstance().addGenericEntityCategory(category.getType(), categoryID, category.getName());
				EntityManager.getInstance().addParentAssociations(category);

				if (user != null) {
					AuditLogger.getInstance().logImport(category, user.getUserID());
				}
			}
		}
		catch (Exception ex) {
			logger.error("Failed to import generic category " + category, ex);
			throw new ImportException("Failed to store the generic category into the DB: " + ex.getMessage());
		}
	}

	public int save(GenericCategory category, boolean validate, User user) throws ServletActionException, DataValidationFailedException {
		if (logger.isDebugEnabled()) logger.debug(">>> save(GenericCategory) with " + category + "," + validate);
		if (validate) validateData(category);
		int categoryID = category.getID();
		if (categoryID != -1) {
			update(category, user);
			return categoryID;
		}

		try {
			// 1. Get a new category ID
			categoryID = DBIdGenerator.getInstance().nextSequentialID();
			category.setID(categoryID);

			logger.debug("    creating new generic category: " + categoryID);

			// 2. insert category row into DB
			GenericEntityUpdater genericEntityUpdater = new GenericEntityUpdater();
			genericEntityUpdater.addCategory(categoryID, category.getName(), category.getType(), extractParentAssociations(category));

			// 3. insert category into server cache
			EntityManager.getInstance().addGenericEntityCategory(category.getType(), categoryID, category.getName());
			EntityManager.getInstance().addParentAssociations(category);

			if (user != null) {
				AuditLogger.getInstance().logNew(category, user.getUserID());
			}
			return categoryID;
		}
		catch (SQLException ex) {
			throw new ServletActionException("ServerError", "Failed to store the category into the DB: " + ex.getMessage());
		}
		catch (SapphireException ex) {
			throw new ServletActionException("ServerError", "Failed to get the next category ID from database: " + ex.getMessage());
		}
	}

	public GenericCategory update(GenericCategory category, User user) throws ServletActionException {
		logger.debug(">>> update(GenericCategory) with " + category);

		int categoryID = category.getID();
		try {
			GenericCategory categoryFromCache = (GenericCategory) EntityManager.getInstance().getGenericCategory(
					category.getType(),
					category.getID());

			// 2. update category DB row
			GenericEntityUpdater genericEntityUpdater = new GenericEntityUpdater();
			genericEntityUpdater.updateCategory(categoryID, category.getName(), category.getType(), extractParentAssociations(category));

			logger.debug("    updated category. udating cache...");

			// 3. update cache
			EntityManager.getInstance().updateCache(categoryFromCache, category);
			AuditLogger.getInstance().logUpdate(category, categoryFromCache, user.getUserID());

			logger.debug("    update completed. returning " + category);
			return category;
		}
		catch (SQLException ex) {
			throw new ServletActionException("ServerError", "Failed to update the category into the DB: " + ex.getMessage());
		}
	}

	/**
	 * This method is called only during Import. Root categories are created by PE during start up, if nothing existed in the DB.
	 * This method updates the root categories display value from the import file
	 * @author vineet khosla
	 * @since PowerEditor 5.0.0
	 * @param type
	 * @param newName
	 * @param user
	 * @throws ImportException
	 */
	public void updateRootCategoryDuringImport(String type, String newName, User user) throws ImportException {
		CategoryTypeDefinition ctd = ConfigurationManager.getInstance().getEntityConfiguration().getCategoryDefinition(
				GenericEntityType.forName(type));
		if (ctd == null) throw new ImportException("No category defined for entity type " + type);
		try {
			// 1. get root category
			GenericCategory rootCategoryFromCache = (GenericCategory) EntityManager.getInstance().getGenericCategoryRoot(ctd.getTypeID());
			if (!rootCategoryFromCache.getName().equals(newName)) {
				logger.debug(">>> update root(GenericCategory) with " + newName);

				// 2. update category DB row
				GenericEntityUpdater genericEntityUpdater = new GenericEntityUpdater();
				genericEntityUpdater.updateCategoryName(rootCategoryFromCache.getID(), rootCategoryFromCache.getType(), newName);

				logger.debug("    updated root category. udating cache...");

				// 3. update cache
				rootCategoryFromCache.setName(newName);
				// audit with the same before and after
				AuditLogger.getInstance().logImport(rootCategoryFromCache, user.getUserID());
			}
		}
		catch (SQLException ex) {
			throw new ImportException("Failed to update the root category into the DB: " + ex.getMessage());
		}
	}

	private void deleteProcessRequest(int requestID, User user) throws ServletActionException {
		logger.debug(">>> deleteProcessRequest with " + requestID);
		try {
			if (ProcessManager.getInstance().getRequest(requestID) == null) {
				throw new SQLException("Request " + requestID + " not found");
			}

			ProcessUpdater updater = new ProcessUpdater();
			updater.deleteRequest(requestID);

			ProcessRequest cachedRequest = ProcessManager.getInstance().getRequest(requestID);
			logger.debug("deleteProcessRequest: removing from cache");

			ProcessManager.getInstance().removeRequest(requestID);

			AuditLogger.getInstance().logDelete(cachedRequest, user.getUserID());

			logger.debug("<<< deleteProcessRequest");
		}
		catch (SQLException ex) {
			logger.error("Failed to delete request " + requestID, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	public int save(ProcessRequest request, User user) throws ServletActionException {
		logger.debug(">>> save(ProcessRequest) with " + request);

		int requestID = request.getID();
		if (requestID != -1) {
			update(request, user);
			return requestID;
		}
		else {
			return create(request, user);
		}
	}

	private int create(ProcessRequest request, User user) throws ServletActionException {
		try {
			// 1. Get a new request ID
			int requestID = DBIdGenerator.getInstance().nextSequentialID();
			request.setID(requestID);

			logger.debug("    creating new ProcessRequest: " + requestID);

			// 2. insert request row into DB

			ProcessUpdater updater = new ProcessUpdater();
			updater.insertRequest(
					requestID,
					request.getName(),
					request.getRequestType(),
					request.getDisplayName(),
					request.getDescription(),
					request.getInitFunction(),
					request.getPurpose(),
					(request.getPhase() == null ? Persistent.UNASSIGNED_ID : request.getPhase().getID()));

			// 3. insert request into server cache
			ProcessManager.getInstance().addRequest(request);

			AuditLogger.getInstance().logNew(request, user.getUserID());
			return requestID;
		}
		catch (SQLException ex) {
			throw new ServletActionException("ServerError", "Failed to store the ProcessRequest into the DB: " + ex.getMessage());
		}
		catch (SapphireException ex) {
			throw new ServletActionException("ServerError", "Failed to the next ProcessRequest ID from database: " + ex.getMessage());
		}
	}

	private void update(ProcessRequest request, User user) throws ServletActionException {
		logger.debug(">>> update(ProcessRequest) with " + request);

		int requestID = request.getID();
		try {
			ProcessRequest requestFromCache = ProcessManager.getInstance().getRequest(requestID);

			// 1. update request DB row
			ProcessUpdater updater = new ProcessUpdater();
			updater.updateRequest(
					requestID,
					request.getName(),
					request.getRequestType(),
					request.getDisplayName(),
					request.getDescription(),
					request.getInitFunction(),
					request.getPurpose(),
					(request.getPhase() == null ? Persistent.UNASSIGNED_ID : request.getPhase().getID()));

			logger.debug("    updated ProcessRequest. updating cache...");

			AuditLogger.getInstance().logUpdate(request, requestFromCache, user.getUserID());

			// 2. update cache
			ProcessManager.getInstance().updateRequest(request);

			logger.debug("    update completed.");
		}
		catch (Exception ex) {
			throw new ServletActionException("ServerError", "Failed to update the ProcessRequest into the DB: " + ex.getMessage());
		}
	}

	private void deleteProcessPhase(int phaseID, User user) throws ServletActionException {
		logger.debug(">>> deleteProcessPhase with " + phaseID);
		try {
			if (ProcessManager.getInstance().getPhase(phaseID) == null) {
				throw new SQLException("Phase " + phaseID + " not found");
			}

			ProcessUpdater updater = new ProcessUpdater();
			updater.deletePhase(phaseID);

			Phase cachedPhase = ProcessManager.getInstance().getPhase(phaseID);
			logger.debug("deleteProcessPhase: removing from cache");

			ProcessManager.getInstance().removePhase(phaseID);

			AuditLogger.getInstance().logDelete(cachedPhase, user.getUserID());

			logger.debug("<<< deleteProcessPhase");
		}
		catch (SQLException ex) {
			logger.error("Failed to delete phase " + phaseID, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	private void deleteDateSynonym(int synonymID, User user) throws ServletActionException {
		Connection conn = null;
		try {
			conn = getLocallyManagedConnection();
			deleteDateSynonym(synonymID, user, conn);
			conn.commit();
		}
		catch (SQLException ex) {
			logger.error("Failed to delete DateSynonym " + synonymID, ex);
			rollBackLocallyManagedConnection(conn);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
		finally {
			freeLocallyManagedConnection(conn);
		}
	}

	private void deleteDateSynonym(int synonymID, User user, Connection conn) throws ServletActionException {
		logger.debug(">>> deleteDateSynonym with " + synonymID);
		try {
			LockManager.getInstance().getExistingLock(EntityType.DATE_SYNONYM, synonymID, user);
			if (DateSynonymManager.getInstance().getDateSynonym(synonymID) == null) {
				throw new SQLException("date synonym" + synonymID + " not found");
			}

			DateSynonymUpdater updater = new DateSynonymUpdater(conn);
			updater.deleteDateSynonym(synonymID);

			DateSynonym labelFromCache = DateSynonymManager.getInstance().getDateSynonym(synonymID);
			logger.debug("deleteDateSynonym: removing from cache");

			DateSynonymManager.getInstance().remove(synonymID);

			AuditLogger.getInstance().logDelete(labelFromCache, user.getUserID());

			logger.debug("<<< deleteDateSynonym");
		}
		catch (LockException ex) {
			throw new ServletActionException("LockFailureMsg", ex.getLockedBy());
		}
		catch (SQLException ex) {
			logger.error("Failed to delete date synonym " + synonymID, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	private void deleteActionTypeDefinition(int actionID, User user) throws ServletActionException {
		logger.debug(">>> deleteActionTypeDefinition with " + actionID);
		try {
			ActionTypeDefinition actionDef = GuidelineFunctionManager.getInstance().getActionTypeDefinition(actionID);
			if (actionDef == null) {
				throw new SQLException("Action " + actionID + " not found");
			}

			List<GridTemplate> allTemplates = GuidelineTemplateManager.getInstance().getAllTemplates();
			for (GridTemplate template : allTemplates) {
				RuleDefinition ruleDef = template.getRuleDefinition();
				if (ruleDef != null && ruleDef.getActionTypeID() == actionID)
					throw new SQLException("Action \"" + actionDef.getName() + "\' is used by template \"" + template.getName() + "\"");
				List<GridTemplateColumn> columns = template.getColumns();
				for (Iterator<GridTemplateColumn> it2 = columns.iterator(); it2.hasNext();) {
					GridTemplateColumn column = it2.next();
					ruleDef = column.getRuleDefinition();
					if (ruleDef != null && ruleDef.getActionTypeID() == actionID)
						throw new SQLException("Action \"" + actionDef.getName() + "\' is used by template \"" + template.getName() + "\"");
				}
			}

			GuidelineActionUpdater updater = new GuidelineActionUpdater();
			updater.deleteAction(actionID);

			ActionTypeDefinition actionFromCache = GuidelineFunctionManager.getInstance().getActionTypeDefinition(actionID);
			logger.debug("deleteActionTypeDefinition: removing from cache");

			GuidelineFunctionManager.getInstance().removeActionTypeDefinition(actionID);

			AuditLogger.getInstance().logDelete(actionFromCache, user.getUserID());

			logger.debug("<<< deleteActionTypeDefinition");
		}
		catch (SQLException ex) {
			logger.error("Failed to delete action " + actionID, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	private void deleteTestTypeDefinition(int testID, User user) throws ServletActionException {
		logger.debug(">>> deleteTestTypeDefinition with " + testID);
		try {
			TestTypeDefinition actionDef = GuidelineFunctionManager.getInstance().getTestTypeDefinition(testID);
			if (actionDef == null) {
				throw new SQLException("Test Condition " + testID + " not found");
			}

			List<GridTemplate> allTemplates = GuidelineTemplateManager.getInstance().getAllTemplates();
			for (GridTemplate template : allTemplates) {
				RuleDefinition ruleDef = template.getRuleDefinition();
				if (ruleDef != null) {
					for (Iterator<TestCondition> it3 = ruleDef.getTestConditions().iterator(); it3.hasNext();) {
						TestCondition tc = it3.next();
						if (tc.getTestType().getId() == testID)
							throw new SQLException("Test \"" + actionDef.getName() + "\' is used by template \"" + template.getName()
									+ "\"");
					}
				}
				List<GridTemplateColumn> columns = template.getColumns();
				for (Iterator<GridTemplateColumn> it2 = columns.iterator(); it2.hasNext();) {
					GridTemplateColumn column = it2.next();
					ruleDef = column.getRuleDefinition();
					if (ruleDef != null) {
						for (Iterator<TestCondition> it3 = ruleDef.getTestConditions().iterator(); it3.hasNext();) {
							TestCondition tc = it3.next();
							if (tc.getTestType().getId() == testID)
								throw new SQLException("Test \"" + actionDef.getName() + "\' is used by template \"" + template.getName()
										+ "\"");
						}
					}

				}
			}

			GuidelineTestConditionUpdater updater = new GuidelineTestConditionUpdater();
			updater.deleteTest(testID);

			TestTypeDefinition actionFromCache = GuidelineFunctionManager.getInstance().getTestTypeDefinition(testID);
			logger.debug("deleteTestTypeDefinition: removing from cache");

			GuidelineFunctionManager.getInstance().removeTestTypeDefinition(testID);

			AuditLogger.getInstance().logDelete(actionFromCache, user.getUserID());

			logger.debug("<<< deleteTestTypeDefinition");
		}
		catch (SQLException ex) {
			logger.error("Failed to delete action " + testID, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	public int save(DateSynonym dateSynonym, User user) throws ServletActionException, DataValidationFailedException {
		logger.debug(">>> save(DateSynonym) with " + dateSynonym);
		validateData(dateSynonym);
		int synonymID = dateSynonym.getID();
		if (synonymID != -1) {
			update(dateSynonym, user);
			return synonymID;
		}
		else {
			return create(dateSynonym, user);
		}
	}

	private int create(DateSynonym dateSynonym, User user) throws ServletActionException {
		Connection conn = null;
		try {
			conn = getLocallyManagedConnection();
			int id = create(dateSynonym, user, conn);
			conn.commit();
			return id;
		}
		catch (SQLException e) {
			rollBackLocallyManagedConnection(conn);
			logger.error("Failed to create DateSynonym", e);
			throw new ServletActionException("ServerError", e.getMessage());
		}
		finally {
			freeLocallyManagedConnection(conn);
		}
	}

	private int create(DateSynonym dateSynonym, User user, Connection conn) throws ServletActionException {
		try {
			// 1. Get a new date synonym ID
			int synonymID = DBIdGenerator.getInstance().nextSequentialID();
			dateSynonym.setID(synonymID);

			logger.debug("    creating new date synonym: " + synonymID);

			// 2. insert date synonym row into DB
			DateSynonymUpdater updater = new DateSynonymUpdater(conn);
			updater.insertDateSynonym(
					synonymID,
					dateSynonym.getName(),
					dateSynonym.getDescription(),
					dateSynonym.getDate(),
					dateSynonym.isNamed());

			// 3. insert date synonym into server cache
			DateSynonymManager.getInstance().insert(dateSynonym);

			AuditLogger.getInstance().logNew(dateSynonym, user.getUserID());
			return synonymID;
		}
		catch (SQLException ex) {
			throw new ServletActionException("ServerError", "Failed to store the date synonym into the DB: " + ex.getMessage());
		}
		catch (SapphireException ex) {
			throw new ServletActionException("ServerError", "Failed to the next date synonym ID from database: " + ex.getMessage());
		}
	}

	private void update(DateSynonym dateSynonym, User user) throws ServletActionException {
		logger.debug(">>> update(DateSynonym) with " + dateSynonym);

		int synonymID = dateSynonym.getID();
		try {
			lockEntity(EntityType.DATE_SYNONYM, synonymID, user);
			DateSynonym dateSynonymFromCache = DateSynonymManager.getInstance().getDateSynonym(synonymID);

			// 1. update date synonym DB row
			DateSynonymUpdater updater = new DateSynonymUpdater();
			updater.updateDateSynonym(
					synonymID,
					dateSynonym.getName(),
					dateSynonym.getDescription(),
					dateSynonym.getDate(),
					dateSynonym.isNamed());

			logger.debug("    updated date synonym row. updating cache...");

			AuditLogger.getInstance().logUpdate(dateSynonym, dateSynonymFromCache, user.getUserID());

			// 2. update cache
			DateSynonymManager.getInstance().update(dateSynonym);
			// TT 2029  
			EntityManager.getInstance().updateCategoryAssociationDateSynonyms(dateSynonym);
			EntityManager.getInstance().updateEntityAssociationDateSynonyms(dateSynonym);

			unlockEntity(EntityType.DATE_SYNONYM, synonymID, user);

			logger.debug("    update completed.");
		}
		catch (SQLException ex) {
			throw new ServletActionException("ServerError", "Failed to update the date synonym in the DB: " + ex.getMessage());
		}
	}

	/**
	 * Imports the specified guideline action definition.
	 * 
	 * @param actionDef
	 *            guideline action definition to import
	 * @param merge
	 *            merge flag
	 * @param actionIDMap
	 *            action id map
	 * @param user
	 * @param updateIfExist
	 *            when the specified action already exists, if set to <code>true</code>, existing
	 *            action is updated; otherwise, a ServletActionException is thrown
	 * @throws ServletActionException
	 * @throws ParseException
	 */
	public void importActionTypeDefinition(ActionTypeDefinition actionDef, boolean merge, Map<String, Integer> actionIDMap, User user,
			boolean updateIfExist) throws ServletActionException, ParseException {
		logger.debug(">>> importActionTypeDefinition: " + actionDef + ",merge?=" + merge);
		if (merge) {
			try {
				int newID = DBIdGenerator.getInstance().nextSequentialID();
				actionIDMap.put("action:" + actionDef.getId(), newID);
				logger.debug("    importActionTypeDefinition: new action id = " + newID);
				actionDef.setID(newID);
			}
			catch (SapphireException ex) {
				logger.error("Failed to generate new id for action " + actionDef, ex);
				throw new ServletActionException("msg.error.failure.import.action", ex.getMessage());
			}
		}
		if (!merge && GuidelineFunctionManager.getInstance().getActionTypeDefinition(actionDef.getID()) != null) {
			if (updateIfExist) {
				update(actionDef, user);
			}
			else {
				throw new ServletActionException("msg.error.failure.import.action", "Action Definition already exists");
			}
		}
		else {
			try {
				logger.debug("    creating new action: " + actionDef.getID());

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
						toStringRepresentation(actionDef.getUsageTypes()));

				// audit log new action
				AuditLogger.getInstance().logImport(actionDef, user.getUserID());
			}
			catch (SQLException ex) {
				// this is required as we attempt to insert the action into the cache
				// before attempt to persist it in DB
				if (GuidelineFunctionManager.getInstance().getActionTypeDefinition(actionDef.getID()) != null) {
					GuidelineFunctionManager.getInstance().removeActionTypeDefinition(actionDef.getID());
				}
				throw new ServletActionException("ServerError", "Failed to store the action into the DB: " + ex.getMessage());
			}
		}
	}

	/**
	 * Imports the specified test condition.
	 * 
	 * @param testDef
	 *            the test condition to import
	 * @param user
	 * @param updateIfExist
	 *            when the specified test condition already exists, if set to <code>true</code>,
	 *            existing test condition is updated; otherwise, a ServletActionException is thrown
	 * @throws ServletActionException
	 * @throws ParseException
	 */
	public void importTestTypeDefinition(TestTypeDefinition testDef, boolean merge, Map<String, Integer> actionIDMap, User user,
			boolean updateIfExist) throws ServletActionException, ParseException {
		logger.debug(">>> importTestTypeDefinition: " + testDef + ",merge?=" + merge);
		if (merge) {
			try {
				int newID = DBIdGenerator.getInstance().nextSequentialID();
				actionIDMap.put("test:" + testDef.getId(), newID);
				logger.debug("    importTestTypeDefinition: new test id = " + newID);
				testDef.setID(newID);
			}
			catch (SapphireException ex) {
				logger.error("Failed to generate new id for test " + testDef, ex);
				throw new ServletActionException("msg.error.failure.import.test", ex.getMessage());
			}
		}
		if (!merge && GuidelineFunctionManager.getInstance().getTestTypeDefinition(testDef.getID()) != null) {
			if (updateIfExist) {
				update(testDef, user);
			}
			else {
				throw new ServletActionException("msg.error.failure.import.test", "Test Definition already exists");
			}
		}
		else {
			try {
				logger.debug("    creating new test: " + testDef.getID());

				// 1. insert test into server cache
				GuidelineFunctionManager.getInstance().insertTestTypeDefinition(testDef);

				// 2. insert test row into DB
				GuidelineTestConditionUpdater updater = new GuidelineTestConditionUpdater();
				updater.insertTest(
						testDef.getID(),
						testDef.getName(),
						testDef.getDescription(),
						testDef.getDeploymentRule(),
						testDef.getParameterDefinitions());

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

	/**
	 * Persist the specified action definition and updates action cache accordingly.
	 * 
	 * @param actionDef
	 *            the action to save
	 * @param user
	 * @return the new action ID
	 * @throws ServletActionException
	 * @throws ServletActionException
	 *             if actionDef conains a rule that is not parsable; when this is thrown, the
	 *             actionDef will not be inserted or updated
	 */
	public int save(ActionTypeDefinition actionDef, User user) throws ServletActionException {
		logger.debug(">>> save(ActionTypeDefinition) with " + actionDef);

		int actionID = actionDef.getID();
		try {
			if (actionID != -1) {
				update(actionDef, user);
				return actionID;
			}
			else {
				return create(actionDef, user);
			}
		}
		catch (ParseException x) {
			throw new ServletActionException("ServerError", "Error Saving Guideline Action: " + x.getMessage());
		}
	}

	public int save(TestTypeDefinition testDef, User user) throws ServletActionException {
		logger.debug(">>> save(TestTypeDefinition) with " + testDef);

		int testID = testDef.getID();
		try {
			if (testID != -1) {
				update(testDef, user);
				return testID;
			}
			else {
				return create(testDef, user);
			}
		}
		catch (ParseException x) {
			throw new ServletActionException("ServerError", "Error Saving Guideline Test: " + x.getMessage());
		}
	}

	/**
	 * 
	 * @param usageType
	 * @param deployRule
	 * @return the action type definition
	 * @throws ServletActionException
	 */
	public ActionTypeDefinition findActionTypeDefinitionWithDeploymentRule(TemplateUsageType usageType, String deployRule)
			throws ServletActionException {
		logger.debug(">>> findActionTypeDefinitionWithDeploymentRule: " + usageType + "," + deployRule);
		String deployRuleToCheck = (deployRule == null ? "" : deployRule.trim());
		// look for all action types, not just those associated with the specified usage type
		List<ActionTypeDefinition> actionTypeList = GuidelineFunctionManager.getInstance().getAllActionTypes();
		for (ActionTypeDefinition element : actionTypeList) {
			if ((Util.isEmpty(element.getDeploymentRule()) && deployRuleToCheck.length() == 0)
					|| (element.getDeploymentRule() != null && element.getDeploymentRule().trim().equals(deployRuleToCheck))) {
				// if element is not associated with the usage type, link them together
				if (!element.hasUsageType(usageType)) {
					element.addUsageType(usageType);
					BizActionCoordinator.getInstance().saveNoCacheUpdate(element);
				}
				return element;
			}
		}
		return null;
	}

	private void saveNoCacheUpdate(ActionTypeDefinition actionDef) throws ServletActionException {
		logger.debug(">>> saveNoCacheUpdate(actionDef): " + actionDef);
		Connection conn = null;
		try {
			conn = DBConnectionManager.getInstance().getConnection();
			conn.setAutoCommit(false);
			GuidelineActionUpdater updater = new GuidelineActionUpdater();
			updater.updateAction(
					conn,
					actionDef.getID(),
					actionDef.getName(),
					actionDef.getDescription(),
					actionDef.getDeploymentRule(),
					actionDef.getParameterDefinitions(),
					toStringRepresentation(actionDef.getUsageTypes()));
			conn.commit();
		}
		catch (Exception ex) {
			logger.error("Error: rolling back changes and restoring cache from DB", ex);
			try {
				conn.rollback();
			}
			catch (SQLException e) {
				logger.warn("Failed to roll back changes", e);
			}
			throw new ServletActionException("ServerError", "Failed to update the action in the DB: " + ex.getMessage());
		}
		finally {
			DBConnectionManager.getInstance().freeConnection(conn);
		}
	}

	private int create(ActionTypeDefinition actionDef, User user) throws ServletActionException, ParseException {
		try {
			// 1. Get a new action ID
			int actionID = DBIdGenerator.getInstance().nextSequentialID();
			actionDef.setID(actionID);

			logger.debug("    creating new action: " + actionID);

			// 2. insert action into server cache
			GuidelineFunctionManager.getInstance().insertActionTypeDefinition(actionDef);

			// 3. insert action row into DB
			GuidelineActionUpdater updater = new GuidelineActionUpdater();
			updater.insertAction(
					actionID,
					actionDef.getName(),
					actionDef.getDescription(),
					actionDef.getDeploymentRule(),
					actionDef.getParameterDefinitions(),
					toStringRepresentation(actionDef.getUsageTypes()));

			// 4. create audit
			AuditLogger.getInstance().logNew(actionDef, user.getUserID());
			return actionID;
		}
		catch (SQLException ex) {
			// this is required as we attempt to insert the action into the cache
			// before attempt to persist it in DB
			if (GuidelineFunctionManager.getInstance().getActionTypeDefinition(actionDef.getID()) != null) {
				GuidelineFunctionManager.getInstance().removeActionTypeDefinition(actionDef.getID());
			}
			throw new ServletActionException("ServerError", "Failed to store the action into the DB: " + ex.getMessage());
		}
		catch (SapphireException ex) {
			throw new ServletActionException("ServerError", "Failed to the next action ID from database: " + ex.getMessage());
		}
	}

	private int create(TestTypeDefinition testDef, User user) throws ServletActionException, ParseException {
		try {
			// 1. Get a new test ID
			int testID = DBIdGenerator.getInstance().nextSequentialID();
			testDef.setID(testID);

			logger.debug("    creating new test: " + testID);

			// 2. insert test into server cache
			GuidelineFunctionManager.getInstance().insertTestTypeDefinition(testDef);

			// 3. insert test row into DB
			GuidelineTestConditionUpdater updater = new GuidelineTestConditionUpdater();
			updater.insertTest(
					testID,
					testDef.getName(),
					testDef.getDescription(),
					testDef.getDeploymentRule(),
					testDef.getParameterDefinitions());

			// 4. create audit
			AuditLogger.getInstance().logNew(testDef, user.getUserID());
			return testID;
		}
		catch (SQLException ex) {
			// this is required as we attempt to insert the test into the cache
			// before attempt to persist it in DB
			if (GuidelineFunctionManager.getInstance().getTestTypeDefinition(testDef.getID()) != null) {
				GuidelineFunctionManager.getInstance().removeTestTypeDefinition(testDef.getID());
			}
			throw new ServletActionException("ServerError", "Failed to store the test into the DB: " + ex.getMessage());
		}
		catch (SapphireException ex) {
			throw new ServletActionException("ServerError", "Failed to the next action ID from database: " + ex.getMessage());
		}
	}

	private void update(ActionTypeDefinition actionDef, User user) throws ServletActionException, ParseException {
		logger.debug(">>> update(ActionTypeDefinition) with " + actionDef);

		int actionID = actionDef.getID();
		ActionTypeDefinition actionFromCache = GuidelineFunctionManager.getInstance().getActionTypeDefinition(actionID);
		boolean isCacheUpdated = false;

		// check that we didn't delete the UsageType for any template that
		// currently uses this
		// Action
		if (actionFromCache != null && actionDef != null) {
			TemplateUsageType[] oldUsageTypes = actionFromCache.getUsageTypes();
			TemplateUsageType[] newUsageTypes = actionDef.getUsageTypes();
			List<TemplateUsageType> deletedUsageTypes = new ArrayList<TemplateUsageType>();
			for (int i = 0; i < oldUsageTypes.length; i++) {
				boolean found = false;
				for (int j = 0; j < newUsageTypes.length; j++) {
					if (oldUsageTypes[i].equals(newUsageTypes[j])) {
						found = true;
						break;
					}
				}
				if (!found) deletedUsageTypes.add(oldUsageTypes[i]);
			}
			Iterator<TemplateUsageType> it = deletedUsageTypes.iterator();
			while (it.hasNext()) {
				TemplateUsageType ut = it.next();
				List<GridTemplate> templates = GuidelineTemplateManager.getInstance().getTemplates(ut);
				Iterator<GridTemplate> it2 = templates.iterator();
				while (it2.hasNext()) {
					GridTemplate template = it2.next();
					RuleDefinition rule = template.getRuleDefinition();
					if (rule != null && rule.getActionTypeID() == actionFromCache.getID())
						throw new ServletActionException("ServerError", "Template \"" + template.getName() + "\" has usage type \""
								+ ut.getDisplayName() + "\" which cannot be removed from this action.");
				}
			}
		}

		try {
			List<GridTemplate> lockedTemplates = new ArrayList<GridTemplate>();
			List<GridTemplate> previouslyLockedTemplates = new ArrayList<GridTemplate>();

			Connection conn = null;
			// find all templates which use this action and lock them
			List<GridTemplate> allTemplates = GuidelineTemplateManager.getInstance().getAllTemplates();
			Iterator<GridTemplate> it = allTemplates.iterator();
			while (it.hasNext()) {
				GridTemplate template = it.next();
				List<RuleDefinition> ruleDefs = new ArrayList<RuleDefinition>();
				RuleDefinition rule = template.getRuleDefinition();
				if (rule != null) ruleDefs.add(rule);
				for (Iterator<GridTemplateColumn> colIt = template.getColumns().iterator(); colIt.hasNext();) {
					rule = colIt.next().getRuleDefinition();
					if (rule != null) ruleDefs.add(rule);
				}
				for (Iterator<RuleDefinition> ruleIt = ruleDefs.iterator(); ruleIt.hasNext();) {
					rule = ruleIt.next();
					if (rule.getActionTypeID() == actionFromCache.getID()) {
						try {
							if (LockManager.getInstance().getExistingLock(EntityType.TEMPLATE, template.getId(), user) != null) {
								previouslyLockedTemplates.add(template);
								lockedTemplates.add(template);
							}
							else {
								lockEntity(EntityType.TEMPLATE, template.getId(), user);
								lockedTemplates.add(template);
							}
						}
						catch (LockException ex) {
							throw new ServletActionException("LockFailureMsg", "The template \"" + template.getName()
									+ "\" uses this action and is locked by " + ex.getLockedBy());
						}
						break;
					}
				}
			}

			try {
				conn = DBConnectionManager.getInstance().getConnection();
				conn.setAutoCommit(false);
				GuidelineTemplateUpdater templateUpdater = new GuidelineTemplateUpdater();

				// update the templates that use this action
				Iterator<GridTemplate> it2 = lockedTemplates.iterator();
				while (it2.hasNext()) {
					GridTemplate template = it2.next();
					TemplateUtil.updateForModifiedAction(template, actionFromCache, actionDef);
					templateUpdater.updateTemplate(conn, template);
				}

				// update cache
				GuidelineFunctionManager.getInstance().updateActionTypeDefinition(actionDef);
				isCacheUpdated = true;

				// update phase DB row
				GuidelineActionUpdater updater = new GuidelineActionUpdater();
				updater.updateAction(
						conn,
						actionID,
						actionDef.getName(),
						actionDef.getDescription(),
						actionDef.getDeploymentRule(),
						actionDef.getParameterDefinitions(),
						toStringRepresentation(actionDef.getUsageTypes()));

				logger.debug("    updated action. updating cache...");

				// commit the changes to the templates and the action
				conn.commit();

				// create audit
				AuditLogger.getInstance().logUpdate(actionDef, actionFromCache, user.getUserID());

				logger.debug("    update completed.");
			}
			catch (Exception ex) {
				logger.debug("Error: rolling back changes and restoring cache from DB:" + ex.getMessage());
				ex.printStackTrace();
				try {
					conn.rollback();
				}
				catch (RuntimeException e) {
					e.printStackTrace();
				}
				try {
					// reload all the templates from the database since this
					// there is no other convenient way to roll back
					GuidelineTemplateLoader.getInstance().load(ConfigurationManager.getInstance().getKnowledgeBaseFilterConfig());
				}
				catch (Exception x) {
					throw new ServletActionException("ServerError", "Failed to update the action in the DB: " + ex.getMessage());
				}
				// this is required as we attempt to insert the action into the cache
				// before attempt to persist it in DB
				if (isCacheUpdated && actionFromCache != null) {
					GuidelineFunctionManager.getInstance().updateActionTypeDefinition(actionFromCache);
				}
				throw new ServletActionException("ServerError", "Failed to update the action in the DB: " + ex.getMessage());
			}
			finally {
				DBConnectionManager.getInstance().freeConnection(conn);
				// unlock all the templates we changed
				Iterator<GridTemplate> i = lockedTemplates.iterator();
				while (i.hasNext()) {
					GridTemplate template = i.next();
					if (!previouslyLockedTemplates.contains(template)) unlockEntity(EntityType.TEMPLATE, template.getId(), user);
				}
			}
		}
		catch (SQLException ex) {
			throw new ServletActionException("ServerError", "Failed to update the action in the DB: " + ex.getMessage());

		}
	}

	private void update(TestTypeDefinition testDef, User user) throws ServletActionException, ParseException {
		logger.debug(">>> update(TestTypeDefinition) with " + testDef);

		int testID = testDef.getID();
		TestTypeDefinition testFromCache = GuidelineFunctionManager.getInstance().getTestTypeDefinition(testID);
		boolean isCacheUpdated = false;

		List<GridTemplate> lockedTemplates = new ArrayList<GridTemplate>();
		List<GridTemplate> previouslyLockedTemplates = new ArrayList<GridTemplate>();
		Connection conn = null;

		try {
			try {

				List<GridTemplate> allTemplates = GuidelineTemplateManager.getInstance().getAllTemplates();
				Iterator<GridTemplate> it = allTemplates.iterator();
				while (it.hasNext()) {
					GridTemplate template = it.next();
					List<RuleDefinition> ruleDefs = new ArrayList<RuleDefinition>();
					RuleDefinition rule = template.getRuleDefinition();
					if (rule != null) ruleDefs.add(rule);
					for (Iterator<GridTemplateColumn> colIt = template.getColumns().iterator(); colIt.hasNext();) {
						rule = colIt.next().getRuleDefinition();
						if (rule != null) ruleDefs.add(rule);
					}
					for (Iterator<RuleDefinition> ruleIt = ruleDefs.iterator(); ruleIt.hasNext();) {
						rule = ruleIt.next();
						for (Iterator<TestCondition> testIt = rule.getTestConditions().iterator(); testIt.hasNext();) {
							TestCondition tc = testIt.next();
							if (tc.getTestType().getID() == testFromCache.getID()) {
								try {
									if (LockManager.getInstance().getExistingLock(EntityType.TEMPLATE, template.getId(), user) != null) {
										previouslyLockedTemplates.add(template);
										lockedTemplates.add(template);
									}
									else {
										lockEntity(EntityType.TEMPLATE, template.getId(), user);
										lockedTemplates.add(template);
									}
								}
								catch (LockException ex) {
									throw new ServletActionException("LockFailureMsg", ex.getLockedBy());
								}
								break;
							}
						}
					}
				}

				conn = DBConnectionManager.getInstance().getConnection();
				conn.setAutoCommit(false);

				GuidelineTemplateUpdater templateUpdater = new GuidelineTemplateUpdater();

				// update the templates that use this test
				Iterator<GridTemplate> it2 = lockedTemplates.iterator();
				while (it2.hasNext()) {
					GridTemplate template = it2.next();
					TemplateUtil.updateForModifiedTest(template, testFromCache, testDef);
					templateUpdater.updateTemplate(conn, template);
				}

				// update cache
				GuidelineFunctionManager.getInstance().updateTestTypeDefinition(testDef);
				isCacheUpdated = true;

				// update phase DB row
				GuidelineTestConditionUpdater updater = new GuidelineTestConditionUpdater();
				updater.updateTest(
						conn,
						testID,
						testDef.getName(),
						testDef.getDescription(),
						testDef.getDeploymentRule(),
						testDef.getParameterDefinitions());

				logger.debug("    updated test. updating cache...");

				// commit the changes to the templates and the test
				conn.commit();

				// create audit
				AuditLogger.getInstance().logUpdate(testDef, testFromCache, user.getUserID());

				logger.debug("    update completed.");
			}
			catch (Exception ex) {
				logger.debug("Error: rolling back changes and restoring cache from DB:" + ex.getMessage());
				ex.printStackTrace();
				conn.rollback();
				try {
					// reload all the templates from the database since this
					// there is no other convenient way to roll back
					GuidelineTemplateLoader.getInstance().load(ConfigurationManager.getInstance().getKnowledgeBaseFilterConfig());
				}
				catch (Exception x) {
					throw new ServletActionException("ServerError", "Failed to update the test in the DB: " + ex.getMessage());
				}
				// this is required as we attempt to insert the test into the cache
				// before attempt to persist it in DB
				if (isCacheUpdated && testFromCache != null) {
					GuidelineFunctionManager.getInstance().updateTestTypeDefinition(testFromCache);
				}
				throw new ServletActionException("ServerError", "Failed to update the test in the DB: " + ex.getMessage());
			}
			finally {
				DBConnectionManager.getInstance().freeConnection(conn);
				// unlock all the templates we changed
				Iterator<GridTemplate> it = lockedTemplates.iterator();
				while (it.hasNext()) {
					GridTemplate template = it.next();
					if (!previouslyLockedTemplates.contains(template)) unlockEntity(EntityType.TEMPLATE, template.getId(), user);
				}
			}
		}
		catch (SQLException ex) {
			throw new ServletActionException("ServerError", "Failed to update the test in the DB: " + ex.getMessage());

		}
	}

	public int save(Phase phase, User user) throws ServletActionException {
		logger.debug(">>> save(Phase) with " + phase);

		int phaseID = phase.getID();
		if (phaseID != -1) {
			update(phase, user);
			return phaseID;
		}
		else {
			return create(phase, user);
		}
	}

	private int create(Phase phase, User user) throws ServletActionException {
		try {
			// 1. Get a new phase ID
			int phaseID = DBIdGenerator.getInstance().nextSequentialID();
			phase.setID(phaseID);

			logger.debug("    creating new phase: " + phaseID);

			// 2. insert phase row into DB

			ProcessUpdater updater = new ProcessUpdater();
			if (phase instanceof PhaseReference) {
				updater.insertPhase(
						phaseID,
						phase.getPhaseType(),
						phase.getName(),
						phase.getDisplayName(),
						(phase.getPhaseTask() == null ? null : phase.getPhaseTask().getStorageName()),
						extractIDs(phase.getSubPhases()),
						extractIDs(phase.getPrerequisites()),
						((PhaseReference) phase).getReferecePhase().getID());
			}
			else {
				updater.insertPhase(
						phaseID,
						phase.getPhaseType(),
						phase.getName(),
						phase.getDisplayName(),
						(phase.getPhaseTask() == null ? null : phase.getPhaseTask().getStorageName()),
						extractIDs(phase.getSubPhases()),
						extractIDs(phase.getPrerequisites()),
						phase.isDisjunctivePrereqs());
			}

			// 3. insert phase into server cache
			ProcessManager.getInstance().addPhase(phase);

			AuditLogger.getInstance().logNew(phase, user.getUserID());
			return phaseID;
		}
		catch (SQLException ex) {
			throw new ServletActionException("ServerError", "Failed to store the phase into the DB: " + ex.getMessage());
		}
		catch (SapphireException ex) {
			throw new ServletActionException("ServerError", "Failed to the next phase ID from database: " + ex.getMessage());
		}
	}

	private void update(Phase phase, User user) throws ServletActionException {
		logger.debug(">>> update(Phase) with " + phase);

		int phaseID = phase.getID();
		try {
			Phase phaseFromCache = ProcessManager.getInstance().getPhase(phaseID);

			// 1. update phase DB row
			ProcessUpdater updater = new ProcessUpdater();
			if (phase instanceof PhaseReference) {
				updater.updatePhase(
						phaseID,
						phase.getPhaseType(),
						phase.getName(),
						phase.getDisplayName(),
						(phase.getPhaseTask() == null ? null : phase.getPhaseTask().getStorageName()),
						extractIDs(phase.getSubPhases()),
						extractIDs(phase.getPrerequisites()),
						((PhaseReference) phase).getReferecePhase().getID());

			}
			else {
				updater.updatePhase(
						phaseID,
						phase.getPhaseType(),
						phase.getName(),
						phase.getDisplayName(),
						(phase.getPhaseTask() == null ? null : phase.getPhaseTask().getStorageName()),
						extractIDs(phase.getSubPhases()),
						extractIDs(phase.getPrerequisites()),
						phase.isDisjunctivePrereqs());
			}

			logger.debug("    updated phase. updating cache...");

			AuditLogger.getInstance().logUpdate(phase, phaseFromCache, user.getUserID());

			// 2. update cache
			ProcessManager.getInstance().updatePhase(phase);

			logger.debug("    update completed.");
		}
		catch (SQLException ex) {
			throw new ServletActionException("ServerError", "Failed to update the phase into the DB: " + ex.getMessage());
		}
	}

	public int clone(GenericEntity entity, boolean copyPolicies, User user, boolean releaseLocks) throws ServletActionException {
		assert (entity.getID() == -1);

		logger.debug(">>> clone with " + entity + ",copyPolicies?=" + copyPolicies);

		// check existence of original product
		GenericEntity origEntity = EntityManager.getInstance().getEntity(entity.getType(), entity.getParentID());
		if (origEntity == null) {
			throw new ServletActionException("ServerError", "Could not locate original entity of type " + entity.getType() + " with ID "
					+ entity.getParentID());
		}

		int categoryType = getCategoryTypeID(entity.getType());

		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection conn = null;
		try {
			int newEntityID = DBIdGenerator.getInstance().nextSequentialID();
			entity.setID(newEntityID);
			logger.debug("    newEntityID = " + newEntityID);

			conn = dbconnectionmanager.getConnection();
			conn.setAutoCommit(false);

			MutableTimedAssociationKey[] categoryAssociations = extractCategoryAssociations(entity);
			ServiceProviderFactory.getPEDataUpdater().getGenericEntityDataUpdater().insertGenericEntity(
					newEntityID,
					entity.getType().getID(),
					entity.getName(),
					entity.getParentID(),
					entity.getPropertyMap(),
					categoryType,
					categoryAssociations);

			List<ProductGrid> guideillneGridList = null;
			List<ParameterGrid> parameterGridList = null;
			if (copyPolicies) {
				GridUpdater gridUpdater = new GridUpdater(conn);
				guideillneGridList = GridActionCoordinator.getInstance().cloneGuidelines(
						entity.getType(),
						entity.getParentID(),
						entity.getID(),
						gridUpdater);
				ParameterUpdater paramUpdater = new ParameterUpdater(conn);
				parameterGridList = GridActionCoordinator.getInstance().cloneParameters(
						entity.getType(),
						entity.getParentID(),
						entity.getID(),
						paramUpdater);
			}
			conn.commit();

			EntityManager.getInstance().addGenericEntity(
					newEntityID,
					entity.getType().getID(),
					entity.getName(),
					entity.getParentID(),
					entity.getPropertyMap());
			if (categoryType != -1) {
				EntityManager.getInstance().addCategoryAssociations(
						newEntityID,
						entity.getType().getID(),
						categoryType,
						categoryAssociations);
			}

			if (copyPolicies) {
				ProductGrid productgrid;
				for (Iterator<ProductGrid> iterator = guideillneGridList.iterator(); iterator.hasNext();) {
					productgrid = iterator.next();
					GridManager.getInstance().updateGridContext(productgrid.getID(), productgrid.extractGuidelineContext());
				}
				ParameterGrid paramGrid;
				for (Iterator<ParameterGrid> iterator = parameterGridList.iterator(); iterator.hasNext();) {
					paramGrid = iterator.next();
					ParameterManager.getInstance().updateGridContext(paramGrid.getID(), paramGrid.extractGuidelineContext());
				}
			}

			AuditLogger.getInstance().logCloneEntity(entity, origEntity, copyPolicies, user.getUserID());

			if (!releaseLocks) LockManager.getInstance().lock(entity.getType(), entity.getID(), user);

			return newEntityID;
		}
		catch (SapphireException ex) {
			rollBackLocallyManagedConnection(conn);
			logger.error("Failed to generate next entity ID", ex);
			throw new ServletActionException("ServerError", "Could not obtain new Product ID: " + ex.getMessage());
		}
		catch (Exception ex) {
			rollBackLocallyManagedConnection(conn);
			logger.error("Failed to clone " + entity, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
		finally {
			dbconnectionmanager.freeConnection(conn);
		}
	}

	/**
	 * 
	 * @param entity
	 * @param user
	 * @return the entity id
	 * @throws ServletActionException
	 * @since PowerEditor 3.0.0
	 */
	public int save(GenericEntity entity, boolean validate, User user) throws ServletActionException, DataValidationFailedException {
		if (logger.isDebugEnabled()) logger.debug(">>> save(GenericEntity) with " + entity + "," + validate);
		if (validate) validateData(entity);

		int entityID = entity.getID();
		int categoryType = getCategoryTypeID(entity.getType());
		try {
			MutableTimedAssociationKey[] categoryAssociations = extractCategoryAssociations(entity);
			if (entityID != -1) {
				ServiceProviderFactory.getPEDataUpdater().getGenericEntityDataUpdater().updateGenericEntity(
						entityID,
						entity.getType().getID(),
						entity.getName(),
						entity.getParentID(),
						entity.getPropertyMap(),
						categoryType,
						categoryAssociations);

				GenericEntity cachedEntity = EntityManager.getInstance().getEntity(entity.getType(), entityID);
				AuditLogger.getInstance().logUpdate(entity, cachedEntity, user.getUserID());
				EntityManager.getInstance().updateCache(entity);
				return entityID;
			}
			else {
				entityID = DBIdGenerator.getInstance().nextSequentialID();
				entity.setID(entityID);

				ServiceProviderFactory.getPEDataUpdater().getGenericEntityDataUpdater().insertGenericEntity(
						entityID,
						entity.getType().getID(),
						entity.getName(),
						entity.getParentID(),
						entity.getPropertyMap(),
						categoryType,
						categoryAssociations);

				EntityManager.getInstance().addGenericEntity(
						entityID,
						entity.getType().getID(),
						entity.getName(),
						entity.getParentID(),
						entity.getPropertyMap());
				if (categoryType != -1) {
					EntityManager.getInstance().addCategoryAssociations(
							entityID,
							entity.getType().getID(),
							entity.getType().getCategoryType(),
							categoryAssociations);
				}

				AuditLogger.getInstance().logNew(entity, user.getUserID());
				return entityID;
			}
		}
		catch (SQLException ex) {
			throw new ServletActionException("ServerError", "Failed to store the generic entity into the DB: " + ex.getMessage());
		}
		catch (SapphireException ex) {
			throw new ServletActionException("ServerError", "Failed to the next entity ID from database: " + ex.getMessage());
		}
	}

	/**
	 * 
	 * @param entity
	 * @param user
	 * @throws ImportException
	 * @since PowerEditor 3.0.0
	 */
	public void importEntity(GenericEntity entity, boolean merge, User user) throws ImportException, DataValidationFailedException {
		logger.debug(">>> importEntity(GenericEntity): " + entity + ", merge?=" + merge);
		validateData(entity);

		int entityID = entity.getID();
		int categoryType = getCategoryTypeID(entity.getType());
		try {
			if (!merge && EntityManager.getInstance().hasGenericEntity(entity.getType(), entityID)) {
				logger.info("    importEntity: entity with the same id already exists. Updating existing entity...");
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
						extractCategoryAssociations(entity));

				EntityManager.getInstance().updateCache(entity);

				AuditLogger.getInstance().logUpdate(entity, entityFromCache, user.getUserID());
			}
			else {
				logger.info("    importEntity: inserting as new with id " + entityID);
				// 1. insert generic entity row into DB
				ServiceProviderFactory.getPEDataUpdater().getGenericEntityDataUpdater().insertGenericEntity(
						entityID,
						entity.getType().getID(),
						entity.getName(),
						entity.getParentID(),
						entity.getPropertyMap(),
						categoryType,
						extractCategoryAssociations(entity));

				EntityManager.getInstance().addGenericEntity(
						entityID,
						entity.getType().getID(),
						entity.getName(),
						entity.getParentID(),
						entity.getPropertyMap());

				AuditLogger.getInstance().logImport(entity, user.getUserID());
			}
			logger.debug("<<< importEntity(GenericEntity)");
		}
		catch (SQLException ex) {
			logger.error("Failed to import generic entiy " + entity, ex);
			throw new ImportException("failed to updated DB: " + ex.getLocalizedMessage());
		}
		catch (LockException ex) {
			throw new ImportException("Failed to lock the entity - locked by " + ex.getLockedBy());
		}
	}

	/**
	 * 
	 * @param template
	 * @param version
	 * @param parentTemplateID
	 * @param user
	 * @return the number of new guideline action types created
	 * @throws ParseException
	 * @throws SapphireException
	 */
	public int importTemplatePre42(boolean merge, Map<Integer, Integer> idMap, GridTemplate template, String version, int parentTemplateID,
			User user) throws ParseException, SapphireException {
		logger.debug(">>> importTemplatePre42: merge?=" + merge + ",template=" + template + ",v=" + version + ",parent=" + parentTemplateID);

		if (!merge && GuidelineTemplateManager.getInstance().getTemplate(template.getID()) != null) {
			throw new ServletActionException("msg.error.failure.import.template", "Template already exists");
		}
		if (parentTemplateID > 0 && GuidelineTemplateManager.getInstance().getTemplate(parentTemplateID) == null) {
			throw new ServletActionException("msg.error.failure.import.template", "Parent template does not exists");
		}

		int newActionCount = 0;
		try {
			int templateID = template.getID();
			if (merge) {
				templateID = DBIdGenerator.getInstance().nextGridID();
				idMap.put(new Integer(template.getID()), new Integer(templateID));
				template.setID(templateID);
			}

			// 1. prepare the template for import
			logger.debug("    processing template for import: " + templateID);
			newActionCount = prepareTemplateForImportPre42(template, version, parentTemplateID, user);

			logger.debug("    creating new template: " + templateID);

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

				logger.debug("<<< importTemplatePre42: " + newActionCount);
				return newActionCount;
			}
			catch (Exception ex) {
				logger.error("Error while creating template " + templateID, ex);
				if (conn != null) conn.rollback();
				throw new ServletActionException("ServerError", "Failed to store the template into the DB: " + ex.getMessage());
			}
			finally {
				DBConnectionManager.getInstance().freeConnection(conn);
			}
		}
		catch (SQLException ex) {
			logger.error("Failed to persist imported grid template " + template.getID(), ex);
			throw new ServletActionException("ServerError", "Failed to store the template into the DB: " + ex.getMessage());
		}
		catch (SapphireException ex) {
			logger.error("Failed to import template " + template.getID(), ex);
			throw ex;
		}
	}

	/**
	 * This updates it if the specified template already exists.
	 * 
	 * @param template
	 * @param user
	 * @throws ServletActionException
	 */
	public void importTemplate(GridTemplate template, boolean merge, Map<Integer, Integer> idMap, User user) throws ServletActionException {
		// 1. prepare the template for import
		logger.debug("    processing template for import: " + template.getID() + ", isMerge=?" + merge);
		if (merge) {
			int templateID;
			try {
				templateID = DBIdGenerator.getInstance().nextGridID();
				idMap.put(new Integer(template.getID()), new Integer(templateID));
				template.setID(templateID);
				logger.debug("    importTemplate: template id set to " + templateID);
			}
			catch (SapphireException e) {
				throw new ServletActionException("ServerError", e.toString());
			}
		}

		prepareTemplateForImport(template);

		if (GuidelineTemplateManager.getInstance().getTemplate(template.getID()) != null) {
			update(template, user);
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
					logger.error("Error while creating template " + template, ex);
					if (conn != null) conn.rollback();
					throw new ServletActionException("ServerError", "Failed to store the template into the DB: " + ex.getMessage());
				}
				finally {
					DBConnectionManager.getInstance().freeConnection(conn);
				}
			}
			catch (SQLException ex) {
				logger.error("Failed to persist imported template " + template.getID(), ex);
				throw new ServletActionException("ServerError", "Failed to store the template into the DB: " + ex.getMessage());
			}
		}
	}

	private RuleDefinition convertToRuleDefinition(GridTemplate template, int colNo, String ruleName, String ruleString,
			MutableBoolean wasActionCreated, User user) throws ParseException, SapphireException {
		RuleParser.getInstance(new StringReader(ruleString));
		DeploymentRule deploymentRule = RuleParser.parseDeploymentRule();

		RuleDefinition ruleDef = RuleGeneratorHelper.toRuleDefinition(template, colNo, ruleName, deploymentRule, wasActionCreated, user);

		return ruleDef;
	}

	private void prepareTemplateForImport(GridTemplate template) {
		if (logger.isDebugEnabled()) logger.debug(">>> prepareTemplateForImport: " + template);
		if (Util.isEmpty(template.getStatus())) {
			template.setStatus(GridTemplate.DEFAULT_STATUS_DRAFT);
		}
		logger.debug("<<< prepareTemplateForImport");
	}

	/**
	 * 
	 * @param template
	 * @param version
	 * @param parentTemplateID
	 * @param user
	 * @return the number of new action type definitions created; 0, if none
	 * @throws ParseException
	 * @throws SapphireException
	 */
	private int prepareTemplateForImportPre42(GridTemplate template, String version, int parentTemplateID, User user)
			throws ParseException, SapphireException {
		logger.debug(">>> prepareTemplateForImportPre42: " + template);
		template.setVersion(version);
		template.setParentTemplateID(parentTemplateID);
		template.setFitToScreen(ConfigurationManager.getInstance().getUIConfiguration().fitGridToScreen());
		if (Util.isEmpty(template.getStatus())) {
			template.setStatus(GridTemplate.DEFAULT_STATUS_DRAFT);
		}

		int newActionCount = 0;
		// process template rule, if found
		MutableBoolean wasActionCreated = new MutableBoolean(false);
		String ruleString = template.getRuleDefinitionString();
		if (ruleString != null && ruleString.length() > 0) {
			logger.info("Parsing template rule: " + ruleString);
			template.setRuleDefinition(convertToRuleDefinition(template, -1, template.getName(), ruleString, wasActionCreated, user));
			if (wasActionCreated.booleanValue()) ++newActionCount;
			logger.debug("template rule set");
		}

		// process column
		for (Iterator<GridTemplateColumn> iterator = template.getColumns().iterator(); iterator.hasNext();) {
			GridTemplateColumn column = iterator.next();
			logger.debug("  processing " + column.getTitle());
			column.setName("column" + column.getColumnNumber());

			// process template rule, if found
			ruleString = column.getRuleDefinitionString();
			if (ruleString != null && ruleString.length() > 0) {
				logger.info("Parsing column rule: " + ruleString);
				column.setRuleDefinition(convertToRuleDefinition(
						template,
						column.getColumnNumber(),
						template.getName() + "-" + column.getName(),
						ruleString,
						wasActionCreated,
						user));
				if (wasActionCreated.booleanValue()) ++newActionCount;
				logger.debug("column rule set");
			}

			// use attribute's title when column is mapped to one and title has not set
			if (column.getMappedAttribute() != null && column.getTitle() == null) {
				String className = column.getMAClassName();
				String attrName = column.getMAAttributeName();
				DomainAttribute domainattribute = DomainManager.getInstance().getDomainAttribute(className, attrName);
				if (domainattribute != null) {
					column.setTitle(domainattribute.getDisplayLabel());
				}
				else {
					column.setTitle(column.getMappedAttribute());
				}
			}
		}
		logger.debug("<<< prepareTemplateForImportPre42: " + newActionCount);
		return newActionCount;
	}

	public boolean hasGuidelines(int templateID) throws ServletActionException {
		if (templateID <= 0) return false;
		if (GuidelineTemplateManager.getInstance().getTemplate(templateID) == null) return false;
		return GridManager.getInstance().hasGrids(templateID);
	}

	public int save(GridTemplate template, User user) throws ServletActionException {
		logger.debug(">>> save(GridTemplate) with " + template);

		int templateID = template.getID();
		if (templateID != -1) {
			update(template, user);
			return templateID;
		}
		else {
			return create(template, user);
		}
	}

	private int create(GridTemplate template, User user) throws ServletActionException {
		try {
			// 1. Get a new action ID
			int templateID = DBIdGenerator.getInstance().nextGridID();
			template.setID(templateID);

			logger.debug("    creating new template: " + templateID);

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

				AuditLogger.getInstance().logNew(template, user.getUserID());
				return templateID;
			}
			catch (Exception ex) {
				logger.error("Error while creating template " + templateID, ex);
				if (conn != null) conn.rollback();
				throw new ServletActionException("ServerError", "Failed to store the template into the DB: " + ex.getMessage());
			}
			finally {
				DBConnectionManager.getInstance().freeConnection(conn);
			}
		}
		catch (SQLException ex) {
			throw new ServletActionException("ServerError", "Failed to store the template into the DB: " + ex.getMessage());
		}
		catch (ServletActionException ex) {
			throw ex;
		}
		catch (SapphireException ex) {
			throw new ServletActionException("ServerError", "Failed persist the template into database: " + ex.getMessage());
		}
	}

	private void update(GridTemplate template, User user) throws ServletActionException {
		logger.debug(">>> update(GridTemplate) with " + template);

		int templateID = template.getID();
		try {
			GridTemplate templateFromCache = GuidelineTemplateManager.getInstance().getTemplate(templateID);

			// 1. update phase DB row
			GuidelineTemplateUpdater updater = new GuidelineTemplateUpdater();
			Connection conn = null;
			try {
				conn = DBConnectionManager.getInstance().getConnection();
				conn.setAutoCommit(false);
				updater.updateTemplate(conn, template);
				conn.commit();

				AuditLogger.getInstance().logUpdate(template, templateFromCache, user.getUserID());

				// 2. update cache
				GuidelineTemplateManager.getInstance().updateTemplate(template);

				logger.debug("    update completed.");
			}
			catch (Exception ex) {
				if (conn != null) conn.rollback();
				logger.error("Error while updating template " + template, ex);
				throw new ServletActionException("ServerError", "Failed to update the template into the DB: " + ex.getMessage());
			}
			finally {
				DBConnectionManager.getInstance().freeConnection(conn);
			}

			// 2. Generate missing rule ids, if applicable
			if (template.hasRuleIDColumn()) {
				GridActionCoordinator.getInstance().generateMissingRuleIDsForTemplate(templateID);
			}
		}
		catch (SQLException ex) {
			throw new ServletActionException("ServerError", "Failed to update the GridTemplate in the DB: " + ex.getMessage());
		}
		catch (ServerException e) {
			throw new ServletActionException("ServerError", e.getMessage());
		}
		catch (SapphireException e) {
			throw new ServletActionException("ServerError", e.getMessage());
		}
	}

	private GridTemplate deleteGuidelineTemplate(int templateID) throws ServletActionException {
		logger.debug(">>> deleteGuidelineTemplate with " + templateID);
		try {
			if (GuidelineTemplateManager.getInstance().getTemplate(templateID) == null) {
				throw new SQLException("template" + templateID + " not found");
			}

			GuidelineTemplateUpdater updater = new GuidelineTemplateUpdater();
			Connection conn = null;
			try {
				conn = DBConnectionManager.getInstance().getConnection();
				conn.setAutoCommit(false);
				updater.deleteTemplate(conn, templateID);
				conn.commit();

				GridTemplate templateFromCache = GuidelineTemplateManager.getInstance().getTemplate(templateID);
				logger.debug("deleteGuidelineTemplate: removing from cache");

				GuidelineTemplateManager.getInstance().removeFromCache(templateID);

				logger.debug("<<< deleteGuidelineTemplate");
				return templateFromCache;
			}
			catch (Exception ex) {
				if (conn != null) conn.rollback();
				throw new ServletActionException("ServerError", "Failed to delete the template from the DB: " + ex.getMessage());
			}
			finally {
				DBConnectionManager.getInstance().freeConnection(conn);
			}
		}
		catch (SQLException ex) {
			logger.error("Failed to delete template " + templateID, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	public void importAdhocRule(RuleDefinition ruleDef, RuleSet ruleSet, User user) throws ServletActionException, ImportException,
			DataValidationFailedException {
		logger.debug(">>> importAdHocRule: " + ruleDef + "," + ruleSet + " for " + user);

		// 1. Make template and save
		List<String> valueStrList = new LinkedList<String>();

		GridTemplate template = produceTemplate(ruleDef, ruleSet, valueStrList);

		if (logger.isDebugEnabled()) {
			logger.debug("... importAdHocRule: Action after template creation: " + ruleDef.getRuleAction());
			for (int i = 0; i < ruleDef.getRuleAction().size(); i++) {
				logger.debug("... importAdHocRule: action param " + i + " = " + ruleDef.getFunctionParameterAt(i));
			}
		}

		logger.debug("    importAdHocRule: saving template " + template);

		int templateID = save(template, user);
		template.setID(templateID);

		logger.debug("    importAdHocRule: template " + templateID + " saved. saving grid...");

		// 2. Make grid and save
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		gridList.add(GridActionCoordinator.getInstance().produceGrid(
				template,
				valueStrList,
				ruleSet,
				extractContextForAdHocRule(ruleSet),
				user));
		GridActionCoordinator.getInstance().importGridData(template.getID(), gridList, user);

		logger.debug("<<< importAdHocRule");
	}

	private GuidelineContext[] extractContextForAdHocRule(RuleSet ruleSet) {
		List<GuidelineContext> list = new ArrayList<GuidelineContext>();
		GuidelineContext context = null;
		if (ruleSet.hasGenericCategoryContext()) {
			GenericEntityType type = ruleSet.getFirstEntityTypeForGenericCategory();
			int geCategoryID = ruleSet.getGenericCategoryID(type);
			int categoryTypeID = getCategoryTypeID(type);
			if (categoryTypeID == -1) {
				logger.warn("Generate category context " + type + " ignored for " + ruleSet);
			}
			else {
				context = new GuidelineContext(categoryTypeID);
				context.setIDs(new int[] { geCategoryID });
				list.add(context);
			}
		}
		if (ruleSet.hasGenericEntityContext()) {
			GenericEntityType[] genericEntityTypes = ruleSet.getGenericEntityTypesInUse();
			for (int i = 0; i < genericEntityTypes.length; i++) {
				context = new GuidelineContext(genericEntityTypes[i]);
				context.setIDs(new int[] { ruleSet.getGenericEntityID(genericEntityTypes[i]) });
				list.add(context);
			}
		}
		return list.toArray(new GuidelineContext[0]);
	}

	private GridTemplate produceTemplate(RuleDefinition ruleDef, RuleSet ruleSet, List<String> valueStrList) {
		logger.debug(">>> produceTemplate: " + ruleDef + "," + ruleSet + "," + valueStrList);

		GridTemplate template = new GridTemplate(-1, ruleDef.getName(), ruleSet.getUsageType());
		template.setFitToScreen(ConfigurationManager.getInstance().getUIConfiguration().fitGridToScreen());
		template.setMaxNumOfRows(1);
		template.setDescription("Created from RuleSet '" + ruleSet.getName() + "' by PowerEditor");
		template.setVersion(GridTemplate.DEFAULT_VERSION);

		// set messages
		Map<String, String> messageMap = ruleDef.getMessageMap();
		for (Map.Entry<String, String> entry : messageMap.entrySet()) {
			TemplateMessageDigest messageDigest = new TemplateMessageDigest();
			messageDigest.setEntityIDStr(entry.getKey());
			messageDigest.setText(entry.getValue());

			template.addMessageDigest(messageDigest);
		}
		logger.debug("    produceTemplate: " + messageMap.size() + " messages set. Generating columns...");

		// Replace values with columns
		RuleDefinitionUtil.produceTemplateColumns(template, ruleDef, valueStrList);

		logger.debug("... produceTemplate: Action after processing: " + ruleDef.getRuleAction());
		for (int i = 0; i < ruleDef.getRuleAction().size(); i++) {
			logger.debug("... produceTemplate: action param on action" + i + " = " + ruleDef.getRuleAction().get(i));
			logger.debug("... produceTemplate: action param on r-def " + i + " = " + ruleDef.getFunctionParameterAt(i));
		}

		template.setRuleDefinition(ruleDef);

		template.setStatus(ruleSet.getStatus());
		template.setUsageType(ruleSet.getUsageType());

		logger.debug("<<< produceTemplate");
		return template;
	}

	@Override
	public void importGridData(int templateID, List<ProductGrid> grids, User user) throws ImportException {
		GridActionCoordinator.getInstance().importGridData(templateID, grids, user);
	}

	@Override
	public void importParameterGrid(ParameterGrid parameterGrid, User user) throws ImportException {
		logger.debug(">>> importParameterGrid(ParameterContext) with " + parameterGrid + " for " + user);
		logger.debug("importParameterGrid(ParameterContext) cell values = " + parameterGrid.getCellValues());
		try {
			int gridID = parameterGrid.getID();
			if (gridID == -1 || ParameterManager.getInstance().getGrid(gridID) == null) {
				if (gridID == -1) {
					int newGridID = DBIdGenerator.getInstance().nextSequentialID();
					parameterGrid.setID(newGridID);
					logger.debug("importParameterGrid(ParameterContext): newGridID = " + parameterGrid.getID());
				}
				logger.debug("importParameterGrid(ParameterContext): inserting a new row into DB as " + gridID);
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
				logger.debug("importParameterGrid(ParameterContext): inserting the parameter grid into cache...");
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
				logger.debug("importParameterGrid(ParameterContext): lock passed...");
				logger.debug("importParameterGrid(ParameterContext): updating parameter grid row...");
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
				logger.debug("importParameterGrid(ParameterContext): updating cache...");
				ParameterGrid cachedParamGrid = ParameterManager.getInstance().getGrid(parameterGrid.getID());
				ParameterManager.getInstance().updateCache(parameterGrid);
				AuditLogger.getInstance().logUpdate(parameterGrid, cachedParamGrid, user.getUserID());
			}
			logger.debug("<<< importParameterGrid(ParameterContext): " + gridID);
		}
		catch (SQLException ex) {
			logger.error("Failed to import parameter grid " + parameterGrid, ex);
			throw new ImportException("failed to updated DB: " + ex.getLocalizedMessage());
		}
		catch (LockException ex) {
			throw new ImportException("failed to lock - locked by " + ex.getLockedBy());
		}
		catch (SapphireException ex) {
			logger.error("Failed to import parameter grid " + parameterGrid, ex);
			throw new ImportException("failed to generate new ID: " + ex.getLocalizedMessage());
		}
	}

	public int save(ParameterGrid parameterGrid, User user) throws ServletActionException {
		logger.debug(">>> save(ParameterContext) with " + parameterGrid + " for " + user);
		try {
			int gridID = parameterGrid.getID();
			if (gridID == -1) {
				int newGridID = DBIdGenerator.getInstance().nextSequentialID();
				parameterGrid.setID(newGridID);
				logger.debug("save(ParameterContext): inserting a new row into DB as " + newGridID);
				parameterUpdater.insertGrid(
						newGridID,
						parameterGrid.getTemplateID(),
						parameterGrid.getCellValues(),
						parameterGrid.getNumRows(),
						parameterGrid.getEffectiveDate(),
						parameterGrid.getExpirationDate(),
						parameterGrid.getStatus(),
						ServerContextUtil.extractGenericEntityIdentities(parameterGrid),
						ServerContextUtil.extractGenericCategoryIdentities(parameterGrid));
				logger.debug("save(ParameterContext): inserting the parameter grid into cache...");
				ParameterManager.getInstance().insertIntoCache(
						newGridID,
						parameterGrid.getTemplateID(),
						parameterGrid.getCellValues(),
						parameterGrid.getNumRows(),
						parameterGrid.getEffectiveDate(),
						parameterGrid.getExpirationDate(),
						parameterGrid.getStatus(),
						parameterGrid.extractGuidelineContext());
				AuditLogger.getInstance().logNewGrid(parameterGrid, user.getUserID());
				logger.debug("<<< save(ParameterContext): " + newGridID);
				return newGridID;
			}
			else {
				// 1. check lock
				LockManager.getInstance().getExistingParameterGridLock(parameterGrid.getID(), user);
				logger.debug("save(ParameterContext): lock passed...");
				logger.debug("save(ParameterContext): updating parameter grid row...");
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
				logger.debug("save(ParameterContext): updating cache...");
				ParameterGrid cachedParamGrid = ParameterManager.getInstance().getGrid(gridID);
				AuditLogger.getInstance().logUpdateGrid(parameterGrid, cachedParamGrid, user.getUserID());
				ParameterManager.getInstance().updateCache(parameterGrid);
				logger.debug("<<< save(ParameterContext): " + gridID);
				return gridID;
			}
		}
		catch (LockException ex) {
			throw new ServletActionException("LockError", ex.getLockedBy());
		}
		catch (SQLException ex) {
			logger.error("Failed to save " + parameterGrid, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
		catch (SapphireException ex) {
			logger.error("Failed to save " + parameterGrid, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	private void deleteParameter(int parameterID, User user) throws ServletActionException {
		logger.debug(">>> deleteParameter: " + parameterID + "for " + user);
		try {
			if (ParameterManager.getInstance().getGrid(parameterID) == null) {
				throw new SQLException("Paramter grid " + parameterID + " not found");
			}

			LockManager.getInstance().getExistingLock(
					EntityType.PARAMETER_TEMPLATE,
					ParameterManager.getInstance().getGrid(parameterID).getTemplateID(),
					user);
			logger.debug("deleteParameter: deleting from DB");
			parameterUpdater.removeGrid(parameterID);
			ParameterGrid cachedGrid = ParameterManager.getInstance().getGrid(parameterID);
			logger.debug("deleteParameter: removing from cache");
			ParameterManager.getInstance().removeGridFromCache(parameterID);

			AuditLogger.getInstance().logDeleteGrids(Collections.singletonList(cachedGrid), user.getUserID());
			logger.debug("<<< deleteParameter");
		}
		catch (SQLException ex) {
			logger.error("Failed to delete parameter " + parameterID, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
		catch (LockException ex) {
			throw new ServletActionException("LockError", ex.getLockedBy());
		}
	}

	/**
	 * Imports role 
	 * @param role
	 * @param unknownPrivsForRole
	 * @param user
	 * @throws ImportException
	 */
	public void importRole(Role role, List<String> unknownPrivsForRole, User user) throws ImportException {
		int roleID = role.getID();
		try {
			if (SecurityCacheManager.getInstance().getRole(roleID) == null) {
				logger.debug("save(Role): new role ID = " + role.getID());
				if (!unknownPrivsForRole.isEmpty()) {//some exported privileges do not exist now. should be used mainly for going from 4.5.x to 5.0.0
					userDataUpdater.insertRoleWithUnknownPrivileges(
							roleID,
							role.getName(),
							toIDs(role.getPrivileges()),
							unknownPrivsForRole);
				}
				else {// normal insert of roles during normal export
					userDataUpdater.insertRole(roleID, role.getName(), toIDs(role.getPrivileges()));
				}

				SecurityCacheManager.getInstance().insertCache(role);
				logger.debug("save(Role): inserted into cache. from cache = " + SecurityCacheManager.getInstance().getRole(roleID));
				AuditLogger.getInstance().logNew(role, user.getUserID());
			}
			else {
				LockManager.getInstance().getExistingLock(EntityType.ROLE, roleID, user);
				if (!unknownPrivsForRole.isEmpty()) {//some exported privileges do not exist now. should be used mainly for going from 4.5.x to 5.0.0
					userDataUpdater.updateRoleWithUnknownPrivileges(
							roleID,
							role.getName(),
							toIDs(role.getPrivileges()),
							unknownPrivsForRole);
				}
				else {// normal update of roles during normal export
					userDataUpdater.updateRole(roleID, role.getName(), toIDs(role.getPrivileges()));
				}
				AuditLogger.getInstance().logUpdate(role, SecurityCacheManager.getInstance().getRole(roleID), user.getUserID());
				SecurityCacheManager.getInstance().updateCache(SecurityCacheManager.getInstance().getRole(roleID), role);
			}
		}
		catch (SQLException ex) {
			logger.error("Failed to import role " + role, ex);
			throw new ImportException("failed to updated DB: " + ex.getLocalizedMessage());
		}
		catch (LockException ex) {
			throw new ImportException("failed to lock - locked by " + ex.getLockedBy());
		}
	}

	public int save(Role role, User user) throws ServletActionException {
		logger.debug(">>> save(Role) with " + role);
		int roleID = role.getID();
		try { // 1. check lock
			if (roleID != -1) {
				logger.debug("save(Role): connection obtained. updating role table...");
				LockManager.getInstance().getExistingLock(EntityType.ROLE, roleID, user);
				userDataUpdater.updateRole(roleID, role.getName(), toIDs(role.getPrivileges()));
				AuditLogger.getInstance().logUpdate(role, SecurityCacheManager.getInstance().getRole(roleID), user.getUserID());
				SecurityCacheManager.getInstance().updateCache(SecurityCacheManager.getInstance().getRole(roleID), role);
			}
			else {
				logger.debug("save(Role): inserting into role table...");
				roleID = DBIdGenerator.getInstance().nextSequentialID();
				role.setID(roleID);
				logger.debug("save(Role): new role ID = " + role.getID());
				userDataUpdater.insertRole(roleID, role.getName(), toIDs(role.getPrivileges()));
				SecurityCacheManager.getInstance().insertCache(role);
				AuditLogger.getInstance().logNew(role, user.getUserID());
				logger.debug("save(Role): inserted into cache. from cache = " + SecurityCacheManager.getInstance().getRole(roleID));
			}
			logger.debug("<<< save(Role) with " + roleID);
			return roleID;
		}
		catch (LockException ex) {
			throw new ServletActionException("LockFailureMsg", ex.getLockedBy());
		}
		catch (SQLException ex) {
			logger.error("Failed to save " + role, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
		catch (SapphireException ex) {
			logger.error("Failed to save " + role, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	private void deleteRole(int roleID, User user) throws ServletActionException {
		logger.debug(">>> deleteRole with " + roleID);
		try {
			// 1. check lock
			LockManager.getInstance().getExistingLock(EntityType.ROLE, roleID, user);
			logger.debug("deleteRole: connection obtained. updating role table...");
			UserSecurityUpdater updater = new UserSecurityUpdater();
			updater.deleteRole(roleID);
			logger.debug("deleteRole: role table updated. removing the role from cache...");

			AuditLogger.getInstance().logDelete(SecurityCacheManager.getInstance().getRole(roleID), user.getUserID());
			SecurityCacheManager.getInstance().removeFromCache(roleID);
			logger.debug("<<< deleteRole");
		}
		catch (LockException ex) {
			throw new ServletActionException("LockFailureMsg", ex.getLockedBy());
		}
		catch (Exception ex) {
			logger.error("Failed to delete " + roleID, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	public void importUser(User userToSave, User requester) throws ImportException {
		logger.debug(">>> importUser(User) with " + userToSave);
		String userID = userToSave.getUserID();
		try {
			User cachedUser = SecurityCacheManager.getInstance().getUser(userID);
			if (cachedUser == null) {
				logger.debug("importUser(User): inserting new user...");
				userDataUpdater.insertUser(
						userID,
						userToSave.getName(),
						userToSave.getStatus(),
						userToSave.getPasswordChangeRequired(),
						userToSave.getFailedLoginCounter(),
						toIDs(userToSave.getRoles()),
						userToSave.getPasswordHistory(),
						requester.getUserID());
				logger.info("User not found in cache. inserting into cache...");
				SecurityCacheManager.getInstance().insertCache(userToSave.asUserData());
				AuditLogger.getInstance().logImport(userToSave, requester.getUserID());
			}
			else {
				LockManager.getInstance().getExistingLock(EntityType.USER_DATA, userID, requester);
				logger.debug("importUser(User): updating user table...");
				userDataUpdater.updateUser(
						userID,
						userToSave.getName(),
						userToSave.getStatus(),
						userToSave.getPasswordChangeRequired(),
						userToSave.getFailedLoginCounter(),
						toIDs(userToSave.getRoles()),
						userToSave.getPasswordHistory(),
						requester.getUserID());
				logger.debug("save(User): user updated. updating cache");
				AuditLogger.getInstance().logUpdate(userToSave, cachedUser, requester.getUserID());
				SecurityCacheManager.getInstance().updateCache(cachedUser, userToSave.asUserData());
			}
		}
		catch (SQLException ex) {
			logger.error("Failed to import user " + userToSave, ex);
			throw new ImportException("failed to updated DB: " + ex.getLocalizedMessage());
		}
		catch (LockException ex) {
			throw new ImportException("failed to lock - locked by " + ex.getLockedBy());
		}
	}

	public void updateUserPassword(String userID, String newPassword, String requester) throws ServletActionException {
		logger.debug(">>> updateUserPassword(User) with " + userID);
		try {
			User cachedUser = SecurityCacheManager.getInstance().getUser(userID);
			if (cachedUser == null) {
				throw new ServletActionException("ServerError", "User not found: " + userID);
			}

			cachedUser.setPassword(newPassword);
			logger.debug("updateUserPassword(User): updating user table...");
			userDataUpdater.updateUser(
					userID,
					cachedUser.getName(),
					cachedUser.getStatus(),
					false,
					0,
					toIDs(cachedUser.getRoles()),
					cachedUser.getPasswordHistory(),
					requester);

			// update cache after DB dave
			cachedUser.setPasswordChangeRequired(false); // user has successfully reset password
			cachedUser.setFailedLoginCounter(0); // reset failed login counter
			logger.debug("<<< updateUserPassword(User)");
		}
		catch (SQLException ex) {
			logger.error("Failed to change password of " + userID, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	public void updateFailedLoginCounter(String userID, int newValue) throws ServletActionException {
		logger.debug(">>> incrementFailedLoginCounter(User) with " + userID);
		try {
			User cachedUser = SecurityCacheManager.getInstance().getUser(userID);
			if (cachedUser == null) {
				throw new ServletActionException("ServerError", "User not found: " + userID);
			}

			logger.debug("incrementFailedLoginCounter(User): updating user table...");
			userDataUpdater.updateFailedLoginCounter(userID, newValue);
			// update cache after DB dave
			cachedUser.setFailedLoginCounter(newValue); // reset failed login counter

			logger.debug("<<< incrementFailedLoginCounter(User)");
		}
		catch (SQLException ex) {
			logger.error("Failed to change failed login counter of " + userID, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	public void enableUser(String userId, User requester) throws ServletActionException {
		logger.debug(String.format(">>> enabledUser: %s, by %s", userId, requester.getUserID()));
		try {
			User cachedUser = SecurityCacheManager.getInstance().getUser(userId);
			if (cachedUser == null) {
				throw new ServletActionException("ServerError", String.format("User %s not found", userId));
			}
			else {
				userDataUpdater.enableUser(userId, requester.getUserID());
				cachedUser.setDisabled(false);
			}
		}
		catch (SQLException ex) {
			logger.error("Failed to enable " + userId, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	public int save(User userToSave, User requester) throws ServletActionException {
		logger.debug(">>> save(User) with " + userToSave);
		String userID = userToSave.getUserID();
		try {
			User cachedUser = SecurityCacheManager.getInstance().getUser(userID);
			if (cachedUser == null) {
				logger.debug("save(User): inserting new user...");
				userDataUpdater.insertUser(userID, userToSave.getName(), userToSave.getStatus(), true, // new user. ensure that flag for resetPWD is true
						0, // since new password, there are no inavlid attempts
						toIDs(userToSave.getRoles()),
						userToSave.getPasswordHistory(),
						requester.getUserID());
				logger.info("User not found in cache. inserting into cache...");
				SecurityCacheManager.getInstance().insertCache(userToSave.asUserData());
				AuditLogger.getInstance().logNew(userToSave, requester.getUserID());
			}
			else {
				LockManager.getInstance().getExistingLock(EntityType.USER_DATA, userID, requester);
				logger.debug("save(User): updating user table...");
				userDataUpdater.updateUser(userID, userToSave.getName(), userToSave.getStatus(), userToSave.getPasswordChangeRequired(),//next 3 depend on whether save request comes from password reset controller or normal save
						userToSave.getFailedLoginCounter(),
						toIDs(userToSave.getRoles()),
						userToSave.getPasswordHistory(),
						requester.getUserID());
				logger.debug("save(User): user updated. updating cache");
				AuditLogger.getInstance().logUpdate(userToSave, cachedUser, requester.getUserID());
				SecurityCacheManager.getInstance().updateCache(cachedUser, userToSave.asUserData());
			}

			logger.debug("<<< save(User) with 0");
			return 0;
		}
		catch (LockException ex) {
			throw new ServletActionException("LockFailureMsg", ex.getLockedBy());
		}
		catch (SQLException ex) {
			logger.error("Failed to save " + userToSave, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	private void deleteUser(String userID, User user) throws ServletActionException {
		logger.debug(">>> deleteUser with " + userID);
		try {
			// 1. check lock
			LockManager.getInstance().getExistingLock(EntityType.USER_DATA, userID, user);
			User userFromCache = SecurityCacheManager.getInstance().getUser(userID);

			logger.debug("deleteUser: connection obtained. updating user table...");
			UserSecurityUpdater updater = new UserSecurityUpdater();
			updater.deleteUser(userID, user.getUserID());
			SecurityCacheManager.getInstance().removeFromCache(userID);

			AuditLogger.getInstance().logDelete(userFromCache, user.getUserID());
			logger.debug("<<< deleteUser with 0");
		}
		catch (LockException ex) {
			throw new ServletActionException("LockFailureMsg", ex.getLockedBy());
		}
		catch (Exception ex) {
			logger.error("Failed to delete " + userID, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	@Override
	public void importCBRCaseBase(CBRCaseBase caseBase, boolean merge, Map<String, Integer> cbrDataIdMap, User user) throws ImportException {
		int id = caseBase.getID();
		try {
			if (!merge && id != -1 && CBRManager.getInstance().getCBRCaseBase(id) != null) {
				update(caseBase, user);
			}
			else {
				int oldID = caseBase.getId();
				int newID = insert(caseBase, user);
				cbrDataIdMap.put("casebase-" + oldID, newID);
			}
		}
		catch (Exception ex) {
			logger.error("Failed to import case base " + caseBase, ex);
			throw new ImportException("Failed to import the case base " + caseBase + ": " + ex.getMessage());
		}
	}

	/**
	 * Save the CBR Case Base.
	 * 
	 * @param cbrCaseBase
	 * @param user
	 * @return int
	 * @throws ServletActionException
	 * @author Inna Nill
	 * @since PowerEditor 4.1.0
	 */
	public int save(CBRCaseBase cbrCaseBase, User user) throws ServletActionException {
		logger.debug(">>> save(CBRCaseBase) with " + cbrCaseBase);

		int cbrCaseBaseID = cbrCaseBase.getID();
		CBRCaseBase cachedCaseBase = CBRManager.getInstance().getCBRCaseBase(cbrCaseBaseID);
		if (cachedCaseBase == null) {
			insert(cbrCaseBase, user);
		}
		else {
			update(cbrCaseBase, user);
		}
		return cbrCaseBaseID;
	}

	private int insert(CBRCaseBase cbrCaseBase, User user) throws ServletActionException {
		try {
			// 1. Get a new case base ID.
			int caseBaseID = DBIdGenerator.getInstance().nextSequentialID();
			cbrCaseBase.setID(caseBaseID);
			logger.debug("Inserting new cbr case base...");

			// 2. Insert case base into DB.
			new CBRCaseBaseUpdater().insertCBRCaseBase(cbrCaseBase);
			logger.info("CBR Case Base not found in cache. inserting into cache...");
			CBRManager.getInstance().addCBRCaseBase(cbrCaseBase);

			return caseBaseID;
		}
		catch (LockException ex) {
			throw new ServletActionException("LockFailureMsg", ex.getLockedBy());
		}
		catch (SQLException ex) {
			logger.error("Failed to save " + cbrCaseBase, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
		catch (SapphireException ex) {
			logger.error("Failed to save " + cbrCaseBase, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	private void update(CBRCaseBase cbrCaseBase, User user) throws ServletActionException {
		try {
			int cbrCaseBaseID = cbrCaseBase.getID();
			LockManager.getInstance().getExistingLock(EntityType.CBR_CASE_BASE, cbrCaseBaseID, user);
			logger.debug("updating CBRCaseBase table...");
			new CBRCaseBaseUpdater().updateCBRCaseBase(cbrCaseBase);
			CBRManager.getInstance().addCBRCaseBase(cbrCaseBase); // add calls update
			// internally
			logger.debug("CBR Case Base updated.");
			if (cbrCaseBase.getName().indexOf("bIGtEST") == 0) {
				String tokens[] = cbrCaseBase.getName().split(" ");
				int attCount = 20;
				int caseCount = 50;
				int avCount = 10;
				try {
					if (tokens.length >= 2) attCount = Integer.parseInt(tokens[1]);
					if (tokens.length >= 3) caseCount = Integer.parseInt(tokens[2]);
					if (tokens.length >= 4) avCount = Integer.parseInt(tokens[3]);
				}
				catch (NumberFormatException x) {
				}
				insertTestData(user, cbrCaseBase, attCount, caseCount, avCount);
			}
		}
		catch (LockException ex) {
			throw new ServletActionException("LockFailureMsg", ex.getLockedBy());
		}
		catch (SQLException ex) {
			logger.error("Failed to save " + cbrCaseBase, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	void insertTestData(User user, CBRCaseBase caseBase, int attCount, int caseCount, int avCount) throws ServletActionException {
		for (int i = 0; i < attCount; i++) {
			CBRAttribute att = new CBRAttribute();
			att.setCaseBase(caseBase);
			att.setName("att" + i);
			att.setDescription("");
			att.setAttributeType(CBRManager.getInstance().getCBRAttributeType(1));
			att.setAbsencePenalty(Constants.CBR_NULL_DATA_EQUIVALENT_VALUE);
			att.setMatchContribution(Constants.CBR_NULL_DATA_EQUIVALENT_VALUE);
			att.setMismatchPenalty(Constants.CBR_NULL_DATA_EQUIVALENT_VALUE);
			att.setHighestValue(Constants.CBR_NULL_DOUBLE_VALUE);
			att.setLowestValue(Constants.CBR_NULL_DOUBLE_VALUE);
			att.setMatchInterval(Constants.CBR_NULL_DOUBLE_VALUE);
			att.setValueRange(CBRManager.getInstance().getCBRValueRange(1));
			this.save(att, user);
			for (int j = 0; j < caseCount; j++) {
				CBRCase c = new CBRCase();
				c.setCaseBase(caseBase);
				c.setName("case" + j + "-" + i);
				c.setDescription("");
				for (int k = 0; k < avCount; k++) {
					CBRAttributeValue av = new CBRAttributeValue();
					av.setAttribute(att);
					av.setDescription("");
					av.setName("av" + k);
					av.setMatchContribution(Constants.CBR_NULL_DATA_EQUIVALENT_VALUE);
					av.setMismatchPenalty(Constants.CBR_NULL_DATA_EQUIVALENT_VALUE);
					c.getAttributeValues().add(av);
				}
				this.save(c, user);
			}
		}
	}

	@Override
	public void importCBRAttribute(CBRAttribute attribute, boolean merge, Map<String, Integer> cbrDataIdMap, User user)
			throws ImportException {
		int id = attribute.getID();
		try {
			if (!merge && id != -1 && CBRManager.getInstance().getCBRAttribute(id) != null) {
				update(attribute, user);
			}
			else {
				int oldID = attribute.getId();
				int newID = insert(attribute, user);
				cbrDataIdMap.put("attribute-" + oldID, newID);
			}
		}
		catch (Exception ex) {
			logger.error("Failed to import CBR attribute " + attribute, ex);
			throw new ImportException("Failed to import the CBR attribute" + attribute + ": " + ex.getMessage());
		}
	}

	public int save(CBRAttribute cbrAttribute, User user) throws ServletActionException {
		logger.debug(">>> save(CBRAttribute) with " + cbrAttribute);

		int cbrAttributeID = cbrAttribute.getID();
		CBRAttribute cachedAttribute = CBRManager.getInstance().getCBRAttribute(cbrAttributeID);
		if (cachedAttribute == null) {
			insert(cbrAttribute, user);
		}
		else {
			update(cbrAttribute, user);
		}
		return cbrAttributeID;
	}

	private int insert(CBRAttribute cbrAttribute, User user) throws ServletActionException {
		try {
			// 1. Get a new attribute ID.
			int cbrAttributeID = DBIdGenerator.getInstance().nextSequentialID();
			cbrAttribute.setID(cbrAttributeID);
			logger.debug("save(CBRAttribute): inserting new cbr attribute...");

			// 2. Insert attribute into DB.
			new CBRAttributeUpdater().insertCBRAttribute(cbrAttribute);
			logger.info("CBR Attribute not found in cache. inserting into cache...");
			CBRManager.getInstance().addCBRAttribute(cbrAttribute);
			return cbrAttributeID;
		}
		catch (SQLException ex) {
			logger.error("Failed to save " + cbrAttribute, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
		catch (SapphireException ex) {
			logger.error("Failed to save " + cbrAttribute, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	private void update(CBRAttribute cbrAttribute, User user) throws ServletActionException {
		try {
			LockManager.getInstance().getExistingLock(EntityType.CBR_ATTRIBUTE, cbrAttribute.getID(), user);
			logger.debug("updating CBRAttribute table...");
			new CBRAttributeUpdater().updateCBRAttribute(cbrAttribute);
			CBRManager.getInstance().addCBRAttribute(cbrAttribute); // add calls update
			// internally
			logger.debug("CBR Attribute updated.");
		}
		catch (LockException ex) {
			throw new ServletActionException("LockFailureMsg", ex.getLockedBy());
		}
		catch (SQLException ex) {
			logger.error("Failed to save " + cbrAttribute, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	@Override
	public void importCBRCase(CBRCase cbrCase, boolean merge, Map<String, Integer> cbrDataIdMap, User user) throws ImportException {
		int id = cbrCase.getID();
		try {
			if (!merge && id != -1 && CBRManager.getInstance().getCBRCase(id) != null) {
				update(cbrCase, user);
			}
			else {
				int oldID = cbrCase.getId();
				int newID = insert(cbrCase, user);
				cbrDataIdMap.put("case-" + oldID, newID);
			}
		}
		catch (Exception ex) {
			logger.error("Failed to import CBR case " + cbrCase, ex);
			throw new ImportException("Failed to import the CBR case" + cbrCase + ": " + ex.getMessage());
		}
	}

	/**
	 * Save the CBR Case.
	 * 
	 * @param cbrCase
	 * @param user
	 * @return New ID of the new cbr case.
	 * @throws ServletActionException
	 * @author Inna Nill
	 * @since PowerEditor 4.1.0
	 */
	public int save(CBRCase cbrCase, User user) throws ServletActionException {
		logger.debug(">>> save(CBRCase) with " + cbrCase);

		int cbrCaseID = cbrCase.getID();
		CBRCase cachedCase = CBRManager.getInstance().getCBRCase(cbrCaseID);
		if (cachedCase == null) {
			insert(cbrCase, user);
		}
		else {
			update(cbrCase, user);
		}
		return cbrCaseID;
	}

	private int insert(CBRCase cbrCase, User user) throws ServletActionException {
		try {
			// 1. Get a new case ID.
			int cbrCaseID = DBIdGenerator.getInstance().nextSequentialID();
			cbrCase.setID(cbrCaseID);
			// Now generate new ID's for every attribute value in the case.
			Iterator<CBRAttributeValue> it = cbrCase.getAttributeValues().iterator();
			CBRAttributeValue val = null;
			while (it.hasNext()) {
				val = it.next();
				int ValID = DBIdGenerator.getInstance().nextSequentialID();
				val.setID(ValID);
			}
			logger.debug("save(CBRCase): inserting new cbr case ...");

			// 2. Insert case into DB.
			new CBRCaseUpdater().insertCBRCase(cbrCase);
			logger.info("CBR Case not found in cache. inserting into cache...");
			CBRManager.getInstance().addCBRCase(cbrCase);

			return cbrCaseID;
		}
		catch (SQLException ex) {
			logger.error("Failed to save " + cbrCase, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
		catch (SapphireException ex) {
			logger.error("Failed to save " + cbrCase, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	private void update(CBRCase cbrCase, User user) throws ServletActionException {
		try {
			// Generate new ID's for every attribute value in the case.
			Iterator<CBRAttributeValue> it = cbrCase.getAttributeValues().iterator();
			CBRAttributeValue val = null;
			while (it.hasNext()) {
				val = it.next();
				int ValID = DBIdGenerator.getInstance().nextSequentialID();
				val.setID(ValID);
			}
			LockManager.getInstance().getExistingLock(EntityType.CBR_CASE, cbrCase.getId(), user);
			logger.debug("save(CBRCase): updating CBRCase table...");
			new CBRCaseUpdater().updateCBRCase(cbrCase);
			CBRManager.getInstance().addCBRCase(cbrCase); // add calls update internally
			logger.debug("save(CBRCase): CBR Case updated.");
		}
		catch (LockException ex) {
			throw new ServletActionException("LockFailureMsg", ex.getLockedBy());
		}
		catch (SQLException ex) {
			logger.error("Failed to save " + cbrCase, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
		catch (SapphireException ex) {
			logger.error("Failed to save " + cbrCase, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	/**
	 * Clone the CBR Case Base.
	 * 
	 * @param oldCaseBaseID
	 * @param newCaseBaseName
	 * @param user
	 * @return int
	 * @throws ServletActionException
	 * @author Inna Nill
	 * @since PowerEditor 4.1.0
	 */
	public int cloneCBR(int oldCaseBaseID, String newCaseBaseName, User user) throws ServletActionException {
		logger.debug(">>> cloneCBR with " + oldCaseBaseID);

		try {
			int caseBaseID = Persistent.UNASSIGNED_ID;
			List<AbstractIDNameDescriptionObject> allItemsToClone = new ArrayList<AbstractIDNameDescriptionObject>();
			Hashtable<Integer, CBRAttribute> attributeIDMapping = new Hashtable<Integer, CBRAttribute>();
			CBRCaseBase cachedCaseBase = CBRManager.getInstance().getCBRCaseBase(oldCaseBaseID);
			if (cachedCaseBase != null) {
				// 1. Get a new case base ID.
				caseBaseID = DBIdGenerator.getInstance().nextSequentialID();
				CBRCaseBase newCaseBase = new CBRCaseBase();
				newCaseBase.copyFrom(cachedCaseBase);
				newCaseBase.setID(caseBaseID);
				newCaseBase.setName(newCaseBaseName);
				allItemsToClone.add(newCaseBase);
				logger.debug("cloneCBR: created new cbr case base to clone...");

				// 2. Get new attribute id's for each attr for the old case base.
				List<Integer> attrIDs = DBCBRUtil.getAttributesForCaseBase(oldCaseBaseID);
				logger.info("    found " + attrIDs.size() + " attributes to clone.");
				Iterator<Integer> it = attrIDs.iterator();
				CBRAttribute nextAttr = null;
				while (it.hasNext()) {
					nextAttr = (CBRAttribute) CBRManager.getInstance().getCBRAttribute(it.next());
					if (nextAttr != null) {
						CBRAttribute newAttr = new CBRAttribute();
						newAttr.copyFrom(nextAttr);
						int attributeID = DBIdGenerator.getInstance().nextSequentialID();
						newAttr.setID(attributeID);
						newAttr.setCaseBase(newCaseBase);
						allItemsToClone.add(newAttr);
						attributeIDMapping.put(new Integer(nextAttr.getId()), newAttr);
					}
				}
				logger.debug("cloneCBR: created new cbr attributes to clone...");
				logger.debug(" *** LOOKING AT attributeIDMapping: " + attributeIDMapping.toString());

				// 3. Get new case id's for each case for the old case base.
				List<Integer> caseIDs = DBCBRUtil.getCasesForCaseBase(oldCaseBaseID);
				logger.info("    found " + caseIDs.size() + " cases to clone.");
				it = caseIDs.iterator();
				CBRCase nextCase = null;
				while (it.hasNext()) {
					nextCase = (CBRCase) CBRManager.getInstance().getCBRCase(it.next());
					logger.debug("   *** OLD CASE: " + nextCase);
					if (nextCase != null) {
						CBRCase newCase = new CBRCase();
						newCase.copyFrom(nextCase);
						logger.debug("   *** NEW CASE: " + newCase);
						int caseID = DBIdGenerator.getInstance().nextSequentialID();
						newCase.setID(caseID);
						newCase.setCaseBase(newCaseBase);
						Iterator<CBRAttributeValue> attrValIter = newCase.getAttributeValues().iterator();
						CBRAttributeValue attrVal = null;
						while (attrValIter.hasNext()) {
							attrVal = attrValIter.next();
							int attrValID = DBIdGenerator.getInstance().nextSequentialID();
							attrVal.setID(attrValID);
							logger.debug("   *** WHEN CREATING NEW attribute values, old ID = " + attrVal.getAttribute().getID()
									+ " and associated attributeIDMapping mapped new attribute = "
									+ attributeIDMapping.get(new Integer(attrVal.getAttribute().getID())));
							attrVal.setAttribute(attributeIDMapping.get(new Integer(attrVal.getAttribute().getID())));
						}
						allItemsToClone.add(newCase);
					}
				}
				logger.debug("cloneCBR: created new cbr cases to clone...");

				// 4. Insert everything into DB.
				DBCBRUtil.cloneCBR(allItemsToClone);

				// 5. Put everything into cache.
				Iterator<AbstractIDNameDescriptionObject> it2 = allItemsToClone.iterator();
				AbstractIDNameDescriptionObject obj = null;
				while (it2.hasNext()) {
					obj = it2.next();
					if (obj instanceof CBRCaseBase) {
						CBRManager.getInstance().addCBRCaseBase((CBRCaseBase) obj);
					}
					else if (obj instanceof CBRAttribute) {
						CBRManager.getInstance().addCBRAttribute((CBRAttribute) obj);
					}
					else if (obj instanceof CBRCase) {
						CBRManager.getInstance().addCBRCase((CBRCase) obj);
					}
				}
				AuditLogger.getInstance().logNew(newCaseBase, user.getUserID());
				logger.info("Inserted cloned CBR items into cache...");
			}
			else {
				throw new ServletActionException("CloneFailureMsg", "Error: Failed to clone case base " + oldCaseBaseID + ".");
			}
			return caseBaseID;
		}
		catch (LockException ex) {
			throw new ServletActionException("LockFailureMsg", ex.getLockedBy());
		}
		catch (SQLException ex) {
			logger.error("Failed to clone " + oldCaseBaseID, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
		catch (SapphireException ex) {
			logger.error("Failed to clone " + oldCaseBaseID, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
	}

	/**
	 * Delete CBR Case base
	 * 
	 * @param cbrCaseBaseID
	 * @param user
	 * @throws ServletActionException
	 * @author Inna Nill
	 * @since PowerEditor 4.1.0
	 */
	private void deleteCBRCaseBase(int cbrCaseBaseID, User user) throws ServletActionException {
		logger.debug(">>> deleteCBRCaseBase with " + cbrCaseBaseID);
		CBRCaseBase cachedCaseBase = CBRManager.getInstance().getCBRCaseBase(cbrCaseBaseID);
		CBRCaseBaseUpdater updater = new CBRCaseBaseUpdater();
		if (cachedCaseBase != null) {
			try {
				// 1. check lock
				LockManager.getInstance().getExistingLock(EntityType.CBR_CASE_BASE, cbrCaseBaseID, user);
				logger.debug("deleteCBRCaseBase: lock established. deleting cbr case base...");
				updater.deleteCBRCaseBase(cbrCaseBaseID);
				logger.debug("deleteCBRCaseBase: deleted cbr case base. removing from cache...");
				CBRManager.getInstance().removeCBRCaseBaseFromCache(cbrCaseBaseID);
				logger.debug("<<< deleteCBRCaseBase");
			}
			catch (LockException ex) {
				throw new ServletActionException("LockFailureMsg", ex.getLockedBy());
			}
			catch (SQLException ex) {
				logger.error("Failed to delete " + cbrCaseBaseID, ex);
				throw new ServletActionException("ServerError", ex.getMessage());
			}
		}
	}

	private void deleteCBRAttribute(int cbrAttributeID, User user) throws ServletActionException {
		logger.debug(">>> deleteCBRAttribute with " + cbrAttributeID);
		CBRAttribute cachedAttribute = CBRManager.getInstance().getCBRAttribute(cbrAttributeID);
		CBRAttributeUpdater updater = new CBRAttributeUpdater();
		if (cachedAttribute != null) {
			try {
				// 1. check lock
				LockManager.getInstance().getExistingLock(EntityType.CBR_ATTRIBUTE, cbrAttributeID, user);
				logger.debug("deleteCBRAttribute: lock established. deleting cbr attribute...");
				updater.deleteCBRAttribute(cbrAttributeID);
				logger.debug("deleteCBRAttribute: deleted cbr attribute. removing from cache...");
				CBRManager.getInstance().removeCBRAttributeFromCache(cbrAttributeID);
				logger.debug("<<< deleteCBRAttribute");
			}
			catch (ValidationException ex) {
				throw new ServletActionException("ValidationErrorMsg", ex.getMessage());
			}
			catch (LockException ex) {
				throw new ServletActionException("LockFailureMsg", ex.getLockedBy());
			}
			catch (SQLException ex) {
				logger.error("Failed to delete " + cbrAttributeID, ex);
				throw new ServletActionException("ServerError", ex.getMessage());
			}
		}
	}

	/**
	 * Delete the case with the given case id.
	 * 
	 * @param cbrCaseID
	 * @param user
	 * @throws ServletActionException
	 * @author Inna Nill
	 * @since PowerEditor 4.1.0
	 */
	private void deleteCBRCase(int cbrCaseID, User user) throws ServletActionException {
		logger.debug(">>> deleteCBRCase with " + cbrCaseID);
		CBRCase cachedCase = CBRManager.getInstance().getCBRCase(cbrCaseID);
		CBRCaseUpdater updater = new CBRCaseUpdater();
		if (cachedCase != null) {
			try {
				// 1. check lock
				LockManager.getInstance().getExistingLock(EntityType.CBR_CASE, cbrCaseID, user);
				logger.debug("deleteCBRCase: lock established. deleting cbr case...");
				updater.deleteCBRCase(cbrCaseID);
				logger.debug("deleteCBRCase: deleted cbr case. removing from cache...");
				CBRManager.getInstance().removeCBRCaseFromCache(cbrCaseID);
				logger.debug("<<< deleteCBRCase");
			}
			catch (LockException ex) {
				throw new ServletActionException("LockFailureMsg", ex.getLockedBy());
			}
			catch (SQLException ex) {
				logger.error("Failed to delete " + cbrCaseID, ex);
				throw new ServletActionException("ServerError", ex.getMessage());
			}
		}
	}

	public void deleteGenericCategory(int categoryType, int categoryID, User user) throws ServletActionException {
		DBConnectionManager dbconnectionmanager = DBConnectionManager.getInstance();
		Connection conn = null;
		try {
			GenericCategory cachedCategory = EntityManager.getInstance().getGenericCategory(categoryType, categoryID);

			conn = dbconnectionmanager.getConnection();
			conn.setAutoCommit(false);

			// TODO Kim, 2006-12-06: The following two lines should be execuded in a single DB transaction
			//                       This requires some changes to service provider API. 
			//                       This ought to be done when we replace data access layer with a 3rd-party ORM tool

			new GenericEntityUpdater(conn).deleteCategory(categoryID, categoryType);
			conn.commit();

			GridActionCoordinator.getInstance().removeGenericCategoryFromContextAndEntityListColumns(categoryType, categoryID);

			// 4. remove category from server cache
			EntityManager.getInstance().removeCategory(categoryType, categoryID);

			AuditLogger.getInstance().logDelete(cachedCategory, user.getUserID());
		}
		catch (Exception ex) {
			rollBackLocallyManagedConnection(conn);
			logger.error("Failed to delete generic category " + categoryID, ex);
			throw new ServletActionException("ServerError", "Failed to delete the generic category from DB: " + ex.getMessage());
		}
		finally {
			dbconnectionmanager.freeConnection(conn);
		}
	}

	public void deleteGenericEntity(GenericEntityType type, int entityID, User user) throws ServletActionException {
		logger.info(">>> deleteGenericEntity: " + entityID + " of type " + type + "," + user);
		try {
			GenericEntity oldEntity = EntityManager.getInstance().getEntity(type, entityID);

			// TODO Kim, 2006-12-06: The following three lines should be execuded in a single DB transaction
			//                       This requires some changes to service provider API. 
			//                       This ought to be done when we replace data access layer with a 3rd-party ORM tool
			ServiceProviderFactory.getPEDataUpdater().getGenericEntityDataUpdater().deleteGenericEntity(entityID, type.getID());
			ServiceProviderFactory.getPEDataUpdater().getGenericEntityDataUpdater().deleteAllEntityCompatibility(type.getID(), entityID);

			GridActionCoordinator.getInstance().removeGenericEntityFromContextAndEntityListColumns(type, entityID);

			// remove entity from all cache
			EntityManager.getInstance().removeEntity(type, entityID);

			AuditLogger.getInstance().logDelete(oldEntity, user.getUserID());
		}
		catch (ServletActionException ex) {
			throw ex;
		}
		catch (Exception ex) {
			throw new ServletActionException("ServerError", "Failed to delete the generic entity from DB: " + ex.getMessage());
		}
	}

	public void deleteAllEntityCompatibility(GenericEntityType type, int entityID) throws ServletActionException {
		try {
			ServiceProviderFactory.getPEDataUpdater().getGenericEntityDataUpdater().deleteAllEntityCompatibility(type.getID(), entityID);
			EntityManager.getInstance().removeAllEntityCompatibility(type, entityID);
		}
		catch (Exception ex) {
			throw new ServletActionException("ServerError", "Failed to delete the generic entity from DB: " + ex.getMessage());
		}
	}

	public void deleteTemplate(int templateID, boolean deleteGuidelines, User user) throws ServletActionException {
		// first delete template
		GridTemplate templateFromCache = deleteGuidelineTemplate(templateID);
		if (deleteGuidelines) {
			deleteGuidelinesForTemplate(templateID, user);
		}
		AuditLogger.getInstance().logDeleteTemplate(templateFromCache, deleteGuidelines, user.getUserID());
	}

	private void deleteGuidelinesForTemplate(int templateID, User user) throws ServletActionException {
		logger.debug(">>> deleteGuidelinesForTemplate: " + templateID + ", user=" + user);
		try {

			Connection conn = null;
			try {
				conn = DBConnectionManager.getInstance().getConnection();
				conn.setAutoCommit(false);
				new GridUpdater(conn).deleteGridsForTemplate(templateID);
				conn.commit();

				logger.debug("deleteGuidelinesForTemplate: removing from cache");

				GridManager.getInstance().removeGuidelinesForTemplate(templateID);
				logger.debug("<<< deleteGuidelinesForTemplate");
			}
			catch (Exception ex) {
				if (conn != null) conn.rollback();
				logger.error("Failed to delete guideliens for template: " + templateID, ex);
				throw new ServletActionException("ServerError", "Failed to delete guidelines for the template from the DB: "
						+ ex.getMessage());
			}
			finally {
				DBConnectionManager.getInstance().freeConnection(conn);
			}
		}
		catch (SQLException ex) {
			logger.error("Failed to delete guidelines for template " + templateID, ex);
			throw new ServletActionException("ServerError", ex.getMessage());
		}

	}

	public void deleteEntity(EntityType entityType, int entityID, User user) throws ServletActionException {
		if (entityType == EntityType.ROLE) {
			deleteRole(entityID, user);
		}
		else if (entityType == EntityType.PARAMETER_GRID) {
			deleteParameter(entityID, user);
		}
		else if (entityType == EntityType.PROCESS_REQUEST) {
			deleteProcessRequest(entityID, user);
		}
		else if (entityType == EntityType.PROCESS_PHASE) {
			deleteProcessPhase(entityID, user);
		}
		else if (entityType == EntityType.GUIDELINE_ACTION) {
			deleteActionTypeDefinition(entityID, user);
		}
		else if (entityType == EntityType.GUIDELINE_TEST_CONDITION) {
			deleteTestTypeDefinition(entityID, user);
		}
		else if (entityType == EntityType.CBR_CASE_BASE) {
			deleteCBRCaseBase(entityID, user);
		}
		else if (entityType == EntityType.CBR_ATTRIBUTE) {
			deleteCBRAttribute(entityID, user);
		}
		else if (entityType == EntityType.CBR_CASE) {
			deleteCBRCase(entityID, user);
		}
		else if (entityType == EntityType.DATE_SYNONYM) {
			deleteDateSynonym(entityID, user);
		}
		else {
			throw new IllegalArgumentException("Unsupported delete entity " + entityType);
		}
	}

	public void deleteEntity(EntityType entityType, String name, User user) throws ServletActionException {
		if (entityType == EntityType.USER_DATA) {
			deleteUser(name, user);
		}
		else {
			throw new IllegalArgumentException("Unsupported delete entity " + entityType);
		}
	}

	public void lockGenericEntity(GenericEntityType entityType, int entityID, User user) throws ServletActionException {
		try {
			LockManager.getInstance().lock(entityType, entityID, user);
		}
		catch (LockException ex) {
			throw new ServletActionException("LockFailureMsg", ex.getLockedBy());
		}
	}

	public void unlockGenericEntity(GenericEntityType entityType, int entityID, User user) throws ServletActionException {
		try {
			LockManager.getInstance().unlock(entityType, entityID, user);
		}
		catch (LockException ex) {
			throw new ServletActionException("LockFailureMsg", ex.getLockedBy());
		}
	}

	public void lockEntity(EntityType entityType, int entityID, User user) throws ServletActionException {
		try {
			LockManager.getInstance().lock(entityType, entityID, user);
		}
		catch (LockException ex) {
			throw new ServletActionException("LockFailureMsg", ex.getLockedBy());
		}
	}

	public void unlockEntity(EntityType entityType, int entityID, User user) throws ServletActionException {
		try {
			LockManager.getInstance().unlock(entityType, entityID, user);
		}
		catch (LockException ex) {
			throw new ServletActionException("LockFailureMsg", ex.getLockedBy());
		}
	}

	public void lockEntity(EntityType entityType, String name, User user) throws ServletActionException {
		try {
			LockManager.getInstance().lock(entityType, name, user);
		}
		catch (LockException ex) {
			throw new ServletActionException("LockFailureMsg", ex.getLockedBy());
		}
	}

	public void unlockEntity(EntityType entityType, String name, User user) throws ServletActionException {
		try {
			LockManager.getInstance().unlock(entityType, name, user);
		}
		catch (LockException ex) {
			throw new ServletActionException("LockFailureMsg", ex.getLockedBy());
		}
	}

	public void importNextID(NextIDSeed nextIDSeed) throws ImportException, DataValidationFailedException {
		logger.debug(">>> importNextID(NextIDSeed) with " + nextIDSeed);
		validateData(nextIDSeed);
		try {
			DBIdGenerator.getInstance().setNextID(nextIDSeed.getType(), nextIDSeed.getSeed(), nextIDSeed.getCache());
		}
		catch (SQLException ex) {

			logger.error("Failed to import next-id " + nextIDSeed, ex);
			throw new ImportException("failed to updated DB: " + ex.getLocalizedMessage());
		}
	}

	public boolean hasDeployRule(int templateID, int columnID) {
		GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(templateID);
		if (template == null) return false;
		if (columnID < 0) return template.getRuleDefinition() != null;
		GridTemplateColumn column = (GridTemplateColumn) template.getColumn(columnID);
		if (column == null) return false;
		return column.getRuleDefinition() != null;
	}

	public List<ParameterGrid> fetchParameterGrids(int templateID) {
		List<ParameterGrid> list = ParameterManager.getInstance().getGrids(templateID);
		return (list == null ? new ArrayList<ParameterGrid>() : list);
	}

	public synchronized ImportResult importData(ImportSpec importSpec, User user) throws ImportException, IOException {
		logger.info("Importing data for " + importSpec);
		ImportService importService = new ImportService();
		importService.importDataXML(importSpec, user);
		ImportResult result = importService.getImportResult();
		try {
			Loader.loadToCache(true);
		}
		catch (ServerException e) {
			result.addErrorMessage(e.getMessage(), "Server Cache Reload");
		}

		logger.info("<<< importData: " + result);
		return result;
	}

	public synchronized ImportResult importAdHocActionData(String content, User user) throws ImportException, IOException {
		logger.info("Importing Actions...");
		logger.info(content);
		StringReader in = new StringReader(content);
		ImportService importService = new ImportService();
		String message = importService.importAdHocActionsXML(in, user);

		ImportResult result = importService.getImportResult();
		result.addMessage(message, "AdHocActions");
		return result;
	}

	/**
	 * Imports the specified ad-hoc rules and rule sets.
	 * 
	 * @param content1
	 *            content of ad-hoc rule definition file
	 * @param content2
	 *            content of ad-hoc ruleset file
	 * @param user
	 * @return import result
	 * @throws ImportException
	 *             on error
	 */
	public synchronized ImportResult importAdHocRuleData(String content1, String content2, User user) throws ImportException, IOException {
		logger.info("Importing Ad Hoc Rules...");
		long time = System.currentTimeMillis();
		ImportService importService = new ImportService();
		String message = importService.importAdHocRulesXML(content1, content2, user);

		ImportResult result = importService.getImportResult();
		result.addMessage(message, "AdHocRuleData");
		result.setTemplateImported(true);
		result.setElapsedTime(System.currentTimeMillis() - time);
		return result;
	}

	public List<String> getCustomReportNames() throws IOException, ServerException {
		File reportDir = new File(ConfigurationManager.getInstance().getServerBasePath(), ReportGenerator.getReportFileBaseDir());
		if (reportDir.isDirectory()) {
			File[] files = reportDir.listFiles(new FileFilter() {

				public boolean accept(File path) {
					if (path == null) return false;
					return path.isFile() && path.getName().toUpperCase().endsWith(".RPT")
							&& !path.getName().toUpperCase().startsWith("POLICY-SUMMARY-REPORT");
				}
			});
			List<String> nameList = new ArrayList<String>();
			for (int i = 0; i < files.length; i++) {
				nameList.add(files[i].getName());
			}
			return nameList;
		}
		else {
			throw new ServerException("report dir not found: " + reportDir.getAbsolutePath());
		}

	}

	public String generateReportURL(HttpServletRequest request, AbstractReportSpec reportSpec, List<GuidelineReportData> guidelineList)
			throws ServerException {
		logger.debug(">>> generateGuidelineReports");

		if (reportSpec instanceof CustomReportSpec) {
			return Util.extractServerBasePath(request)
					+ ReportGenerator.getCustomReportURL(((CustomReportSpec) reportSpec).getReportFilename());
		}
		else if (reportSpec instanceof GuidelineReportSpec) {
			long reportID = ReportSpecManager.getInstance().cacheNextReportSpec((GuidelineReportSpec) reportSpec, guidelineList);
			logger.debug("... generateReportURL: reportID=" + reportID);
			return Util.extractServerBasePath(request) + ReportGenerator.getPolicySummaryReportURL(reportID);
		}
		else {
			throw new ServerException("Invalid report spec type: " + reportSpec.getClass());
		}
	}

	public synchronized byte[] generateGuidelineReports(AbstractReportSpec reportSpec, List<GuidelineReportData> guidelineList)
			throws IOException, ServerException {
		logger.debug(">>> generateGuidelineReports: " + reportSpec);
		StringWriter writer = new StringWriter();
		ReportGenerator reportGenerator = new ReportGenerator(writer);
		reportGenerator.generateGuidelineReport((GuidelineReportSpec) reportSpec, guidelineList);
		// make it available for garbage collection immediately
		reportGenerator = null;

		// get the file content
		BufferedReader reader = new BufferedReader(new StringReader(writer.toString()));

		ByteArrayOutputStream baOut = new ByteArrayOutputStream();
		BufferedOutputStream out = new BufferedOutputStream(new GZIPOutputStream(baOut));
		int c;
		while ((c = reader.read()) != -1) {
			out.write(c);
		}
		reader.close();
		out.close();

		byte[] ba = baOut.toByteArray();
		logger.debug("<<< generateGuidelineReports: " + (ba == null ? 0 : ba.length));
		return ba;
	}

	public synchronized byte[] fetchExportXML(GuidelineReportFilter filter, String userID) throws IOException, ExportException {
		logger.debug(">>> fetchExportXML");

		StringWriter writer = new StringWriter();
		ExportService.getInstance().export(writer, filter, userID);

		// get the file content
		BufferedReader reader = new BufferedReader(new StringReader(writer.toString()));

		ByteArrayOutputStream baOut = new ByteArrayOutputStream();
		BufferedOutputStream out = new BufferedOutputStream(new GZIPOutputStream(baOut));
		int c;
		while ((c = reader.read()) != -1) {
			out.write(c);
		}
		reader.close();
		out.close();
		byte[] ba = baOut.toByteArray();
		logger.debug("<<< fetchExportXML: " + ba.length);
		return ba;
	}

	public void writeExportXML(GuidelineReportFilter filter, String fileName, String userID) throws IOException, ExportException {
		byte[] ba = fetchExportXML(filter, userID);
		if (ba == null || ba.length == 0) {
			throw new ExportException("Failed to export entities to Server");
		}
		else {
			File file = new File(fileName);
			PrintWriter writer = null;
			BufferedReader reader = null;
			try {
				FileWriter fw = new FileWriter(file, false);
				BufferedWriter bw = new BufferedWriter(fw);
				writer = new PrintWriter(bw, true);
				reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(ba))));
				for (String line = reader.readLine(); line != null; line = reader.readLine()) {
					writer.println(line);
				}
				writer.flush();
				writer.close();
				writer = null;
			}
			finally {
				if (writer != null) writer.close();
				if (reader != null) reader.close();
			}
		}
	}

	public byte[] fetchDomainDefinitionXML() throws IOException {
		logger.debug(">>> fetchDomainDefinitionXML");

		ByteArrayOutputStream baOut = new ByteArrayOutputStream();

		ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(baOut));
		oos.writeObject((DomainClass[]) DomainManager.getInstance().getAllDomainClasses().toArray(new DomainClass[0]));
		oos.close();

		byte[] ba = baOut.toByteArray();
		logger.debug("<<< fetchDomainDefinitionXML: " + ba.length);
		return ba;
	}

	public ServerStats gatherServerStats() {
		Package serverPackage = Package.getPackage("com.mindbox.pe.server");

		ServerStats stats = new ServerStats();
		stats.setVersion(serverPackage.getImplementationVersion());
		stats.setStartedDate(DateFormat.getDateTimeInstance().format(serverStartDate));
		stats.setUserCount(SessionManager.getInstance().countSessions());
		return stats;
	}

	public int importDateSynonym(DateSynonym dateSynonym, User user) throws ImportException, DataValidationFailedException {
		logger.debug(">>> importDateSynonym(...) with " + dateSynonym);
		validateData(dateSynonym);
		int synonymID = -1;
		try {
			DateSynonymUpdater updater = new DateSynonymUpdater();
			synonymID = DBIdGenerator.getInstance().nextSequentialID();
			logger.debug("inserting new date Synonym = " + synonymID);
			updater.insertDateSynonym(synonymID, dateSynonym.getName(), dateSynonym.getDescription(), dateSynonym.getDate(), true);
			dateSynonym.setID(synonymID);
			logger.debug("    inserted into DB. inserting into the cache");
			DateSynonymManager.getInstance().insert(dateSynonym);
		}
		catch (SQLException ex) {
			logger.error("Failed to import date synonym " + dateSynonym, ex);
			throw new ImportException("failed to update DB: " + ex.getLocalizedMessage());
		}
		catch (SapphireException ex) {
			logger.error("Failed to import " + dateSynonym, ex);
			throw new ImportException("Failed to import " + dateSynonym + ": " + ex.getMessage());
		}
		return synonymID;
	}

	public int importDateSynonym(DateSynonym dateSynonym, boolean merge, User user) throws ImportException, DataValidationFailedException {
		logger.debug(">>> importDateSynonym(...) with " + dateSynonym);
		validateData(dateSynonym);
		int synonymID = dateSynonym.getID();
		try {
			DateSynonymUpdater updater = new DateSynonymUpdater();
			if (!merge && DateSynonymManager.getInstance().getDateSynonym(synonymID) != null) {
				lockEntity(EntityType.DATE_SYNONYM, synonymID, user);
				updater.updateDateSynonym(synonymID, dateSynonym.getName(), dateSynonym.getDescription(), dateSynonym.getDate(), true);
				DateSynonymManager.getInstance().update(dateSynonym);
				unlockEntity(EntityType.DATE_SYNONYM, synonymID, user);
			}
			else {
				synonymID = (merge ? DBIdGenerator.getInstance().nextSequentialID() : dateSynonym.getID());
				logger.debug("inserting new date Synonym = " + synonymID);
				updater.insertDateSynonym(synonymID, dateSynonym.getName(), dateSynonym.getDescription(), dateSynonym.getDate(), true);
				dateSynonym.setID(synonymID);
				logger.debug("    inserted into DB. inserting into the cache");
				DateSynonymManager.getInstance().insert(dateSynonym);
			}
		}
		catch (SQLException ex) {
			logger.error("Failed to import date synonym " + dateSynonym, ex);
			throw new ImportException("failed to update DB: " + ex.getLocalizedMessage());
		}
		catch (SapphireException ex) {
			logger.error("Failed to import " + dateSynonym, ex);
			throw new ImportException("Failed to import " + dateSynonym + ": " + ex.getMessage());
		}
		return synonymID;
	}

	public boolean isDateSynonymInUse(DateSynonym dateSynonym) {
		return GridManager.getInstance().isInUse(dateSynonym) || ParameterManager.getInstance().isInUse(dateSynonym)
				|| EntityManager.getInstance().isInUse(dateSynonym);
	}

	public void replaceDateSynonyms(DateSynonym[] toBeReplaced, DateSynonym replacement, User user) throws ServletActionException,
			ServerException {
		Connection conn = null;
		try {
			conn = getLocallyManagedConnection();

			create(replacement, user, conn);

			DateSynonymReferenceUpdater updater = new GridUpdater(conn);
			updater.replaceDateSynonymReferences(toBeReplaced, replacement);

			updater = new ParameterUpdater(conn);
			updater.replaceDateSynonymReferences(toBeReplaced, replacement);

			updater = new CBRCaseUpdater(conn);
			updater.replaceDateSynonymReferences(toBeReplaced, replacement);

			updater = new CBRCaseBaseUpdater(conn);
			updater.replaceDateSynonymReferences(toBeReplaced, replacement);

			updater = new GenericEntityUpdater(conn);
			updater.replaceDateSynonymReferences(toBeReplaced, replacement);

			for (int i = 0; i < toBeReplaced.length; i++) {
				deleteDateSynonym(toBeReplaced[i].getID(), user, conn);
			}
		}
		catch (SQLException ex) {
			rollBackLocallyManagedConnection(conn);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
		catch (SapphireException ex) {
			rollBackLocallyManagedConnection(conn);
			throw new ServletActionException("ServerError", ex.getMessage());
		}
		finally {
			freeLocallyManagedConnection(conn);
		}

		Loader.loadToCache(false);
	}
}
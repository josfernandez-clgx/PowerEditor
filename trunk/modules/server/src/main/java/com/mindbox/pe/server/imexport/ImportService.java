package com.mindbox.pe.server.imexport;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.XmlUtil.unmarshal;
import static com.mindbox.pe.server.imexport.ObjectConverter.isRoot;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.communication.ImportResult;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.ImportSpec;
import com.mindbox.pe.model.exceptions.SapphireException;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.bizlogic.DefaultImportBusinessLogic;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.servlet.ResourceUtil;
import com.mindbox.pe.server.servlet.ServletActionException;
import com.mindbox.pe.xsd.config.CategoryType;
import com.mindbox.pe.xsd.config.FeatureNameType;
import com.mindbox.pe.xsd.data.CBRAttributeElement;
import com.mindbox.pe.xsd.data.CBRCaseBaseElement;
import com.mindbox.pe.xsd.data.CBRCaseElement;
import com.mindbox.pe.xsd.data.CBRDataElement;
import com.mindbox.pe.xsd.data.DateDataElement;
import com.mindbox.pe.xsd.data.DateDataElement.DateElement;
import com.mindbox.pe.xsd.data.EntityDataElement;
import com.mindbox.pe.xsd.data.EntityDataElement.Category;
import com.mindbox.pe.xsd.data.FilterDataElement;
import com.mindbox.pe.xsd.data.GridDataElement;
import com.mindbox.pe.xsd.data.GridTypeAttribute;
import com.mindbox.pe.xsd.data.GuidelineActionDataElement;
import com.mindbox.pe.xsd.data.GuidelineActionDataElement.GuidelineAction;
import com.mindbox.pe.xsd.data.MessageDataElement;
import com.mindbox.pe.xsd.data.MessageDataElement.Message;
import com.mindbox.pe.xsd.data.NextIDDataElement;
import com.mindbox.pe.xsd.data.NextIDDataElement.NextId;
import com.mindbox.pe.xsd.data.PowereditorData;
import com.mindbox.pe.xsd.data.PrivilegesElement;
import com.mindbox.pe.xsd.data.PrivilegesElement.Privilege;
import com.mindbox.pe.xsd.data.RolesElement;
import com.mindbox.pe.xsd.data.RolesElement.Role;
import com.mindbox.pe.xsd.data.SecurityDataElement;
import com.mindbox.pe.xsd.data.TemplateDataElement;
import com.mindbox.pe.xsd.data.TemplateDataElement.GuidelineTemplate;
import com.mindbox.pe.xsd.data.TestConditionDataElement;
import com.mindbox.pe.xsd.data.TestConditionDataElement.TestCondition;
import com.mindbox.pe.xsd.data.TypeEnumDataElement;
import com.mindbox.pe.xsd.data.TypeEnumDataElement.TypeEnum;
import com.mindbox.pe.xsd.data.UsersElement;
import com.mindbox.pe.xsd.data.UsersElement.User;

/**
 * Not thread-safe.
 * Do not reuse instances of this. Should discard each after use.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class ImportService implements ReplacementDateSynonymProvider {

	private static final Logger LOG = Logger.getLogger(ImportService.class);
	private static final Date DATE_FOR_REPLACEMENT_DATESYNONYM = new Date(0L);

	private com.mindbox.pe.server.model.User requester = null;

	private ImportResult importResult;
	private DateSynonym replacementDSForImport = null;

	private Date startDate;

	private void addError(Serializable context, String message) {
		importResult.addErrorMessage(message, context);
	}

	private void buildEntityIDMap(final EntityDataElement entityDataElement, Map<String, Integer> entityIDMap) throws ImportException {
		LOG.debug(">>> buildEntityIDMap: " + entityDataElement);
		BizActionCoordinator bizActionCoordinator = BizActionCoordinator.getInstance();
		// process generic categories first
		try {
			int newID = 0;
			String key = null;
			for (final Category categoryDigest : entityDataElement.getCategory()) {
				GenericEntityType entityType = GenericEntityType.forName(categoryDigest.getType());
				if (entityType != null) {
					CategoryType ctd = ConfigurationManager.getInstance().getEntityConfigHelper().getCategoryDefinition(entityType);
					if (ctd == null) throw new ImportException("No category defined for entity type " + categoryDigest.getType());

					newID = isRoot(categoryDigest) ? getRootCategoryId(ctd.getTypeID().intValue()) : bizActionCoordinator.generateNewIDForMerge();
					key = ObjectConverter.asCategoryIDMapKey(categoryDigest.getType(), categoryDigest.getId());
					entityIDMap.put(key, new Integer(newID));
					LOG.debug("    buildEntityIDMap: " + key + " --> " + newID);
				}
			}

			// process entitites
			for (final com.mindbox.pe.xsd.data.EntityDataElement.Entity entity : entityDataElement.getEntity()) {
				// check for hard-coded category type for backward compatibility (pre 5.0)
				if (entity.getType().equals("category")) {
					GenericEntityType productType = GenericEntityType.forName("product");
					if (productType == null) {
						throw new ImportException("No entity of type \"product\" defined. Verify \"product\" entity is defined in the configuration file.");
					}
					else {
						newID = (entity.getParentID() < 0 ? getRootCategoryId(productType.getCategoryType()) : bizActionCoordinator.generateNewIDForMerge());
						key = ObjectConverter.asCategoryIDMapKey("product", entity.getId());
						entityIDMap.put(key, new Integer(newID));
						LOG.debug("    buildEntityIDMap: " + key + " --> " + newID);
					}
				}
				else {
					GenericEntityType type = GenericEntityType.forName(entity.getType());
					if (type != null) {
						newID = bizActionCoordinator.generateNewIDForMerge();
						key = ObjectConverter.asEntityIDMapKey(type.toString(), entity.getId());
						entityIDMap.put(key, new Integer(newID));
						LOG.debug("    buildEntityIDMap: " + key + " --> " + newID);
					}
				}
			}
			LOG.debug("<<< buildEntityIDMap");
		}
		catch (SapphireException ex) {
			LOG.error("Failed to generate new id", ex);
			throw new ImportException("Failed to generate new id for merge: " + ex.getMessage());
		}
	}

	private void buildEntityIDMapForNonMerge(final EntityDataElement entityDataElement, Map<String, Integer> entityIDMap) throws ImportException {
		for (final Category categoryDigest : entityDataElement.getCategory()) {
			GenericEntityType entityType = GenericEntityType.forName(categoryDigest.getType());
			if (entityType != null) {
				CategoryType ctd = ConfigurationManager.getInstance().getEntityConfigHelper().getCategoryDefinition(entityType);
				if (ctd != null && isRoot(categoryDigest)) {
					String key = ObjectConverter.asCategoryIDMapKey(categoryDigest.getType(), categoryDigest.getId());
					entityIDMap.put(key, new Integer(getRootCategoryId(ctd.getTypeID().intValue())));
					LOG.debug("buildEntityIDMapForNonMerge (category)" + key + " --> " + entityIDMap.get(key));
				}
			}
		}
		// Find hard-coded product generic entity type for processing <Entity> elements for category
		GenericEntityType type = GenericEntityType.forName("product");
		if (type != null) {
			CategoryType ctd = ConfigurationManager.getInstance().getEntityConfigHelper().getCategoryDefinition(type);
			if (ctd != null) {
				for (final com.mindbox.pe.xsd.data.EntityDataElement.Entity entity : entityDataElement.getEntity()) {
					if (entity.getType().equals("category") && entity.getParentID() < 0) {
						String key = ObjectConverter.asCategoryIDMapKey(type.toString(), entity.getId());
						entityIDMap.put(key, new Integer(getRootCategoryId(ctd.getTypeID().intValue())));
						LOG.debug("buildEntityIDMapForNonMerge(entity of type category): " + key + " --> " + entityIDMap.get(key));
					}
				}
			}
		}
	}

	private PowereditorData consolidateAndFilterPowereditorData(final Map<String, PowereditorData> dataMap) {
		final PowereditorData powereditorData = new PowereditorData();
		powereditorData.setCbrData(new CBRDataElement());
		powereditorData.setDateData(new DateDataElement());
		powereditorData.setEntityData(new EntityDataElement());
		powereditorData.setFilterData(new FilterDataElement());
		powereditorData.setGridData(new GridDataElement());
		powereditorData.setGuidelineActionData(new GuidelineActionDataElement());
		powereditorData.setMessageData(new MessageDataElement());
		powereditorData.setNextIdData(new NextIDDataElement());
		powereditorData.setSecurityData(new SecurityDataElement());
		powereditorData.setTemplateData(new TemplateDataElement());
		powereditorData.setTestConditionData(new TestConditionDataElement());
		powereditorData.setTypeEnumData(new TypeEnumDataElement());

		if (dataMap != null && !dataMap.isEmpty()) {
			for (Map.Entry<String, PowereditorData> entry : dataMap.entrySet()) {
				final PowereditorData dataFromList = entry.getValue();
				if (!ConfigurationManager.getInstance().isFeatureEnabled(FeatureNameType.CBR)) {
					if (dataFromList.getCbrData() != null
							&& (!dataFromList.getCbrData().getCbrAttribute().isEmpty() || !dataFromList.getCbrData().getCbrCase().isEmpty() || !dataFromList.getCbrData().getCbrCaseBase().isEmpty())) {
						addError(entry.getKey(), "CBR feature is not enabled. CBR data will not be imported!");
					}
				}
				else {
					if (dataFromList.getCbrData() != null) {
						for (final CBRAttributeElement element : dataFromList.getCbrData().getCbrAttribute()) {
							powereditorData.getCbrData().getCbrAttribute().add(element);
						}
						for (final CBRCaseElement element : dataFromList.getCbrData().getCbrCase()) {
							powereditorData.getCbrData().getCbrCase().add(element);
						}
						for (final CBRCaseBaseElement element : dataFromList.getCbrData().getCbrCaseBase()) {
							powereditorData.getCbrData().getCbrCaseBase().add(element);
						}
					}
				}

				if (dataFromList.getGridData() != null) {
					for (com.mindbox.pe.xsd.data.GridDataElement.Grid grid : dataFromList.getGridData().getGrid()) {
						if (grid.getType() == GridTypeAttribute.PARAMETER) {
							if (!ConfigurationManager.getInstance().isFeatureEnabled(FeatureNameType.PARAMETER)) {
								addError(entry.getKey(), "Parameter feature is not enabled. Parameter data will not be imported!");
							}
							else {
								powereditorData.getGridData().getGrid().add(grid);
							}
						}
						else {
							powereditorData.getGridData().getGrid().add(grid);
						}
					}
				}

				if (dataFromList.getTypeEnumData() != null) {
					for (final TypeEnum typeEnum : dataFromList.getTypeEnumData().getTypeEnum()) {
						powereditorData.getTypeEnumData().getTypeEnum().add(typeEnum);
					}
				}
				if (dataFromList.getNextIdData() != null) {
					for (final NextId element : dataFromList.getNextIdData().getNextId()) {
						powereditorData.getNextIdData().getNextId().add(element);
					}
				}
				if (dataFromList.getDateData() != null) {
					for (final DateElement element : dataFromList.getDateData().getDateElement()) {
						powereditorData.getDateData().getDateElement().add(element);
					}
				}
				if (dataFromList.getEntityData() != null) {
					for (final Category element : dataFromList.getEntityData().getCategory()) {
						powereditorData.getEntityData().getCategory().add(element);
					}
					for (final com.mindbox.pe.xsd.data.EntityDataElement.Entity element : dataFromList.getEntityData().getEntity()) {
						powereditorData.getEntityData().getEntity().add(element);
					}
				}
				if (dataFromList.getFilterData() != null) {
					for (final FilterDataElement.Filter element : dataFromList.getFilterData().getFilter()) {
						powereditorData.getFilterData().getFilter().add(element);
					}
				}
				if (dataFromList.getGuidelineActionData() != null) {
					for (final GuidelineAction element : dataFromList.getGuidelineActionData().getGuidelineAction()) {
						powereditorData.getGuidelineActionData().getGuidelineAction().add(element);
					}
				}
				if (dataFromList.getMessageData() != null) {
					for (final Message element : dataFromList.getMessageData().getMessage()) {
						powereditorData.getMessageData().getMessage().add(element);
					}
				}
				if (dataFromList.getSecurityData() != null) {
					if (dataFromList.getSecurityData().getPrivileges() != null) {
						if (powereditorData.getSecurityData().getPrivileges() == null) {
							powereditorData.getSecurityData().setPrivileges(new PrivilegesElement());
						}
						for (final Privilege element : dataFromList.getSecurityData().getPrivileges().getPrivilege()) {
							powereditorData.getSecurityData().getPrivileges().getPrivilege().add(element);
						}
					}
					if (dataFromList.getSecurityData().getRoles() != null) {
						if (powereditorData.getSecurityData().getRoles() == null) {
							powereditorData.getSecurityData().setRoles(new RolesElement());
						}
						for (final Role element : dataFromList.getSecurityData().getRoles().getRole()) {
							powereditorData.getSecurityData().getRoles().getRole().add(element);
						}
					}
					if (dataFromList.getSecurityData().getUsers() != null) {
						if (powereditorData.getSecurityData().getUsers() == null) {
							powereditorData.getSecurityData().setUsers(new UsersElement());
						}
						for (final User element : dataFromList.getSecurityData().getUsers().getUser()) {
							powereditorData.getSecurityData().getUsers().getUser().add(element);
						}
					}
				}
				if (dataFromList.getTemplateData() != null) {
					for (final GuidelineTemplate element : dataFromList.getTemplateData().getGuidelineTemplate()) {
						powereditorData.getTemplateData().getGuidelineTemplate().add(element);
					}
				}
				if (dataFromList.getTestConditionData() != null) {
					for (final TestCondition element : dataFromList.getTestConditionData().getTestCondition()) {
						powereditorData.getTestConditionData().getTestCondition().add(element);
					}
				}
			}
		}
		return powereditorData;
	}

	public synchronized ImportResult getImportResult() {
		return importResult;
	}


	public synchronized DateSynonym getReplacementDateSynonymForImport() throws ImportException {
		String replacementDateName = "Replacement For Unspecified Effective Date For Import";

		if (replacementDSForImport == null) {
			DateSynonym existingDS = DateSynonymManager.getInstance().getDateSynonym(replacementDateName, DATE_FOR_REPLACEMENT_DATESYNONYM);

			if (existingDS != null) {
				replacementDSForImport = existingDS;
			}
			else {
				replacementDSForImport = new DateSynonym(-1, replacementDateName, replacementDateName + " at " + startDate, DATE_FOR_REPLACEMENT_DATESYNONYM);
				try {
					int newID = BizActionCoordinator.getInstance().save(replacementDSForImport, requester);
					replacementDSForImport.setID(newID);

					importResult.addErrorMessage(
							ResourceUtil.getInstance().getResource("msg.info.date.synonym.add.replacement", new Object[] { replacementDSForImport.getName() }),
							"Replacement Data Synonym");
				}
				catch (ServletActionException ex) {
					LOG.error("Failed to create replacement date for unspecified effective date", ex);
					throw new ImportException("Failed to create replacement date for unspecified effective date: " + ex.getMessage());
				}
				catch (DataValidationFailedException ex) {
					LOG.error("Failed to validate new replacement date for unspecified effective date", ex);
					throw new ImportException("Failed to create replacement date for unspecified effective date: " + ex.getMessage());
				}
			}
		}
		return replacementDSForImport;
	}

	private int getRootCategoryId(int categoryTypeId) {
		GenericCategory root = EntityManager.getInstance().getGenericCategoryRoot(categoryTypeId);
		return root == null ? -1 : root.getID();
	}

	/**
	 * Imports PE data specified in the content and returns status message.
	 * 
	 * @param importSpec
	 * @param result
	 * @param user
	 * @throws ImportException
	 *             on error while importing data
	 */
	public synchronized void importDataXML(final ImportSpec importSpec, final boolean entitiesOnly, final com.mindbox.pe.server.model.User user) throws ImportException {
		reset(user);
		logDebug(LOG, "---> importDataXML: %s, user=%s, no.files=%d", importSpec, user, importSpec.getFilenames().length);
		try {
			final Map<String, PowereditorData> powereditorDataList = new HashMap<String, PowereditorData>();
			// parse all import files
			for (final String filename : importSpec.getFilenames()) {
				powereditorDataList.put(filename, unmarshal(new StringReader(importSpec.getContent(filename)), PowereditorData.class));
			}
			logDebug(LOG, "[importDataXML] all files parsed");

			final PowereditorData powereditorDataToUse = consolidateAndFilterPowereditorData(powereditorDataList);
			final ImportBusinessLogic importBusinessLogic = new DefaultImportBusinessLogic(BizActionCoordinator.getInstance());

			// [1] Import Type enum values
			if (powereditorDataToUse.getTypeEnumData() != null) {
				new TypeEnumDataImporter(importBusinessLogic).importData(powereditorDataToUse.getTypeEnumData(), importResult, importSpec.isMerge(), user, "");
			}

			// [2] Import next id seed, next, if necessary
			if (!importSpec.isMerge()) {
				new NextIDSeedImporter(importBusinessLogic).importData(powereditorDataToUse.getNextIdData(), importResult, false, user, null);
				LOG.debug("    importDataXML: next id seeds processed");
			}

			// [3] Import date synonyms
			Map<Integer, Integer> dateSynonymIDMap = new HashMap<Integer, Integer>();
			new DateSynonymImporter(importBusinessLogic).importData(powereditorDataToUse.getDateData(), importResult, importSpec.isMerge(), user, dateSynonymIDMap);
			LOG.debug("    importDataXML: all date synonyms processed");

			// Build id maps before importing entities/categories
			final Map<String, Integer> entityIDMap = new HashMap<String, Integer>();

			// If merge, Build entityID map first for , before processing each entity
			if (importSpec.isMerge()) {
				buildEntityIDMap(powereditorDataToUse.getEntityData(), entityIDMap);
			}
			// If not, build map of root category ids
			else {
				buildEntityIDMapForNonMerge(powereditorDataToUse.getEntityData(), entityIDMap);
			}
			LOG.debug("    importDataXML: entityIDMap size = " + entityIDMap.size());

			// Import entities
			EntityImportOptionalData entityImportOptionalData = new EntityImportOptionalData(this, entityIDMap, dateSynonymIDMap);
			new EntityDataImporter(importBusinessLogic).importData(powereditorDataToUse.getEntityData(), importResult, importSpec.isMerge(), user, entityImportOptionalData);
			LOG.debug("    importDataXML: all entities processed");

			// process compatibility links
			new CompatilibytLinkImporter(importBusinessLogic).importData(powereditorDataToUse.getEntityData(), importResult, importSpec.isMerge(), user, entityImportOptionalData);
			LOG.debug("    importDataXML: all compatibility links processed");

			if (!entitiesOnly) {
				// Import roles & users
				new RoleImporter(importBusinessLogic).importData(powereditorDataToUse.getSecurityData(), importResult, importSpec.isMerge(), user, null);
				new UserImporter(importBusinessLogic).importData(powereditorDataToUse.getSecurityData(), importResult, importSpec.isMerge(), user, null);
				LOG.debug("    importDataXML: all users & roles processed");

				// Import templates
				Map<Integer, Integer> templateIDMap = (importSpec.isMerge() ? new HashMap<Integer, Integer>() : null);
				Map<String, Integer> actionIDMap = (importSpec.isMerge() ? new HashMap<String, Integer>() : null);
				List<Integer> unimportedTemplateIDs = new ArrayList<Integer>();
				TemplateImportOptionalData templateImportOptionalData = new TemplateImportOptionalData(entityImportOptionalData, templateIDMap, actionIDMap, unimportedTemplateIDs);

				// 3.1. import guideline actions and test conditions
				new GuidelineActionImporter(importBusinessLogic).importData(powereditorDataToUse.getGuidelineActionData(), importResult, importSpec.isMerge(), user, templateImportOptionalData);
				new TestConditionImporter(importBusinessLogic).importData(powereditorDataToUse.getTestConditionData(), importResult, importSpec.isMerge(), user, templateImportOptionalData);

				// 3.2. import templates
				new GuidelineTemplateImporter(importBusinessLogic).importData(powereditorDataToUse.getTemplateData(), importResult, importSpec.isMerge(), user, templateImportOptionalData);
				LOG.debug("    importDataXML: all templates processed");

				// Import grids
				new GridImporter(importBusinessLogic).importData(powereditorDataToUse.getGridData(), importResult, importSpec.isMerge(), user, templateImportOptionalData);
				LOG.debug("    importDataXML: all grids processed");

				// Import CBR Data
				CBRImportOptionalData cbrImportOptionalData = new CBRImportOptionalData(new HashMap<String, Integer>(), dateSynonymIDMap);
				new CBRDataImporter(importBusinessLogic).importData(powereditorDataToUse.getCbrData(), importResult, importSpec.isMerge(), user, cbrImportOptionalData);
			}
		}
		catch (ImportException ex) {
			throw ex;
		}
		catch (Exception ex) {
			LOG.error("Import failed", ex);
			throw new ImportException(ex.getMessage());
		}
	}

	/**
	 * Equivalent to <code>importDataXML(importSpec, false, user)</code>.
	 * @param importSpec
	 * @param user
	 * @throws ImportException
	 */
	public synchronized void importDataXML(ImportSpec importSpec, com.mindbox.pe.server.model.User user) throws ImportException {
		importDataXML(importSpec, false, user);
	}

	private synchronized void reset(com.mindbox.pe.server.model.User user) {
		this.requester = user;
		replacementDSForImport = null;
		startDate = new Date();
		importResult = new ImportResult();
	}

}
package com.mindbox.pe.server.imexport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.CategoryTypeDefinition;
import com.mindbox.pe.common.config.FeatureConfiguration;
import com.mindbox.pe.common.digest.DigestedObjectHolder;
import com.mindbox.pe.common.validate.ValidationViolation;
import com.mindbox.pe.communication.ImportResult;
import com.mindbox.pe.model.CBRAttribute;
import com.mindbox.pe.model.CBRAttributeType;
import com.mindbox.pe.model.CBRAttributeValue;
import com.mindbox.pe.model.CBRCase;
import com.mindbox.pe.model.CBRCaseAction;
import com.mindbox.pe.model.CBRCaseBase;
import com.mindbox.pe.model.CBRCaseClass;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.ImportSpec;
import com.mindbox.pe.model.ParameterTemplate;
import com.mindbox.pe.model.exceptions.SapphireException;
import com.mindbox.pe.model.process.Phase;
import com.mindbox.pe.model.process.ProcessRequest;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.bizlogic.DataValidationFailedException;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.TemplateXMLDigester;
import com.mindbox.pe.server.imexport.digest.CategoryDigest;
import com.mindbox.pe.server.imexport.digest.Entity;
import com.mindbox.pe.server.imexport.digest.Grid;
import com.mindbox.pe.server.imexport.digest.ImportXMLDigester;
import com.mindbox.pe.server.imexport.digest.Rule;
import com.mindbox.pe.server.imexport.digest.RuleSet;
import com.mindbox.pe.server.repository.AdHocRuleRepositoryManager;
import com.mindbox.pe.server.repository.RepositoryManageFactory;
import com.mindbox.pe.server.servlet.ResourceUtil;
import com.mindbox.pe.server.servlet.ServletActionException;

/**
 * Not thread-safe.
 * Do not reuse instances of this. Should discard each after use.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class ImportService implements ReplacementDateSynonymProvider {

	private static final Date dateForReplacementDateSynonym = new Date(0L);

	private final Logger logger = Logger.getLogger(getClass());

	private com.mindbox.pe.server.model.User requester = null;

	private ImportResult importResult;
	private DateSynonym replacementDSForImport = null;
	private Date startDate;

	public ImportService() {
	}

	private synchronized void reset(com.mindbox.pe.server.model.User user) {
		this.requester = user;
		replacementDSForImport = null;
		startDate = new Date();
		importResult = new ImportResult();
	}

	public synchronized ImportResult getImportResult() {
		return importResult;
	}

	public synchronized DateSynonym getReplacementDateSynonymForImport() throws ImportException {
		String replacementDateName = "Replacement For Unspecified Effective Date For Import";

		if (replacementDSForImport == null) {
			DateSynonym existingDS = DateSynonymManager.getInstance().getDateSynonym(replacementDateName, dateForReplacementDateSynonym);

			if (existingDS != null) {
				replacementDSForImport = existingDS;
			}
			else {
				replacementDSForImport = new DateSynonym(
						-1,
						replacementDateName,
						replacementDateName + " at " + startDate,
						dateForReplacementDateSynonym);
				try {
					int newID = BizActionCoordinator.getInstance().save(replacementDSForImport, requester);
					replacementDSForImport.setID(newID);

					importResult.addErrorMessage(ResourceUtil.getInstance().getResource(
							"msg.info.date.synonym.add.replacement",
							new Object[] { replacementDSForImport.getName() }), "Replacement Data Synonym");
				}
				catch (ServletActionException ex) {
					logger.error("Failed to create replacement date for unspecified effective date", ex);
					throw new ImportException("Failed to create replacement date for unspecified effective date: " + ex.getMessage());
				}
				catch (DataValidationFailedException ex) {
					logger.error("Failed to validate new replacement date for unspecified effective date", ex);
					throw new ImportException("Failed to create replacement date for unspecified effective date: " + ex.getMessage());
				}
			}
		}
		return replacementDSForImport;
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

	/**
	 * Imports PE data specified in the content and returns status message.
	 * 
	 * @param importSpec
	 * @param result
	 * @param user
	 * @throws ImportException
	 *             on error while importing data
	 */
	public synchronized void importDataXML(ImportSpec importSpec, boolean entitiesOnly, com.mindbox.pe.server.model.User user)
			throws ImportException {
		reset(user);
		logger.debug(">>> importDataXML: " + importSpec + ",user=" + user);
		String[] filenames = importSpec.getFilenames();
		logger.debug("    importDataXML: files.size = " + filenames.length);
		try {
			// first, digest all contents
			Map<String, DigestedObjectHolder> objectHolderMap = new HashMap<String, DigestedObjectHolder>();
			for (int i = 0; i < filenames.length; i++) {
				logger.debug("    importDataXML: digesting " + filenames[i] + "...");
				objectHolderMap.put(filenames[i], ImportXMLDigester.getInstance().digestImportXML(importSpec.getContent(filenames[i])));
			}
			logger.debug("    importDataXML: all files digested");

			// check entities only
			if (entitiesOnly) {
				for (DigestedObjectHolder objectHolder : objectHolderMap.values()) {
					for (Class<?> objClass : objectHolder.getClassKeySet()) {
						if (!objClass.equals(Entity.class) && !objClass.equals(CategoryDigest.class)) {
							throw new ImportException("Importing " + objClass.getSimpleName() + " is not supported!");
						}
					}
				}
			}

			// TT 2131: filter out data that is not enabled
			filterOutDataForDisabledFeatures(objectHolderMap);

			// filter out data that fails validation
			filterOutInvalidData(objectHolderMap);

			// Import next id seed, first, if necessary
			if (!importSpec.isMerge()) {
				new NextIDSeedImporter(BizActionCoordinator.getInstance()).importData(objectHolderMap, importResult, false, user, null);
				logger.debug("    importDataXML: next id seeds processed");
			}

			// second, import date synonyms
			Map<Integer, Integer> dateSynonymIDMap = new HashMap<Integer, Integer>();
			new DateSynonymImporter(BizActionCoordinator.getInstance()).importData(objectHolderMap, importResult, importSpec.isMerge(), user, dateSynonymIDMap);
			logger.debug("    importDataXML: all date synonyms processed");

			// Build id maps before importing entities/categories
			Map<String, Integer> entityIDMap = new HashMap<String, Integer>();

			// If merge, Build entityID map first for , before processing each entity
			if (importSpec.isMerge()) {
				for (int i = 0; i < filenames.length; i++) {
					DigestedObjectHolder objectHolder = objectHolderMap.get(filenames[i]);
					buildEntityIDMap(objectHolder, entityIDMap);
				}
			}
			// If not, build map of root category ids
			else {
				for (int i = 0; i < filenames.length; i++) {
					DigestedObjectHolder objectHolder = objectHolderMap.get(filenames[i]);
					buildEntityIDMapForNonMerge(objectHolder, entityIDMap);
				}
			}
			logger.debug("    importDataXML: entityIDMap size = " + entityIDMap.size());

			// Import entities
			EntityImportOptionalData entityImportOptionalData = new EntityImportOptionalData(this, entityIDMap, dateSynonymIDMap);
			new EntityDataImporter(BizActionCoordinator.getInstance()).importData(objectHolderMap, importResult, importSpec.isMerge(), user, entityImportOptionalData);
			logger.debug("    importDataXML: all entities processed");

			// process compatibility links
			new CompatilibytLinkImporter(BizActionCoordinator.getInstance()).importData(objectHolderMap, importResult, importSpec.isMerge(), user, entityImportOptionalData);
			logger.debug("    importDataXML: all compatibility links processed");

			// Import roles & users
			new RoleImporter(BizActionCoordinator.getInstance()).importData(objectHolderMap, importResult, importSpec.isMerge(), user, null);
			new UserImporter(BizActionCoordinator.getInstance()).importData(objectHolderMap, importResult, importSpec.isMerge(), user, null);
			logger.debug("    importDataXML: all users & roles processed");

			// Import templates
			Map<Integer, Integer> templateIDMap = (importSpec.isMerge() ? new HashMap<Integer, Integer>() : null);
			Map<String, Integer> actionIDMap = (importSpec.isMerge() ? new HashMap<String, Integer>() : null);
			List<Integer> unimportedTemplateIDs = new ArrayList<Integer>();
			TemplateImportOptionalData templateImportOptionalData = new TemplateImportOptionalData(entityImportOptionalData,templateIDMap,actionIDMap,unimportedTemplateIDs);

			// import old templates (v 3.x)
			processVx3OldTemplates(filenames, importSpec, templateIDMap);

			// 3.1. import guideline actions and test conditions
			new GuidelineActionTestConditionImporter(BizActionCoordinator.getInstance()).importData(objectHolderMap, importResult, importSpec.isMerge(), user, templateImportOptionalData);
			
			// 3.2. import templates
			new GuidelineTemplateImporter(BizActionCoordinator.getInstance()).importData(objectHolderMap, importResult, importSpec.isMerge(), user, templateImportOptionalData);
			logger.debug("    importDataXML: all templates processed");

			// Import grids
			new GridImporter(BizActionCoordinator.getInstance()).importData(objectHolderMap, importResult, importSpec.isMerge(), user, templateImportOptionalData);
			logger.debug("    importDataXML: all grids processed");

			// Import CBR Data
			CBRImportOptionalData cbrImportOptionalData = new CBRImportOptionalData(new HashMap<String,Integer>(), dateSynonymIDMap);
			new CBRDataImporter(BizActionCoordinator.getInstance()).importData(objectHolderMap, importResult, importSpec.isMerge(), user, cbrImportOptionalData);
		}
		catch (ImportException ex) {
			throw ex;
		}
		catch (Exception ex) {
			logger.error("Import failed", ex);
			throw new ImportException(ex.getMessage());
		}
	}

	private void processVx3OldTemplates(String[] filenames, ImportSpec importSpec, Map<Integer, Integer> templateIDMap) throws ImportException {
		for (int i = 0; i < filenames.length; i++) {
			int[] counts = processOldTemplatesActions(importSpec.getContent(filenames[i]), importSpec.isMerge(), templateIDMap);
			if (counts[0] > 0) {
				importResult.addMessage("  Imported " + counts[0] + " guideline actions", "File: " + filenames[i]);
			}
			if (counts[1] > 0) {
				importResult.addMessage("  Imported " + counts[1] + " guideline templates", "File: " + filenames[i]);
				importResult.setTemplateImported(true);
			}
		}
	}
	
	private void filterOutDataForDisabledFeatures(Map<String, DigestedObjectHolder> objectHolderMap) {
		List<Class<?>> filteredClasses = new ArrayList<Class<?>>();
		for (String filename : objectHolderMap.keySet()) {
			DigestedObjectHolder objectHolder = objectHolderMap.get(filename);
			// check class keys
			boolean parameterChecked = false;
			boolean cbrChecked = false;
			boolean phaseChecked = false;
			Set<Class<?>> classKeySet = objectHolder.getClassKeySet();
			for (Class<?> clazz : classKeySet) {
				if (!filteredClasses.contains(clazz)) {
					if ((clazz.equals(CBRCase.class) || clazz.equals(CBRCaseBase.class) || clazz.equals(CBRAttribute.class)
							|| clazz.equals(CBRAttributeType.class) || clazz.equals(CBRAttributeValue.class)
							|| clazz.equals(CBRCaseAction.class) || clazz.equals(CBRCaseClass.class))
							&& !ConfigurationManager.getInstance().getFeatureConfiguration().isFeatureEnabled(
									FeatureConfiguration.CBR_FEATURE)) {
						filteredClasses.add(clazz);
						if (!cbrChecked) {
							cbrChecked = true;
							addError(filename, "CBR feature is not enabled. CBR data will not be imported!");
						}
					}
					else if ((Phase.class.isAssignableFrom(clazz) || clazz.equals(ProcessRequest.class))
							&& !ConfigurationManager.getInstance().getFeatureConfiguration().isFeatureEnabled(
									FeatureConfiguration.PHASE_FEATURE)) {
						filteredClasses.add(clazz);
						if (!phaseChecked) {
							phaseChecked = true;
							addError(filename, "Phase feature is not enabled. Phase data will not be imported!");
						}
					}
					else if (clazz.equals(ParameterTemplate.class)
							&& !ConfigurationManager.getInstance().getFeatureConfiguration().isFeatureEnabled(
									FeatureConfiguration.PARAMETER_FEATURE)) {
						filteredClasses.add(clazz);
						if (!parameterChecked) {
							parameterChecked = true;
							addError(filename, "Parameter feature is not enabled. Parameter data will not be imported!");
						}
					}
				}
			}
			// filter out parameter grids
			if (!ConfigurationManager.getInstance().getFeatureConfiguration().isFeatureEnabled(FeatureConfiguration.PARAMETER_FEATURE)) {
				List<Grid> grids = objectHolder.getObjects(Grid.class);
				List<Grid> gridsToRetain = new ArrayList<Grid>();
				for (Iterator<Grid> iter = grids.iterator(); iter.hasNext();) {
					Grid grid = iter.next();
					if (grid.getType().equals(Grid.GRID_TYPE_PARAMETER)) {
						if (!parameterChecked) {
							parameterChecked = true;
							addError(filename, "Parameter feature is not enabled. Parameter data will not be imported!");
						}
					}
					else {
						gridsToRetain.add(grid);
					}
				}
				if (grids.size() != gridsToRetain.size()) {
					objectHolder.removeAll(Grid.class);
					objectHolder.addObjects(gridsToRetain);
				}
			}
		}
		// remove filtered out data
		for (Class<?> filteredClass : filteredClasses) {
			for (DigestedObjectHolder objectHolder : objectHolderMap.values()) {
				objectHolder.removeAll(filteredClass);
			}
		}
	}

	private void filterOutInvalidData(Map<String, DigestedObjectHolder> objectHolderMap) {
		for (String filename : objectHolderMap.keySet()) {
			DigestedObjectHolder objectHolderFromMap = objectHolderMap.get(filename);
			DigestedObjectHolder newHolder = new DigestedObjectHolder();
			for (Class<?> c : objectHolderFromMap.getClassKeySet()) {
				List<?> list = objectHolderFromMap.getObjects(c);
				for (Object obj : list) {
					try {
						BizActionCoordinator.validateData(obj);
						newHolder.addObject(obj);
					}
					catch (DataValidationFailedException ex) {
						addErrors(obj.toString(), ex);
					}
				}
			}
			objectHolderMap.put(filename, newHolder);
		}
	}

	public synchronized String importAdHocActionsXML(Reader reader, com.mindbox.pe.server.model.User user) throws ImportException {
		reset(user);
		try {
			TemplateXMLDigester digester = TemplateXMLDigester.getInstance();
			digester.reset();
			digester.digestTemplateXML(reader);

			List<Object> objectList = digester.getAllObjects();
			logger.debug("... importXML: total digested objects = " + objectList.size());

			// import actions
			int count = 0;
			for (Iterator<Object> iter = objectList.iterator(); iter.hasNext();) {
				Object element = iter.next();
				if (element instanceof ActionTypeDefinition) {
					ActionTypeDefinition actionDef = (ActionTypeDefinition) element;
					try {
						BizActionCoordinator.getInstance().importActionTypeDefinition(actionDef, false, null, user, false);
						++count;
					}
					catch (Exception ex) {
						logger.error("Failed to import ad-hoc action: " + actionDef, ex);
						addError(actionDef, new ImportException(ex.getMessage()));
					}
				}
			}
			return "  Imported " + count + " ad-hoc actions as guideline actions";
		}
		catch (Exception ex) {
			logger.error("Failed to import", ex);
			throw new ImportException(ex.getMessage());
		}
	}

	private int[] processOldTemplatesActions(String content, boolean merge, Map<Integer, Integer> templateIDMap) throws ImportException {
		logger.debug(">>> processOldTemplatesActions: merge=" + merge);
		try {
			// [1] import 3.3 GridTemplates, if any
			TemplateXMLDigester oldDigester = TemplateXMLDigester.getInstance();
			oldDigester.reset();
			oldDigester.digestTemplateXML(content);

			int templateCount = 0;
			int actionCount = 0;

			List<Object> objectList = oldDigester.getAllObjects();
			logger.debug("... processOldTemplatesActions: total digested objects = " + objectList.size());
			for (Iterator<Object> iter = objectList.iterator(); iter.hasNext();) {
				Object element = iter.next();
				if (element instanceof GridTemplate) {
					GridTemplate template = (GridTemplate) element;

					// validate template for TT 729
					try {
						BizActionCoordinator.getInstance().validateTemplateForImport(template, false, merge);

						String version = template.getVersion();

						// set to default version, if not specified
						if (version == null || UtilBase.trim(version).length() == 0) version = GridTemplate.DEFAULT_VERSION;

						// process rules
						actionCount += BizActionCoordinator.getInstance().importTemplatePre42(
								merge,
								templateIDMap,
								template,
								version,
								template.getParentTemplateID(),
								requester);
						++templateCount;
					}
					catch (ImportException ex) {
						logger.error("Failed to import template: " + template, ex);
						addError(template, ex);
					}
					catch (Exception ex) {
						logger.error("Failed to import template: " + template, ex);
						addError(template, new ImportException(ex.getMessage()));
					}
				}
			}
			return new int[] { actionCount, templateCount };
		}
		catch (Exception ex) {
			logger.error("Failed to import", ex);
			throw new ImportException(ex.getMessage());
		}
	}


	/**
	 * Imports the specified ad-hoc rules and rule sets.
	 * 
	 * @param content1
	 *            content of ad-hoc rule definition file
	 * @param content2
	 *            content of ad-hoc ruleset file
	 * @param user
	 * @return message
	 * @throws ImportException
	 *             on error
	 */
	public synchronized String importAdHocRulesXML(String content1, String content2, com.mindbox.pe.server.model.User user)
			throws ImportException {
		reset(user);
		try {
			logger.debug(">>> importAdHocRules for " + user);

			String rulesContent = content1;
			String ruleSetContent = content2;
			if (content2.indexOf("<AdHocRules>") > 0) {
				rulesContent = content2;
				ruleSetContent = content1;
			}
			// 1. store the ad-hoc rule file into a temporary file
			File adhocRuleFile = new File(System.currentTimeMillis() + "-adhocrules.xml");

			logger.debug("... persisting input stream as " + adhocRuleFile.getAbsolutePath());

			StringReader reader = new StringReader(rulesContent);
			persistTemporaryFile(reader, adhocRuleFile);
			reader.close();

			logger.debug("... loading ad-hoc rules...");

			// 2. store rule in temporary cache
			AdHocRuleRepositoryManager respositoryManager = RepositoryManageFactory.getInstance().getAdHocRuleRepositoryManager();
			respositoryManager.initialize(adhocRuleFile);

			Collection<?> allAdHocRules = respositoryManager.getAllRules();

			logger.debug("... " + allAdHocRules.size() + " ad-hoc rules loaded");

			// 3. process rulesets
			DigestedObjectHolder ruleSetHolder = ImportXMLDigester.getInstance().digestImportXML(ruleSetContent);

			List<RuleSet> ruleSetList = ruleSetHolder.getObjects(RuleSet.class);

			logger.debug("... " + ruleSetList.size() + " ad-hoc rulesets loaded");

			// 4. combine and persist ruleset and rule
			int ruleCount = 0;
			int ruleSetCount = 0;
			for (Iterator<RuleSet> iter = ruleSetList.iterator(); iter.hasNext();) {
				RuleSet rulesetDigest = iter.next();
				logger.debug("... processing " + rulesetDigest);
				try {
					com.mindbox.pe.model.rule.RuleSet ruleSetObj = ObjectConverter.asRuleset(rulesetDigest, false, null);
					++ruleSetCount;

					List<Rule> ruleDigestList = rulesetDigest.getRules();
					if (ruleDigestList == null || ruleDigestList.isEmpty()) {
						addError("RuleSet", new ImportException("Skipped the ruleset " + String.valueOf(rulesetDigest.getName()) + ": "
								+ "No rule is found in the ruleset"));
					}
					else {
						for (Iterator<Rule> iterator = ruleDigestList.iterator(); iterator.hasNext();) {
							Rule ruleDigest = iterator.next();
							int ruleID = ruleDigest.getId();
							logger.debug("... processing " + ruleID + " for Ruleset " + rulesetDigest.getId());
							RuleDefinition ruleDef = findRuleDefinition(allAdHocRules, ruleID);
							if (ruleDef == null) {
								addError(ruleDigest, new ImportException("Failed to import ad-hoc rule " + String.valueOf(ruleID) + " - "
										+ "No rule with id of " + ruleID + " found"));
							}
							else {
								logger.debug("... rule-def: " + ruleDef);
								logger.debug("... rule-def-action: " + ruleDef.getRuleAction());
								for (int i = 0; i < ruleDef.sizeOfActionParemeters(); i++) {
									logger.debug("... rule-param-at " + i + " = " + ruleDef.getFunctionParameterAt(i));
								}

								try {
									BizActionCoordinator.getInstance().importAdhocRule(ruleDef, ruleSetObj, user);
									++ruleCount;
								}
								catch (Exception ex) {
									logger.error("Failed to import adhoc rule " + ruleID, ex);
									addError(ruleDef, new ImportException("Failed to import ad-hoc rule " + String.valueOf(ruleDef.getID())
											+ " - " + ex.getMessage()));
								}
							}
						}
					}
				}
				catch (Exception ex) {
					logger.error("Failed to import adhoc ruleset " + rulesetDigest.getId(), ex);
					addError("RuleSet", new ImportException("Failed to import ad-hoc ruleset " + String.valueOf(rulesetDigest.getName())
							+ " - " + ex.getMessage()));
				}
			}
			return "  imported " + ruleCount + " ad-hoc rule(s) with " + ruleSetCount + " ruleset(s). ";
		}
		catch (Exception ex) {
			throw new ImportException(ex.getMessage());
		}
	}

	private void addErrors(Object context, DataValidationFailedException ex) {
		for (ValidationViolation violation : ex.getViolations()) {
			addError(context, toErrorString(violation));
		}
	}

	private static String toErrorString(ValidationViolation violation) {
		StringBuilder buff = new StringBuilder();
		buff.append(violation.getMessage());
		if (violation.getCauses() != null && !violation.getCauses().isEmpty()) {
			buff.append(". Cause: ");
			for (ValidationViolation cause : violation.getCauses()) {
				if (cause != null) {
					buff.append(toErrorString(cause));
					buff.append("; ");
				}
			}
		}
		return buff.toString();
	}

	private void addError(Object context, ImportException ex) {
		addError(context, ex.getMessage());
	}

	private void addError(Object context, String message) {
		importResult.addErrorMessage(message, context);
	}

	private void buildEntityIDMapForNonMerge(DigestedObjectHolder objectHolder, Map<String, Integer> entityIDMap) throws ImportException {
		List<CategoryDigest> list = objectHolder.getObjects(CategoryDigest.class);
		for (int i = 0; i < list.size(); i++) {
			CategoryDigest categoryDigest = list.get(i);
			GenericEntityType entityType = GenericEntityType.forName(categoryDigest.getType());
			if (entityType != null) {
				CategoryTypeDefinition ctd = ConfigurationManager.getInstance().getEntityConfiguration().getCategoryDefinition(entityType);
				if (ctd != null && categoryDigest.isRoot()) {
					String key = ObjectConverter.asCategoryIDMapKey(categoryDigest.getType(), categoryDigest.getId());
					entityIDMap.put(key, new Integer(getRootCategoryId(ctd.getTypeID())));
					logger.debug("buildEntityIDMapForNonMerge (category)" + key + " --> " + entityIDMap.get(key));
				}
			}
		}
		// Find hard-coded product generic entity type for processing <Entity> elements for category
		GenericEntityType type = GenericEntityType.forName("product");
		if (type != null) {
			CategoryTypeDefinition ctd = ConfigurationManager.getInstance().getEntityConfiguration().getCategoryDefinition(type);
			if (ctd != null) {
				List<Entity> entityList = objectHolder.getObjects(Entity.class);
				for (int i = 0; i < entityList.size(); i++) {
					Entity entity = entityList.get(i);
					if (entity.getType().equals("category") && entity.getParentID() < 0) {
						String key = ObjectConverter.asCategoryIDMapKey(type.toString(), entity.getId());
						entityIDMap.put(key, new Integer(getRootCategoryId(ctd.getTypeID())));
						logger.debug("buildEntityIDMapForNonMerge(entity of type category): " + key + " --> " + entityIDMap.get(key));
					}
				}
			}
		}
	}

	private void buildEntityIDMap(DigestedObjectHolder objectHolder, Map<String, Integer> entityIDMap) throws ImportException {
		logger.debug(">>> buildEntityIDMap: " + objectHolder);
		BizActionCoordinator bizActionCoordinator = BizActionCoordinator.getInstance();
		// process generic categories first
		try {
			List<CategoryDigest> list = objectHolder.getObjects(CategoryDigest.class);
			int newID = 0;
			String key = null;
			for (int i = 0; i < list.size(); i++) {
				CategoryDigest categoryDigest = list.get(i);
				GenericEntityType entityType = GenericEntityType.forName(categoryDigest.getType());
				if (entityType != null) {
					CategoryTypeDefinition ctd = ConfigurationManager.getInstance().getEntityConfiguration().getCategoryDefinition(
							entityType);
					if (ctd == null) throw new ImportException("No category defined for entity type " + categoryDigest.getType());

					newID = categoryDigest.isRoot() ? getRootCategoryId(ctd.getTypeID()) : bizActionCoordinator.generateNewIDForMerge();
					key = ObjectConverter.asCategoryIDMapKey(categoryDigest.getType(), categoryDigest.getId());
					entityIDMap.put(key, new Integer(newID));
					logger.debug("    buildEntityIDMap: " + key + " --> " + newID);
				}
			}

			// process entitites
			List<Entity> entityList = objectHolder.getObjects(Entity.class);
			for (int i = 0; i < entityList.size(); i++) {
				Entity entity = entityList.get(i);
				// check for hard-coded category type for backward compatibility (pre 5.0)
				if (entity.getType().equals("category")) {
					GenericEntityType productType = GenericEntityType.forName("product");
					if (productType == null) {
						throw new ImportException(
								"No entity of type \"product\" defined. Verify \"product\" entity is defined in the configuration file.");
					}
					else {
						newID = (entity.getParentID() < 0
								? getRootCategoryId(productType.getCategoryType())
								: bizActionCoordinator.generateNewIDForMerge());
						key = ObjectConverter.asCategoryIDMapKey("product", entity.getId());
						entityIDMap.put(key, new Integer(newID));
						logger.debug("    buildEntityIDMap: " + key + " --> " + newID);
					}
				}
				else {
					GenericEntityType type = GenericEntityType.forName(entity.getType());
					if (type != null) {
						newID = bizActionCoordinator.generateNewIDForMerge();
						key = ObjectConverter.asEntityIDMapKey(type.toString(), entity.getId());
						entityIDMap.put(key, new Integer(newID));
						logger.debug("    buildEntityIDMap: " + key + " --> " + newID);
					}
				}
			}
			logger.debug("<<< buildEntityIDMap");
		}
		catch (SapphireException ex) {
			logger.error("Failed to generate new id", ex);
			throw new ImportException("Failed to generate new id for merge: " + ex.getMessage());
		}
	}

	private int getRootCategoryId(int categoryTypeId) {
		GenericCategory root = EntityManager.getInstance().getGenericCategoryRoot(categoryTypeId);
		return root == null ? -1 : root.getID();
	}


	private void persistTemporaryFile(Reader reader, File fileToSave) throws IOException {
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileToSave)));

		for (int c = reader.read(); c != -1; c = reader.read()) {
			writer.print((char) c);
		}
		writer.println("<!-- Temporary adhoc rule file: filename =  " + fileToSave.getAbsolutePath() + " -->");
		writer.close();
		reader.close();
	}

	private static RuleDefinition findRuleDefinition(Collection<?> ruleList, int ruleID) {
		for (Iterator<?> iter = ruleList.iterator(); iter.hasNext();) {
			RuleDefinition element = (RuleDefinition) iter.next();
			if (element.getId() == ruleID) {
				return element;
			}
		}
		return null;
	}

}
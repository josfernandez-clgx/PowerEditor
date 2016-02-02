package com.mindbox.pe.server.bizlogic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.AbstractGrid;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.ParameterTemplate;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.admin.UserData;
import com.mindbox.pe.model.filter.AbstractGenericCategorySearchFilter;
import com.mindbox.pe.model.filter.CloneGenericEntityFilter;
import com.mindbox.pe.model.filter.GenericEntityByCategoryFilter;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.filter.SearchFilter;
import com.mindbox.pe.model.filter.TemplateFilter;
import com.mindbox.pe.server.ServerContextUtil;
import com.mindbox.pe.server.ServerException;
import com.mindbox.pe.server.cache.CBRManager;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.ParameterManager;
import com.mindbox.pe.server.cache.ParameterTemplateManager;
import com.mindbox.pe.server.cache.ProcessManager;
import com.mindbox.pe.server.cache.SecurityCacheManager;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.model.GenericEntityIdentity;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ResourceUtil;

/**
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.10.0
 */
public class SearchCooridinator {

	private static final SearchCooridinator INSTANCE = new SearchCooridinator();

	public static SearchCooridinator getInstance() {
		return INSTANCE;
	}

	private static GuidelineReportFilter.ServerFilterHelper serverFilterHelper = null;

	public static GuidelineReportFilter.ServerFilterHelper getServerFilterHelper() {
		if (serverFilterHelper == null) {
			serverFilterHelper = new GuidelineReportFilterServerHelper();
		}
		return serverFilterHelper;
	}

	private static class GuidelineReportFilterServerHelper implements GuidelineReportFilter.ServerFilterHelper {

		@Override
		public List<Integer> findMatchingGridRows(GuidelineContext[] contexts, AbstractGrid<?> grid, boolean includeParents,
				boolean includeChildren, boolean includeEmptyContexts) {
			return ServerContextUtil.findMatchingGridRows(contexts, grid, includeParents, includeChildren, includeEmptyContexts);
		}

		@Override
		public int getGridID(int templateID, GuidelineContext[] context, DateSynonym effDate, DateSynonym expDate) {
			return GridManager.getInstance().getGridID(templateID, context, effDate, expDate);
		}

		@Override
		public ProductGrid getProductGrid(int gridID) {
			return GridManager.getInstance().getProductGrid(gridID);
		}

		@Override
		public String getResource(String key) {
			return ResourceUtil.getInstance().getResource(key);
		}

		@Override
		public boolean isParentContext(GuidelineContext[] parentContexts, GuidelineContext[] childContexts, boolean includeEmptyContexts) {
			return ServerContextUtil.isParentContext(parentContexts, childContexts, includeEmptyContexts);
		}

		@Override
		public boolean inStatus(String status, String targetStatus) {
			return RuleGeneratorHelper.inStatus(status, targetStatus);
		}
	}

	private final Logger logger = Logger.getLogger(SearchCooridinator.class);

	/**
	 * 
	 */
	private SearchCooridinator() {
		super();
	}

	@SuppressWarnings("unchecked")
	public <T extends Persistent> List<T> process(SearchFilter<T> searchFilter, String userID) throws ServerException {
		logger.debug(">>> process with " + searchFilter + " for " + userID);

		EntityType entityType = searchFilter.getEntityType();
		GenericEntityType genericEntityType = searchFilter.getGenericEntityType();

		logger.debug("        entityType = " + entityType);
		logger.debug("generic entityType = " + genericEntityType);

		Iterator<?> iter = null;
		if (searchFilter instanceof AbstractGenericCategorySearchFilter) {
			iter = EntityManager.getInstance().getAllCategories(((AbstractGenericCategorySearchFilter) searchFilter).getCategoryType()).iterator();
		}
		else if (entityType == null) {
			if (searchFilter instanceof GenericEntityByCategoryFilter) {
				GenericEntityByCategoryFilter filter = (GenericEntityByCategoryFilter) searchFilter;
				int[] categoryIDs = filter.getCategoryIDs();
				if (categoryIDs == null || categoryIDs.length == 0) {
					iter = new ArrayList<T>().iterator();
				}
				else {
					logger.debug("process: GenericEntityByCategoryFilter: categoryIDs.size = " + categoryIDs.length);
					GenericEntityIdentity[] identities;
					if (filter.getDate() == null) {
						identities = EntityManager.getInstance().getGenericEntitiesInCategorySetAtAnyTime(
								genericEntityType.getCategoryType(),
								categoryIDs,
								((GenericEntityByCategoryFilter) searchFilter).includeDescendents());
					}
					else {
						if (filter.includeDescendents()) {
							identities = EntityManager.getInstance().getGenericEntitiesInCategorySetAsOfWithDescendents(
									genericEntityType.getCategoryType(),
									categoryIDs,
									filter.getDate(),
									false); // false to indicate that we care about dates on entity-category associations
						}
						else {
							identities = EntityManager.getInstance().getGenericEntitiesInCategorySetAsOfWithOutDescendents(
									genericEntityType.getCategoryType(),
									categoryIDs,
									filter.getDate());
						}
					}
					if (identities == null) {
						iter = new ArrayList<T>().iterator();
					}
					else {
						logger.debug("process: GenericEntityByCategoryFilter: entities found = " + identities.length);
						List<GenericEntity> geList = new ArrayList<GenericEntity>();
						for (int i = 0; i < identities.length; i++) {
							geList.add(EntityManager.getInstance().getEntity(genericEntityType, identities[i].getEntityID()));
						}
						iter = geList.iterator();
					}
				}
			}
			else if (searchFilter instanceof CloneGenericEntityFilter) {
				iter = EntityManager.getInstance().getDescendents(
						genericEntityType,
						((CloneGenericEntityFilter) searchFilter).getParentID(),
						((CloneGenericEntityFilter) searchFilter).isForAllDescendents()).iterator();
			}
			else {
				iter = EntityManager.getInstance().getAllEntities(genericEntityType).iterator();
			}
		}
		else {
			if (entityType == EntityType.PRIVILEGE) {
				iter = SecurityCacheManager.getInstance().getPrivileges();
			}
			else if (entityType == EntityType.ROLE) {
				iter = SecurityCacheManager.getInstance().getRoles();
			}
			else if (entityType == EntityType.TEMPLATE) {
				List<GridTemplate> templateList = new ArrayList<GridTemplate>();
				boolean skipSecurityCheck = false;
				if (searchFilter instanceof TemplateFilter) {
					templateList = GuidelineTemplateManager.getInstance().searchTemplates((TemplateFilter) searchFilter);
					skipSecurityCheck = ((TemplateFilter) searchFilter).isSkipSecurityCheck();
				}
				else {
					templateList = GuidelineTemplateManager.getInstance().getAllTemplates();
				}
				logger.debug("        skipSecurityCheck = " + skipSecurityCheck);
				logger.debug("        templateList.size = " + templateList.size());
				if (skipSecurityCheck || templateList.isEmpty()) {
					iter = templateList.iterator();
				}
				else {
					SecurityCacheManager securityController = SecurityCacheManager.getInstance();
					List<GridTemplate> resultList = new ArrayList<GridTemplate>();
					for (Iterator<GridTemplate> iterator = templateList.iterator(); iterator.hasNext();) {
						GridTemplate element = (GridTemplate) iterator.next();
						if (securityController.allowView(element.getID(), userID)) {
							resultList.add(element);
						}
					}
					iter = resultList.iterator();
				}
			}
			else if (entityType == EntityType.PARAMETER_TEMPLATE) {
				iter = ParameterTemplateManager.getInstance().getTemplates().iterator();
			}
			else if (entityType == EntityType.USER_DATA) {
				iter = SecurityCacheManager.getInstance().getUsers();
				List<UserData> userList = new ArrayList<UserData>();
				while (iter.hasNext()) {
					userList.add(((User) iter.next()).asUserData());
				}
				iter = null;
				iter = userList.iterator();
			}
			else if (entityType == EntityType.GUIDELINE_REPORT) {
				if (searchFilter instanceof GuidelineReportFilter) {
					GuidelineReportFilter reportFilter = (GuidelineReportFilter) searchFilter;
					// set server helper
					reportFilter.setServerFilterHelper(getServerFilterHelper());
					List<GuidelineReportData> tempList = GridManager.getInstance().searchGuidelinesWithCategoryCheck(reportFilter, userID);
					if (reportFilter.isIncludeParameters()) {
						List<ParameterTemplate> templateList = ParameterTemplateManager.getInstance().getTemplates();
						tempList.addAll(ParameterManager.getInstance().searchParameters(templateList));
					}
					iter = tempList.iterator();
				}
				else {
					iter = GridManager.getInstance().getAllGuidelineReportData(userID).iterator();
				}
			}
			else if (entityType == EntityType.PROCESS_PHASE) {
				iter = ProcessManager.getInstance().getAllPhases().iterator();
			}
			else if (entityType == EntityType.PROCESS_REQUEST) {
				iter = ProcessManager.getInstance().getAllRequests().iterator();
			}
			else if (entityType == EntityType.PROCESS_REQUEST) {
				iter = ProcessManager.getInstance().getAllRequests().iterator();
			}
			else if (entityType == EntityType.GUIDELINE_ACTION) {
				iter = GuidelineFunctionManager.getInstance().getAllActionTypes().iterator();
			}
			else if (entityType == EntityType.GUIDELINE_TEST_CONDITION) {
				iter = GuidelineFunctionManager.getInstance().getAllTestTypes().iterator();
			}
			else if (entityType == EntityType.CBR_CASE_BASE) {
				iter = CBRManager.getInstance().getCBRCaseBases().iterator();
			}
			else if (entityType == EntityType.CBR_CASE_CLASS) {
				iter = CBRManager.getInstance().getCBRCaseClasses().iterator();
			}
			else if (entityType == EntityType.CBR_SCORING_FUNCTION) {
				iter = CBRManager.getInstance().getCBRScoringFunctions().iterator();
			}
			else if (entityType == EntityType.CBR_CASE_ACTION) {
				iter = CBRManager.getInstance().getCBRCaseActions().iterator();
			}
			else if (entityType == EntityType.CBR_ATTRIBUTE_TYPE) {
				iter = CBRManager.getInstance().getCBRAttributeTypes().iterator();
			}
			else if (entityType == EntityType.CBR_ATTRIBUTE) {
				iter = CBRManager.getInstance().getCBRAttributes().iterator();
			}
			else if (entityType == EntityType.CBR_VALUE_RANGE) {
				iter = CBRManager.getInstance().getCBRValueRanges().iterator();
			}
			else if (entityType == EntityType.CBR_CASE) {
				iter = CBRManager.getInstance().getCBRCases().iterator();
			}
			else if (entityType == EntityType.DATE_SYNONYM) {
				iter = DateSynonymManager.getInstance().getAllDateSynonyms().iterator();
			}
			else {
				throw new IllegalArgumentException("Unsupported search entity " + entityType);
			}
		}

		List<T> result = new LinkedList<T>();
		Iterator<T> iterT = (Iterator<T>) iter;
		while (iterT.hasNext()) {
			T persistent = iterT.next();
			logger.debug("   checking " + persistent);
			if (searchFilter.isAcceptable(persistent)) {
				logger.debug("   adding " + persistent + " - passed filter criteria");
				result.add(persistent);
			}
		}
		return result;
	}

	public Persistent retrieveEntity(EntityType entityType, int entityID) {
		if (entityType == EntityType.GUIDELINE_ACTION) {
			return GuidelineFunctionManager.getInstance().getActionTypeDefinition(entityID);
		}
		if (entityType == EntityType.GUIDELINE_TEST_CONDITION) {
			return GuidelineFunctionManager.getInstance().getTestTypeDefinition(entityID);
		}
		if (entityType == EntityType.TEMPLATE) {
			return GuidelineTemplateManager.getInstance().getTemplate(entityID);
		}
		if (entityType == EntityType.CBR_CASE_CLASS) {
			return CBRManager.getInstance().getCBRCaseClass(entityID);
		}
		if (entityType == EntityType.CBR_SCORING_FUNCTION) {
			return CBRManager.getInstance().getCBRScoringFunction(entityID);
		}
		if (entityType == EntityType.CBR_CASE_ACTION) {
			return CBRManager.getInstance().getCBRCaseAction(entityID);
		}
		if (entityType == EntityType.CBR_ATTRIBUTE_TYPE) {
			return CBRManager.getInstance().getCBRAttributeType(entityID);
		}
		if (entityType == EntityType.CBR_ATTRIBUTE) {
			return CBRManager.getInstance().getCBRAttribute(entityID);
		}
		if (entityType == EntityType.CBR_VALUE_RANGE) {
			return CBRManager.getInstance().getCBRValueRange(entityID);
		}
		if (entityType == EntityType.DATE_SYNONYM) {
			return DateSynonymManager.getInstance().getDateSynonym(entityID);
		}

		throw new IllegalArgumentException("Unsupported retrieval of ID entity " + entityType);
	}

	public Persistent retrieveEntity(EntityType entityType, String name) {
		if (entityType == EntityType.USER_DATA) {
			return SecurityCacheManager.getInstance().getUser(name).asUserData();
		}

		throw new IllegalArgumentException("Unsupported retrieval of named entity " + entityType);
	}
}
package com.mindbox.pe.model.filter;

import static com.mindbox.pe.common.LogUtil.logInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.ContextUtil;
import com.mindbox.pe.common.DateUtil;
import com.mindbox.pe.model.AbstractIDObject;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.grid.ProductGrid;

/**
 * A generic search filter that contains super set of supporte files for import, export, deploy, and policy search.
 * <P>
 * This accepts {@link GuidelineReportData}, {@link ParameterGrid}, and {@link ProductGrid}.
 * @author Geneho Kim
 * @author MindBox
 */
public class GuidelineReportFilter extends AbstractSearchFilter<AbstractIDObject> {

	private static final Logger LOG = Logger.getLogger(GuidelineReportFilter.class);


	private static interface Filterable {
		DateSynonym getActivationDate();

		GuidelineContext[] getContext();

		DateSynonym getExpirationDate();

		String getStatus();

		int getTemplateID();

		TemplateUsageType getUsageType();

		boolean isParameter();

		void setMatchingRowNumbers(String numbers);
	}

	private static class GuidelineGridFilterable implements Filterable {
		private final ProductGrid data;

		public GuidelineGridFilterable(ProductGrid data) {
			this.data = data;
		}

		@Override
		public DateSynonym getActivationDate() {
			return data.getEffectiveDate();
		}

		@Override
		public GuidelineContext[] getContext() {
			return data.extractGuidelineContext();
		}

		@Override
		public DateSynonym getExpirationDate() {
			return data.getExpirationDate();
		}

		@Override
		public String getStatus() {
			return data.getStatus();
		}

		@Override
		public int getTemplateID() {
			return data.getTemplateID();
		}

		@Override
		public TemplateUsageType getUsageType() {
			return data.getTemplate().getUsageType();
		}

		@Override
		public boolean isParameter() {
			return false;
		}

		@Override
		public void setMatchingRowNumbers(String numbers) {
		}
	}

	private static class GuidelineReportDataFilterable implements Filterable {
		private final GuidelineReportData data;

		public GuidelineReportDataFilterable(GuidelineReportData data) {
			this.data = data;
		}

		@Override
		public DateSynonym getActivationDate() {
			return data.getActivationDate();
		}

		@Override
		public GuidelineContext[] getContext() {
			return data.getContext();
		}

		@Override
		public DateSynonym getExpirationDate() {
			return data.getExpirationDate();
		}

		@Override
		public String getStatus() {
			return data.getStatus();
		}

		@Override
		public int getTemplateID() {
			return data.getID();
		}

		@Override
		public TemplateUsageType getUsageType() {
			return data.getUsageType();
		}

		@Override
		public boolean isParameter() {
			return data.isParameter();
		}

		@Override
		public void setMatchingRowNumbers(String numbers) {
			data.setMatchingRowNumbers(numbers);
		}
	}

	private static class ParameterGridFilterable implements Filterable {
		private final ParameterGrid data;

		public ParameterGridFilterable(ParameterGrid data) {
			this.data = data;
		}

		@Override
		public DateSynonym getActivationDate() {
			return data.getEffectiveDate();
		}

		@Override
		public GuidelineContext[] getContext() {
			return data.extractGuidelineContext();
		}

		@Override
		public DateSynonym getExpirationDate() {
			return data.getExpirationDate();
		}

		@Override
		public String getStatus() {
			return data.getStatus();
		}

		@Override
		public int getTemplateID() {
			return data.getTemplateID();
		}

		@Override
		public TemplateUsageType getUsageType() {
			return null;
		}

		@Override
		public boolean isParameter() {
			return true;
		}

		@Override
		public void setMatchingRowNumbers(String numbers) {
		}
	}

	public static interface ServerFilterHelper {

		List<Integer> findMatchingGridRows(GuidelineContext[] contexts, AbstractGrid<?> grid, boolean includeParents, boolean includeChildren, boolean includeEmptyContexts);

		int getGridID(int templateID, GuidelineContext[] context, DateSynonym effDate, DateSynonym expDate);

		ProductGrid getProductGrid(int gridID);

		String getResource(String key);

		boolean inStatus(String status, String targetStatus);

		boolean isParentContext(GuidelineContext[] context1, GuidelineContext[] context2, boolean includeEmptyContexts);
	}

	private static final long serialVersionUID = -1857841196227483046L;

	private Date activeDate = null;
	private Date changesOnDate = null;
	private final List<GuidelineContext> contextList = new ArrayList<GuidelineContext>();
	private final List<Integer> guidelineTemplateIDs = new ArrayList<Integer>();
	private final List<Integer> parameterTemplateIDs = new ArrayList<Integer>();
	private final List<TemplateUsageType> usageTypeList = new ArrayList<TemplateUsageType>();
	private String className, attributeName;
	private String value;
	private final List<String> statusList;
	private boolean includeEmptyContexts;
	private boolean includeParentCategories;
	private boolean includeChildrenCategories;
	private boolean searchInColumnData;
	private ServerFilterHelper serverFilterHelper;
	private long ruleID = 0L;
	private int daysAgo = -1; // Filter policies expired before specified number of days
	private String thisStatusAndAbove = null; // Filter policies from this status and above
	private Date deployAsOfDate = null;

	// Data selection criteria
	private boolean includeGuidelines = false;

	private boolean includeTemplates = false;

	private boolean includeParameters = false;
	private boolean includeEntities = false;
	private boolean includeGuidelineActions = false;
	private boolean includeTestConditions = false;
	private boolean includeDateSynonyms = false;
	private boolean includeSecurityData = false;
	private boolean includeCBR = false;
	private boolean includeProcessData = false;
	private boolean optimizeRuleGeneration = true;
	private transient String searchDescription;

	public GuidelineReportFilter() {
		super(PeDataType.GUIDELINE_REPORT);
		statusList = new ArrayList<String>();
	}

	public void addAllUsageTypes(TemplateUsageType[] usageTypes) {
		for (TemplateUsageType usageType : usageTypes) {
			addUsageType(usageType);
		}
	}

	public void addContext(GuidelineContext context) {
		contextList.add(context);
	}

	public void addGuidelineTemplateID(Integer templateID) {
		if (!guidelineTemplateIDs.contains(templateID)) {
			guidelineTemplateIDs.add(templateID);
		}
	}

	public void addParameterTemplateID(Integer templateID) {
		if (!parameterTemplateIDs.contains(templateID)) {
			parameterTemplateIDs.add(templateID);
		}
	}

	public void addStatus(String status) {
		statusList.add(status);
	}

	public void addUsageType(TemplateUsageType usageType) {
		if (!usageTypeList.contains(usageType)) {
			usageTypeList.add(usageType);
		}
	}

	// TT-92
	private String findGridRowsContainingEntities(final Filterable data, final boolean doNotCheckEmptyContextFlag) {
		String rowNumbers = null;
		final int gridID = serverFilterHelper.getGridID(data.getTemplateID(), data.getContext(), data.getActivationDate(), data.getExpirationDate());
		final ProductGrid grid = serverFilterHelper.getProductGrid(gridID);
		if (grid != null && grid.getTemplate() != null) {
			if (grid.getTemplate().hasEntityTypeColumns()) {
				final List<Integer> list = serverFilterHelper.findMatchingGridRows(this.getContexts(), grid, includeParentCategories, includeChildrenCategories, includeEmptyContexts);
				if (list != null && list.size() > 0) {
					for (Iterator<Integer> i = list.iterator(); i.hasNext();) {
						String rowNumber = String.valueOf(i.next().intValue());
						rowNumbers = (rowNumbers == null) ? String.valueOf(rowNumber) : rowNumbers + "," + (rowNumber);
					}
				}
			}
			else if (doNotCheckEmptyContextFlag || includeEmptyContexts) {
				rowNumbers = serverFilterHelper.getResource("label.all");
			}
		}
		return rowNumbers;
	}

	public Date getActiveDate() {
		return activeDate;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public Date getChangesOnDate() {
		return changesOnDate;
	}

	public String getClassName() {
		return className;
	}

	public GuidelineContext[] getContexts() {
		return contextList.toArray(new GuidelineContext[contextList.size()]);
	}

	public int getDaysAgo() {
		return daysAgo;
	}

	public final Date getDeployAsOfDate() {
		return deployAsOfDate;
	}

	public List<Integer> getGuidelineTemplateIDs() {
		return Collections.unmodifiableList(guidelineTemplateIDs);
	}

	public int[] getIDsForCategoryType(int categoryTypeID) {
		for (int i = 0; i < contextList.size(); i++) {
			if (categoryTypeID == contextList.get(i).getGenericCategoryType()) {
				return contextList.get(i).getIDs();
			}
		}
		return null;
	}

	public int[] getIDsForEntityType(GenericEntityType type) {
		for (int i = 0; i < contextList.size(); i++) {
			if (type == contextList.get(i).getGenericEntityType()) {
				return contextList.get(i).getIDs();
			}
		}
		return null;
	}

	public List<Integer> getParameterTemplateIDs() {
		return Collections.unmodifiableList(parameterTemplateIDs);
	}

	public long getRuleID() {
		return ruleID;
	}

	public String getSearchDescription() {
		return searchDescription;
	}

	public String[] getStatuses() {
		return statusList.toArray(new String[0]);
	}

	public String getThisStatusAndAbove() {
		return thisStatusAndAbove;
	}

	public List<TemplateUsageType> getUsageTypes() {
		return Collections.unmodifiableList(usageTypeList);
	}

	public String getValue() {
		return value;
	}

	public boolean hasRuleID() {
		return ruleID > 0L;
	}

	private final boolean inTime(Filterable data) {
		return DateUtil.inTime(true, daysAgo, deployAsOfDate, data.getExpirationDate());
	}

	/**
	 * This method accepts {@link GuidelineReportData}, {@link ParameterGrid}, {@link ProductGrid}.
	 */
	@Override
	public boolean isAcceptable(AbstractIDObject object) {
		if (object == null) return false;
		if (object instanceof GuidelineReportData) {
			return isAcceptable(new GuidelineReportDataFilterable((GuidelineReportData) object));
		}
		else if (object instanceof ParameterGrid) {
			return includeParameters && isAcceptable(new ParameterGridFilterable((ParameterGrid) object));
		}
		else if (object instanceof ProductGrid) {
			return isAcceptable(new GuidelineGridFilterable((ProductGrid) object));
		}
		else {
			return false;
		}
	}

	private boolean isAcceptable(final Filterable data) {
		// Check template id and usage type, and rule id, if applicable
		if (data.isParameter()) {
			if (!parameterTemplateIDs.isEmpty() && !parameterTemplateIDs.contains(data.getTemplateID())) {
				return false;
			}
			if (hasRuleID()) return false;
		}
		else {
			if (!isForAcceptableGuidelineTemplate(data)) {
				return false;
			}
			if (hasRuleID() && !isRuleIDAcceptable(data)) {
				return false;
			}
		}

		// check status
		if (!isStatusAcceptable(data.getStatus())) return false;

		// check dates
		if (activeDate != null
				&& ((data.getActivationDate() != null && data.getActivationDate().getDate().after(activeDate)) || (data.getExpirationDate() != null && data.getExpirationDate().getDate().before(
						activeDate)))) {
			return false;
		}
		if (changesOnDate != null && ((data.getActivationDate() == null) || (data.getActivationDate() != null && !data.getActivationDate().getDate().equals(changesOnDate)))
				&& ((data.getExpirationDate() == null) || (data.getExpirationDate() != null && !data.getExpirationDate().getDate().equals(changesOnDate)))) {
			return false;
		}
		if (useDaysAgo() && !inTime(data)) {
			return false;
		}

		// check context
		if (!contextList.isEmpty()) {
			String matchingRowNumbers = null;
			boolean containsContext = false;
			GuidelineContext[] contexts = contextList.toArray(new GuidelineContext[contextList.size()]);
			if (!includeChildrenCategories && !includeParentCategories) {
				containsContext = ContextUtil.containsContext(data.getContext(), contexts, includeEmptyContexts);
			}
			else {
				if (includeChildrenCategories && includeParentCategories) {
					containsContext = serverFilterHelper.isParentContext(contexts, data.getContext(), includeEmptyContexts)
							|| serverFilterHelper.isParentContext(data.getContext(), contexts, includeEmptyContexts);
				}
				else if (includeChildrenCategories) {
					containsContext = serverFilterHelper.isParentContext(contexts, data.getContext(), includeEmptyContexts);
				}
				else if (includeParentCategories) {
					containsContext = serverFilterHelper.isParentContext(data.getContext(), contexts, includeEmptyContexts);
				}
			}
			if ((containsContext || serverFilterHelper.isParentContext(contexts, data.getContext(), true)) && searchInColumnData && !data.isParameter()) {
				// need to search grid data
				matchingRowNumbers = findGridRowsContainingEntities(data, containsContext);
				containsContext = matchingRowNumbers != null;
			}
			if (containsContext && matchingRowNumbers == null) {
				matchingRowNumbers = serverFilterHelper.getResource("label.all");
			}
			data.setMatchingRowNumbers(matchingRowNumbers);
			return containsContext;
		}

		// check attribute value
		return true;
	}

	private boolean isForAcceptableGuidelineTemplate(Filterable data) {
		if (usageTypeList.isEmpty() && guidelineTemplateIDs.isEmpty()) return true;
		if (!usageTypeList.isEmpty() && usageTypeList.contains(data.getUsageType())) {
			return true;
		}
		if (!guidelineTemplateIDs.isEmpty() && guidelineTemplateIDs.contains(data.getTemplateID())) {
			return true;
		}
		return false;
	}

	public boolean isIncludeCBR() {
		return includeCBR;
	}

	public boolean isIncludeChildrenCategories() {
		return includeChildrenCategories;
	}

	public boolean isIncludeDateSynonyms() {
		return includeDateSynonyms;
	}

	public boolean isIncludeEmptyContexts() {
		return includeEmptyContexts;
	}

	public boolean isIncludeEntities() {
		return includeEntities;
	}

	public boolean isIncludeGuidelineActions() {
		return includeGuidelineActions;
	}

	public boolean isIncludeGuidelines() {
		return includeGuidelines;
	}

	public boolean isIncludeParameters() {
		return includeParameters;
	}

	public boolean isIncludeParentCategories() {
		return includeParentCategories;
	}

	public boolean isIncludeProcessData() {
		return includeProcessData;
	}

	public boolean isIncludeSecurityData() {
		return includeSecurityData;
	}

	public boolean isIncludeTemplates() {
		return includeTemplates;
	}

	public boolean isIncludeTestConditions() {
		return includeTestConditions;
	}

	public boolean isOptimizeRuleGeneration() {
		return optimizeRuleGeneration;
	}

	private boolean isRuleIDAcceptable(Filterable data) {
		int gridID = serverFilterHelper.getGridID(data.getTemplateID(), data.getContext(), data.getActivationDate(), data.getExpirationDate());
		ProductGrid grid = serverFilterHelper.getProductGrid(gridID);
		return (grid != null && grid.hasRuleID(ruleID));
	}

	public boolean isSearchInColumnData() {
		return searchInColumnData;
	}

	private boolean isStatusAcceptable(String status) {
		if (!statusList.isEmpty() && !statusList.contains(status)) {
			return false;
		}
		if (thisStatusAndAbove != null && !serverFilterHelper.inStatus(status, thisStatusAndAbove)) {
			return false;
		}
		return true;
	}

	public void setActiveDate(Date date) {
		this.activeDate = date;
	}

	public void setAttributeName(String string) {
		attributeName = string;
	}

	public void setChangesOnDate(Date date) {
		this.changesOnDate = date;
	}

	public void setClassName(String string) {
		className = string;
	}

	public void setDaysAgo(int daysAgo) {
		this.daysAgo = daysAgo;
	}

	public final void setDeployAsOfDate(Date deployAsOfDate) {
		this.deployAsOfDate = deployAsOfDate;
		logInfo(LOG, "Deploy as of date set to [%s]", this.deployAsOfDate);
	}

	public void setIncludeCBR(boolean includeCBR) {
		this.includeCBR = includeCBR;
	}

	public void setIncludeChildrenCategories(boolean includeChildrenCategories) {
		this.includeChildrenCategories = includeChildrenCategories;
	}

	public void setIncludeDateSynonyms(boolean includeDateSynonyms) {
		this.includeDateSynonyms = includeDateSynonyms;
	}

	public void setIncludeEmptyContexts(boolean includeEmptyContexts) {
		this.includeEmptyContexts = includeEmptyContexts;
	}

	public void setIncludeEntities(boolean includeEntities) {
		this.includeEntities = includeEntities;
	}

	public void setIncludeGuidelineActions(boolean includeGuidelineActions) {
		this.includeGuidelineActions = includeGuidelineActions;
	}

	public void setIncludeGuidelines(boolean includeGuidelines) {
		this.includeGuidelines = includeGuidelines;
	}

	public void setIncludeParameters(boolean includeParameters) {
		this.includeParameters = includeParameters;
	}

	public void setIncludeParentCategories(boolean includeParentCategories) {
		this.includeParentCategories = includeParentCategories;
	}

	public void setIncludeProcessData(boolean includeProcessData) {
		this.includeProcessData = includeProcessData;
	}

	public void setIncludeSecurityData(boolean includeSecurityData) {
		this.includeSecurityData = includeSecurityData;
	}

	public void setIncludeTemplates(boolean includeTemplates) {
		this.includeTemplates = includeTemplates;
	}

	public void setIncludeTestConditions(boolean includeTestConditions) {
		this.includeTestConditions = includeTestConditions;
	}

	public void setOptimizeRuleGeneration(boolean optimizeRuleGeneration) {
		this.optimizeRuleGeneration = optimizeRuleGeneration;
	}

	public void setRuleID(long ruleID) {
		if (ruleID > 0L) {
			this.ruleID = ruleID;
		}
	}

	public void setSearchDescription(String searchDescription) {
		this.searchDescription = searchDescription;
	}

	public void setSearchInColumnData(boolean searchInColumnData) {
		this.searchInColumnData = searchInColumnData;
	}

	public void setServerFilterHelper(ServerFilterHelper serverFilterHelper) {
		this.serverFilterHelper = serverFilterHelper;
	}

	public void setThisStatusAndAbove(String thisStatusAndAbove) {
		this.thisStatusAndAbove = thisStatusAndAbove;
	}

	public void setValue(String string) {
		value = string;
	}

	@Override
	public String toString() {
		return "GuidelineReportFilter[status=" + thisStatusAndAbove + ",context=" + contextList + ",guideliens?" + includeGuidelines + ",template?=" + includeTemplates + ",params?="
				+ includeParameters + ",entities?=" + includeEntities + ",gActions?=" + includeGuidelineActions + ",testCond?=" + includeTestConditions + ",dates?=" + includeDateSynonyms + ",cbr?="
				+ includeCBR + ",procData?=" + includeProcessData + ",daysAgo=" + daysAgo + ",activeDate=" + activeDate + ",chagesOnDate=" + changesOnDate + ",ruleID=" + ruleID + ",gTempIds="
				+ guidelineTemplateIDs + ",ptempIds=" + parameterTemplateIDs + ",usages=" + usageTypeList + ",class=" + className + ",attr=" + attributeName + ",value=" + value + ",statusList="
				+ statusList + ",emptyContexts?" + includeEmptyContexts + ",parentCats?=" + includeParentCategories + ",childCats?=" + includeChildrenCategories + ",columnData?=" + searchInColumnData
				+ ']';
	}

	public boolean useDaysAgo() {
		return daysAgo > -1;
	}
}

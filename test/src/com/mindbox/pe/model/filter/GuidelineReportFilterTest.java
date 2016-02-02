package com.mindbox.pe.model.filter;

import java.util.List;

import junit.framework.TestSuite;

import org.easymock.MockControl;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ContextUtil;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.SimpleEntityData;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.server.ServerContextUtil;
import com.mindbox.pe.server.cache.EntityManager;

public class GuidelineReportFilterTest extends AbstractTestWithTestConfig {

	public static junit.framework.Test suite() {
		TestSuite suite = new TestSuite("Guideline Report Filter Tests"); // new
		suite.addTestSuite(GuidelineReportFilterTest.class);
		return suite;
	}

	private GuidelineContext c1, c2, c3;
	private GuidelineReportFilter filter;
	private GuidelineReportData guidelineReportData;
	private ParameterGrid parameterGrid;
	private ProductGrid productGrid;
	private GridTemplate template;

	public GuidelineReportFilterTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer("test/config/PowerEditorConfiguration-NoProgram.xml");
		guidelineReportData = new GuidelineReportData(
				100,
				1,
				"name",
				TemplateUsageType.getAllInstances()[0],
				"1.0",
				new GuidelineContext[0],
				null,
				null,
				null,
				true);
		parameterGrid = new ParameterGrid(1, 100, null, null);
		c1 = new GuidelineContext(GenericEntityType.forName("channel"));
		c1.setIDs(new int[] { 10, 20, 30 });
		c2 = new GuidelineContext(GenericEntityType.forName("channel"));
		c2.setIDs(new int[] { 30, 10, 3, 20 });
		c3 = new GuidelineContext(GenericEntityType.forName("product"));
		c3.setIDs(new int[] { 9000 });
		filter = new GuidelineReportFilter();
	}

	private void setUpProvideGrid() {
		template = ObjectMother.attachGridTemplateColumn(ObjectMother.createGridTemplate(TemplateUsageType.getAllInstances()[0]), 1);
		productGrid = ObjectMother.createGuidelineGrid(template);
	}

	protected void tearDown() throws Exception {
		filter = null;
		guidelineReportData = null;
		parameterGrid = null;
		productGrid = null;
		super.tearDown();
	}

	public void testConstructorSetsInvariantsProperly() throws Exception {
		assertFalse(filter.isIncludeCBR());
		assertFalse(filter.isIncludeChildrenCategories());
		assertFalse(filter.isIncludeDateSynonyms());
		assertFalse(filter.isIncludeEmptyContexts());
		assertFalse(filter.isIncludeEntities());
		assertFalse(filter.isIncludeGuidelineActions());
		assertFalse(filter.isIncludeGuidelines());
		assertFalse(filter.isIncludeParameters());
		assertFalse(filter.isIncludeParentCategories());
		assertFalse(filter.isIncludeProcessData());
		assertFalse(filter.isIncludeSecurityData());
		assertFalse(filter.isIncludeTemplates());
		assertFalse(filter.isIncludeTestConditions());
		assertFalse(filter.useDaysAgo());
		assertFalse(filter.isSearchInColumnData());
		assertFalse(filter.hasRuleID());
		assertNull(filter.getThisStatusAndAbove());
		assertNull(filter.getValue());
		assertNull(filter.getClassName());
		assertNull(filter.getAttributeName());
		assertNull(filter.getChangesOnDate());
		assertNull(filter.getActiveDate());
		assertTrue(filter.getGuidelineTemplateIDs().isEmpty());
		assertTrue(filter.getUsageTypes().isEmpty());
		assertTrue(filter.getContexts() == null || filter.getContexts().length == 0);
		assertTrue(filter.getStatuses().length == 0);
	}

	public void testHasRuleIDPositiveCase() throws Exception {
		filter.setRuleID(ObjectMother.createInt());
		assertTrue(filter.hasRuleID());
	}

	public void testHasRuleIDNegativeCase() throws Exception {
		assertFalse(filter.hasRuleID());
	}

	public void testIsAcceptableForContextWithEntityColumnsIncludeEmptyPositiveCase() throws Exception {
		setUpProvideGrid();
		GridTemplateColumn entityColumn = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		template.getColumn(1).setDataSpecDigest(ObjectMother.createEntityColumnDataSpecDigest("product", true, true, false));
		entityColumn = ObjectMother.createGridTemplateColumn(2, ObjectMother.createUsageType());
		entityColumn.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		template.addGridTemplateColumn(entityColumn);
		entityColumn = ObjectMother.createGridTemplateColumn(3, ObjectMother.createUsageType());
		entityColumn.setDataSpecDigest(template.getColumn(1).getColumnDataSpecDigest());
		template.addGridTemplateColumn(entityColumn);

		GenericEntityType type = GenericEntityType.forName("product");
		GenericEntity entity = ObjectMother.createGenericEntity(type);
		entity.setName("name");
		int entityID = entity.getId();
		EntityManager.getInstance().addGenericEntity(entityID, type.getID(), entity.getName(), -1, entity.getPropertyMap());

		productGrid.setNumRows(3);
		productGrid.setValue(1, 1, new CategoryOrEntityValue(type, true, entityID + 1));
		productGrid.setValue(1, 3, new CategoryOrEntityValue(type, true, entityID + 2));
		productGrid.setValue(2, 1, new CategoryOrEntityValue(type, true, entityID + 3));
		productGrid.setValue(2, 3, new CategoryOrEntityValue(type, true, entityID));
		productGrid.setValue(3, 1, new CategoryOrEntityValue(type, true, entityID + 4));
		productGrid.setValue(3, 3, null);

		GuidelineContext context = new GuidelineContext(type);
		context.setIDs(new int[] { entityID });
		filter.addContext(context);

		MockControl mockControl = MockControl.createControl(GuidelineReportFilter.ServerFilterHelper.class);
		GuidelineReportFilter.ServerFilterHelper mockHelper = (GuidelineReportFilter.ServerFilterHelper) mockControl.getMock();
		filter.setServerFilterHelper(mockHelper);
		filter.setIncludeEmptyContexts(true);
		filter.setSearchInColumnData(true);
		int gridID = productGrid.getID();
		List<Integer> matchingRows = ServerContextUtil.findMatchingGridRows(
				new GuidelineContext[] { context },
				productGrid,
				false,
				false,
				true);
		mockControl.expectAndReturn(mockHelper.getGridID(
				guidelineReportData.getTemplateID(),
				guidelineReportData.getContext(),
				guidelineReportData.getActivationDate(),
				guidelineReportData.getExpirationDate()), gridID);
		mockControl.expectAndReturn(mockHelper.getProductGrid(gridID), productGrid);
		mockHelper.findMatchingGridRows(filter.getContexts(), productGrid, false, false, true);
		mockControl.setMatcher(MockControl.ARRAY_MATCHER);
		mockControl.setReturnValue(matchingRows);
		mockControl.replay();

		assertTrue(filter.isAcceptable(guidelineReportData));
		assertEquals("2,3", guidelineReportData.getMatchingRowNumbers());
	}

	public void testIsAcceptableForContextWithEntityColumnsNoIncludeEmptyPositiveCase() throws Exception {
		setUpProvideGrid();
		GridTemplateColumn entityColumn = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		template.getColumn(1).setDataSpecDigest(ObjectMother.createEntityColumnDataSpecDigest("product", true, true, false));
		entityColumn = ObjectMother.createGridTemplateColumn(2, ObjectMother.createUsageType());
		entityColumn.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		template.addGridTemplateColumn(entityColumn);
		entityColumn = ObjectMother.createGridTemplateColumn(3, ObjectMother.createUsageType());
		entityColumn.setDataSpecDigest(template.getColumn(1).getColumnDataSpecDigest());
		template.addGridTemplateColumn(entityColumn);

		GenericEntityType type = GenericEntityType.forName("product");
		GenericEntity entity = ObjectMother.createGenericEntity(type);
		entity.setName("name");
		int entityID = entity.getId();
		EntityManager.getInstance().addGenericEntity(entityID, type.getID(), entity.getName(), -1, entity.getPropertyMap());

		productGrid.setNumRows(3);
		productGrid.setValue(1, 1, new CategoryOrEntityValue(type, true, entityID + 1));
		productGrid.setValue(1, 3, new CategoryOrEntityValue(type, true, entityID + 2));
		productGrid.setValue(2, 1, new CategoryOrEntityValue(type, true, entityID + 3));
		productGrid.setValue(2, 3, new CategoryOrEntityValue(type, true, entityID));
		productGrid.setValue(3, 1, new CategoryOrEntityValue(type, true, entityID + 4));
		productGrid.setValue(3, 3, null);

		GuidelineContext context = new GuidelineContext(type);
		context.setIDs(new int[] { entityID });
		filter.addContext(context);

		MockControl mockControl = MockControl.createControl(GuidelineReportFilter.ServerFilterHelper.class);
		GuidelineReportFilter.ServerFilterHelper mockHelper = (GuidelineReportFilter.ServerFilterHelper) mockControl.getMock();
		filter.setServerFilterHelper(mockHelper);
		filter.setIncludeEmptyContexts(false);
		filter.setSearchInColumnData(true);
		int gridID = productGrid.getID();
		List<Integer> matchingRows = ServerContextUtil.findMatchingGridRows(
				new GuidelineContext[] { context },
				productGrid,
				false,
				false,
				false);
		mockControl.expectAndReturn(mockHelper.getGridID(
				guidelineReportData.getTemplateID(),
				guidelineReportData.getContext(),
				guidelineReportData.getActivationDate(),
				guidelineReportData.getExpirationDate()), gridID);
		mockControl.expectAndReturn(mockHelper.getProductGrid(gridID), productGrid);
		mockHelper.findMatchingGridRows(filter.getContexts(), productGrid, false, false, false);
		mockControl.setMatcher(MockControl.ARRAY_MATCHER);
		mockControl.setReturnValue(matchingRows);
		mockHelper.isParentContext(filter.getContexts(), guidelineReportData.getContext(), true);
		mockControl.setMatcher(MockControl.ARRAY_MATCHER);
		mockControl.setReturnValue(ServerContextUtil.isParentContext(filter.getContexts(), guidelineReportData.getContext(), true));
		mockControl.replay();

		assertTrue(filter.isAcceptable(guidelineReportData));
		assertEquals("2", guidelineReportData.getMatchingRowNumbers());
	}

	public void testIsAcceptableForContextWithEntityColumnsNegativeCase() throws Exception {
		setUpProvideGrid();
		GridTemplateColumn entityColumn = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		template.getColumn(1).setDataSpecDigest(ObjectMother.createEntityColumnDataSpecDigest("product", true, true, false));
		entityColumn = ObjectMother.createGridTemplateColumn(2, ObjectMother.createUsageType());
		entityColumn.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		template.addGridTemplateColumn(entityColumn);
		entityColumn = ObjectMother.createGridTemplateColumn(3, ObjectMother.createUsageType());
		entityColumn.setDataSpecDigest(template.getColumn(1).getColumnDataSpecDigest());
		template.addGridTemplateColumn(entityColumn);

		GenericEntityType type = GenericEntityType.forName("product");
		GenericEntity entity = ObjectMother.createGenericEntity(type);
		entity.setName("name");
		int entityID = entity.getId();
		EntityManager.getInstance().addGenericEntity(entityID, type.getID(), entity.getName(), -1, entity.getPropertyMap());

		productGrid.setNumRows(3);
		productGrid.setValue(1, 1, new CategoryOrEntityValue(type, true, entityID + 1));
		productGrid.setValue(1, 3, new CategoryOrEntityValue(type, true, entityID + 2));
		productGrid.setValue(2, 1, new CategoryOrEntityValue(type, true, entityID + 3));
		productGrid.setValue(2, 3, new CategoryOrEntityValue(type, true, entityID + 3));
		productGrid.setValue(3, 1, new CategoryOrEntityValue(type, true, entityID + 4));
		productGrid.setValue(3, 3, new CategoryOrEntityValue(type, true, entityID + 4));

		GuidelineContext context = new GuidelineContext(type);
		context.setIDs(new int[] { entityID });
		filter.addContext(context);

		MockControl mockControl = MockControl.createControl(GuidelineReportFilter.ServerFilterHelper.class);
		GuidelineReportFilter.ServerFilterHelper mockHelper = (GuidelineReportFilter.ServerFilterHelper) mockControl.getMock();
		filter.setServerFilterHelper(mockHelper);
		filter.setIncludeEmptyContexts(true);
		filter.setSearchInColumnData(true);
		int gridID = productGrid.getID();
		List<Integer> matchingRows = ServerContextUtil.findMatchingGridRows(
				new GuidelineContext[] { context },
				productGrid,
				false,
				false,
				true);
		mockControl.expectAndReturn(mockHelper.getGridID(
				guidelineReportData.getTemplateID(),
				guidelineReportData.getContext(),
				guidelineReportData.getActivationDate(),
				guidelineReportData.getExpirationDate()), gridID);
		mockControl.expectAndReturn(mockHelper.getProductGrid(gridID), productGrid);
		mockHelper.findMatchingGridRows(filter.getContexts(), productGrid, false, false, true);
		mockControl.setMatcher(MockControl.ARRAY_MATCHER);
		mockControl.setReturnValue(matchingRows);
		mockControl.replay();

		assertFalse(filter.isAcceptable(guidelineReportData));
	}

	public void testIsAcceptableForContextWithNoEntityColumnsPositiveCase() throws Exception {
		setUpProvideGrid();
		GridTemplateColumn entityColumn = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		template.getColumn(1).setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		entityColumn = ObjectMother.createGridTemplateColumn(2, ObjectMother.createUsageType());
		entityColumn.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		template.addGridTemplateColumn(entityColumn);
		entityColumn = ObjectMother.createGridTemplateColumn(3, ObjectMother.createUsageType());
		entityColumn.setDataSpecDigest(template.getColumn(1).getColumnDataSpecDigest());
		template.addGridTemplateColumn(entityColumn);

		GenericEntityType type = GenericEntityType.forName("product");
		GenericEntity entity = ObjectMother.createGenericEntity(type);
		entity.setName("name");
		int entityID = entity.getId();
		EntityManager.getInstance().addGenericEntity(entityID, type.getID(), entity.getName(), -1, entity.getPropertyMap());

		productGrid.setNumRows(3);

		GuidelineContext context = new GuidelineContext(type);
		context.setIDs(new int[] { entityID });
		filter.addContext(context);

		MockControl mockControl = MockControl.createControl(GuidelineReportFilter.ServerFilterHelper.class);
		GuidelineReportFilter.ServerFilterHelper mockHelper = (GuidelineReportFilter.ServerFilterHelper) mockControl.getMock();
		filter.setServerFilterHelper(mockHelper);
		filter.setIncludeEmptyContexts(true);
		filter.setSearchInColumnData(true);
		int gridID = productGrid.getID();
		List<Integer> matchingRows = ServerContextUtil.findMatchingGridRows(
				new GuidelineContext[] { context },
				productGrid,
				false,
				false,
				true);
		mockControl.expectAndReturn(mockHelper.getGridID(
				guidelineReportData.getTemplateID(),
				guidelineReportData.getContext(),
				guidelineReportData.getActivationDate(),
				guidelineReportData.getExpirationDate()), gridID);
		mockControl.expectAndReturn(mockHelper.getProductGrid(gridID), productGrid);
		mockHelper.findMatchingGridRows(filter.getContexts(), productGrid, false, false, true);
		mockControl.setMatcher(MockControl.ARRAY_MATCHER);
		mockControl.setReturnValue(matchingRows);
		mockControl.expectAndReturn(mockHelper.getResource("label.all"), "ALL");
		mockControl.replay();

		assertTrue(filter.isAcceptable(guidelineReportData));
		assertEquals("ALL", guidelineReportData.getMatchingRowNumbers());
	}

	public void testIsAcceptableForRuleIDPositiveCaseWithNoRuleIDSet() throws Exception {
		setUpProvideGrid();
		productGrid.getTemplate().getColumn(1).setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		productGrid.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		productGrid.setValue(1, 1, new Long(ObjectMother.createInt()));

		MockControl mockControl = MockControl.createControl(GuidelineReportFilter.ServerFilterHelper.class);
		GuidelineReportFilter.ServerFilterHelper mockHelper = (GuidelineReportFilter.ServerFilterHelper) mockControl.getMock();
		filter.setServerFilterHelper(mockHelper);
		int gridID = ObjectMother.createInt();
		mockControl.expectAndReturn(mockHelper.getGridID(
				guidelineReportData.getTemplateID(),
				guidelineReportData.getContext(),
				guidelineReportData.getActivationDate(),
				guidelineReportData.getExpirationDate()), gridID);
		mockControl.expectAndReturn(mockHelper.getProductGrid(gridID), productGrid);
		mockControl.replay();

		assertTrue(filter.isAcceptable(guidelineReportData));
	}

	public void testIsAcceptableForRuleIDPositiveCaseWithRuleIDSet() throws Exception {
		filter.setRuleID(ObjectMother.createInt());

		setUpProvideGrid();
		productGrid.getTemplate().getColumn(1).setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		productGrid.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		productGrid.setValue(1, 1, new Long(filter.getRuleID()));

		MockControl mockControl = MockControl.createControl(GuidelineReportFilter.ServerFilterHelper.class);
		GuidelineReportFilter.ServerFilterHelper mockHelper = (GuidelineReportFilter.ServerFilterHelper) mockControl.getMock();
		filter.setServerFilterHelper(mockHelper);
		int gridID = ObjectMother.createInt();
		mockControl.expectAndReturn(mockHelper.getGridID(
				guidelineReportData.getTemplateID(),
				guidelineReportData.getContext(),
				guidelineReportData.getActivationDate(),
				guidelineReportData.getExpirationDate()), gridID);
		mockControl.expectAndReturn(mockHelper.getProductGrid(gridID), productGrid);
		mockControl.replay();

		assertTrue(filter.isAcceptable(guidelineReportData));
	}

	public void testIsAcceptableForRuleIDNegativeCaseWithRuleIDSet() throws Exception {
		filter.setRuleID(ObjectMother.createInt());

		setUpProvideGrid();
		productGrid.getTemplate().getColumn(1).setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		productGrid.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		productGrid.setValue(1, 1, new Long(ObjectMother.createInt()));

		MockControl mockControl = MockControl.createControl(GuidelineReportFilter.ServerFilterHelper.class);
		GuidelineReportFilter.ServerFilterHelper mockHelper = (GuidelineReportFilter.ServerFilterHelper) mockControl.getMock();
		filter.setServerFilterHelper(mockHelper);
		int gridID = ObjectMother.createInt();
		mockControl.expectAndReturn(mockHelper.getGridID(
				guidelineReportData.getTemplateID(),
				guidelineReportData.getContext(),
				guidelineReportData.getActivationDate(),
				guidelineReportData.getExpirationDate()), gridID);
		mockControl.expectAndReturn(mockHelper.getProductGrid(gridID), productGrid);
		mockControl.replay();

		assertFalse(filter.isAcceptable(guidelineReportData));
	}

	public void testIsAcceptableForRuleIDNegativeCaseWithRuleIDSetAndNoRuleIDColumn() throws Exception {
		filter.setRuleID(ObjectMother.createInt());

		setUpProvideGrid();
		productGrid.getTemplate().getColumn(1).setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		productGrid.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);

		MockControl mockControl = MockControl.createControl(GuidelineReportFilter.ServerFilterHelper.class);
		GuidelineReportFilter.ServerFilterHelper mockHelper = (GuidelineReportFilter.ServerFilterHelper) mockControl.getMock();
		filter.setServerFilterHelper(mockHelper);
		int gridID = ObjectMother.createInt();
		mockControl.expectAndReturn(mockHelper.getGridID(
				guidelineReportData.getTemplateID(),
				guidelineReportData.getContext(),
				guidelineReportData.getActivationDate(),
				guidelineReportData.getExpirationDate()), gridID);
		mockControl.expectAndReturn(mockHelper.getProductGrid(gridID), productGrid);
		mockControl.replay();

		assertFalse(filter.isAcceptable(guidelineReportData));
	}

	public void testIsAcceptableForRuleIDNegativeCaseForParamGridWithRuleIDSet() throws Exception {
		filter.setRuleID(ObjectMother.createInt());
		assertFalse(filter.isAcceptable(parameterGrid));
	}

	public void testIsAcceptableForTemplateIDPositiveCase() throws Exception {
		filter.addGuidelineTemplateID(guidelineReportData.getTemplateID());
		assertTrue(filter.isAcceptable(guidelineReportData));
	}

	public void testIsAcceptableForTemplateIDNegativeCase() throws Exception {
		filter.addGuidelineTemplateID(guidelineReportData.getTemplateID() + 1);
		assertFalse(filter.isAcceptable(guidelineReportData));
	}

	public void testIsAcceptableForTemplateIDAndUsageTypePositiveCase() throws Exception {
		filter.addGuidelineTemplateID(guidelineReportData.getTemplateID());
		filter.addUsageType(guidelineReportData.getUsageType());
		assertTrue(filter.isAcceptable(guidelineReportData));
	}

	public void testIsAcceptableForTemplateIDAndUsageTypeNegativeCase() throws Exception {
		filter.addGuidelineTemplateID(guidelineReportData.getTemplateID() + 1);
		TemplateUsageType[] usageTypes = TemplateUsageType.getAllInstances();
		for (int i = 0; i < usageTypes.length; i++) {
			if (usageTypes[i] != guidelineReportData.getUsageType()) {
				filter.addUsageType(usageTypes[i]);
			}
		}
		assertFalse(filter.isAcceptable(guidelineReportData));
	}

	public void testIsAcceptableForUsageTypePositiveCase() throws Exception {
		filter.addUsageType(guidelineReportData.getUsageType());
		assertTrue(filter.isAcceptable(guidelineReportData));
	}

	public void testIsAcceptableForUsageTypeNegativeCase() throws Exception {
		TemplateUsageType[] usageTypes = TemplateUsageType.getAllInstances();
		for (int i = 0; i < usageTypes.length; i++) {
			if (usageTypes[i] != guidelineReportData.getUsageType()) {
				filter.addUsageType(usageTypes[i]);
			}
		}
		assertFalse(filter.isAcceptable(guidelineReportData));
	}

	public void testInitDefaultsAddParametersToFalse() throws Exception {
		assertFalse(filter.isIncludeParameters());
	}

	public void testIsAcceptableWithNullReturnsFalse() throws Exception {
		assertFalse(filter.isAcceptable(null));
	}

	public void testIsAcceptableOnlyAcceptsValidObjectTypes() throws Exception {
		assertFalse(filter.isAcceptable(new SimpleEntityData(1, "name")));
		assertTrue(filter.isAcceptable(guidelineReportData));
	}

	public void testIsAcceptableOnlyAcceptsParametersIfAddParametersIsSetToTrue() throws Exception {
		assertFalse(filter.isAcceptable(parameterGrid));
		filter.setIncludeParameters(true);
		assertTrue(filter.isAcceptable(parameterGrid));
	}

	public void testIsAcceptableWithNoStatusAcceptsAllForGuidelineReportData() throws Exception {
		guidelineReportData.setStatus("Draft");
		assertTrue(filter.isAcceptable(guidelineReportData));
		guidelineReportData.setStatus("Dev Test");
		assertTrue(filter.isAcceptable(guidelineReportData));
		guidelineReportData.setStatus("QA");
		assertTrue(filter.isAcceptable(guidelineReportData));
		guidelineReportData.setStatus("Production");
		assertTrue(filter.isAcceptable(guidelineReportData));
	}

	public void testIsAcceptableWithAcceptsIncludedStatusOnlyForGuidelineReportData() throws Exception {
		filter.addStatus("Dev Test");
		filter.addStatus("Production");
		guidelineReportData.setStatus("Draft");
		assertFalse(filter.isAcceptable(guidelineReportData));
		guidelineReportData.setStatus("Dev Test");
		assertTrue(filter.isAcceptable(guidelineReportData));
		guidelineReportData.setStatus("QA");
		assertFalse(filter.isAcceptable(guidelineReportData));
		guidelineReportData.setStatus("Production");
		assertTrue(filter.isAcceptable(guidelineReportData));
	}

	public void testIsAcceptableWithNoStatusAcceptsAllForParameterGrid() throws Exception {
		filter.setIncludeParameters(true);
		parameterGrid.setStatus("Draft");
		assertTrue(filter.isAcceptable(parameterGrid));
		parameterGrid.setStatus("Dev Test");
		assertTrue(filter.isAcceptable(parameterGrid));
		parameterGrid.setStatus("QA");
		assertTrue(filter.isAcceptable(parameterGrid));
		parameterGrid.setStatus("Production");
		assertTrue(filter.isAcceptable(parameterGrid));
	}

	public void testIsAcceptableWithAcceptsIncludedStatusOnlyForParameterGrid() throws Exception {
		filter.setIncludeParameters(true);
		filter.addStatus("Dev Test");
		filter.addStatus("Production");
		parameterGrid.setStatus("Draft");
		assertFalse(filter.isAcceptable(parameterGrid));
		parameterGrid.setStatus("Dev Test");
		assertTrue(filter.isAcceptable(parameterGrid));
		parameterGrid.setStatus("QA");
		assertFalse(filter.isAcceptable(parameterGrid));
		parameterGrid.setStatus("Production");
		assertTrue(filter.isAcceptable(parameterGrid));
	}

	public void testNotFoundEntityIDShouldFail() throws Exception {
		filter.addContext(c3);

		GuidelineContext otherProdContext = new GuidelineContext(GenericEntityType.forName("product"));
		otherProdContext.setIDs(new int[] { 4000 });

		GuidelineContext[] contexts = new GuidelineContext[] { otherProdContext };

		assertFalse(
				"Filter with the same entity should not accept context that does not have the specified entity id",
				ContextUtil.containsContext(contexts, filter.getContexts()));
	}

	public void testNotFoundGenericEntityTypeShouldFail() throws Exception {
		filter.addContext(c3);

		GuidelineContext[] contexts = new GuidelineContext[] { c2 };
		assertFalse(
				"Filter with the same entity should not accept context that does not have the specified entity type",
				ContextUtil.containsContext(contexts, filter.getContexts()));
	}

	public void testContextMatchEmpty() throws Exception {
		GuidelineContext[] contexts = new GuidelineContext[] { c3, new GuidelineContext(GenericEntityType.forName("product")) };
		assertTrue("Filter with empty context must accept any context", ContextUtil.containsContext(contexts, filter.getContexts()));
	}

	public void testContainsContextWithNullContextAndEmptyFilterReturnsTrue() throws Exception {
		assertTrue(ContextUtil.containsContext(null, filter.getContexts()));
	}

	public void testContainsContextWithNullContextAndNonEmptyFilterReturnsFalse() throws Exception {
		filter.addContext(c1);
		assertFalse(ContextUtil.containsContext(null, filter.getContexts()));
	}

	public void testContainsContextWithSubCategorySetReturnsTrue() throws Exception {
		GuidelineContext context = new GuidelineContext(GenericEntityType.forName("channel").getCategoryType());
		context.setIDs(new int[] { 20 });
		filter.addContext(context);

		context = new GuidelineContext(GenericEntityType.forName("channel").getCategoryType());
		context.setIDs(new int[] { 20, 30, 40 });
		assertTrue(ContextUtil.containsContext(new GuidelineContext[] { context }, filter.getContexts()));
	}

	public void testContainsContextWithSuperCategorySetReturnsFalse() throws Exception {
		GuidelineContext context = new GuidelineContext(GenericEntityType.forName("channel").getCategoryType());
		context.setIDs(new int[] { 20, 30 });
		filter.addContext(context);

		context = new GuidelineContext(GenericEntityType.forName("channel").getCategoryType());
		context.setIDs(new int[] { 20 });
		assertFalse(ContextUtil.containsContext(new GuidelineContext[] { context }, filter.getContexts()));
	}

	public void testContainsContextWithMoreContextReturnsTrue() throws Exception {
		filter.addContext(c2);
		assertTrue(ContextUtil.containsContext(new GuidelineContext[] { c3, c2 }, filter.getContexts()));
	}

	public void testContextMatchSuccess() throws Exception {
		filter.addContext(c1);

		assertTrue("Failed to match on ids", ContextUtil.containsContext(new GuidelineContext[] { c2 }, filter.getContexts()));
	}

	public void testContextMatchFailureIDs() throws Exception {
		filter.addContext(c2);
		assertFalse("Falsely matched on ids when contexts size are equal", ContextUtil.containsContext(
				new GuidelineContext[] { c1 },
				filter.getContexts()));
	}

	public void testContextMatchFailureGenericEntityTypes() throws Exception {
		filter.addContext(c2);
		filter.addContext(c3);
		assertFalse("Falsely matched on ids when contexts of size > 1 is used", ContextUtil.containsContext(
				new GuidelineContext[] { c3, c1 },
				filter.getContexts()));
	}

	public void testIsIncludeParentCategories() throws Exception {
		filter.setIncludeParentCategories(true);
		assertTrue(filter.isIncludeParentCategories());
		filter.setIncludeParentCategories(false);
		assertFalse(filter.isIncludeParentCategories());
	}

	public void testDefaultSearchOptionsAreFalse() throws Exception {
		assertFalse(filter.isIncludeChildrenCategories());
		assertFalse(filter.isIncludeParentCategories());
		assertFalse(filter.isSearchInColumnData());
	}

	public void testSearchInColumnData() throws Exception {
		filter.setSearchInColumnData(true);
		assertTrue(filter.isSearchInColumnData());
		filter.setSearchInColumnData(false);
		assertFalse(filter.isSearchInColumnData());
	}


}

package com.mindbox.pe.model.filter;

import static com.mindbox.pe.common.CommonTestObjectMother.attachGridTemplateColumn;
import static com.mindbox.pe.common.CommonTestObjectMother.createColumnDataSpecDigest;
import static com.mindbox.pe.common.CommonTestObjectMother.createGenericEntityType;
import static com.mindbox.pe.common.CommonTestObjectMother.createGridTemplate;
import static com.mindbox.pe.common.CommonTestObjectMother.createGuidelineGrid;
import static com.mindbox.pe.common.CommonTestObjectMother.createUsageType;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.ContextUtil;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.SimpleEntityData;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.unittest.AbstractTestBase;

public class GuidelineReportFilterTest extends AbstractTestBase {

	private GuidelineContext c1, c2, c3;
	private GuidelineReportFilter filter;
	private GuidelineReportData guidelineReportData;
	private ParameterGrid parameterGrid;
	private ProductGrid productGrid;
	private GridTemplate template;
	private GenericEntityType genericEntityType1;
	private GenericEntityType genericEntityType2;

	@Before
	public void setUp() throws Exception {
		genericEntityType1 = createGenericEntityType(createInt(), createInt());
		genericEntityType2 = createGenericEntityType(createInt(), createInt());

		guidelineReportData = new GuidelineReportData(100, 1, "name", createUsageType(), "1.0", new GuidelineContext[0], null, null, null, true);
		parameterGrid = new ParameterGrid(1, 100, null, null);
		c1 = new GuidelineContext(genericEntityType1);
		c1.setIDs(new int[] { 10, 20, 30 });
		c2 = new GuidelineContext(genericEntityType1);
		c2.setIDs(new int[] { 30, 10, 3, 20 });
		c3 = new GuidelineContext(genericEntityType2);
		c3.setIDs(new int[] { 9000 });
		filter = new GuidelineReportFilter();
	}

	private void setUpProvideGrid() {
		template = attachGridTemplateColumn(createGridTemplate(guidelineReportData.getUsageType()), 1);
		productGrid = createGuidelineGrid(template);
	}

	@Test
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

	@Test
	public void testContainsContextWithMoreContextReturnsTrue() throws Exception {
		filter.addContext(c2);
		assertTrue(ContextUtil.containsContext(new GuidelineContext[] { c3, c2 }, filter.getContexts()));
	}

	@Test
	public void testContainsContextWithNullContextAndEmptyFilterReturnsTrue() throws Exception {
		assertTrue(ContextUtil.containsContext(null, filter.getContexts()));
	}

	@Test
	public void testContainsContextWithNullContextAndNonEmptyFilterReturnsFalse() throws Exception {
		filter.addContext(c1);
		assertFalse(ContextUtil.containsContext(null, filter.getContexts()));
	}

	@Test
	public void testContainsContextWithSubCategorySetReturnsTrue() throws Exception {
		GuidelineContext context = new GuidelineContext(genericEntityType1.getCategoryType());
		context.setIDs(new int[] { 20 });
		filter.addContext(context);

		context = new GuidelineContext(genericEntityType1.getCategoryType());
		context.setIDs(new int[] { 20, 30, 40 });
		assertTrue(ContextUtil.containsContext(new GuidelineContext[] { context }, filter.getContexts()));
	}

	@Test
	public void testContainsContextWithSuperCategorySetReturnsFalse() throws Exception {
		GuidelineContext context = new GuidelineContext(genericEntityType1.getCategoryType());
		context.setIDs(new int[] { 20, 30 });
		filter.addContext(context);

		context = new GuidelineContext(genericEntityType1.getCategoryType());
		context.setIDs(new int[] { 20 });
		assertFalse(ContextUtil.containsContext(new GuidelineContext[] { context }, filter.getContexts()));
	}

	@Test
	public void testContextMatchEmpty() throws Exception {
		GuidelineContext[] contexts = new GuidelineContext[] { c3, new GuidelineContext(genericEntityType2) };
		assertTrue("Filter with empty context must accept any context", ContextUtil.containsContext(contexts, filter.getContexts()));
	}

	@Test
	public void testContextMatchFailureGenericEntityTypes() throws Exception {
		filter.addContext(c2);
		filter.addContext(c3);
		assertFalse("Falsely matched on ids when contexts of size > 1 is used", ContextUtil.containsContext(new GuidelineContext[] { c3, c1 }, filter.getContexts()));
	}

	@Test
	public void testContextMatchFailureIDs() throws Exception {
		filter.addContext(c2);
		assertFalse("Falsely matched on ids when contexts size are equal", ContextUtil.containsContext(new GuidelineContext[] { c1 }, filter.getContexts()));
	}

	@Test
	public void testContextMatchSuccess() throws Exception {
		filter.addContext(c1);

		assertTrue("Failed to match on ids", ContextUtil.containsContext(new GuidelineContext[] { c2 }, filter.getContexts()));
	}

	@Test
	public void testDefaultSearchOptionsAreFalse() throws Exception {
		assertFalse(filter.isIncludeChildrenCategories());
		assertFalse(filter.isIncludeParentCategories());
		assertFalse(filter.isSearchInColumnData());
	}

	@Test
	public void testHasRuleIDNegativeCase() throws Exception {
		assertFalse(filter.hasRuleID());
	}

	@Test
	public void testHasRuleIDPositiveCase() throws Exception {
		filter.setRuleID(createInt());
		assertTrue(filter.hasRuleID());
	}

	@Test
	public void testInitDefaultsAddParametersToFalse() throws Exception {
		assertFalse(filter.isIncludeParameters());
	}

	@Test
	public void testIsAcceptableForRuleIDNegativeCaseForParamGridWithRuleIDSet() throws Exception {
		filter.setRuleID(createInt());
		assertFalse(filter.isAcceptable(parameterGrid));
	}

	@Test
	public void testIsAcceptableForRuleIDNegativeCaseWithRuleIDSet() throws Exception {
		filter.setRuleID(createInt());

		setUpProvideGrid();
		productGrid.getTemplate().getColumn(1).setDataSpecDigest(createColumnDataSpecDigest());
		productGrid.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		productGrid.setValue(1, 1, new Long(createInt()));

		GuidelineReportFilter.ServerFilterHelper mockHelper = createMock(GuidelineReportFilter.ServerFilterHelper.class);
		filter.setServerFilterHelper(mockHelper);
		int gridID = createInt();
		expect(mockHelper.getGridID(guidelineReportData.getTemplateID(), guidelineReportData.getContext(), guidelineReportData.getActivationDate(), guidelineReportData.getExpirationDate())).andReturn(
				gridID);
		expect(mockHelper.getProductGrid(gridID)).andReturn(productGrid);
		replay(mockHelper);

		assertFalse(filter.isAcceptable(guidelineReportData));
	}

	@Test
	public void testIsAcceptableForRuleIDNegativeCaseWithRuleIDSetAndNoRuleIDColumn() throws Exception {
		filter.setRuleID(createInt());

		setUpProvideGrid();
		productGrid.getTemplate().getColumn(1).setDataSpecDigest(createColumnDataSpecDigest());
		productGrid.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);

		GuidelineReportFilter.ServerFilterHelper mockHelper = createMock(GuidelineReportFilter.ServerFilterHelper.class);
		filter.setServerFilterHelper(mockHelper);
		int gridID = createInt();
		expect(mockHelper.getGridID(guidelineReportData.getTemplateID(), guidelineReportData.getContext(), guidelineReportData.getActivationDate(), guidelineReportData.getExpirationDate())).andReturn(
				gridID);
		expect(mockHelper.getProductGrid(gridID)).andReturn(productGrid);
		replay(mockHelper);

		assertFalse(filter.isAcceptable(guidelineReportData));
	}

	@Test
	public void testIsAcceptableForRuleIDPositiveCaseWithNoRuleIDSet() throws Exception {
		setUpProvideGrid();
		productGrid.getTemplate().getColumn(1).setDataSpecDigest(createColumnDataSpecDigest());
		productGrid.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		productGrid.setValue(1, 1, new Long(createInt()));

		GuidelineReportFilter.ServerFilterHelper mockHelper = createMock(GuidelineReportFilter.ServerFilterHelper.class);
		filter.setServerFilterHelper(mockHelper);
		int gridID = createInt();
		expect(mockHelper.getGridID(guidelineReportData.getTemplateID(), guidelineReportData.getContext(), guidelineReportData.getActivationDate(), guidelineReportData.getExpirationDate())).andReturn(
				gridID);
		expect(mockHelper.getProductGrid(gridID)).andReturn(productGrid);
		replay(mockHelper);


		assertTrue(filter.isAcceptable(guidelineReportData));
	}

	@Test
	public void testIsAcceptableForRuleIDPositiveCaseWithRuleIDSet() throws Exception {
		filter.setRuleID(createInt());

		setUpProvideGrid();
		productGrid.getTemplate().getColumn(1).setDataSpecDigest(createColumnDataSpecDigest());
		productGrid.getTemplate().getColumn(1).getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_RULE_ID);
		productGrid.setValue(1, 1, new Long(filter.getRuleID()));

		GuidelineReportFilter.ServerFilterHelper mockHelper = createMock(GuidelineReportFilter.ServerFilterHelper.class);
		filter.setServerFilterHelper(mockHelper);
		int gridID = createInt();
		expect(mockHelper.getGridID(guidelineReportData.getTemplateID(), guidelineReportData.getContext(), guidelineReportData.getActivationDate(), guidelineReportData.getExpirationDate())).andReturn(
				gridID);
		expect(mockHelper.getProductGrid(gridID)).andReturn(productGrid);
		replay(mockHelper);

		assertTrue(filter.isAcceptable(guidelineReportData));
	}

	@Test
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

	@Test
	public void testIsAcceptableForTemplateIDAndUsageTypePositiveCase() throws Exception {
		filter.addGuidelineTemplateID(guidelineReportData.getTemplateID());
		filter.addUsageType(guidelineReportData.getUsageType());
		assertTrue(filter.isAcceptable(guidelineReportData));
	}

	@Test
	public void testIsAcceptableForTemplateIDNegativeCase() throws Exception {
		filter.addGuidelineTemplateID(guidelineReportData.getTemplateID() + 1);
		assertFalse(filter.isAcceptable(guidelineReportData));
	}

	@Test
	public void testIsAcceptableForTemplateIDPositiveCase() throws Exception {
		filter.addGuidelineTemplateID(guidelineReportData.getTemplateID());
		assertTrue(filter.isAcceptable(guidelineReportData));
	}

	@Test
	public void testIsAcceptableForUsageTypeNegativeCase() throws Exception {
		filter.addUsageType(createUsageType());
		assertFalse(filter.isAcceptable(guidelineReportData));
	}

	@Test
	public void testIsAcceptableForUsageTypePositiveCase() throws Exception {
		filter.addUsageType(guidelineReportData.getUsageType());
		assertTrue(filter.isAcceptable(guidelineReportData));
	}

	@Test
	public void testIsAcceptableOnlyAcceptsParametersIfAddParametersIsSetToTrue() throws Exception {
		assertFalse(filter.isAcceptable(parameterGrid));
		filter.setIncludeParameters(true);
		assertTrue(filter.isAcceptable(parameterGrid));
	}

	@Test
	public void testIsAcceptableOnlyAcceptsValidObjectTypes() throws Exception {
		assertFalse(filter.isAcceptable(new SimpleEntityData(1, "name")));
		assertTrue(filter.isAcceptable(guidelineReportData));
	}

	@Test
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

	@Test
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

	@Test
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

	@Test
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

	@Test
	public void testIsAcceptableWithNullReturnsFalse() throws Exception {
		assertFalse(filter.isAcceptable(null));
	}

	@Test
	public void testIsIncludeParentCategories() throws Exception {
		filter.setIncludeParentCategories(true);
		assertTrue(filter.isIncludeParentCategories());
		filter.setIncludeParentCategories(false);
		assertFalse(filter.isIncludeParentCategories());
	}

	@Test
	public void testNotFoundEntityIDShouldFail() throws Exception {
		filter.addContext(c3);

		GuidelineContext otherProdContext = new GuidelineContext(genericEntityType2);
		otherProdContext.setIDs(new int[] { 4000 });

		GuidelineContext[] contexts = new GuidelineContext[] { otherProdContext };

		assertFalse("Filter with the same entity should not accept context that does not have the specified entity id", ContextUtil.containsContext(contexts, filter.getContexts()));
	}

	@Test
	public void testNotFoundGenericEntityTypeShouldFail() throws Exception {
		filter.addContext(c3);

		GuidelineContext[] contexts = new GuidelineContext[] { c2 };
		assertFalse("Filter with the same entity should not accept context that does not have the specified entity type", ContextUtil.containsContext(contexts, filter.getContexts()));
	}

	@Test
	public void testSearchInColumnData() throws Exception {
		filter.setSearchInColumnData(true);
		assertTrue(filter.isSearchInColumnData());
		filter.setSearchInColumnData(false);
		assertFalse(filter.isSearchInColumnData());
	}

}

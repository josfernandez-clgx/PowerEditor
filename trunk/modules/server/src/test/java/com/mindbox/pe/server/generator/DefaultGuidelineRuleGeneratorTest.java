package com.mindbox.pe.server.generator;

import static com.mindbox.pe.server.ServerTestObjectMother.attachDomainAttributes;
import static com.mindbox.pe.server.ServerTestObjectMother.attachTimeSlice;

import static com.mindbox.pe.server.ServerTestObjectMother.createDomainClass;
import static com.mindbox.pe.server.ServerTestObjectMother.createTimeSliceContainer;
import static com.mindbox.pe.unittest.UnitTestHelper.clearDir;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.RuleAction;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.model.template.TemplateMessageDigest;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.TypeEnumValueManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.model.TimeSliceContainer;

public class DefaultGuidelineRuleGeneratorTest extends AbstractTestWithTestConfig {

	private ProductGrid grid;
	private GenericEntityType messageContextType;
	private DefaultGuidelineRuleGenerator guidelineRuleGenerator = null;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		config.initServer();

		DomainClass domainClass = attachDomainAttributes(createDomainClass(), 1);
		DomainManager.getInstance().addDomainClass(domainClass);
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.STRING);

		TypeEnumValueManager.getInstance().startLoading();
		TypeEnumValueManager.getInstance().insert(TypeEnumValue.TYPE_STATUS, new TypeEnumValue(1, "Draft", "Draft"));
		TypeEnumValueManager.getInstance().finishLoading();

		messageContextType = GenericEntityType.forID(ConfigurationManager.getInstance().getEntityTypeForMessageContext().getTypeID().intValue());
		EntityManager.getInstance().startLoading();
		EntityManager.getInstance().addGenericEntity(11, messageContextType.getID(), "entity11", -1, new HashMap<String, Object>());
		EntityManager.getInstance().addGenericEntity(12, messageContextType.getID(), "entity12", -1, new HashMap<String, Object>());

		// Categories added to avoid connecting to DB
		EntityManager.getInstance().addGenericEntityCategory(10, 11, "Test Category");
		EntityManager.getInstance().addGenericEntityCategory(20, 22, "Test Category");
		EntityManager.getInstance().addGenericEntityCategory(30, 1000, "Test Channel Category");
		EntityManager.getInstance().addGenericEntityCategory(40, 1000, "Test Investor Category");
		EntityManager.getInstance().addGenericEntityCategory(50, 1000, "Test Branch Category");
		EntityManager.getInstance().finishLoading();

		GuidelineFunctionManager.getInstance().startLoading();
		ActionTypeDefinition atDef = new ActionTypeDefinition(3, "action", "action");
		atDef.addUsageType(TemplateUsageType.getAllInstances()[0]);
		atDef.setDeploymentRule("peaction(value,\"%column 1%\",context, \"%context%\")");

		GuidelineFunctionManager.getInstance().insertActionTypeDefinition(atDef);
		GuidelineFunctionManager.getInstance().finishLoading();
		assertNotNull(GuidelineFunctionManager.getInstance().getActionObject(atDef.getID()));

		GridTemplateColumn column = new GridTemplateColumn(1, "col1", "Col1", 100, TemplateUsageType.getAllInstances()[0]);
		ColumnDataSpecDigest cdsDigest = new ColumnDataSpecDigest();
		cdsDigest.setAllowBlank("yes");
		cdsDigest.setMultipleSelect("false");
		cdsDigest.setType(ColumnDataSpecDigest.TYPE_STRING);
		column.setDataSpecDigest(cdsDigest);
		GridTemplate template = new GridTemplate(1000, "template", TemplateUsageType.getAllInstances()[0]);
		template.addColumn(column);
		RuleDefinition ruleDef = new RuleDefinition(1, "rule", "rule");
		ruleDef.setUsageType(template.getUsageType());
		Condition condition = RuleElementFactory.getInstance().createCondition();
		condition.setReference(RuleElementFactory.getInstance().createReference(domainClass.getName(), domainClass.getDomainAttributes().get(0).getName()));
		condition.setValue(RuleElementFactory.getInstance().createValue(RuleElementFactory.getInstance().createColumnReference(1)));
		ruleDef.add(condition);
		RuleAction action = RuleElementFactory.getInstance().createRuleAction();
		action.setActionType(atDef);
		action.setName("action");
		ruleDef.updateAction(action);
		template.setRuleDefinition(ruleDef);

		GuidelineTemplateManager.getInstance().startLoading();
		GuidelineTemplateManager.getInstance().addTemplate(template);
		GuidelineTemplateManager.getInstance().finishLoading();

		grid = new ProductGrid(100, template, null, null);
		grid.setNumRows(1);
		grid.setValue(1, 1, "Value for row one");

		GridManager.getInstance().startLoading();
		GridManager.getInstance().addProductGrid(grid);
		GridManager.getInstance().finishLoading();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		messageContextType = null;
		grid = null;
		EntityManager.getInstance().startLoading();
		TypeEnumValueManager.getInstance().startLoading();
		DomainManager.getInstance().startLoading();
		GridManager.getInstance().startLoading();
		GuidelineFunctionManager.getInstance().startLoading();
		GuidelineTemplateManager.getInstance().startLoading();
		clearDir(new File(ConfigurationManager.getInstance().getServerConfigHelper().getDeploymentConfig().getBaseDir()));
		config.resetConfiguration();
		super.tearDown();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGenerateWithEmptyTimeSliceCotnainerThrowsIllegalArgumentException() throws Exception {
		guidelineRuleGenerator = new DefaultGuidelineRuleGenerator(GuidelineTemplateManager.getInstance().getTemplate(1000), new GenerateStats("target"), new DefaultOutputController("Draft"));
		guidelineRuleGenerator.generateOptimized(100, createTimeSliceContainer(), new GuidelineReportFilter());
	}

	@Test
	public void testGenerateWithEntitySpecificMessageButEmptyContextGeneratesSingleRule() throws Exception {
		TimeSliceContainer timeSliceContainer = attachTimeSlice(createTimeSliceContainer());
		timeSliceContainer.freeze();
		GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(1000);
		TemplateMessageDigest tmDigest = new TemplateMessageDigest();
		tmDigest.setEntityID(-1);
		tmDigest.setText("Default message");
		template.addMessageDigest(tmDigest);
		tmDigest = new TemplateMessageDigest();
		tmDigest.setEntityID(12);
		tmDigest.setText("Message for 12");
		template.addMessageDigest(tmDigest);

		final GenerateStats generateStats = new GenerateStats("target");
		guidelineRuleGenerator = new DefaultGuidelineRuleGenerator(GuidelineTemplateManager.getInstance().getTemplate(1000), generateStats, new DefaultOutputController("Draft"));
		GuidelineReportFilter filter = new GuidelineReportFilter();
		filter.setIncludeGuidelines(true);
		guidelineRuleGenerator.generateOptimized(100, timeSliceContainer, filter);
		assertEquals(0, generateStats.getNumErrorsGenerated());
		assertEquals(1, generateStats.getNumRulesGenerated());
	}

	@Test
	public void testGenerateWithEntitySpecificMessagesAtColumnSplitsRules() throws Exception {
		TimeSliceContainer timeSliceContainer = attachTimeSlice(createTimeSliceContainer());
		timeSliceContainer.freeze();
		GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(1000);
		GridTemplateColumn column = (GridTemplateColumn) template.getColumn(1);
		TemplateMessageDigest tmDigest = new TemplateMessageDigest();
		tmDigest.setEntityID(-1);
		tmDigest.setText("Default message");
		column.addMessageDigest(tmDigest);
		tmDigest = new TemplateMessageDigest();
		tmDigest.setEntityID(12);
		tmDigest.setText("Message for 12");
		column.addMessageDigest(tmDigest);
		column.setRuleDefinition(template.getRuleDefinition());
		template.setRuleDefinition(null);

		GridManager.getInstance().addGridContext(100, messageContextType, 11);
		GridManager.getInstance().addGridContext(100, messageContextType, 12);

		final GenerateStats generateStats = new GenerateStats("target");
		guidelineRuleGenerator = new DefaultGuidelineRuleGenerator(GuidelineTemplateManager.getInstance().getTemplate(1000), generateStats, new DefaultOutputController("Draft"));
		GuidelineReportFilter filter = new GuidelineReportFilter();
		filter.setIncludeGuidelines(true);
		guidelineRuleGenerator.generateOptimized(100, timeSliceContainer, filter);
		assertEquals(0, generateStats.getNumErrorsGenerated());
		assertEquals(2, generateStats.getNumRulesGenerated());
	}

	@Test
	public void testGenerateWithEntitySpecificMessagesAtTemplateSplitsRules() throws Exception {
		TimeSliceContainer timeSliceContainer = attachTimeSlice(createTimeSliceContainer());
		timeSliceContainer.freeze();
		GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(1000);
		TemplateMessageDigest tmDigest = new TemplateMessageDigest();
		tmDigest.setEntityID(-1);
		tmDigest.setText("Default message");
		template.addMessageDigest(tmDigest);
		tmDigest = new TemplateMessageDigest();
		tmDigest.setEntityID(12);
		tmDigest.setText("Message for 12");
		template.addMessageDigest(tmDigest);

		GridManager.getInstance().addGridContext(100, messageContextType, 11);
		GridManager.getInstance().addGridContext(100, messageContextType, 12);

		final GenerateStats generateStats = new GenerateStats("target");
		guidelineRuleGenerator = new DefaultGuidelineRuleGenerator(GuidelineTemplateManager.getInstance().getTemplate(1000), generateStats, new DefaultOutputController("Draft"));
		GuidelineReportFilter filter = new GuidelineReportFilter();
		filter.setIncludeGuidelines(true);
		guidelineRuleGenerator.generateOptimized(100, timeSliceContainer, filter);
		assertEquals(0, generateStats.getNumErrorsGenerated());
		assertEquals(2, generateStats.getNumRulesGenerated());
	}

	@Test
	public void testGenerateWithMultipleEntitiesAndNoEntitySpecificMessageGeneratesSingleRule() throws Exception {
		TimeSliceContainer timeSliceContainer = attachTimeSlice(createTimeSliceContainer());
		timeSliceContainer.freeze();
		GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(1000);
		TemplateMessageDigest tmDigest = new TemplateMessageDigest();
		tmDigest.setEntityID(-1);
		tmDigest.setText("Default message");
		template.addMessageDigest(tmDigest);

		GridManager.getInstance().addGridContext(100, messageContextType, 11);
		GridManager.getInstance().addGridContext(100, messageContextType, 12);

		final GenerateStats generateStats = new GenerateStats("target");
		guidelineRuleGenerator = new DefaultGuidelineRuleGenerator(GuidelineTemplateManager.getInstance().getTemplate(1000), generateStats, new DefaultOutputController("Draft"));
		GuidelineReportFilter filter = new GuidelineReportFilter();
		filter.setIncludeGuidelines(true);
		guidelineRuleGenerator.generateOptimized(100, timeSliceContainer, filter);
		assertEquals(0, generateStats.getNumErrorsGenerated());
		assertEquals(1, generateStats.getNumRulesGenerated());
	}

	@Test
	public void testGenerateWithNullTemplateThrowsNullPointerException() throws Exception {
		TimeSliceContainer timeSliceContainer = attachTimeSlice(createTimeSliceContainer());
		try {
			guidelineRuleGenerator.generateOptimized(100, timeSliceContainer, new GuidelineReportFilter());
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testGenerateWithNullTimeSliceCotnainerThrowsNullPointerException() throws Exception {
		try {
			guidelineRuleGenerator.generateOptimized(100, null, new GuidelineReportFilter());
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

}

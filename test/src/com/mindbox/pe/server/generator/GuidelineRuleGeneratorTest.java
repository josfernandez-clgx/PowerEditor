package com.mindbox.pe.server.generator;

import java.io.File;
import java.util.HashMap;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.TemplateMessageDigest;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.TypeEnumValue;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.RuleAction;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.cache.GridManager;
import com.mindbox.pe.server.cache.GuidelineFunctionManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.TypeEnumValueManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.model.TimeSliceContainer;

public class GuidelineRuleGeneratorTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GuidelineRuleGeneratorTest Tests");
		suite.addTestSuite(GuidelineRuleGeneratorTest.class);
		return suite;
	}

	private ProductGrid grid;
	private GenericEntityType messageContextType;
	private boolean deinitOutputController = false;

	public GuidelineRuleGeneratorTest(String name) {
		super(name);
	}

	public void testGenerateWithNullTimeSliceCotnainerThrowsNullPointerException() throws Exception {
		try {
			GuidelineRuleGenerator.getInstance().generate(null, null, new GuidelineReportFilter());
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testGenerateWithEmptyTimeSliceCotnainerThrowsIllegalArgumentException() throws Exception {
		try {
			GuidelineRuleGenerator.getInstance().generate(
					ObjectMother.createTimeSliceContainer(),
					GuidelineTemplateManager.getInstance().getTemplate(1000),
					new GuidelineReportFilter());
			fail("Expected IllegalArgumentException not thrown");
		}
		catch (IllegalArgumentException ex) {
			// expected
		}
	}

	public void testGenerateWithNullTemplateThrowsNullPointerException() throws Exception {
		TimeSliceContainer timeSliceContainer = ObjectMother.attachTimeSlice(ObjectMother.createTimeSliceContainer());
		try {
			GuidelineRuleGenerator.getInstance().generate(timeSliceContainer, null, new GuidelineReportFilter());
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	public void testGenerateWithEntitySpecificMessagesAtTemplateSplitsRules() throws Exception {
		TimeSliceContainer timeSliceContainer = ObjectMother.attachTimeSlice(ObjectMother.createTimeSliceContainer());
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

		GuidelineRuleGenerator.getInstance().init(new OutputController("Draft"));
		deinitOutputController = true;
		GuidelineReportFilter filter = new GuidelineReportFilter();
		filter.setIncludeGuidelines(true);
		GuidelineRuleGenerator.getInstance().generate(timeSliceContainer, GuidelineTemplateManager.getInstance().getTemplate(1000), filter);
		assertEquals(0, GuidelineRuleGenerator.getInstance().getErrorCount());
		assertEquals(2, GuidelineRuleGenerator.getInstance().getGeneratedRuleCount());
	}

	public void testGenerateWithEntitySpecificMessagesAtColumnSplitsRules() throws Exception {
		TimeSliceContainer timeSliceContainer = ObjectMother.attachTimeSlice(ObjectMother.createTimeSliceContainer());
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

		GuidelineRuleGenerator.getInstance().init(new OutputController("Draft"));
		deinitOutputController = true;
		GuidelineReportFilter filter = new GuidelineReportFilter();
		filter.setIncludeGuidelines(true);
		GuidelineRuleGenerator.getInstance().generate(timeSliceContainer, GuidelineTemplateManager.getInstance().getTemplate(1000), filter);
		assertEquals(0, GuidelineRuleGenerator.getInstance().getErrorCount());
		assertEquals(2, GuidelineRuleGenerator.getInstance().getGeneratedRuleCount());
	}

	public void testGenerateWithEntitySpecificMessageButEmptyContextGeneratesSingleRule() throws Exception {
		TimeSliceContainer timeSliceContainer = ObjectMother.attachTimeSlice(ObjectMother.createTimeSliceContainer());
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

		GuidelineRuleGenerator.getInstance().init(new OutputController("Draft"));
		deinitOutputController = true;
		GuidelineReportFilter filter = new GuidelineReportFilter();
		filter.setIncludeGuidelines(true);
		GuidelineRuleGenerator.getInstance().generate(timeSliceContainer, GuidelineTemplateManager.getInstance().getTemplate(1000), filter);
		assertEquals(0, GuidelineRuleGenerator.getInstance().getErrorCount());
		assertEquals(1, GuidelineRuleGenerator.getInstance().getGeneratedRuleCount());
	}

	public void testGenerateWithMultipleEntitiesAndNoEntitySpecificMessageGeneratesSingleRule() throws Exception {
		TimeSliceContainer timeSliceContainer = ObjectMother.attachTimeSlice(ObjectMother.createTimeSliceContainer());
		timeSliceContainer.freeze();
		GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(1000);
		TemplateMessageDigest tmDigest = new TemplateMessageDigest();
		tmDigest.setEntityID(-1);
		tmDigest.setText("Default message");
		template.addMessageDigest(tmDigest);

		GridManager.getInstance().addGridContext(100, messageContextType, 11);
		GridManager.getInstance().addGridContext(100, messageContextType, 12);

		GuidelineRuleGenerator.getInstance().init(new OutputController("Draft"));
		deinitOutputController = true;
		GuidelineReportFilter filter = new GuidelineReportFilter();
		filter.setIncludeGuidelines(true);
		GuidelineRuleGenerator.getInstance().generate(timeSliceContainer, GuidelineTemplateManager.getInstance().getTemplate(1000), filter);
		assertEquals(0, GuidelineRuleGenerator.getInstance().getErrorCount());
		assertEquals(1, GuidelineRuleGenerator.getInstance().getGeneratedRuleCount());
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();

		DomainClass domainClass = ObjectMother.attachDomainAttributes(ObjectMother.createDomainClass(), 1);
		DomainManager.getInstance().addDomainClass(domainClass);
		domainClass.getDomainAttributes().get(0).setDeployType(DeployType.STRING);

		TypeEnumValueManager.getInstance().startLoading();
		TypeEnumValueManager.getInstance().insert(TypeEnumValue.TYPE_STATUS, new TypeEnumValue(1, "Draft", "Draft"));
		TypeEnumValueManager.getInstance().finishLoading();

		messageContextType = GenericEntityType.forID(ConfigurationManager.getInstance().getEntityConfiguration().getEntityTypeForMessageContext().getTypeID());
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
		condition.setReference(RuleElementFactory.getInstance().createReference(
				domainClass.getName(),
				domainClass.getDomainAttributes().get(0).getName()));
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

	protected void tearDown() throws Exception {
		if (deinitOutputController) GuidelineRuleGenerator.getInstance().writeAll();
		if (deinitOutputController) GuidelineRuleGenerator.getInstance().getOutputController().closeErrorWriters();
		messageContextType = null;
		grid = null;
		EntityManager.getInstance().startLoading();
		TypeEnumValueManager.getInstance().startLoading();
		DomainManager.getInstance().startLoading();
		GridManager.getInstance().startLoading();
		GuidelineFunctionManager.getInstance().startLoading();
		GuidelineTemplateManager.getInstance().startLoading();
		clearDir(new File(ConfigurationManager.getInstance().getServerConfiguration().getDeploymentConfig().getBaseDir()));
		config.resetConfiguration();
		super.tearDown();
	}

}

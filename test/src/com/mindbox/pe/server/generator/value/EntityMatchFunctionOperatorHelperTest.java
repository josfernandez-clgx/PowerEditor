package com.mindbox.pe.server.generator.value;

import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.server.cache.EntityManager;

import junit.framework.Test;
import junit.framework.TestSuite;


public class EntityMatchFunctionOperatorHelperTest extends OperatorHelperTestBase {

	public EntityMatchFunctionOperatorHelperTest(String name) {
		super(name);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("EntityMatchFunctionOperatorHelperTest Tests");
		suite.addTestSuite(EntityMatchFunctionOperatorHelperTest.class);

		return suite;
	}

	private static final String VARIABLE = "?var";

	public void testFormatForPatternWithNullHappyCase() throws Exception {
		testFormatForPattern(VARIABLE, null, Condition.OP_ENTITY_MATCH_FUNC, false, null);
		testFormatForPattern(VARIABLE, null, Condition.OP_ENTITY_MATCH_FUNC, true, null);
	}

	public void testFormatForPatternWithEmptyStringHappyCase() throws Exception {
		testFormatForPattern(VARIABLE, "", Condition.OP_ENTITY_MATCH_FUNC, false, null);
		testFormatForPattern(VARIABLE, "", Condition.OP_ENTITY_MATCH_FUNC, true, null);
	}

	public void testFormatForPatternForEntityValue() throws Exception {
		CategoryOrEntityValue value = new CategoryOrEntityValue(GenericEntityType.forName("product"), true, 1);
		String expectedValue = VARIABLE + " &:(pe:entity-match :type product :id " + VARIABLE
				+ " :time-slice ?time-slice :entity (build$ 1))";
		testFormatForPattern(expectedValue, value, Condition.OP_ENTITY_MATCH_FUNC, false, null);
	}

	public void testFormatForPatternForCategoryValue() throws Exception {

		CategoryOrEntityValue value = new CategoryOrEntityValue(GenericEntityType.forName("product"), false, 100);
		String expectedValue = VARIABLE + " &:(pe:entity-match :type product :id " + VARIABLE
				+ " :time-slice ?time-slice :category (build$ 100))";
		testFormatForPattern(expectedValue, value, Condition.OP_ENTITY_MATCH_FUNC, false, null);
	}

	public void testFormatForPatternForMultipleEntityValues() throws Exception {
		CategoryOrEntityValue value1 = new CategoryOrEntityValue(GenericEntityType.forName("product"), true, 1);
		CategoryOrEntityValue value2 = new CategoryOrEntityValue(GenericEntityType.forName("product"), true, 2);
		CategoryOrEntityValues value = new CategoryOrEntityValues();
		value.add(value1);
		value.add(value2);

		String expectedValue = VARIABLE + " &:(pe:entity-match :type product :id " + VARIABLE
				+ " :time-slice ?time-slice :entity (build$ 1 2))";
		testFormatForPattern(expectedValue, value, Condition.OP_ENTITY_MATCH_FUNC, false, null);
	}

	public void testFormatForPatternForMultipleEntityAndCategoryValues() throws Exception {
		CategoryOrEntityValue value1 = new CategoryOrEntityValue(GenericEntityType.forName("product"), true, 1);
		CategoryOrEntityValue value2 = new CategoryOrEntityValue(GenericEntityType.forName("product"), true, 2);
		CategoryOrEntityValues value = new CategoryOrEntityValues();
		value.add(value1);
		value.add(value2);

		CategoryOrEntityValue value3 = new CategoryOrEntityValue(GenericEntityType.forName("product"), false, 1);
		CategoryOrEntityValue value4 = new CategoryOrEntityValue(GenericEntityType.forName("product"), false, 100);
		value.add(value3);
		value.add(value4);

		String expectedValue = VARIABLE + " &:(pe:entity-match :type product :id " + VARIABLE
				+ " :time-slice ?time-slice :category (build$ 1 100) :entity (build$ 1 2))";
		testFormatForPattern(expectedValue, value, Condition.OP_ENTITY_MATCH_FUNC, false, null);
	}

	protected void setUp() throws Exception {
		super.setUp();
		EntityManager.getInstance().addGenericEntityCategory(GenericEntityType.forName("product").getCategoryType(), 1, "Root Category");
		EntityManager.getInstance().addGenericEntityCategory(GenericEntityType.forName("product").getCategoryType(), 100, "Category 100");
		EntityManager.getInstance().addParentAssociation(GenericEntityType.forName("product").getCategoryType(), 100, 1, -1, -1);
		operatorHelper = createOperatorHelper("com.mindbox.pe.server.generator.value.EntityMatchFunctionOperatorHelper", TemplateUsageType
				.getAllInstances()[0]);
	}

	protected void tearDown() throws Exception {
		EntityManager.getInstance().startLoading();
		super.tearDown();
	}
}

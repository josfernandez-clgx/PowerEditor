package com.mindbox.pe.server.generator.value;

import static com.mindbox.pe.server.ServerTestObjectMother.createGenericCategory;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.AbstractTestWithGenericEntityType;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

public class ContextElementValueSlotHelperTest extends AbstractTestWithGenericEntityType {

	private static final String TEST_VAR_NAME = "?var";

	private GenericCategory genericCategory;
	private ContextElementValueSlotHelper contextElementValueSlotHelper;

	@Test
	public void testAppendContextMatchFunctionPatternAsOfWithEmptyCategoryIDsHappyCase() throws Exception {
		testAppendContextMatchFunctionPatternAsOf("", entityType, null, null, false);
		testAppendContextMatchFunctionPatternAsOf("", entityType, new int[0], null, false);
	}

	@Test
	public void testAppendContextMatchFunctionPatternAsOfWithCategoryIDsAndTrueHappyCase() throws Exception {
		String expected = " &:(pe:entity-match :type " + entityType.toString() + " :id " + TEST_VAR_NAME + " :time-slice " + RuleGeneratorHelper.TIME_SLICE_VARIABLE + " :category (build$ "
				+ genericCategory.getID() + ")" + ")";
		testAppendContextMatchFunctionPatternAsOf(expected, entityType, new int[] { genericCategory.getID() }, null, false);
	}

	@Test
	public void testAppendContextMatchFunctionPatternAsOfWithCategoryIDsAndFalseHappyCase() throws Exception {
		String expected = " &:(pe:entity-match :type " + entityType.toString() + " :id " + TEST_VAR_NAME + " :time-slice " + RuleGeneratorHelper.TIME_SLICE_VARIABLE + " :category (build$ "
				+ genericCategory.getID() + ")" + " :exclude-flag t" + ")";
		testAppendContextMatchFunctionPatternAsOf(expected, entityType, new int[] { genericCategory.getID() }, null, true);
	}

	@Test
	public void testAppendContextMatchFunctionPatternAsOfWithCategoryAndEntityIDsAndTrueHappyCase() throws Exception {
		String expected = " &:(pe:entity-match :type " + entityType.toString() + " :id " + TEST_VAR_NAME + " :time-slice " + RuleGeneratorHelper.TIME_SLICE_VARIABLE + " :category (build$ "
				+ genericCategory.getID() + ")" + " :entity (build$ 100)" + ")";
		testAppendContextMatchFunctionPatternAsOf(expected, entityType, new int[] { genericCategory.getID() }, new int[] { 100 }, false);
	}

	@Test
	public void testAppendContextMatchFunctionPatternAsOfWithCategoryAndEntityIDsAndFalseHappyCase() throws Exception {
		String expected = " &:(pe:entity-match :type " + entityType.toString() + " :id " + TEST_VAR_NAME + " :time-slice " + RuleGeneratorHelper.TIME_SLICE_VARIABLE + " :category (build$ "
				+ genericCategory.getID() + ")" + " :entity (build$ 100)" + " :exclude-flag t" + ")";
		testAppendContextMatchFunctionPatternAsOf(expected, entityType, new int[] { genericCategory.getID() }, new int[] { 100 }, true);
	}

	private void testAppendContextMatchFunctionPatternAsOf(String expected, GenericEntityType entityType, int[] categoryIDs, int[] entityIDs, boolean exclude) throws Exception {
		StringBuilder valueBuff = new StringBuilder();
		StringBuilder commentBuff = new StringBuilder();
		contextElementValueSlotHelper.appendContextMatchFunctionPatternAsOf(valueBuff, commentBuff, TEST_VAR_NAME, entityType, categoryIDs, entityIDs, exclude, new Date());
		String actual = valueBuff.toString();
		assertEquals(expected, actual);
	}

	public void setUp() throws Exception {
		super.setUp();
		contextElementValueSlotHelper = new ContextElementValueSlotHelper(null);

		genericCategory = createGenericCategory(entityType);
		int parentID = genericCategory.getID();
		EntityManager.getInstance().addGenericEntityCategory(entityType.getCategoryType(), genericCategory.getID(), genericCategory.getName());
		genericCategory = createGenericCategory(entityType);
		EntityManager.getInstance().addGenericEntityCategory(entityType.getCategoryType(), genericCategory.getID(), genericCategory.getName());
		EntityManager.getInstance().addParentAssociation(entityType.getCategoryType(), genericCategory.getID(), parentID, -1, -1);
	}

	public void tearDown() throws Exception {
		EntityManager.getInstance().startLoading();
		super.tearDown();
	}
}

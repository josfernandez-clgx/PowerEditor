package com.mindbox.pe.server.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.AbstractGrid;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.server.cache.EntityManager;

public class RuleGeneratorHelperTest extends AbstractTestWithGenericEntityType {


	public static TestSuite suite() {
		TestSuite suite = new TestSuite("RuleGeneratorHelper Tests");
		suite.addTestSuite(RuleGeneratorHelperTest.class);
		return suite;
	}

	private GenericCategory genericCategory;

	public RuleGeneratorHelperTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		genericCategory = ObjectMother.createGenericCategory(entityType);
		int parentID = genericCategory.getID();
		EntityManager.getInstance().addGenericEntityCategory(
				entityType.getCategoryType(),
				genericCategory.getID(),
				genericCategory.getName());
		genericCategory = ObjectMother.createGenericCategory(entityType);
		EntityManager.getInstance().addGenericEntityCategory(
				entityType.getCategoryType(),
				genericCategory.getID(),
				genericCategory.getName());
		EntityManager.getInstance().addParentAssociation(entityType.getCategoryType(), genericCategory.getID(), parentID, -1, -1);
	}

	protected void tearDown() throws Exception {
		EntityManager.getInstance().startLoading();
		super.tearDown();
	}

	public void testAppendFormattedForStringTypeWithNullBuffThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				RuleGeneratorHelper.class,
				"appendFormattedForStringType",
				new Class[] { StringBuffer.class, String.class, boolean.class },
				new Object[] { null, "str", Boolean.TRUE });
	}

	public void testAppendFormattedForStringTypeHappyCaseWithAsString() throws Exception {
		String str = ObjectMother.createString();
		// test quotes are added
		testAppendFormattedForStringType(str, true, "\"" + str + "\"");

		// test quotes are preserved
		testAppendFormattedForStringType("\"" + str + "\"", true, "\"" + str + "\"");
	}
	
	public void testAppendFormattedForStringTypeHappyCaseWithStringWithQuotes() throws Exception {
		String str = "test \"string\"";
		// test quotes are added
		testAppendFormattedForStringType(str, true, "\"test \\\"string\\\"\"");
	}
	
	public void testAppendFormattedForStringTypeHappyCaseWithNull() throws Exception {
		testAppendFormattedForStringType(null, true, RuleGeneratorHelper.AE_NIL);
		testAppendFormattedForStringType(null, false, RuleGeneratorHelper.AE_NIL);
	}

	public void testAppendFormattedForStringTypeHappyCaseWithNotAsString() throws Exception {
		String str = ObjectMother.createString();
		// test quotes are not added
		testAppendFormattedForStringType(str, false, str);

		// test quotes are stripped
		testAppendFormattedForStringType("\"" + str + "\"", false, str);
	}

	private void testAppendFormattedForStringType(String value, boolean asString, String expectedStr) throws Exception {
		StringBuffer buff = new StringBuffer();
		RuleGeneratorHelper.appendFormattedForStringType(buff, value, asString);
		assertEquals(expectedStr, buff.toString());
	}

	public void testFormatForContextMatchFunctionCategoryArgWithNullListThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				RuleGeneratorHelper.class,
				"formatForContextMatchFunctionCategoryArg",
				new Class[] { List.class },
				new Object[] { null });
	}

	public void testFormatForContextMatchFunctionCategoryArgWithEmptyArrayReturnsEmptyString() throws Exception {
		assertEquals("", RuleGeneratorHelper.formatForContextMatchFunctionCategoryArg(new ArrayList<Collection<Integer>>()));
	}

	public void testFormatForContextMatchFunctionCategoryArgHappyCaseSingleList() throws Exception {
		List<Integer> list = new ArrayList<Integer>();
		list.add(new Integer(100));
		List<Collection<Integer>> groupList = new ArrayList<Collection<Integer>>();
		groupList.add(list);
		assertEquals("(build$ (100))", RuleGeneratorHelper.formatForContextMatchFunctionCategoryArg(groupList));
	}

	public void testFormatForContextMatchFunctionCategoryArgHappyCaseMultipleList() throws Exception {
		List<Integer> list = new ArrayList<Integer>();
		list.add(new Integer(100));
		list.add(new Integer(120));
		List<Collection<Integer>> groupList = new ArrayList<Collection<Integer>>();
		groupList.add(list);
		list = new ArrayList<Integer>();
		list.add(new Integer(200));
		list.add(new Integer(300));
		groupList.add(list);
		list = new ArrayList<Integer>();
		list.add(new Integer(400));
		groupList.add(list);
		assertEquals("(build$ (100 120) (200 300) (400))", RuleGeneratorHelper.formatForContextMatchFunctionCategoryArg(groupList));
	}

	public void testFormatForExcludedObject_EmpytStringOnNull() throws Exception {
		assertEquals("formatForExcludedObject() didn't return empty string", "", RuleGeneratorHelper.formatForExcludedObject(null));
	}

	public void testFormatForExcludedObject_MultipleArgsWithSpace() throws Exception {
		assertEquals("& ~?obj1 & ~?obj2", RuleGeneratorHelper.formatForExcludedObject("obj1 & obj2"));

		assertEquals("& ~?obj1 & ~?obj2 & ~?obj3", RuleGeneratorHelper.formatForExcludedObject("& obj1 & obj2 & obj3"));
	}

	public void testFormatForExcludedObject_MultipleArgsWithoutSpace() throws Exception {
		assertEquals("& ~?obj1 & ~?obj2", RuleGeneratorHelper.formatForExcludedObject("obj1&obj2 "));

		assertEquals("& ~?obj1 & ~?obj2 & ~?obj3", RuleGeneratorHelper.formatForExcludedObject(" obj1 &obj2&obj3"));
	}

	public void testFormatForExcludedObject_SingleArg() throws Exception {
		assertEquals("& ~?test", RuleGeneratorHelper.formatForExcludedObject("test"));
	}

	public void testFormatForSprintfOpeningQuoteNotEscaped() throws Exception {
		assertEquals("\"ccc", RuleGeneratorHelper.formatForSprintf("\"ccc"));
	}

	public void testFormatForSprintfClosingQuoteEscaped() throws Exception {
		assertEquals("ccc\\\"", RuleGeneratorHelper.formatForSprintf("ccc\"")); // passes
	}

	public void testBackslashesAreEscaped() throws Exception {
		assertEquals("ccc\\\\ccc", RuleGeneratorHelper.formatForSprintf("ccc\\ccc"));
	}

	public void testQuotesAreEscaped() throws Exception {
		assertEquals("ccc\\\"ccc", RuleGeneratorHelper.formatForSprintf("ccc\"ccc"));
	}

	public void testBackslashFollowedByQuoteIsNotEscaped() throws Exception {
		assertEquals("ccc\\\"ccc", RuleGeneratorHelper.formatForSprintf("ccc\\\"ccc"));
	}

	public void testPercentAnyNotEscaped() throws Exception {
		assertEquals("ccc %a ccc", RuleGeneratorHelper.formatForSprintf("ccc %a ccc"));
	}

	public void testPercentStringNotEscaped() throws Exception {
		assertEquals("ccc %s ccc", RuleGeneratorHelper.formatForSprintf("ccc %s ccc"));
	}

	public void testPercentFloatWidthNotEscaped() throws Exception {
		assertEquals("ccc %.5321f ccc", RuleGeneratorHelper.formatForSprintf("ccc %.5321f ccc"));
	}

	public void testPercentCharEscaped() throws Exception {
		char[] followingChar = "bcdefghijklmnopqrtuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 !@#$^&*(){}[]<>,.?/=+-_~`\b\t'\r".toCharArray();
		for (int i = 0; i < followingChar.length; i++) {
			assertEquals("ccc %%" + followingChar[i] + " ccc", RuleGeneratorHelper.formatForSprintf("ccc %" + followingChar[i] + " ccc"));
		}
	}

	public void testGenerateContextSequence() throws Exception {
		String expectedResults = "(build$ product category 1)(build$ product category 2)";
		assertEquals(expectedResults, RuleGeneratorHelper.generateContextSequence("product", "category", new int[] { 1, 2 }).toString());
	}

	public void testHasSameEffectiveAndExpirationDatesWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerExceptionWithNullArgs(
				RuleGeneratorHelper.class,
				"hasSameEffectiveAndExpirationDates",
				new Class[] { AbstractGrid.class });
	}

	public void testHasSameEffectiveAndExpirationDatesPositiveCase() throws Exception {
		ParameterGrid grid = ObjectMother.createParameterGrid();
		grid.setEffectiveDate(ObjectMother.createDateSynonym());
		grid.setExpirationDate(grid.getEffectiveDate());
		assertTrue(RuleGeneratorHelper.hasSameEffectiveAndExpirationDates(grid));
	}

	public void testHasSameEffectiveAndExpirationDatesWithNoEffDateReturnsFalse() throws Exception {
		ParameterGrid grid = ObjectMother.createParameterGrid();
		grid.setEffectiveDate(null);
		grid.setExpirationDate(null);
		assertFalse(RuleGeneratorHelper.hasSameEffectiveAndExpirationDates(grid));
		// should still return false with expiration date with no effective date
		grid.setExpirationDate(ObjectMother.createDateSynonym());
		assertFalse(RuleGeneratorHelper.hasSameEffectiveAndExpirationDates(grid));
	}

	public void testHasSameEffectiveAndExpirationDatesWithNoExpDateReturnsFalse() throws Exception {
		ParameterGrid grid = ObjectMother.createParameterGrid();
		grid.setEffectiveDate(ObjectMother.createDateSynonym());
		grid.setExpirationDate(null);
		assertFalse(RuleGeneratorHelper.hasSameEffectiveAndExpirationDates(grid));
	}
}

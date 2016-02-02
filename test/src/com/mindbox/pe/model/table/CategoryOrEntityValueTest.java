package com.mindbox.pe.model.table;

import java.io.Serializable;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.GenericEntityType;

public class CategoryOrEntityValueTest extends AbstractTestWithGenericEntityType {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("CategoryOrEntityValueTest Tests");
		suite.addTestSuite(CategoryOrEntityValueTest.class);
		return suite;
	}

	public CategoryOrEntityValueTest(String name) {
		super(name);
	}

	public void testImplementsSerializable() throws Exception {
		Serializable.class.isAssignableFrom(CategoryOrEntityValue.class);
	}

	public void testValueOfWithFalseFalseThrowsIllegalArgumentException() throws Exception {
		try {
			CategoryOrEntityValue.valueOf(null, "", false, false);
			fail("Expected IllegalArgumentException not thrown");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	public void testValueOfWithNullEntityTypeThrowsNullPointerException() throws Exception {
		try {
			CategoryOrEntityValue.valueOf("s", null, false, false);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException e) {
			// expected
		}
	}

	public void testValueOfWithNullOrEmptyStringReturnsNull() throws Exception {
		assertNull(CategoryOrEntityValue.valueOf(null, entityType.toString(), true, true));
		assertNull(CategoryOrEntityValue.valueOf("", entityType.toString(), true, true));
	}

	public void testValueOfWithInvalidStringReturnsNull() throws Exception {
		assertNull(CategoryOrEntityValue.valueOf(":true:1", entityType.toString(), true, true));
		assertNull(CategoryOrEntityValue.valueOf("product:true:1", entityType.toString(), true, true));
		assertNull(CategoryOrEntityValue.valueOf(entityType.toString() + ":true", entityType.toString(), true, true));
		assertNull(CategoryOrEntityValue.valueOf(entityType.toString() + "::", entityType.toString(), true, true));
		assertNull(CategoryOrEntityValue.valueOf(entityType.toString() + "::10s", entityType.toString(), true, true));
	}

	public void testValueOfWithIncorrectEntityCategoryCheckReturnsNull() throws Exception {
		assertNull(CategoryOrEntityValue.valueOf(entityType.toString() + ":true:100", entityType.toString(), false, true));
		assertNull(CategoryOrEntityValue.valueOf(entityType.toString() + ":false:100", entityType.toString(), true, false));
	}

	public void testValueOfWithIncorrectEntityTypeReturnsNull() throws Exception {
		assertNull(CategoryOrEntityValue.valueOf("product" + ":true:100", entityType.toString(), false, true));
		assertNull(CategoryOrEntityValue.valueOf("product" + ":false:100", entityType.toString(), true, false));
	}

	public void testValueOfHappyCases() throws Exception {
		CategoryOrEntityValue value = new CategoryOrEntityValue(entityType, true, 100);
		assertEquals(value, CategoryOrEntityValue.valueOf(value.toString(), entityType.toString(), true, true));
		value = new CategoryOrEntityValue(entityType, false, 1245);
		assertEquals(value, CategoryOrEntityValue.valueOf(value.toString(), entityType.toString(), true, true));
	}

	public void testAsStringHappyCase() throws Exception {
		int id = ObjectMother.createInt();
		assertEquals(entityType.getName() + ":false:" + id, CategoryOrEntityValue.asString(entityType, false, id));
		assertEquals(entityType.getName() + ":true:" + id, CategoryOrEntityValue.asString(entityType, true, id));
	}

	public void testAsStringWithNullEntityTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				CategoryOrEntityValue.class,
				"asString",
				new Class[] { GenericEntityType.class, boolean.class, int.class },
				new Object[] { null, Boolean.TRUE, new Integer(0) });
	}

	protected void setUp() throws Exception {
		super.setUp();
		// Set up for CategoryOrEntityValueTest
	}

	protected void tearDown() throws Exception {
		// Tear downs for CategoryOrEntityValueTest
		super.tearDown();
	}
}

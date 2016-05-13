package com.mindbox.pe.model.table;

import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.Serializable;

import org.junit.Test;

import com.mindbox.pe.common.AbstractTestWithGenericEntityType;
import com.mindbox.pe.model.GenericEntityType;

public class CategoryOrEntityValueTest extends AbstractTestWithGenericEntityType {

	@Test
	public void testAsStringHappyCase() throws Exception {
		int id = createInt();
		assertEquals(entityType.getName() + ":false:" + id, CategoryOrEntityValue.asString(entityType, false, id));
		assertEquals(entityType.getName() + ":true:" + id, CategoryOrEntityValue.asString(entityType, true, id));
	}

	@Test
	public void testAsStringWithNullEntityTypeThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				CategoryOrEntityValue.class,
				"asString",
				new Class[] { GenericEntityType.class, boolean.class, int.class },
				new Object[] { null, Boolean.TRUE, new Integer(0) });
	}

	@Test
	public void testImplementsSerializable() throws Exception {
		Serializable.class.isAssignableFrom(CategoryOrEntityValue.class);
	}

	@Test
	public void testValueOfHappyCases() throws Exception {
		CategoryOrEntityValue value = new CategoryOrEntityValue(entityType, true, 100);
		assertEquals(value, CategoryOrEntityValue.valueOf(value.toString(), entityType.toString(), true, true));
		value = new CategoryOrEntityValue(entityType, false, 1245);
		assertEquals(value, CategoryOrEntityValue.valueOf(value.toString(), entityType.toString(), true, true));
	}

	@Test
	public void testValueOfWithFalseFalseThrowsIllegalArgumentException() throws Exception {
		try {
			CategoryOrEntityValue.valueOf(null, "", false, false);
			fail("Expected IllegalArgumentException not thrown");
		}
		catch (IllegalArgumentException e) {
			// expected
		}
	}

	@Test
	public void testValueOfWithIncorrectEntityCategoryCheckReturnsNull() throws Exception {
		assertNull(CategoryOrEntityValue.valueOf(entityType.toString() + ":true:100", entityType.toString(), false, true));
		assertNull(CategoryOrEntityValue.valueOf(entityType.toString() + ":false:100", entityType.toString(), true, false));
	}

	@Test
	public void testValueOfWithIncorrectEntityTypeReturnsNull() throws Exception {
		assertNull(CategoryOrEntityValue.valueOf("product" + ":true:100", entityType.toString(), false, true));
		assertNull(CategoryOrEntityValue.valueOf("product" + ":false:100", entityType.toString(), true, false));
	}

	@Test
	public void testValueOfWithInvalidStringReturnsNull() throws Exception {
		assertNull(CategoryOrEntityValue.valueOf(":true:1", entityType.toString(), true, true));
		assertNull(CategoryOrEntityValue.valueOf("product:true:1", entityType.toString(), true, true));
		assertNull(CategoryOrEntityValue.valueOf(entityType.toString() + ":true", entityType.toString(), true, true));
		assertNull(CategoryOrEntityValue.valueOf(entityType.toString() + "::", entityType.toString(), true, true));
		assertNull(CategoryOrEntityValue.valueOf(entityType.toString() + "::10s", entityType.toString(), true, true));
	}

	@Test
	public void testValueOfWithNullEntityTypeThrowsNullPointerException() throws Exception {
		try {
			CategoryOrEntityValue.valueOf("s", null, false, false);
			fail("Expected NullPointerException not thrown");
		}
		catch (NullPointerException e) {
			// expected
		}
	}

	@Test
	public void testValueOfWithNullOrEmptyStringReturnsNull() throws Exception {
		assertNull(CategoryOrEntityValue.valueOf(null, entityType.toString(), true, true));
		assertNull(CategoryOrEntityValue.valueOf("", entityType.toString(), true, true));
	}

}

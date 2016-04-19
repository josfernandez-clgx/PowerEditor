package com.mindbox.pe.model.table;

import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class DynamicStringValueTest extends AbstractTestBase {

	@Test
	public void testImplementsSerializable() throws Exception {
		Serializable.class.isAssignableFrom(DynamicStringValue.class);
	}

	@Test
	public void testReplaceColumnReferencesColumnNotFoundCase() throws Exception {
		final String value = "value %column 1% and %column 2% value: " + System.currentTimeMillis();
		final DynamicStringValue dynamicStringValue = new DynamicStringValue(value);
		assertFalse(dynamicStringValue.replaceColumnReferences(new HashMap<Integer, Integer>()));
		assertEquals(value, dynamicStringValue.toString());
	}

	@Test
	public void testReplaceColumnReferencesHappyCase() throws Exception {
		final String value = "value %column 1% and %column 2% value:";
		final DynamicStringValue dynamicStringValue = new DynamicStringValue(value);

		final Map<Integer, Integer> columnsMap = new HashMap<Integer, Integer>();
		columnsMap.put(1, 2);
		columnsMap.put(2, createInt());
		assertTrue(dynamicStringValue.replaceColumnReferences(columnsMap));
		assertEquals(String.format("value %%column 2%% and %%column %d%% value:", columnsMap.get(2)), dynamicStringValue.toString());
	}

	@Test
	public void testReplaceColumnReferencesNoColumnRefCase() throws Exception {
		final String value = "value and another value: " + System.currentTimeMillis();
		final DynamicStringValue dynamicStringValue = new DynamicStringValue(value);
		assertFalse(dynamicStringValue.replaceColumnReferences(new HashMap<Integer, Integer>()));
		assertEquals(value, dynamicStringValue.toString());
	}

	@Test
	public void testToStringReturnsStringValue() throws Exception {
		String str = "dynamic string text " + createInt();
		DynamicStringValue dsValue = DynamicStringValue.parseValue(str);
		assertEquals(str, dsValue.toString());
	}
}

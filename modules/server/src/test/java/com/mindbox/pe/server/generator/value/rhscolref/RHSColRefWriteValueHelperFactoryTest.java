package com.mindbox.pe.server.generator.value.rhscolref;

import static com.mindbox.pe.server.ServerTestObjectMother.createEnumValue;
import static com.mindbox.pe.server.ServerTestObjectMother.createEnumValues;
import static com.mindbox.pe.server.ServerTestObjectMother.createIntegerRange;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.table.DynamicStringValue;
import com.mindbox.pe.unittest.AbstractTestBase;

public class RHSColRefWriteValueHelperFactoryTest extends AbstractTestBase {

	private void testGetRHSColRefWriteValueHelper(Object value, Class<?> expectedClass) {
		assertEquals(expectedClass, RHSColRefWriteValueHelperFactory.getInstance().getRHSColRefWriteValueHelper(value).getClass());
	}

	@Test
	public void testGetRHSColRefWriteValueHelperWithBooleanHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(Boolean.FALSE, DefaultValueHelper.class);
	}

	@Test
	public void testGetRHSColRefWriteValueHelperWithCategoryOrEntityValueHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(new CategoryOrEntityValue(), CategoryOrEntityValueValueHelper.class);
	}

	@Test
	public void testGetRHSColRefWriteValueHelperWithCategoryOrEntityValuesHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(new CategoryOrEntityValues(), CategoryOrEntityValuesValueHelper.class);
	}

	@Test
	public void testGetRHSColRefWriteValueHelperWithDateHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(new Date(), DateValueHelper.class);
	}

	@Test
	public void testGetRHSColRefWriteValueHelperWithDoubleHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(new Double(createInt()), DoubleValueHelper.class);
	}

	@Test
	public void testGetRHSColRefWriteValueHelperWithDynamicStringValueHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(new DynamicStringValue(""), DynamicStringValueHelper.class);
	}

	@Test
	public void testGetRHSColRefWriteValueHelperWithEnumValueHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(createEnumValue(), EnumValueHelper.class);
	}

	@Test
	public void testGetRHSColRefWriteValueHelperWithEnumValuesHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(createEnumValues(), EnumValuesHelper.class);
	}

	@Test
	public void testGetRHSColRefWriteValueHelperWithFloatHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(new Float(createInt()), FloatValueHelper.class);
	}

	@Test
	public void testGetRHSColRefWriteValueHelperWithIntegerHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(new Integer(createInt()), IntegerValueHelper.class);
	}

	@Test
	public void testGetRHSColRefWriteValueHelperWithIRangeHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(createIntegerRange(1, 2), IRangeValueHelper.class);
	}

	@Test
	public void testGetRHSColRefWriteValueHelperWithStringHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(createString(), DefaultValueHelper.class);
	}

}

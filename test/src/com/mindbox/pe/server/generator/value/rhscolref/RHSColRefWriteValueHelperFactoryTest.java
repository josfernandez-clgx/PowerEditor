package com.mindbox.pe.server.generator.value.rhscolref;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.table.DynamicStringValue;

public class RHSColRefWriteValueHelperFactoryTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("RHSColRefWriteValueHelperFactoryTest Tests");
		suite.addTestSuite(RHSColRefWriteValueHelperFactoryTest.class);
		return suite;
	}

	public RHSColRefWriteValueHelperFactoryTest(String name) {
		super(name);
	}

	public void testGetRHSColRefWriteValueHelperWithBooleanHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(Boolean.FALSE, DefaultValueHelper.class);
	}

	public void testGetRHSColRefWriteValueHelperWithCategoryOrEntityValueHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(new CategoryOrEntityValue(), CategoryOrEntityValueValueHelper.class);
	}

	public void testGetRHSColRefWriteValueHelperWithCategoryOrEntityValuesHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(new CategoryOrEntityValues(), CategoryOrEntityValuesValueHelper.class);
	}

	public void testGetRHSColRefWriteValueHelperWithDateHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(new Date(), DateValueHelper.class);
	}

	public void testGetRHSColRefWriteValueHelperWithDoubleHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(new Double(ObjectMother.createInt()), DoubleValueHelper.class);
	}

	public void testGetRHSColRefWriteValueHelperWithDynamicStringValueHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(new DynamicStringValue(), DynamicStringValueHelper.class);
	}

	public void testGetRHSColRefWriteValueHelperWithEnumValueHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(ObjectMother.createEnumValue(), EnumValueHelper.class);
	}

	public void testGetRHSColRefWriteValueHelperWithEnumValuesHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(ObjectMother.createEnumValues(), EnumValuesHelper.class);
	}

	public void testGetRHSColRefWriteValueHelperWithFloatHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(new Float(ObjectMother.createInt()), FloatValueHelper.class);
	}

	public void testGetRHSColRefWriteValueHelperWithIntegerHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(new Integer(ObjectMother.createInt()), IntegerValueHelper.class);
	}

	public void testGetRHSColRefWriteValueHelperWithIRangeHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(ObjectMother.createIntegerRange(1, 2), IRangeValueHelper.class);
	}

	public void testGetRHSColRefWriteValueHelperWithStringHappyCase() throws Exception {
		testGetRHSColRefWriteValueHelper(ObjectMother.createString(), DefaultValueHelper.class);
	}

	private void testGetRHSColRefWriteValueHelper(Object value, Class<?> expectedClass) {
		assertEquals(expectedClass, RHSColRefWriteValueHelperFactory.getInstance().getRHSColRefWriteValueHelper(value).getClass());
	}

}

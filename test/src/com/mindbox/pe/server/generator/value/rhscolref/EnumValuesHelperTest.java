package com.mindbox.pe.server.generator.value.rhscolref;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.table.EnumValues;

public class EnumValuesHelperTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("EnumValuesHelperTest Tests");
		suite.addTestSuite(EnumValuesHelperTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public EnumValuesHelperTest(String name) {
		super(name);
	}

	public void testWriteValueWithEmptyEnumValuesWriteNothing() throws Exception {
		testWriteValue("", ObjectMother.createEnumValues(), true, true);
		testWriteValue("", ObjectMother.createEnumValues(), true, false);
		testWriteValue("", ObjectMother.createEnumValues(), false, true);
		testWriteValue("", ObjectMother.createEnumValues(), false, false);
	}

	public void testWriteValueForEnumValueEnumValuesWithAddQuotesEnumAsSeqHappyCase() throws Exception {
		EnumValues<EnumValue> enumValues = ObjectMother.attachEnumValue(ObjectMother.createEnumValues(), 1);
		testWriteValue("(create$ \"" + enumValues.get(0).getDeployValue() + "\")", enumValues, true, true);
	}

	public void testWriteValueForEnumValueEnumValuesWithNoAddQuotesEnumAsSeqHappyCase() throws Exception {
		EnumValues<EnumValue> enumValues = ObjectMother.attachEnumValue(ObjectMother.createEnumValues(), 1);
		testWriteValue("(create$ " + enumValues.get(0).getDeployValue() + ")", enumValues, false, true);
	}

	public void testWriteValueForEnumValueEnumValuesWithAddQuotesNoEnumAsSeqHappyCase() throws Exception {
		EnumValues<EnumValue> enumValues = ObjectMother.attachEnumValue(ObjectMother.createEnumValues(), 2);
		testWriteValue(
				"\"" + enumValues.get(0).getDeployValue() + " | " + enumValues.get(1).getDeployValue() + "\"",
				enumValues,
				true,
				false);
	}

	public void testWriteValueForEnumValueEnumValuesWithNoAddQuotesNoEnumAsSeqHappyCase() throws Exception {
		EnumValues<EnumValue> enumValues = ObjectMother.attachEnumValue(ObjectMother.createEnumValues(), 2);
		testWriteValue(enumValues.get(0).getDeployValue() + " | " + enumValues.get(1).getDeployValue(), enumValues, false, false);
	}

	private void testWriteValue(String expectedValue, EnumValues<?> enumValues, boolean addQuotes, boolean multiEnumAsSequence)
			throws Exception {
		StringBuilder buff = new StringBuilder();
		GridTemplateColumn column = ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(
				1,
				ObjectMother.createUsageType()));
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		enumValuesHelper.writeValue(buff, enumValues, column, addQuotes, multiEnumAsSequence);
		assertEquals(expectedValue, buff.toString());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.enumValuesHelper = new EnumValuesHelper();
	}

	private EnumValuesHelper enumValuesHelper;
}

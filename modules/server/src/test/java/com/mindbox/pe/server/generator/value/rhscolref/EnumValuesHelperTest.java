package com.mindbox.pe.server.generator.value.rhscolref;

import static com.mindbox.pe.server.ServerTestObjectMother.attachColumnDataSpecDigest;
import static com.mindbox.pe.server.ServerTestObjectMother.attachEnumValue;
import static com.mindbox.pe.server.ServerTestObjectMother.createEnumValues;
import static com.mindbox.pe.server.ServerTestObjectMother.createGridTemplateColumn;
import static com.mindbox.pe.server.ServerTestObjectMother.createUsageType;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.unittest.AbstractTestBase;

public class EnumValuesHelperTest extends AbstractTestBase {

	private EnumValuesHelper enumValuesHelper;

	@Before
	public void setUp() throws Exception {
		this.enumValuesHelper = new EnumValuesHelper();
	}

	private void testWriteValue(String expectedValue, EnumValues<?> enumValues, boolean addQuotes, boolean multiEnumAsSequence) throws Exception {
		StringBuilder buff = new StringBuilder();
		GridTemplateColumn column = attachColumnDataSpecDigest(createGridTemplateColumn(1, createUsageType()));
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		enumValuesHelper.writeValue(buff, enumValues, column, addQuotes, multiEnumAsSequence);
		assertEquals(expectedValue, buff.toString());
	}

	@Test
	public void testWriteValueForEnumValueEnumValuesWithAddQuotesEnumAsSeqHappyCase() throws Exception {
		EnumValues<EnumValue> enumValues = attachEnumValue(createEnumValues(), 1);
		testWriteValue("(create$ \"" + enumValues.get(0).getDeployValue() + "\")", enumValues, true, true);
	}

	@Test
	public void testWriteValueForEnumValueEnumValuesWithAddQuotesNoEnumAsSeqHappyCase() throws Exception {
		EnumValues<EnumValue> enumValues = attachEnumValue(createEnumValues(), 2);
		testWriteValue("\"" + enumValues.get(0).getDeployValue() + " | " + enumValues.get(1).getDeployValue() + "\"", enumValues, true, false);
	}

	@Test
	public void testWriteValueForEnumValueEnumValuesWithNoAddQuotesEnumAsSeqHappyCase() throws Exception {
		EnumValues<EnumValue> enumValues = attachEnumValue(createEnumValues(), 1);
		testWriteValue("(create$ " + enumValues.get(0).getDeployValue() + ")", enumValues, false, true);
	}

	@Test
	public void testWriteValueForEnumValueEnumValuesWithNoAddQuotesNoEnumAsSeqHappyCase() throws Exception {
		EnumValues<EnumValue> enumValues = attachEnumValue(createEnumValues(), 2);
		testWriteValue(enumValues.get(0).getDeployValue() + " | " + enumValues.get(1).getDeployValue(), enumValues, false, false);
	}

	@Test
	public void testWriteValueWithEmptyEnumValuesWriteNothing() throws Exception {
		testWriteValue("", createEnumValues(), true, true);
		testWriteValue("", createEnumValues(), true, false);
		testWriteValue("", createEnumValues(), false, true);
		testWriteValue("", createEnumValues(), false, false);
	}
}

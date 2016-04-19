package com.mindbox.pe.server.generator.value.rhscolref;

import static com.mindbox.pe.server.ServerTestObjectMother.attachColumnDataSpecDigest;
import static com.mindbox.pe.server.ServerTestObjectMother.createEnumValue;
import static com.mindbox.pe.server.ServerTestObjectMother.createGridTemplateColumn;
import static com.mindbox.pe.server.ServerTestObjectMother.createUsageType;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplateColumn;
import com.mindbox.pe.unittest.AbstractTestBase;

public class EnumValueHelperTest extends AbstractTestBase {

	private EnumValueHelper enumValueHelper;

	@Before
	public void setUp() throws Exception {
		this.enumValueHelper = new EnumValueHelper();
	}

	private void testWriteValue(String expectedValue, EnumValue enumValue, boolean addQuotes) throws Exception {
		StringBuilder buff = new StringBuilder();
		GridTemplateColumn column = attachColumnDataSpecDigest(createGridTemplateColumn(1, createUsageType()));
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		enumValueHelper.writeValue(buff, enumValue, column, addQuotes, true);
		assertEquals(expectedValue, buff.toString());
	}

	@Test
	public void testWriteValueWithAddQuotesHappyCase() throws Exception {
		EnumValue enumValue = createEnumValue();
		testWriteValue("\"" + enumValue.getDeployValue() + "\"", enumValue, true);
	}

	@Test
	public void testWriteValueWithNoAddQuotesHappyCase() throws Exception {
		EnumValue enumValue = createEnumValue();
		testWriteValue(enumValue.getDeployValue(), enumValue, false);
	}

}

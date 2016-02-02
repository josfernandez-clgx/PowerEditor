package com.mindbox.pe.server.generator.value.rhscolref;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.GridTemplateColumn;

public class EnumValueHelperTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("EnumValueHelperTest Tests");
		suite.addTestSuite(EnumValueHelperTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public EnumValueHelperTest(String name) {
		super(name);
	}

	public void testWriteValueWithAddQuotesHappyCase() throws Exception {
		EnumValue enumValue = ObjectMother.createEnumValue();
		testWriteValue("\"" + enumValue.getDeployValue() + "\"", enumValue, true);
	}

	public void testWriteValueWithNoAddQuotesHappyCase() throws Exception {
		EnumValue enumValue = ObjectMother.createEnumValue();
		testWriteValue(enumValue.getDeployValue(), enumValue, false);
	}

	private void testWriteValue(String expectedValue, EnumValue enumValue, boolean addQuotes) throws Exception {
		StringBuilder buff = new StringBuilder();
		GridTemplateColumn column = ObjectMother.attachColumnDataSpecDigest(ObjectMother.createGridTemplateColumn(
				1,
				ObjectMother.createUsageType()));
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		enumValueHelper.writeValue(buff, enumValue, column, addQuotes, true);
		assertEquals(expectedValue, buff.toString());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.enumValueHelper = new EnumValueHelper();
	}

	private EnumValueHelper enumValueHelper;


}

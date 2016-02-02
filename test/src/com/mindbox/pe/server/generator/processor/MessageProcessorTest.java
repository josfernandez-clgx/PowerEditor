package com.mindbox.pe.server.generator.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.config.MessageConfiguration;
import com.mindbox.pe.common.format.FloatFormatter;
import com.mindbox.pe.model.AbstractTemplateColumn;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.FloatDomainAttribute;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.GridTemplateColumn;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.table.DateRange;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.FloatRange;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.generator.AbstractGenerateParms;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Message;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Node;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.NodeListOptional;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.NodeToken;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Reference;

public class MessageProcessorTest extends AbstractTestWithTestConfig {
	private GridTemplateColumn gridColumn;
	private ColumnDataSpecDigest colDataSpec;
	private StringBuffer buffer;

	public static TestSuite suite() {
		TestSuite suite = new TestSuite(MessageProcessorTest.class.getName());
		suite.addTestSuite(MessageProcessorTest.class);
		return suite;
	}

	public MessageProcessorTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		config.initServer();

		gridColumn = new GridTemplateColumn();
		colDataSpec = new ColumnDataSpecDigest();
		gridColumn.setDataSpecDigest(colDataSpec);

		buffer = new StringBuffer();
	}

	protected void tearDown() throws Exception {
		config.resetConfiguration();
		EntityManager.getInstance().startLoading();
		super.tearDown();
	}

	public void testSimpleMsg() throws Exception {
		testProcess(Collections.singletonList(new NodeToken("A Simple Msg")), "A Simple Msg", new ArrayList<String>());
	}

	public void testAttributeReferenceWithNullTokenImage() throws Exception {
		Reference attributeRef = new Reference(null, new NodeToken(null), null);
		testProcess(Collections.singletonList(attributeRef), "", new ArrayList<String>());
	}

	public void testAttributeReferenceWithEmptyTokenImage() throws Exception {
		Reference attributeRef = new Reference(null, new NodeToken(""), null);
		testProcess(Collections.singletonList(attributeRef), "", new ArrayList<String>());
	}

	public void testAttributeReferenceWithUnexpectedTokenImageFormatThrowsIndexOutOfBounds() throws Exception {
		Reference attributeRef = new Reference(null, new NodeToken("Not 2 dot-separated elements"), null);
		try {
			testProcess(Collections.singletonList(attributeRef), "", new ArrayList<String>());
			fail("Expected " + IndexOutOfBoundsException.class.getName());
		}
		catch (IndexOutOfBoundsException e) {
			// pass
		}
	}

	public void testMultipartMsgWithAttributeReference() throws Exception {
		Reference attributeRef = createReferencedDomainAttribute("TestDomClass.TestDomAttr", DeployType.STRING);
		Node[] msgParts = new Node[] { new NodeToken("Pre attribute text "), attributeRef, new NodeToken(" Post attribute text") };

		testProcess(Arrays.asList(msgParts), "Pre attribute text %a Post attribute text", Collections.singletonList("?testdomattr"));
	}

	public void testDefaultAttrReference() throws Exception {
		Reference attributeRef = createReferencedDomainAttribute("TestDomClass.TestDomAttr", DeployType.STRING);
		testProcess(Collections.singletonList(attributeRef), "%a", Collections.singletonList("?testdomattr"));
	}

	public void testDateAttrReference() throws Exception {
		Reference attributeRef = createReferencedDomainAttribute("TestDomClass.TestDomAttr", DeployType.DATE);
		String dateFormatPattern = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateFormatAe();
		String expectedMsgArg = "(format-julian-date ?testdomattr \"" + dateFormatPattern + "\")";
		testProcess(Collections.singletonList(attributeRef), "%s", Collections.singletonList(expectedMsgArg));
	}

	public void testFloatAttrReferenceDefaultPrecision() throws Exception {
		Reference attributeRef = createReferencedDomainAttribute("TestDomClass.TestDomAttr", DeployType.FLOAT);
		testProcess(Collections.singletonList(attributeRef), "%.2f", Collections.singletonList("?testdomattr"));
	}

	public void testFloatAttrReference() throws Exception {
		Reference attributeRef = createReferencedDomainAttribute("TestDomClass.TestDomAttr", DeployType.FLOAT, 3);
		testProcess(Collections.singletonList(attributeRef), "%.3f", Collections.singletonList("?testdomattr"));
	}

	public void testPercentAttrReference() throws Exception {
		Reference attributeRef = createReferencedDomainAttribute("TestDomClass.TestDomAttr", DeployType.PERCENT, 0);
		testProcess(Collections.singletonList(attributeRef), "%.0f", Collections.singletonList("?testdomattr"));
	}

	public void testCurrencyAttrReference() throws Exception {
		Reference attributeRef = createReferencedDomainAttribute("TestDomClass.TestDomAttr", DeployType.CURRENCY, 1);
		testProcess(Collections.singletonList(attributeRef), "%.1f", Collections.singletonList("?testdomattr"));
	}

	// We should be able to replace all the testWriteXyz() tests below (which call writeVal directly)
	// with tests similar to those above (which call process and thus test the entire 'sprintf' statement creation).
	// But at the moment the configuration is a bit challenging...
	//	public void testProcessFloatColumnReference() throws Exception {
	//		ColumnLiteral colRef = new ColumnLiteral(null, new NodeToken("1"), null);
	//		testProcess(Collections.singletonList(colRef), "", Collections.EMPTY_LIST, createGenParmsWithColumnDataAndOtherConfigStuff());
	//	}

	public void testWriteFloat() throws Exception {
		testWriteNumberWithPrecision(new Float(1.11), 2, "1.11");
	}

	public void testWriteDouble() throws Exception {
		testWriteNumberWithPrecision(new Double(1.11), 2, "1.11");
	}

	public void testWriteFloatDelegatesToFloatFormatter() throws Exception {
		assertNotEquals(FloatFormatter.DEFAULT_PRECISION, 0); // sanity check

		testWriteNumberWithPrecision(new Float(1.11), 0, "1");
	}

	public void testWriteCurrencyDelegatesToCurrencyFormatter() throws Exception {
		testWriteCurrencyWithPrecision(new Float(1.11), 3, "$1.110");
	}

	public void testWriteFloatRangeDelegatesToFloatFormatter() throws Exception {
		testWriteFloatRangeWithPrecision(
				new Double("2.22"),
				new Double("4444.666666"),
				ColumnDataSpecDigest.TYPE_FLOAT_RANGE,
				1,
				"greater than or equal to 2.2 and less than or equal to 4,444.7");
	}

	public void testWriteCurrencyRangeDelegatesToCurrencyFormatter() throws Exception {
		testWriteFloatRangeWithPrecision(
				new Double("2.22"),
				new Double("4444.666666"),
				ColumnDataSpecDigest.TYPE_CURRENCY_RANGE,
				3,
				"greater than or equal to $2.220 and less than or equal to $4,444.667");
	}

	public void testWriteDate() throws Exception {
		Date d = new Date();
		String expectedString = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateFormat().format(d);
		MessageProcessor.writeVal(gridColumn, d, buffer, null, null);
		assertEquals(expectedString, buffer.toString());
	}

	public void testWriteDateRange_Equal() throws Exception {
		Date d = new Date();
		String expectedString = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(d);
		testWriteDateRange(d, d, true, MessageConfiguration.RANGE_BRACKETED_KEY, expectedString);
	}

	public void testWriteDateRange_Bracket_NotEqual_Inclusive() throws Exception {
		Date lo = new Date();
		Date hi = new Date(lo.getTime() + 86400000); // add a day (in millis)
		String expectedLoStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(lo);
		String expectedHiStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(hi);
		String expectedStr = '[' + expectedLoStr + '-' + expectedHiStr + ']';
		testWriteDateRange(lo, hi, true, MessageConfiguration.RANGE_BRACKETED_KEY, expectedStr);
	}

	public void testWriteDateRange_Bracket_NullHi_Inclusive() throws Exception {
		Date lo = new Date();
		String expectedLoStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(lo);
		String expectedStr = '[' + expectedLoStr + '-';
		testWriteDateRange(lo, null, true, MessageConfiguration.RANGE_BRACKETED_KEY, expectedStr);
	}

	public void testWriteDateRange_Bracket_NullLo_Inclusive() throws Exception {
		Date hi = new Date();
		String expectedHiStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(hi);
		String expectedStr = '-' + expectedHiStr + ']';
		testWriteDateRange(null, hi, true, MessageConfiguration.RANGE_BRACKETED_KEY, expectedStr);
	}

	public void testWriteDateRange_Bracket_NotEqual_Exclusive() throws Exception {
		Date lo = new Date();
		Date hi = new Date(lo.getTime() + 86400000); // add a day (in millis)
		String expectedLoStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(lo);
		String expectedHiStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(hi);
		String expectedStr = '(' + expectedLoStr + '-' + expectedHiStr + ')';
		testWriteDateRange(lo, hi, false, MessageConfiguration.RANGE_BRACKETED_KEY, expectedStr);
	}

	public void testWriteDateRange_Bracket_NullHi_Exclusive() throws Exception {
		Date lo = new Date();
		String expectedLoStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(lo);
		String expectedStr = '(' + expectedLoStr + '-';
		testWriteDateRange(lo, null, false, MessageConfiguration.RANGE_BRACKETED_KEY, expectedStr);
	}

	public void testWriteDateRange_Bracket_NullLo_Exclusive() throws Exception {
		Date hi = new Date();
		String expectedHiStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(hi);
		String expectedStr = '-' + expectedHiStr + ')';
		testWriteDateRange(null, hi, false, MessageConfiguration.RANGE_BRACKETED_KEY, expectedStr);
	}

	public void testWriteDateRange_Symbol_NotEqual_Inclusive() throws Exception {
		Date lo = new Date();
		Date hi = new Date(lo.getTime() + 86400000); // add a day (in millis)
		String expectedLoStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(lo);
		String expectedHiStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(hi);
		String expectedStr = ">=" + expectedLoStr + ",<=" + expectedHiStr;
		testWriteDateRange(lo, hi, true, MessageConfiguration.RANGE_SYMBOLIC_KEY, expectedStr);
	}

	public void testWriteDateRange_Symbol_NullHi_Inclusive() throws Exception {
		Date lo = new Date();
		String expectedLoStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(lo);
		String expectedStr = ">=" + expectedLoStr;
		testWriteDateRange(lo, null, true, MessageConfiguration.RANGE_SYMBOLIC_KEY, expectedStr);
	}

	public void testWriteDateRange_Symbol_NullLo_Inclusive() throws Exception {
		Date hi = new Date();
		String expectedHiStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(hi);
		String expectedStr = "<=" + expectedHiStr;
		testWriteDateRange(null, hi, true, MessageConfiguration.RANGE_SYMBOLIC_KEY, expectedStr);
	}

	public void testWriteDateRange_Symbol_NotEqual_Exclusive() throws Exception {
		Date lo = new Date();
		Date hi = new Date(lo.getTime() + 86400000); // add a day (in millis)
		String expectedLoStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(lo);
		String expectedHiStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(hi);
		String expectedStr = '>' + expectedLoStr + ",<" + expectedHiStr;
		testWriteDateRange(lo, hi, false, MessageConfiguration.RANGE_SYMBOLIC_KEY, expectedStr);
	}

	public void testWriteDateRange_Symbol_NullHi_Exclusive() throws Exception {
		Date lo = new Date();
		String expectedLoStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(lo);
		String expectedStr = '>' + expectedLoStr;
		testWriteDateRange(lo, null, false, MessageConfiguration.RANGE_SYMBOLIC_KEY, expectedStr);
	}

	public void testWriteDateRange_Symbol_NullLo_Exclusive() throws Exception {
		Date hi = new Date();
		String expectedHiStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(hi);
		String expectedStr = '<' + expectedHiStr;
		testWriteDateRange(null, hi, false, MessageConfiguration.RANGE_SYMBOLIC_KEY, expectedStr);
	}

	public void testWriteDateRange_Verbose_NotEqual_Inclusive() throws Exception {
		Date lo = new Date();
		Date hi = new Date(lo.getTime() + 86400000); // add a day (in millis)
		String expectedLoStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(lo);
		String expectedHiStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(hi);
		String expectedStr = "greater than or equal to " + expectedLoStr + " and less than or equal to " + expectedHiStr;
		testWriteDateRange(lo, hi, true, MessageConfiguration.RANGE_VERBOSE_KEY, expectedStr);
	}

	public void testWriteDateRange_Verbose_NullHi_Inclusive() throws Exception {
		Date lo = new Date();
		String expectedLoStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(lo);
		String expectedStr = "greater than or equal to " + expectedLoStr;
		testWriteDateRange(lo, null, true, MessageConfiguration.RANGE_VERBOSE_KEY, expectedStr);
	}

	public void testWriteDateRange_Verbose_NullLo_Inclusive() throws Exception {
		Date hi = new Date();
		String expectedHiStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(hi);
		String expectedStr = "less than or equal to " + expectedHiStr;
		testWriteDateRange(null, hi, true, MessageConfiguration.RANGE_VERBOSE_KEY, expectedStr);
	}

	public void testWriteDateRange_Verbose_NotEqual_Exclusive() throws Exception {
		Date lo = new Date();
		Date hi = new Date(lo.getTime() + 86400000); // add a day (in millis)
		String expectedLoStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(lo);
		String expectedHiStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(hi);
		String expectedStr = "greater than " + expectedLoStr + " and less than " + expectedHiStr;
		testWriteDateRange(lo, hi, false, MessageConfiguration.RANGE_VERBOSE_KEY, expectedStr);
	}

	public void testWriteDateRange_Verbose_NullHi_Exclusive() throws Exception {
		Date lo = new Date();
		String expectedLoStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(lo);
		String expectedStr = "greater than " + expectedLoStr;
		testWriteDateRange(lo, null, false, MessageConfiguration.RANGE_VERBOSE_KEY, expectedStr);
	}

	public void testWriteDateRange_Verbose_NullLo_Exclusive() throws Exception {
		Date hi = new Date();
		String expectedHiStr = ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageDateRangeFormat().format(hi);
		String expectedStr = "less than " + expectedHiStr;
		testWriteDateRange(null, hi, false, MessageConfiguration.RANGE_VERBOSE_KEY, expectedStr);
	}

	public void testWriteEnumWithNullEnumValuesThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(MessageProcessor.class, "writeEnum", new Class[] { AbstractTemplateColumn.class, EnumValues.class,
				StringBuffer.class, MessageConfiguration.class }, new Object[] { gridColumn, null, buffer, new MessageConfiguration() });
	}

	public void testWriteEnumWithStringsHappyCase() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add("Value1");
		enumValues.add("Value2");
		testWriteEnum(enumValues, "Value1 or Value2");
	}

	public void testWriteEnumWithStringsAndExclusionHappyCase() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.setSelectionExclusion(true);
		enumValues.add("Value1");
		enumValues.add("Value2");
		testWriteEnum(enumValues, "not Value1 or Value2");
	}

	public void testWriteEnumWithEnumValuesHappyCase() throws Exception {
		EnumValues<EnumValue> enumValues = ObjectMother.attachEnumValue(ObjectMother.createEnumValues(), 2);
		testWriteEnum(enumValues, ((EnumValue) enumValues.get(0)).getDisplayLabel() + " or " + ((EnumValue) enumValues.get(1)).getDisplayLabel());
	}

	public void testWriteEnumWithEnumValuesAndExclusionHappyCase() throws Exception {
		EnumValues<EnumValue> enumValues = ObjectMother.attachEnumValue(ObjectMother.createEnumValues(), 2);
		enumValues.setSelectionExclusion(true);
		testWriteEnum(enumValues, "not " + ((EnumValue) enumValues.get(0)).getDisplayLabel() + " or "
				+ ((EnumValue) enumValues.get(1)).getDisplayLabel());
	}

	public void testWriteCategoryOrEntityValueWithNullEnumValuesIsNoOp() throws Exception {
		MessageProcessor.writeCategoryOrEntityValue(buffer, null);
		assertEquals("", buffer.toString());
	}

	public void testWriteCategoryOrEntityValueWithInvalidEntityReturnsErrorMessage() throws Exception {
		GenericEntity entity = ObjectMother.createGenericEntity(GenericEntityType.forName("product"));
		testWriteCategoryOrEntityValue(new CategoryOrEntityValue(entity), "***ERROR: Entity of type " + entity.getType() + " with id "
				+ entity.getId() + " not found***");
	}

	public void testWriteCategoryOrEntityValueWithEntityHappyCase() throws Exception {
		GenericEntity entity = ObjectMother.createGenericEntity(GenericEntityType.forName("product"));
		EntityManager.getInstance().addGenericEntity(entity.getID(), entity.getType().getID(), entity.getName(), -1, null);
		testWriteCategoryOrEntityValue(new CategoryOrEntityValue(entity), entity.getName());
	}

	public void testWriteCategoryOrEntityValueWithInvalidCategoryReturnsErrorMessage() throws Exception {
		GenericCategory category = ObjectMother.createGenericCategory(GenericEntityType.forName("product"));
		testWriteCategoryOrEntityValue(
				new CategoryOrEntityValue(GenericEntityType.forName("product"), false, category.getID()),
				"***ERROR: Category of type " + GenericEntityType.forName("product") + " with id " + category.getId() + " not found***");
	}

	public void testWriteCategoryOrEntityValueWithCategoryHappyCase() throws Exception {
		GenericCategory category = ObjectMother.createGenericCategory(GenericEntityType.forName("product"));
		EntityManager.getInstance().addGenericEntityCategory(category.getType(), category.getID(), category.getName());
		testWriteCategoryOrEntityValue(new CategoryOrEntityValue(GenericEntityType.forName("product"), false, category.getID()), category.getName());
	}

	public void testWriteCategoryOrEntityValuesWithNullEnumValuesIsNoOp() throws Exception {
		testWriteCategoryOrEntityValues(null, "");
	}

	public void testWriteCategoryOrEntityValuesWithSingleEntityHappyCase() throws Exception {
		GenericEntity entity = ObjectMother.createGenericEntity(GenericEntityType.forName("product"));
		EntityManager.getInstance().addGenericEntity(entity.getID(), entity.getType().getID(), entity.getName(), -1, null);
		CategoryOrEntityValues values = new CategoryOrEntityValues();
		values.add(new CategoryOrEntityValue(entity));
		testWriteCategoryOrEntityValues(values, entity.getName());
	}

	public void testWriteCategoryOrEntityValuesWithMultipleEntitiesHappyCase() throws Exception {
		GenericEntity entity = ObjectMother.createGenericEntity(GenericEntityType.forName("product"));
		EntityManager.getInstance().addGenericEntity(entity.getID(), entity.getType().getID(), entity.getName(), -1, null);
		CategoryOrEntityValues values = new CategoryOrEntityValues();
		values.add(new CategoryOrEntityValue(entity));
		GenericEntity entity2 = ObjectMother.createGenericEntity(GenericEntityType.forName("product"));
		EntityManager.getInstance().addGenericEntity(entity2.getID(), entity2.getType().getID(), entity2.getName(), -1, null);
		values.add(new CategoryOrEntityValue(entity2));
		testWriteCategoryOrEntityValues(values, entity.getName() + " or " + entity2.getName());
	}

	public void testWriteCategoryOrEntityValuesWithSingleCategoryHappyCase() throws Exception {
		GenericCategory category = ObjectMother.createGenericCategory(GenericEntityType.forName("product"));
		EntityManager.getInstance().addGenericEntityCategory(category.getType(), category.getID(), category.getName());
		CategoryOrEntityValues values = new CategoryOrEntityValues();
		values.add(new CategoryOrEntityValue(GenericEntityType.forName("product"), false, category.getID()));
		testWriteCategoryOrEntityValues(values, category.getName());
	}

	public void testWriteCategoryOrEntityValuesWithMultipleCategoryHappyCase() throws Exception {
		GenericCategory category = ObjectMother.createGenericCategory(GenericEntityType.forName("product"));
		EntityManager.getInstance().addGenericEntityCategory(category.getType(), category.getID(), category.getName());
		CategoryOrEntityValues values = new CategoryOrEntityValues();
		values.add(new CategoryOrEntityValue(GenericEntityType.forName("product"), false, category.getID()));
		GenericCategory category2 = ObjectMother.createGenericCategory(GenericEntityType.forName("product"));
		EntityManager.getInstance().addGenericEntityCategory(category2.getType(), category2.getID(), category2.getName());
		values.add(new CategoryOrEntityValue(GenericEntityType.forName("product"), false, category2.getID()));
		testWriteCategoryOrEntityValues(values, category.getName() + " or " + category2.getName());
	}

	private void testWriteEnum(EnumValues<?> enumValues, String expectedString) throws Exception {
		testWriteEnum(enumValues, ",", "or", expectedString);
	}

	private void testWriteEnum(EnumValues<?> enumValues, String condDelim, String condFinalDelim, String expectedString) throws Exception {
		MessageConfiguration msgConfig = new MessageConfiguration();
		msgConfig.setConditionalDelimiter(condDelim);
		msgConfig.setConditionalFinalDelimiter(condFinalDelim);
		testWriteEnum(enumValues, msgConfig, expectedString);
	}

	private void testWriteEnum(EnumValues<?> enumValues, MessageConfiguration msgConfig, String expectedString) throws Exception {
		MessageProcessor.writeEnum(this.gridColumn, enumValues, buffer, msgConfig);
		assertEquals(expectedString, buffer.toString());
	}

	private void testWriteCategoryOrEntityValue(CategoryOrEntityValue value, String expectedString) throws Exception {
		MessageProcessor.writeCategoryOrEntityValue(buffer, value);
		assertEquals(expectedString, buffer.toString());
	}

	private void testWriteCategoryOrEntityValues(CategoryOrEntityValues values, String expectedString) throws Exception {
		testWriteCategoryOrEntityValues(values, ",", "or", expectedString);
	}

	private void testWriteCategoryOrEntityValues(CategoryOrEntityValues values, String condDelim, String condFinalDelim, String expectedString)
			throws Exception {
		MessageConfiguration msgConfig = new MessageConfiguration();
		msgConfig.setConditionalDelimiter(condDelim);
		msgConfig.setConditionalFinalDelimiter(condFinalDelim);
		testWriteCategoryOrEntityValues(values, msgConfig, expectedString);
	}

	private void testWriteCategoryOrEntityValues(CategoryOrEntityValues values, MessageConfiguration msgConfig, String expectedString)
			throws Exception {
		MessageProcessor.writeCategoryOrEntityValues(msgConfig, buffer, values);
		assertEquals(expectedString, buffer.toString());
	}

	private void testWriteDateRange(Date lo, Date hi, boolean inclusive, String style, String expectedString) throws Exception {
		DateRange range = new DateRange();
		range.setLowerValue(lo);
		range.setUpperValue(hi);
		range.setLowerValueInclusive(inclusive);
		range.setUpperValueInclusive(inclusive);
		MessageConfiguration msgConf = new MessageConfiguration();
		msgConf.setRangeStyle(style);
		MessageProcessor.writeRange(range, buffer, colDataSpec, msgConf);
		assertEquals(expectedString, buffer.toString());
	}

	private void testWriteFloatRangeWithPrecision(Double lo, Double hi, String colType, int precision, String expectedString) throws Exception {
		FloatRange range = new FloatRange();
		range.setLowerValue(lo);
		range.setUpperValue(hi);
		colDataSpec.setType(colType);
		colDataSpec.setPrecision(precision);
		MessageConfiguration msgCfg = new MessageConfiguration();

		MessageProcessor.writeRange(range, buffer, colDataSpec, msgCfg);
		assertEquals(expectedString, buffer.toString());
	}

	private void testWriteNumberWithPrecision(Number n, int precision, String expectedString) {
		colDataSpec.setPrecision(precision);

		MessageProcessor.writeVal(gridColumn, n, buffer, null, null);
		assertEquals(expectedString, buffer.toString());
	}

	private void testWriteCurrencyWithPrecision(Number n, int precision, String expectedString) {
		colDataSpec.setType(ColumnDataSpecDigest.TYPE_CURRENCY);

		testWriteNumberWithPrecision(n, precision, expectedString);
	}

	private void testProcess(List<? extends Node> msgParts, String expectedFormattedMsgString, List<String> expectedFormattedMsgArgs) throws Exception {
		testProcess(msgParts, expectedFormattedMsgString, expectedFormattedMsgArgs, new TestGenerateParms(100));
	}

	private void testProcess(List<? extends Node> msgParts, String expectedFormattedMsgString, List<String> expectedFormattedMsgArgs,
			AbstractGenerateParms genParms) throws Exception {
		// Create a Message to contain all msgParts
		Message msg = new Message(null);
		msg.f0 = new NodeListOptional();

		for (Iterator<? extends Node> iter = msgParts.iterator(); iter.hasNext();) {
			msg.f0.addNode(iter.next());
		}

		StringBuffer sb = new StringBuffer("(");
		sb.append(ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getMessageFormatConversionFunction());
		sb.append(" \"");
		sb.append(expectedFormattedMsgString);
		sb.append("\"");
		for (Iterator<String> iter = expectedFormattedMsgArgs.iterator(); iter.hasNext();) {
			String arg = iter.next();
			sb.append(" ");
			sb.append(arg);
		}
		sb.append(')');
		assertEquals(sb.toString(), new MessageProcessor().process(msg, genParms, null));
	}

	private Reference createReferencedDomainAttribute(String domainClassDotAttributeName, DeployType deployType) {
		return createReferencedDomainAttribute(domainClassDotAttributeName, deployType, FloatFormatter.DEFAULT_PRECISION);
	}

	private Reference createReferencedDomainAttribute(String domainClassDotAttributeName, DeployType deployType, int precision) {
		// add DomainAttribute instance to DomainClass
		String[] domainClassAttrNames = domainClassDotAttributeName.split("\\.");

		DomainAttribute domAttr = null;
		if (deployType == DeployType.FLOAT || deployType == DeployType.PERCENT || deployType == DeployType.CURRENCY) { 
			domAttr = new FloatDomainAttribute();
			((FloatDomainAttribute) domAttr).setPrecision(precision);
		} else {
			domAttr = new DomainAttribute();
		}
		domAttr.setName(domainClassAttrNames[1]);
		domAttr.setDeployType(deployType);
		
		DomainClass domClass = new DomainClass();
		domClass.setName(domainClassAttrNames[0]);
		domClass.addDomainAttribute(domAttr);

		DomainManager.getInstance().addDomainClass(domClass);


		// create and return the Reference to the new DomainAttribute
		return new Reference(null, new NodeToken(domainClassDotAttributeName), null);
	}

	private static class TestGenerateParms extends AbstractGenerateParms {
		public TestGenerateParms(int id) {
			super(id, ObjectMother.createDateSynonym(), ObjectMother.createDateSynonym(), new GridTemplate(), -1, 0, new ArrayList<Object>(), "status");
		}

		public TemplateUsageType getUsage() {
			return null;
		}

		public boolean hasGenericCategoryAsCellValue() {
			return false;
		}
	}
}

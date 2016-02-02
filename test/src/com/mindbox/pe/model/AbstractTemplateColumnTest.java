package com.mindbox.pe.model;

import java.util.Date;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.easymock.MockControl;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.DomainClassProvider;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.model.ColumnDataSpecDigest.EnumSourceType;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.table.DateRange;
import com.mindbox.pe.model.table.DateTimeRange;
import com.mindbox.pe.model.table.DynamicStringValue;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.EnumerationSourceProxy;
import com.mindbox.pe.model.table.FloatRange;
import com.mindbox.pe.model.table.IntegerRange;
import com.mindbox.pe.model.table.TimeRange;

public class AbstractTemplateColumnTest extends AbstractTestWithTestConfig {

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractTemplateColumnTest Tests");
		suite.addTestSuite(AbstractTemplateColumnTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private DomainClassProvider domainClassProviderMock;
	private MockControl domainClassProviderMockControl;
	private EnumerationSourceProxy enumerationSourceProxyMock;
	private MockControl enumerationSourceProxyMockControl;
	private ColumnDataSpecDigest columnDataSpecDigest;

	protected void setUpMocks() {
		domainClassProviderMockControl = MockControl.createControl(DomainClassProvider.class);
		domainClassProviderMock = (DomainClassProvider) domainClassProviderMockControl.getMock();
		enumerationSourceProxyMockControl = MockControl.createControl(EnumerationSourceProxy.class);
		enumerationSourceProxyMock = (EnumerationSourceProxy) enumerationSourceProxyMockControl.getMock();
	}

	public AbstractTemplateColumnTest(String name) {
		super(name);
	}

	public void testConvertToCellValueWithInvalidBooleanStrThrowsInvalidDataException() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		column.getColumnDataSpecDigest().setIsBlankAllowed(true);

		assertThrowsException(
				column,
				"convertToCellValue",
				new Class[] { String.class, DomainClassProvider.class, EnumerationSourceProxy.class },
				new Object[] { "XYZ", domainClassProviderMock, enumerationSourceProxyMock },
				InvalidDataException.class);
	}

	public void testConvertToCellValueHappyCaseForBooleanColumnWithNoDateSpecWithAllowBlank() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		column.getColumnDataSpecDigest().setIsBlankAllowed(true);

		Object obj = column.convertToCellValue("true", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(Boolean.TRUE, obj);
	}

	public void testConvertToCellValueHappyCaseForBooleanColumnWithNoDateSpecWithNoAllowBlank() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);

		Object obj = column.convertToCellValue("true", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals("true", obj.toString());
	}

	public void testConvertToCellValueHappyCaseForCurrencyColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_CURRENCY);

		Object obj = column.convertToCellValue("12345.67", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(Double.class, obj.getClass());
		assertEquals("12345.67", obj.toString());
	}

	public void testConvertToCellValueHappyCaseForCurrencyRangeColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE);

		Object obj = column.convertToCellValue("[100.50-200.75]", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(FloatRange.class, obj.getClass());
		assertEquals(100.50, ((FloatRange) obj).getLowerValue().doubleValue(), 0.2d);
		assertEquals(200.75, ((FloatRange) obj).getUpperValue().doubleValue(), 0.2d);

	}

	public void testConvertToCellValueHappyCaseForDateColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_DATE);

		Object obj = column.convertToCellValue("12/25/2000", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(Date.class, obj.getClass());
		assertEquals("12/25/2000", UIConfiguration.FORMAT_DATE.format((Date) obj));
	}

	public void testConvertToCellValueHappyCaseForDateRangeColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_DATE_RANGE);

		Object obj = column.convertToCellValue("[01/05/2007-10/31/2007]", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(DateRange.class, obj.getClass());
		assertEquals("01/05/2007", UIConfiguration.FORMAT_DATE.format(((DateRange) obj).getLowerValue()));
		assertEquals("10/31/2007", UIConfiguration.FORMAT_DATE.format(((DateRange) obj).getUpperValue()));
	}

	public void testConvertToCellValueHappyCaseForDateTimeColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_DATE_TIME);

		Object obj = column.convertToCellValue("12/25/2000 12:34:56", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(Date.class, obj.getClass());
		assertEquals("12/25/2000 12:34:56", UIConfiguration.FORMAT_DATE_TIME_SEC.format((Date) obj));
	}

	public void testConvertToCellValueHappyCaseForDateTimeRangeColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_DATE_TIME_RANGE);

		Object obj = column.convertToCellValue(
				"[01/05/2007 00:05:30-10/31/2007 23:59:59]",
				domainClassProviderMock,
				enumerationSourceProxyMock);
		assertEquals(DateTimeRange.class, obj.getClass());
		assertEquals("01/05/2007 00:05:30", UIConfiguration.FORMAT_DATE_TIME_SEC.format(((DateTimeRange) obj).getLowerValue()));
		assertEquals("10/31/2007 23:59:59", UIConfiguration.FORMAT_DATE_TIME_SEC.format(((DateTimeRange) obj).getUpperValue()));
	}

	public void testConvertToCellValueHappyCaseForDynamicStringColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_DYNAMIC_STRING);

		String str = "dynamic-" + ObjectMother.createString();
		Object obj = column.convertToCellValue(str, domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(DynamicStringValue.class, obj.getClass());
		assertEquals(str, ((DynamicStringValue) obj).toString());
	}

	public void testConvertToCellValueHappyCaseForEnumListColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);

		String str = ObjectMother.createString();
		Object obj = column.convertToCellValue(str, domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(str, obj.toString());
	}

	public void testConvertToCellValueHappyCaseForEnumListColumnForDomainAttribute() throws Exception {
		List<EnumValue> enumValueList = ObjectMother.createEnumValuesAsList(2);
		DomainClass dc = ObjectMother.attachDomainAttributes(ObjectMother.createDomainClass(), 1);
		for (EnumValue ev : enumValueList) {
			dc.getDomainAttributes().get(0).addEnumValue(ev);
		}
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		column.getColumnDataSpecDigest().setIsMultiSelectAllowed(true);
		column.getColumnDataSpecDigest().setEnumSourceType(EnumSourceType.DOMAIN_ATTRIBUTE);
		column.getColumnDataSpecDigest().setAttributeMap(dc.getName() + "." + dc.getDomainAttributes().get(0).getName());

		domainClassProviderMockControl.expectAndReturn(domainClassProviderMock.getDomainClass(dc.getName()), dc);
		domainClassProviderMockControl.replay();
		
		Object obj = column.convertToCellValue(
				enumValueList.get(0).getDeployID() + "," + enumValueList.get(1).getDeployID(),
				domainClassProviderMock,
				enumerationSourceProxyMock);
		assertEquals(EnumValues.class, obj.getClass());
		assertEquals(2, ((EnumValues<?>) obj).size());
	}

	public void testConvertToCellValueHappyCaseForEnumListColumnForExternalSource() throws Exception {
		List<EnumValue> enumValueList = ObjectMother.createEnumValuesAsList(2);
		for (EnumValue ev : enumValueList) {
			ev.setDeployID(null);
		}
		String enumSourceName = "ES" + ObjectMother.createString();

		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		column.getColumnDataSpecDigest().setIsMultiSelectAllowed(true);
		column.getColumnDataSpecDigest().setEnumSourceType(EnumSourceType.EXTERNAL);
		column.getColumnDataSpecDigest().setEnumSourceName(enumSourceName);
		
		enumerationSourceProxyMockControl.expectAndReturn(enumerationSourceProxyMock.getAllEnumValues(enumSourceName), enumValueList);
		enumerationSourceProxyMockControl.replay();

		Object obj = column.convertToCellValue(
				enumValueList.get(0).getDeployValue() + "," + enumValueList.get(1).getDeployValue(),
				domainClassProviderMock,
				enumerationSourceProxyMock);
		assertEquals(EnumValues.class, obj.getClass());
		assertEquals(2, ((EnumValues<?>) obj).size());
	}

	public void testConvertToCellValueHappyCaseForFloatColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_FLOAT);

		Object obj = column.convertToCellValue("12345.67", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(Double.class, obj.getClass());
		assertEquals("12345.67", obj.toString());
	}

	public void testConvertToCellValueHappyCaseForFloatRangeColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_FLOAT_RANGE);

		Object obj = column.convertToCellValue("[100.50-200.75]", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(FloatRange.class, obj.getClass());
		assertEquals(100.50, ((FloatRange) obj).getLowerValue().doubleValue(), 0.2d);
		assertEquals(200.75, ((FloatRange) obj).getUpperValue().doubleValue(), 0.2d);
	}

	public void testConvertToCellValueHappyCaseForIntegerColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createIntegerColumnDataSpecDigest());

		Object obj = column.convertToCellValue("1234", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(Integer.class, obj.getClass());
		assertEquals("1234", obj.toString());
	}

	public void testConvertToCellValueHappyCaseForIntegerRangeColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_INTEGER_RANGE);

		Object obj = column.convertToCellValue("[1234-5678]", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(IntegerRange.class, obj.getClass());
		assertEquals("1234", ((IntegerRange) obj).getLowerValue().toString());
		assertEquals("5678", ((IntegerRange) obj).getUpperValue().toString());
	}

	public void testConvertToCellValueHappyCaseForPercentColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_PERCENT);

		Object obj = column.convertToCellValue("12345.67", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(Double.class, obj.getClass());
		assertEquals("12345.67", obj.toString());
	}

	public void testConvertToCellValueHappyCaseForTimeRangeColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_TIME_RANGE);

		Object obj = column.convertToCellValue("[05:15:00-23:59:59]", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(TimeRange.class, obj.getClass());
		assertEquals(60 * 15 + 3600 * 5, ((TimeRange) obj).getLowerValue().intValue());
		assertEquals(60 * 60 * 24 - 1, ((TimeRange) obj).getUpperValue().intValue());
	}

	public void testConvertToCellValueAcceptsEmpyStringForFloat() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_FLOAT);
		column.getColumnDataSpecDigest().setIsBlankAllowed(true);

		Object obj = column.convertToCellValue("", domainClassProviderMock, enumerationSourceProxyMock);
		assertNull(obj);
	}

	public void testConvertToCellValueAcceptsEmpyStringForInteger() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_INTEGER);
		column.getColumnDataSpecDigest().setIsBlankAllowed(true);

		Object obj = column.convertToCellValue("", domainClassProviderMock, enumerationSourceProxyMock);
		assertNull(obj);
	}

	public void testConvertToCellValueOnlyAcceptsEntityValueForSingleSelectEntityColumn() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createEntityColumnDataSpecDigest("product", true, false, false));

		Object obj = column.convertToCellValue("product:true:1", domainClassProviderMock, enumerationSourceProxyMock);
		assertTrue(obj instanceof CategoryOrEntityValue);
		assertEquals(new CategoryOrEntityValue(GenericEntityType.forName("product"), true, 1), obj);
		assertNull(column.convertToCellValue("product:FALSE:2", domainClassProviderMock, enumerationSourceProxyMock));
	}

	public void testConvertToCellValueOnlyAcceptsCategoryValueForSingleSelectCategoryColumn() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createEntityColumnDataSpecDigest("product", false, true, false));

		Object obj = column.convertToCellValue("product:FALSE:2", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(new CategoryOrEntityValue(GenericEntityType.forName("product"), false, 2), obj);
		assertNull(column.convertToCellValue("product:true:1", domainClassProviderMock, enumerationSourceProxyMock));
	}

	public void testConvertToCellValueOnlyAcceptsEntityValuesForMultiSelectEntityColumn() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createEntityColumnDataSpecDigest("product", true, false, true));

		Object obj = column.convertToCellValue(
				EnumValues.EXCLUSION_PREFIX + "product:true:1,product:FALSE:2",
				domainClassProviderMock,
				enumerationSourceProxyMock);
		assertTrue(obj instanceof CategoryOrEntityValues);
		assertEquals(1, ((CategoryOrEntityValues) obj).size());
		assertEquals(new CategoryOrEntityValue(GenericEntityType.forName("product"), true, 1), ((CategoryOrEntityValues) obj).get(0));
		assertTrue(((CategoryOrEntityValues) obj).isSelectionExclusion());
	}

	public void testConvertToCellValueOnlyAcceptsCategoryValuesForMultiSelectCategoryColumn() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createEntityColumnDataSpecDigest("product", false, true, true));

		Object obj = column.convertToCellValue("product:true:1,product:FALSE:2", domainClassProviderMock, enumerationSourceProxyMock);
		assertTrue(obj instanceof CategoryOrEntityValues);
		assertEquals(1, ((CategoryOrEntityValues) obj).size());
		assertEquals(new CategoryOrEntityValue(GenericEntityType.forName("product"), false, 2), ((CategoryOrEntityValues) obj).get(0));
		assertFalse(((CategoryOrEntityValues) obj).isSelectionExclusion());
	}

	public void testConvertToCellValueHappyCaseForSingleSelectCategoryOrEntityColumn() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createEntityColumnDataSpecDigest("product", true, true, false));
		Object obj = column.convertToCellValue("product:true:1", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(new CategoryOrEntityValue(GenericEntityType.forName("product"), true, 1), obj);
		obj = column.convertToCellValue("product:FALSE:2", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(new CategoryOrEntityValue(GenericEntityType.forName("product"), false, 2), obj);
	}

	public void testConvertToCellValueHappyCaseForMultiSelectCategoryOrEntityColumn() throws Exception {
		GridTemplateColumn column = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
		column.setDataSpecDigest(ObjectMother.createEntityColumnDataSpecDigest("product", true, true, true));

		Object obj = column.convertToCellValue("product:true:1,product:FALSE:2", domainClassProviderMock, enumerationSourceProxyMock);
		assertTrue(obj instanceof CategoryOrEntityValues);
		assertEquals(2, ((CategoryOrEntityValues) obj).size());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		setUpMocks();
		columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
	}
}

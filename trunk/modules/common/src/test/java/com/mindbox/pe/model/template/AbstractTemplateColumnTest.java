package com.mindbox.pe.model.template;

import static com.mindbox.pe.common.CommonTestObjectMother.attachDomainAttributes;
import static com.mindbox.pe.common.CommonTestObjectMother.createColumnDataSpecDigest;
import static com.mindbox.pe.common.CommonTestObjectMother.createDomainClass;
import static com.mindbox.pe.common.CommonTestObjectMother.createEntityColumnDataSpecDigest;
import static com.mindbox.pe.common.CommonTestObjectMother.createEnumValuesAsList;
import static com.mindbox.pe.common.CommonTestObjectMother.createGridTemplateColumn;
import static com.mindbox.pe.common.CommonTestObjectMother.createIntegerColumnDataSpecDigest;
import static com.mindbox.pe.common.CommonTestObjectMother.createUsageType;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.AbstractTestWithGenericEntityType;
import com.mindbox.pe.common.DomainClassProvider;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.domain.DomainClass;
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
import com.mindbox.pe.model.template.ColumnDataSpecDigest.EnumSourceType;

public class AbstractTemplateColumnTest extends AbstractTestWithGenericEntityType {

	private DomainClassProvider domainClassProviderMock;
	private EnumerationSourceProxy enumerationSourceProxyMock;
	private ColumnDataSpecDigest columnDataSpecDigest;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		setUpMocks();
		columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
	}

	protected void setUpMocks() {
		domainClassProviderMock = createMock(DomainClassProvider.class);
		enumerationSourceProxyMock = createMock(EnumerationSourceProxy.class);
	}

	@Test
	public void testConvertToCellValueAcceptsEmpyStringForFloat() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_FLOAT);
		column.getColumnDataSpecDigest().setIsBlankAllowed(true);

		Object obj = column.convertToCellValue("", domainClassProviderMock, enumerationSourceProxyMock);
		assertNull(obj);
	}

	@Test
	public void testConvertToCellValueAcceptsEmpyStringForInteger() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_INTEGER);
		column.getColumnDataSpecDigest().setIsBlankAllowed(true);

		Object obj = column.convertToCellValue("", domainClassProviderMock, enumerationSourceProxyMock);
		assertNull(obj);
	}

	@Test
	public void testConvertToCellValueHappyCaseForBooleanColumnWithNoDateSpecWithAllowBlank() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		column.getColumnDataSpecDigest().setIsBlankAllowed(true);

		Object obj = column.convertToCellValue("true", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(Boolean.TRUE, obj);
	}

	@Test
	public void testConvertToCellValueHappyCaseForBooleanColumnWithNoDateSpecWithNoAllowBlank() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);

		Object obj = column.convertToCellValue("true", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals("true", obj.toString());
	}

	@Test
	public void testConvertToCellValueHappyCaseForCurrencyColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_CURRENCY);

		Object obj = column.convertToCellValue("12345.67", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(Double.class, obj.getClass());
		assertEquals("12345.67", obj.toString());
	}

	@Test
	public void testConvertToCellValueHappyCaseForCurrencyRangeColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_CURRENCY_RANGE);

		Object obj = column.convertToCellValue("[100.50-200.75]", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(FloatRange.class, obj.getClass());
		assertEquals(100.50, ((FloatRange) obj).getLowerValue().doubleValue(), 0.2d);
		assertEquals(200.75, ((FloatRange) obj).getUpperValue().doubleValue(), 0.2d);
	}

	@Test
	public void testConvertToCellValueHappyCaseForDateColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_DATE);

		Object obj = column.convertToCellValue("12/25/2000", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(Date.class, obj.getClass());
		assertEquals("12/25/2000", Constants.THREADLOCAL_FORMAT_DATE.get().format((Date) obj));
	}

	@Test
	public void testConvertToCellValueHappyCaseForDateRangeColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_DATE_RANGE);

		Object obj = column.convertToCellValue("[01/05/2007-10/31/2007]", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(DateRange.class, obj.getClass());
		assertEquals("01/05/2007", Constants.THREADLOCAL_FORMAT_DATE.get().format(((DateRange) obj).getLowerValue()));
		assertEquals("10/31/2007", Constants.THREADLOCAL_FORMAT_DATE.get().format(((DateRange) obj).getUpperValue()));
	}

	@Test
	public void testConvertToCellValueHappyCaseForDateTimeColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_DATE_TIME);

		Object obj = column.convertToCellValue("12/25/2000 12:34:56", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(Date.class, obj.getClass());
		assertEquals("12/25/2000 12:34:56", Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format((Date) obj));
	}

	@Test
	public void testConvertToCellValueHappyCaseForDateTimeRangeColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_DATE_TIME_RANGE);

		Object obj = column.convertToCellValue("[01/05/2007 00:05:30-10/31/2007 23:59:59]", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(DateTimeRange.class, obj.getClass());
		assertEquals("01/05/2007 00:05:30", Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format(((DateTimeRange) obj).getLowerValue()));
		assertEquals("10/31/2007 23:59:59", Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format(((DateTimeRange) obj).getUpperValue()));
	}

	@Test
	public void testConvertToCellValueHappyCaseForDynamicStringColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_DYNAMIC_STRING);

		String str = "dynamic-" + createString();
		Object obj = column.convertToCellValue(str, domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(DynamicStringValue.class, obj.getClass());
		assertEquals(str, ((DynamicStringValue) obj).toString());
	}

	@Test
	public void testConvertToCellValueHappyCaseForEnumListColumnForDomainAttribute() throws Exception {
		List<EnumValue> enumValueList = createEnumValuesAsList(2);
		DomainClass dc = attachDomainAttributes(createDomainClass(), 1);
		for (EnumValue ev : enumValueList) {
			dc.getDomainAttributes().get(0).addEnumValue(ev);
		}
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		column.getColumnDataSpecDigest().setIsMultiSelectAllowed(true);
		column.getColumnDataSpecDigest().setEnumSourceType(EnumSourceType.DOMAIN_ATTRIBUTE);
		column.getColumnDataSpecDigest().setAttributeMap(dc.getName() + "." + dc.getDomainAttributes().get(0).getName());

		expect(domainClassProviderMock.getDomainClass(dc.getName())).andReturn(dc);
		replay(domainClassProviderMock);

		Object obj = column.convertToCellValue(enumValueList.get(0).getDeployID() + "," + enumValueList.get(1).getDeployID(), domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(EnumValues.class, obj.getClass());

		assertEquals(2, ((EnumValues<?>) obj).size());

		verify(domainClassProviderMock);
	}

	@Test
	public void testConvertToCellValueHappyCaseForEnumListColumnForExternalSource() throws Exception {
		List<EnumValue> enumValueList = createEnumValuesAsList(2);
		for (EnumValue ev : enumValueList) {
			ev.setDeployID(null);
		}
		String enumSourceName = "ES" + createString();

		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		column.getColumnDataSpecDigest().setIsMultiSelectAllowed(true);
		column.getColumnDataSpecDigest().setEnumSourceType(EnumSourceType.EXTERNAL);
		column.getColumnDataSpecDigest().setEnumSourceName(enumSourceName);

		expect(enumerationSourceProxyMock.getAllEnumValues(enumSourceName)).andReturn(enumValueList);
		replay(enumerationSourceProxyMock);

		Object obj = column.convertToCellValue(enumValueList.get(0).getDeployValue() + "," + enumValueList.get(1).getDeployValue(), domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(EnumValues.class, obj.getClass());
		assertEquals(2, ((EnumValues<?>) obj).size());

		verify(enumerationSourceProxyMock);
	}

	@Test
	public void testConvertToCellValueHappyCaseForEnumListColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);

		String str = createString();
		Object obj = column.convertToCellValue(str, domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(str, obj.toString());
	}

	@Test
	public void testConvertToCellValueHappyCaseForFloatColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_FLOAT);

		Object obj = column.convertToCellValue("12345.67", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(Double.class, obj.getClass());
		assertEquals("12345.67", obj.toString());
	}

	@Test
	public void testConvertToCellValueHappyCaseForFloatRangeColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_FLOAT_RANGE);

		Object obj = column.convertToCellValue("[100.50-200.75]", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(FloatRange.class, obj.getClass());
		assertEquals(100.50, ((FloatRange) obj).getLowerValue().doubleValue(), 0.2d);
		assertEquals(200.75, ((FloatRange) obj).getUpperValue().doubleValue(), 0.2d);
	}

	@Test
	public void testConvertToCellValueHappyCaseForIntegerColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createIntegerColumnDataSpecDigest());

		Object obj = column.convertToCellValue("1234", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(Integer.class, obj.getClass());
		assertEquals("1234", obj.toString());
	}

	@Test
	public void testConvertToCellValueHappyCaseForIntegerRangeColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_INTEGER_RANGE);

		Object obj = column.convertToCellValue("[1234-5678]", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(IntegerRange.class, obj.getClass());
		assertEquals("1234", ((IntegerRange) obj).getLowerValue().toString());
		assertEquals("5678", ((IntegerRange) obj).getUpperValue().toString());
	}

	@Test
	public void testConvertToCellValueHappyCaseForMultiSelectCategoryOrEntityColumn() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createEntityColumnDataSpecDigest(entityType.getName(), true, true, true));

		Object obj = column.convertToCellValue(entityType.getName() + ":true:1," + entityType.getName() + ":FALSE:2", domainClassProviderMock, enumerationSourceProxyMock);
		assertTrue(obj instanceof CategoryOrEntityValues);
		assertEquals(2, ((CategoryOrEntityValues) obj).size());
	}

	@Test
	public void testConvertToCellValueHappyCaseForPercentColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_PERCENT);

		Object obj = column.convertToCellValue("12345.67", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(Double.class, obj.getClass());
		assertEquals("12345.67", obj.toString());
	}

	@Test
	public void testConvertToCellValueHappyCaseForSingleSelectCategoryOrEntityColumn() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createEntityColumnDataSpecDigest(entityType.getName(), true, true, false));
		Object obj = column.convertToCellValue(entityType.getName() + ":true:1", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(new CategoryOrEntityValue(entityType, true, 1), obj);
		obj = column.convertToCellValue(entityType.getName() + ":FALSE:2", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(new CategoryOrEntityValue(entityType, false, 2), obj);
	}

	@Test
	public void testConvertToCellValueHappyCaseForTimeRangeColumnWithNoDateSpec() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_TIME_RANGE);

		Object obj = column.convertToCellValue("[05:15:00-23:59:59]", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(TimeRange.class, obj.getClass());
		assertEquals(60 * 15 + 3600 * 5, ((TimeRange) obj).getLowerValue().intValue());
		assertEquals(60 * 60 * 24 - 1, ((TimeRange) obj).getUpperValue().intValue());
	}

	@Test
	public void testConvertToCellValueOnlyAcceptsCategoryValueForSingleSelectCategoryColumn() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createEntityColumnDataSpecDigest(entityType.getName(), false, true, false));

		Object obj = column.convertToCellValue(entityType.getName() + ":FALSE:2", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(new CategoryOrEntityValue(entityType, false, 2), obj);
		assertNull(column.convertToCellValue(entityType.getName() + ":true:1", domainClassProviderMock, enumerationSourceProxyMock));
	}

	@Test
	public void testConvertToCellValueOnlyAcceptsCategoryValuesForMultiSelectCategoryColumn() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createEntityColumnDataSpecDigest(entityType.getName(), false, true, true));

		Object obj = column.convertToCellValue(entityType.getName() + ":true:1," + entityType.getName() + ":FALSE:2", domainClassProviderMock, enumerationSourceProxyMock);
		assertTrue(obj instanceof CategoryOrEntityValues);
		assertEquals(1, ((CategoryOrEntityValues) obj).size());
		assertEquals(new CategoryOrEntityValue(entityType, false, 2), ((CategoryOrEntityValues) obj).get(0));
		assertFalse(((CategoryOrEntityValues) obj).isSelectionExclusion());
	}

	@Test
	public void testConvertToCellValueOnlyAcceptsEntityValueForSingleSelectEntityColumn() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createEntityColumnDataSpecDigest(entityType.getName(), true, false, false));

		Object obj = column.convertToCellValue(entityType.getName() + ":true:1", domainClassProviderMock, enumerationSourceProxyMock);
		assertTrue(obj instanceof CategoryOrEntityValue);
		assertEquals(new CategoryOrEntityValue(entityType, true, 1), obj);
		assertNull(column.convertToCellValue(entityType.getName() + ":FALSE:2", domainClassProviderMock, enumerationSourceProxyMock));
	}

	@Test
	public void testConvertToCellValueOnlyAcceptsEntityValuesForMultiSelectEntityColumn() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createEntityColumnDataSpecDigest(entityType.getName(), true, false, true));

		Object obj = column.convertToCellValue(EnumValues.EXCLUSION_PREFIX + entityType.getName() + ":true:1,product:FALSE:2", domainClassProviderMock, enumerationSourceProxyMock);
		assertTrue(obj instanceof CategoryOrEntityValues);
		assertEquals(1, ((CategoryOrEntityValues) obj).size());
		assertEquals(new CategoryOrEntityValue(entityType, true, 1), ((CategoryOrEntityValues) obj).get(0));
		assertTrue(((CategoryOrEntityValues) obj).isSelectionExclusion());
	}

	@Test
	public void testConvertToCellValueWithInvalidBooleanStrThrowsInvalidDataException() throws Exception {
		GridTemplateColumn column = createGridTemplateColumn(1, createUsageType());
		column.setDataSpecDigest(createColumnDataSpecDigest());
		column.getColumnDataSpecDigest().setType(ColumnDataSpecDigest.TYPE_BOOLEAN);
		column.getColumnDataSpecDigest().setIsBlankAllowed(true);

		assertThrowsException(column, "convertToCellValue", new Class[] { String.class, DomainClassProvider.class, EnumerationSourceProxy.class }, new Object[] {
				"XYZ",
				domainClassProviderMock,
				enumerationSourceProxyMock }, InvalidDataException.class);
	}
}

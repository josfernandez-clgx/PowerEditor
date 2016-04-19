package com.mindbox.pe.model.table;

import static com.mindbox.pe.common.CommonTestObjectMother.attachDomainAttributes;
import static com.mindbox.pe.common.CommonTestObjectMother.createColumnDataSpecDigest;
import static com.mindbox.pe.common.CommonTestObjectMother.createDomainClass;
import static com.mindbox.pe.common.CommonTestObjectMother.createEnumValue;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.DomainClassProvider;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.ColumnDataSpecDigest.EnumSourceType;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.unittest.AbstractTestBase;

public class EnumValuesDataHelperTest extends AbstractTestBase {

	private DomainClassProvider domainClassProviderMock;
	private EnumerationSourceProxy enumerationSourceProxyMock;
	private ColumnDataSpecDigest columnDataSpecDigest;

	@Before
	public void setUp() throws Exception {
		setUpMocks();
		columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
	}

	private void setUpMocks() {
		domainClassProviderMock = createMock(DomainClassProvider.class);
		enumerationSourceProxyMock = createMock(EnumerationSourceProxy.class);
	}

	@Test
	public void testConvertToEnumValueForEnumValueDataSpecWithSameDisplayValueAndDeployIDMultiSelect() throws Exception {
		List<EnumValue> enumValues = new ArrayList<EnumValue>();
		enumValues.add(createEnumValue());
		enumValues.add(createEnumValue());
		enumValues.add(createEnumValue());
		enumValues.get(2).setDisplayLabel(enumValues.get(0).getDeployID().toString());

		DomainClass dc = attachDomainAttributes(createDomainClass(), 1);
		for (EnumValue ev : enumValues) {
			dc.getDomainAttributes().get(0).addEnumValue(ev);
		}

		columnDataSpecDigest.setIsMultiSelectAllowed(true);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.DOMAIN_ATTRIBUTE);
		columnDataSpecDigest.setAttributeMap(dc.getName() + "." + dc.getDomainAttributes().get(0).getName());

		expect(domainClassProviderMock.getDomainClass(dc.getName())).andReturn(dc).times(4);
		replay(domainClassProviderMock);

		Object obj = EnumValuesDataHelper.convertToEnumValue(columnDataSpecDigest, enumValues.get(0).getDeployID() + ","
				+ enumValues.get(1).getDeployID() + "," + enumValues.get(2).getDeployID(), domainClassProviderMock, enumerationSourceProxyMock);
		assertTrue(EnumValues.class.isInstance(obj));
		assertEquals(3, ((EnumValues<?>) obj).size());

		obj = EnumValuesDataHelper.convertToEnumValue(
				columnDataSpecDigest,
				enumValues.get(0).getDeployID().toString(),
				domainClassProviderMock,
				enumerationSourceProxyMock);
		assertTrue(EnumValues.class.isInstance(obj));
		assertEquals(1, ((EnumValues<?>) obj).size());
		assertEquals(enumValues.get(0), ((EnumValues<?>) obj).get(0));

		obj = EnumValuesDataHelper.convertToEnumValue(
				columnDataSpecDigest,
				enumValues.get(1).getDeployID().toString(),
				domainClassProviderMock,
				enumerationSourceProxyMock);
		assertTrue(EnumValues.class.isInstance(obj));
		assertEquals(1, ((EnumValues<?>) obj).size());
		assertEquals(enumValues.get(1), ((EnumValues<?>) obj).get(0));

		obj = EnumValuesDataHelper.convertToEnumValue(
				columnDataSpecDigest,
				enumValues.get(2).getDeployID().toString(),
				domainClassProviderMock,
				enumerationSourceProxyMock);
		assertTrue(EnumValues.class.isInstance(obj));
		assertEquals(1, ((EnumValues<?>) obj).size());
		assertEquals(enumValues.get(2), ((EnumValues<?>) obj).get(0));
	}

	@Test
	public void testConvertToEnumValueForEnumValueDataSpecWithSameDisplayValueAndDeployIDSingleSelect() throws Exception {
		List<EnumValue> enumValues = new ArrayList<EnumValue>();
		enumValues.add(createEnumValue());
		enumValues.add(createEnumValue());
		enumValues.add(createEnumValue());
		enumValues.get(2).setDisplayLabel(enumValues.get(0).getDeployID().toString());

		DomainClass dc = attachDomainAttributes(createDomainClass(), 1);
		for (EnumValue ev : enumValues) {
			dc.getDomainAttributes().get(0).addEnumValue(ev);
		}

		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.DOMAIN_ATTRIBUTE);
		columnDataSpecDigest.setAttributeMap(dc.getName() + "." + dc.getDomainAttributes().get(0).getName());

		expect(domainClassProviderMock.getDomainClass(dc.getName())).andReturn(dc).times(3);
		replay(domainClassProviderMock);

		Object obj = EnumValuesDataHelper.convertToEnumValue(
				columnDataSpecDigest,
				enumValues.get(0).getDeployID().toString(),
				domainClassProviderMock,
				enumerationSourceProxyMock);
		assertTrue(obj instanceof EnumValue);
		assertEquals(enumValues.get(0), obj);

		obj = EnumValuesDataHelper.convertToEnumValue(
				columnDataSpecDigest,
				enumValues.get(1).getDeployID().toString(),
				domainClassProviderMock,
				enumerationSourceProxyMock);
		assertTrue(obj instanceof EnumValue);
		assertEquals(enumValues.get(1), obj);

		obj = EnumValuesDataHelper.convertToEnumValue(
				columnDataSpecDigest,
				enumValues.get(2).getDeployID().toString(),
				domainClassProviderMock,
				enumerationSourceProxyMock);
		assertTrue(obj instanceof EnumValue);
		assertEquals(enumValues.get(2), obj);
	}

	@Test
	public void testConvertToEnumValueHappyCaseForColumnDefinedEnumsMultiSelect() throws Exception {
		columnDataSpecDigest.setIsMultiSelectAllowed(true);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.COLUMN);
		columnDataSpecDigest.addColumnEnumValue("Enum1");
		columnDataSpecDigest.addColumnEnumValue("Enum2");
		columnDataSpecDigest.addColumnEnumValue("Enum3");

		Object obj = EnumValuesDataHelper.convertToEnumValue(
				columnDataSpecDigest/*new EnumDataSpec<String>(true, enumValueList, true, false)*/,
				"Enum2,Enum3,Enum1",
				domainClassProviderMock,
				enumerationSourceProxyMock);
		assertTrue(EnumValues.class.isInstance(obj));
		assertEquals(3, ((EnumValues<?>) obj).size());

		obj = EnumValuesDataHelper.convertToEnumValue(
				columnDataSpecDigest/*new EnumDataSpec<String>(true, enumValueList, true, false)*/,
				"Enum1",
				domainClassProviderMock,
				enumerationSourceProxyMock);
		assertTrue(EnumValues.class.isInstance(obj));
		assertEquals(1, ((EnumValues<?>) obj).size());
		assertEquals("Enum1", ((EnumValue) ((EnumValues<?>) obj).get(0)).getDeployValue());

		obj = EnumValuesDataHelper.convertToEnumValue(
				columnDataSpecDigest/*new EnumDataSpec<String>(true, enumValueList, true, false)*/,
				"Enum2",
				domainClassProviderMock,
				enumerationSourceProxyMock);
		assertTrue(EnumValues.class.isInstance(obj));
		assertEquals(1, ((EnumValues<?>) obj).size());
		assertEquals("Enum2", ((EnumValue) ((EnumValues<?>) obj).get(0)).getDeployValue());

		obj = EnumValuesDataHelper.convertToEnumValue(
				columnDataSpecDigest/*new EnumDataSpec<String>(true, enumValueList, true, false)*/,
				"Enum3",
				domainClassProviderMock,
				enumerationSourceProxyMock);
		assertTrue(EnumValues.class.isInstance(obj));
		assertEquals(1, ((EnumValues<?>) obj).size());
		assertEquals("Enum3", ((EnumValue) ((EnumValues<?>) obj).get(0)).getDeployValue());
	}

	@Test
	public void testConvertToEnumValueHappyCaseForColumnDefinedEnumsSingleSelect() throws Exception {
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.COLUMN);
		columnDataSpecDigest.addColumnEnumValue("Enum1");
		columnDataSpecDigest.addColumnEnumValue("Enum2");
		columnDataSpecDigest.addColumnEnumValue("Enum3");
		Object obj = EnumValuesDataHelper.convertToEnumValue(columnDataSpecDigest, "Enum2", domainClassProviderMock, enumerationSourceProxyMock);
		assertEquals(EnumValue.class, obj.getClass());
		assertEquals("Enum2", ((EnumValue) obj).getDeployValue());
	}

	@Test
	public void testConvertToEnumValueHappyCaseForDomainAttributeMultiSelect() throws Exception {
		List<EnumValue> enumValues = new ArrayList<EnumValue>();
		enumValues.add(createEnumValue());
		enumValues.add(createEnumValue());
		enumValues.add(createEnumValue());
		DomainClass dc = attachDomainAttributes(createDomainClass(), 1);
		for (EnumValue ev : enumValues) {
			dc.getDomainAttributes().get(0).addEnumValue(ev);
		}

		columnDataSpecDigest.setIsMultiSelectAllowed(true);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.DOMAIN_ATTRIBUTE);
		columnDataSpecDigest.setAttributeMap(dc.getName() + "." + dc.getDomainAttributes().get(0).getName());

		expect(domainClassProviderMock.getDomainClass(dc.getName())).andReturn(dc);
		replay(domainClassProviderMock);

		Object obj = EnumValuesDataHelper.convertToEnumValue(columnDataSpecDigest, enumValues.get(0).getDeployID() + ","
				+ enumValues.get(1).getDeployID(), domainClassProviderMock, enumerationSourceProxyMock);
		assertTrue(EnumValues.class.isInstance(obj));
		assertEquals(2, ((EnumValues<?>) obj).size());
	}

	@Test
	public void testConvertToEnumValueHappyCaseForDomainAttributeSingleSelect() throws Exception {
		List<EnumValue> enumValues = new ArrayList<EnumValue>();
		enumValues.add(createEnumValue());
		enumValues.add(createEnumValue());
		enumValues.add(createEnumValue());
		DomainClass dc = attachDomainAttributes(createDomainClass(), 1);
		for (EnumValue ev : enumValues) {
			dc.getDomainAttributes().get(0).addEnumValue(ev);
		}

		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.DOMAIN_ATTRIBUTE);
		columnDataSpecDigest.setAttributeMap(dc.getName() + "." + dc.getDomainAttributes().get(0).getName());

		expect(domainClassProviderMock.getDomainClass(dc.getName())).andReturn(dc);
		replay(domainClassProviderMock);

		Object obj = EnumValuesDataHelper.convertToEnumValue(
				columnDataSpecDigest,
				enumValues.get(1).getDeployID().toString(),
				domainClassProviderMock,
				enumerationSourceProxyMock);
		assertTrue(obj instanceof EnumValue);
		assertEquals(enumValues.get(1), obj);
	}

	@Test
	public void testConvertToEnumValueHappyCaseForExternalSourceMultiSelect() throws Exception {
		List<EnumValue> enumValues = new ArrayList<EnumValue>();
		enumValues.add(createEnumValue());
		enumValues.add(createEnumValue());
		enumValues.add(createEnumValue());
		for (EnumValue ev : enumValues) {
			ev.setDeployID(null);
		}
		String enumSourceName = "ES" + createString();

		columnDataSpecDigest.setIsMultiSelectAllowed(true);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setEnumSourceName(enumSourceName);

		expect(enumerationSourceProxyMock.getAllEnumValues(enumSourceName)).andReturn(enumValues);
		replay(enumerationSourceProxyMock);

		Object obj = EnumValuesDataHelper.convertToEnumValue(columnDataSpecDigest, enumValues.get(0).getDeployValue() + ","
				+ enumValues.get(1).getDeployValue(), domainClassProviderMock, enumerationSourceProxyMock);
		assertTrue(EnumValues.class.isInstance(obj));
		assertEquals(2, ((EnumValues<?>) obj).size());
	}

	@Test
	public void testConvertToEnumValueHappyCaseForExternalSourceSingleSelect() throws Exception {
		List<EnumValue> enumValues = new ArrayList<EnumValue>();
		enumValues.add(createEnumValue());
		enumValues.add(createEnumValue());
		enumValues.add(createEnumValue());
		for (EnumValue ev : enumValues) {
			ev.setDeployID(null);
		}
		String enumSourceName = "ES" + createString();

		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setEnumSourceName(enumSourceName);

		expect(enumerationSourceProxyMock.getAllEnumValues(enumSourceName)).andReturn(enumValues);
		replay(enumerationSourceProxyMock);

		Object obj = EnumValuesDataHelper.convertToEnumValue(
				columnDataSpecDigest,
				enumValues.get(1).getDeployValue(),
				domainClassProviderMock,
				enumerationSourceProxyMock);
		assertTrue(obj instanceof EnumValue);
		assertEquals(enumValues.get(1), obj);
	}

	@Test
	public void testConvertToEnumValueWithNullDataSpecThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(EnumValuesDataHelper.class, "convertToEnumValue", new Class[] { ColumnDataSpecDigest.class, String.class,
				DomainClassProvider.class, EnumerationSourceProxy.class }, new Object[] { null, "", domainClassProviderMock,
				enumerationSourceProxyMock });
	}

	@Test
	public void testConvertToEnumValueWithNullStringForMultiSelectReturnsEmptyEnumValues() throws Exception {
		columnDataSpecDigest.setIsMultiSelectAllowed(true);
		Object obj = EnumValuesDataHelper.convertToEnumValue(columnDataSpecDigest, null, domainClassProviderMock, enumerationSourceProxyMock);
		assertTrue(EnumValues.class.isInstance(obj));
		assertEquals(0, ((EnumValues<?>) obj).size());
	}

	@Test
	public void testConvertToEnumValueWithNullStringForSingleSelectReturnsNull() throws Exception {
		assertNull(EnumValuesDataHelper.convertToEnumValue(columnDataSpecDigest, null, domainClassProviderMock, enumerationSourceProxyMock));
	}
}

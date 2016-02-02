package com.mindbox.pe.model.table;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.easymock.MockControl;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.DomainClassProvider;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.ColumnDataSpecDigest.EnumSourceType;

public class EnumValuesDataHelperTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("EnumValuesDataHelperTest Tests");
		suite.addTestSuite(EnumValuesDataHelperTest.class);
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

	public EnumValuesDataHelperTest(String name) {
		super(name);
	}

	protected void setUpMocks() {
		domainClassProviderMockControl = MockControl.createControl(DomainClassProvider.class);
		domainClassProviderMock = (DomainClassProvider) domainClassProviderMockControl.getMock();
		enumerationSourceProxyMockControl = MockControl.createControl(EnumerationSourceProxy.class);
		enumerationSourceProxyMock = (EnumerationSourceProxy) enumerationSourceProxyMockControl.getMock();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setUpMocks();
		columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
	}

	public void testConvertToEnumValueWithNullDataSpecThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(EnumValuesDataHelper.class, "convertToEnumValue", new Class[] {
				ColumnDataSpecDigest.class,
				String.class,
				DomainClassProvider.class,
				EnumerationSourceProxy.class }, new Object[] { null, "", domainClassProviderMock, enumerationSourceProxyMock });
	}

	public void testConvertToEnumValueWithNullStringForMultiSelectReturnsEmptyEnumValues() throws Exception {
		columnDataSpecDigest.setIsMultiSelectAllowed(true);
		Object obj = EnumValuesDataHelper.convertToEnumValue(
				columnDataSpecDigest,
				null,
				domainClassProviderMock,
				enumerationSourceProxyMock);
		assertTrue(EnumValues.class.isInstance(obj));
		assertEquals(0, ((EnumValues<?>) obj).size());
	}

	public void testConvertToEnumValueWithNullStringForSingleSelectReturnsNull() throws Exception {
		assertNull(EnumValuesDataHelper.convertToEnumValue(columnDataSpecDigest, null, domainClassProviderMock, enumerationSourceProxyMock));
	}

	public void testConvertToEnumValueHappyCaseForDomainAttributeSingleSelect() throws Exception {
		List<EnumValue> enumValues = new ArrayList<EnumValue>();
		enumValues.add(ObjectMother.createEnumValue());
		enumValues.add(ObjectMother.createEnumValue());
		enumValues.add(ObjectMother.createEnumValue());
		DomainClass dc = ObjectMother.attachDomainAttributes(ObjectMother.createDomainClass(), 1);
		for (EnumValue ev : enumValues) {
			dc.getDomainAttributes().get(0).addEnumValue(ev);
		}

		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.DOMAIN_ATTRIBUTE);
		columnDataSpecDigest.setAttributeMap(dc.getName() + "." + dc.getDomainAttributes().get(0).getName());

		domainClassProviderMockControl.expectAndReturn(domainClassProviderMock.getDomainClass(dc.getName()), dc);
		domainClassProviderMockControl.replay();

		Object obj = EnumValuesDataHelper.convertToEnumValue(
				columnDataSpecDigest,
				enumValues.get(1).getDeployID().toString(),
				domainClassProviderMock,
				enumerationSourceProxyMock);
		assertTrue(obj instanceof EnumValue);
		assertEquals(enumValues.get(1), obj);
	}

	public void testConvertToEnumValueHappyCaseForExternalSourceSingleSelect() throws Exception {
		List<EnumValue> enumValues = new ArrayList<EnumValue>();
		enumValues.add(ObjectMother.createEnumValue());
		enumValues.add(ObjectMother.createEnumValue());
		enumValues.add(ObjectMother.createEnumValue());
		for (EnumValue ev : enumValues) {
			ev.setDeployID(null);
		}
		String enumSourceName = "ES" + ObjectMother.createString();

		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setEnumSourceName(enumSourceName);

		enumerationSourceProxyMockControl.expectAndReturn(enumerationSourceProxyMock.getAllEnumValues(enumSourceName), enumValues);
		enumerationSourceProxyMockControl.replay();

		Object obj = EnumValuesDataHelper.convertToEnumValue(
				columnDataSpecDigest,
				enumValues.get(1).getDeployValue(),
				domainClassProviderMock,
				enumerationSourceProxyMock);
		assertTrue(obj instanceof EnumValue);
		assertEquals(enumValues.get(1), obj);
	}

	public void testConvertToEnumValueHappyCaseForDomainAttributeMultiSelect() throws Exception {
		List<EnumValue> enumValues = new ArrayList<EnumValue>();
		enumValues.add(ObjectMother.createEnumValue());
		enumValues.add(ObjectMother.createEnumValue());
		enumValues.add(ObjectMother.createEnumValue());
		DomainClass dc = ObjectMother.attachDomainAttributes(ObjectMother.createDomainClass(), 1);
		for (EnumValue ev : enumValues) {
			dc.getDomainAttributes().get(0).addEnumValue(ev);
		}

		columnDataSpecDigest.setIsMultiSelectAllowed(true);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.DOMAIN_ATTRIBUTE);
		columnDataSpecDigest.setAttributeMap(dc.getName() + "." + dc.getDomainAttributes().get(0).getName());

		domainClassProviderMockControl.expectAndReturn(domainClassProviderMock.getDomainClass(dc.getName()), dc);
		domainClassProviderMockControl.replay();

		Object obj = EnumValuesDataHelper.convertToEnumValue(columnDataSpecDigest, enumValues.get(0).getDeployID() + ","
				+ enumValues.get(1).getDeployID(), domainClassProviderMock, enumerationSourceProxyMock);
		assertTrue(EnumValues.class.isInstance(obj));
		assertEquals(2, ((EnumValues<?>) obj).size());
	}

	public void testConvertToEnumValueHappyCaseForExternalSourceMultiSelect() throws Exception {
		List<EnumValue> enumValues = new ArrayList<EnumValue>();
		enumValues.add(ObjectMother.createEnumValue());
		enumValues.add(ObjectMother.createEnumValue());
		enumValues.add(ObjectMother.createEnumValue());
		for (EnumValue ev : enumValues) {
			ev.setDeployID(null);
		}
		String enumSourceName = "ES" + ObjectMother.createString();

		columnDataSpecDigest.setIsMultiSelectAllowed(true);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.EXTERNAL);
		columnDataSpecDigest.setEnumSourceName(enumSourceName);

		enumerationSourceProxyMockControl.expectAndReturn(enumerationSourceProxyMock.getAllEnumValues(enumSourceName), enumValues);
		enumerationSourceProxyMockControl.replay();

		Object obj = EnumValuesDataHelper.convertToEnumValue(columnDataSpecDigest, enumValues.get(0).getDeployValue() + ","
				+ enumValues.get(1).getDeployValue(), domainClassProviderMock, enumerationSourceProxyMock);
		assertTrue(EnumValues.class.isInstance(obj));
		assertEquals(2, ((EnumValues<?>) obj).size());
	}

	public void testConvertToEnumValueForEnumValueDataSpecWithSameDisplayValueAndDeployIDSingleSelect() throws Exception {
		List<EnumValue> enumValues = new ArrayList<EnumValue>();
		enumValues.add(ObjectMother.createEnumValue());
		enumValues.add(ObjectMother.createEnumValue());
		enumValues.add(ObjectMother.createEnumValue());
		enumValues.get(2).setDisplayLabel(enumValues.get(0).getDeployID().toString());

		DomainClass dc = ObjectMother.attachDomainAttributes(ObjectMother.createDomainClass(), 1);
		for (EnumValue ev : enumValues) {
			dc.getDomainAttributes().get(0).addEnumValue(ev);
		}

		columnDataSpecDigest.setIsMultiSelectAllowed(false);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.DOMAIN_ATTRIBUTE);
		columnDataSpecDigest.setAttributeMap(dc.getName() + "." + dc.getDomainAttributes().get(0).getName());

		domainClassProviderMockControl.expectAndReturn(domainClassProviderMock.getDomainClass(dc.getName()), dc, 3);
		domainClassProviderMockControl.replay();

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

	public void testConvertToEnumValueForEnumValueDataSpecWithSameDisplayValueAndDeployIDMultiSelect() throws Exception {
		List<EnumValue> enumValues = new ArrayList<EnumValue>();
		enumValues.add(ObjectMother.createEnumValue());
		enumValues.add(ObjectMother.createEnumValue());
		enumValues.add(ObjectMother.createEnumValue());
		enumValues.get(2).setDisplayLabel(enumValues.get(0).getDeployID().toString());

		DomainClass dc = ObjectMother.attachDomainAttributes(ObjectMother.createDomainClass(), 1);
		for (EnumValue ev : enumValues) {
			dc.getDomainAttributes().get(0).addEnumValue(ev);
		}

		columnDataSpecDigest.setIsMultiSelectAllowed(true);
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.DOMAIN_ATTRIBUTE);
		columnDataSpecDigest.setAttributeMap(dc.getName() + "." + dc.getDomainAttributes().get(0).getName());

		domainClassProviderMockControl.expectAndReturn(domainClassProviderMock.getDomainClass(dc.getName()), dc, 4);
		domainClassProviderMockControl.replay();

		Object obj = EnumValuesDataHelper.convertToEnumValue(
				columnDataSpecDigest,
				enumValues.get(0).getDeployID() + "," + enumValues.get(1).getDeployID() + "," + enumValues.get(2).getDeployID(),
				domainClassProviderMock,
				enumerationSourceProxyMock);
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

	public void testConvertToEnumValueHappyCaseForColumnDefinedEnumsSingleSelect() throws Exception {
		columnDataSpecDigest.setEnumSourceType(EnumSourceType.COLUMN);
		columnDataSpecDigest.addColumnEnumValue("Enum1");
		columnDataSpecDigest.addColumnEnumValue("Enum2");
		columnDataSpecDigest.addColumnEnumValue("Enum3");
		Object obj = EnumValuesDataHelper.convertToEnumValue(
				columnDataSpecDigest,
				"Enum2",
				domainClassProviderMock,
				enumerationSourceProxyMock);
		assertEquals(EnumValue.class, obj.getClass());
		assertEquals("Enum2", ((EnumValue) obj).getDeployValue());
	}

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
}

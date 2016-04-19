package com.mindbox.pe.server.cache;

import static com.mindbox.pe.server.ServerTestObjectMother.createEnumValuesAsList;
import static com.mindbox.pe.unittest.UnitTestHelper.assertArrayEqualsIgnoresOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.server.AbstractTestWithTestConfig;

public class DeploymentManagerTest extends AbstractTestWithTestConfig {

	private String className, attributeName;

	@SuppressWarnings("unchecked")
	private List<Object> invokeGetEnumValueList(String className, String attributeName) {
		return (List<Object>) ReflectionUtil.executePrivate(DeploymentManager.getInstance(), "getEnumValueList", new Class[] { String.class, String.class }, new Object[] { className, attributeName });
	}

	@Test
	public void testGetEnumDeployValuesExcludeNone() throws Exception {
		invokeGetEnumValueList(className, attributeName);

		List<EnumValue> toExcludeList = new ArrayList<EnumValue>();
		EnumValues<EnumValue> evs = new EnumValues<EnumValue>(toExcludeList);

		String[] excludedArray = DeploymentManager.getInstance().getEnumDeployValues(className, attributeName, evs);

		List<String> expectedExcludedList = new ArrayList<String>();
		String[] expectedExcludedArray = (String[]) expectedExcludedList.toArray(new String[0]);

		assertArrayEqualsIgnoresOrder(excludedArray, expectedExcludedArray);
	}

	@Test
	public void testGetEnumDeployValuesExcludeOne() throws Exception {
		List<Object> ls = invokeGetEnumValueList(className, attributeName);

		List<EnumValue> toExcludeList = new ArrayList<EnumValue>();
		EnumValue enumValue0 = (EnumValue) ReflectionUtil.getPrivate(ls.get(0), "enumValueInstance");
		toExcludeList.add(enumValue0);
		EnumValues<EnumValue> evs = new EnumValues<EnumValue>(toExcludeList);

		String[] excludedArray = DeploymentManager.getInstance().getEnumDeployValues(className, attributeName, evs);

		List<String> expectedExcludedList = new ArrayList<String>();
		expectedExcludedList.add(enumValue0.getDeployValue());
		String[] expectedExcludedArray = expectedExcludedList.toArray(new String[0]);

		assertArrayEqualsIgnoresOrder(excludedArray, expectedExcludedArray);
	}

	@Test
	public void testGetEnumDeployValuesExcludeTwo() throws Exception {
		List<Object> ls = invokeGetEnumValueList(className, attributeName);
		EnumValue enumValue0 = (EnumValue) ReflectionUtil.getPrivate(ls.get(0), "enumValueInstance");
		EnumValue enumValue1 = (EnumValue) ReflectionUtil.getPrivate(ls.get(1), "enumValueInstance");
		List<EnumValue> toExcludeList = new ArrayList<EnumValue>();
		toExcludeList.add(enumValue0);
		toExcludeList.add(enumValue1);
		EnumValues<EnumValue> evs = new EnumValues<EnumValue>(toExcludeList);

		String[] excludedArray = DeploymentManager.getInstance().getEnumDeployValues(className, attributeName, evs);

		List<String> expectedExcludedList = new ArrayList<String>();
		expectedExcludedList.add(enumValue0.getDeployValue());
		expectedExcludedList.add(enumValue1.getDeployValue());
		String[] expectedExcludedArray = expectedExcludedList.toArray(new String[0]);
		assertArrayEqualsIgnoresOrder(excludedArray, expectedExcludedArray);
	}

	@Test
	public void testGetEnumDeployValuesExcludeAll() throws Exception {
		List<Object> ls = invokeGetEnumValueList(className, attributeName);
		EnumValue enumValue0 = (EnumValue) ReflectionUtil.getPrivate(ls.get(0), "enumValueInstance");
		EnumValue enumValue1 = (EnumValue) ReflectionUtil.getPrivate(ls.get(1), "enumValueInstance");
		EnumValue enumValue2 = (EnumValue) ReflectionUtil.getPrivate(ls.get(2), "enumValueInstance");
		List<EnumValue> toExcludeList = new ArrayList<EnumValue>();
		toExcludeList.add(enumValue0);
		toExcludeList.add(enumValue1);
		toExcludeList.add(enumValue2);
		EnumValues<EnumValue> evs = new EnumValues<EnumValue>(toExcludeList);

		String[] excludedArray = DeploymentManager.getInstance().getEnumDeployValues(className, attributeName, evs);

		List<String> expectedExcludedList = new ArrayList<String>();
		expectedExcludedList.add(enumValue0.getDeployValue());
		expectedExcludedList.add(enumValue1.getDeployValue());
		expectedExcludedList.add(enumValue2.getDeployValue());
		String[] expectedExcludedArray = expectedExcludedList.toArray(new String[0]);
		assertArrayEqualsIgnoresOrder(excludedArray, expectedExcludedArray);
	}

	@Test
	public void testGetEnumDeployValueWithDeployIDReturnsDeployValue() throws Exception {
		Object obj = invokeGetEnumValueList(className, attributeName).get(0);
		EnumValue enumValue = (EnumValue) ReflectionUtil.getPrivate(obj, "enumValueInstance");

		assertEquals(enumValue.getDeployValue(), DeploymentManager.getInstance().getEnumDeployValue("domainClass", "attribute", enumValue.getDeployID().toString(), false, true));
		assertEquals(enumValue.getDeployValue(), DeploymentManager.getInstance().getEnumDeployValue("domainClass", "attribute", enumValue.getDeployID().toString(), false, false));
		assertEquals(enumValue.getDeployValue(), DeploymentManager.getInstance().getEnumDeployValue("domainClass", "attribute", enumValue.getDeployID().toString(), true, true));
		assertEquals(enumValue.getDeployValue(), DeploymentManager.getInstance().getEnumDeployValue("domainClass", "attribute", enumValue.getDeployID().toString(), true, false));
	}

	@Test
	public void testGetEnumDeployValueWithDisplayLabelReturnsDeployValue() throws Exception {
		Object obj = invokeGetEnumValueList(className, attributeName).get(0);
		EnumValue enumValue = (EnumValue) ReflectionUtil.getPrivate(obj, "enumValueInstance");
		assertEquals(enumValue.getDeployValue(), DeploymentManager.getInstance().getEnumDeployValue("domainClass", "attribute", enumValue.getDisplayLabel(), true, true));
		assertEquals(enumValue.getDeployValue(), DeploymentManager.getInstance().getEnumDeployValue("domainClass", "attribute", enumValue.getDisplayLabel(), true, false));
	}

	@Test
	public void testGetEnumDeployValueWithDeployValueAndCheckIfDeployValueReturnsSameValue() throws Exception {
		Object obj = invokeGetEnumValueList(className, attributeName).get(0);
		EnumValue enumValue = (EnumValue) ReflectionUtil.getPrivate(obj, "enumValueInstance");
		assertEquals(enumValue.getDeployValue(), DeploymentManager.getInstance().getEnumDeployValue("domainClass", "attribute", enumValue.getDeployValue(), true, true));
	}

	@Test
	public void testGetEnumDeployValueWithDeployValueAndUncheckIfDeployValueReturnsNull() throws Exception {
		Object obj = invokeGetEnumValueList(className, attributeName).get(0);
		EnumValue enumValue = (EnumValue) ReflectionUtil.getPrivate(obj, "enumValueInstance");
		assertNull(DeploymentManager.getInstance().getEnumDeployValue("domainClass", "attribute", enumValue.getDeployValue(), true, false));
	}

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		className = "domainClass";
		attributeName = "attribute";
		DeploymentManager.getInstance().addEnumValueMap(className, attributeName, createEnumValuesAsList(3));
	}

	public void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}
}

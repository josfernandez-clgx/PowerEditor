package com.mindbox.pe.server.cache;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.table.EnumValues;

public class DeploymentManagerTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("DeploymentManagerTest Tests");
		suite.addTestSuite(DeploymentManagerTest.class);
		return suite;
	}

	private String className, attributeName;

	public DeploymentManagerTest(String name) {
		super(name);
	}

	@SuppressWarnings("unchecked")
	private List<Object> invokeGetEnumValueList(String className, String attributeName) {
		return (List<Object>) ReflectionUtil.executePrivate(
				DeploymentManager.getInstance(),
				"getEnumValueList",
				new Class[] { String.class, String.class },
				new Object[] { className, attributeName });
	}

	public void testGetEnumDeployValuesExcludeNone()throws Exception {
		invokeGetEnumValueList(className, attributeName);
		
		List<EnumValue> toExcludeList = new ArrayList<EnumValue>();
		EnumValues<EnumValue> evs = new EnumValues<EnumValue>(toExcludeList);
		
		String[] excludedArray=DeploymentManager.getInstance().getEnumDeployValues( className,  attributeName,  evs);		
		
		List<String> expectedExcludedList = new ArrayList<String>();
		String[] expectedExcludedArray =  (String[]) expectedExcludedList.toArray(new String[0]);
		
		assertEquals(excludedArray,expectedExcludedArray);
	}
	

	public void testGetEnumDeployValuesExcludeOne()throws Exception {
		List<Object> ls = invokeGetEnumValueList(className, attributeName);
		
		List<EnumValue> toExcludeList = new ArrayList<EnumValue>();
		EnumValue enumValue0 = (EnumValue) ReflectionUtil.getPrivate(ls.get(0), "enumValueInstance");				
		toExcludeList.add(enumValue0);				
		EnumValues<EnumValue> evs = new EnumValues<EnumValue>(toExcludeList);
		
		String[] excludedArray=DeploymentManager.getInstance().getEnumDeployValues( className,  attributeName,  evs);		
		
		List<String> expectedExcludedList = new ArrayList<String>();
		expectedExcludedList.add(enumValue0.getDeployValue());
		String[] expectedExcludedArray =  expectedExcludedList.toArray(new String[0]);
		
		assertEquals(excludedArray,expectedExcludedArray);
	}	

	public void testGetEnumDeployValuesExcludeTwo()throws Exception {
		List<Object> ls = invokeGetEnumValueList(className, attributeName);
		EnumValue enumValue0 = (EnumValue) ReflectionUtil.getPrivate(ls.get(0), "enumValueInstance");
		EnumValue enumValue1 = (EnumValue) ReflectionUtil.getPrivate(ls.get(1), "enumValueInstance");
		List<EnumValue> toExcludeList = new ArrayList<EnumValue>();
		toExcludeList.add(enumValue0);
		toExcludeList.add(enumValue1);		
		EnumValues<EnumValue> evs = new EnumValues<EnumValue>(toExcludeList);
		
		String[] excludedArray=DeploymentManager.getInstance().getEnumDeployValues( className,  attributeName,  evs);		
		
		List<String> expectedExcludedList = new ArrayList<String>();
		expectedExcludedList.add(enumValue0.getDeployValue());
		expectedExcludedList.add(enumValue1.getDeployValue());
		String[] expectedExcludedArray =  expectedExcludedList.toArray(new String[0]);
		assertEquals(excludedArray,expectedExcludedArray);
	}
	
	public void testGetEnumDeployValuesExcludeAll()throws Exception {
		List<Object> ls = invokeGetEnumValueList(className, attributeName);
		EnumValue enumValue0 = (EnumValue) ReflectionUtil.getPrivate(ls.get(0), "enumValueInstance");
		EnumValue enumValue1 = (EnumValue) ReflectionUtil.getPrivate(ls.get(1), "enumValueInstance");
		EnumValue enumValue2 = (EnumValue) ReflectionUtil.getPrivate(ls.get(2), "enumValueInstance");
		List<EnumValue> toExcludeList = new ArrayList<EnumValue>();
		toExcludeList.add(enumValue0);
		toExcludeList.add(enumValue1);		
		toExcludeList.add(enumValue2);	
		EnumValues<EnumValue> evs = new EnumValues<EnumValue>(toExcludeList);
		
		String[] excludedArray=DeploymentManager.getInstance().getEnumDeployValues( className,  attributeName,  evs);		
				
		List<String> expectedExcludedList = new ArrayList<String>();
		expectedExcludedList.add(enumValue0.getDeployValue());
		expectedExcludedList.add(enumValue1.getDeployValue());
		expectedExcludedList.add(enumValue2.getDeployValue());
		String[] expectedExcludedArray =  expectedExcludedList.toArray(new String[0]);
		assertEquals(excludedArray,expectedExcludedArray);
	}

	public void testGetEnumDeployValueWithDeployIDReturnsDeployValue() throws Exception {
		Object obj = invokeGetEnumValueList(className, attributeName).get(0);
		EnumValue enumValue = (EnumValue) ReflectionUtil.getPrivate(obj, "enumValueInstance");

		assertEquals(enumValue.getDeployValue(), DeploymentManager.getInstance().getEnumDeployValue(
				"domainClass",
				"attribute",
				enumValue.getDeployID().toString(),
				false,
				true));
		assertEquals(enumValue.getDeployValue(), DeploymentManager.getInstance().getEnumDeployValue(
				"domainClass",
				"attribute",
				enumValue.getDeployID().toString(),
				false,
				false));
		assertEquals(enumValue.getDeployValue(), DeploymentManager.getInstance().getEnumDeployValue(
				"domainClass",
				"attribute",
				enumValue.getDeployID().toString(),
				true,
				true));
		assertEquals(enumValue.getDeployValue(), DeploymentManager.getInstance().getEnumDeployValue(
				"domainClass",
				"attribute",
				enumValue.getDeployID().toString(),
				true,
				false));
	}

	public void testGetEnumDeployValueWithDisplayLabelReturnsDeployValue() throws Exception {
		Object obj = invokeGetEnumValueList(className, attributeName).get(0);
		EnumValue enumValue = (EnumValue) ReflectionUtil.getPrivate(obj, "enumValueInstance");
		assertEquals(enumValue.getDeployValue(), DeploymentManager.getInstance().getEnumDeployValue(
				"domainClass",
				"attribute",
				enumValue.getDisplayLabel(),
				true,
				true));
		assertEquals(enumValue.getDeployValue(), DeploymentManager.getInstance().getEnumDeployValue(
				"domainClass",
				"attribute",
				enumValue.getDisplayLabel(),
				true,
				false));
	}

	public void testGetEnumDeployValueWithDeployValueAndCheckIfDeployValueReturnsSameValue() throws Exception {
		Object obj = invokeGetEnumValueList(className, attributeName).get(0);
		EnumValue enumValue = (EnumValue) ReflectionUtil.getPrivate(obj, "enumValueInstance");
		assertEquals(enumValue.getDeployValue(), DeploymentManager.getInstance().getEnumDeployValue(
				"domainClass",
				"attribute",
				enumValue.getDeployValue(),
				true,
				true));
	}

	public void testGetEnumDeployValueWithDeployValueAndUncheckIfDeployValueReturnsNull() throws Exception {
		Object obj = invokeGetEnumValueList(className, attributeName).get(0);
		EnumValue enumValue = (EnumValue) ReflectionUtil.getPrivate(obj, "enumValueInstance");
		assertNull(DeploymentManager.getInstance().getEnumDeployValue("domainClass", "attribute", enumValue.getDeployValue(), true, false));
	}	

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		className = "domainClass";
		attributeName = "attribute";
		DeploymentManager.getInstance().startLoading();
		DeploymentManager.getInstance().addEnumValueMap(className, attributeName, ObjectMother.createEnumValuesAsList(3));
	}

	protected void tearDown() throws Exception {
		DeploymentManager.getInstance().startLoading();
		config.resetConfiguration();
		super.tearDown();
	}
}

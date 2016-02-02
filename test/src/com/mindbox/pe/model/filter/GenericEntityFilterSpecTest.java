package com.mindbox.pe.model.filter;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.config.ConfigurationManager;

public class GenericEntityFilterSpecTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GenericEntityFilterSpecTest Tests");
		suite.addTestSuite(GenericEntityFilterSpecTest.class);
		return suite;
	}

	private GenericEntityFilterSpec genericEntityFilterSpec;
	private EntityTypeDefinition entityTypeDef;

	public GenericEntityFilterSpecTest(String name) {
		super(name);
	}

	public void testSetInvariantsForParamMapWithNullThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(
				genericEntityFilterSpec,
				"setInvariants",
				new Class[] { Map.class },
				new Object[] { null },
				UnsupportedOperationException.class);
	}

	public void testSetInvariantsForParamMapWithMapThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(
				genericEntityFilterSpec,
				"setInvariants",
				new Class[] { Map.class },
				new Object[] { new HashMap<String, String>() },
				UnsupportedOperationException.class);
	}

	public void testSetInvariantsForParamMapAndHelperWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(genericEntityFilterSpec, "setInvariants", new Class[] { Map.class, Object.class }, new Object[] {
				null,
				entityTypeDef });
	}

	public void testSetInvariantsForParamMapAndHelperWithNullHelperThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(genericEntityFilterSpec, "setInvariants", new Class[] { Map.class, Object.class }, new Object[] {
				new HashMap<String, String>(),
				null }, UnsupportedOperationException.class);
	}

	public void testSetInvariantsForParamMapAndHelperWithInvalidHelperThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(genericEntityFilterSpec, "setInvariants", new Class[] { Map.class, Object.class }, new Object[] {
				new HashMap<String, String>(),
				"helper" }, UnsupportedOperationException.class);
	}

	public void testSetInvariantsSetsNamePropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put(GenericEntityFilterSpec.KEY_NAME, "name1");
		genericEntityFilterSpec.setInvariants(map, entityTypeDef);
		assertEquals("name1", genericEntityFilterSpec.getNameCriterion());
	}

	public void testSetInvariantsSetsParentIDPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put(GenericEntityFilterSpec.KEY_PARENT, "4");
		genericEntityFilterSpec.setInvariants(map, entityTypeDef);
		assertEquals(4, genericEntityFilterSpec.getParentIDCriteria());
	}

	public void testSetInvariantsSetsTrueBooleanPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("assumable", Boolean.TRUE.toString());
		genericEntityFilterSpec.setInvariants(map, entityTypeDef);
		assertEquals(Boolean.TRUE, genericEntityFilterSpec.getPropertyCriterion("assumable"));
	}

	public void testSetInvariantsSetsFalseBooleanPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("assumable", Boolean.FALSE.toString());
		genericEntityFilterSpec.setInvariants(map, entityTypeDef);
		assertEquals(Boolean.FALSE, genericEntityFilterSpec.getPropertyCriterion("assumable"));
	}

	public void testSetInvariantsSetsCurrencyPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("hazard.insurance.amount", "1257950.50");
		genericEntityFilterSpec.setInvariants(map, entityTypeDef);
		assertEquals(new Double(1257950.50), genericEntityFilterSpec.getPropertyCriterion("hazard.insurance.amount"));
	}

	public void testSetInvariantsSetsDoublePropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("arm.later.adjust.cap", "957950.49");
		genericEntityFilterSpec.setInvariants(map, entityTypeDef);
		assertEquals(new Double(957950.49), genericEntityFilterSpec.getPropertyCriterion("arm.later.adjust.cap"));
	}

	public void testSetInvariantsSetsFloatPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("arm.first.payment.cap", "789.1234");
		genericEntityFilterSpec.setInvariants(map, entityTypeDef);
		assertEquals(new Float(789.1234f), genericEntityFilterSpec.getPropertyCriterion("arm.first.payment.cap"));
	}

	public void testSetInvariantsSetsPercentPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("late.charge.percent", "23.125");
		genericEntityFilterSpec.setInvariants(map, entityTypeDef);
		assertEquals(new Float(23.125f), genericEntityFilterSpec.getPropertyCriterion("late.charge.percent"));
	}

	public void testSetInvariantsSetsIntPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("late.charge.type", "4567");
		genericEntityFilterSpec.setInvariants(map, entityTypeDef);
		assertEquals(new Integer(4567), genericEntityFilterSpec.getPropertyCriterion("late.charge.type"));
	}

	public void testSetInvariantsSetsLongPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("deferred.limit", "900500100789");
		genericEntityFilterSpec.setInvariants(map, entityTypeDef);
		assertEquals(new Long(900500100789L), genericEntityFilterSpec.getPropertyCriterion("deferred.limit"));
	}

	public void testSetInvariantsSetsDatePropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("activation.date", "2006-07-30 12:34:55");
		genericEntityFilterSpec.setInvariants(map, entityTypeDef);
		assertEquals(getDate(2006, 7, 30, 12, 34, 55), genericEntityFilterSpec.getPropertyCriterion("activation.date"));
	}

	public void testSetInvariantsSetsEnumPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("loan.type", "enumValue");
		genericEntityFilterSpec.setInvariants(map, entityTypeDef);
		assertEquals("enumValue", genericEntityFilterSpec.getPropertyCriterion("loan.type"));
	}

	public void testSetInvariantsSetsSymbolPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("pricing.group", "1234symbol_");
		genericEntityFilterSpec.setInvariants(map, entityTypeDef);
		assertEquals("1234symbol_", genericEntityFilterSpec.getPropertyCriterion("pricing.group"));
	}

	public void testSetInvariantsSetsStringPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("product.type", "string ~!@#$%|[]{}&*^");
		genericEntityFilterSpec.setInvariants(map, entityTypeDef);
		assertEquals("string ~!@#$%|[]{}&*^", genericEntityFilterSpec.getPropertyCriterion("product.type"));
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		entityTypeDef = ConfigurationManager.getInstance().getEntityConfiguration().findEntityTypeDefinition(
				GenericEntityType.forName("product"));
		genericEntityFilterSpec = new GenericEntityFilterSpec(GenericEntityType.forName("product"), -1, "filterSpec");
	}

	protected void tearDown() throws Exception {
		genericEntityFilterSpec = null;
		entityTypeDef = null;
		config.resetConfiguration();
		super.tearDown();
	}
}

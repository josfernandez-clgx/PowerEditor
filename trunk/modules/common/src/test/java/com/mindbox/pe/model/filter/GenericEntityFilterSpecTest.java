package com.mindbox.pe.model.filter;

import static com.mindbox.pe.common.CommonTestObjectMother.createEntityPropertyDefinition;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static com.mindbox.pe.unittest.UnitTestHelper.getDate;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.common.AbstractTestWithGenericEntityType;
import com.mindbox.pe.xsd.config.EntityPropertyType;

public class GenericEntityFilterSpecTest extends AbstractTestWithGenericEntityType {

	private GenericEntityFilterSpec genericEntityFilterSpec;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		entityTypeDefinition.getEntityProperty().add(createEntityPropertyDefinition(EntityPropertyType.BOOLEAN, "assumable"));
		entityTypeDefinition.getEntityProperty().add(createEntityPropertyDefinition(EntityPropertyType.CURRENCY, "hazard.insurance.amount"));
		entityTypeDefinition.getEntityProperty().add(createEntityPropertyDefinition(EntityPropertyType.DATE, "activation.date"));
		entityTypeDefinition.getEntityProperty().add(createEntityPropertyDefinition(EntityPropertyType.LONG, "deferred.limit"));
		entityTypeDefinition.getEntityProperty().add(createEntityPropertyDefinition(EntityPropertyType.FLOAT, "arm.first.payment.cap"));
		entityTypeDefinition.getEntityProperty().add(createEntityPropertyDefinition(EntityPropertyType.INTEGER, "late.charge.type"));
		entityTypeDefinition.getEntityProperty().add(createEntityPropertyDefinition(EntityPropertyType.DOUBLE, "arm.later.adjust.cap"));
		entityTypeDefinition.getEntityProperty().add(createEntityPropertyDefinition(EntityPropertyType.PERCENT, "late.charge.percent"));
		entityTypeDefinition.getEntityProperty().add(createEntityPropertyDefinition(EntityPropertyType.STRING, "product.type"));
		entityTypeDefinition.getEntityProperty().add(createEntityPropertyDefinition(EntityPropertyType.SYMBOL, "pricing.group"));
		entityTypeDefinition.getEntityProperty().add(createEntityPropertyDefinition(EntityPropertyType.ENUM, "loan.type"));

		genericEntityFilterSpec = new GenericEntityFilterSpec(entityType, -1, "filterSpec");
	}

	@Test
	public void testSetInvariantsForParamMapAndHelperWithInvalidHelperThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(
				genericEntityFilterSpec,
				"setInvariants",
				new Class[] { Map.class, Object.class },
				new Object[] { new HashMap<String, String>(), "helper" },
				UnsupportedOperationException.class);
	}

	@Test
	public void testSetInvariantsForParamMapAndHelperWithNullHelperThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(
				genericEntityFilterSpec,
				"setInvariants",
				new Class[] { Map.class, Object.class },
				new Object[] { new HashMap<String, String>(), null },
				UnsupportedOperationException.class);
	}

	@Test
	public void testSetInvariantsForParamMapAndHelperWithNullThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(genericEntityFilterSpec, "setInvariants", new Class[] { Map.class, Object.class }, new Object[] { null, entityTypeDefinition });
	}

	@Test
	public void testSetInvariantsForParamMapWithMapThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(genericEntityFilterSpec, "setInvariants", new Class[] { Map.class }, new Object[] { new HashMap<String, String>() }, UnsupportedOperationException.class);
	}

	@Test
	public void testSetInvariantsForParamMapWithNullThrowsUnsupportedOperationException() throws Exception {
		assertThrowsException(genericEntityFilterSpec, "setInvariants", new Class[] { Map.class }, new Object[] { null }, UnsupportedOperationException.class);
	}

	@Test
	public void testSetInvariantsSetsCurrencyPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("hazard.insurance.amount", "1257950.50");
		genericEntityFilterSpec.setInvariants(map, entityTypeDefinition);
		assertEquals(new Double(1257950.50), genericEntityFilterSpec.getPropertyCriterion("hazard.insurance.amount"));
	}

	@Test
	public void testSetInvariantsSetsDatePropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("activation.date", "2006-07-30 12:34:55");
		genericEntityFilterSpec.setInvariants(map, entityTypeDefinition);
		assertEquals(getDate(2006, 7, 30, 12, 34, 55), genericEntityFilterSpec.getPropertyCriterion("activation.date"));
	}

	@Test
	public void testSetInvariantsSetsDoublePropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("arm.later.adjust.cap", "957950.49");
		genericEntityFilterSpec.setInvariants(map, entityTypeDefinition);
		assertEquals(new Double(957950.49), genericEntityFilterSpec.getPropertyCriterion("arm.later.adjust.cap"));
	}

	@Test
	public void testSetInvariantsSetsEnumPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("loan.type", "enumValue");
		genericEntityFilterSpec.setInvariants(map, entityTypeDefinition);
		assertEquals("enumValue", genericEntityFilterSpec.getPropertyCriterion("loan.type"));
	}

	@Test
	public void testSetInvariantsSetsFalseBooleanPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("assumable", Boolean.FALSE.toString());
		genericEntityFilterSpec.setInvariants(map, entityTypeDefinition);
		assertEquals(Boolean.FALSE, genericEntityFilterSpec.getPropertyCriterion("assumable"));
	}

	@Test
	public void testSetInvariantsSetsFloatPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("arm.first.payment.cap", "789.1234");
		genericEntityFilterSpec.setInvariants(map, entityTypeDefinition);
		assertEquals(new Float(789.1234f), genericEntityFilterSpec.getPropertyCriterion("arm.first.payment.cap"));
	}

	@Test
	public void testSetInvariantsSetsIntPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("late.charge.type", "4567");
		genericEntityFilterSpec.setInvariants(map, entityTypeDefinition);
		assertEquals(new Integer(4567), genericEntityFilterSpec.getPropertyCriterion("late.charge.type"));
	}

	@Test
	public void testSetInvariantsSetsLongPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("deferred.limit", "900500100789");
		genericEntityFilterSpec.setInvariants(map, entityTypeDefinition);
		assertEquals(new Long(900500100789L), genericEntityFilterSpec.getPropertyCriterion("deferred.limit"));
	}

	@Test
	public void testSetInvariantsSetsNamePropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put(GenericEntityFilterSpec.KEY_NAME, "name1");
		genericEntityFilterSpec.setInvariants(map, entityTypeDefinition);
		assertEquals("name1", genericEntityFilterSpec.getNameCriterion());
	}

	@Test
	public void testSetInvariantsSetsParentIDPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put(GenericEntityFilterSpec.KEY_PARENT, "4");
		genericEntityFilterSpec.setInvariants(map, entityTypeDefinition);
		assertEquals(4, genericEntityFilterSpec.getParentIDCriteria());
	}

	@Test
	public void testSetInvariantsSetsPercentPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("late.charge.percent", "23.125");
		genericEntityFilterSpec.setInvariants(map, entityTypeDefinition);
		assertEquals(new Float(23.125f), genericEntityFilterSpec.getPropertyCriterion("late.charge.percent"));
	}

	@Test
	public void testSetInvariantsSetsStringPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("product.type", "string ~!@#$%|[]{}&*^");
		genericEntityFilterSpec.setInvariants(map, entityTypeDefinition);
		assertEquals("string ~!@#$%|[]{}&*^", genericEntityFilterSpec.getPropertyCriterion("product.type"));
	}

	@Test
	public void testSetInvariantsSetsSymbolPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("pricing.group", "1234symbol_");
		genericEntityFilterSpec.setInvariants(map, entityTypeDefinition);
		assertEquals("1234symbol_", genericEntityFilterSpec.getPropertyCriterion("pricing.group"));
	}

	@Test
	public void testSetInvariantsSetsTrueBooleanPropertyCorrectly() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("assumable", Boolean.TRUE.toString());
		genericEntityFilterSpec.setInvariants(map, entityTypeDefinition);
		assertEquals(Boolean.TRUE, genericEntityFilterSpec.getPropertyCriterion("assumable"));
	}

}

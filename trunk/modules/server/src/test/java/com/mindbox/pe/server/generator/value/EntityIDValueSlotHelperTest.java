package com.mindbox.pe.server.generator.value;

import static com.mindbox.pe.server.ServerTestObjectMother.createGuidelineGenerateParams;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsException;
import static com.mindbox.pe.unittest.UnitTestHelper.assertThrowsNullPointerException;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.generator.rule.EntityIDValueSlot;
import com.mindbox.pe.server.generator.rule.RuleObjectMother;
import com.mindbox.pe.server.generator.rule.ValueSlot;

public class EntityIDValueSlotHelperTest extends AbstractTestWithTestConfig {

	private EntityIDValueSlotHelper entityIDValueSlotHelper;
	private GenericEntityType entityType;

	@Test
	public void testGenerateValueWithIncorrectValueSlotTypeThrowsRuleGenerationException() throws Exception {
		assertThrowsException(entityIDValueSlotHelper, "generateValue", new Class[] { GuidelineGenerateParams.class, ValueSlot.class }, new Object[] {
				null, RuleObjectMother.createCategoryIDValueSlot(entityType) }, RuleGenerationException.class);
	}

	@Test
	public void testGenerateValueWithNullGenerateParamsThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(
				entityIDValueSlotHelper,
				"generateValue",
				new Class[] { GuidelineGenerateParams.class, ValueSlot.class },
				new Object[] { null, RuleObjectMother.createEntityIDValueSlot(entityType) });
	}

	@Test
	public void testGenerateValueWithGenericEntityInContextReturnsVariableName() throws Exception {
		GuidelineGenerateParams generateParams = createGuidelineGenerateParams();
		ProductGrid productGrid = (ProductGrid) ReflectionUtil.getPrivate(generateParams, "contextContainer");
		productGrid.addGenericEntityID(entityType, 1);
		EntityIDValueSlot entityIDValueSlot = RuleObjectMother.createEntityIDValueSlot(entityType);
		assertEquals(entityIDValueSlot.getEntityVariableName(), entityIDValueSlotHelper.generateValue(generateParams, entityIDValueSlot));
	}

	@Test
	public void testGenerateValueWithGenericCategoryInContextReturnsVariableName() throws Exception {
		GuidelineGenerateParams generateParams = createGuidelineGenerateParams();
		ProductGrid productGrid = (ProductGrid) ReflectionUtil.getPrivate(generateParams, "contextContainer");
		productGrid.addGenericCategoryID(entityType, 1);
		EntityIDValueSlot entityIDValueSlot = RuleObjectMother.createEntityIDValueSlot(entityType);
		assertEquals(entityIDValueSlot.getEntityVariableName(), entityIDValueSlotHelper.generateValue(generateParams, entityIDValueSlot));
	}

	@Test
	public void testGenerateValueWithNoContextReturnsNil() throws Exception {
		GuidelineGenerateParams generateParams = createGuidelineGenerateParams();
		assertEquals(
				RuleGeneratorHelper.AE_NIL,
				entityIDValueSlotHelper.generateValue(generateParams, RuleObjectMother.createEntityIDValueSlot(GenericEntityType.forName("product"))));
	}

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		this.entityType = GenericEntityType.forName("product");
		this.entityIDValueSlotHelper = new EntityIDValueSlotHelper();
	}
}

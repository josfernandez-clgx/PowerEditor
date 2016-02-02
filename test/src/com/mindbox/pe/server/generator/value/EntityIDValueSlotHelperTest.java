package com.mindbox.pe.server.generator.value;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.generator.rule.EntityIDValueSlot;
import com.mindbox.pe.server.generator.rule.RuleObjectMother;
import com.mindbox.pe.server.generator.rule.ValueSlot;

public class EntityIDValueSlotHelperTest extends AbstractTestWithTestConfig {

	public static Test suite() {
		TestSuite suite = new TestSuite("EntityIDValueSlotHelperTest Tests");
		suite.addTestSuite(EntityIDValueSlotHelperTest.class);
		return suite;
	}

	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	private EntityIDValueSlotHelper entityIDValueSlotHelper;
	private GenericEntityType entityType;

	public EntityIDValueSlotHelperTest(String name) {
		super(name);
	}

	public void testGenerateValueWithIncorrectValueSlotTypeThrowsRuleGenerationException() throws Exception {
		assertThrowsException(
				entityIDValueSlotHelper,
				"generateValue",
				new Class[] { GuidelineGenerateParams.class, ValueSlot.class },
				new Object[] { null, RuleObjectMother.createCategoryIDValueSlot(entityType) },
				RuleGenerationException.class);
	}

	public void testGenerateValueWithNullGenerateParamsThrowsNullPointerException() throws Exception {
		assertThrowsNullPointerException(entityIDValueSlotHelper, "generateValue", new Class[] { GuidelineGenerateParams.class,
				ValueSlot.class }, new Object[] { null, RuleObjectMother.createEntityIDValueSlot(entityType) });
	}

	public void testGenerateValueWithGenericEntityInContextReturnsVariableName() throws Exception {
		GuidelineGenerateParams generateParams = ObjectMother.createGuidelineGenerateParams();
		ProductGrid productGrid = (ProductGrid) ReflectionUtil.getPrivate(generateParams, "contextContainer");
		productGrid.addGenericEntityID(entityType, 1);
		EntityIDValueSlot entityIDValueSlot = RuleObjectMother.createEntityIDValueSlot(entityType);
		assertEquals(entityIDValueSlot.getEntityVariableName(), entityIDValueSlotHelper.generateValue(generateParams, entityIDValueSlot));
	}

	public void testGenerateValueWithGenericCategoryInContextReturnsVariableName() throws Exception {
		GuidelineGenerateParams generateParams = ObjectMother.createGuidelineGenerateParams();
		ProductGrid productGrid = (ProductGrid) ReflectionUtil.getPrivate(generateParams, "contextContainer");
		productGrid.addGenericCategoryID(entityType, 1);
		EntityIDValueSlot entityIDValueSlot = RuleObjectMother.createEntityIDValueSlot(entityType);
		assertEquals(entityIDValueSlot.getEntityVariableName(), entityIDValueSlotHelper.generateValue(generateParams, entityIDValueSlot));
	}

	public void testGenerateValueWithNoContextReturnsNil() throws Exception {
		GuidelineGenerateParams generateParams = ObjectMother.createGuidelineGenerateParams();
		assertEquals(RuleGeneratorHelper.AE_NIL, entityIDValueSlotHelper.generateValue(generateParams, RuleObjectMother
				.createEntityIDValueSlot(GenericEntityType.forName("product"))));
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		this.entityType = GenericEntityType.forName("product");
		this.entityIDValueSlotHelper = new EntityIDValueSlotHelper();
	}

}

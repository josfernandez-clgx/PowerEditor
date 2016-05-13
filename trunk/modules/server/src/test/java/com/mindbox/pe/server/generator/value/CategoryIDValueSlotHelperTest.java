package com.mindbox.pe.server.generator.value;

import static com.mindbox.pe.server.ServerTestObjectMother.createGuidelineGenerateParams;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

public class CategoryIDValueSlotHelperTest extends AbstractTestWithTestConfig {

	private CategoryIDValueSlotHelper categoryIDValueSlotHelper;

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		this.categoryIDValueSlotHelper = new CategoryIDValueSlotHelper();
	}

	@Test
	public void testGenerateValueWithEmptyProductContextReturnsNill() throws Exception {
		GuidelineGenerateParams ruleParams = createGuidelineGenerateParams();
		assertEquals(RuleGeneratorHelper.AE_NIL, categoryIDValueSlotHelper.generateValue(ruleParams, null));
	}

}

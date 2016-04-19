package com.mindbox.pe.server.generator.value;

import static com.mindbox.pe.server.ServerTestObjectMother.createGuidelineGenerateParams;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

public class CategoryNameValueSlotHelperTest extends AbstractTestWithTestConfig {

	private CategoryNameValueSlotHelper categoryNameValueSlotHelper;

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		this.categoryNameValueSlotHelper = new CategoryNameValueSlotHelper();
	}

	@Test
	public void testGenerateValueWithEmptyProductContextReturnsNill() throws Exception {
		GuidelineGenerateParams ruleParams = createGuidelineGenerateParams();
		assertEquals(RuleGeneratorHelper.AE_NIL, categoryNameValueSlotHelper.generateValue(ruleParams, null));
	}
}

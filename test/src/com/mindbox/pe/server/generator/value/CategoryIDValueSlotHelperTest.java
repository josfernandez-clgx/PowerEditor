package com.mindbox.pe.server.generator.value;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

public class CategoryIDValueSlotHelperTest extends AbstractTestWithTestConfig {
	public static Test suite() {
		TestSuite suite = new TestSuite("CategoryNameValueSlotHelperTest Tests");
		suite.addTestSuite(CategoryNameValueSlotHelperTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private CategoryIDValueSlotHelper categoryIDValueSlotHelper;

	public CategoryIDValueSlotHelperTest(String name) {
		super(name);
	}

	public void testGenerateValueWithEmptyProductContextReturnsNill() throws Exception {
		GuidelineGenerateParams ruleParams = ObjectMother.createGuidelineGenerateParams();
		assertEquals(RuleGeneratorHelper.AE_NIL, categoryIDValueSlotHelper.generateValue(ruleParams, null));
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		this.categoryIDValueSlotHelper = new CategoryIDValueSlotHelper();
	}

}

package com.mindbox.pe.server.generator.value;

import java.lang.reflect.Constructor;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.RuleGenerationConfiguration;
import com.mindbox.pe.server.model.TimeSlice;

public abstract class OperatorHelperTestBase extends AbstractTestWithTestConfig {

	protected final static String DEFAULT_VAR = "?var";

	protected OperatorHelper operatorHelper;
	protected RuleGenerationConfiguration ruleGenerationConfiguration;

	protected OperatorHelperTestBase(String name) {
		super(name);
	}

	/**
	 * This updates {@link #ruleGenerationConfiguration} with an appropriate one for the specified usage type.
	 * @param operatorHelperClassName
	 * @param usageType
	 * @return
	 * @throws Exception
	 */
	protected final OperatorHelper createOperatorHelper(String operatorHelperClassName, TemplateUsageType usageType) throws Exception {
		ruleGenerationConfiguration = ConfigurationManager.getInstance().getRuleGenerationConfiguration(usageType);
		assertNotNull(ruleGenerationConfiguration);
		Class<?> c = Class.forName(operatorHelperClassName);
		Constructor<?> constructor = c.getDeclaredConstructor(new Class[]
			{ RuleGenerationConfiguration.class});
		constructor.setAccessible(true);
		return (OperatorHelper) constructor.newInstance(new Object[]
			{ ruleGenerationConfiguration});
	}

	protected final void testFormatForPattern(String expected, Object value, int op, boolean asString, Reference reference, TimeSlice timeSlice) throws Exception {
		ValueAndComment result = operatorHelper.formatForPattern(value, op, DEFAULT_VAR, asString, reference, timeSlice);
		assertEquals(expected, result.getValue());
	}

	protected final void testFormatForPattern(String expected, Object value, int op, boolean asString, Reference reference) throws Exception {
		ValueAndComment result = operatorHelper.formatForPattern(value, op, DEFAULT_VAR, asString, reference, TimeSlice.createInstance(null, ObjectMother.createDateSynonym()));
		assertEquals(expected, result.getValue());
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}

	protected void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}
}

package com.mindbox.pe.server.generator.value;

import static com.mindbox.pe.server.ServerTestObjectMother.createDateSynonym;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Constructor;

import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.RuleGenerationConfigHelper;
import com.mindbox.pe.server.model.TimeSlice;

public abstract class AbstractOperatorHelperTestBase extends AbstractTestWithTestConfig {

	protected final static String DEFAULT_VAR = "?var";

	protected OperatorHelper operatorHelper;
	protected RuleGenerationConfigHelper ruleGenerationConfiguration;

	/**
	 * This updates {@link #ruleGenerationConfiguration} with an appropriate one for the specified usage type.
	 * 
	 * @param operatorHelperClassName
	 * @param usageType
	 * @return
	 * @throws Exception
	 */
	protected final OperatorHelper createOperatorHelper(String operatorHelperClassName, TemplateUsageType usageType) throws Exception {
		ruleGenerationConfiguration = ConfigurationManager.getInstance().getRuleGenerationConfigHelper(usageType);
		assertNotNull(ruleGenerationConfiguration);
		Class<?> c = Class.forName(operatorHelperClassName);
		Constructor<?> constructor = c.getDeclaredConstructor(new Class[] { RuleGenerationConfigHelper.class });
		constructor.setAccessible(true);
		return (OperatorHelper) constructor.newInstance(new Object[] { ruleGenerationConfiguration });
	}

	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}

	public void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}

	protected final void testFormatForPattern(String expected, Object value, int op, boolean asString, Reference reference) throws Exception {
		ValueAndComment result = operatorHelper.formatForPattern(value, op, DEFAULT_VAR, asString, reference, TimeSlice.createInstance(null, createDateSynonym()), null);
		assertEquals(expected, result.getValue());
	}

	protected final void testFormatForPattern(String expected, Object value, int op, boolean asString, Reference reference, TimeSlice timeSlice) throws Exception {
		ValueAndComment result = operatorHelper.formatForPattern(value, op, DEFAULT_VAR, asString, reference, timeSlice, null);
		assertEquals(expected, result.getValue());
	}
}

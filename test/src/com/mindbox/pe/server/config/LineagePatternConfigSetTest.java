package com.mindbox.pe.server.config;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.server.config.RuleGenerationConfiguration.LineagePatternConfig;

public class LineagePatternConfigSetTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(LineagePatternConfigSetTest.class);
	}

	public static TestSuite suite() {
		TestSuite suite = new TestSuite("LineagePatternConfigSetTest Tests");
		suite.addTestSuite(LineagePatternConfigSetTest.class);
		return suite;
	}

	/**
	 * Constructor for LineagePatternConfigSetTest.
	 * @param arg0
	 */
	public LineagePatternConfigSetTest(String arg0) {
		super(arg0);
	}

	private void assertContainsText(LineagePatternConfig[] configs, String text) {
		for (int i = 0; i < configs.length; i++) {
			if (configs[i].getText().equals(text)) {
				return;
			}
		}
		fail("Specified text not found; text = " + text);
	}

	private void assertContainsVariable(LineagePatternConfig[] configs, String variable) {
		for (int i = 0; i < configs.length; i++) {
			if (configs[i].getVariable().equals(variable)) {
				return;
			}
		}
		fail("Specified text not found; variable = " + variable);
	}

	public void testLineagePatternPrefixSetup() {
		LineagePatternConfigSet configSet = new LineagePatternConfigSet();

		configSet.addLineagePatternConfig(new LineagePatternConfig(true, "AAA,BBB", "text for AAA & BBB", "aaa-bbb"));
		configSet.addLineagePatternConfig(new LineagePatternConfig(true, "AAA", "text for AAA", "aaa"));
		configSet.addLineagePatternConfig(new LineagePatternConfig(true, "BBB,CCC,DDD", "text for BBB & CCC & DDD", "bbb-ccc-ddd"));
		configSet.addLineagePatternConfig(new LineagePatternConfig(true, "BBB", "text for BBB", "bbb2"));
		configSet.addLineagePatternConfig(new LineagePatternConfig(false, "eee", "text for eee", "eee"));

		assertEquals("Pattern set size mismatch", 4, configSet.size());
		assertEquals("Pattern size mismatch for AAA", 2, configSet.getLineagePatternConfigs("AAA").length);
		assertEquals("Pattern size mismatch for BBB", 3, configSet.getLineagePatternConfigs("BBB").length);
		assertEquals("Pattern size mismatch for CCC", 1, configSet.getLineagePatternConfigs("CCC").length);
		assertEquals("Pattern size mismatch for DDD", 1, configSet.getLineagePatternConfigs("DDD").length);
		assertEquals("Pattern size mismatch for eee (should be ignored)", 0, configSet.getLineagePatternConfigs("eee").length);
		assertContainsText(configSet.getLineagePatternConfigs("AAA"), "text for AAA & BBB");
		assertContainsText(configSet.getLineagePatternConfigs("AAA"), "text for AAA");
		assertContainsText(configSet.getLineagePatternConfigs("BBB"), "text for BBB & CCC & DDD");
		assertContainsText(configSet.getLineagePatternConfigs("BBB"), "text for AAA & BBB");
		assertContainsText(configSet.getLineagePatternConfigs("BBB"), "text for BBB");
		assertContainsText(configSet.getLineagePatternConfigs("CCC"), "text for BBB & CCC & DDD");
		assertContainsText(configSet.getLineagePatternConfigs("DDD"), "text for BBB & CCC & DDD");
		assertContainsVariable(configSet.getLineagePatternConfigs("AAA"), "aaa-bbb");
		assertContainsVariable(configSet.getLineagePatternConfigs("AAA"), "aaa");
		assertContainsVariable(configSet.getLineagePatternConfigs("BBB"), "aaa-bbb");
		assertContainsVariable(configSet.getLineagePatternConfigs("BBB"), "bbb-ccc-ddd");
		assertContainsVariable(configSet.getLineagePatternConfigs("BBB"), "bbb2");
		assertContainsVariable(configSet.getLineagePatternConfigs("CCC"), "bbb-ccc-ddd");
		assertContainsVariable(configSet.getLineagePatternConfigs("DDD"), "bbb-ccc-ddd");
	}

	public void testLineagePatternOverride() {
		LineagePatternConfigSet configSet = new LineagePatternConfigSet();

		configSet.addLineagePatternConfig(new LineagePatternConfig(true, "AAA,BBB", "text for AAA & BBB", "aaa-bbb"));
		configSet.addLineagePatternConfig(new LineagePatternConfig(true, "BBB,CCC,DDD", "text for BBB & CCC & DDD", "bbb-ccc-ddd"));

		configSet = LineagePatternConfigSet.newInstance(configSet);
		assertEquals("Pattern set size mismatch before adding", 4, configSet.size());

		configSet.addLineagePatternConfig(new LineagePatternConfig(true, "BBB", "text for BBB2", "bbb"));
		configSet.addLineagePatternConfig(new LineagePatternConfig(true, "BBB", "text for BBB3", "bbb"));

		assertEquals("Pattern set size mismatch after adding", 1, configSet.size());
		assertEquals("Pattern size mismatch for BBB", 2, configSet.getLineagePatternConfigs("BBB").length);
		assertContainsText(configSet.getLineagePatternConfigs("BBB"), "text for BBB2");
		assertContainsText(configSet.getLineagePatternConfigs("BBB"), "text for BBB3");
		assertContainsVariable(configSet.getLineagePatternConfigs("BBB"), "bbb");
	}
}

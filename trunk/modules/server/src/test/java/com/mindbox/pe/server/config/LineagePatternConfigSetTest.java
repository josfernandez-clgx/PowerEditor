package com.mindbox.pe.server.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;
import com.mindbox.pe.xsd.config.LHSPatternType;
import com.mindbox.pe.xsd.config.RuleGenerationLHS;
import com.mindbox.pe.xsd.config.RuleGenerationLHS.Pattern;

public class LineagePatternConfigSetTest extends AbstractTestBase {

	private RuleGenerationLHS ruleGenerationLHS;

	@Before
	public void setUp() throws Exception {
		ruleGenerationLHS = new RuleGenerationLHS();
	}

	private void assertContainsText(final List<Pattern> configs, final String text) {
		for (int i = 0; i < configs.size(); i++) {
			if (configs.get(i).getText().equals(text)) {
				return;
			}
		}
		fail("Specified text not found; text = " + text);
	}

	private void assertContainsVariable(final List<Pattern> configs, final String variable) {
		for (int i = 0; i < configs.size(); i++) {
			if (configs.get(i).getVariable().equals(variable)) {
				return;
			}
		}
		fail("Specified text not found; variable = " + variable);
	}

	@Test
	public void testLineagePatternOverride() {
		Pattern pattern = new Pattern();
		pattern.setType(LHSPatternType.LINEAGE);
		pattern.setGenerate(Boolean.TRUE);
		pattern.setPrefix("AAA,BBB");
		pattern.setText("text for AAA & BBB");
		pattern.setVariable("aaa-bbb");
		ruleGenerationLHS.getPattern().add(pattern);

		pattern = new Pattern();
		pattern.setType(LHSPatternType.LINEAGE);
		pattern.setGenerate(Boolean.TRUE);
		pattern.setPrefix("BBB,CCC,DDD");
		pattern.setText("text for BBB & CCC & DDD");
		pattern.setVariable("bbb-ccc-ddd");
		ruleGenerationLHS.getPattern().add(pattern);

		LineagePatternConfigHelper configSet = new LineagePatternConfigHelper(ruleGenerationLHS);

		assertEquals("Pattern set size mismatch before adding", 4, configSet.size());

		ruleGenerationLHS.getPattern().clear();
		pattern = new Pattern();
		pattern.setType(LHSPatternType.LINEAGE);
		pattern.setGenerate(Boolean.TRUE);
		pattern.setPrefix("BBB");
		pattern.setText("text for BBB2");
		pattern.setVariable("bbb");
		ruleGenerationLHS.getPattern().add(pattern);

		pattern = new Pattern();
		pattern.setType(LHSPatternType.LINEAGE);
		pattern.setGenerate(Boolean.TRUE);
		pattern.setPrefix("BBB");
		pattern.setText("text for BBB3");
		pattern.setVariable("bbb");
		ruleGenerationLHS.getPattern().add(pattern);

		configSet = new LineagePatternConfigHelper(ruleGenerationLHS);

		assertEquals("Pattern set size mismatch after adding", 1, configSet.size());
		assertEquals("Pattern size mismatch for BBB", 2, configSet.getLineagePatternConfigs("BBB").size());
		assertContainsText(configSet.getLineagePatternConfigs("BBB"), "text for BBB2");
		assertContainsText(configSet.getLineagePatternConfigs("BBB"), "text for BBB3");
		assertContainsVariable(configSet.getLineagePatternConfigs("BBB"), "bbb");
	}

	@Test
	public void testLineagePatternPrefixSetup() {
		Pattern pattern = new Pattern();
		pattern.setType(LHSPatternType.LINEAGE);
		pattern.setGenerate(Boolean.TRUE);
		pattern.setPrefix("AAA,BBB");

		pattern.setText("text for AAA & BBB");
		pattern.setVariable("aaa-bbb");
		ruleGenerationLHS.getPattern().add(pattern);

		pattern = new Pattern();
		pattern.setType(LHSPatternType.LINEAGE);
		pattern.setGenerate(Boolean.TRUE);
		pattern.setPrefix("AAA");
		pattern.setText("text for AAA");
		pattern.setVariable("aaa");
		ruleGenerationLHS.getPattern().add(pattern);

		pattern = new Pattern();
		pattern.setType(LHSPatternType.LINEAGE);
		pattern.setGenerate(Boolean.TRUE);
		pattern.setPrefix("BBB,CCC,DDD");
		pattern.setText("text for BBB & CCC & DDD");
		pattern.setVariable("bbb-ccc-ddd");
		ruleGenerationLHS.getPattern().add(pattern);

		pattern = new Pattern();
		pattern.setType(LHSPatternType.LINEAGE);
		pattern.setGenerate(Boolean.TRUE);
		pattern.setPrefix("BBB");
		pattern.setText("text for BBB");
		pattern.setVariable("bbb2");
		ruleGenerationLHS.getPattern().add(pattern);

		pattern = new Pattern();
		pattern.setType(LHSPatternType.LINEAGE);
		pattern.setGenerate(Boolean.FALSE);
		pattern.setPrefix("eee");
		pattern.setText("text for eee");
		pattern.setVariable("eee");
		ruleGenerationLHS.getPattern().add(pattern);

		final LineagePatternConfigHelper configSet = new LineagePatternConfigHelper(ruleGenerationLHS);

		assertEquals("Pattern set size mismatch", 4, configSet.size());
		assertEquals("Pattern size mismatch for AAA", 2, configSet.getLineagePatternConfigs("AAA").size());
		assertEquals("Pattern size mismatch for BBB", 3, configSet.getLineagePatternConfigs("BBB").size());
		assertEquals("Pattern size mismatch for CCC", 1, configSet.getLineagePatternConfigs("CCC").size());
		assertEquals("Pattern size mismatch for DDD", 1, configSet.getLineagePatternConfigs("DDD").size());
		assertEquals("Pattern size mismatch for eee (should be ignored)", 0, configSet.getLineagePatternConfigs("eee").size());
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
}

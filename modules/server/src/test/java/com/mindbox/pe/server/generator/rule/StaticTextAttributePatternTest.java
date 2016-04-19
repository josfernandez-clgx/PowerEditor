package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class StaticTextAttributePatternTest extends AbstractTestBase {

	@Test
	public void testConstructorSetsFieldsCorrectly() throws Exception {
		String str = "text" + createInt();
		StaticTextAttributePattern attributePattern = new StaticTextAttributePattern("attr", "v", str);
		assertFalse(attributePattern.hasValueSlot());
		assertEquals(str, attributePattern.getValueText());
	}

}

package com.mindbox.pe.server.enumsrc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.mindbox.pe.common.XmlUtil;
import com.mindbox.pe.unittest.AbstractTestBase;
import com.mindbox.pe.xsd.extenm.EnumValueType;
import com.mindbox.pe.xsd.extenm.PowerEditorEnumeration;

public class EnumValueParseTest extends AbstractTestBase {

	@Test
	public void testParseEnumValuesHappyCase() throws Exception {
		String testStr = "<?xml version=\"1.0\" encoding=\"utf-8\"?><PowerEditorEnumeration>"
				+ "<EnumValue><Value>value1</Value><DisplayLabel>Disp 1</DisplayLabel><Inactive>true</Inactive></EnumValue>"
				+ "<EnumValue><Value>value2</Value><DisplayLabel>Disp 2</DisplayLabel><SelectorValue>SV</SelectorValue></EnumValue>" + "</PowerEditorEnumeration>";

		final PowerEditorEnumeration powerEditorEnumeration = XmlUtil.unmarshal(testStr, PowerEditorEnumeration.class);

		final List<EnumValueType> list = powerEditorEnumeration.getEnumValue();

		assertEquals(2, list.size());
		assertEquals("value1", list.get(0).getValue());
		assertEquals("Disp 1", list.get(0).getDisplayLabel());
		assertTrue(list.get(0).isInactive());
		assertNull(list.get(0).getSelectorValue());
		assertEquals("value2", list.get(1).getValue());
		assertEquals("Disp 2", list.get(1).getDisplayLabel());
		assertNull(list.get(1).isInactive());
		assertEquals("SV", list.get(1).getSelectorValue());
	}
}

package com.mindbox.pe.client.common;

import static com.mindbox.pe.client.ClientTestObjectMother.createEnumValues;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.unittest.AbstractTestBase;

public class EnumValueCellRendererTest extends AbstractTestBase {

	private List<EnumValue> enumVals;
	private EnumValue anEnumVal;
	private EnumValueCellRenderer renderer;
	private JList list;
	private JLabel label;

	@Before
	public void setUp() throws Exception {
		enumVals = Arrays.asList(createEnumValues(3));
		anEnumVal = enumVals.get(0);
		renderer = new EnumValueCellRenderer(enumVals);
		list = new JList();
		label = (JLabel) renderer.getListCellRendererComponent(list, null, 0, false, false);
	}

	@Test
	public void testGetDisplayLabelNull() throws Exception {
		assertEquals("", EnumValueCellRenderer.getDisplayLabel(null));
	}

	@Test
	public void testGetDisplayLabelEnumValue() throws Exception {
		assertEquals(anEnumVal.getDisplayLabel(), EnumValueCellRenderer.getDisplayLabel(anEnumVal));
	}

	@Test
	public void testGetDisplayLabelString() throws Exception {
		assertEquals("foobar", EnumValueCellRenderer.getDisplayLabel("foobar"));
	}

	@Test
	public void testGetDisplayLabelClassCastException() throws Exception {

		try {
			EnumValueCellRenderer.getDisplayLabel(new Object());
			fail("Expected " + ClassCastException.class.getName());
		}
		catch (ClassCastException e) {
			// pass
		}
	}

	@Test
	public void testGetDisplayLabelIdToDisplayLabel() throws Exception {
		String expectedDisplayLabel = anEnumVal.getDisplayLabel();
		String idStr = anEnumVal.getDeployID().toString();

		assertEquals(expectedDisplayLabel, EnumValueCellRenderer.getDisplayLabel(idStr, enumVals));
	}

	@Test
	public void testGetCellRendererComponentWithDisplayLabel() throws Exception {
		for (Iterator<EnumValue> iter = enumVals.iterator(); iter.hasNext();) {
			EnumValue enumVal = iter.next();
			renderer.getListCellRendererComponent(list, enumVal.getDisplayLabel(), 0, false, false);
			assertEquals(enumVal.getDisplayLabel(), label.getText());
		}
	}

	@Test
	public void testGetCellRendererComponentWithId() throws Exception {
		for (Iterator<EnumValue> iter = enumVals.iterator(); iter.hasNext();) {
			EnumValue enumVal = iter.next();
			renderer.getListCellRendererComponent(list, enumVal.getDeployID().toString(), 0, false, false);
			assertEquals(enumVal.getDisplayLabel(), label.getText());
		}
	}

	@Test
	public void testGetCellRendererComponentWithEnum() throws Exception {
		for (Iterator<EnumValue> iter = enumVals.iterator(); iter.hasNext();) {
			EnumValue enumVal = iter.next();
			renderer.getListCellRendererComponent(list, enumVal, 0, false, false);
			assertEquals(enumVal.getDisplayLabel(), label.getText());
		}
	}
}

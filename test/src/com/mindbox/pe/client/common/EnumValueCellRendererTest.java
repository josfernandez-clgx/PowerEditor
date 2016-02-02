package com.mindbox.pe.client.common;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.EnumValue;

public class EnumValueCellRendererTest extends AbstractTestBase {
	private List<EnumValue> enumVals;
	private EnumValue anEnumVal;
	private EnumValueCellRenderer renderer;
	private JList list;
	private JLabel label;

	public static TestSuite suite() {
		TestSuite suite = new TestSuite(EnumValueCellRendererTest.class.getName());
		suite.addTestSuite(EnumValueCellRendererTest.class);
		return suite;
	}

	public EnumValueCellRendererTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		enumVals = Arrays.asList(ObjectMother.createEnumValues(3));
		anEnumVal = enumVals.get(0);
		renderer = new EnumValueCellRenderer(enumVals);
		list = new JList();
		label = (JLabel) renderer.getListCellRendererComponent(list, null, 0, false, false);
	}

	public void testGetDisplayLabelNull() throws Exception {
		assertEquals("", EnumValueCellRenderer.getDisplayLabel(null));
	}

	public void testGetDisplayLabelEnumValue() throws Exception {
		assertEquals(anEnumVal.getDisplayLabel(), EnumValueCellRenderer.getDisplayLabel(anEnumVal));
	}

	public void testGetDisplayLabelString() throws Exception {
		assertEquals("foobar", EnumValueCellRenderer.getDisplayLabel("foobar"));
	}

	public void testGetDisplayLabelClassCastException() throws Exception {
		try {
			EnumValueCellRenderer.getDisplayLabel(new Object());
			fail("Expected " + ClassCastException.class.getName());
		} catch (ClassCastException e) {
			// pass
		}
	}

	public void testGetDisplayLabelIdToDisplayLabel() throws Exception {
		String expectedDisplayLabel = anEnumVal.getDisplayLabel();
		String idStr = anEnumVal.getDeployID().toString();
		
		assertEquals(expectedDisplayLabel, EnumValueCellRenderer.getDisplayLabel(idStr, enumVals));
	}
	
	public void testGetCellRendererComponentWithDisplayLabel() throws Exception {
		for (Iterator<EnumValue> iter = enumVals.iterator(); iter.hasNext();) {
			EnumValue enumVal = iter.next();
			renderer.getListCellRendererComponent(list, enumVal.getDisplayLabel(), 0, false, false);
			assertEquals(enumVal.getDisplayLabel(), label.getText());
		}
	}
	
	public void testGetCellRendererComponentWithId() throws Exception {
		for (Iterator<EnumValue> iter = enumVals.iterator(); iter.hasNext();) {
			EnumValue enumVal = iter.next();
			renderer.getListCellRendererComponent(list, enumVal.getDeployID().toString(), 0, false, false);
			assertEquals(enumVal.getDisplayLabel(), label.getText());
		}
	}
	
	public void testGetCellRendererComponentWithEnum() throws Exception {
		for (Iterator<EnumValue> iter = enumVals.iterator(); iter.hasNext();) {
			EnumValue enumVal = iter.next();
			renderer.getListCellRendererComponent(list, enumVal, 0, false, false);
			assertEquals(enumVal.getDisplayLabel(), label.getText());
		}
	}
}

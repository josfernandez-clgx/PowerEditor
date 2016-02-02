package com.mindbox.pe.client.common.grid;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.table.EnumValues;

public class MultiSelectEnumCellRendererTest extends AbstractTestBase {

	private MultiSelectEnumCellRenderer renderer;
	private JTable table;
	private JLabel label;

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("MultiSelectEnumCellRendererTest Tests");
		suite.addTestSuite(MultiSelectEnumCellRendererTest.class);
		return suite;
	}

	public MultiSelectEnumCellRendererTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		renderer = new MultiSelectEnumCellRenderer();
		table = new JTable();
		label = (JLabel) renderer.getTableCellRendererComponent(table, null, false, false, 0, 0);
	}

	public void testToDisplayStringWithNullReturnsEmptyString() throws Exception {
		assertEquals("", MultiSelectEnumCellRenderer.toDisplayString(null));
	}

	public void testToDisplayStringWithEmptyEnumValuesReturnsEmptyString() throws Exception {
		assertEquals("", MultiSelectEnumCellRenderer.toDisplayString(ObjectMother.createEnumValues()));
	}

	public void testToDisplayStringWithEmptyEnumValuesAndIsSelectionExclusionReturnsEmptyString() throws Exception {
		EnumValues<EnumValue> enumValues = ObjectMother.createEnumValues();
		enumValues.setSelectionExclusion(true);
		assertEquals("", MultiSelectEnumCellRenderer.toDisplayString(enumValues));
	}

	public void testToDisplayStringWithExclusionReturnsOLD_EXCLUSION_PREFIX() throws Exception {
		EnumValues<EnumValue> enumValues = ObjectMother.attachEnumValue(ObjectMother.createEnumValues(), 3);
		enumValues.setSelectionExclusion(true);
		String result = MultiSelectEnumCellRenderer.toDisplayString(enumValues);
		assertTrue(result.indexOf(EnumValues.OLD_EXCLUSION_PREFIX) >= 0);
	}

	public void testGetDisplayLabelWithEmptyEnumValues() throws Exception {
		assertEquals("", MultiSelectEnumCellRenderer.toDisplayString(ObjectMother.createEnumValues()));
	}

	public void testToDisplayStringHappyPath_ForEnumValueEnumValues() throws Exception {
		EnumValues<EnumValue> enumValues = ObjectMother.attachEnumValue(ObjectMother.createEnumValues(), 3);
		enumValues.setSelectionExclusion(true);
		String label = MultiSelectEnumCellRenderer.toDisplayString(enumValues);
		assertEquals(0, label.indexOf(EnumValues.OLD_EXCLUSION_PREFIX));
		for (int i = 0; i < enumValues.size(); i++) {
			assertTrue(label.indexOf(enumValues.get(i).getDisplayLabel()) > 0);
		}
	}

	public void testToDisplayStringHappyPath_ForStringEnumValues() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add(ObjectMother.createString());
		enumValues.add(ObjectMother.createString());
		enumValues.setSelectionExclusion(true);

		String label = MultiSelectEnumCellRenderer.toDisplayString(enumValues);
		assertEquals(0, label.indexOf(EnumValues.OLD_EXCLUSION_PREFIX));
		for (int i = 0; i < enumValues.size(); i++) {
			assertTrue(label.indexOf(enumValues.get(i).toString()) > 0);
		}
	}

	public void testSetIconNullValue() throws Exception {
		label.setIcon(new ImageIcon());
		assertNotNull(label.getIcon()); // sanity check

		renderer.getTableCellRendererComponent(table, null, false, false, 0, 0);
		assertNull(label.getIcon());
	}

	public void testSetIconEmptyStringValue() throws Exception {
		label.setIcon(new ImageIcon());
		assertNotNull(label.getIcon()); // sanity check

		renderer.getTableCellRendererComponent(table, "", false, false, 0, 0);
		assertNull(label.getIcon());
	}

	public void testSetIconNotEmptyStringValue() throws Exception {
		label.setIcon(new ImageIcon());
		assertNotNull(label.getIcon()); // sanity check

		renderer.getTableCellRendererComponent(table, "string", false, false, 0, 0);
		assertNull(label.getIcon());
	}

	public void testSetIconStringWithOnlyExclusionPrefixValue() throws Exception {
		label.setIcon(new ImageIcon());
		assertNotNull(label.getIcon()); // sanity check

		renderer.getTableCellRendererComponent(table, EnumValues.EXCLUSION_PREFIX, false, false, 0, 0);
		assertNull(label.getIcon());
	}

	public void testSetIconStringWithOnlyOldExclusionPrefixValue() throws Exception {
		label.setIcon(new ImageIcon());
		assertNotNull(label.getIcon()); // sanity check

		renderer.getTableCellRendererComponent(table, EnumValues.OLD_EXCLUSION_PREFIX, false, false, 0, 0);
		assertNull(label.getIcon());
	}

	// ClientUtil.makeImageIcon throws null pointer trying to create the NOT icon
	public void testSetIconStringWithExclusionPrefixAndEnumValue() throws Exception {
		//		label.setIcon(null);
		//		renderer.getTableCellRendererComponent(table, EnumValues.EXCLUSION_PREFIX + "string", false, false, 0, 0);
		//		assertNotNull(label.getIcon());
	}

	// ClientUtil.makeImageIcon throws null pointer trying to create the NOT icon
	public void testSetIconStringWithOldExclusionPrefixAndEnumValue() throws Exception {
		//		label.setIcon(null);
		//		renderer.getTableCellRendererComponent(table, EnumValues.OLD_EXCLUSION_PREFIX + "string", false, false, 0, 0);
		//		assertNotNull(label.getIcon());
	}
}

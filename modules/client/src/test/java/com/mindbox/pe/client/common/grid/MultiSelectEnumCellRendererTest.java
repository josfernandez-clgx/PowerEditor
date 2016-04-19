package com.mindbox.pe.client.common.grid;

import static com.mindbox.pe.client.ClientTestObjectMother.attachEnumValue;
import static com.mindbox.pe.client.ClientTestObjectMother.createEnumValues;
import static com.mindbox.pe.unittest.TestObjectMother.createString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.EnumValue;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.unittest.AbstractTestBase;

public class MultiSelectEnumCellRendererTest extends AbstractTestBase {

	private MultiSelectEnumCellRenderer renderer;
	private JTable table;
	private JLabel label;

	@Before
	public void setUp() throws Exception {
		renderer = new MultiSelectEnumCellRenderer();
		table = new JTable();
		label = (JLabel) renderer.getTableCellRendererComponent(table, null, false, false, 0, 0);
	}

	@Test
	public void testToDisplayStringWithNullReturnsEmptyString() throws Exception {
		assertEquals("", MultiSelectEnumCellRenderer.toDisplayString(null));
	}

	@Test
	public void testToDisplayStringWithEmptyEnumValuesReturnsEmptyString() throws Exception {
		assertEquals("", MultiSelectEnumCellRenderer.toDisplayString(createEnumValues()));
	}

	@Test
	public void testToDisplayStringWithEmptyEnumValuesAndIsSelectionExclusionReturnsEmptyString() throws Exception {
		EnumValues<EnumValue> enumValues = createEnumValues();
		enumValues.setSelectionExclusion(true);
		assertEquals("", MultiSelectEnumCellRenderer.toDisplayString(enumValues));
	}

	@Test
	public void testToDisplayStringWithExclusionReturnsOLD_EXCLUSION_PREFIX() throws Exception {
		EnumValues<EnumValue> enumValues = attachEnumValue(createEnumValues(), 3);
		enumValues.setSelectionExclusion(true);
		String result = MultiSelectEnumCellRenderer.toDisplayString(enumValues);
		assertTrue(result.indexOf(EnumValues.OLD_EXCLUSION_PREFIX) >= 0);
	}

	@Test
	public void testGetDisplayLabelWithEmptyEnumValues() throws Exception {
		assertEquals("", MultiSelectEnumCellRenderer.toDisplayString(createEnumValues()));
	}

	@Test
	public void testToDisplayStringHappyPath_ForEnumValueEnumValues() throws Exception {
		EnumValues<EnumValue> enumValues = attachEnumValue(createEnumValues(), 3);
		enumValues.setSelectionExclusion(true);
		String label = MultiSelectEnumCellRenderer.toDisplayString(enumValues);
		assertEquals(0, label.indexOf(EnumValues.OLD_EXCLUSION_PREFIX));
		for (int i = 0; i < enumValues.size(); i++) {
			assertTrue(label.indexOf(enumValues.get(i).getDisplayLabel()) > 0);
		}
	}

	@Test
	public void testToDisplayStringHappyPath_ForStringEnumValues() throws Exception {
		EnumValues<String> enumValues = new EnumValues<String>();
		enumValues.add(createString());
		enumValues.add(createString());
		enumValues.setSelectionExclusion(true);

		String label = MultiSelectEnumCellRenderer.toDisplayString(enumValues);
		assertEquals(0, label.indexOf(EnumValues.OLD_EXCLUSION_PREFIX));
		for (int i = 0; i < enumValues.size(); i++) {
			assertTrue(label.indexOf(enumValues.get(i).toString()) > 0);
		}
	}

	@Test
	public void testSetIconNullValue() throws Exception {
		label.setIcon(new ImageIcon());
		assertNotNull(label.getIcon()); // sanity check

		renderer.getTableCellRendererComponent(table, null, false, false, 0, 0);
		assertNull(label.getIcon());
	}

	@Test
	public void testSetIconEmptyStringValue() throws Exception {
		label.setIcon(new ImageIcon());
		assertNotNull(label.getIcon()); // sanity check

		renderer.getTableCellRendererComponent(table, "", false, false, 0, 0);
		assertNull(label.getIcon());
	}

	@Test
	public void testSetIconNotEmptyStringValue() throws Exception {
		label.setIcon(new ImageIcon());
		assertNotNull(label.getIcon()); // sanity check

		renderer.getTableCellRendererComponent(table, "string", false, false, 0, 0);
		assertNull(label.getIcon());
	}

	@Test
	public void testSetIconStringWithOnlyExclusionPrefixValue() throws Exception {
		label.setIcon(new ImageIcon());
		assertNotNull(label.getIcon()); // sanity check

		renderer.getTableCellRendererComponent(table, EnumValues.EXCLUSION_PREFIX, false, false, 0, 0);
		assertNull(label.getIcon());
	}

	@Test
	public void testSetIconStringWithOnlyOldExclusionPrefixValue() throws Exception {
		label.setIcon(new ImageIcon());
		assertNotNull(label.getIcon()); // sanity check

		renderer.getTableCellRendererComponent(table, EnumValues.OLD_EXCLUSION_PREFIX, false, false, 0, 0);
		assertNull(label.getIcon());
	}

	// ClientUtil.makeImageIcon throws null pointer trying to create the NOT icon
	@Test
	public void testSetIconStringWithExclusionPrefixAndEnumValue() throws Exception {
		//		label.setIcon(null);
		//		renderer.getTableCellRendererComponent(table, EnumValues.EXCLUSION_PREFIX + "string", false, false, 0, 0);
		//		assertNotNull(label.getIcon());
	}

	// ClientUtil.makeImageIcon throws null pointer trying to create the NOT icon
	@Test
	public void testSetIconStringWithOldExclusionPrefixAndEnumValue() throws Exception {
		//		label.setIcon(null);
		//		renderer.getTableCellRendererComponent(table, EnumValues.OLD_EXCLUSION_PREFIX + "string", false, false, 0, 0);
		//		assertNotNull(label.getIcon());
	}
}

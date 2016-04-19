package com.mindbox.pe.client.common.grid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.swing.JTextField;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.common.FloatTextField;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.unittest.AbstractTestBase;

public class FloatCellEditorTest extends AbstractTestBase {


	private FloatCellEditor editor;
	private FloatTextField field;

	@Before
	public void setUp() throws Exception {
		setEditor(new FloatCellEditor());
	}

	protected void setEditor(FloatCellEditor editor) {
		this.editor = editor;
		field = (FloatTextField) editor.getTableCellEditorComponent(null, null, false, 0, 0);
	}

	@Test
	public void testAsEditorStringValueLargeValueFloat() throws Exception {
		assertEquals("8000000.01", FloatCellEditor.asEditorStringValue(Double.valueOf("8000000.01")));
	}

	@Test
	public void testEditLargeValueInteger() throws Exception {
		testEditTextSetsValue("0.0", "15700000.0", 15700000.0);
	}

	@Test
	public void testEditLargeValueFloat() throws Exception {
		testEditTextSetsValue("0.0", "8000000.01", 8000000.01);
	}

	@Test
	public void testConstructionValueNull() throws Exception {
		assertNull(editor.getCellEditorValue());
	}

	@Test
	public void testConstructionClickCount() throws Exception {
		assertEquals(2, editor.getClickCountToStart());
	}

	@Test
	public void testEditNullToNotNull() throws Exception {
		testEditTextSetsValue(null, "2.5");
	}

	@Test
	public void testEditNotNullToNull() throws Exception {
		testEditTextSetsValue("2.5", null);
	}

	@Test
	public void testEditNotNullToEmpty() throws Exception {
		testEditTextSetsValue("2.5", "");
	}

	@Test
	public void testGetTableCellEditorSetsValue() throws Exception {
		assertNull(editor.getCellEditorValue()); // sanity check

		editor.getTableCellEditorComponent(null, new Double("2.5"), false, 0, 0);
		assertEquals(new Double("2.5"), editor.getCellEditorValue());

		editor.getTableCellEditorComponent(null, null, false, 0, 0);
		assertNull(editor.getCellEditorValue());
	}

	// TestTracker #1448: New grid rows with column type Float or Currency have initial value set as empty string
	@Test
	public void testGetTableCellEditorAcceptsEmptyStringObject() throws Exception {
		editor.getTableCellEditorComponent(null, "", false, 0, 0);
		assertEquals(null, editor.getCellEditorValue());
	}

	@Test
	public void testString2FloatHappyPath() throws Exception {
		assertEquals(new Double("1.1"), editor.string2Double("1.1"));
	}

	@Test
	public void testString2FloatNull() throws Exception {
		assertNull(editor.string2Double(null));
	}

	@Test
	public void testString2FloatEmpty() throws Exception {
		assertNull(editor.string2Double(""));
	}

	@Test
	public void testString2FloatCommasStripped() throws Exception {
		assertEquals(new Double("1.1"), editor.string2Double("1,.1,,"));
	}

	@Test
	public void testString2FloatOnlyCommas() throws Exception {
		assertNull(editor.string2Double(",,,,"));
	}

	@Test
	public void testString2FloatLargeNumberInteger() throws Exception {
		assertEquals(new Double("15000000.0"), editor.string2Double("15000000.0"));
	}

	@Test
	public void testString2FloatLargeNumberFloat() throws Exception {
		assertEquals(new Double("8000000.01"), editor.string2Double("8000000.01"));
	}

	@Test
	public void testString2FloatScientificNotation() throws Exception {
		assertEquals(new Double("15000000.0"), editor.string2Double("1.5E7"));
	}

	protected void testEditTextSetsValue(String oldText, String newText) {
		testEditTextSetsValue(oldText, newText, UtilBase.isEmpty(newText) ? null : new Double(newText));
	}

	protected void testEditTextSetsValue(String oldText, String newText, Double expectedValue) {
		field.setText(oldText);
		editor.fireEditingStopped();

		field.setText(newText);
		editor.fireEditingStopped();
		assertEquals(expectedValue, editor.getCellEditorValue());
	}

	@Test
	public void testGetTableCellEditorComponentWithInvalidStringSetValueToNull() throws Exception {
		editor.getTableCellEditorComponent(null, "bogus2.5", false, 0, 0);
		assertNull(editor.getCellEditorValue());
	}

	@Test
	public void testGetTableCellEditorComponentWithValidStringSetsCorrectValue() throws Exception {
		editor.getTableCellEditorComponent(null, "2.5", false, 0, 0);
		assertEquals(2.5f, ((Number) editor.getCellEditorValue()).floatValue(), 0.2f);
	}

	@Test
	public void testGetTableCellEditorComponentWithValidStringForLargeValueInteger() throws Exception {
		JTextField component = JTextField.class.cast(editor.getTableCellEditorComponent(null, "250000000.0", false, 0, 0));
		assertEquals("250000000", component.getText());
	}

	@Test
	public void testGetTableCellEditorComponentWithValidStringForLargeValueFloat() throws Exception {
		JTextField component = JTextField.class.cast(editor.getTableCellEditorComponent(null, "8000000.01", false, 0, 0));
		assertEquals("8000000.01", component.getText());
	}

	@Test
	public void testGetTableCellEditorComponentWithValidStringForLargeValueFloat2() throws Exception {
		JTextField component = JTextField.class.cast(editor.getTableCellEditorComponent(null, 8000000.01, false, 0, 0));
		assertEquals("8000000.01", component.getText());
	}
}

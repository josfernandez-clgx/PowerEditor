package com.mindbox.pe.client.common.grid;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.table.DynamicStringValue;

public class DynamicStringCellEditorTest extends AbstractClientTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("DynamicStringCellEditorTest Tests");
		suite.addTestSuite(DynamicStringCellEditorTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	private ColumnDataSpecDigest columnDataSpecDigest;

	public DynamicStringCellEditorTest(String name) {
		super(name);
	}

	public void testGetCellEditorValueReturnsDynamicStringValueObject() throws Exception {
		String[] colNames = new String[] { "Column1" };
		DynamicStringCellEditor dynamicStringCellEditor = new DynamicStringCellEditor(colNames, "Column1", columnDataSpecDigest, false);
		String strValue = "dynamic-string-" + ObjectMother.createInt();
		dynamicStringCellEditor.setCellEditorValue(strValue);

		DynamicStringValue dsValue = (DynamicStringValue) dynamicStringCellEditor.getCellEditorValue();
		assertEquals(strValue, dsValue.toString());
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.columnDataSpecDigest = ObjectMother.createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_DYNAMIC_STRING);
	}
}

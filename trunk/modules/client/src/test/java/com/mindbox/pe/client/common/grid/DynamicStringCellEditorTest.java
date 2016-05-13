package com.mindbox.pe.client.common.grid;


import static com.mindbox.pe.client.ClientTestObjectMother.createColumnDataSpecDigest;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.model.table.DynamicStringValue;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;

public class DynamicStringCellEditorTest extends AbstractClientTestBase {

	private ColumnDataSpecDigest columnDataSpecDigest;

	@Test
	public void testGetCellEditorValueReturnsDynamicStringValueObject() throws Exception {
		String[] colNames = new String[] { "Column1" };
		DynamicStringCellEditor dynamicStringCellEditor = new DynamicStringCellEditor(colNames, "Column1", columnDataSpecDigest, false);
		String strValue = "dynamic-string-" + createInt();
		dynamicStringCellEditor.setCellEditorValue(strValue);

		DynamicStringValue dsValue = (DynamicStringValue) dynamicStringCellEditor.getCellEditorValue();
		assertEquals(strValue, dsValue.toString());
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		this.columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_DYNAMIC_STRING);
	}
}

package com.mindbox.pe.client.common.table;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.model.SimpleEntityData;

public class IDNameObjectSelectionTableTest extends AbstractClientTestBase {
	
	private static class TestSelectionTable extends IDNameObjectSelectionTable<IDNameObjectSelectionTableModel<SimpleEntityData>,SimpleEntityData> {

		public TestSelectionTable(IDNameObjectSelectionTableModel<SimpleEntityData> tableModel, boolean canSelectMultiple) {
			super(tableModel, canSelectMultiple);
		}
	}
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("IDNameObjectSelectionTableTest Tests");
		suite.addTestSuite(IDNameObjectSelectionTableTest.class);
		return suite;
	}

	private IDNameObjectSelectionTable<IDNameObjectSelectionTableModel<SimpleEntityData>,SimpleEntityData> selectionTable;
	
	public IDNameObjectSelectionTableTest(String name) {
		super(name);
	}

	public void testUpdateRowTakesInvalidValue() throws Exception {
		int value = -2;
		try {
			selectionTable.updateRow(value);
			value = new Long(System.currentTimeMillis()).intValue();
			selectionTable.updateRow(value);
		}
		catch (Exception ex) {
			fail("updateRow(int) failed to take " + value + ": " + ex);
		}
	}
	
	public void testRemoveWithNullThrowsNullPointerException() throws Exception {
		try {
			selectionTable.remove((SimpleEntityData) null);
			fail("Expected NullPointerException");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}
	
	public void testUpdateRowUpdatesRowsProperly() throws Exception {
		List<SimpleEntityData> dataList = new ArrayList<SimpleEntityData>();
		SimpleEntityData data = new SimpleEntityData(2,"name2");
		dataList.add(data);
		dataList.add(new SimpleEntityData(1,"name1"));
		selectionTable.setDataList(dataList);
		assertEquals("name2", selectionTable.getValueAt(1,0));
		data.setName("new-name");
		selectionTable.updateRow(1);
		assertEquals(data.getName(), selectionTable.getValueAt(1,0));
	}
	
	/**
	 * Tests if the sorting order is preserved when items are removed, per TT 1442.
	 */
	public void testRemoveDoesNotChangeSortOrder() throws Exception {
		List<SimpleEntityData> dataList = new ArrayList<SimpleEntityData>();
		SimpleEntityData data = new SimpleEntityData(2,"name2");
		dataList.add(data);
		dataList.add(new SimpleEntityData(3,"name3"));
		dataList.add(new SimpleEntityData(1,"name1"));
		selectionTable.setDataList(dataList);
		assertEquals(0, selectionTable.getIndexOfIDNameObjectInView(1));
		assertEquals(2, selectionTable.getIndexOfIDNameObjectInView(3));
		
		// remove the middle one
		selectionTable.remove(data);
		assertEquals(0, selectionTable.getIndexOfIDNameObjectInView(1));
		assertEquals(1, selectionTable.getIndexOfIDNameObjectInView(3));
	}
	
	public void testAddWithNullThrowsNullPointerException() throws Exception {
		try {
			selectionTable.add((SimpleEntityData) null);
			fail("Expected NullPointerException");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}
	
	public void testAddDoesNotChangeSortOrder() throws Exception {
		List<SimpleEntityData> dataList = new ArrayList<SimpleEntityData>();
		SimpleEntityData data = new SimpleEntityData(2,"name2");
		dataList.add(data);
		dataList.add(new SimpleEntityData(1,"name1"));
		selectionTable.setDataList(dataList);
		assertEquals(0, selectionTable.getIndexOfIDNameObjectInView(1));
		assertEquals(1, selectionTable.getIndexOfIDNameObjectInView(2));
		
		// remove the middle one
		selectionTable.add(new SimpleEntityData(3,"name3"));
		assertEquals(0, selectionTable.getIndexOfIDNameObjectInView(1));
		assertEquals(2, selectionTable.getIndexOfIDNameObjectInView(3));
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		IDNameObjectSelectionTableModel<SimpleEntityData> model = new IDNameObjectSelectionTableModel<SimpleEntityData>(new String[]{"name"});
		selectionTable = new TestSelectionTable(model, true);
	}

	protected void tearDown() throws Exception {
		selectionTable= null;
		super.tearDown();
	}
}

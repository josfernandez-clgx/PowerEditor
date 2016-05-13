package com.mindbox.pe.client.common.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.model.SimpleEntityData;

public class IDNameObjectSelectionTableTest extends AbstractClientTestBase {


	private static class TestSelectionTable extends IDNameObjectSelectionTable<IDNameObjectSelectionTableModel<SimpleEntityData>, SimpleEntityData> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public TestSelectionTable(IDNameObjectSelectionTableModel<SimpleEntityData> tableModel, boolean canSelectMultiple) {
			super(tableModel, canSelectMultiple);
		}
	}

	private IDNameObjectSelectionTable<IDNameObjectSelectionTableModel<SimpleEntityData>, SimpleEntityData> selectionTable;

	@Test
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

	@Test
	public void testRemoveWithNullThrowsNullPointerException() throws Exception {
		try {
			selectionTable.remove((SimpleEntityData) null);
			fail("Expected NullPointerException");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testUpdateRowUpdatesRowsProperly() throws Exception {
		List<SimpleEntityData> dataList = new ArrayList<SimpleEntityData>();
		SimpleEntityData data = new SimpleEntityData(2, "name2");
		dataList.add(data);
		dataList.add(new SimpleEntityData(1, "name1"));
		selectionTable.setDataList(dataList);
		assertEquals("name2", selectionTable.getValueAt(1, 0));
		data.setName("new-name");
		selectionTable.updateRow(1);
		assertEquals(data.getName(), selectionTable.getValueAt(1, 0));
	}

	/**
	 * Tests if the sorting order is preserved when items are removed, per TT 1442.
	 */
	@Test
	public void testRemoveDoesNotChangeSortOrder() throws Exception {
		List<SimpleEntityData> dataList = new ArrayList<SimpleEntityData>();
		SimpleEntityData data = new SimpleEntityData(2, "name2");
		dataList.add(data);
		dataList.add(new SimpleEntityData(3, "name3"));
		dataList.add(new SimpleEntityData(1, "name1"));
		selectionTable.setDataList(dataList);
		assertEquals(0, selectionTable.getIndexOfIDNameObjectInView(1));
		assertEquals(2, selectionTable.getIndexOfIDNameObjectInView(3));

		// remove the middle one
		selectionTable.remove(data);
		assertEquals(0, selectionTable.getIndexOfIDNameObjectInView(1));
		assertEquals(1, selectionTable.getIndexOfIDNameObjectInView(3));
	}

	@Test
	public void testAddWithNullThrowsNullPointerException() throws Exception {
		try {
			selectionTable.add((SimpleEntityData) null);
			fail("Expected NullPointerException");
		}
		catch (NullPointerException ex) {
			// expected
		}
	}

	@Test
	public void testAddDoesNotChangeSortOrder() throws Exception {
		List<SimpleEntityData> dataList = new ArrayList<SimpleEntityData>();
		SimpleEntityData data = new SimpleEntityData(2, "name2");
		dataList.add(data);
		dataList.add(new SimpleEntityData(1, "name1"));
		selectionTable.setDataList(dataList);
		assertEquals(0, selectionTable.getIndexOfIDNameObjectInView(1));
		assertEquals(1, selectionTable.getIndexOfIDNameObjectInView(2));

		// remove the middle one
		selectionTable.add(new SimpleEntityData(3, "name3"));
		assertEquals(0, selectionTable.getIndexOfIDNameObjectInView(1));
		assertEquals(2, selectionTable.getIndexOfIDNameObjectInView(3));
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		IDNameObjectSelectionTableModel<SimpleEntityData> model = new IDNameObjectSelectionTableModel<SimpleEntityData>(new String[] { "name" });
		selectionTable = new TestSelectionTable(model, true);
	}

	@After
	public void tearDown() throws Exception {
		selectionTable = null;
		super.tearDown();
	}
}

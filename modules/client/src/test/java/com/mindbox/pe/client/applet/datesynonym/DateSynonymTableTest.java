package com.mindbox.pe.client.applet.datesynonym;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.model.DateSynonym;

public class DateSynonymTableTest extends AbstractClientTestBase {


	/**
	 * Make sure the toString() method returns the name embedded in the String
	 * the name
	 *
	 * @throws Exception
	 */
	@Test
	public void testAll() throws Exception {
		DateSynonym ds1 = new DateSynonym(3, "C Test", "C Test", Calendar.getInstance().getTime());
		DateSynonym ds2 = new DateSynonym(1, "A Test", "A Test", Calendar.getInstance().getTime());
		DateSynonym ds3 = new DateSynonym(2, "B Test", "B Test", Calendar.getInstance().getTime());
		List<DateSynonym> list = new ArrayList<DateSynonym>();
		list.add(ds1);
		list.add(ds2);
		list.add(ds3);

		DateSynonymTableModel tableModel = new DateSynonymTableModel();
		tableModel.setDataList(list);
		DateSynonymTable table = new DateSynonymTable(tableModel);
		DateSynonym ds = (DateSynonym) table.getValueAt(0, -1);
		assertTrue(ds.getName().equals("C Test"));
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
}

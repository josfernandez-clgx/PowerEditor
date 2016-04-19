package com.mindbox.pe.client.applet.entities.generic.category;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;


public class CategoryToCategorySelectionTableModelTest extends AbstractClientTestBase {

	/**
	 * Test the basics.
	 *
	 * @throws Exception
	 */
	@Test
	public void testAll() throws Exception {
		CategoryToCategorySelectionTableModel tableModel = new CategoryToCategorySelectionTableModel(entityType1.getCategoryType());
		String[] colNames = tableModel.getColumnNames();
		assertNotNull(colNames);
		assertTrue(colNames.length > 0);

		MutableTimedAssociationKey key = new DefaultMutableTimedAssociationKey(1);
		tableModel.addData(key);
		assertNotNull(tableModel.getDataList());
		assertTrue(tableModel.getDataList().size() == 1);
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

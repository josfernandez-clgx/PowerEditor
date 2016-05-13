package com.mindbox.pe.client.applet.entities.compatibility;


import static com.mindbox.pe.client.ClientTestObjectMother.createGenericEntityType;
import static com.mindbox.pe.unittest.UnitTestHelper.getDate;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;

public class CompatibilitySelectionTableModelTest extends AbstractClientTestBase {

	/**
	 *
	 * @throws Exception
	 */
	@Test
	public void testAll() throws Exception {
		GenericEntityType entityType = createGenericEntityType(1, 1);
		CompatibilitySelectionTableModel model = new CompatibilitySelectionTableModel(entityType, entityType);

		DateSynonym date1 = DateSynonym.createUnnamedInstance(getDate(2006, 10, 10));
		DateSynonym date2 = DateSynonym.createUnnamedInstance(getDate(2006, 11, 11));

		GenericEntityCompatibilityData data = new GenericEntityCompatibilityData(entityType, 1, entityType, 1, date1, date2);
		model.addData(data);
		model.setShowDateNames(false);
		String value = (String) model.getValueAt(0, 2);
		assertEquals("10/10/2006 00:00:00", value);
		value = (String) model.getValueAt(0, 3);
		assertEquals("11/11/2006 00:00:00", value);

		model.setShowDateNames(true);
		value = (String) model.getValueAt(0, 2);
		assertEquals(date1.getName(), value);
		value = (String) model.getValueAt(0, 3);
		assertEquals(date2.getName(), value);
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

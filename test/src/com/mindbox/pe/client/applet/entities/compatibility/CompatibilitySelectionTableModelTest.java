package com.mindbox.pe.client.applet.entities.compatibility;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.GenericEntityCompatibilityData;


public class CompatibilitySelectionTableModelTest extends AbstractClientTestBase {
    public CompatibilitySelectionTableModelTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("CompatibilitySelectionTableModelTest Tests");
        suite.addTestSuite(CompatibilitySelectionTableModelTest.class);

        return suite;
    }

    /**
     *
     * @throws Exception
     */
    public void testAll() throws Exception {
		GenericEntityType entityType = ObjectMother.createGenericEntityType(1, 1);
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

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}

package com.mindbox.pe.client.applet.entities.generic.category;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;

import junit.framework.Test;
import junit.framework.TestSuite;


public class CategoryToCategorySelectionTableModelTest
    extends AbstractClientTestBase {
    public CategoryToCategorySelectionTableModelTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(
                "CategoryToCategorySelectionTableModelTest Tests");
        suite.addTestSuite(CategoryToCategorySelectionTableModelTest.class);

        return suite;
    }

    /**
     * Test the basics.
     *
     * @throws Exception
     */
    public void testAll() throws Exception {
        CategoryToCategorySelectionTableModel tableModel = new CategoryToCategorySelectionTableModel(GenericEntityType.forName(
                    "product").getCategoryType());
        String[] colNames = tableModel.getColumnNames();
        assertNotNull(colNames);
        assertTrue(colNames.length > 0);

        MutableTimedAssociationKey key = new DefaultMutableTimedAssociationKey(1);
        tableModel.addData(key);
        assertNotNull(tableModel.getDataList());
        assertTrue(tableModel.getDataList().size() == 1);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}

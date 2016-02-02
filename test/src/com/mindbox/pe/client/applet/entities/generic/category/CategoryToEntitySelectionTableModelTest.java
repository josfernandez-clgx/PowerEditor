package com.mindbox.pe.client.applet.entities.generic.category;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.assckey.DefaultMutableTimedAssociationKey;
import com.mindbox.pe.model.assckey.MutableTimedAssociationKey;


public class CategoryToEntitySelectionTableModelTest extends AbstractClientTestBase {
    public CategoryToEntitySelectionTableModelTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("CategoryToEntitySelectionTableModelTest Tests");
        suite.addTestSuite(CategoryToEntitySelectionTableModelTest.class);

        return suite;
    }

    /**
     * Test the basics. 
     *
     * @throws Exception
     */
    public void testAll() throws Exception {
        CategoryToEntitySelectionTableModel tableModel = new CategoryToEntitySelectionTableModel();
        String[] colNames = tableModel.getColumnNames();
        assertNotNull(colNames);
        assertTrue(colNames.length > 0);
        
        GenericEntity entity = ObjectMother.createGenericEntity(GenericEntityType.forName("product"));
        MutableTimedAssociationKey key = new DefaultMutableTimedAssociationKey(1);
        CategoryToEntityAssociationData data = new CategoryToEntityAssociationData(entity, key);
        tableModel.addData(data);
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

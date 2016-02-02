package com.mindbox.pe.client.common;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithGenericEntityType;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;


public class GenericEntityCheckListTest extends AbstractTestWithGenericEntityType {
    
    public GenericEntityCheckListTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("GenericEntityCheckListTest Tests");
        suite.addTestSuite(GenericEntityCheckListTest.class);

        return suite;
    }

    /**
     * Test to make sure the drop-down is not editable and that the toString() method contains
     * the name
     *
     * @throws Exception
     */
    public void testSelectAll()
        throws Exception {
        EntityModelCacheFactory.getInstance().add(ObjectMother.createGenericEntity(entityType));
        GenericEntityCheckList checkList = new GenericEntityCheckList(GenericEntityType.forName("product"));
        checkList.selectAll();
        GenericEntity[] entities = checkList.getSelectedGenericEntities();
        assertNotNull(entities);
        assertTrue(checkList.getModel().getSize() == entities.length);
    }

    protected void setUp() throws Exception {
        super.setUp();
        
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}

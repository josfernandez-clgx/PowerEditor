package com.mindbox.pe.client.common.tree;

import javax.swing.tree.TreeSelectionModel;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntityType;

public class GenericCategorySelectionTreeTest extends AbstractClientTestBase {
    private GenericCategory category;
    
    public GenericCategorySelectionTreeTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(
                "GenericCategorySelectionTreeTest Tests");
        suite.addTestSuite(GenericCategorySelectionTreeTest.class);

        return suite;
    }

    /**
     * @throws Exception
     */
    public void testTreeSelectionSetToDISCONTIGUOUS_TREE_SELECTION() throws Exception {
        GenericCategorySelectionTree tree = new GenericCategorySelectionTree(GenericEntityType.forName("product").getCategoryType(), false, true, true);
        assertEquals(tree.getTreeSelectionModel().getSelectionMode(),
                TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    }

    protected void setUp() throws Exception {
        super.setUp();
        category = ObjectMother.createGenericCategory(GenericEntityType.forName("product"));        
        EntityModelCacheFactory.getInstance().addGenericCategory(category);        
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        EntityModelCacheFactory.getInstance().removeGenericCategory(category);        
    }
}

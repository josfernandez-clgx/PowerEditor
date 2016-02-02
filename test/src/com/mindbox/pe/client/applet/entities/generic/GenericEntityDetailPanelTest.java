package com.mindbox.pe.client.applet.entities.generic;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.client.AbstractClientTestBase;


public class GenericEntityDetailPanelTest extends AbstractClientTestBase {
    
    public GenericEntityDetailPanelTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("GenericEntityDetailPanelTest Tests");
        suite.addTestSuite(GenericEntityDetailPanelTest.class);

        return suite;
    }

    /**
     * TODO: Complete implementation once framework is in place 
     *       to unit test GUI widgets.
     *
     * @throws Exception
     */
    public void testSaveEntity()
    //create a new EntityManagementPanel test the performAction which 
    // invokes saveEntity_internal
        throws Exception {
        
    }

    protected void setUp() throws Exception {
        super.setUp();
        
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}

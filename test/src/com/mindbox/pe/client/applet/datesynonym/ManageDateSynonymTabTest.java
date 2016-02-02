package com.mindbox.pe.client.applet.datesynonym;

import com.mindbox.pe.client.AbstractClientTestBase;

import junit.framework.Test;
import junit.framework.TestSuite;

import javax.swing.event.ChangeListener;


public class ManageDateSynonymTabTest extends AbstractClientTestBase {
    public ManageDateSynonymTabTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("ManageDateSynonymTabTest Tests");
        suite.addTestSuite(ManageDateSynonymTabTest.class);

        return suite;
    }

    /**
     * @throws Exception
     */
    public void testAll() throws Exception {
        assertNotNull(ManageDateSynonymTab.class.getInterfaces());
        assertContains(ManageDateSynonymTab.class.getInterfaces(), ChangeListener.class);
        ManageDateSynonymTab.reset();
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}

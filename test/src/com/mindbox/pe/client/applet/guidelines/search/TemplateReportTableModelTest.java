package com.mindbox.pe.client.applet.guidelines.search;

import java.util.Arrays;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.ClientUtil;


public class TemplateReportTableModelTest extends AbstractClientTestBase {
    public TemplateReportTableModelTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("TemplateReportTableModelTest Tests");
        suite.addTestSuite(TemplateReportTableModelTest.class);

        return suite;
    }

    /**
     * Test to make sure the drop-down is not editable and that the toString() method contains
     * the name
     *
     * @throws Exception
     */
    public void testAll() throws Exception {
        TemplateReportTableModel tableModel = new TemplateReportTableModel();
        String[] colNames = tableModel.getColumnNames();
        assertNotNull(colNames);
        assertTrue(colNames.length > 0);
        assertTrue(Arrays.asList(colNames).contains(ClientUtil.getInstance().getLabel("label.row")));
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}

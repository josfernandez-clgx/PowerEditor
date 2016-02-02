package com.mindbox.pe.client.applet.parameters;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.ParameterGrid;

import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.Date;


public class ParameterContextTableModelTest extends AbstractClientTestBase {
    public ParameterContextTableModelTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("ParameterContextTableModelTest Tests");
        suite.addTestSuite(ParameterContextTableModelTest.class);

        return suite;
    }

    /**
     *
     * @throws Exception
     */
    public void testAll() throws Exception {
        ParameterContextTableModel model = new ParameterContextTableModel();

        Date effdate = getDate(2006, 10, 10);
        Date expdate = getDate(2006, 11, 11);
        DateSynonym ds1 = new DateSynonym(1, "ds1", "ds1", effdate);
        DateSynonym ds2 = new DateSynonym(1, "ds2", "ds2", expdate);

        ParameterGrid data = new ParameterGrid(0, 0, ds1, ds2);

        model.addParameterGrid(data);

        String value = (String) model.getValueAt(0, 5);
        assertEquals(value, ds1.getName());
        value = (String) model.getValueAt(0, 6);
        assertEquals(value, ds2.getName());
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}

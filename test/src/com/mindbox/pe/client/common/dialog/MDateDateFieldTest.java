package com.mindbox.pe.client.common.dialog;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.common.dialog.MDateDateField;
import com.mindbox.pe.common.ReflectionUtil;

import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.Date;

import javax.swing.JButton;
import javax.swing.JTextField;


public class MDateDateFieldTest extends AbstractClientTestBase {
    public MDateDateFieldTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("MDateDateFieldTest Tests");
        suite.addTestSuite(MDateDateFieldTest.class);

        return suite;
    }

    /**
     * @throws Exception
     */
    public void testAll() throws Exception {
        Date date = getDate(2006, 10, 10, 10, 11, 12);
        MDateDateField mdateField = new MDateDateField(false, false, true);
        mdateField.setValue(date);
        assertEquals(mdateField.getDate(), date);
        assertEquals(mdateField.formatToString(mdateField.getDate()),
            "10/10/2006 10:11:12");
        assertEquals(mdateField.formatToDate("10/10/2006 10:11:12"), mdateField.getDate());

        

        mdateField = new MDateDateField(false, true, false);
        mdateField.setValue(date);
        assertNotEquals(mdateField.getDate(), date);
        assertEquals(mdateField.getDate(), getDate(2006, 10, 10));
        assertEquals(mdateField.formatToString(mdateField.getDate()),
            "10/10/2006");
        assertEquals(mdateField.formatToDate("10/10/2006"), mdateField.getDate());
        
        mdateField = new MDateDateField(true, true);
        mdateField.setValue(date);
        mdateField.setEnabled(false);
        assertFalse(((JTextField) ReflectionUtil.getPrivate(mdateField, "timeField")).isEnabled());
        assertFalse(((JButton) ReflectionUtil.getPrivate(mdateField, "clearButton")).isEnabled());        
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}

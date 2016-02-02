package com.mindbox.pe.client.common;

import javax.swing.DefaultComboBoxModel;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.model.DateSynonym;


public class DateSynonymComboBoxTest extends AbstractClientTestBase {
    
    public DateSynonymComboBoxTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("DateSynonymComboBoxTest Tests");
        suite.addTestSuite(DateSynonymComboBoxTest.class);

        return suite;
    }

    /**
     * Test to make sure the drop-down is not editable and that the toString() method contains
     * the name
     *
     * @throws Exception
     */
    public void testAll()
        throws Exception {
        DateSynonymComboBox dsCombo = new DateSynonymComboBox(true, false);
        DateSynonym ds1 = new DateSynonym(1, "ds1", "ds1", getDate(2006, 10, 10));
        DateSynonym ds2 = new DateSynonym(2, "ds2", "ds2", getDate(2006, 10, 10));
        DateSynonym ds3 = new DateSynonym(3, "ds3", "ds3", getDate(2006, 10, 10));
        DateSynonym ds4 = new DateSynonym(4, "ds4", "ds4", getDate(2006, 10, 10));

        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement(ds1);
        model.addElement(ds2);
        model.addElement(ds3);
        dsCombo.setModel(model);
       
        dsCombo.setValue(ds4);
        assertFalse(dsCombo.isEditable());
        assertTrue(dsCombo.getSelectedIndex() == 3);
        assertTrue(dsCombo.getSelectedItem().equals(ds4));
        assertFalse(dsCombo.isEditable());
    }

    protected void setUp() throws Exception {
        super.setUp();
        
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}

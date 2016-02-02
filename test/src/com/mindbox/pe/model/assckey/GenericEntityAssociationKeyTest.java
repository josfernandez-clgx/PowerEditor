package com.mindbox.pe.model.assckey;

import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.model.DateSynonym;

/**
 * Unit tests for {@link GenericEntityAssociationKey}.
 */
public class GenericEntityAssociationKeyTest extends AbstractTestBase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static TestSuite suite() {
        TestSuite suite = new TestSuite("GenericEntityAssociationDataTest Tests");
        suite.addTestSuite(GenericEntityAssociationKeyTest.class);
        return suite;
    }

    public GenericEntityAssociationKeyTest(String name) {
        super(name);
    }

    public void testGetIDReturnsZero() throws Exception {
        GenericEntityAssociationKey data = new GenericEntityAssociationKey(null, (int) System.currentTimeMillis(), null, null);
        assertEquals(0, data.getID());
    }

    public void testSetEffectiveDates() throws Exception {
        DateSynonym eff = DateSynonym.createUnnamedInstance(getDate(2006, 1, 1));
        DateSynonym exp = DateSynonym.createUnnamedInstance(getDate(2006, 9, 1));
        
        GenericEntityAssociationKey data = new GenericEntityAssociationKey(null, (int) System.currentTimeMillis(), null, null);
        data.setEffectiveDate(eff);
        data.setExpirationDate(exp);
        assertEquals(data.getEffectiveDate(), eff);
        assertEquals(data.getExpirationDate(), exp);
    }   
   
}

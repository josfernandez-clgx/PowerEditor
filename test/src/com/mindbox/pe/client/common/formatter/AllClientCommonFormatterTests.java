package com.mindbox.pe.client.common.formatter;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllClientCommonFormatterTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(AllClientCommonFormatterTests.class.getName());
        
        suite.addTest(SymbolDocumentFilterTest.suite());
        
        return suite;
    }

}

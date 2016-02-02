/*

 *
 */
package com.mindbox.pe.client.applet.validate;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author MindBox
 * @since PowerEditor 5.1.0
 */
public class AllClientAppletValidateTestSuite {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(
                "AllClientAppletValidateTestSuite Tests");
        suite.addTest(ValidatorTest.suite());

        return suite;
    }
}

/*
 * Created on 2006. 10. 23.
 *
 */
package com.mindbox.pe.client.applet.guidelines.search;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author MindBox
 * @since PowerEditor 5.0.0
 */
public class AllClientAppletGuidelinesSearchTests {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(
                "AllClientAppletGuidelinesSearchTests Tests");
        suite.addTest(TemplateReportTableModelTest.suite());

        return suite;
    }
}

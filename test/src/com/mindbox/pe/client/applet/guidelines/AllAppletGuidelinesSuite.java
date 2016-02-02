/*
 * Created on Oct 23, 2006
 *
 */
package com.mindbox.pe.client.applet.guidelines;

import com.mindbox.pe.client.applet.guidelines.manage.AllClientAppletGuidelinesManagerTests;
import com.mindbox.pe.client.applet.guidelines.search.AllClientAppletGuidelinesSearchTests;

import junit.framework.Test;
import junit.framework.TestSuite;

import junit.textui.TestRunner;


/**
 * Contains tests for applet packages.
 * @author MindBox
 */
public final class AllAppletGuidelinesSuite {
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All Client Applet Tests");
        suite.addTest(AllClientAppletGuidelinesManagerTests.suite());
        suite.addTest(AllClientAppletGuidelinesSearchTests.suite());

        return suite;
    }
}

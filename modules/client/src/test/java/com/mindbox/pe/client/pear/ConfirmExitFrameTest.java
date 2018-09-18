package com.mindbox.pe.client.pear;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConfirmExitFrameTest {
    private static Logger LOG = Logger.getLogger(ConfirmExitFrameTest.class);

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        ConfirmExitFrame frame = new ConfirmExitFrame("ConfirmExitFrame test");
        frame.run();
        LOG.info(frame.ok() ? "OK" : "Cancel");
        frame.dispose();
    }

}

package com.mindbox.pe.client.pear;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mindbox.pe.communication.pear.HttpRequest;
import com.mindbox.pe.communication.pear.PowerEditorInfoRequest;
import com.mindbox.pe.communication.pear.PowerEditorInfoResponse;

public class PowerEditorInfoTest {

    private static final Logger LOG = Logger.getLogger(PowerEditorInfoTest.class);

    final static String pearURL = "http://localhost:8210/powereditor/PEARServlet";

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
    public void test() throws Exception {
        PowerEditorInfoRequest request = new PowerEditorInfoRequest();
        PowerEditorInfoResponse response = (PowerEditorInfoResponse) HttpRequest.post(pearURL, request);
        LOG.info("response=" + response.toString());
    }
}

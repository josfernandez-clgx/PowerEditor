package com.mindbox.pe.client.pear;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mindbox.pe.communication.pear.HttpRequest;
import com.mindbox.pe.communication.pear.SessionInfoRequest;
import com.mindbox.pe.communication.pear.SessionInfoResponse;

public class SessionInfoTest {

    private static final Logger LOG = Logger.getLogger(SessionInfoTest.class);

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
        SessionInfoRequest request = new SessionInfoRequest();
        SessionInfoResponse response = (SessionInfoResponse) HttpRequest.post(pearURL, request);
        LOG.info("response=" + response.toString());
    }
}

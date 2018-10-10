package com.mindbox.pe.client.pear;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mindbox.pe.communication.pear.HttpRequest;
import com.mindbox.pe.communication.pear.VerifyUserRequest;
import com.mindbox.pe.communication.pear.VerifyUserResponse;

public class VerifyUserTest {

    private static final Logger LOG = Logger.getLogger(VerifyUserTest.class);

    final static String pearURL = "http://localhost:8210/powereditor/PEAR";

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
    public void test_bad_bad() throws Exception {
        LOG.info("test_bad_bad():");
        VerifyUserRequest request = new VerifyUserRequest("bad", "bad");
        LOG.info("test_bad_bad(): request=" + request.toString());
        VerifyUserResponse response = (VerifyUserResponse) HttpRequest.post(pearURL, request);
        LOG.info("test_bad_bad(): response=" + response.toString());
    }

    @Test
    public void test_demo_bad() throws Exception {
        LOG.info("test_demo_bad():");
        VerifyUserRequest request = new VerifyUserRequest("demo", "bad");
        LOG.info("test_demo_bad(): request=" + request.toString());
        VerifyUserResponse response = (VerifyUserResponse) HttpRequest.post(pearURL, request);
        LOG.info("test_demo_bad(): response=" + response.toString());
    }

    @Test
    public void test_demo_demo() throws Exception {
        LOG.info("test_demo_demo():=");
        VerifyUserRequest request = new VerifyUserRequest("demo", "demo");
        LOG.info("test_demo_demo(): request=" + request.toString());
        VerifyUserResponse response = (VerifyUserResponse) HttpRequest.post(pearURL, request);
        LOG.info("test_demo_demo(): response=" + response.toString());
    }

}

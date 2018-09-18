package com.mindbox.pe.client.pear;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mindbox.pe.communication.pear.HttpRequest;
import com.mindbox.pe.communication.pear.LoginUserRequest;
import com.mindbox.pe.communication.pear.LoginUserResponse;
import com.mindbox.pe.communication.pear.LogoutUserRequest;
import com.mindbox.pe.communication.pear.LogoutUserResponse;

public class LoginUserTest {

    private static final Logger LOG = Logger.getLogger(LoginUserTest.class);

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
    public void bad_bad() throws Exception {
        LOG.info("bad_bad():");
        LoginUserRequest request = new LoginUserRequest("bad", "bad");
        LOG.info("bad_bad(): request=" + request.toString());
        LoginUserResponse response = (LoginUserResponse) HttpRequest.post(pearURL, request);
        LOG.info("bad_bad(): response=" + response.toString());
        if (!response.failed) {
            LogoutUserRequest logoutRequest = new LogoutUserRequest(response.sessionID, request.getUsername());
            LOG.info("bad_bad(): logoutRequest=" + logoutRequest.toString());
            LogoutUserResponse logoutResponse = (LogoutUserResponse) HttpRequest.post(pearURL, logoutRequest);
            LOG.info("bad_bad(): logoutResponse=" + logoutResponse.toString());
        }
    }

    @Test
    public void demo_bad() throws Exception {
        LOG.info("demo_bad():");
        LoginUserRequest request = new LoginUserRequest("demo", "bad");
        LOG.info("demo_bad(): request=" + request.toString());
        LoginUserResponse response = (LoginUserResponse) HttpRequest.post(pearURL, request);
        LOG.info("demo_bad(): response=" + response.toString());
        if (!response.failed) {
            LogoutUserRequest logoutRequest = new LogoutUserRequest(response.sessionID, request.getUsername());
            LOG.info("demo_bad(): logoutRequest=" + logoutRequest.toString());
            LogoutUserResponse logoutResponse = (LogoutUserResponse) HttpRequest.post(pearURL, logoutRequest);
            LOG.info("demo_bad(): logoutResponse=" + logoutResponse.toString());
        }
    }

    @Test
    public void demo_demo() throws Exception {
        LOG.info("demo_demo(): pearURL=" + pearURL);
        LoginUserRequest request = new LoginUserRequest("demo", "demo");
        LOG.info("demo_demo(): request=" + request.toString());
        LoginUserResponse response = (LoginUserResponse) HttpRequest.post(pearURL, request);
        LOG.info("demo_demo(): response=" + response.toString());
        if (!response.failed) {
            LogoutUserRequest logoutRequest = new LogoutUserRequest(response.sessionID, request.getUsername());
            LOG.info("demo_demo(): logoutRequest=" + logoutRequest.toString());
            LogoutUserResponse logoutResponse = (LogoutUserResponse) HttpRequest.post(pearURL, logoutRequest);
            LOG.info("demo_demo(): logoutResponse=" + logoutResponse.toString());
        }
    }

}

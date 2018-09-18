package com.mindbox.pe.client.pear;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.mindbox.pe.communication.pear.ChangePasswordRequest;
import com.mindbox.pe.communication.pear.ChangePasswordResponse;
import com.mindbox.pe.communication.pear.HttpRequest;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ChangePasswordTest {

    private static final Logger LOG = Logger.getLogger(ChangePasswordTest.class);

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
    public void t1_bad_bad_bad() throws Exception {
        LOG.info("t1_bad_bad_bad()");
        ChangePasswordRequest request = new ChangePasswordRequest("bad", "bad", "bad");
        LOG.info("t1_bad_bad_bad() request=" + request.toString());
        ChangePasswordResponse response = (ChangePasswordResponse) HttpRequest.post(pearURL, request);
        LOG.info("t1_bad_bad_bad() response=" + response.toString());
    }

    @Test
    public void t2_demo_bad_bad() throws Exception {
        LOG.info("t2_demo_bad_bad()");
        ChangePasswordRequest request = new ChangePasswordRequest("demo", "bad", "bad");
        LOG.info("t2_demo_bad_bad() request=" + request.toString());
        ChangePasswordResponse response = (ChangePasswordResponse) HttpRequest.post(pearURL, request);
        LOG.info("t2_demo_bad_bad() response=" + response.toString());
    }

    @Test
    public void t3_demo_demo_alpha() throws Exception {
        LOG.info("t3_demo_demo_alpha()");
        ChangePasswordRequest request = new ChangePasswordRequest("demo", "demo", "alpha@2018");
        LOG.info("t3_demo_demo_alpha() request=" + request.toString());
        ChangePasswordResponse response = (ChangePasswordResponse) HttpRequest.post(pearURL, request);
        LOG.info("t3_demo_demo_alpha() response=" + response.toString());
    }

    @Test
    public void t4_demo_alpha_beta() throws Exception {
        LOG.info("t4_demo_alpha_beta()");
        ChangePasswordRequest request = new ChangePasswordRequest("demo", "alpha@2018", "beta@2018");
        LOG.info("t4_demo_alpha_beta() request=" + request.toString());
        ChangePasswordResponse response = (ChangePasswordResponse) HttpRequest.post(pearURL, request);
        LOG.info("t4_demo_alpha_beta() response=" + response.toString());
    }

    @Test
    public void t5_demo_beta_gamma() throws Exception {
        LOG.info("t5_demo_beta_gamma()");
        ChangePasswordRequest request = new ChangePasswordRequest("demo", "beta@2018", "gamma@2018");
        LOG.info("t5_demo_beta_gamma() request=" + request.toString());
        ChangePasswordResponse response = (ChangePasswordResponse) HttpRequest.post(pearURL, request);
        LOG.info("t5_demo_beta_gamma() response=" + response.toString());
    }

    @Test
    public void t6_demo_gamma_delta() throws Exception {
        LOG.info("t6_demo_gamma_delta()");
        ChangePasswordRequest request = new ChangePasswordRequest("demo", "gamma@2018", "delta@2018");
        LOG.info("t6_demo_gamma_delta() request=" + request.toString());
        ChangePasswordResponse response = (ChangePasswordResponse) HttpRequest.post(pearURL, request);
        LOG.info("t6_demo_gamma_delta() response=" + response.toString());
    }

    @Test
    public void t7_demo_delta_demo() throws Exception {
        LOG.info("t7_demo_delta_demo()");
        ChangePasswordRequest request = new ChangePasswordRequest("demo", "delta@2018", "demo", true, true);
        LOG.info("t7_demo_delta_demo() request=" + request.toString());
        ChangePasswordResponse response = (ChangePasswordResponse) HttpRequest.post(pearURL, request);
        LOG.info("t7_demo_delta_demo() response=" + response.toString());
    }

}

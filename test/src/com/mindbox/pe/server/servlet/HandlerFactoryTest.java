package com.mindbox.pe.server.servlet;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.communication.RequestComm;
import com.mindbox.pe.server.servlet.handlers.DateSynonymInUseRequestHandler;
import com.mindbox.pe.server.servlet.handlers.IRequestCommHandler;

public class HandlerFactoryTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("HandlerFactoryTest Tests");
		suite.addTestSuite(HandlerFactoryTest.class);
		return suite;
	}

	private Object handlerFactoryInstance = null;

	public HandlerFactoryTest(String name) {
		super(name);
	}

	public void testGetHandlerWithDateSynonymInUseRequestHappyCase() throws Exception {
		IRequestCommHandler<?> handler = invokeGetHandler(ObjectMother.createDateSynonymInUseRequest());
		assertTrue(handler instanceof DateSynonymInUseRequestHandler);
	}

	private IRequestCommHandler<?> invokeGetHandler(RequestComm<?> requestcomm) throws Exception {
		return (IRequestCommHandler<?>) ReflectionUtil.executePrivate(
				handlerFactoryInstance,
				"getHandler",
				new Class[] { RequestComm.class },
				new Object[] { requestcomm });
	}

	protected void setUp() throws Exception {
		super.setUp();
		handlerFactoryInstance = ReflectionUtil.createInstance("com.mindbox.pe.server.servlet.HandlerFactory", new Class[0], new Object[0]);
	}

	protected void tearDown() throws Exception {
		// Tear downs for HandlerFactoryTest
		super.tearDown();
	}

}

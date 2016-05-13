package com.mindbox.pe.server.servlet;

import static com.mindbox.pe.server.ServerTestObjectMother.createDateSynonymInUseRequest;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.communication.RequestComm;
import com.mindbox.pe.server.AbstractTestWithTestConfig;
import com.mindbox.pe.server.servlet.handlers.DateSynonymInUseRequestHandler;
import com.mindbox.pe.server.servlet.handlers.IRequestCommHandler;

public class HandlerFactoryTest extends AbstractTestWithTestConfig {

	private Object handlerFactoryInstance = null;

	private IRequestCommHandler<?> invokeGetHandler(RequestComm<?> requestcomm) throws Exception {
		return (IRequestCommHandler<?>) ReflectionUtil.executePrivate(
				handlerFactoryInstance,
				"getHandler",
				new Class[] { RequestComm.class },
				new Object[] { requestcomm });
	}

	public void setUp() throws Exception {
		super.setUp();
		handlerFactoryInstance = ReflectionUtil.createInstance("com.mindbox.pe.server.servlet.HandlerFactory", new Class[0], new Object[0]);
	}

	@Test
	public void testGetHandlerWithDateSynonymInUseRequestHappyCase() throws Exception {
		IRequestCommHandler<?> handler = invokeGetHandler(createDateSynonymInUseRequest());
		assertTrue(handler instanceof DateSynonymInUseRequestHandler);
	}

}

package com.mindbox.pe.server.audit;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.unittest.AbstractTestBase;

public class DefaultAuditStorageTest extends AbstractTestBase {

	private DefaultAuditStorage defaultAuditStorage;

	@Before
	public void setUp() throws Exception {
		this.defaultAuditStorage = new DefaultAuditStorage();
	}

	@Test(expected = NullPointerException.class)
	public void testLogAuditEventWithNullThrowsNullPointerException() throws Exception {
		defaultAuditStorage.log(null);
	}

}

package com.mindbox.pe.client.applet.datesynonym;

import static com.mindbox.pe.unittest.UnitTestHelper.assertContains;
import static org.junit.Assert.assertNotNull;

import javax.swing.event.ChangeListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;

public class ManageDateSynonymTabTest extends AbstractClientTestBase {

	/**
	 * @throws Exception
	 */
	@Test
	public void testAll() throws Exception {
		assertNotNull(ManageDateSynonymTab.class.getInterfaces());
		assertContains(ManageDateSynonymTab.class.getInterfaces(), ChangeListener.class);
		ManageDateSynonymTab.reset();
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
}

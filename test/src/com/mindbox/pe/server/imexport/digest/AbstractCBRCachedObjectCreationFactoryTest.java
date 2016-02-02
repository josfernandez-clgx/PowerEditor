package com.mindbox.pe.server.imexport.digest;

import org.easymock.MockControl;
import org.xml.sax.Attributes;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.server.cache.CBRManager;

abstract class AbstractCBRCachedObjectCreationFactoryTest extends AbstractTestWithTestConfig {

	protected MockControl attributesMockControl;
	protected Attributes attributesMock;

	protected AbstractCBRCachedObjectCreationFactoryTest(String name) {
		super(name);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		config.initServer();
		attributesMockControl = MockControl.createControl(Attributes.class);
		attributesMock = (Attributes) attributesMockControl.getMock();
	}

	@Override
	protected void tearDown() throws Exception {
		CBRManager.getInstance().startDbLoading();
		super.tearDown();
	}
	
	protected final void setUpMockForId(int id) throws Exception {
		attributesMockControl.expectAndReturn(attributesMock.getValue("id"), String.valueOf(id));
	}

	protected final int setUpMockForNewId() throws Exception {
		int id = ObjectMother.createInt();
		setUpMockForId(id);
		return id;
	}

	protected void replayAllMocks() {
		attributesMockControl.replay();
	}

	protected void verifyAllMocks() {
		attributesMockControl.verify();
	}
}

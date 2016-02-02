package com.mindbox.pe.server.audit.command;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.server.spi.audit.AuditEvent;
import com.mindbox.pe.server.spi.audit.AuditEventType;
import com.mindbox.pe.server.spi.audit.AuditKBMaster;

public class AuditDataBuilderTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("AuditDataBuilderTest Tests");
		suite.addTestSuite(AuditDataBuilderTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
//
//	private static AuditDataBuilder createAuditDataBuilderForKBMod() {
//		AuditDataBuilder auditDataBuilder = new AuditDataBuilder(ObjectMother.createInt(), AuditEventType.KB_MOD, new Date(), null);
//		auditDataBuilder.insertAuditMasterLog(ObjectMother.createInt(), ObjectMother.createInt(), ObjectMother.createInt());
//		return auditDataBuilder;
//	}

	public AuditDataBuilderTest(String name) {
		super(name);
	}

	public void testAsAuditEvent() throws Exception {
		int i1 = ObjectMother.createInt();
		String user = ObjectMother.createString();
		Date date = new Date();
		AuditEvent auditEvent = AuditDataBuilder.asAuditEvent(i1, AuditEventType.KB_MOD, date, user);
		assertNotNull(auditEvent);
		assertEquals(i1, auditEvent.getAuditID());
		assertEquals(AuditEventType.KB_MOD, auditEvent.getAuditType());
		assertEquals(user, auditEvent.getUserName());
		assertEquals(date, auditEvent.getDate());
	}

	public void testSetAuditEventLogHappyCase() throws Exception {
		int i1 = ObjectMother.createInt();
		String user = null;
		Date date = new Date();

		AuditDataBuilder auditDataBuilder = new AuditDataBuilder(i1, AuditEventType.KB_MOD, date, user);
		auditDataBuilder.freeze();
		AuditEvent auditEvent = auditDataBuilder.getAuditEvent();
		assertNotNull(auditEvent);
		assertEquals(i1, auditEvent.getAuditID());
		assertEquals(AuditEventType.KB_MOD, auditEvent.getAuditType());
		assertEquals(user, auditEvent.getUserName());
		assertEquals(date, auditEvent.getDate());
	}

	public void testSetAuditMasterLogHappyCase() throws Exception {
		int i1 = ObjectMother.createInt();
		int i2 = ObjectMother.createInt();
		int i3 = ObjectMother.createInt();
		AuditDataBuilder auditDataBuilder = new AuditDataBuilder(ObjectMother.createInt(), AuditEventType.KB_MOD, new Date(), null);
		auditDataBuilder.insertAuditMasterLog(i1, i2, i3);
		auditDataBuilder.freeze();
		AuditKBMaster auditMaster = auditDataBuilder.getAuditEvent().getKBMaster(0);
		assertNotNull(auditMaster);
		assertEquals(i1, auditMaster.getKbAuditID());
		assertEquals(i2, auditMaster.getKbChangedTypeID());
		assertEquals(i3, auditMaster.getElementID());
	}

	// TODO Kim: move to AuditKBMasterBuilder tests
	/*
	public void testInsertAuditDetailLogHappyCase() throws Exception {
		AuditDataBuilder auditDataBuilder = createAuditDataBuilderForKBMod();
		int i1 = ObjectMother.createInt();
		int i2 = ObjectMother.createInt();
		String str = ObjectMother.createString();
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(i1, i2, str);
		assertEquals(1, auditDataBuilder.getBuildForLastKBMaster().getAuditKBMaster().detailCount());
		AuditKBDetail auditDetail = auditDataBuilder.getBuildForLastKBMaster().getAuditKBMaster().getDetail(0);
		assertNotNull(auditDetail);
		assertEquals(auditDataBuilder.getBuildForLastKBMaster().getAuditKBMaster().getKbAuditID(), auditDetail.getKbAuditID());
		assertEquals(i1, auditDetail.getKbAuditDetailID());
		assertEquals(i2, auditDetail.getKbModTypeID());
		assertEquals(str, auditDetail.getDescription());
	}

	public void testInsertAuditDetailLogWithNoMasterThrowsIllegalStateException() throws Exception {
		AuditDataBuilder auditDataBuilder = new AuditDataBuilder(ObjectMother.createInt(), AuditEventType.KB_MOD, new Date(), null);
		assertThrowsException(
				auditDataBuilder,
				"insertAuditDetailLog",
				new Class[] { int.class, int.class, String.class },
				new Object[] { ObjectMother.createInteger(), ObjectMother.createInteger(), ObjectMother.createString() },
				IllegalStateException.class);
	}

	public void testInsertAuditDetailLogWithExistingDetailIDThrowsIllegalArgumentException() throws Exception {
		AuditDataBuilder auditDataBuilder = createAuditDataBuilderForKBMod();
		int detailID = ObjectMother.createInt();
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(detailID, ObjectMother.createInt(), ObjectMother.createString());
		assertThrowsException(
				auditDataBuilder,
				"insertAuditDetailLog",
				new Class[] { int.class, int.class, String.class },
				new Object[] { new Integer(detailID), ObjectMother.createInteger(), ObjectMother.createString() },
				IllegalArgumentException.class);
	}

	public void testInsertAuditDetailDataLogHappyCase() throws Exception {
		AuditDataBuilder auditDataBuilder = createAuditDataBuilderForKBMod();
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(
				ObjectMother.createInt(),
				ObjectMother.createInt(),
				ObjectMother.createString());

		int i1 = ObjectMother.createInt();
		String str = ObjectMother.createString();
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
				auditDataBuilder.getBuildForLastKBMaster().getAuditKBMaster().getDetail(0).getKbAuditDetailID(),
				i1,
				str);
		assertEquals(1, auditDataBuilder.getBuildForLastKBMaster().getAuditKBMaster().getDetail(0).detailDataCount());
		AuditKBDetailData auditDetailData = auditDataBuilder.getBuildForLastKBMaster().getAuditKBMaster().getDetail(0).getDetailData(0);
		assertNotNull(auditDetailData);
		assertEquals(auditDataBuilder.getBuildForLastKBMaster().getAuditKBMaster().getDetail(0).getKbAuditDetailID(), auditDetailData
				.getKbAuditDetailID());
		assertEquals(i1, auditDetailData.getElementTypeID());
		assertEquals(str, auditDetailData.getElementValue());
	}

	public void testInsertAuditDetailDataLogHappyCaseMultiple() throws Exception {
		AuditDataBuilder auditDataBuilder = createAuditDataBuilderForKBMod();
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(
				ObjectMother.createInt(),
				ObjectMother.createInt(),
				ObjectMother.createString());

		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(auditDataBuilder
				.getBuildForLastKBMaster()
				.getAuditKBMaster()
				.getDetail(0)
				.getKbAuditDetailID(), ObjectMother.createInt(), ObjectMother.createString());
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(auditDataBuilder
				.getBuildForLastKBMaster()
				.getAuditKBMaster()
				.getDetail(0)
				.getKbAuditDetailID(), ObjectMother.createInt(), ObjectMother.createString());
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(auditDataBuilder
				.getBuildForLastKBMaster()
				.getAuditKBMaster()
				.getDetail(0)
				.getKbAuditDetailID(), ObjectMother.createInt(), ObjectMother.createString());
		assertEquals(3, auditDataBuilder.getBuildForLastKBMaster().getAuditKBMaster().getDetail(0).detailDataCount());
	}

	public void testInsertAuditDetailDataLogWithNoMasterThrowsIllegalStateException() throws Exception {
		AuditDataBuilder auditDataBuilder = new AuditDataBuilder(ObjectMother.createInt(), AuditEventType.KB_MOD, new Date(), null);
		assertThrowsException(
				auditDataBuilder,
				"insertAuditDetailDataLog",
				new Class[] { int.class, int.class, String.class },
				new Object[] { ObjectMother.createInteger(), ObjectMother.createInteger(), ObjectMother.createString() },
				IllegalStateException.class);
	}

	public void testInsertAuditDetailDataLogWithNonExistingElementTypeIDThrowsIllegalArgumentException() throws Exception {
		AuditDataBuilder auditDataBuilder = createAuditDataBuilderForKBMod();
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(
				ObjectMother.createInt(),
				ObjectMother.createInt(),
				ObjectMother.createString());
		int elementTypeID = ObjectMother.createInt();
		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailDataLog(
				auditDataBuilder.getBuildForLastKBMaster().getAuditKBMaster().getDetail(0).getKbAuditDetailID(),
				elementTypeID,
				ObjectMother.createString());

		assertThrowsException(
				auditDataBuilder,
				"insertAuditDetailDataLog",
				new Class[] { int.class, int.class, String.class },
				new Object[] { new Integer(auditDataBuilder.getBuildForLastKBMaster().getAuditKBMaster().getDetail(0).getKbAuditDetailID()), new Integer(
						elementTypeID), ObjectMother.createString() },
				IllegalArgumentException.class);
	}

	public void testInsertAuditDetailDataLogWithNonExistingDetailIDThrowsIllegalArgumentException() throws Exception {
		AuditDataBuilder auditDataBuilder = createAuditDataBuilderForKBMod();
		int detailID = ObjectMother.createInt();
		assertThrowsException(
				auditDataBuilder.,
				"insertAuditDetailDataLog",
				new Class[] { int.class, int.class, String.class },
				new Object[] { new Integer(detailID), ObjectMother.createInteger(), ObjectMother.createString() },
				IllegalArgumentException.class);

		auditDataBuilder.getBuildForLastKBMaster().insertAuditDetailLog(detailID, ObjectMother.createInt(), ObjectMother.createString());
		assertThrowsException(
				auditDataBuilder,
				"insertAuditDetailDataLog",
				new Class[] { int.class, int.class, String.class },
				new Object[] { new Integer(detailID + 1), ObjectMother.createInteger(), ObjectMother.createString() },
				IllegalArgumentException.class);
	}
	*/
}

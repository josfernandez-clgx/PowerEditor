package com.mindbox.pe.server.audit.command;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.AbstractGrid;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.ParameterGrid;
import com.mindbox.pe.model.ProductGrid;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.server.audit.AuditConstants;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.spi.audit.AuditEventType;
import com.mindbox.pe.server.spi.audit.AuditKBDetail;

public class ActivationContextUpdateAuditCommandTest extends AbstractAuditCommandTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("ActivationContextUpdateAuditCommandTest Tests");
		suite.addTestSuite(ActivationContextUpdateAuditCommandTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public ActivationContextUpdateAuditCommandTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
		useTestAuditStorage();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		EntityManager.getInstance().startLoading();
	}

	private <T extends AbstractGrid<?>> UpdateMultiActivationContextAuditCommand<T> createMultiActivationContextUpdateAuditCommand(T grid,
			GuidelineContext[] newContext, GuidelineContext[] oldContext) {
		return createMultiActivationContextUpdateAuditCommand(grid, newContext, oldContext, new Date(), ObjectMother.createString());
	}

	private <T extends AbstractGrid<?>> UpdateMultiActivationContextAuditCommand<T> createMultiActivationContextUpdateAuditCommand(T grid,
			GuidelineContext[] newContext, GuidelineContext[] oldContext, Date date, String userName) {
		Map<T, GuidelineContext[]> map = new HashMap<T, GuidelineContext[]>();
		map.put(grid, oldContext);
		UpdateMultiActivationContextAuditCommand<T> auditCommand = new UpdateMultiActivationContextAuditCommand<T>(
				map,
				newContext,
				date,
				userName);
		return auditCommand;
	}

	public void testExecuteWithNullAndNullContextsIsNoOp() throws Exception {
		UpdateMultiActivationContextAuditCommand<ProductGrid> auditCommand = createMultiActivationContextUpdateAuditCommand(ObjectMother
				.createGuidelineGrid(ObjectMother.createGridTemplate(ObjectMother.createUsageType())), null, null);
		auditCommand.execute(testAuditStorage);
	}

	public void testExecuteWithNoOldContextHappyCase() throws Exception {
		int categoryID = ObjectMother.createInt();
		int categoryType = GenericEntityType.forName("product").getCategoryType();
		EntityManager.getInstance().addGenericEntityCategory(categoryType, categoryID, "Category 10");
		ProductGrid grid = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(ObjectMother.createUsageType()));
		GuidelineContext context = new GuidelineContext(10);
		context.setIDs(new int[] { categoryID });

		String userName = ObjectMother.createString();
		UpdateMultiActivationContextAuditCommand<ProductGrid> auditCommand = createMultiActivationContextUpdateAuditCommand(
				grid,
				new GuidelineContext[] { context },
				null,
				new Date(),
				userName);
		auditCommand.execute(testAuditStorage);
		assertNotNull(testAuditStorage.getAuditEvent());
		assertNotNull(testAuditStorage.getFirstAuditMaster());
		assertEquals(AuditEventType.KB_MOD, testAuditStorage.getAuditEvent().getAuditType());
		assertEquals(userName, testAuditStorage.getAuditEvent().getUserName());
		assertEquals(AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION, testAuditStorage.getFirstAuditMaster().getKbChangedTypeID());
		assertEquals(grid.getID(), testAuditStorage.getFirstAuditMaster().getElementID());
		assertEquals(1, testAuditStorage.getFirstAuditMaster().detailCount());
		AuditKBDetail detail = testAuditStorage.getFirstAuditMaster().getDetail(0);
		assertEquals(AuditConstants.KB_MOD_TYPE_ADD_CONTEXT_ELEMENT, detail.getKbModTypeID());
		assertEquals(1, detail.detailDataCount());
		assertEquals(AuditConstants.KB_ELEMENT_TYPE_CATEGORY_ID, detail.getDetailData(0).getElementTypeID());
		assertEquals(detail.getKbAuditDetailID(), detail.getDetailData(0).getKbAuditDetailID());
		assertEquals(CategoryOrEntityValue.asString(GenericEntityType.forName("product"), false, context.getIDs()[0]), detail
				.getDetailData(0)
				.getElementValue());
	}

	public void testExecuteWithAddedEntityOfSameTypeContextHappyCase() throws Exception {
		int entityID = ObjectMother.createInt();
		GenericEntityType entityType = GenericEntityType.getAllGenericEntityTypes()[0];
		EntityManager.getInstance().addGenericEntity(entityID, entityType.getID(), "entity " + entityID, -1, null);
		EntityManager.getInstance().addGenericEntity(entityID + 1, entityType.getID(), "entity " + (entityID + 1), -1, null);
		ProductGrid grid = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(ObjectMother.createUsageType()));
		GuidelineContext context1 = new GuidelineContext(entityType);
		context1.setIDs(new int[] { entityID + 1 });
		GuidelineContext context2 = new GuidelineContext(entityType);
		context2.setIDs(new int[] { entityID, entityID + 1 });

		String userName = ObjectMother.createString();
		UpdateMultiActivationContextAuditCommand<ProductGrid> auditCommand = createMultiActivationContextUpdateAuditCommand(
				grid,
				new GuidelineContext[] { context2 },
				new GuidelineContext[] { context1 },
				new Date(),
				userName);
		auditCommand.execute(testAuditStorage);
		assertNotNull(testAuditStorage.getAuditEvent());
		assertNotNull(testAuditStorage.getFirstAuditMaster());
		assertEquals(AuditEventType.KB_MOD, testAuditStorage.getAuditEvent().getAuditType());
		assertEquals(userName, testAuditStorage.getAuditEvent().getUserName());
		assertEquals(AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION, testAuditStorage.getFirstAuditMaster().getKbChangedTypeID());
		assertEquals(grid.getID(), testAuditStorage.getFirstAuditMaster().getElementID());
		assertEquals(1, testAuditStorage.getFirstAuditMaster().detailCount());
		AuditKBDetail detail = testAuditStorage.getFirstAuditMaster().getDetail(0);
		assertEquals(AuditConstants.KB_MOD_TYPE_ADD_CONTEXT_ELEMENT, detail.getKbModTypeID());
		assertEquals(1, detail.detailDataCount());
		assertEquals(AuditConstants.KB_ELEMENT_TYPE_ENTITY_ID, detail.getDetailData(0).getElementTypeID());
		assertEquals(detail.getKbAuditDetailID(), detail.getDetailData(0).getKbAuditDetailID());
		assertEquals(CategoryOrEntityValue.asString(entityType, true, entityID), detail.getDetailData(0).getElementValue());
	}

	public void testExecuteWithAddedEntityOfDifferentTypeContextHappyCase() throws Exception {
		int entityID = ObjectMother.createInt();
		GenericEntityType entityType1 = GenericEntityType.getAllGenericEntityTypes()[0];
		GenericEntityType entityType2 = GenericEntityType.getAllGenericEntityTypes()[1];
		EntityManager.getInstance().addGenericEntity(entityID, entityType1.getID(), "entity " + entityID, -1, null);
		EntityManager.getInstance().addGenericEntity(entityID + 2, entityType2.getID(), "entity " + (entityID + 2), -1, null);
		ProductGrid grid = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(ObjectMother.createUsageType()));
		GuidelineContext context1 = new GuidelineContext(entityType1);
		context1.setIDs(new int[] { entityID });
		GuidelineContext context2 = new GuidelineContext(entityType2);
		context2.setIDs(new int[] { entityID + 2 });

		String userName = ObjectMother.createString();
		UpdateMultiActivationContextAuditCommand<ProductGrid> auditCommand = createMultiActivationContextUpdateAuditCommand(
				grid,
				new GuidelineContext[] { context1, context2 },
				new GuidelineContext[] { context2 },
				new Date(),
				userName);
		auditCommand.execute(testAuditStorage);
		assertNotNull(testAuditStorage.getAuditEvent());
		assertNotNull(testAuditStorage.getFirstAuditMaster());
		assertEquals(AuditEventType.KB_MOD, testAuditStorage.getAuditEvent().getAuditType());
		assertEquals(userName, testAuditStorage.getAuditEvent().getUserName());
		assertEquals(AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION, testAuditStorage.getFirstAuditMaster().getKbChangedTypeID());
		assertEquals(grid.getID(), testAuditStorage.getFirstAuditMaster().getElementID());
		assertEquals(1, testAuditStorage.getFirstAuditMaster().detailCount());
		AuditKBDetail detail = testAuditStorage.getFirstAuditMaster().getDetail(0);
		assertEquals(AuditConstants.KB_MOD_TYPE_ADD_CONTEXT_ELEMENT, detail.getKbModTypeID());
		assertEquals(1, detail.detailDataCount());
		assertEquals(AuditConstants.KB_ELEMENT_TYPE_ENTITY_ID, detail.getDetailData(0).getElementTypeID());
		assertEquals(detail.getKbAuditDetailID(), detail.getDetailData(0).getKbAuditDetailID());
		assertEquals(CategoryOrEntityValue.asString(entityType1, true, entityID), detail.getDetailData(0).getElementValue());
	}

	public void testExecuteWithRemovedEntityOfSameTypeContextHappyCase() throws Exception {
		int entityID = ObjectMother.createInt();
		GenericEntityType entityType = GenericEntityType.getAllGenericEntityTypes()[0];
		EntityManager.getInstance().addGenericEntity(entityID, entityType.getID(), "entity " + entityID, -1, null);
		EntityManager.getInstance().addGenericEntity(entityID + 1, entityType.getID(), "entity " + (entityID + 1), -1, null);
		ProductGrid grid = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(ObjectMother.createUsageType()));
		GuidelineContext context1 = new GuidelineContext(entityType);
		context1.setIDs(new int[] { entityID + 1 });
		GuidelineContext context2 = new GuidelineContext(entityType);
		context2.setIDs(new int[] { entityID, entityID + 1 });

		String userName = ObjectMother.createString();
		UpdateMultiActivationContextAuditCommand<ProductGrid> auditCommand = createMultiActivationContextUpdateAuditCommand(
				grid,
				new GuidelineContext[] { context1 },
				new GuidelineContext[] { context2 },
				new Date(),
				userName);
		auditCommand.execute(testAuditStorage);
		assertNotNull(testAuditStorage.getAuditEvent());
		assertNotNull(testAuditStorage.getFirstAuditMaster());
		assertEquals(AuditEventType.KB_MOD, testAuditStorage.getAuditEvent().getAuditType());
		assertEquals(userName, testAuditStorage.getAuditEvent().getUserName());
		assertEquals(AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION, testAuditStorage.getFirstAuditMaster().getKbChangedTypeID());
		assertEquals(grid.getID(), testAuditStorage.getFirstAuditMaster().getElementID());
		assertEquals(1, testAuditStorage.getFirstAuditMaster().detailCount());
		AuditKBDetail detail = testAuditStorage.getFirstAuditMaster().getDetail(0);
		assertEquals(AuditConstants.KB_MOD_TYPE_REMOVE_CONTEXT_ELEMENT, detail.getKbModTypeID());
		assertEquals(1, detail.detailDataCount());
		assertEquals(AuditConstants.KB_ELEMENT_TYPE_ENTITY_ID, detail.getDetailData(0).getElementTypeID());
		assertEquals(detail.getKbAuditDetailID(), detail.getDetailData(0).getKbAuditDetailID());
		assertEquals(CategoryOrEntityValue.asString(entityType, true, entityID), detail.getDetailData(0).getElementValue());
	}

	public void testExecuteWithRemovedEntityOfDifferentTypeContextHappyCase() throws Exception {
		int entityID = ObjectMother.createInt();
		GenericEntityType entityType1 = GenericEntityType.getAllGenericEntityTypes()[0];
		GenericEntityType entityType2 = GenericEntityType.getAllGenericEntityTypes()[1];
		EntityManager.getInstance().addGenericEntity(entityID, entityType1.getID(), "entity " + entityID, -1, null);
		EntityManager.getInstance().addGenericEntity(entityID + 2, entityType2.getID(), "entity " + (entityID + 2), -1, null);
		ProductGrid grid = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(ObjectMother.createUsageType()));
		GuidelineContext context1 = new GuidelineContext(entityType1);
		context1.setIDs(new int[] { entityID });
		GuidelineContext context2 = new GuidelineContext(entityType2);
		context2.setIDs(new int[] { entityID + 2 });

		String userName = ObjectMother.createString();
		UpdateMultiActivationContextAuditCommand<ProductGrid> auditCommand = createMultiActivationContextUpdateAuditCommand(
				grid,
				new GuidelineContext[] { context2 },
				new GuidelineContext[] { context1, context2 },
				new Date(),
				userName);
		auditCommand.execute(testAuditStorage);
		assertNotNull(testAuditStorage.getAuditEvent());
		assertNotNull(testAuditStorage.getFirstAuditMaster());
		assertEquals(AuditEventType.KB_MOD, testAuditStorage.getAuditEvent().getAuditType());
		assertEquals(userName, testAuditStorage.getAuditEvent().getUserName());
		assertEquals(AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION, testAuditStorage.getFirstAuditMaster().getKbChangedTypeID());
		assertEquals(grid.getID(), testAuditStorage.getFirstAuditMaster().getElementID());
		assertEquals(1, testAuditStorage.getFirstAuditMaster().detailCount());
		AuditKBDetail detail = testAuditStorage.getFirstAuditMaster().getDetail(0);
		assertEquals(AuditConstants.KB_MOD_TYPE_REMOVE_CONTEXT_ELEMENT, detail.getKbModTypeID());
		assertEquals(1, detail.detailDataCount());
		assertEquals(AuditConstants.KB_ELEMENT_TYPE_ENTITY_ID, detail.getDetailData(0).getElementTypeID());
		assertEquals(detail.getKbAuditDetailID(), detail.getDetailData(0).getKbAuditDetailID());
		assertEquals(CategoryOrEntityValue.asString(entityType1, true, entityID), detail.getDetailData(0).getElementValue());
	}

	public void testExecuteWithNoNewContextHappyCase() throws Exception {
		int categoryID = ObjectMother.createInt();
		int categoryType = GenericEntityType.forName("product").getCategoryType();
		EntityManager.getInstance().addGenericEntityCategory(categoryType, categoryID, "Category 10");
		ProductGrid grid = ObjectMother.createGuidelineGrid(ObjectMother.createGridTemplate(ObjectMother.createUsageType()));
		GuidelineContext context = new GuidelineContext(10);
		context.setIDs(new int[] { categoryID });

		String userName = ObjectMother.createString();
		UpdateMultiActivationContextAuditCommand<ProductGrid> auditCommand = createMultiActivationContextUpdateAuditCommand(
				grid,
				null,
				new GuidelineContext[] { context },
				new Date(),
				userName);
		auditCommand.execute(testAuditStorage);
		assertNotNull(testAuditStorage.getAuditEvent());
		assertNotNull(testAuditStorage.getFirstAuditMaster());
		assertEquals(AuditEventType.KB_MOD, testAuditStorage.getAuditEvent().getAuditType());
		assertEquals(userName, testAuditStorage.getAuditEvent().getUserName());
		assertEquals(AuditConstants.KB_CHANGED_ELMENT_TYPE_GUIDELINE_ACTIVATION, testAuditStorage.getFirstAuditMaster().getKbChangedTypeID());
		assertEquals(grid.getID(), testAuditStorage.getFirstAuditMaster().getElementID());
		assertEquals(1, testAuditStorage.getFirstAuditMaster().detailCount());
		AuditKBDetail detail = testAuditStorage.getFirstAuditMaster().getDetail(0);
		assertEquals(AuditConstants.KB_MOD_TYPE_REMOVE_CONTEXT_ELEMENT, detail.getKbModTypeID());
		assertEquals(1, detail.detailDataCount());
		assertEquals(AuditConstants.KB_ELEMENT_TYPE_CATEGORY_ID, detail.getDetailData(0).getElementTypeID());
		assertEquals(detail.getKbAuditDetailID(), detail.getDetailData(0).getKbAuditDetailID());
		assertEquals(CategoryOrEntityValue.asString(GenericEntityType.forName("product"), false, context.getIDs()[0]), detail
				.getDetailData(0)
				.getElementValue());
	}

	public void testExecuteWithParameterGridHappyCase() throws Exception {
		int categoryID = ObjectMother.createInt();
		int categoryType = GenericEntityType.forName("product").getCategoryType();
		EntityManager.getInstance().addGenericEntityCategory(categoryType, categoryID, "Category 10");
		ParameterGrid grid = ObjectMother.attachParameterTemplate(ObjectMother.createParameterGrid());
		GuidelineContext context = new GuidelineContext(10);
		context.setIDs(new int[] { categoryID });

		String userName = ObjectMother.createString();
		UpdateMultiActivationContextAuditCommand<ParameterGrid> auditCommand = createMultiActivationContextUpdateAuditCommand(
				grid,
				new GuidelineContext[] { context },
				null,
				new Date(),
				userName);
		auditCommand.execute(testAuditStorage);
		assertNotNull(testAuditStorage.getAuditEvent());
		assertNotNull(testAuditStorage.getFirstAuditMaster());
		assertEquals(AuditEventType.KB_MOD, testAuditStorage.getAuditEvent().getAuditType());
		assertEquals(userName, testAuditStorage.getAuditEvent().getUserName());
		assertEquals(AuditConstants.KB_CHANGED_ELMENT_TYPE_PARAMETER_ACTIVATION, testAuditStorage.getFirstAuditMaster().getKbChangedTypeID());
		assertEquals(grid.getID(), testAuditStorage.getFirstAuditMaster().getElementID());
		assertEquals(1, testAuditStorage.getFirstAuditMaster().detailCount());
		AuditKBDetail detail = testAuditStorage.getFirstAuditMaster().getDetail(0);
		assertEquals(AuditConstants.KB_MOD_TYPE_ADD_CONTEXT_ELEMENT, detail.getKbModTypeID());
		assertEquals(1, detail.detailDataCount());
		assertEquals(AuditConstants.KB_ELEMENT_TYPE_CATEGORY_ID, detail.getDetailData(0).getElementTypeID());
		assertEquals(detail.getKbAuditDetailID(), detail.getDetailData(0).getKbAuditDetailID());
		assertEquals(CategoryOrEntityValue.asString(GenericEntityType.forName("product"), false, context.getIDs()[0]), detail
				.getDetailData(0)
				.getElementValue());
	}
}

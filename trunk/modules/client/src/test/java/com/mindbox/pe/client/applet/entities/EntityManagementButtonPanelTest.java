package com.mindbox.pe.client.applet.entities;

import static com.mindbox.pe.client.ClientTestObjectMother.createGenericCategory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.entities.generic.GenericEntityDetailPanel;
import com.mindbox.pe.client.applet.entities.generic.GenericEntitySelectionPanel;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;

public class EntityManagementButtonPanelTest extends AbstractClientTestBase {

	private GenericCategory category;

	private static class DetailPanelImpl extends GenericEntityDetailPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2369027757151837922L;

		public DetailPanelImpl(GenericEntityType entityType) {
			super(entityType);
		}

		public GenericEntity getCurrentEntity() {
			return (GenericEntity) super.currentObject;
		}
	}

	private EntityManagementButtonPanel<GenericEntity> entityManagementButtonPanel;
	private DetailPanelImpl detailPanelImpl;
	private GenericEntitySelectionPanel selectionPanel;

	@Test
	public void test_copyEntitySetsCopyPoliciesToFalseAndSetParentID() throws Exception {
		logger.debug("in test_copyEntitySetsCopyPoliciesToFalseAndSetParentID...");

		List<GenericEntity> list = new ArrayList<GenericEntity>();
		GenericEntity entity = new GenericEntity(10, entityType1, "name");
		list.add(entity);
		selectionPanel.populate(list);
		selectionPanel.selectEntity(10);
		invoke_copyEntity();
		assertFalse(detailPanelImpl.getCurrentEntity().shouldCopyPolicies());
		assertEquals(10, detailPanelImpl.getCurrentEntity().getParentID());
	}

	@Test
	public void test_cloneEntitySetsCopyPoliciesToTrueAndSetParentID() throws Exception {
		logger.debug("in test_cloneEntitySetsCopyPoliciesToTrueAndSetParentID...");

		List<GenericEntity> list = new ArrayList<GenericEntity>();
		GenericEntity entity = new GenericEntity(10, entityType1, "name");
		list.add(entity);
		selectionPanel.populate(list);
		selectionPanel.selectEntity(10);
		invoke_cloneEntity();
		assertTrue(detailPanelImpl.getCurrentEntity().shouldCopyPolicies());
		assertEquals(10, detailPanelImpl.getCurrentEntity().getParentID());
	}

	// Added for TestTracker 1439: Editing cloned product name causes err msg on save
	@Test
	public void test_cloneEntitySetsSetForCloneToTrue() throws Exception {
		logger.debug("in test_cloneEntitySetsSetForCloneToTrue...");

		List<GenericEntity> list = new ArrayList<GenericEntity>();
		GenericEntity entity = new GenericEntity(10, entityType1, "name");
		list.add(entity);
		selectionPanel.populate(list);
		selectionPanel.selectEntity(10);
		invoke_cloneEntity();
		assertTrue(detailPanelImpl.getCurrentEntity().isForClone());
	}

	// Added for TestTracker 1439: Editing cloned product name causes err msg on save
	@Test
	public void test_editEntitySetsSetForCloneToFalse() throws Exception {
		logger.debug("in test_editEntitySetsSetForCloneToFalse...");

		List<GenericEntity> list = new ArrayList<GenericEntity>();
		GenericEntity entity = new GenericEntity(10, entityType1, "name");
		list.add(entity);
		selectionPanel.populate(list);
		selectionPanel.selectEntity(10);
		invoke_cloneEntity();
		//selectionPanel.discardChanges();

		selectionPanel.selectEntity(10);
		invoke_editEntity();
		assertFalse(detailPanelImpl.getCurrentEntity().isForClone());
	}

	private void invoke_cloneEntity() throws Exception {
		ReflectionUtil.executePrivate(entityManagementButtonPanel, "_cloneEntity", new Class[0], new Object[0]);
	}

	private void invoke_copyEntity() throws Exception {
		ReflectionUtil.executePrivate(entityManagementButtonPanel, "_copyEntity", new Class[0], new Object[0]);
	}

	private void invoke_editEntity() throws Exception {
		ReflectionUtil.executePrivate(entityManagementButtonPanel, "_editEntity", new Class[0], new Object[0]);
	}

	@Before
	public void setUp() throws Exception {
		logger.debug("in setUp...");
		try {
			super.setUp();

			category = createGenericCategory(entityType1);
			EntityModelCacheFactory.getInstance().addGenericCategory(category);
			detailPanelImpl = new DetailPanelImpl(entityType1);
			selectionPanel = GenericEntitySelectionPanel.createInstance(entityType1, true, detailPanelImpl, false);
			entityManagementButtonPanel = new EntityManagementButtonPanel<GenericEntity>(false, selectionPanel, entityType1, entityType1.getName(), true, false);
			entityManagementButtonPanel.setDetailPanel(detailPanelImpl);
		}
		catch (Exception e) {
			logger.error("Error in setUp", e);
		}
	}

	@After
	public void tearDown() throws Exception {
		logger.debug("in tearDown...");
		try {
			EntityModelCacheFactory.getInstance().removeGenericCategory(category);
			category = null;
			entityManagementButtonPanel = null;
			selectionPanel = null;
			detailPanelImpl = null;
			super.tearDown();
		}
		catch (Exception e) {
			logger.error("Error in tearDown", e);
		}

	}
}

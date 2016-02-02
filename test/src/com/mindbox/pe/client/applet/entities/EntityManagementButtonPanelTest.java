package com.mindbox.pe.client.applet.entities;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
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

		public DetailPanelImpl(GenericEntityType entityType) {
			super(entityType);
		}

		public GenericEntity getCurrentEntity() {
			return (GenericEntity) super.currentObject;
		}
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("EntityManagementButtonPanelTest Tests");
		suite.addTestSuite(EntityManagementButtonPanelTest.class);
		return suite;
	}

	private EntityManagementButtonPanel<GenericEntity> entityManagementButtonPanel;
	private DetailPanelImpl detailPanelImpl;
	private GenericEntitySelectionPanel selectionPanel;

	public EntityManagementButtonPanelTest(String name) {
		super(name);
	}

	public void test_copyEntitySetsCopyPoliciesToFalseAndSetParentID() throws Exception {
		List<GenericEntity> list = new ArrayList<GenericEntity>();
		GenericEntity entity = new GenericEntity(10, GenericEntityType.forName("channel"), "name");
		list.add(entity);
		selectionPanel.populate(list);
		selectionPanel.selectEntity(10);
		invoke_copyEntity();
		assertFalse(detailPanelImpl.getCurrentEntity().shouldCopyPolicies());
		assertEquals(10, detailPanelImpl.getCurrentEntity().getParentID());
	}

	public void test_cloneEntitySetsCopyPoliciesToTrueAndSetParentID() throws Exception {
		List<GenericEntity> list = new ArrayList<GenericEntity>();
		GenericEntity entity = new GenericEntity(10, GenericEntityType.forName("channel"), "name");
		list.add(entity);
		selectionPanel.populate(list);
		selectionPanel.selectEntity(10);
		invoke_cloneEntity();
		assertTrue(detailPanelImpl.getCurrentEntity().shouldCopyPolicies());
		assertEquals(10, detailPanelImpl.getCurrentEntity().getParentID());
	}

	// Added for TestTracker 1439: Editing cloned product name causes err msg on save
	public void test_cloneEntitySetsSetForCloneToTrue() throws Exception {
		List<GenericEntity> list = new ArrayList<GenericEntity>();
		GenericEntity entity = new GenericEntity(10, GenericEntityType.forName("channel"), "name");
		list.add(entity);
		selectionPanel.populate(list);
		selectionPanel.selectEntity(10);
		invoke_cloneEntity();
		assertTrue(detailPanelImpl.getCurrentEntity().isForClone());
	}

	// Added for TestTracker 1439: Editing cloned product name causes err msg on save
	public void test_editEntitySetsSetForCloneToFalse() throws Exception {
		List<GenericEntity> list = new ArrayList<GenericEntity>();
		GenericEntity entity = new GenericEntity(10, GenericEntityType.forName("channel"), "name");
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

	protected void setUp() throws Exception {
		super.setUp();
		category = ObjectMother.createGenericCategory(GenericEntityType.forName("channel"));
		EntityModelCacheFactory.getInstance().addGenericCategory(category);
		detailPanelImpl = new DetailPanelImpl(GenericEntityType.forName("channel"));
		selectionPanel = GenericEntitySelectionPanel.createInstance(GenericEntityType.forName("channel"), true, detailPanelImpl, false);
		entityManagementButtonPanel = new EntityManagementButtonPanel<GenericEntity>(
				false,
				selectionPanel,
				GenericEntityType.forName("channel"),
				"channel",
				true,
				false);
		entityManagementButtonPanel.setDetailPanel(detailPanelImpl);
	}

	protected void tearDown() throws Exception {
		EntityModelCacheFactory.getInstance().removeGenericCategory(category);
		category = null;
		entityManagementButtonPanel = null;
		selectionPanel = null;
		detailPanelImpl = null;
		super.tearDown();
	}
}

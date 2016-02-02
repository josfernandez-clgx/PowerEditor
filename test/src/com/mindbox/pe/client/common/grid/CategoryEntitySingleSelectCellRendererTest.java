package com.mindbox.pe.client.common.grid;

import javax.swing.JLabel;
import javax.swing.JTable;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.table.CategoryOrEntityValue;

public class CategoryEntitySingleSelectCellRendererTest extends AbstractClientTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("CategoryEntitySingleSelectCellRendererTest Tests");
		suite.addTestSuite(CategoryEntitySingleSelectCellRendererTest.class);
		return suite;
	}

	private GenericEntityType entityType;
	private GenericEntity entity;

	public CategoryEntitySingleSelectCellRendererTest(String name) {
		super(name);
	}

	public void testSetValueHandlesCategoryOrEntityValue() throws Exception {
		CategoryEntitySingleSelectCellRenderer cellRenderer = new CategoryEntitySingleSelectCellRenderer(
				ObjectMother.createEntityColumnDataSpecDigest(entityType.toString(), true, false, false));

		CategoryOrEntityValue value = new CategoryOrEntityValue(entity);
		JLabel component = (JLabel) cellRenderer.getTableCellRendererComponent(new JTable(), value, true, false, 0, 0);
		assertEquals(entity.getName(), component.getText());

	}

	public void testSetValueHandlesString() throws Exception {
		CategoryEntitySingleSelectCellRenderer cellRenderer = new CategoryEntitySingleSelectCellRenderer(
				ObjectMother.createEntityColumnDataSpecDigest(entityType.toString(), true, false, false));

		CategoryOrEntityValue value = new CategoryOrEntityValue(entity);
		JLabel component = (JLabel) cellRenderer.getTableCellRendererComponent(new JTable(), value.toString(), true, false, 0, 0);
		assertEquals(entity.getName(), component.getText());

	}

	protected void setUp() throws Exception {
		super.setUp();
		entityType = GenericEntityType.getAllGenericEntityTypes()[0];
		entity = ObjectMother.createGenericEntity(entityType);
		EntityModelCacheFactory.getInstance().add(entity);
	}

	protected void tearDown() throws Exception {
		// Tear downs for CategoryEntitySingleSelectCellRendererTest
		EntityModelCacheFactory.getInstance().remove(entity);
		super.tearDown();
	}
}

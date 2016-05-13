package com.mindbox.pe.client.common.grid;

import static com.mindbox.pe.client.ClientTestObjectMother.createEntityColumnDataSpecDigest;
import static com.mindbox.pe.client.ClientTestObjectMother.createGenericEntity;
import static org.junit.Assert.assertEquals;

import javax.swing.JLabel;
import javax.swing.JTable;

import org.junit.After;
import org.junit.Before;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.table.CategoryOrEntityValue;

public class CategoryEntitySingleSelectCellRendererTest extends AbstractClientTestBase {

	private GenericEntityType entityType;
	private GenericEntity entity;

	//@Test
	public void testSetValueHandlesCategoryOrEntityValue() throws Exception {
		CategoryEntitySingleSelectCellRenderer cellRenderer = new CategoryEntitySingleSelectCellRenderer(createEntityColumnDataSpecDigest(entityType.toString(), true, false, false));

		CategoryOrEntityValue value = new CategoryOrEntityValue(entity);
		JLabel component = (JLabel) cellRenderer.getTableCellRendererComponent(new JTable(), value, true, false, 0, 0);
		assertEquals(entity.getName(), component.getText());

	}

	//@Test
	public void testSetValueHandlesString() throws Exception {
		CategoryEntitySingleSelectCellRenderer cellRenderer = new CategoryEntitySingleSelectCellRenderer(createEntityColumnDataSpecDigest(entityType.toString(), true, false, false));

		CategoryOrEntityValue value = new CategoryOrEntityValue(entity);
		JLabel component = (JLabel) cellRenderer.getTableCellRendererComponent(new JTable(), value.toString(), true, false, 0, 0);
		assertEquals(entity.getName(), component.getText());
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		entityType = GenericEntityType.getAllGenericEntityTypes()[0];
		entity = createGenericEntity(entityType);
		EntityModelCacheFactory.getInstance().add(entity);
	}

	@After
	public void tearDown() throws Exception {
		// Tear downs for CategoryEntitySingleSelectCellRendererTest
		EntityModelCacheFactory.getInstance().remove(entity);
		super.tearDown();
	}
}

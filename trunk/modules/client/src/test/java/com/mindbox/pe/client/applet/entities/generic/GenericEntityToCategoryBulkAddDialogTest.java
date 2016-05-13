package com.mindbox.pe.client.applet.entities.generic;

import static com.mindbox.pe.client.ClientTestObjectMother.createGenericCategory;
import static com.mindbox.pe.client.ClientTestObjectMother.createGenericEntity;
import static org.junit.Assert.assertFalse;

import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;

public class GenericEntityToCategoryBulkAddDialogTest extends AbstractClientTestBase {

	private GenericCategory category;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		category = createGenericCategory(entityType1);
		EntityModelCacheFactory.getInstance().addGenericCategory(category);
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
		Map<?, ?> map = ReflectionUtil.getPrivate(EntityModelCacheFactory.getInstance(), "cachedGenericCategoryTreeModelMap", Map.class);
		map.clear();
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testInitWithEntity() throws Exception {
		GenericEntity entity = createGenericEntity(entityType1);

		GenericEntityToCategoryBulkAddDialog dialog = (GenericEntityToCategoryBulkAddDialog) ReflectionUtil.createInstance(
				"com.mindbox.pe.client.applet.entities.generic.GenericEntityToCategoryBulkAddDialog",
				new Class[] { GenericEntity.class, JDialog.class },
				new Object[] { entity, new JDialog() });

		assertFalse(((JTextField) ReflectionUtil.getPrivate(dialog, "entityNameField")).isEnabled());
		assertFalse(((JButton) ReflectionUtil.getPrivate(dialog, "okButton")).isEnabled());
	}
}

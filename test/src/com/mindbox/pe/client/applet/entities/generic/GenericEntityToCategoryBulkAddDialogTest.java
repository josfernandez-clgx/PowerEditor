package com.mindbox.pe.client.applet.entities.generic;

import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;

public class GenericEntityToCategoryBulkAddDialogTest extends AbstractClientTestBase {
	private GenericCategory category;

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GenericEntityToCategoryBulkAddDialogTest Tests");
		suite.addTestSuite(GenericEntityToCategoryBulkAddDialogTest.class);
		return suite;
	}

	public GenericEntityToCategoryBulkAddDialogTest(String name) {
		super(name);
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void testInitWithEntity() throws Exception {
		GenericEntity entity = ObjectMother.createGenericEntity(GenericEntityType.forName("product"));

		GenericEntityToCategoryBulkAddDialog dialog = (GenericEntityToCategoryBulkAddDialog) ReflectionUtil.createInstance(
				"com.mindbox.pe.client.applet.entities.generic.GenericEntityToCategoryBulkAddDialog",
				new Class[] { GenericEntity.class, JDialog.class },
				new Object[] { entity, new JDialog() });

		assertFalse(((JTextField) ReflectionUtil.getPrivate(dialog, "entityNameField")).isEnabled());
		assertFalse(((JButton) ReflectionUtil.getPrivate(dialog, "okButton")).isEnabled());
	}

	protected void setUp() throws Exception {
		super.setUp();
		category = ObjectMother.createGenericCategory(GenericEntityType.forName("product"));
		EntityModelCacheFactory.getInstance().addGenericCategory(category);
	}

	@SuppressWarnings("unchecked")
	protected void tearDown() throws Exception {
		super.tearDown();
		Map map = ReflectionUtil.getPrivate(EntityModelCacheFactory.getInstance(), "cachedGenericCategoryTreeModelMap", Map.class);
		map.clear();
	}
}

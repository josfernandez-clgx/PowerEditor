package com.mindbox.pe.client.applet.entities.generic;

import static com.mindbox.pe.client.ClientTestObjectMother.createGenericCategory;
import static org.junit.Assert.assertNull;

import javax.swing.JTextField;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.filter.GenericEntityFilterSpec;

public class GenericEntityFilterPanelTest extends AbstractClientTestBase {

	private GenericCategory category;
	private GenericEntityFilterPanel genericEntityFilterPanel;

	@Test
	public void testCreateFilterSpecFromFieldsWithEmptyNameFieldDoesNotSetNameCriterion() throws Exception {
		((JTextField) ReflectionUtil.getPrivate(genericEntityFilterPanel, "nameField")).setText(null);
		GenericEntityFilterSpec filterSpec = invokeCreateFilterSpecFromFields("filter");
		assertNull(filterSpec.getNameCriterion());
	}

	private GenericEntityFilterSpec invokeCreateFilterSpecFromFields(String filterName) {
		return (GenericEntityFilterSpec) ReflectionUtil.executePrivate(genericEntityFilterPanel, "createFilterSpecFromFields", new Class[] { String.class }, new Object[] { filterName });
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		category = createGenericCategory(entityType1);
		EntityModelCacheFactory.getInstance().addGenericCategory(category);
		GenericEntityType type = entityType1;
		genericEntityFilterPanel = new GenericEntityFilterPanel(type, GenericEntitySelectionPanel.createInstance(type, true, new GenericEntityDetailPanel(type), false));
	}

	@After
	public void tearDown() throws Exception {
		EntityModelCacheFactory.getInstance().removeGenericCategory(category);
		genericEntityFilterPanel = null;
		super.tearDown();
	}
}

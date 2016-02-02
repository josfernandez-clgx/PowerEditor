package com.mindbox.pe.client.applet.entities.generic;

import javax.swing.JTextField;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.GenericCategory;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.filter.GenericEntityFilterSpec;

public class GenericEntityFilterPanelTest extends AbstractClientTestBase {

	private GenericCategory category;

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GenericEntityFilterPanelTest Tests");
		suite.addTestSuite(GenericEntityFilterPanelTest.class);
		return suite;
	}

	private GenericEntityFilterPanel genericEntityFilterPanel;

	public GenericEntityFilterPanelTest(String name) {
		super(name);
	}

	public void testCreateFilterSpecFromFieldsWithEmptyNameFieldDoesNotSetNameCriterion() throws Exception {
		((JTextField) ReflectionUtil.getPrivate(genericEntityFilterPanel, "nameField")).setText(null);
		GenericEntityFilterSpec filterSpec = invokeCreateFilterSpecFromFields("filter");
		assertNull(filterSpec.getNameCriterion());
	}

	// Kim: Parent ID field is removed in PE 4.5.0 build 10
	/*
	public void testCreateFilterSpecFromFieldsWithNonSelectedParentFieldDoesNotSetParentIDCriterion() throws Exception {
		((GenericEntityComboBox)ReflectionUtil.getPrivate(genericEntityFilterPanel, "parentCombo")).setSelectedIndex(-1);
		GenericEntityFilterSpec filterSpec = invokeCreateFilterSpecFromFields("filter");
		assertEquals(Persistent.UNASSIGNED_ID, filterSpec.getParentIDCriteria());
	}*/

	private GenericEntityFilterSpec invokeCreateFilterSpecFromFields(String filterName) {
		return (GenericEntityFilterSpec) ReflectionUtil.executePrivate(
				genericEntityFilterPanel,
				"createFilterSpecFromFields",
				new Class[] { String.class },
				new Object[] { filterName });
	}

	protected void setUp() throws Exception {
		super.setUp();
		category = ObjectMother.createGenericCategory(GenericEntityType.forName("channel"));
		EntityModelCacheFactory.getInstance().addGenericCategory(category);
		GenericEntityType type = GenericEntityType.forName("channel");
		genericEntityFilterPanel = new GenericEntityFilterPanel(type, GenericEntitySelectionPanel.createInstance(
				type,
				true,
				new GenericEntityDetailPanel(type),
				false));
	}

	protected void tearDown() throws Exception {
		EntityModelCacheFactory.getInstance().removeGenericCategory(category);
		genericEntityFilterPanel = null;
		super.tearDown();
	}
}

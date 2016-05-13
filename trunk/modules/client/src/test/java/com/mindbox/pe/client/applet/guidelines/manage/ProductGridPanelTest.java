package com.mindbox.pe.client.applet.guidelines.manage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JTextArea;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.client.AbstractClientTestBase;
import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.common.TypeEnumValueComboBox;
import com.mindbox.pe.common.ReflectionUtil;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GuidelineContext;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.grid.ProductGrid;
import com.mindbox.pe.model.template.GridTemplate;

public class ProductGridPanelTest extends AbstractClientTestBase {


	/**
	 * Test removing a grid.
	 *
	 * @throws Exception
	 */
	@Test
	public void testRemoveGrids() throws Exception {
		ProductGridPanel panel = new ProductGridPanel(false);
		assertNotNull(panel.removedGrids);

		GridTemplate template = new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]);
		ProductGrid grid = new ProductGrid(1000, template, null, null);

		GuidelineContext c1 = new GuidelineContext(10);
		c1.setIDs(new int[] {});
		panel.populate(new GuidelineContext[] { c1 }, new ArrayList<ProductGrid>(), template, false, null);
		panel.removedGrids.add(grid);
		panel.discardChanges();
		assertTrue(panel.removedGrids.size() == 0);
	}

	/**
	 * Tests that the various buttons are enabled properly when the grid is in production
	 * and the use has edit production privileges
	 *
	 * @throws Exception
	 */
	@Test
	public void testProductionStatusGridDisablingWithPrivilege() throws Exception {
		ProductGridPanel panel = new ProductGridPanel(false);

		GridTemplate template = new GridTemplate(100, "test", TemplateUsageType.getAllInstances()[0]);
		ProductGrid grid = new ProductGrid(1000, template, null, null);
		grid.setStatus(ClientUtil.getHighestStatus());
		List<ProductGrid> gridList = new ArrayList<ProductGrid>();
		gridList.add(grid);

		GuidelineContext c1 = new GuidelineContext(10);
		c1.setIDs(new int[] {});
		panel.populate(new GuidelineContext[] { c1 }, gridList, template, false, grid);
		panel.setViewOnly(false);
		panel.setEnabled(true);
		assertFalse(((JButton) ReflectionUtil.getPrivate(panel, "saveGridButton")).isEnabled());
		assertTrue(((JButton) ReflectionUtil.getPrivate(panel, "editActivationBtn")).isEnabled());
		assertTrue(((JButton) ReflectionUtil.getPrivate(panel, "addActivationBtn")).isEnabled());
		assertTrue(((JButton) ReflectionUtil.getPrivate(panel, "cloneActivationBtn")).isEnabled());
		assertTrue(((JButton) ReflectionUtil.getPrivate(panel, "removeActivationBtn")).isEnabled());
		assertTrue(((JTextArea) ReflectionUtil.getPrivate(panel, "commentsField")).isEnabled());
		assertTrue(((TypeEnumValueComboBox) ReflectionUtil.getPrivate(panel, "statusField")).isEnabled());
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2004);
		grid.setExpirationDate(DateSynonym.createUnnamedInstance(c.getTime()));
		panel.setViewOnly(false);
		panel.setEnabled(true);
		assertFalse(((JButton) ReflectionUtil.getPrivate(panel, "saveGridButton")).isEnabled());
		assertTrue(((JButton) ReflectionUtil.getPrivate(panel, "editActivationBtn")).isEnabled());
		assertTrue(((JButton) ReflectionUtil.getPrivate(panel, "addActivationBtn")).isEnabled());
		assertTrue(((JButton) ReflectionUtil.getPrivate(panel, "cloneActivationBtn")).isEnabled());
		assertTrue(((JButton) ReflectionUtil.getPrivate(panel, "removeActivationBtn")).isEnabled());
		assertTrue(((JTextArea) ReflectionUtil.getPrivate(panel, "commentsField")).isEnabled());
		assertTrue(((TypeEnumValueComboBox) ReflectionUtil.getPrivate(panel, "statusField")).isEnabled());
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();

	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}
}

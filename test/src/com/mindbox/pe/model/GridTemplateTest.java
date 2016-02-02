/*
 * Created on Feb 28, 2006
 *
 */
package com.mindbox.pe.model;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;


/**
 * @since PowerEditor 5.0
 */
public class GridTemplateTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("GridTemplateTest Tests");
		suite.addTestSuite(GridTemplateTest.class);
		return suite;
	}

	private GridTemplate template;
    private GridTemplateColumn entityColumn;
    private GridTemplateColumn stringColumn;
    

	public GridTemplateTest(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();
        template = ObjectMother.createGridTemplate(ObjectMother.createUsageType());
        stringColumn = ObjectMother.createGridTemplateColumn(1, ObjectMother.createUsageType());
        stringColumn.setDataSpecDigest(ObjectMother.createColumnDataSpecDigest());
        
        entityColumn = ObjectMother.createGridTemplateColumn(2, ObjectMother.createUsageType());
        entityColumn.setDataSpecDigest(ObjectMother.createEntityColumnDataSpecDigest("product", false, false, false));

        template.addGridTemplateColumn(stringColumn);
        template.addGridTemplateColumn(entityColumn);        
	}

	public void tearDown() throws Exception {
        template = null;
	}

	public void testGetEntityTypeColumns() throws Exception {
        List<GridTemplateColumn> entityCols = template.getEntityTypeColumns();
        assertNotNull(entityCols);
        assertTrue(entityCols.size() == 1); 
        assertTrue(entityCols.get(0).equals(entityColumn));
	}
}

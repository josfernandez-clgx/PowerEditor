package com.mindbox.pe.model.filter;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.GridTemplate;

public class TemplateByNameVersionFilterTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("TemplateByNameVersionFilterTest Tests");
		suite.addTestSuite(TemplateByNameVersionFilterTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public TemplateByNameVersionFilterTest(String name) {
		super(name);
	}

	public void testIsAcceptableWithNullReturnsFalse() throws Exception {
		TemplateByNameVersionFilter filter = new TemplateByNameVersionFilter(null, null);
		assertFalse(filter.isAcceptable(null));
	}

	public void testIsAcceptablePositiveCase() throws Exception {
		GridTemplate template = ObjectMother.createGridTemplate(ObjectMother.createUsageType());
		// no criteria
		TemplateByNameVersionFilter filter = new TemplateByNameVersionFilter(null, null);
		assertTrue(filter.isAcceptable(template));

		// just name
		filter = new TemplateByNameVersionFilter(template.getName(), null);
		assertTrue(filter.isAcceptable(template));

		// name & version
		filter = new TemplateByNameVersionFilter(template.getName(), template.getVersion());
		assertTrue(filter.isAcceptable(template));
	}

	public void testIsAcceptableNegativeCase() throws Exception {
		GridTemplate template = ObjectMother.createGridTemplate(ObjectMother.createUsageType());
		// diff name
		TemplateByNameVersionFilter filter = new TemplateByNameVersionFilter(template.getName() + "X", template.getVersion());
		assertFalse(filter.isAcceptable(template));

		// diff version
		filter = new TemplateByNameVersionFilter(template.getName(), template.getVersion() + "X");
		assertFalse(filter.isAcceptable(template));
	}
}

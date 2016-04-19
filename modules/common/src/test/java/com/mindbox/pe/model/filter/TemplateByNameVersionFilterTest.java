package com.mindbox.pe.model.filter;

import static com.mindbox.pe.common.CommonTestObjectMother.createGridTemplate;
import static com.mindbox.pe.common.CommonTestObjectMother.createUsageType;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.unittest.AbstractTestBase;

public class TemplateByNameVersionFilterTest extends AbstractTestBase {

	@Test
	public void testIsAcceptableNegativeCase() throws Exception {
		GridTemplate template = createGridTemplate(createUsageType());
		// diff name
		TemplateByNameVersionFilter filter = new TemplateByNameVersionFilter(template.getName() + "X", template.getVersion());
		assertFalse(filter.isAcceptable(template));

		// diff version
		filter = new TemplateByNameVersionFilter(template.getName(), template.getVersion() + "X");
		assertFalse(filter.isAcceptable(template));
	}

	@Test
	public void testIsAcceptablePositiveCase() throws Exception {
		GridTemplate template = createGridTemplate(createUsageType());
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

	@Test
	public void testIsAcceptableWithNullReturnsFalse() throws Exception {
		TemplateByNameVersionFilter filter = new TemplateByNameVersionFilter(null, null);
		assertFalse(filter.isAcceptable(null));
	}
}

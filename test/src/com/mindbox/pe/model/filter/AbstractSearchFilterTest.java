package com.mindbox.pe.model.filter;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.Persistent;

public class AbstractSearchFilterTest extends AbstractTestBase {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AbstractSearchFilterTest Tests");
		suite.addTestSuite(AbstractSearchFilterTest.class);
		return suite;
	}

	private static class SearchFilterImpl<T extends Persistent> extends AbstractSearchFilter<T> {
		public SearchFilterImpl(EntityType entityType) {
			super(entityType);
		}

		public boolean isAcceptable(Persistent object) {
			return true;
		}

	}

	public AbstractSearchFilterTest(String name) {
		super(name);
	}

	public void testConstructorAcceptsNullEntityType() throws Exception {
		new SearchFilterImpl<GenericEntity>(null);
	}
}

package com.mindbox.pe.model.filter;

import org.junit.Test;

import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.Persistent;
import com.mindbox.pe.unittest.AbstractTestBase;

public class AbstractSearchFilterTest extends AbstractTestBase {

	private static class SearchFilterImpl<T extends Persistent> extends AbstractSearchFilter<T> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 123456789L;

		public SearchFilterImpl(PeDataType entityType) {
			super(entityType);
		}

		public boolean isAcceptable(Persistent object) {
			return true;
		}

	}

	@Test
	public void testConstructorAcceptsNullEntityType() throws Exception {
		new SearchFilterImpl<GenericEntity>(null);
	}
}

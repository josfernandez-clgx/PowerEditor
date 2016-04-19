package com.mindbox.pe.client.common.grid;

import static com.mindbox.pe.client.ClientTestObjectMother.createColumnDataSpecDigest;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.unittest.AbstractTestBase;

public class CellValidatorTest extends AbstractTestBase {


	@Test
	public void testValidateValueForSingleSelectEnumDataSpecWorksWithCustomEnums() throws Exception {
		List<String> enumValueList = new ArrayList<String>();
		enumValueList.add("Enum1");
		enumValueList.add("Enum2");
		enumValueList.add("Enum3");
		ColumnDataSpecDigest columnDataSpecDigest = createColumnDataSpecDigest();
		columnDataSpecDigest.setType(ColumnDataSpecDigest.TYPE_ENUM_LIST);
		columnDataSpecDigest.setIsBlankAllowed(true);
		assertTrue(CellValidator.validateValue("Enum2", columnDataSpecDigest));
	}

	// TODO Kim, 2008-05-28: Add Tests for more scenarios/ other methods

}

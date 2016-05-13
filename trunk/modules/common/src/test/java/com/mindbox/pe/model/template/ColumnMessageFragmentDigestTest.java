package com.mindbox.pe.model.template;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import org.junit.Before;
import org.junit.Test;

import com.mindbox.pe.model.template.ColumnMessageFragmentDigest;
import com.mindbox.pe.unittest.AbstractTestBase;
import com.mindbox.pe.xsd.config.CellSelectionType;

public class ColumnMessageFragmentDigestTest extends AbstractTestBase {

	private ColumnMessageFragmentDigest columnMessageFragmentDigest;

	@Test
	public void testCellSelectionDefaultsToDefaultKeyIfNotSet() throws Exception {
		assertEquals(CellSelectionType.DEFAULT, columnMessageFragmentDigest.getCellSelection());
	}

	@Before
	public void setUp() throws Exception {
		columnMessageFragmentDigest = new ColumnMessageFragmentDigest();

		// Test serialization
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		new ObjectOutputStream(out).writeObject(columnMessageFragmentDigest);
	}
}

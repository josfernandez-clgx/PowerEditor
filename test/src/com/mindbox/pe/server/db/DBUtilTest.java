package com.mindbox.pe.server.db;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;

public class DBUtilTest extends AbstractTestBase {

	public static Test suite() {
		TestSuite suite = new TestSuite("DBUtilTest Tests");
		suite.addTestSuite(DBUtilTest.class);
		return suite;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public DBUtilTest(String name) {
		super(name);
	}

	public void testEncodeDecodeSpaceHolderRoundTrip() throws Exception {
		String str = "  abc  def  zzz  ";
		assertEquals(str, DBUtil.decodeSpacePlaceHolder(DBUtil.encodeSpacePlaceHolder(str)));
	}

	public void testEncodeSpaceHolderWithNullReturnsNull() throws Exception {
		assertNull(DBUtil.encodeSpacePlaceHolder(null));
	}

	public void testEncodeSpaceHolderWithNoSpaceString() throws Exception {
		String str = "stralkmlsdfkmlsk\tabc";
		assertEquals(str, DBUtil.encodeSpacePlaceHolder(str));
	}

	public void testDecodeSpaceHolderWithNullReturnsNull() throws Exception {
		assertNull(DBUtil.decodeSpacePlaceHolder(null));
	}

	public void testDecodeSpaceHolderWithNoSpaceString() throws Exception {
		String str = "stralkmlsdfkmlsk\tabc";
		assertEquals(str, DBUtil.decodeSpacePlaceHolder(str));
	}

}

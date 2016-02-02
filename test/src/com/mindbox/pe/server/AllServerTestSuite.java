package com.mindbox.pe.server;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.server.audit.AllServerAuditTestSuite;
import com.mindbox.pe.server.bizlogic.AllServerBizLogicTestSuite;
import com.mindbox.pe.server.cache.AllServerCacheTestSuite;
import com.mindbox.pe.server.config.AllServerConfigTestSuite;
import com.mindbox.pe.server.db.AllDBTestSuite;
import com.mindbox.pe.server.enumsrc.AllServerEnumSrcTestSuite;
import com.mindbox.pe.server.generator.AllServerGeneratorTestSuite;
import com.mindbox.pe.server.imexport.AllImportExportTestSuite;
import com.mindbox.pe.server.ldap.AllServerLDAPTestSuite;
import com.mindbox.pe.server.model.AllServerModelTestSuite;
import com.mindbox.pe.server.report.AllServerReportTestSuite;
import com.mindbox.pe.server.servlet.AllServletTestSuite;
import com.mindbox.pe.server.spi.AllServerSpiTestSuite;
import com.mindbox.pe.server.validate.AllServerValidateTestSuite;


/**
 * Collection of all server test cases.
 * All tests in this collection calls server code directory, bypassing communication layer.
 * @author Gene Kim
 * @author MindBox, Inc
 * @since 
 */
public final class AllServerTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite("All Server Tests");
		suite.addTest(ContextUtilTest.suite());
		suite.addTest(RuleDefinitionStringWriterTest.suite());
		suite.addTest(RuleDefinitionUtilTest.suite());
		suite.addTest(UtilTest.suite());
		suite.addTest(AllDBTestSuite.suite());
		suite.addTest(AllImportExportTestSuite.suite());
		suite.addTest(AllServerAuditTestSuite.suite());
		suite.addTest(AllServerBizLogicTestSuite.suite());
		suite.addTest(AllServerCacheTestSuite.suite());
		suite.addTest(AllServerConfigTestSuite.suite());
		suite.addTest(AllServerEnumSrcTestSuite.suite());
		suite.addTest(AllServerGeneratorTestSuite.suite());
		suite.addTest(AllServerLDAPTestSuite.suite());
		suite.addTest(AllServerModelTestSuite.suite());
		suite.addTest(AllServerReportTestSuite.suite());
		suite.addTest(AllServerSpiTestSuite.suite());
		suite.addTest(AllServerValidateTestSuite.suite());
		suite.addTest(AllServletTestSuite.suite());
		return suite;
	}
}

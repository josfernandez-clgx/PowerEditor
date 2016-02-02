package com.mindbox.pe.model;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.model.admin.AllModelAdminTestSuite;
import com.mindbox.pe.model.assckey.AllModelAsscKeyTestSuite;
import com.mindbox.pe.model.comparator.AllModelComparatorTestSuite;
import com.mindbox.pe.model.filter.AllModelFilterTestSuite;
import com.mindbox.pe.model.rule.AllModelRuleTests;
import com.mindbox.pe.model.table.AllModelTableTestSuite;

/**
 * Collection of all model object test cases.
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since
 */
public final class AllModelTestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("All Model Object Tests");
        suite.addTest(GridTemplateTest.suite());
		suite.addTest(AbstractGuidelineContextHolderTest.suite());
		suite.addTest(AbstractGuidelineGridTest.suite());
		suite.addTest(AbstractIDObjectTest.suite());
		suite.addTest(AbstractIDNameObjectTest.suite());
		suite.addTest(AbstractGridTest.suite());
		suite.addTest(AbstractTemplateColumnTest.suite());
		suite.addTest(AbstractTemplateCoreTest.suite());
		suite.addTest(CBRCaseClassTest.suite());
		suite.addTest(CBRAttributeValueTest.suite());
		suite.addTest(ColumnDataSpecDigestTest.suite());
		suite.addTest(ColumnMessageFragmentDigestTest.suite());
		suite.addTest(DateSynonymTest.suite());
		suite.addTest(DeployTypeTest.suite());
		suite.addTest(EnumValueTest.suite());
		suite.addTest(FloatDomainAttributeTest.suite());
		suite.addTest(GenericCategoryTest.suite());
		suite.addTest(GenericEntityTest.suite());
		suite.addTest(GenericEntityTypeTest.suite());
		suite.addTest(GridTemplateColumnTest.suite());
		suite.addTest(GridCellCoordinatesTest.suite());
		suite.addTest(GridCellSetTest.suite());
		suite.addTest(GridValueContainerTest.suite());
        suite.addTest(GuidelineReportDataTest.suite());
		suite.addTest(ParameterGridTest.suite());
		suite.addTest(ProductGridTest.suite());
        suite.addTest(UserTest.suite());
        suite.addTest(AllModelAdminTestSuite.suite());        
		suite.addTest(AllModelAsscKeyTestSuite.suite());
		suite.addTest(AllModelComparatorTestSuite.suite());
		suite.addTest(AllModelFilterTestSuite.suite());
		suite.addTest(AllModelRuleTests.suite());
		suite.addTest(AllModelTableTestSuite.suite());
		return suite;
	}
}

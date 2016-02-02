/*
 * Created on 2004. 3. 4.
 *
 */
package com.mindbox.pe.client.common.grid;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public class AllClientCommonGridTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All Client Common Grid Tests");
        suite.addTest(AbstractGridTableTest.suite());
        suite.addTest(AbstractGridTableModelTest.suite());
        suite.addTest(CategoryEntitySingleSelectCellRendererTest.suite());
        suite.addTest(CellValidatorTest.suite());
        suite.addTest(CurrencyCellEditorTest.suite());
        suite.addTest(CurrencyCellRendererTest.suite());
        suite.addTest(CurrencyRangeCellRendererTest.suite());
        suite.addTest(DynamicStringCellEditorTest.suite());
        suite.addTest(EnumCellRendererTest.suite());
        suite.addTest(EnumCellEditorTest.suite());
        suite.addTest(ExcelAdapterTest.suite());
        suite.addTest(FloatCellRendererTest.suite());
        suite.addTest(FloatCellEditorTest.suite());
        suite.addTest(FloatRangeCellRendererTest.suite());
        suite.addTest(MultiSelectEnumCellEditorTest.suite());
        suite.addTest(MultiSelectEnumCellRendererTest.suite());
        return suite;
    }

}

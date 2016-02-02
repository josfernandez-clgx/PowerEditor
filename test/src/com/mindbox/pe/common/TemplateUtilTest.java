package com.mindbox.pe.common;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestBase;
import com.mindbox.pe.ObjectMother;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.FunctionParameterDefinition;


/**
 * @author Dan Gaughan
 * @since PowerEditor 5.0.0
 */
public class TemplateUtilTest extends AbstractTestBase {

    public TemplateUtilTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("TemplateUtilTest Tests");
        suite.addTestSuite(TemplateUtilTest.class);

        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGenerateAndAddColumnsEntityDeployType() throws Exception {
        GridTemplate template = ObjectMother.createGridTemplate(ObjectMother.createUsageType());
        ActionTypeDefinition actionType = ObjectMother.createActionTypeDefinition();
        FunctionParameterDefinition def = ObjectMother.createFunctionParameterDefinition();
        def.setDeployType(DeployType.ENTITY_LIST);
        actionType.addParameterDefinition(def);
        TemplateUtil.generateAndAddColumns(template,  actionType);
        assertNotNull(template.getColumns());
        assertTrue(template.getColumns().size() == 1);
        assertTrue(template.getColumn(1).getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENTITY));
    }
}

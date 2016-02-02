package com.mindbox.pe.model.rule;

import com.mindbox.pe.AbstractTestBase;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author Dan Gaughan
 * @since PowerEditor 5.0.0
 */
public class ConditionTest extends AbstractTestBase {
    public ConditionTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("ConditionTest Tests");
        suite.addTestSuite(ConditionTest.class);

        return suite;
    }

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testToOpStrForEntityMatchFunction() throws Exception {
        assertEquals(Condition.Aux.toOpString(Condition.OP_ENTITY_MATCH_FUNC),
            Condition.OPSTR_ENTITY_MATCH_FUNC);
    }

    public void testToOpIntForEntityMatchFunction() throws Exception {
        assertEquals(Condition.Aux.toOpInt(Condition.OPSTR_ENTITY_MATCH_FUNC),
            Condition.OP_ENTITY_MATCH_FUNC);
    }
    
    public void testToOpIntForEntityNotMatchFunction() throws Exception {
        assertEquals(Condition.Aux.toOpInt(Condition.OPSTR_NOT_ENTITY_MATCH_FUNC),
            Condition.OP_NOT_ENTITY_MATCH_FUNC);
    }
   
}

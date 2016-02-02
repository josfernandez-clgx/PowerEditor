package com.mindbox.pe.server.generator;

import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.mindbox.pe.AbstractTestWithTestConfig;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.server.generator.aemodel.AeRule;
import com.mindbox.server.parser.jtb.rule.RuleParser;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Action;
import com.mindbox.server.parser.jtb.rule.syntaxtree.DeploymentRule;

/**
 * Sample rule generator test.
 * <b>Do not include in the unit test suite</b>.
 * @author Geneho Kim
 *
 */
public class AeRuleBuilderTest extends AbstractTestWithTestConfig {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("AeRuleBuilderTest Tests");
		suite.addTestSuite(AeRuleBuilderTest.class);
		return suite;
	}

	public AeRuleBuilderTest(String name) {
		super(name);
	}

	public void testGenerateRuleSkeleton() throws Exception {
		String ruleString = "if LOAN.LoanPurpose in %column 1% and TEMP.MiType == \"No\" and  BORROWER.OccupancyType in %column 2% and" +
                " DEAL.Credit_Score between %column 3% and DEAL.AdjustedLTV between %column 4% and BORROWER.DocType in %column 5% then "+
                " pe_action(      function_name,price_adjustment_add,product_id, \"%productID%\",category_name, \"%categoryName%\"," +
                "category_id, \"%categoryID%\",price_adjustment, \"%column 6%\",mi_adjustment, t)";
		
		RuleParser.getInstance(new StringReader(ruleString));
		DeploymentRule deploymentRule = RuleParser.parseDeploymentRule();

		AeRule aeRule = new AeRuleBuilder().generateRuleSkeleton(deploymentRule, TemplateUsageType.getAllInstances()[0]);
		assertNotNull(aeRule);

		GridTemplate template = new GridTemplate(1, "name",TemplateUsageType.getAllInstances()[0]);
		
		RuleDefinition ruleDef = RuleGeneratorHelper.toRuleDefinition(template, -1, "ruleName", deploymentRule, null, null);
		assertNotNull(ruleDef);
	}
	
	public void testGenerateAction() throws Exception {
		String ruleString = "pe_action(price_adjustment, \"%column 6%\",attr,\"|ClassName.Attr|\", mi_adjustment, t)";
		
		RuleParser.getInstance(new StringReader(ruleString));
		Action action = RuleParser.Action();
		assertNotNull(action);

		AeRule aeRule = new AeRule();
		new AeRuleBuilder().visit(action, aeRule);
		assertNotNull(aeRule);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		config.initServer();
	}

	protected void tearDown() throws Exception {
		config.resetConfiguration();
		super.tearDown();
	}
}

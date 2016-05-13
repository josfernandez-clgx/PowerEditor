package com.mindbox.pe.server.generator;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import org.easymock.Capture;
import org.junit.Test;

import com.mindbox.pe.common.MutableBoolean;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.RuleDefinition;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.server.generator.aemodel.AeRule;
import com.mindbox.pe.server.model.User;
import com.mindbox.pe.server.servlet.ServletTest;
import com.mindbox.server.parser.jtb.rule.RuleParser;
import com.mindbox.server.parser.jtb.rule.syntaxtree.Action;
import com.mindbox.server.parser.jtb.rule.syntaxtree.DeploymentRule;

/**
 * Sample rule generator test. <b>Do not include in the unit test suite</b>.
 * 
 * @author Geneho Kim
 * 
 */
public class AeRuleBuilderTest extends ServletTest {

	@Test
	public void testGenerateAction() throws Exception {
		String ruleString = "pe_action(price_adjustment, \"%column 6%\",attr,\"|ClassName.Attr|\", mi_adjustment, t)";

		RuleParser.getInstance(new StringReader(ruleString));
		Action action = RuleParser.Action();
		assertNotNull(action);

		AeRule aeRule = new AeRule();
		new AeRuleBuilder().visit(action, aeRule);
		assertNotNull(aeRule);
	}

	@Test
	public void testGenerateRuleSkeleton() throws Exception {
		String ruleString = "if LOAN.LoanPurpose in %column 1% and TEMP.MiType == \"No\" and  BORROWER.OccupancyType in %column 2% and"
				+ " DEAL.Credit_Score between %column 3% and DEAL.AdjustedLTV between %column 4% and BORROWER.DocType in %column 5% then "
				+ " pe_action(      function_name,price_adjustment_add,product_id, \"%productID%\",category_name, \"%categoryName%\","
				+ "category_id, \"%categoryID%\",price_adjustment, \"%column 6%\",mi_adjustment, t)";

		useMockBizActionCoordinator();

		final Capture<ActionTypeDefinition> actionTypeDefinitionCapture = new Capture<ActionTypeDefinition>();
		expect(mockBizActionCoordinator.save(capture(actionTypeDefinitionCapture), User.class.cast(anyObject()))).andReturn(1);

		RuleParser.getInstance(new StringReader(ruleString));
		DeploymentRule deploymentRule = RuleParser.parseDeploymentRule();

		AeRule aeRule = new AeRuleBuilder().generateRuleSkeleton(deploymentRule, TemplateUsageType.getAllInstances()[0]);
		assertNotNull(aeRule);

		GridTemplate template = new GridTemplate(1, "name", TemplateUsageType.getAllInstances()[0]);

		expect(mockBizActionCoordinator.findActionTypeDefinitionWithDeploymentRule(eq(TemplateUsageType.getAllInstances()[0]), String.class.cast(anyObject()))).andReturn(null);

		replayAllMocks();

		final MutableBoolean mutableBoolean = new MutableBoolean(false);
		RuleDefinition ruleDef = RuleGeneratorHelper.toRuleDefinition(template, -1, "ruleName", deploymentRule, mutableBoolean, null);
		assertNotNull(ruleDef);
		assertTrue(mutableBoolean.booleanValue());

		verifyAllMocks();
	}
}

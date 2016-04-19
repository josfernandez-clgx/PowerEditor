package com.mindbox.pe.common;

import static com.mindbox.pe.common.CommonTestObjectMother.createActionTypeDefinition;
import static com.mindbox.pe.common.CommonTestObjectMother.createFunctionParameterDefinition;
import static com.mindbox.pe.common.CommonTestObjectMother.createGridTemplate;
import static com.mindbox.pe.common.CommonTestObjectMother.createUsageType;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.FunctionParameterDefinition;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.unittest.AbstractTestBase;


/**
 * @author Dan Gaughan
 * @since PowerEditor 5.0.0
 */
public class TemplateUtilTest extends AbstractTestBase {

	@Test
	public void testGenerateAndAddColumnsEntityDeployType() throws Exception {
		GridTemplate template = createGridTemplate(createUsageType());
		ActionTypeDefinition actionType = createActionTypeDefinition();
		FunctionParameterDefinition def = createFunctionParameterDefinition();
		def.setDeployType(DeployType.ENTITY_LIST);
		actionType.addParameterDefinition(def);
		TemplateUtil.generateAndAddColumns(template, actionType);
		assertNotNull(template.getColumns());
		assertTrue(template.getColumns().size() == 1);
		assertTrue(template.getColumn(1).getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENTITY));
	}
}

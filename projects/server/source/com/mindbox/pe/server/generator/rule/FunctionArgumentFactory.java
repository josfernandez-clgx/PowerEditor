package com.mindbox.pe.server.generator.rule;

import java.util.Date;

import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.rule.ClassReference;
import com.mindbox.pe.model.rule.ColumnReference;
import com.mindbox.pe.model.rule.FunctionParameter;
import com.mindbox.pe.model.rule.FunctionParameterDefinition;
import com.mindbox.pe.model.rule.FunctionTypeDefinition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

public final class FunctionArgumentFactory {

	private final PatternFactoryHelper helper;
	private final ObjectPatternFactory objectPatternFactory;

	public FunctionArgumentFactory(PatternFactoryHelper helper) {
		this.helper = helper;
		this.objectPatternFactory = new ObjectPatternFactory(helper);
	}

	public FunctionArgument createFunctionArgument(int paramNo, FunctionParameterContainer functionContainer, LHSPatternList patternList) throws RuleGenerationException {
		FunctionParameter parameter = functionContainer.getParameterAt(paramNo - 1);
		if (parameter == null) {
			helper.reportError("Invalid parameter number " + paramNo);
			throw new RuleGenerationException("Invalid parameter number " + paramNo);
		}
		else {
			String valueStr = parameter.valueString();
			FunctionTypeDefinition functionType = functionContainer.getFunctionTypeDefinition();
			FunctionParameterDefinition paramDef = functionType.getParameterDefinitionAt(paramNo);
			if (parameter instanceof ColumnReference) {
				int columnNo = ((ColumnReference) parameter).getColumnNo();
				ColumnReferencePatternValueSlot valueSlot = new ColumnReferencePatternValueSlot(columnNo);
				valueSlot.setParameterDeployType(paramDef.getDeployType());
				return valueSlot;
			}
			else if (parameter instanceof Reference) {
				valueStr = helper.asVariableName(((Reference) parameter).getAttributeName());
				// add attribute pattern for binding this variable
				patternList.append(objectPatternFactory.createSingleAttrbiuteObjectPattern((Reference) parameter));
			}
			else if (parameter instanceof ClassReference) {
				valueStr = helper.asVariableName(((ClassReference) parameter).getClassName());
				// add attribute pattern for binding this variable
				patternList.append(objectPatternFactory.createEmptyObjectPattern(((ClassReference) parameter).getClassName()));
			}
			// TT 1991
			else if (paramDef.getDeployType() == DeployType.DATE) {
				try {
					Date dateValue = RuleGeneratorHelper.parseToDate(valueStr);
					valueStr = (RuleGeneratorHelper.toRuleDateString(dateValue));
				}
				catch (Exception e) {
					helper.reportError("Invalid date: can't convert " + valueStr + " to date: " + e.getMessage());
					throw new RuleGenerationException("ERROR-INVALID-DATE-" + valueStr);
				}
			}
			else if (paramDef.getDeployType() == DeployType.STRING) {
				valueStr = RuleGeneratorHelper.QUOTE + valueStr + RuleGeneratorHelper.QUOTE;
			}
			return new StaticFunctionArgument(valueStr);
		}
	}

}

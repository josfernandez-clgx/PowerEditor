package com.mindbox.pe.server.generator.rule;

import static com.mindbox.pe.server.ServerTestObjectMother.createColumnReference;
import static com.mindbox.pe.server.ServerTestObjectMother.createReference;
import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static com.mindbox.pe.unittest.TestObjectMother.createString;

import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.FunctionParameter;
import com.mindbox.pe.model.rule.FunctionTypeDefinition;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.model.rule.Value;
import com.mindbox.pe.server.generator.AeMapper;
import com.mindbox.pe.server.generator.RuleGenerationException;

public final class RuleObjectMother {

	public static ObjectPattern appendAttributePattern(ObjectPattern objectPattern, String attributeName) {
		try {
			objectPattern.add(createAttributePattern(attributeName));
		}
		catch (RuleGenerationException e) {
			throw new RuntimeException(e);
		}
		return objectPattern;
	}

	public static ObjectPattern appendAttributePatterns(ObjectPattern objectPattern, int count) {
		try {
			for (int i = 0; i < count; i++) {
				objectPattern.add(createAttributePattern());
			}
		}
		catch (RuleGenerationException e) {
			throw new RuntimeException(e);
		}
		return objectPattern;
	}

	public static ObjectPattern appendAttributePatternWithStaticText(ObjectPattern objectPattern) {
		try {
			objectPattern.add(createAttributePatternWithStaticText());
		}
		catch (RuleGenerationException e) {
			throw new RuntimeException(e);
		}
		return objectPattern;
	}

	public static ObjectPattern appendAttributePatternWithStaticText(ObjectPattern objectPattern, String attributeName) {
		try {
			objectPattern.add(createAttributePatternWithStaticText(attributeName));
		}
		catch (RuleGenerationException e) {
			throw new RuntimeException(e);
		}
		return objectPattern;
	}

	public static ActivationSpanValueSlot createActivationSpanValueSlot() {
		return new ActivationSpanValueSlot();
	}

	public static AttributePattern createAttributePattern() {
		int id = createInt();
		return createAttributePattern("pe:attr" + id);
	}

	public static AttributePattern createAttributePattern(String attributeName) {
		return createAttributePattern(attributeName, AeMapper.makeAEVariable(attributeName));
	}

	public static AttributePattern createAttributePattern(String attributeName, String variableName) {
		return new StaticTextAttributePattern(attributeName, variableName);
	}

	public static AttributePattern createAttributePatternWithStaticText() {
		int id = createInt();
		String attributeName = "pe:attr" + id;
		return createAttributePatternWithStaticText(attributeName);
	}

	public static AttributePattern createAttributePatternWithStaticText(String attributeName) {
		return new StaticTextAttributePattern(attributeName, AeMapper.makeAEVariable(attributeName), createString());
	}

	public static CategoryIDValueSlot createCategoryIDValueSlot(GenericEntityType type) {
		return new CategoryIDValueSlot(type);
	}

	public static ColumnReferencePatternValueSlot createColumnReferencePatternValueSlot(int columnNo) {
		return new ColumnReferencePatternValueSlot(columnNo);
	}

	public static ColumnReferencePatternValueSlot createColumnReferencePatternValueSlot(int columnNo, String slotText) {
		return new ColumnReferencePatternValueSlot(columnNo, slotText);
	}

	public static EntityIDValueSlot createEntityIDValueSlot(GenericEntityType type) {
		return new EntityIDValueSlot(type, createString());
	}

	public static FunctionParameter createFunctionParameter(int paramNo, int columnNo) {
		return RuleElementFactory.getInstance().createFunctionParameter(paramNo, "Param" + paramNo, createColumnReference(columnNo));
	}

	public static FunctionParameter createFunctionParameter(int paramNo, Reference reference) {
		return RuleElementFactory.getInstance().createAttributeRefParameter(paramNo, "Param" + paramNo, reference.getAttributeName(), reference.getClassName());
	}

	public static FunctionParameter createFunctionParameterForClassReference(int paramNo, String className) {
		return RuleElementFactory.getInstance().createClassRefParameter(paramNo, "Param" + paramNo, className);
	}

	public static FunctionParameter createFunctionParameterForStringValue(int paramNo) {
		return RuleElementFactory.getInstance().createFunctionParameter(paramNo, "Param" + paramNo, createString());
	}

	public static FunctionTypeDefinition createFunctionTypeDefinition() {
		int id = createInt();
		return new FunctionTypeDefinition(id, "FunctionType-" + id, "FunctionType-" + id + " description");
	}

	public static LHSPatternList createLHSPatternList() {
		return new OptimizingLHSPatternList(LHSPatternList.TYPE_AND);
	}

	public static ObjectPattern createObjectPattern(String cName) {
		ObjectPattern objectPattern = OptimizingObjectPattern.createNormalInstance(cName, AeMapper.makeAEVariable(cName));
		return objectPattern;
	}

	public static Value createReferenceValue() {
		return RuleElementFactory.getInstance().createValue(createReference());
	}

	public static RuleNameValueSlot createRuleNameValueSlot() {
		return new RuleNameValueSlot();
	}

	public static Value createStringValue() {
		return RuleElementFactory.getInstance().createValue(createString());
	}

	public static StringValuePatternValueSlot createStringValuePatternValueSlot() {
		return new StringValuePatternValueSlot(createReference(), Condition.OP_EQUAL, "value-" + createString());
	}
}

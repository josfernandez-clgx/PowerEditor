package com.mindbox.pe.server.generator.value;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.generator.rule.ValueSlot;

public class CategoryIDValueSlotHelper extends AbstractRHSValueSlotHelper {

	public String generateValue(GuidelineGenerateParams ruleParams, ValueSlot valueSlot) throws RuleGenerationException {
		GenericEntityType productType = GenericEntityType.forName("product");
		if (productType != null && productType.hasCategory()) {
			if (ruleParams.getGenericCategoryIDs(productType) != null && ruleParams.getGenericCategoryIDs(productType).length > 0) {
				return RuleGeneratorHelper.QUOTE + UtilBase.toString(ruleParams.getGenericCategoryIDs(productType)) + RuleGeneratorHelper.QUOTE;
			}
		}
		return RuleGeneratorHelper.AE_NIL;
	}

}

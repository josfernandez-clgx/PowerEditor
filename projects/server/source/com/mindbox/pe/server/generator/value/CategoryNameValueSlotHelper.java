package com.mindbox.pe.server.generator.value;

import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.server.cache.EntityManager;
import com.mindbox.pe.server.generator.GuidelineGenerateParams;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;
import com.mindbox.pe.server.generator.rule.ValueSlot;

public class CategoryNameValueSlotHelper extends AbstractRHSValueSlotHelper {

	public String generateValue(GuidelineGenerateParams ruleParams, ValueSlot valueSlot) throws RuleGenerationException {
		GenericEntityType productType = GenericEntityType.forName("product");
		if (productType != null && productType.hasCategory()) {
			if (ruleParams.getGenericCategoryIDs(productType) != null && ruleParams.getGenericCategoryIDs(productType).length > 0) {
				StringBuffer buff = new StringBuffer();
				buff.append(RuleGeneratorHelper.QUOTE);
				int[] ids = ruleParams.getGenericCategoryIDs(productType);
				for (int i = 0; i < ids.length; i++) {
					if (i != 0) {
						buff.append(",");
					}
					buff.append((EntityManager.getInstance().getGenericCategory(productType.getCategoryType(), ids[i]) == null
							? "Category[" + ids[i] + "]"
							: EntityManager.getInstance().getGenericCategory(productType.getCategoryType(), ids[i]).getName()));
				}
				buff.append(RuleGeneratorHelper.QUOTE);
				return buff.toString();
			}
		}
		return RuleGeneratorHelper.AE_NIL;
	}

}

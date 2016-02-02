/**
 * 
 */
package com.mindbox.pe.server.generator.value.rhscolref;

import com.mindbox.pe.model.AbstractTemplateColumn;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

class CategoryOrEntityValueValueHelper extends AbstractWriteValueHelper<CategoryOrEntityValue> {

	@Override
	public void writeValue(StringBuilder buff, CategoryOrEntityValue value, AbstractTemplateColumn column, boolean addQuotes,
			boolean multiEnumAsSequence) throws RuleGenerationException {
		StringBuffer sequence = RuleGeneratorHelper.generateContextSequence(value.getEntityType().getName(), value.isForEntity()
				? "entity"
				: "category", new int[] { value.getId() });
		buff.append(" (create$ ");
		buff.append(sequence);
		buff.append(")");
	}

}
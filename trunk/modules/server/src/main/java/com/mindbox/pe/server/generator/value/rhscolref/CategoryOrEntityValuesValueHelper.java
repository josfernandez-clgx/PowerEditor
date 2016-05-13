/**
 * 
 */
package com.mindbox.pe.server.generator.value.rhscolref;

import java.util.Iterator;

import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.model.table.CategoryOrEntityValues;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

class CategoryOrEntityValuesValueHelper extends AbstractWriteValueHelper<CategoryOrEntityValues> {

	@Override
	public void writeValue(StringBuilder buff, CategoryOrEntityValues values, AbstractTemplateColumn column, boolean addQuotes,
			boolean multiEnumAsSequence) throws RuleGenerationException {
		if (values.size() > 0) {
			StringBuilder sequences = new StringBuilder();
			for (Iterator<CategoryOrEntityValue> i = values.iterator(); i.hasNext();) {
				CategoryOrEntityValue v = i.next();
				sequences.append(RuleGeneratorHelper.generateContextSequence(v.getEntityType().getName(), v.isForEntity()
						? "entity"
						: "category", new int[] { v.getId() }));
				sequences.append(" ");
			}
			buff.append(" (create$ ");
			if (values.isSelectionExclusion()) {
				buff.append("NOT ");
			}
			buff.append(sequences);
			buff.append(")");
		}
		else {
			buff.append(" nil");
		}
	}

}
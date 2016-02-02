/**
 * 
 */
package com.mindbox.pe.server.generator.value.rhscolref;

import com.mindbox.pe.model.AbstractTemplateColumn;
import com.mindbox.pe.model.table.IRange;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

class IRangeValueHelper extends AbstractWriteValueHelper<IRange> {

	@Override
	public void writeValue(StringBuilder buff, IRange irange, AbstractTemplateColumn column, boolean addQuotes,
			boolean multiEnumAsSequence) throws RuleGenerationException {
		buff.append(RuleGeneratorHelper.QUOTE);
		if (irange.isLowerValueInclusive())
			buff.append("[");
		else
			buff.append("(");

		buff.append((irange.getFloor() == null ? "" : irange.getFloor().toString()));
		buff.append(",");
		buff.append((irange.getCeiling() == null ? "" : irange.getCeiling().toString()));

		if (irange.isUpperValueInclusive())
			buff.append("]");
		else
			buff.append(")");
		buff.append(RuleGeneratorHelper.QUOTE);
	}

}
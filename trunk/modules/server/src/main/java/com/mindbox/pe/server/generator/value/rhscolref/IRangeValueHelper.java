/**
 * 
 */
package com.mindbox.pe.server.generator.value.rhscolref;

import com.mindbox.pe.model.table.IRange;
import com.mindbox.pe.model.template.AbstractTemplateColumn;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

class IRangeValueHelper extends AbstractWriteValueHelper<IRange> {

	@Override
	public void writeValue(StringBuilder buff, IRange iRange, AbstractTemplateColumn column, boolean addQuotes, boolean multiEnumAsSequence) throws RuleGenerationException {
		buff.append(RuleGeneratorHelper.QUOTE);
		if (iRange.isLowerValueInclusive()) {
			buff.append("[");
		}
		else {
			buff.append("(");
		}

		if (column.getColumnDataSpecDigest().isFloatRangeType()) {
			buff.append((iRange.getFloor() == null ? "" : RuleGeneratorHelper.getFormatterFactoryForCurrentThread().getFloatFormatter(column.getColumnDataSpecDigest().getPercisionAsInteger()).format(
					iRange.getFloor())));
			buff.append(",");
			buff.append((iRange.getCeiling() == null
					? ""
					: RuleGeneratorHelper.getFormatterFactoryForCurrentThread().getFloatFormatter(column.getColumnDataSpecDigest().getPercisionAsInteger()).format(iRange.getCeiling())));
		}
		else {
			buff.append((iRange.getFloor() == null ? "" : iRange.getFloor().toString()));
			buff.append(",");
			buff.append((iRange.getCeiling() == null ? "" : iRange.getCeiling().toString()));
		}

		if (iRange.isUpperValueInclusive()) {
			buff.append("]");
		}
		else {
			buff.append(")");
		}

		buff.append(RuleGeneratorHelper.QUOTE);
	}

}
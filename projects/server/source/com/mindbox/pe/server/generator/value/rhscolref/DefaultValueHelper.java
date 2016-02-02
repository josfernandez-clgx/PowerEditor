/**
 * 
 */
package com.mindbox.pe.server.generator.value.rhscolref;

import com.mindbox.pe.model.AbstractTemplateColumn;
import com.mindbox.pe.model.ColumnDataSpecDigest;
import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.ColumnDataSpecDigest.EnumSourceType;
import com.mindbox.pe.server.cache.DeploymentManager;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.generator.RuleGenerationException;
import com.mindbox.pe.server.generator.RuleGeneratorHelper;

class DefaultValueHelper extends AbstractWriteValueHelper<Object> {

	@Override
	public void writeValue(StringBuilder buff, Object valueObj, AbstractTemplateColumn column, boolean addQuotes,
			boolean multiEnumAsSequence) throws RuleGenerationException {
		// Gene Kim, 2008-06-10: This shouldn't happen; i.e., valueObj should be an instanceof EnumValue for EnumList columns
		//                       This is here for backward-compatibility!
		if (column.getColumnDataSpecDigest().getEnumSourceType() == EnumSourceType.DOMAIN_ATTRIBUTE && column.getMAAttributeName() != null) {
			logger.debug("writeVal: column attributeMap = " + column.getMAClassName() + "." + column.getMAAttributeName());
			DomainAttribute attribute = DomainManager.getInstance().getDomainAttribute(column.getMAClassName(), column.getMAAttributeName());
			if (attribute != null && column.getColumnDataSpecDigest().getType().equals(ColumnDataSpecDigest.TYPE_ENUM_LIST)) {
				logger.debug("writeVal: following attribute map for String...");
				String valueStr = DeploymentManager.getInstance().getEnumDeployValue(
						column.getMAClassName(),
						column.getMAAttributeName(),
						valueObj.toString(),
						false);
				if (attribute.getDeployType() == DeployType.STRING) {
					buff.append(RuleGeneratorHelper.QUOTE);
					buff.append((valueStr == null ? "ERROR-DeployValue-Not-Found-For-" + valueObj.toString() : valueStr));
					buff.append(RuleGeneratorHelper.QUOTE);
					// report error
					if (valueStr == null) {
						throw new RuleGenerationException(valueObj.toString() + " has no deploy value for attribute "
								+ column.getMAClassName() + "." + column.getMAAttributeName() + "; Column=" + column);
					}
				}
				else {
					buff.append((valueStr == null ? "ERROR-DeployValue-Not-Found-For-" + valueObj.toString() : valueStr));
					// report error
					if (valueStr == null) {
						throw new RuleGenerationException(valueObj.toString() + " has no deploy value for attribute "
								+ column.getMAClassName() + "." + column.getMAAttributeName() + "; Column=" + column);
					}
				}
				return;
			}
		}
		if (addQuotes) {
			buff.append(RuleGeneratorHelper.QUOTE + RuleGeneratorHelper.formatForSprintf(valueObj.toString()) + RuleGeneratorHelper.QUOTE);
		}
		else {
			buff.append(valueObj.toString());
		}
	}
}
package com.mindbox.pe.client.applet.template.rule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.rule.ActionTypeDefinition;
import com.mindbox.pe.model.rule.FunctionCall;
import com.mindbox.pe.model.rule.RuleAction;
import com.mindbox.pe.model.rule.RuleElementFactory;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class ActionEditDialog extends FunctionEditDialog {

	private static Map<TemplateUsageType, List<ActionTypeDefinition>> usageTypeMap = new HashMap<TemplateUsageType, List<ActionTypeDefinition>>();

	/**
	 * 
	 */
	protected ActionEditDialog(JDialog dialog, TemplateUsageType usageType, RuleAction action) {
		super(dialog, (FunctionCall)action, usageType);

	}

	public FunctionCall createFunctionCallInstance() {
		return (FunctionCall)RuleElementFactory.getInstance().createRuleAction();
	}

	public String getFunctionTypeLabel() {
		return "label.action.type";
	}

	public String getIconString() {
		return "image.node.adhoc.action";
	}
	
	public List<ActionTypeDefinition> getTypeList(Object typeDeterminer) {
		TemplateUsageType usageType = (TemplateUsageType)typeDeterminer;
		List<ActionTypeDefinition> typeList = null;
		if (usageTypeMap.containsKey(usageType)) {
			typeList = usageTypeMap.get(usageType);
		}
		else {
			try {
				typeList = ClientUtil.getActionTypes(usageType);
				usageTypeMap.put(usageType, typeList);
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
		return typeList;
	}

	public static void resetTypeListMap() {
		usageTypeMap  = new HashMap<TemplateUsageType, List<ActionTypeDefinition>>();
	}
}

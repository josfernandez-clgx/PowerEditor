package com.mindbox.pe.client.applet.template.rule;

import java.util.List;

import javax.swing.JDialog;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.model.rule.FunctionCall;
import com.mindbox.pe.model.rule.FunctionTypeDefinition;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.model.rule.TestCondition;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class TestConditionEditDialog extends FunctionEditDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private static List<? extends FunctionTypeDefinition> testTypeList = null;

	/**
	 * 
	 */
	protected TestConditionEditDialog(JDialog dialog, TestCondition test) {
		super(dialog, (FunctionCall) test, null);

	}

	public FunctionCall createFunctionCallInstance() {
		return (FunctionCall) RuleElementFactory.getInstance().createTestCondition();
	}

	public String getFunctionTypeLabel() {
		return "label.test.type";
	}

	public String getIconString() {
		return "image.node.adhoc.test";
	}

	public List<? extends FunctionTypeDefinition> getTypeList(Object typeDeterminer) {
		if (testTypeList == null) {
			try {
				testTypeList = ClientUtil.getTestTypes();
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
		return testTypeList;
	}

	public static void resetTypeList() {
		testTypeList = null;
	}
}

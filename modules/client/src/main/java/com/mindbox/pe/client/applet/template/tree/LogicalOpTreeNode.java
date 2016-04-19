/*
 * Created on 2004. 1. 28.
 *
 */
package com.mindbox.pe.client.applet.template.tree;

import javax.swing.tree.TreeNode;

import com.mindbox.pe.model.rule.CompoundLHSElement;
import com.mindbox.pe.model.rule.Condition;
import com.mindbox.pe.model.rule.ExistExpression;
import com.mindbox.pe.model.rule.RuleElement;
import com.mindbox.pe.model.rule.RuleElementFactory;
import com.mindbox.pe.model.rule.TestCondition;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class LogicalOpTreeNode extends AbstractRuleTreeNode implements LogicalOpAttachable {

	private final int type;
	
	/**
	 * @param data
	 */
	public LogicalOpTreeNode(TreeNode parent, CompoundLHSElement data) {
		super(parent, data);
		type = data.getType();
		for (int i = 0; i < data.size(); i++) {
			RuleElement element = data.get(i);
			if (element instanceof Condition) {
				addChild(new ConditionTreeNode(this, RuleElementFactory.deepCopyCondition((Condition) element)));
			}
			else if (element instanceof TestCondition) {
				addChild(new TestTreeNode(this, RuleElementFactory.deepCopyTestCondition((TestCondition)element)));
			}
			else if (element instanceof CompoundLHSElement) {
				addChild(new LogicalOpTreeNode(this, (CompoundLHSElement) element));
			}
			else if (element instanceof ExistExpression) {
				addChild(new ExistTreeNode(this, RuleElementFactory.deepCopyExistExpression((ExistExpression) element)));
			}
		}
	}

	
	public void swapChildren(int index1, int index2) {
		if (index1 == index2) return;
		super.swapChildren(index1, index2);
		CompoundLHSElement data = (CompoundLHSElement) super.data;
		data.swapRuleElements(index1, index2);
	}
	
	public int getCompoundLHSElementType() {
		return type;
	}

}

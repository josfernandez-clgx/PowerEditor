/*
 * Created on 2004. 8. 17.
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
 * @since PowerEditor 
 */
public class ExistTreeNode extends AbstractRuleTreeNode implements LogicalOpAttachable {

	public ExistTreeNode(TreeNode parent, ExistExpression data) {
		super(parent, data);
		for (int i = 0; i < data.getCompoundLHSElement().size(); i++) {
			RuleElement element = data.getCompoundLHSElement().get(i);
			if (element instanceof Condition) {
				addChild(new ConditionTreeNode(this, RuleElementFactory.deepCopyCondition((Condition) element)));
			}
			else if (element instanceof CompoundLHSElement) {
				addChild(new LogicalOpTreeNode(this, RuleElementFactory.deepCopyCompoundLHSElement((CompoundLHSElement) element)));
			}
			else if (element instanceof ExistExpression) {
				addChild(new ExistTreeNode(this, RuleElementFactory.deepCopyExistExpression((ExistExpression) element)));
			}
			else if (element instanceof TestCondition) {
				addChild(new TestTreeNode(this, RuleElementFactory.deepCopyTestCondition((TestCondition) element)));
			}
		}
	}

	public String getExistClassName() {
		return ((ExistExpression) super.data).getClassName();
	}

	public ExistExpression getExistExpression() {
		return (ExistExpression) super.data;
	}
}

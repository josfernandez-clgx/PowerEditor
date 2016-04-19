/*
 * Created on 2004. 3. 9.
 *
 */
package com.mindbox.pe.server.generator.processor;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.AdditiveExpression;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.AndExpression;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.ConditionalExpression;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.ConditionalOperator;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.DeploymentRule;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.LiteralExpression;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.MembershipOperator;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.MultiplicativeExpression;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.NodeSequence;
import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.OrExpression;
import com.mindbox.pe.server.parser.jtb.rule.visitor.TreeFormatter;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public class PERuleTreeFormatter extends TreeFormatter {

	public PERuleTreeFormatter() {
		super(4, 0);
	}

	/*private void acceptLogicalSequence(NodeSequence nodesequence) {
		add(outdent());
		add(force());
		nodesequence.elementAt(0).accept(this);
		add(indent());
		nodesequence.elementAt(1).accept(this);
	}*/

	public void visit(DeploymentRule n) {
		n.f0.accept(this);

		add(indent());
		add(force());
		n.f1.accept(this);

		add(outdent());
		add(force());
		n.f2.accept(this);

		add(indent());
		add(force());
		n.f3.accept(this);

		add(outdent());
		add(force());
		
		n.f4.accept(this);
	}

	public void visit(OrExpression n) {
		if (n.f1.present()) {
			add(indent());
		}
		n.f0.accept(this);
		add(force());

		if (n.f1.present()) {
			for (int i = 0; i < n.f1.size(); i++) {
				add(outdent());
				NodeSequence seq = (NodeSequence) n.f1.elementAt(i);
				seq.elementAt(0).accept(this);
				add(indent());
			}
		}
	}

	public void visit(AndExpression n) {
		if (n.f1.present()) {
			add(indent());
		}
		n.f0.accept(this);
		add(force());

		if (n.f1.present()) {
			for (int i = 0; i < n.f1.size(); i++) {
				add(outdent());
				NodeSequence seq = (NodeSequence) n.f1.elementAt(i);
				seq.elementAt(0).accept(this);
				add(indent());
			}
		}
	}

	public void visit(ConditionalExpression n) {
		super.visit(n);
	}

	public void visit(MultiplicativeExpression n) {
		super.visit(n);
	}

	public void visit(AdditiveExpression n) {
		super.visit(n);
	}

	/*public void visit(NodeToken n) {
		super.visit(n);
		add(space());
	}*/

	public void visit(LiteralExpression n) {
		super.visit(n);
		//add(space());
	}

	public void visit(ConditionalOperator n) {
		add(space());
		super.visit(n);
		add(space());
	}

	public void visit(MembershipOperator n) {
		add(space());
		super.visit(n);
		add(space());
	}

}

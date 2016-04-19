package com.mindbox.server.parser.jtb.rule.syntaxtree;

import java.io.Serializable;

import com.mindbox.server.parser.jtb.rule.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.rule.visitor.Visitor;

public interface Node extends Serializable {

	public abstract void setParent(Node node);

	public abstract Node getParent();

	public abstract void accept(Visitor visitor);

	public abstract Object accept(ObjectVisitor objectvisitor, Object obj);
}
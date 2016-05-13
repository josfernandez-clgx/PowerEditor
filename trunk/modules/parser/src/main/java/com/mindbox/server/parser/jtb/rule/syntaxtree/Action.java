package com.mindbox.server.parser.jtb.rule.syntaxtree;

import com.mindbox.server.parser.jtb.rule.visitor.ObjectVisitor;
import com.mindbox.server.parser.jtb.rule.visitor.Visitor;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.syntaxtree:
//            Name, Arguments, Node

public class Action implements Node {

	public void setParent(Node node) {
		parent = node;
	}

	public Node getParent() {
		return parent;
	}

	public Action(Name name1, Arguments arguments1) {
		name = name1;
		if (name != null)
			name.setParent(this);
		arguments = arguments1;
		if (arguments != null)
			arguments.setParent(this);
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public Object accept(ObjectVisitor objectvisitor, Object obj) {
		return objectvisitor.visit(this, obj);
	}

	public String toString() {
		return "Action[name="+name+",arguments="+arguments+"]";
	}
	
	private Node parent;
	public Name name;
	public Arguments arguments;
}
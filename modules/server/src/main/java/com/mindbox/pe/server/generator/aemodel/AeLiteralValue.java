package com.mindbox.pe.server.generator.aemodel;

import com.mindbox.server.parser.jtb.rule.syntaxtree.Node;

// Referenced classes of package com.mindbox.server.generator:
//            AbstractAeValue

public class AeLiteralValue extends AbstractAeValue {

	public Object getValue() {
		return mValue;
	}

	public void setValue(Object obj) {
		mValue = obj;
	}

	public String toString() {
		if (mValue == null)
			return "<null>";
		else
			return mValue.toString();
	}

	public AeLiteralValue(Node node) {
		super(node);
	}

	private Object mValue;
}
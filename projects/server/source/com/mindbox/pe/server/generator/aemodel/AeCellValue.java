package com.mindbox.pe.server.generator.aemodel;

import com.mindbox.server.parser.jtb.rule.syntaxtree.Node;

// Referenced classes of package com.mindbox.server.generator:
//            AbstractAeValue

public class AeCellValue extends AbstractAeValue {

	public String toString() {
		return "%cellValue%";
	}

	public AeCellValue(Node node) {
		super(node);
	}
}
/*
 * Created on Jul 2, 2004
 *
 */
package com.mindbox.pe.server.generator.processor;

import com.mindbox.pe.server.parser.jtb.message.visitor.TreeFormatter;


/**
 * @author Geneho
 * @since PowerEditor 3.2.0
 */
public class PEMessageTreeFormatter extends TreeFormatter {

	/**
	 * 
	 */
	public PEMessageTreeFormatter() {
		super(4, 0);
	}
	
	/*
	public void visit(CellValueLiteral n) {
		add(space());
		super.visit(n);
		add(space());
	}

	public void visit(ColumnLiteral n) {
		add(space());
		super.visit(n);
		add(space());
	}

	public void visit(Reference n) {
		add(space());
		super.visit(n);
		add(space());
	}

	public void visit(RuleNameLiteral n) {
		add(space());
		super.visit(n);
		add(space());
	}*/
}

/*
 * Created on 2004. 2. 27.
 *
 */
package com.mindbox.pe.server.generator.aeobjects;

import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;


/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public class AeTestFunctionPattern extends AbstractAeCondition {

	private final List<AbstractAeValue> paramList;
	private String functionName = null;

	/**
	 * @param node node
	 */
	public AeTestFunctionPattern(Node node) {
		super(node);
		paramList = new LinkedList<AbstractAeValue>();
	}

	public void addParam(AbstractAeValue value) {
		paramList.add(value);
	}

	public String getFunctionName() {
		return functionName;
	}

	public AbstractAeValue getParamAt(int index) {
		return paramList.get(index);
	}

	public void setFunctionName(String string) {
		functionName = string;
	}

	@Override
	public int size() {
		return paramList.size();
	}

	@Override
	public String toString() {
		return "aetest[" + functionName + "(size=" + paramList.size() + ")]";
	}
}

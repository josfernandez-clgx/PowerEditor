/*
 * Created on 2004. 1. 19.
 *
 */
package com.mindbox.pe.server.generator.aeobjects;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 1.2.0
 */
public class AeCreateSequenceValue extends AbstractAeValue {

	private final List<AbstractAeValue> valueList;
	
	/**
	 * @param node
	 */
	public AeCreateSequenceValue(Node node) {
		super(node);
		this.valueList = new LinkedList<AbstractAeValue>();
	}
	
	public void addValue(AbstractAeValue value) {
		this.valueList.add(value);
	}
	
	public void clearValues() {
		this.valueList.clear();
	}
	
	public AbstractAeValue[] getValues() {
		return valueList.toArray(new AbstractAeValue[0]);
	}
	
	public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("$create[");
		for (Iterator<AbstractAeValue> iter = valueList.iterator(); iter.hasNext();) {
			buff.append(iter.next());
			buff.append(" ");
		}
		buff.append("]");
		return buff.toString();
	}
}

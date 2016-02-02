package com.mindbox.pe.server.generator.aeobjects;

import java.util.LinkedList;
import java.util.List;

import com.mindbox.pe.server.parser.jtb.rule.syntaxtree.Node;


public class AeFormulaValue extends AbstractAeValue {

	public static final int PLUS = 1;
	public static final int MINUS = 2;
	public static final int MULTIPLY = 3;
	public static final int DIVIDE = 4;
	public static final int BLAH_BLAH = 5;
	
	private int operator;
	private final List<Object> argumentList;
	
	public AeFormulaValue(Node node) {
		super(node);
		argumentList = new LinkedList<Object>();
	}

	
	public List<Object> getArguments() {
		return argumentList;
	}

	public void addArgument(Object argument) {
		argumentList.add(argument);
	}
	
	public String getOperator() {
		switch (operator) {
			case PLUS: return "+";
			case MINUS: return "-";
			case MULTIPLY: return "*";
			case DIVIDE: return "/";
		}
		return "Invalid-OP-" + operator;
	}

	public void setOperator(int i) {
		operator = i;
	}

	public String toString() {
		return "AeFormula[args=" + argumentList.size() + ";op=" + operator + "]";
	}

}
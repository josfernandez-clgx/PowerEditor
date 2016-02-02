package com.mindbox.server.parser.jtb.rule.visitor;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;

import com.mindbox.server.parser.jtb.rule.syntaxtree.NodeToken;
//import java.util.List;

// Referenced classes of package com.mindbox.server.parser.jtb.rule.visitor:
//            DepthFirstVisitor

public class TreeDumper extends DepthFirstVisitor {

	public TreeDumper() {
		curLine = 1;
		curColumn = 1;
		startAtNextToken = false;
		printSpecials = true;
		out = new PrintWriter(System.out, true);
	}

	public TreeDumper(Writer writer) {
		curLine = 1;
		curColumn = 1;
		startAtNextToken = false;
		printSpecials = true;
		out = new PrintWriter(writer, true);
	}

	public TreeDumper(OutputStream outputstream) {
		curLine = 1;
		curColumn = 1;
		startAtNextToken = false;
		printSpecials = true;
		out = new PrintWriter(outputstream, true);
	}

	public void startAtNextToken() {
		startAtNextToken = true;
	}

	public void flushWriter() {
		out.flush();
	}

	public void printSpecials(boolean flag) {
		printSpecials = flag;
	}

	public void resetPosition() {
		curLine = curColumn = 1;
	}

	public void visit(NodeToken nodetoken) {
		if (nodetoken.beginLine == -1 || nodetoken.beginColumn == -1) {
			printToken(nodetoken.tokenImage);
			return;
		}
		if (printSpecials && nodetoken.numSpecials() > 0) {
			for (Iterator iter = nodetoken.specialTokens.iterator(); iter.hasNext(); visit((NodeToken) iter.next()));
		}
		if (startAtNextToken) {
			curLine = nodetoken.beginLine;
			curColumn = 1;
			startAtNextToken = false;
			if (nodetoken.beginColumn < curColumn)
				out.println();
		}
		if (nodetoken.beginLine < curLine)
			throw new IllegalStateException(
				"at token \""
					+ nodetoken.tokenImage
					+ "\", n.beginLine = "
					+ Integer.toString(nodetoken.beginLine)
					+ ", curLine = "
					+ Integer.toString(curLine));
		if (nodetoken.beginLine == curLine && nodetoken.beginColumn < curColumn)
			throw new IllegalStateException(
				"at token \""
					+ nodetoken.tokenImage
					+ "\", n.beginColumn = "
					+ Integer.toString(nodetoken.beginColumn)
					+ ", curColumn = "
					+ Integer.toString(curColumn));
		if (curLine < nodetoken.beginLine) {
			curColumn = 1;
			for (; curLine < nodetoken.beginLine; curLine++)
				out.println();

		}
		for (; curColumn < nodetoken.beginColumn; curColumn++)
			out.print(" ");

		printToken(nodetoken.tokenImage);
	}

	private void printToken(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '\n') {
				curLine++;
				curColumn = 1;
			}
			else {
				curColumn++;
			}
			out.print(s.charAt(i));
		}

		out.flush();
	}

	protected PrintWriter out;
	private int curLine;
	private int curColumn;
	private boolean startAtNextToken;
	private boolean printSpecials;
}
/*
 * Created on Sep 23, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.mindbox.pe.client.applet.validate;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.common.validate.WarningConsumer;
import com.mindbox.pe.common.validate.WarningInfo;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.CellValueLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.ColumnLiteral;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Message;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.NodeToken;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Reference;
import com.mindbox.pe.server.parser.jtb.message.visitor.ObjectDepthFirst;


/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since 
 */
final class MessageVisitorValidator {

	private static final MessageVisitorValidator INSTANCE = new MessageVisitorValidator();

	static MessageVisitorValidator getInstance() {
		return INSTANCE;
	}

	private static String toLocationInfo(NodeToken token) {
		return " (line: " + token.beginLine + ", column:" + token.beginColumn + ")";
	}

	private final MessageVisitor messageVisitor;
	private boolean isForColumn = false;
	private boolean isValid = true;
	private int columnCount = 0;

	private MessageVisitorValidator() {
		messageVisitor = new MessageVisitor();
	}

	public synchronized boolean validate(Message message, boolean isForColumn, int columnCount, WarningConsumer warningConsumer) {
		if (message == null) throw new NullPointerException("message cannot be null");
		if (warningConsumer == null) throw new NullPointerException("warning consumer cannot be null");

		this.isForColumn = isForColumn;
		this.columnCount = columnCount;
		this.isValid = true;
		message.accept(messageVisitor, warningConsumer);

		return isValid;
	}

	private class MessageVisitor extends ObjectDepthFirst {

		public Object visit(CellValueLiteral cellValueLiteral, Object arg1) {
			super.visit(cellValueLiteral, arg1);
			if (!isForColumn) {
				String msg = ClientUtil.getInstance().getMessage(
						"msg.warning.invalid.reference.cell",
						new Object[] { toLocationInfo(cellValueLiteral.f0)});

				((WarningConsumer) arg1).addWarning(WarningInfo.ERROR, msg);
				isValid = false;
			}
			return null;
		}

		public Object visit(ColumnLiteral columnLiteral, Object arg1) {
			super.visit(columnLiteral, arg1);
			String columnStr = columnLiteral.f1.tokenImage;
			try {
				int columnNo = Integer.parseInt(columnStr);
				if (columnNo < 1 || columnNo > columnCount) {
					String msg = ClientUtil.getInstance().getMessage(
							"msg.warning.invalid.reference.column.range",
							new Object[] { columnStr, String.valueOf(columnCount), toLocationInfo(columnLiteral.f2)});

					((WarningConsumer) arg1).addWarning(WarningInfo.ERROR, msg);
					isValid = false;
				}
			}
			catch (Exception ex) {
				String msg = ClientUtil.getInstance().getMessage(
						"msg.warning.invalid.reference.column.number",
						new Object[] { columnStr, String.valueOf(columnCount), toLocationInfo(columnLiteral.f2)});

				((WarningConsumer) arg1).addWarning(WarningInfo.ERROR, msg);
				isValid = false;
			}
			return null;
		}

		public Object visit(Reference reference, Object arg1) {
			super.visit(reference, arg1);
			// as of 4.1.0
			String imageStr = reference.f1.tokenImage;
			if (imageStr != null && imageStr.length() > 0) {
				String[] strs = imageStr.split("\\.");
				if (strs == null || strs.length == 0) return null;
				if (strs.length == 1) {
					if (DomainModel.getInstance().getDomainClass(strs[0]) == null) {
						String msg = ClientUtil.getInstance().getMessage(
								"msg.warning.invalid.reference.class",
								new Object[] { strs[0], toLocationInfo(reference.f1)});
						((WarningConsumer) arg1).addWarning(WarningInfo.ERROR, msg);
						isValid = false;

					}
				}
				else {
					DomainClass dc = null;
					// last one is a name of an attribute
					for (int i = 0; i < strs.length - 1; i++) {
						dc = DomainModel.getInstance().getDomainClass(strs[i]);
						if (dc == null) {
							String msg = ClientUtil.getInstance().getMessage(
									"msg.warning.invalid.reference.class",
									new Object[] { strs[i], toLocationInfo(reference.f1)});
							((WarningConsumer) arg1).addWarning(WarningInfo.ERROR, msg);
							isValid = false;
							return null;
						}
					}
					if (dc != null) {
						if (dc.getDomainAttribute(strs[strs.length - 1]) == null) {
							String msg = ClientUtil.getInstance().getMessage(
									"msg.warning.invalid.reference.attribute",
									new Object[] { strs[strs.length - 1], strs[strs.length - 2], toLocationInfo(reference.f1)});
							((WarningConsumer) arg1).addWarning(WarningInfo.ERROR, msg);
							isValid = false;
						}
					}
				}
			}
			/*
			 String className = reference.f1.tokenImage;
			 if (DomainModel.getInstance().getDomainClass(className) == null) {
			 String msg = ClientUtil.getInstance().getMessage(
			 "msg.warning.invalid.reference.class",
			 new Object[] { className, toLocationInfo(reference.f1)});
			 ((WarningConsumer) arg1).addWarning(WarningInfo.ERROR, msg);
			 isValid = false;
			 }
			 else {
			 // validate class.attribute reference only
			 if (reference.f2.present() && reference.f2.size() == 1) {
			 String attribName = ((NodeToken) ((NodeSequence) reference.f2.elementAt(0)).elementAt(1)).tokenImage;
			 if (DomainModel.getInstance().getDomainClass(className).getDomainAttribute(attribName) == null) {
			 String msg = ClientUtil.getInstance().getMessage(
			 "msg.warning.invalid.reference.attribute",
			 new Object[] { attribName, className,
			 toLocationInfo((NodeToken) ((NodeSequence) reference.f2.elementAt(0)).elementAt(1))});
			 ((WarningConsumer) arg1).addWarning(WarningInfo.ERROR, msg);
			 isValid = false;
			 }
			 }
			 }
			 */
			return null;
		}
	}
}
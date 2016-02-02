package com.mindbox.pe.server.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.table.CategoryOrEntityValue;
import com.mindbox.pe.server.Util;
import com.mindbox.pe.server.audit.AuditConstants;
import com.mindbox.pe.server.cache.DateSynonymManager;

public class WriteChangeDetailElementTag extends AbstractValueTag {

	private static final String CONTEXT_ELEMENT_NAME = "context-element";

	/**
	 * 
	 */
	private static final long serialVersionUID = -2685474263191580634L;

	private static String asChangeDetailElementName(int elementTypeID) {
		switch (elementTypeID) {
		case AuditConstants.KB_ELEMENT_TYPE_BEFORE_VALUE:
			return "previous-value";
		case AuditConstants.KB_ELEMENT_TYPE_AFTER_VALUE:
			return "new-value";
		case AuditConstants.KB_ELEMENT_TYPE_ROW_NUMBER:
			return "row-number";
		case AuditConstants.KB_ELEMENT_TYPE_COLUMN_NAME:
			return "column-name";
		case AuditConstants.KB_ELEMENT_TYPE_TEMPLATE_ID:
			return "template";
		case AuditConstants.KB_ELEMENT_TYPE_SOURCE_ID:
			return "source";
		case AuditConstants.KB_ELEMENT_TYPE_EFFECTIVE_DATESYNONYM_ID:
			return "effective-date";
		case AuditConstants.KB_ELEMENT_TYPE_EXPIRATION_DATESYNONYM_ID:
			return "expiration-date";
		case AuditConstants.KB_ELEMENT_TYPE_ENTITY_ID: {
			return CONTEXT_ELEMENT_NAME;
		}
		case AuditConstants.KB_ELEMENT_TYPE_CATEGORY_ID: {
			return CONTEXT_ELEMENT_NAME;
		}
		default:
			return "[Invalid element type id " + elementTypeID + "]";
		}
	}

	static void writeChangeDetailElement(JspWriter writer, int elementTypeId, String detailString) throws IOException {
		String elementName = asChangeDetailElementName(elementTypeId);
		writer.print("<");
		writer.print(elementName);
		writer.print(">");
		switch (elementTypeId) {
		case AuditConstants.KB_ELEMENT_TYPE_ENTITY_ID:
		case AuditConstants.KB_ELEMENT_TYPE_CATEGORY_ID: {
			writer.print(WriteContextTag.toContextTagValue(CategoryOrEntityValue.valueOf(detailString)));
			break;
		}
		case AuditConstants.KB_ELEMENT_TYPE_EFFECTIVE_DATESYNONYM_ID:
		case AuditConstants.KB_ELEMENT_TYPE_EXPIRATION_DATESYNONYM_ID: {
			try {
				int dsID = Integer.parseInt(detailString);
				DateSynonym ds = DateSynonymManager.getInstance().getDateSynonym(dsID);
				writer.print((ds == null ? "Date Synonym " + dsID : ds.getName()));
			}
			catch (Exception ex) {
				writer.print("Date Synonym ");
				writer.print(Util.xmlify(detailString));
			}
			break;
		}
		default:
			writer.print(Util.xmlify(detailString));
		}
		writer.print("</");
		writer.print(elementName);
		writer.println(">");
	}

	private int elementTypeId;

	public int getElementTypeId() {
		return elementTypeId;
	}

	public void setElementTypeId(int elementTypeId) {
		this.elementTypeId = elementTypeId;
	}

	public void setElementTypeId(Integer elementTypeId) {
		if (elementTypeId != null) {
			this.elementTypeId = elementTypeId.intValue();
		}
	}

	public int doStartTag() throws JspException {
		if (elementTypeId == 0) throw new JspException("elementTypeId is required");
		if (value == null) return SKIP_BODY;
		if (!(value instanceof String)) {
			throw new JspException("value must be an instance of " + String.class.getName());
		}
		String detailString = (String) value;
		try {
			JspWriter writer = pageContext.getOut();
			writeChangeDetailElement(writer, elementTypeId, detailString);
		}
		catch (IOException ex) {
			throw new JspException(ex);
		}
		return SKIP_BODY;
	}
}

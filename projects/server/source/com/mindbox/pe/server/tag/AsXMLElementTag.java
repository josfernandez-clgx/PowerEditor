package com.mindbox.pe.server.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import com.mindbox.pe.server.report.ReportGenerator;

/**
 * Implementation of &lt;as-xml-element&gt; PowerEditor custom tag.
 * <p>
 * <b>Required Parameters</b><ul>
 * <li><code>value</code> - the value to be output as a valid XML element name</li>
 * </ul>
 * @author Geneho Kim
 * @since PowerEditor 
 */
public class AsXMLElementTag extends AbstractValueTag {

	private static final long serialVersionUID = -4799318067721560041L;

	public int doStartTag() throws JspException {
		Object obj = getValueObject();
		try {
			String elementName = ReportGenerator.toElementName((obj instanceof String ? (String) obj : obj.toString()));
			pageContext.getOut().print(elementName);
		}
		catch (IOException e) {
			throw new JspException(e);
		}
		return SKIP_BODY;
	}

}

package com.mindbox.pe.server.tag;

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.JspException;

import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.model.DateSynonym;

/**
 * Implementation of &lt;as-xml-element&gt; PowerEditor custom tag.
 * <p>
 * <b>Required Parameters</b><ul>
 * <li><code>value</code> - the value to be output as a valid XML element name</li>
 * </ul>
 * @author Geneho Kim
 * @since PowerEditor 
 */
public class AsXMLDateTag extends AbstractValueTag {

	private static final long serialVersionUID = -4799318067721560041L;

	public int doStartTag() throws JspException {
		Object obj = getValueObject();
		try {
			Date date = null;
			if (obj instanceof Date) {
				date =(Date) obj;
			}
			else if (obj instanceof DateSynonym) {
				date = ((DateSynonym)obj).getDate();
			}
			else {
				try {
					date = UIConfiguration.FORMAT_DATE_TIME_SEC.parse(obj.toString());
				}
				catch (Exception ex) {
					date = ConfigUtil.toDate(obj.toString());
				}
			}
			pageContext.getOut().print(ConfigUtil.toDateXMLString(date));
		}
		catch (IOException e) {
			throw new JspException(e);
		}
		return SKIP_BODY;
	}

}

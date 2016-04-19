package com.mindbox.pe.server.tag;

import javax.servlet.jsp.JspException;

import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.xsd.config.CategoryType;

/**
 * Implementation of &lt;category-type&gt; PowerEditor custom tag.
 * <p>
 * <b>Required Parameters</b><ul>
 * <li><code>var</code> - Name of the attribute to be bound to an instance of {@link CategoryTypeDefinition}</li>
 * </ul>
 */
public class CategoryTypeDefinitionTag extends AbstractVarTag {

	private static final long serialVersionUID = 3038861095417400117L;

	static CategoryType getCategoryTypeDefinition(int categoryTypeId) {
		return ConfigurationManager.getInstance().getEntityConfigHelper().findCategoryTypeDefinition(categoryTypeId);
	}

	private int categoryTypeId;

	public int getCategoryTypeId() {
		return categoryTypeId;
	}

	public void setCategoryTypeId(int categoryTypeId) {
		this.categoryTypeId = categoryTypeId;
	}

	public int doStartTag() throws JspException {
		if (categoryTypeId == 0) throw new JspException("categoryTypeId is required");
		setVarObject(getCategoryTypeDefinition(categoryTypeId));
		return SKIP_BODY;
	}
}

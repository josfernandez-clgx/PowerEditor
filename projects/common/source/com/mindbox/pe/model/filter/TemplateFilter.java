package com.mindbox.pe.model.filter;

import com.mindbox.pe.model.EntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.TemplateUsageType;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 1.0
 */
public final class TemplateFilter extends AbstractSearchFilter<GridTemplate> {

	private static final long serialVersionUID = 4817909570437588820L;

	private final TemplateUsageType usageType;
	private String className, attributeName;
	private boolean skipSecurityCheck = false;
	private int templateID;

	public TemplateFilter(TemplateUsageType usageType) {
		super(EntityType.TEMPLATE);
		this.usageType = usageType;
	}

	/**
	 * @return Returns the skipSecurityCheck.
	 */
	public boolean isSkipSecurityCheck() {
		return skipSecurityCheck;
	}

	/**
	 * @param skipSecurityCheck The skipSecurityCheck to set.
	 */
	public void setSkipSecurityCheck(boolean skipSecurityCheck) {
		this.skipSecurityCheck = skipSecurityCheck;
	}

	public boolean isAcceptable(GridTemplate object) {
		if (templateID > 0) {
			return templateID == object.getID();
		}
		if (usageType != null) {
			return usageType == object.getUsageType();
		}
		else {
			return true;
		}
	}

	public String getAttributeName() {
		return attributeName;
	}

	public String getClassName() {
		return className;
	}

	public void setAttributeName(String string) {
		attributeName = string;
	}

	public void setClassName(String string) {
		className = string;
	}

	public int getTemplateID() {
		return templateID;
	}

	public void setTemplateID(int templateID) {
		this.templateID = templateID;
	}


}
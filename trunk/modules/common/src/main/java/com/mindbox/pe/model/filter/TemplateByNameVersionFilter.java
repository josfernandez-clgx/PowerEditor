package com.mindbox.pe.model.filter;

import com.mindbox.pe.model.PeDataType;
import com.mindbox.pe.model.template.GridTemplate;

/**
 * 
 * @author Gene Kim
 * @author MindBox, Inc
 * @since PowerEditor 5.4
 */
public final class TemplateByNameVersionFilter extends AbstractSearchFilter<GridTemplate> {

	private static final long serialVersionUID = 4817909570437588820L;

	private String templateName, templateVersion;

	public TemplateByNameVersionFilter(String templateName, String templateVersion) {
		super(PeDataType.TEMPLATE);
		this.templateName = templateName;
		this.templateVersion = templateVersion;
	}

	public String getTemplateName() {
		return templateName;
	}

	public String getTemplateVersion() {
		return templateVersion;
	}

	public boolean isAcceptable(GridTemplate template) {
		if (template == null) return false;
		if (templateName != null && !templateName.equals(template.getName())) {
			return false;
		}
		if (templateVersion != null && !templateVersion.equals(template.getVersion())) {
			return false;
		}
		else {
			return true;
		}
	}
}
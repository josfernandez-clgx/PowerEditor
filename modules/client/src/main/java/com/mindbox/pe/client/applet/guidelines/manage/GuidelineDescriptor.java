package com.mindbox.pe.client.applet.guidelines.manage;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.TemplateUsageType;

public class GuidelineDescriptor {

	public String getDisplayResource() {
		return guidelineDisplayString;
	}

	public String getEditPermissionReqd() {
		return editPermissionReqd;
	}

	public String toString() {
		return guidelineDisplayString;
	}


	public GuidelineDescriptor(String editPermission, String viewPermission, TemplateUsageType usageType, String guidelineDisplayName) {
		this.editPermissionReqd = editPermission;
		this.viewPermissionReqd = viewPermission;
		this.guidelineType = usageType;
		this.guidelineDisplayString = guidelineDisplayName;
	}

	public GuidelineDescriptor(TemplateUsageType usageType, String guidelineDisplayName) {
		this(
				UtilBase.getRequiredPermission(usageType, false),
				UtilBase.getRequiredPermission(usageType, true),
				usageType,
				guidelineDisplayName);
	}

	public String getViewPermissionReqd() {
		return viewPermissionReqd;
	}

	public TemplateUsageType getGuidelineType() {
		return guidelineType;
	}

	private String editPermissionReqd;
	private String viewPermissionReqd;
	private TemplateUsageType guidelineType;
	private String guidelineDisplayString;
}

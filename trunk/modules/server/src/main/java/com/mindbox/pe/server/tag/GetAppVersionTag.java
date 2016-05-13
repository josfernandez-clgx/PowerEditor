package com.mindbox.pe.server.tag;

import com.mindbox.pe.server.config.ConfigurationManager;


public class GetAppVersionTag extends AbstractVarTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2452608726646334394L;

	private boolean appendBuild = false;

	@Override
	protected Object getVarObject() {
		return appendBuild
				? String.format("%s (%s)", ConfigurationManager.getInstance().getAppVersion(), ConfigurationManager.getInstance().getAppBuild())
				: ConfigurationManager.getInstance().getAppVersion();
	}

	public final boolean isAppendBuild() {
		return appendBuild;
	}

	public final void setAppendBuild(boolean appendBuild) {
		this.appendBuild = appendBuild;
	}
}

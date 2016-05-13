/*
 * Created on 2004. 12. 22.
 *
 */
package com.mindbox.pe.client;

import java.awt.Cursor;

import com.mindbox.pe.client.common.AbstractClientGeneratedRuntimeException;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.UserProfile;
import com.mindbox.pe.model.exceptions.CanceledException;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.xsd.config.GuidelineTab;


/**
 * Main Application.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public interface MainApplication {

	/**
	 * Given the privilege name, this returns true if that permission exists
	 * for the role the current user has logged in as
	 * @param s String
	 * @return boolean
	 * @since 5.0.0 (replaces checkPermission(String str))
	 */
	boolean checkPermissionByPrivilegeName(String s);


	/**
	 * Extracts all UsageTypes of a GuidelineTab and returns 'true' if view
	 * guideline privilege exists OR if edit guideline privilege exists 
	 * on any one of the UsageType's belonging to this GuidelineTab
	 * @param gtConfig GuidelineTab
	 * @return boolean
	 * @since 5.0.0
	 */
	boolean checkViewOrEditGuidelinePermission(GuidelineTab gtConfig);


	/**
	 * Returns 'true' if view guideline privilege exists OR if edit guideline privilege exists 
	 * on a specific UsageType
	 * @param usageType TemplateUsageType
	 * @return boolean
	 * @since 5.0.0
	 */
	boolean checkViewOrEditGuidelinePermissionOnUsageType(TemplateUsageType usageType);


	/**
	 * Extracts all UsageTypes of a GuidelineTab and returns 'true' if view
	 * template privilege exists OR if edit template privilege exists 
	 * on any one of the UsageType's belonging to this GuidelineTab
	 * @param gtConfig GuidelineTab
	 * @return boolean
	 * @since 5.0.0
	 */
	boolean checkViewOrEditTemplatePermission(GuidelineTab gtConfig);


	/**
	 * Returns 'true' if view template privilege exists OR if edit template privilege exists 
	 * on a specific UsageType
	 * @param usageType TemplateUsageType
	 * @return boolean
	 * @since 5.0.0
	 */
	boolean checkViewOrEditTemplatePermissionOnUsageType(TemplateUsageType usageType);

	boolean confirmExit();

	void dispose();

	Communicator getCommunicator();

	String getSessionID();

	String getUserID();

	UserProfile getUserSession();

	/**
	 * Processes the specified exception and present appropriate dialogs to the user.
	 * This should treat {@link ServerException} and {@link AbstractClientGeneratedRuntimeException} specially.
	 * @param ex the exception
	 * @see ServerException
	 * @see AbstractClientGeneratedRuntimeException
	 */
	void handleRuntimeException(Exception ex);

	void reloadTemplates() throws ServerException;

	void setCursor(Cursor cursor);

	void setStatusMsg(String msg);

	void showTemplateEditPanel(GridTemplate template) throws CanceledException;
}
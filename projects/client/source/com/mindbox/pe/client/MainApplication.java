/*
 * Created on 2004. 12. 22.
 *
 */
package com.mindbox.pe.client;

import java.awt.Cursor;

import com.mindbox.pe.client.common.AbstractClientGeneratedRuntimeException;
import com.mindbox.pe.common.config.EntityConfiguration;
import com.mindbox.pe.common.config.GuidelineTabConfig;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.UserProfile;
import com.mindbox.pe.model.exceptions.CanceledException;


/**
 * Main Application.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public interface MainApplication {
	
	/**
	 * Given the privilege name, this returns true if that permission exists
	 * for the role the current user has logged in as
	 * @param String
	 * @return boolean
	 * @since 5.0.0 (replaces checkPermission(String str))
	 */
	boolean checkPermissionByPrivilegeName(String s);
	
	
	/**
	 * Extracts all UsageTypes of a GuidelineTab and returns 'true' if view
	 * guideline privilege exists OR if edit guideline privilege exists 
	 * on any one of the UsageType's belonging to this GuidelineTab
	 * @param GuidelineTabConfig
	 * @return boolean
	 * @since 5.0.0
	 */
	boolean checkViewOrEditGuidelinePermission(GuidelineTabConfig gtConfig);	
	
	
	/**
	 * Returns 'true' if view guideline privilege exists OR if edit guideline privilege exists 
	 * on a specific UsageType
	 * @param TemplateUsageType
	 * @return boolean
	 * @since 5.0.0
	 */
	boolean checkViewOrEditGuidelinePermissionOnUsageType(TemplateUsageType usageType);
	
	
	/**
	 * Extracts all UsageTypes of a GuidelineTab and returns 'true' if view
	 * template privilege exists OR if edit template privilege exists 
	 * on any one of the UsageType's belonging to this GuidelineTab
	 * @param GuidelineTabConfig
	 * @return boolean
	 * @since 5.0.0
	 */
	boolean checkViewOrEditTemplatePermission(GuidelineTabConfig gtConfig);
	
	
	/**
	 * Returns 'true' if view template privilege exists OR if edit template privilege exists 
	 * on a specific UsageType
	 * @param TemplateUsageType
	 * @return boolean
	 * @since 5.0.0
	 */
	boolean checkViewOrEditTemplatePermissionOnUsageType(TemplateUsageType usageType);

	/**
	 * Processes the specified exception and present appropriate dialogs to the user.
	 * This should treat {@link ServerException} and {@link AbstractClientGeneratedRuntimeException} specially.
	 * @param ex the exception
	 * @see ServerException
	 * @see AbstractClientGeneratedRuntimeException
	 */
	void handleRuntimeException(Exception ex);

	boolean confirmExit();
	
	void dispose();

	void showTemplateEditPanel(GridTemplate template) throws CanceledException;
	
	Communicator getCommunicator();
	
	String getUserID();
	String getSessionID();
	UserProfile getUserSession();
	EntityConfiguration getEntityConfiguration();
	
	void setCursor(Cursor cursor);
	void setStatusMsg(String msg);
	
	void reloadTemplates() throws ServerException;
}
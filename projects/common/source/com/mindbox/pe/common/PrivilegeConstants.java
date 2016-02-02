/*
 * Created on 2004. 2. 26.
 *
 */
package com.mindbox.pe.common;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.4.0
 */
public class PrivilegeConstants {

	public static final String PRIV_ACCESS_WEBSERVICE = "AccessWebService";
	public static final String PRIV_MANAGE_DATE_SYNONYM = "ManageDateSynonym";
	public static final String PRIV_MANAGE_REQUEST_TYPE = "ManageRequestType";
	public static final String PRIV_MANAGE_PHASE = "ManagePhase";
	public static final String PRIV_MANAGE_CBR = "ManageCBR";

	public static final String PRIV_MANAGE_PARAMETERS = "ManageParameters";

	// Pre-5.0 hard coded manage templates privilege constant
	//Now used only for upgrading to 5.0.x from 4.5.x
	public static final String PRIV_MANAGE_TEMPLATES = "ManageTemplates";

	public static final String PRIV_MANAGE_GUIDELINE_ACTIONS = "ManageGuidelineActions";

	public static final String PRIV_MANAGE_USERS = "ManageUsers";
	public static final String PRIV_MANAGE_LOCKS = "ManageLocks";
	public static final String PRIV_MANAGE_CONFIGURATION = "ManageConfiguration";
	public static final String PRIV_DEPLOY = "Deploy";
	public static final String PRIV_EXPORT_DATA = "ExportData";

	// Pre-5.0 hard coded generic entity type privilege constants
	// Now they are used for upgrading to 5.0.x from 4.5.x
	public static final String PRIV_EDIT_ENTITIES = "EditEntityData";
	public static final String PRIV_VIEW_ENTITIES = "ViewEntityData";
	public static final String PRIV_VIEW_REPORT = "ViewReport";

	public static final String PRIV_EDIT_PRODUCTION_DATA = "EditProductionData";

	// These go into MB_Privilege table. 
	public static final int HARD_CODED_PRIV = 0;
	public static final int ENTITY_TYPE_PRIV = 1; // Constructed from EntityType in the config file
	public static final int USAGE_TYPE_PRIV = 2; // Constructed from UsageType in the config file

	// prefix-suffix strings that are used during constructing privileges from config file
	public static final String VIEW_PRIV_NAME_PREFIX = "View";
	public static final String VIEW_PRIV_DISPLAY_NAME_PREFIX = "View ";

	public static final String EDIT_PRIV_NAME_PREFIX = "Edit";
	public static final String EDIT_PRIV_DISPLAY_NAME_PREFIX = "Edit ";

	public static final String VIEW_AND_EDIT_ENTITY_PRIV_DISPLAY_NAME_SUFFIX = " Data";

	// For backwards compatability, we ar not adding 'Guidelines' to name of this priv.
	//public static final String VIEW_AND_EDIT_USAGE_GUIDELINE_PRIV_nameSuffix = "Guidelines";
	public static final String VIEW_AND_EDIT_USAGE_GUIDELINE_PRIV_DISPLAY_NAME_SUFFIX = " Guidelines";

	public static final String VIEW_AND_EDIT_USAGE_TEMPLATE_PRIV_NAME_SUFFIX = "Templates";
	public static final String VIEW_AND_EDIT_USAGE_TEMPLATE_PRIV_DISPLAY_NAME_SUFFIX = " Templates";

	public static final String DEPLOY_PRIV_NAME_PREFIX = "DeployInStatus";

	private PrivilegeConstants() {
	}
}

package com.mindbox.pe.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.mindbox.pe.client.applet.PowerEditorLoggedApplet;
import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.admin.AdminTab;
import com.mindbox.pe.client.applet.admin.ProcessManagementTab;
import com.mindbox.pe.client.applet.admin.role.RoleTabPanel;
import com.mindbox.pe.client.applet.admin.user.UserTabPanel;
import com.mindbox.pe.client.applet.cbr.CBRPanel;
import com.mindbox.pe.client.applet.datesynonym.ManageDateSynonymTab;
import com.mindbox.pe.client.applet.entities.EntitiesTab;
import com.mindbox.pe.client.applet.guidelines.GuidelinesTab;
import com.mindbox.pe.client.applet.policy.PolicyTab;
import com.mindbox.pe.client.applet.report.ReportTab;
import com.mindbox.pe.client.applet.template.TemplateManagementTab;
import com.mindbox.pe.client.applet.template.rule.ActionEditDialog;
import com.mindbox.pe.client.applet.template.rule.TestConditionEditDialog;
import com.mindbox.pe.client.applet.validate.DomainModel;
import com.mindbox.pe.client.common.AbstractClientGeneratedRuntimeException;
import com.mindbox.pe.client.common.tab.PowerEditorTab;
import com.mindbox.pe.client.common.tab.PowerEditorTabPanel;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.EntityConfiguration;
import com.mindbox.pe.common.config.EntityTabConfig;
import com.mindbox.pe.common.config.FeatureConfiguration;
import com.mindbox.pe.common.config.GuidelineTabConfig;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.UserProfile;
import com.mindbox.pe.model.admin.Privilege;
import com.mindbox.pe.model.exceptions.CanceledException;

/**
 * The main panel.
 * This is the main panel for the application, displayed after a successful login.
 * This holds references to all objects specific to a particular session.
 * @since PowerEditor 4.2.0
 */
public final class MainPanel extends JPanel implements MainApplication {

	private final class TabbedSelectionPanel extends JPanel {

		private final PowerEditorTab tabbedPane;
		private final TemplateManagementTab templateManagementTab;

		public TabbedSelectionPanel() throws ServerException {
			this.tabbedPane = new PowerEditorTab();
			this.tabbedPane.setTabPlacement(SwingConstants.TOP);
			this.tabbedPane.setFont(PowerEditorSwingTheme.bigTabFont);
			this.tabbedPane.setFocusable(false);
			this.tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
			this.templateManagementTab = new TemplateManagementTab(MainPanel.this.readOnly);

			setLayout(new GridLayout(1, 1, 0, 0));
			add(this.tabbedPane);

			addTabs();
		}

		private void selectTemplateManagementTab() {
			this.tabbedPane.setSelectedIndex((entitiesTab == null ? 0 : 1));
			PolicyTab tmp = (PolicyTab) this.tabbedPane.getSelectedComponent();
			Component tmp2 = tmp.getComponentAt(1);
			tmp.setSelectedComponent(tmp2);
			((GuidelinesTab) tmp2).setSelectedIndex(0);
		}

		private void addTab(String tabLabel, String iconName, JComponent comp, String tooltipName) {
			tabbedPane.addTab(tabLabel, ClientUtil.getInstance().makeImageIcon(iconName), comp, tooltipName);
		}

		private void addTabs() throws ServerException {

			if (entitiesTab != null) {
				addTab(ClientUtil.getInstance().getLabel("tab.entity"), "image.blank.tab", entitiesTab, ClientUtil.getInstance().getLabel(
						"tab.tooltip.entity"));
			}

			addTab(ClientUtil.getInstance().getLabel("tab.policies"), "image.blank.tab", new PolicyTab(
					guidelineTabConfigs,
					templateManagementTab,
					readOnly), ClientUtil.getInstance().getLabel("tab.tooltip.policies"));

			if (checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_VIEW_REPORT)) {
				addTab(
						ClientUtil.getInstance().getLabel("tab.reports"),
						"image.blank.tab",
						new ReportTab(),
						ClientUtil.getInstance().getLabel("tab.tooltip.report"));
			}

			if (checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_MANAGE_DATE_SYNONYM)) {
				ManageDateSynonymTab dsTab = new ManageDateSynonymTab(readOnly);
				addTab(ClientUtil.getInstance().getLabel("tab.date.synonym"), "image.blank.tab", dsTab, ClientUtil.getInstance().getLabel(
						"tab.tooltip.date.synonym"));
				tabbedPane.addChangeListener(dsTab);
			}

			if (ClientUtil.getUserSession().getFeatureConfiguration().isFeatureEnabled(FeatureConfiguration.CBR_FEATURE)) {
				if (checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_MANAGE_CBR)) {
					addTab(
							ClientUtil.getInstance().getLabel("tab.cbr"),
							"image.blank.tab",
							CBRPanel.getInstance(),
							ClientUtil.getInstance().getLabel("tab.tooltip.cbr"));
					tabbedPane.addChangeListener(CBRPanel.getInstance());
				}
			}

			if (ClientUtil.getUserSession().getFeatureConfiguration().isFeatureEnabled(FeatureConfiguration.PHASE_FEATURE)) {
				if (checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_MANAGE_REQUEST_TYPE)
						|| checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_MANAGE_PHASE)) {
					addTab(ClientUtil.getInstance().getLabel("tab.process.manage"), "image.blank.tab", new ProcessManagementTab(
							MainPanel.this.readOnly), ClientUtil.getInstance().getLabel("tab.tooltip.process.manage"));
				}
			}

			if (checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_MANAGE_USERS)
					|| checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_DEPLOY)
					|| checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_EXPORT_DATA)
					|| checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_MANAGE_LOCKS)) {
				addTab(ClientUtil.getInstance().getLabel("tab.admin"), "image.blank.tab", adminTab, ClientUtil.getInstance().getLabel(
						"tab.tooltip.admin"));
			}

			this.tabbedPane.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent changeevent) {
					if (tabbedPane.getSelectedComponent() == adminTab && adminTab.getTabCount() == 1) {
						adminTab.loadCurrentPanel();
					}
				}
			});

		}
	}

	private PowerEditorLoggedApplet parentApplet = null;
	private JTabbedPane entitiesTab;
	private JComponent[] guidelineTabs;
	private AdminTab adminTab;
	private boolean isUserLoggedIn;
	private final String sessionID;
	private final String userID;
	private final UserProfile userSession;
	private final GuidelineTabConfig[] guidelineTabConfigs;
	private final EntityConfiguration entityConfiguration;

	private Communicator communicator = null;
	private TabbedSelectionPanel mainTabPanel = null;
	private final boolean readOnly;

	/**
	 * Constructs a new main frame with the specified session and user profile information.
	 * @param applet
	 * @param sessionID
	 * @param userProfile
	 * @throws ServerException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public MainPanel(PowerEditorLoggedApplet applet, String sessionID, EntityConfiguration entityConfiguration, UserProfile userProfile,
			boolean readOnly) throws ServerException, IOException, ClassNotFoundException {
		super();
		setName(ClientUtil.getInstance().getLabel("app.title", new Object[] { userProfile.getUserID() }));
		this.entityConfiguration = entityConfiguration;
		parentApplet = applet;
		entitiesTab = null;
		isUserLoggedIn = true;
		this.readOnly = readOnly;

		this.sessionID = sessionID;
		userID = userProfile.getUserID();
		this.userSession = userProfile;
		this.guidelineTabConfigs = userProfile.getGuidelineConfigurations();

		// set global settings
		communicator = new DefaultCommunicator(this);

		ClientUtil.getInstance().setParent(this);
		EntityModelCacheFactory.getInstance().reloadCache();

		initPanel();

		System.out.format("Created MainPanel with %b%n", readOnly);
	}

	public void initPanel() throws ServerException, IOException, ClassNotFoundException {
		initBase();

		// initialize domain model -- do this before creating tabs
		DomainModel.initInstance();

		createTabs();
	}

	public void showTemplateEditPanel(GridTemplate template) throws CanceledException {
		this.mainTabPanel.templateManagementTab.editTemplate(template);
		this.mainTabPanel.selectTemplateManagementTab();
	}

	public String getSessionID() {
		return sessionID;
	}

	public void handleRuntimeException(Exception ex) {
		ClientUtil.getLogger().warn("Handling Exception", ex);
		if (ex instanceof ServerException) {
			ClientUtil.getInstance().showErrorDialog((ServerException) ex);
		}
		else if (ex instanceof AbstractClientGeneratedRuntimeException) {
			ClientUtil.getInstance().showErrorMessage(ex.getMessage());
		}
		else {
			ClientUtil.getInstance().showErrorDialog("ClientErrorMsg", new Object[] { ex.getMessage(), ex.getClass().getName() });
		}
	}

	private void initBase() {
		Dimension dimension = getToolkit().getScreenSize();
		setSize(dimension.width - 28, dimension.height - 28);

		setLayout(new BorderLayout(2, 2));
		addKeyListener(new KeyAdapter() {
		});
		addMouseListener(new MouseAdapter() {
		});
		setCursor(Cursor.getPredefinedCursor(3));
	}

	public Communicator getCommunicator() {
		if (communicator == null) {
			communicator = new DefaultCommunicator(this);
		}
		return communicator;
	}

	private boolean processUnsavedChangesIfAny() {
		PowerEditorTabPanel peTabPanel = this.mainTabPanel.tabbedPane.getSelectedPowerEditorTabPanel();
		if (peTabPanel != null) {
			boolean hasChanges = peTabPanel.hasUnsavedChanges();
			if (hasChanges) {
				Boolean result = ClientUtil.getInstance().showSaveDiscardCancelDialog();
				if (result == null) {
					return false;
				}
				else if (result.booleanValue()) {
					try {
						peTabPanel.saveChanges();
						return true;
					}
					catch (CanceledException e) {
						return false;
					}
					catch (Exception ex) {
						ClientUtil.handleRuntimeException(ex);
						return false;
					}
				}
				else {
					peTabPanel.discardChanges();
					return true;
				}
			}
		}
		return true;
	}

	public boolean confirmExit() {
		if (processUnsavedChangesIfAny()) {
			return ClientUtil.getInstance().showConfirmation("msg.question.close.applet");
		}
		else {
			return false;
		}
	}

	public String getUserID() {
		return userID;
	}

	public boolean checkPermissionByPrivilegeName(String privName) {
		boolean returnVal = false;
		Set<Privilege> privSet = userSession.getPrivileges();
		for (Iterator<Privilege> iter1 = privSet.iterator(); iter1.hasNext();) {
			Privilege priv = iter1.next();
			if (priv.getName().equals(privName)) {
				returnVal = true;
				break;
			}
		}
		return returnVal;
	}


	public boolean checkViewOrEditGuidelinePermission(GuidelineTabConfig gtConfig) {
		TemplateUsageType[] usageTypes = gtConfig.getUsageTypes();
		for (int i = 0; i < usageTypes.length; i++) {
			TemplateUsageType usageType = usageTypes[i];
			if (checkViewOrEditGuidelinePermissionOnUsageType(usageType)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkViewOrEditGuidelinePermissionOnUsageType(TemplateUsageType usageType) {
		GuidelineTabConfig tabConfig = findTabConfigFor(usageType);
		if (tabConfig != null) {
			String viewGuidelinePrivilege_Name = UtilBase.constructViewGuidelinePrivilege_Name(usageType.getPrivilege());
			String editGuidelinePrivilege_Name = UtilBase.constructEditGuidelinePrivilege_Name(usageType.getPrivilege());
			if (checkPermissionByPrivilegeName(viewGuidelinePrivilege_Name) || checkPermissionByPrivilegeName(editGuidelinePrivilege_Name)) {
				return true;
			}
			else {
				return false;
			}
		}
		return false;
	}

	public boolean checkViewOrEditTemplatePermission(GuidelineTabConfig gtConfig) {
		TemplateUsageType[] usageTypes = gtConfig.getUsageTypes();
		for (int i = 0; i < usageTypes.length; i++) {
			TemplateUsageType usageType = usageTypes[i];
			if (checkViewOrEditTemplatePermissionOnUsageType(usageType)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkViewOrEditTemplatePermissionOnUsageType(TemplateUsageType usageType) {
		GuidelineTabConfig tabConfig = findTabConfigFor(usageType);
		if (tabConfig != null) {
			String viewTemplatesPrivilege_Name = UtilBase.constructViewTemplatesPrivilege_Name(usageType.getPrivilege());
			String editTemplatesPrivilege_Name = UtilBase.constructEditTemplatesPrivilege_Name(usageType.getPrivilege());
			if (checkPermissionByPrivilegeName(viewTemplatesPrivilege_Name) || checkPermissionByPrivilegeName(editTemplatesPrivilege_Name)) {
				return true;
			}
			else {
				return false;
			}
		}
		return false;
	}

	/**
	 * This ensures that the UsageType for which we are checking some privilege (edit/view, guideline/template)
	 * exists in some GuideineTab. if not, then it is supposed to be hidden, hence its privileges dont matter
	 * @param usage
	 * @return value or null
	 */
	private GuidelineTabConfig findTabConfigFor(TemplateUsageType usage) {
		GuidelineTabConfig[] configs = userSession.getGuidelineConfigurations();
		for (int i = 0; i < configs.length; i++) {
			if (configs[i].containsUsageType(usage)) {
				return configs[i];
			}
		}
		return null;
	}


	public void setStatusMsg(String s) {
		parentApplet.showStatus(s);
	}

	public UserProfile getUserSession() {
		return userSession;
	}

	public void dispose() {
		logout();
	}

	/**
	 *  Log out the current user.
	 *
	 */
	public void logout() {
		if (!isUserLoggedIn) return;
		try {
			getCommunicator().logout();
		}
		catch (ServerException ex) {
			ClientUtil.getLogger().error("Failed to terminate session", ex);
		}

		try {
			// clear invariants - make 'em available for garbage collection
			entitiesTab = null;

			for (int i = 0; i < guidelineTabs.length; i++) {
				guidelineTabs[i] = null;
			}

			adminTab = null;

			UserTabPanel.reset();
			RoleTabPanel.reset();
			ManageDateSynonymTab.reset();
			CBRPanel.reset();

			// clear cache
			ClientUtil.clearCache();

			// notify the applet for logoff
			parentApplet.logoff();
			ClientUtil.getInstance().setParent(null);
			parentApplet = null;
		}
		catch (Exception ex) {
			ClientUtil.getLogger().error("Failed to logoff", ex);
			ex.printStackTrace();
		}
		isUserLoggedIn = false;
	}

	private void createTabs() throws ServerException {
		JPanel jpanel = UIFactory.createJPanel(new BorderLayout());

		// TODO Kim: make UI read-only 

		// iff entity tab is enabled, show it
		if (hasAtLeastOneEntityTab() && hasAtLeastOneEntityPrivilege()) {
			entitiesTab = new EntitiesTab(readOnly);
		}

		adminTab = new AdminTab(readOnly);
		CBRPanel.getNewInstance(readOnly);

		this.guidelineTabs = new JComponent[guidelineTabConfigs.length];

		this.mainTabPanel = new TabbedSelectionPanel();
		jpanel.add(mainTabPanel, BorderLayout.CENTER);
		add(jpanel);
	}

	/**
	 * Returns true if user has atleast one entity type privilege. 
	 * Either view or rdit
	 * @return boolean
	 */
	private boolean hasAtLeastOneEntityPrivilege() {
		Set<Privilege> privSet = ClientUtil.getUserSession().getPrivileges();
		boolean result = false;
		for (Iterator<Privilege> iter1 = privSet.iterator(); iter1.hasNext();) {
			Privilege priv = iter1.next();
			if (priv.getPrivilegeType() == PrivilegeConstants.ENTITY_TYPE_PRIV) {
				result = true;
				break;
			}
		}
		return result;
	}

	private boolean hasAtLeastOneEntityTab() {
		Map<GenericEntityType, EntityTabConfig> entityTabMap = userSession.getEntityTabConfigMap();
		for (Iterator<GenericEntityType> iter = entityTabMap.keySet().iterator(); iter.hasNext();) {
			GenericEntityType key = iter.next();
			EntityTabConfig tabConfig = entityTabMap.get(key);
			if (tabConfig != null && tabConfig.isVisible()) {
				return true;
			}
		}
		return false;
	}

	public void reloadTemplates() throws ServerException {
		this.mainTabPanel.templateManagementTab.reloadTemplates();
		TestConditionEditDialog.resetTypeList();
		ActionEditDialog.resetTypeListMap();
	}

	public EntityConfiguration getEntityConfiguration() {
		return entityConfiguration;
	}


}
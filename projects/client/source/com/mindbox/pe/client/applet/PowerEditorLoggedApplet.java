package com.mindbox.pe.client.applet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.MainApplication;
import com.mindbox.pe.client.MainPanel;
import com.mindbox.pe.common.config.EntityConfiguration;
import com.mindbox.pe.common.config.EntityTypeDefinition;
import com.mindbox.pe.communication.FetchEntityConfigurationRequest;
import com.mindbox.pe.communication.FetchEntityConfigurationResponse;
import com.mindbox.pe.communication.FetchUserProfileRequest;
import com.mindbox.pe.communication.FetchUserProfileResponse;
import com.mindbox.pe.communication.RequestComm;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.UserProfile;

/**
 * PowerEditor Applet that requires a valid session.
 *
 * @since PowerEditor 4.0
 */
public class PowerEditorLoggedApplet extends JApplet {

	private MainApplication application = null;
	private String sessionID = null;
	private String logOffURL = null;

	public PowerEditorLoggedApplet() {
		application = null;
	}


	public String getAppletInfo() {
		return "MindBox PowerEditor";
	}

	public void destroy() {
		try {
			if (application != null) {
				ClientUtil.printInfo("Calling Dispose from Applet.destroy");
				application.dispose();
			}
		}
		finally {
			super.destroy();
		}
	}

	/**
	 *
	 */
	public void logoff() {
		ClientUtil.getLogger().info("--> logoff");

		showStatus(ClientUtil.getInstance().getMessage("msg.logoff.success"));
		getContentPane().setCursor(Cursor.getDefaultCursor());
		getGlassPane().setVisible(false);
		application = null;

		PowerEditorSwingTheme.resetLookAndFeelSet();
		ClientUtil.getLogger().info("<-- logoff");
	}

	private void gotoLoginScreen() {
		this.setVisible(false);
		try {
			getAppletContext().showDocument(getURL(logOffURL));
		}
		catch (Exception e) {
			e.printStackTrace();
			getAppletContext().showDocument(getURL("/powererditor/login.jsp"));
		}
	}

	protected URL getURL(String s) {
		URL url = getCodeBase();
		URL url1 = null;

		try {
			url1 = new URL(url, s);
		}
		catch (MalformedURLException _ex) {
			ClientUtil.getLogger().warn("Couldn't create image: badly specified URL", _ex);
			return null;
		}

		return url1;
	}

	private void printPackageInfo() {
		Package appletPackage = Package.getPackage("com.mindbox.pe.client.applet");
		if (appletPackage != null) {
			ClientUtil.getLogger().info("*** Specification Title:   " + appletPackage.getSpecificationTitle());
			ClientUtil.getLogger().info("*** Specification Version: " + appletPackage.getSpecificationVersion());
			ClientUtil.getLogger().info("*** Implementation Version: " + appletPackage.getImplementationVersion());
		}
	}

	private boolean checkSecurity() {
		SecurityManager sm = System.getSecurityManager();
		try {
			if (sm != null) sm.checkSystemClipboardAccess();
		}
		catch (SecurityException ex) {
			ex.printStackTrace(System.err);
			ClientUtil.getInstance().showErrorDialog("msg.error.init.security.clipboard");
			setMessage(ClientUtil.getInstance().getMessage("msg.error.init.security.clipboard"));
			return false;
		}
		return true;
	}

	public void init() {
		ClientUtil.getLogger().info("---> init");
		ClientUtil.getInstance().setApplet(this);

		if (getParameter("lookAndFeel") != null && getParameter("lookAndFeel").equals("OS")) {
			PowerEditorSwingTheme.setLookAndFeelToOS();
		}
		else {
			PowerEditorSwingTheme.setLookAndFeelToMulti();
		}

		printPackageInfo();

		sessionID = this.getParameter("ssid");
		ClientUtil.getLogger().info("sessionID = " + sessionID);

		if (sessionID == null) {
			ClientUtil.getInstance().showErrorMessage("Failed to initialize PowerEditor application. Please contact Administrator");
		}

		String server = this.getParameter("server");
		RequestComm.setServletURL(server);
		ClientUtil.getLogger().info("server = " + server);

		this.logOffURL = this.getParameter("logoffURL");
		ClientUtil.getLogger().info("logOffURL = " + logOffURL);

		//Execute a job on the event-dispatching thread:
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				public void run() {
					initGUI();
				}
			});
		}
		catch (Exception e) {
			System.err.println("Applet initialization incomplete: " + e.getMessage());
		}
		
		ClientUtil.getLogger().info("<--- init");
	}

	private void initGUI() {
		JPanel panel = UIFactory.createBorderLayoutPanel(0, 0);
		panel.setBackground(Color.WHITE);
		panel.setOpaque(true);

		setContentPane(panel);
	}

	private void setMessage(String messageStr) {
		showStatus(messageStr);
	}

	public void start() {
		ClientUtil.getLogger().info("---> start");
		try {
			showStatus("Starting application...");
			if (checkSecurity()) {
				showStatus(ClientUtil.getInstance().getMessage(
						"msg.info.applet.running",
						new Object[] { System.getProperty("line.separator") }));

				// check if session is invalid
				if (showMainFrame()) {
				}
				else {
					gotoLoginScreen();
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace(System.err);
			ClientUtil.getInstance().showErrorDialog("msg.error.init.failure.start", new Object[] { ex.getMessage() });
		}
		finally {
			if (ClientUtil.getParent() != null) ClientUtil.getParent().setCursor(UIFactory.getDefaultCursor());
		}
		
		ClientUtil.getLogger().info("<--- start");
	}

	private void initEntityTypeDefs(EntityConfiguration entityConfig) {
		// @since 3.0.0 - create generic entity instances
		EntityTypeDefinition[] entityTypeDefs = entityConfig.getEntityTypeDefinitions();
		for (int i = 0; i < entityTypeDefs.length; i++) {
			GenericEntityType.makeInstance(entityTypeDefs[i]);
		}
		ClientUtil.getLogger().debug("entity configuration initialized");
	}

	private boolean showMainFrame() throws ServerException, IOException, ClassNotFoundException {
		showStatus("Running PowerEditor...");
		
		// retrieve entity configuration first...
		FetchEntityConfigurationRequest entityConfigRequest = new FetchEntityConfigurationRequest(sessionID);
		FetchEntityConfigurationResponse entityConfigResponse = entityConfigRequest.sendRequest();
		initEntityTypeDefs(entityConfigResponse.getEntityConfiguration());
		ClientUtil.getInstance().resetCachedTypeEnumValueMap(entityConfigResponse.getTypeEnumValueMap());

		// retrieve user profile & cache info...
		FetchUserProfileRequest request = new FetchUserProfileRequest(sessionID);
		FetchUserProfileResponse response = (FetchUserProfileResponse) request.sendRequest();
		UserProfile userProfile = response.getUserProfile();
		if (userProfile == null) {
			ClientUtil.getInstance().showErrorDialog("msg.warning.invalid.session");
			return false;
		}

		MainPanel desktop = new MainPanel(
				this,
				sessionID,
				entityConfigResponse.getEntityConfiguration(),
				userProfile,
				entityConfigResponse.getKbDateFilterConfig() != null && entityConfigResponse.getKbDateFilterConfig().getEndDate() != null);

		this.application = desktop;

		this.getContentPane().add(desktop, BorderLayout.CENTER);
		desktop.setVisible(true);

		return true;
	}

}
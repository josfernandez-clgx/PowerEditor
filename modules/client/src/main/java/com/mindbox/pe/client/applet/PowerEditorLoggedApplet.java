package com.mindbox.pe.client.applet;

import static com.mindbox.pe.common.LogUtil.logInfo;
import static com.mindbox.pe.common.LogUtil.logWarn;
import static com.mindbox.pe.common.XmlUtil.unmarshal;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.MainApplication;
import com.mindbox.pe.client.MainPanel;
import com.mindbox.pe.communication.FetchPeConfigurationRequest;
import com.mindbox.pe.communication.FetchPeConfigurationResponse;
import com.mindbox.pe.communication.RequestComm;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.xsd.config.EntityType;
import com.mindbox.pe.xsd.config.PowerEditorConfiguration;
import com.mindbox.pe.xsd.config.UserInterfaceConfig.UsageTypeList.UsageType;

/**
 * PowerEditor Applet that requires a valid session.
 *
 * @since PowerEditor 4.0
 */
public class PowerEditorLoggedApplet extends JApplet implements TimeOutHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	private MainApplication application = null;
	private String sessionID = null;
	private String logOffURL = null;
	private JPanel contentPanel = null;
	private CardLayout cardLayout = new CardLayout();
	private JPanel timedOutPanel = null;

	public PowerEditorLoggedApplet() {
		application = null;
	}

	@SuppressWarnings("deprecation")
	private boolean checkSecurity() {
		SecurityManager sm = System.getSecurityManager();
		try {
			if (sm != null) {
				sm.checkSystemClipboardAccess();
			}
		}
		catch (SecurityException ex) {
			ex.printStackTrace(System.err);
			ClientUtil.getInstance().showErrorDialog("msg.error.init.security.clipboard");
			setMessage(ClientUtil.getInstance().getMessage("msg.error.init.security.clipboard"));
			return false;
		}
		return true;
	}

	@Override
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

	@Override
	public String getAppletInfo() {
		return "MindBox PowerEditor";
	}

	private JPanel getTimedOutPanel() {
		if (timedOutPanel == null) {
			final JLabel messageLabel = new JLabel(
					String.format("<html><body><font size='+1'><b>%s</b></font></body></html>", ClientUtil.getInstance().getMessage("msg.warning.timed.out")));
			timedOutPanel = UIFactory.createFlowLayoutPanel(FlowLayout.CENTER, 12, 40);
			timedOutPanel.setBackground(Color.WHITE);
			timedOutPanel.setOpaque(true);
			timedOutPanel.add(messageLabel);
		}
		return timedOutPanel;
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

	@Override
	public synchronized void init() {
		System.out.println("---> PEApplet: init");

		final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
			final InputStream log4jConfigIn = getClass().getClassLoader().getResourceAsStream("log4j.properties");
			if (log4jConfigIn != null) {
				PropertyConfigurator.configure(log4jConfigIn);
			}
			else {
				BasicConfigurator.configure();
				System.out.println("WARNING: log4j.properties not found");
			}
		}
		finally {
			Thread.currentThread().setContextClassLoader(currentClassLoader);
		}

		ClientUtil.getLogger().info("---> init");
		ClientUtil.getInstance().setApplet(this);

		printPackageInfo();

		sessionID = this.getParameter("ssid");
		ClientUtil.getLogger().info("sessionID = " + sessionID);

		if (sessionID == null) {
			ClientUtil.getInstance().showErrorMessage("Failed to initialize PowerEditor application. Please contact Administrator");
		}

		final String server = this.getParameter("server");
		RequestComm.setServletURL(server);
		ClientUtil.getLogger().info("server = " + server);

		this.logOffURL = this.getParameter("logoffURL");
		ClientUtil.getLogger().info("logOffURL = " + logOffURL);

		//Execute a job on the event-dispatching thread:
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					initGUI();
				}
			});
		}
		catch (Exception e) {
			ClientUtil.getLogger().error("Initialization failed", e);
			System.err.println("Applet initialization incomplete: " + e.getMessage());
		}

		ClientUtil.getLogger().info("<--- init");
		System.out.println("<--- PEApplet: init");
	}

	private void initEntityTypeDefs(final List<EntityType> entityTypes) {
		// @since 3.0.0 - create generic entity instances
		for (final EntityType entityType : entityTypes) {
			GenericEntityType.makeInstance(entityType);
		}
		ClientUtil.getLogger().debug("entity configuration initialized");
	}

	private void initGUI() {
		contentPanel = UIFactory.createJPanel();
		contentPanel.setLayout(cardLayout);
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setOpaque(true);
		setContentPane(contentPanel);
	}

	public void logoff() {
		ClientUtil.getLogger().info("--> logoff");

		showStatus(ClientUtil.getInstance().getMessage("msg.logoff.success"));
		getContentPane().setCursor(Cursor.getDefaultCursor());
		getGlassPane().setVisible(false);
		application = null;

		PowerEditorSwingTheme.resetLookAndFeelSet();
		ClientUtil.getLogger().info("<-- logoff");
	}

	private PowerEditorConfiguration parsePowerEditorConfiguration(final String configXmlString) throws JAXBException {
		PowerEditorConfiguration powerEditorConfiguration = null;
		try {
			powerEditorConfiguration = unmarshal(configXmlString, PowerEditorConfiguration.class);
		}
		catch (Exception e) {
			logWarn(ClientUtil.getLogger(), e, "Failed to parse config xml using default method; trying alternate approach...");
			final JAXBContext jaxbContext = JAXBContext.newInstance(
					PowerEditorConfiguration.class.getPackage().toString(),
					new PowerEditorConfiguration().getClass().getClassLoader());
			final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			powerEditorConfiguration = PowerEditorConfiguration.class.cast(unmarshaller.unmarshal(new StringReader(configXmlString)));
		}
		return powerEditorConfiguration;
	}

	private void printPackageInfo() {
		Package appletPackage = Package.getPackage("com.mindbox.pe.client.applet");
		if (appletPackage != null) {
			ClientUtil.getLogger().info("*** Specification Title:   " + appletPackage.getSpecificationTitle());
			ClientUtil.getLogger().info("*** Specification Version: " + appletPackage.getSpecificationVersion());
			ClientUtil.getLogger().info("*** Implementation Version: " + appletPackage.getImplementationVersion());
		}
	}

	@Override
	public void sessionTimedOut() {
		System.out.println("[PEApplet] Session timed out!");
		cardLayout.show(contentPanel, "TIMEOUT");
	}

	private void setMessage(String messageStr) {
		showStatus(messageStr);
	}

	private boolean showMainFrame() throws ServerException, IOException, ClassNotFoundException, JAXBException {
		showStatus("Running PowerEditor...");

		// retrieve PE configuration first...
		final FetchPeConfigurationRequest fetchPeConfigRequest = new FetchPeConfigurationRequest(sessionID);
		final FetchPeConfigurationResponse fetchPeConfigResponse = fetchPeConfigRequest.sendRequest(null);

		ClientUtil.getLogger().info("PE config retrieved from server. Parsing config...");

		// Parse PowerEditorConfiguration
		final PowerEditorConfiguration powerEditorConfiguration = parsePowerEditorConfiguration(fetchPeConfigResponse.getConfigContent());

		ClientUtil.getLogger().info("Config parsed. initializing applet data...");

		// NOTE: The order of the following are important

		// Create entity types first
		initEntityTypeDefs(powerEditorConfiguration.getEntityConfig().getEntityType());

		// Then, create template usage types
		for (final UsageType usageType : powerEditorConfiguration.getUserInterface().getUsageTypeList().getUsageType()) {
			TemplateUsageType.createInstance(
					usageType.getName(),
					(usageType.getDisplayName() == null ? usageType.getName() : usageType.getDisplayName()),
					usageType.getPrivilege());
		}

		// Then reset PE config
		ClientUtil.resetPowerEditorConfiguration(powerEditorConfiguration, fetchPeConfigResponse.getUserManagementConfig());

		ClientUtil.getInstance().resetCachedTypeEnumValueMap(fetchPeConfigResponse.getTypeEnumValueMap());

		ClientUtil.getInstance().initializeTimeOutController(powerEditorConfiguration.getServer().getSession().getTimeOutInMin().longValue() * 60);

		final boolean readOnly = (powerEditorConfiguration.getKnowledgeBaseFilter() != null && powerEditorConfiguration.getKnowledgeBaseFilter().getDateFilter() != null
				&& powerEditorConfiguration.getKnowledgeBaseFilter().getDateFilter().getEndDate() != null);

		logInfo(ClientUtil.getLogger(), "Initialized applet data (readOnly?=%b). Setting up UI elements...", readOnly);

		final MainPanel desktop = new MainPanel(
				this,
				sessionID,
				ClientUtil.getEntityConfigHelper(),
				fetchPeConfigResponse.getUserProfile(),
				ClientUtil.getGuidelineTabs(),
				ClientUtil.getEntityTabMap(),
				readOnly);

		this.application = desktop;

		logInfo(ClientUtil.getLogger(), "UI elements ready.");

		final JPanel wrapperPanel = UIFactory.createBorderLayoutPanel(0, 0);
		wrapperPanel.add(desktop, BorderLayout.CENTER);
		this.getContentPane().add(wrapperPanel, "MAIN");
		this.getContentPane().add(getTimedOutPanel(), "TIMEOUT");
		desktop.setVisible(true);

		ClientUtil.getInstance().getTimeOutController().addTimeOutListener(new DefaultTimeOutListener(this));

		return true;
	}

	@Override
	public synchronized void start() {
		ClientUtil.getLogger().info("---> start");
		try {
			showStatus("Starting application...");
			if (checkSecurity()) {
				if (getParameter("lookAndFeel") != null && getParameter("lookAndFeel").equals("OS")) {
					PowerEditorSwingTheme.setLookAndFeelToOS();
				}
				else {
					PowerEditorSwingTheme.setLookAndFeelToMulti();
				}

				showStatus(ClientUtil.getInstance().getMessage("msg.info.applet.running", new Object[] { System.getProperty("line.separator") }));

				// check if session is invalid
				if (showMainFrame()) {
					ClientUtil.getInstance().getTimeOutController().restartTimer();
					logInfo(ClientUtil.getLogger(), "Timeout timer started");
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
			if (ClientUtil.getParent() != null) {
				ClientUtil.getParent().setCursor(UIFactory.getDefaultCursor());
			}
		}

		ClientUtil.getLogger().info("<--- start");
	}
}
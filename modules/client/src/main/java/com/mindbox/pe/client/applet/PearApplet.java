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
import javax.swing.JFrame;
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
public class PearApplet extends PowerEditorLoggedApplet {
	/**
	 *
	 */
	private static final long serialVersionUID = -2588077856342092948L;

	private JFrame outerFrame = null;
	public static PearApplet selfReference = null;

	public PearApplet() throws Exception {
	  super();
	  if (null != selfReference) {
	    throw new Exception("PearApplet instance already exists");
	  }
	  selfReference = this;
	}

	public void setOuterFrame(JFrame frame) throws Exception {
	  if (null != outerFrame) {
		throw new Exception("Outer frame already set");
	  }
	  outerFrame = frame;
	  outerFrame.addWindowListener(new java.awt.event.WindowAdapter() {
	      @Override
	      public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		((MainPanel) application).logout();
		PearApplet.selfReference.logoff();
		System.exit(0);
	      }
	    });
	}

	@Override
	public void logoff() {
		ClientUtil.getLogger().info("--> logoff");

		getContentPane().setCursor(Cursor.getDefaultCursor());
		getGlassPane().setVisible(false);
		application = null;

		PowerEditorSwingTheme.resetLookAndFeelSet();
		ClientUtil.getLogger().info("<-- logoff");
	}

	@Override
	protected void setMessage(String messageStr) {
	}

	@Override
	protected boolean showMainFrame() throws ServerException, IOException, ClassNotFoundException, JAXBException {
	  // showStatus("Running PowerEditor...");

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
		  // showStatus("Starting application...");
			if (checkSecurity()) {
				if (getParameter("lookAndFeel") != null && getParameter("lookAndFeel").equals("OS")) {
					PowerEditorSwingTheme.setLookAndFeelToOS();
				}
				else {
					PowerEditorSwingTheme.setLookAndFeelToMulti();
				}

				// showStatus(ClientUtil.getInstance().getMessage("msg.info.applet.running", new Object[] { System.getProperty("line.separator") }));

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

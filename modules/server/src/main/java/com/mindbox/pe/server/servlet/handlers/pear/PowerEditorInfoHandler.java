package com.mindbox.pe.server.servlet.handlers.pear;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import com.mindbox.pe.communication.pear.PowerEditorInfoRequest;
import com.mindbox.pe.communication.pear.PowerEditorInfoResponse;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.DateFilterConfigHelper;
import com.mindbox.pe.server.spi.ServiceProviderFactory;
import com.mindbox.pe.xsd.config.PowerEditorConfiguration;
import com.mindbox.pe.xsd.config.UserInterfaceConfig;

public class PowerEditorInfoHandler extends Handler {

    private static final Logger LOG = Logger.getLogger(PowerEditorInfoHandler.class);
    private static final SimpleDateFormat TITLE_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    public static PowerEditorInfoResponse process(PowerEditorInfoRequest request, HttpServletRequest servletRequest) {
        LOG.trace("Process()" + request.toString());

        ConfigurationManager manager = ConfigurationManager.getInstance();
        PowerEditorConfiguration peConfig = manager.getPowerEditorConfiguration();
        UserInterfaceConfig uiConfig = peConfig.getUserInterface();

        String accessWarning = uiConfig.getUnauthorizedAccessWarningText();
        String clientWindowTitle = generateTitle(uiConfig.getClientWindowTitle(), peConfig);

        String iconPath = servletRequest.getSession().getServletContext().getRealPath(".") + File.separator + "images" + File.separator + "MB.gif";
        ImageIcon mindboxIcon = new ImageIcon(iconPath);
        String passwordRequirements = ServiceProviderFactory.getPasswordValidatorProvider().getDescription();
        String version = manager.getAppVersion() + "(" + manager.getAppBuild() + ")";

        PowerEditorInfoResponse response = new PowerEditorInfoResponse(accessWarning, clientWindowTitle, mindboxIcon, passwordRequirements, version);
        return response;
    }

    private static String generateTitle(String clientWindowTitle, PowerEditorConfiguration peConfig) {
        final DateFilterConfigHelper dateFilterConfigHelper = ConfigurationManager.getInstance().getDateFilterConfigHelper();
        StringBuffer sb = new StringBuffer(clientWindowTitle);
        if (dateFilterConfigHelper != null && dateFilterConfigHelper.hasBeginOrEndDate()) {
            synchronized (TITLE_DATE_FORMAT) {
                sb.append(" [");
                Date beginDate = dateFilterConfigHelper.getBeginDate();
                if (null != beginDate) {
                    sb.append(TITLE_DATE_FORMAT.format(beginDate));
                }
                sb.append(" - ");
                Date endDate = dateFilterConfigHelper.getEndDate();
                if (null != endDate) {
                    sb.append(TITLE_DATE_FORMAT.format(endDate));
                }
                sb.append(" ]");
            }
            if (dateFilterConfigHelper.hasEndDate()) {
                sb.append("(READ-ONLY)");
            }
        } else {
            sb.append(" [All Dates]");
        }
        return sb.toString();
    }
}

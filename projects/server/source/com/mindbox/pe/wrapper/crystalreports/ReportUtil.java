/*
 * Created on Jan 30, 2006
 *
 */
package com.mindbox.pe.wrapper.crystalreports;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.crystaldecisions.reports.sdk.ReportClientDocument;
import com.crystaldecisions.reports.sdk.SubreportController;
import com.crystaldecisions.sdk.occa.report.data.Fields;
import com.crystaldecisions.sdk.occa.report.data.ParameterField;
import com.crystaldecisions.sdk.occa.report.data.ParameterFieldDiscreteValue;
import com.crystaldecisions.sdk.occa.report.data.Values;
import com.crystaldecisions.sdk.occa.report.lib.ReportSDKException;
import com.mindbox.pe.model.report.GuidelineReportSpec;
import com.mindbox.pe.server.cache.ReportSpecManager;
import com.mindbox.pe.server.report.ReportException;


/**
 * Provides utility methods for integration with Crystal Reports. 
 * @author Geneho Kim
 * @since PowerEditor 4.4.0
 */
public class ReportUtil {

	public static ReportClientDocument createPolicySummaryReportSource(String reportFilename, String reportIDStr) throws ReportException {
		Logger logger = Logger.getLogger(ReportUtil.class);
		GuidelineReportSpec reportSpec = ReportSpecManager.getInstance().getReportSpec(reportIDStr);
		if (reportSpec == null) throw new ReportException("Invalid report id: " + reportIDStr);
		logger.debug(">>> createPolicySummaryReportSource: report spec=" + reportSpec + ",id=" + reportIDStr);
		try {
			ReportClientDocument reportDocument = new ReportClientDocument();
			reportDocument.open(reportFilename, 0);
			return reportDocument;
		}
		catch (Exception ex) {
			logger.error("Error while creating policy summary report source", ex);
			throw new ReportException(ex.toString());
		}
	}

	public static ReportClientDocument createCustomReportSource(String reportFilename) throws ReportException {
		try {
			ReportClientDocument reportClientDoc = new ReportClientDocument();
			reportClientDoc.open(reportFilename, 0);

			return reportClientDoc;
		}
		catch (Exception ex) {
			throw new ReportException(ex.toString());
		}
	}

	@SuppressWarnings("unchecked")
	public static Fields createParameterFields(Object reportDocumentObj, HttpServletRequest request) throws ReportSDKException {
		Fields fields = new Fields();
		for (java.util.Enumeration<String> en = request.getParameterNames(); en.hasMoreElements();) {
			String paramName =  en.nextElement();
			// handle policy summary report
			if (paramName.equals("reportid")) {
				ReportClientDocument reportDocument = (ReportClientDocument) reportDocumentObj;
				// set to default report
				ParameterField paramField = new ParameterField();
				paramField.setName(paramName);
				paramField.setReportName("");

				ParameterFieldDiscreteValue pfieldDV = new ParameterFieldDiscreteValue();
				pfieldDV.setValue(request.getParameter(paramName));
				pfieldDV.setDescription(paramName);

				Values values = new Values();
				values.add(pfieldDV);
				paramField.setCurrentValues(values);
				fields.add(paramField);
				Logger.getLogger(ReportUtil.class).debug(
						"createParameterFields: added " + paramField.getName() + " for " + paramField.getReportName());

				// set reportid for subreports
				SubreportController subreportController = reportDocument.getSubreportController();
				if (subreportController == null) {
					Logger.getLogger(ReportUtil.class).warn("No subreport controller found");
				}
				else {
					//List reportNames = new ArrayList();
					//reportNames.add("TemplateRules");
					//reportNames.add("TemplateMessages");
					// GKim: only for CRXI release 2
					List<?> reportNames = subreportController.getSubreportNames();
					for (int i = 0; i < reportNames.size(); i++) {
						String subreportName = (String) reportNames.get(i);
						// set to GridView subreport
						paramField = new ParameterField();
						paramField.setName(paramName);
						paramField.setReportName(subreportName);

						pfieldDV = new ParameterFieldDiscreteValue();
						pfieldDV.setValue(request.getParameter(paramName));
						pfieldDV.setDescription(paramName);
						values = new Values();
						values.add(pfieldDV);
						paramField.setCurrentValues(values);
						fields.add(paramField);
						Logger.getLogger(ReportUtil.class).debug(
								"createParameterFields: added " + paramField.getName() + " for " + paramField.getReportName());
					}
				}
			}
			else if (!paramName.equals("reportname")) {
				ParameterField paramField = new ParameterField();
				paramField.setName(paramName);
				paramField.setReportName("");

				ParameterFieldDiscreteValue pfieldDV = new ParameterFieldDiscreteValue();
				pfieldDV.setValue(request.getParameter(paramName));
				pfieldDV.setDescription(paramName);

				Values values = new Values();
				values.add(pfieldDV);
				paramField.setCurrentValues(values);
				fields.add(paramField);
				Logger.getLogger(ReportUtil.class).debug("createParameterFields: added " + paramField.getName());
			}
		}

		return (fields.isEmpty() ? null : fields);
	}

	private ReportUtil() {

	}
}

// set result sets for tables used for pushing data into TTX based reports
/*
 DatabaseController dbController = reportDocument.getDatabaseController();
 Tables tables = dbController.getDatabase().getTables();
 logger.debug("    createPolicySummaryReportSource: tables size = " + tables.size());
 ITable table = tables.findTableByAlias("Policy_Summary_Activations");
 if (table != null) {
 logger.debug("    createPolicySummaryReportSource: table = " + table + ",alias=" + table.getAlias() + ",name=" + table.getName());
 ResultSet resultSet = new ReportResultSet(table.getAlias(), new PolicySummaryActivationDataHolder(reportIDStr));
 dbController.setDataSource(resultSet, table.getAlias(), table.getAlias() + "RunTime");
 }
 logger.debug("    createPolicySummaryReportSource: Policy_Summary_Activations resultset set");
 logger.debug("    createPolicySummaryReportSource: tables size after = " + tables.size());
 table = tables.findTableByAlias("Activation_Properties");
 if (table != null) {
 logger.debug("    createPolicySummaryReportSource: table = " + table + ",alias=" + table.getAlias() + ",name=" + table.getName());
 ResultSet resultSet = new ReportResultSet(table.getAlias(), new PolicySummaryPropertiesDataHolder(reportIDStr));
 dbController.setDataSource(resultSet, table.getAlias(), table.getAlias() + "RunTime");
 }
 logger.debug("    createPolicySummaryReportSource: Activation_Properties resultset set");
 if (table != null) {
 logger.debug("    createPolicySummaryReportSource: table = " + table + ",alias=" + table.getAlias() + ",name=" + table.getName());
 table = tables.findTableByAlias("Activation_Grid_Data");
 ResultSet resultSet = new ReportResultSet(table.getAlias(), new PolicySummaryGridDataHolder(reportIDStr));
 dbController.setDataSource(resultSet, table.getAlias(), table.getAlias() + "RunTime");
 }
 logger.debug("    createPolicySummaryReportSource: Activation_Grid_Data resultset set");
 table = tables.findTableByAlias("Activation_Per_Row_Data");
 if (table != null) {
 logger.debug("    createPolicySummaryReportSource: table = " + table + ",alias=" + table.getAlias() + ",name=" + table.getName());
 ResultSet resultSet = new ReportResultSet(table.getAlias(), new PolicySummaryPerRowDataHolder(reportIDStr));
 dbController.setDataSource(resultSet, table.getAlias(), table.getAlias() + "RunTime");
 }
 logger.debug("    createPolicySummaryReportSource: Activation_Per_Row_Data resultset set");
 //*/
/*
 ITable table = tables.findTableByAlias("getActivationListResultSet"); //"Policy_Summary_Activations");
 if (table != null) {
 logger.debug("    createPolicySummaryReportSource: table = " + table + ",alias=" + table.getAlias() + ",name=" + table.getName());
 ResultSet resultSet = new ReportResultSet(table.getAlias(), new PolicySummaryActivationDataHolder(reportIDStr));
 dbController.setDataSource(resultSet, table.getAlias(), table.getAlias() + "RunTime");
 }
 logger.debug("    createPolicySummaryReportSource: Policy_Summary_Activations resultset set");
 logger.debug("    createPolicySummaryReportSource: tables size after = " + tables.size());
 table = tables.findTableByAlias("getActivationPropertiesResultSet"); //Activation_Properties");
 if (table != null) {
 logger.debug("    createPolicySummaryReportSource: table = " + table + ",alias=" + table.getAlias() + ",name=" + table.getName());
 ResultSet resultSet = new ReportResultSet(table.getAlias(), new PolicySummaryPropertiesDataHolder(reportIDStr));
 dbController.setDataSource(resultSet, table.getAlias(), table.getAlias() + "RunTime");
 }
 logger.debug("    createPolicySummaryReportSource: Activation_Properties resultset set");
 if (table != null) {
 logger.debug("    createPolicySummaryReportSource: table = " + table + ",alias=" + table.getAlias() + ",name=" + table.getName());
 table = tables.findTableByAlias("getActivationGridDataResultSet"); //Activation_Grid_Data");
 ResultSet resultSet = new ReportResultSet(table.getAlias(), new PolicySummaryGridDataHolder(reportIDStr));
 dbController.setDataSource(resultSet, table.getAlias(), table.getAlias() + "RunTime");
 }
 logger.debug("    createPolicySummaryReportSource: Activation_Grid_Data resultset set");
 table = tables.findTableByAlias("getActivationPerRowDataResultSet"); //Activation_Per_Row_Data");
 if (table != null) {
 logger.debug("    createPolicySummaryReportSource: table = " + table + ",alias=" + table.getAlias() + ",name=" + table.getName());
 ResultSet resultSet = new ReportResultSet(table.getAlias(), new PolicySummaryPerRowDataHolder(reportIDStr));
 dbController.setDataSource(resultSet, table.getAlias(), table.getAlias() + "RunTime");
 }
 logger.debug("    createPolicySummaryReportSource: Activation_Per_Row_Data resultset set");
 //*/

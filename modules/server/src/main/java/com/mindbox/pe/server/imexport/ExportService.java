package com.mindbox.pe.server.imexport;

import static com.mindbox.pe.common.XmlUtil.marshal;

import java.io.Writer;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.server.bizlogic.SearchCooridinator;
import com.mindbox.pe.server.imexport.provider.DefaultDataProvider;
import com.mindbox.pe.server.imexport.provider.ExportDataProvider;
import com.mindbox.pe.xsd.data.PowereditorData;

/**
 * Export service that provides export functionality.
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.5.0
 */
public class ExportService {

	private static ExportService instance = null;

	public static ExportService getInstance() {
		if (instance == null) {
			instance = new ExportService();
		}
		return instance;
	}

	private final Logger logger = Logger.getLogger(getClass());
	private final ExportDataProvider exportDataProvider = new DefaultDataProvider();

	private ExportService() {
	}

	public void exportAll(Writer writer, String userID) throws ExportException {
		GuidelineReportFilter filter = new GuidelineReportFilter();
		filter.setIncludeEntities(true);
		filter.setIncludeSecurityData(true);
		filter.setIncludeGuidelines(true);
		filter.setIncludeParameters(true);
		filter.setIncludeTemplates(true);
		filter.setIncludeGuidelineActions(true);
		filter.setIncludeTestConditions(true);
		filter.setIncludeDateSynonyms(true);
		export_internal(writer, filter, userID);
	}

	public void export(Writer writer, GuidelineReportFilter filter, String userID) throws ExportException {
		if (filter == null) throw new NullPointerException("filter is null");
		export_internal(writer, filter, userID);
	}

	private synchronized void export_internal(Writer writer, GuidelineReportFilter filter, String userID) throws ExportException {
		logger.debug(">>> export_internal: filter=" + filter);
		try {
			final ExportDataGenerator exportDataGenerator = new ExportDataGenerator(exportDataProvider);

			exportDataGenerator.exportMetaData(userID);
			exportDataGenerator.exportTypeEnumData();

			// new in 5.0 XSD
			if (filter.isIncludeDateSynonyms()) {
				exportDataGenerator.exportDateData();
			}

			if (filter.isIncludeEntities()) {
				exportDataGenerator.exportNextIDSeeds();
				exportDataGenerator.exportEntities(filter.useDaysAgo(), filter.getDaysAgo());
			}

			if (filter.isIncludeSecurityData()) {
				exportDataGenerator.exportSecurity();
			}

			if (filter.isIncludeGuidelineActions()) {
				exportDataGenerator.exportGuidelineActions();
			}

			if (filter.isIncludeTestConditions()) {
				exportDataGenerator.exportTestConditions();
			}

			if (filter.isIncludeTemplates()) {
				exportDataGenerator.exportGuidelineTemplates(filter.getUsageTypes(), filter.getGuidelineTemplateIDs());
			}

			if (filter.isIncludeGuidelines() || filter.isIncludeParameters()) {
				filter.setServerFilterHelper(SearchCooridinator.getServerFilterHelper());
				if (filter.isIncludeGuidelines()) {
					exportDataGenerator.exportGuidelines(filter);
				}
				if (filter.isIncludeParameters()) {
					exportDataGenerator.exportParameters(filter);
				}
			}

			if (filter.isIncludeCBR()) {
				exportDataGenerator.exportCBRData();
			}

			marshal(exportDataGenerator.getPowereditorData(), writer, true, true, PowereditorData.class);
		}
		catch (Exception ex) {
			logger.error("Export failed", ex);
			ex.printStackTrace(System.err);
			throw new ExportException(ex.getMessage());
		}
	}
}
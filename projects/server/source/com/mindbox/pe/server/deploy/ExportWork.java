package com.mindbox.pe.server.deploy;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.generator.OutputController;

class ExportWork extends AbstractDeployWork {

	private final boolean exportPolicies;
	private final String userID;

	ExportWork(CountDownLatch countDownLatch, OutputController outputController, GuidelineReportFilter filter,
			AtomicInteger uncaughtErrorCount, boolean exportPolicies, String userID) {
		super("Export data", countDownLatch, outputController, filter, uncaughtErrorCount);
		this.exportPolicies = exportPolicies;
		this.userID = userID;
	}

	@Override
	void performWork() throws Exception {
		logger.debug("Exporting entities...");

		// Export Entities
		final GuidelineReportFilter entitiesFilter = new GuidelineReportFilter();
		entitiesFilter.setIncludeEntities(true);
		entitiesFilter.setIncludeDateSynonyms(true);

		final File deployDir = new File(outputController.getDeployDir());
		final String entitiesFileName = new File(deployDir, Constants.ENTITIES_EXPORT_FILE_NAME).getAbsolutePath();

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("writing entities file at %s...", entitiesFileName));
		}
		
		long startTime = System.currentTimeMillis();

		BizActionCoordinator.getInstance().writeExportXML(entitiesFilter, entitiesFileName, userID);

		logger.info(String.format("Entity export completed: took %d ms", (System.currentTimeMillis() - startTime)));

		if (exportPolicies) {
			logger.debug("Exporting policy file...");

			final GuidelineReportFilter policiesFilter = new GuidelineReportFilter();
			// We need date synonym as grid elements refer to them
			policiesFilter.setIncludeDateSynonyms(true);
			policiesFilter.setIncludeEntities(true);
			policiesFilter.setIncludeTemplates(true);
			policiesFilter.setIncludeGuidelines(true);
			policiesFilter.setIncludeParameters(true);
			policiesFilter.setIncludeSecurityData(true);
			
			final String policiesFilename = new File(deployDir, Constants.POLICIES_EXPORT_FILE_NAME).getAbsolutePath();

			if (logger.isDebugEnabled()) {
				logger.debug(String.format("writing policy file at %s...", policiesFilename));
			}

			startTime = System.currentTimeMillis();

			BizActionCoordinator.getInstance().writeExportXML(policiesFilter, policiesFilename, userID);

			logger.info(String.format("Policy export completed: took %d ms", (System.currentTimeMillis() - startTime)));
		}
	}
}

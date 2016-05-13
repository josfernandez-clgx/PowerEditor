package com.mindbox.pe.server.deploy;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.LogUtil.logInfo;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.server.bizlogic.BizActionCoordinator;
import com.mindbox.pe.server.generator.OutputController;
import com.mindbox.pe.server.generator.RuleGenerationException;

class ExportWork extends AbstractDeployWork {

	private final boolean exportPolicies;
	private final String userID;

	ExportWork(final int percentageAllocation, final CountDownLatch countDownLatch, final OutputController outputController, final GuidelineReportFilter filter, final GenerateStats generateStats,
			final boolean exportPolicies, final String userID) throws RuleGenerationException {
		super(percentageAllocation, "Export data", countDownLatch, outputController, filter, generateStats);
		this.exportPolicies = exportPolicies;
		this.userID = userID;
	}

	@Override
	void performWork() throws Exception {
		int percentageAdded = 0;
		final File deployDir = new File(outputController.getDeployDir());
		try {
			logger.debug("Exporting entities...");

			// Export Entities
			if (filter.isIncludeEntities()) {
				logger.debug("Exporting entities...");

				final GuidelineReportFilter entitiesFilter = new GuidelineReportFilter();
				entitiesFilter.setIncludeEntities(true);
				entitiesFilter.setIncludeDateSynonyms(true);

				final String entitiesFileName = new File(deployDir, Constants.ENTITIES_EXPORT_FILE_NAME).getAbsolutePath();

				logDebug(logger, "writing entities file at %s...", entitiesFileName);

				final long startTime = System.currentTimeMillis();

				BizActionCoordinator.getInstance().writeExportXML(entitiesFilter, entitiesFileName, userID);

				logInfo(logger, "Entity export completed: took %d ms", (System.currentTimeMillis() - startTime));
			}

			final int percentageToAdd = percentageAllocation / 2;
			generateStats.addPercentComplete(percentageToAdd);
			percentageAdded += percentageToAdd;


			if (exportPolicies) {
				logger.debug("Exporting policy file...");

				final GuidelineReportFilter policiesFilter = new GuidelineReportFilter();
				// We need date synonym as grid elements refer to them
				policiesFilter.setIncludeDateSynonyms(true);
				policiesFilter.setIncludeEntities(true);
				policiesFilter.setIncludeTemplates(true);
				policiesFilter.setIncludeGuidelines(true);
				policiesFilter.setIncludeParameters(true);
				policiesFilter.setIncludeSecurityData(false);

				final String policiesFilename = new File(deployDir, Constants.POLICIES_EXPORT_FILE_NAME).getAbsolutePath();

				logDebug(logger, "writing policy file at %s...", policiesFilename);

				final long startTime = System.currentTimeMillis();

				BizActionCoordinator.getInstance().writeExportXML(policiesFilter, policiesFilename, userID);

				generateStats.addPercentComplete(percentageToAdd);
				percentageAdded += percentageToAdd;

				logger.info(String.format("Policy export completed: took %d ms", (System.currentTimeMillis() - startTime)));
			}
		}
		finally {
			generateStats.addPercentComplete(percentageAllocation - percentageAdded);
		}
	}
}

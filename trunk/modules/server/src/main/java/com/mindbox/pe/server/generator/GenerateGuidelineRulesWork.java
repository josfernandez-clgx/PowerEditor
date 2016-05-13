package com.mindbox.pe.server.generator;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.LogUtil.logInfo;
import static com.mindbox.pe.common.LogUtil.logWarn;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.server.model.TimeSliceContainer;

/**
 * Work that generates guidelines rule for a given template.
 * This returns the generated file name.
 * @author clint
 *
 */
final class GenerateGuidelineRulesWork implements Callable<GridTemplate> {

	private static final Logger LOG = Logger.getLogger(GenerateGuidelineRulesWork.class);

	private final GridTemplate gridTemplate;
	private final TimeSliceContainer timeSliceContainer;
	private final GuidelineReportFilter filter;
	private final CountDownLatch countDownLatch;
	private final RuleGenerator ruleGenerator;
	private final int percentageAllocation;

	GenerateGuidelineRulesWork(final int percentageAllocation, final GenerateStats generateStats, final GridTemplate gridTemplate, final TimeSliceContainer timeSliceContainer,
			final GuidelineReportFilter filter, CountDownLatch countDownLatch, final RuleGenerator ruleGenerator) {
		super();
		this.percentageAllocation = percentageAllocation;
		this.gridTemplate = gridTemplate;
		this.timeSliceContainer = timeSliceContainer;
		this.filter = filter;
		this.countDownLatch = countDownLatch;
		this.ruleGenerator = ruleGenerator;
		logDebug(LOG, "Created instance for template=%s, filter=%s", gridTemplate, filter);
	}

	@Override
	public GridTemplate call() {
		logInfo(LOG, "---> run() for template %s", gridTemplate.getId());
		GridTemplate gridTemplateToReturn = null;
		try {
			TemplateUsageType usageType = gridTemplate.getUsageType();
			if (usageType != null) {
				final long startTime = System.currentTimeMillis();

				final GuidelineRuleGenerator guidelineRuleGenerator = new DefaultGuidelineRuleGenerator(gridTemplate, ruleGenerator.getGenerateStats(), ruleGenerator.getOutputController());
				logInfo(LOG, "guidelineRuleGenerator initialized for %s in %d (ms)", gridTemplate.getName(), (System.currentTimeMillis() - startTime));

				guidelineRuleGenerator.generateOptimized(percentageAllocation, timeSliceContainer, filter);

				gridTemplateToReturn = gridTemplate;

				logInfo(LOG, "guideline rule generation for %s (%d) took %d (ms)", gridTemplate.getName(), gridTemplate.getId(), (System.currentTimeMillis() - startTime));
			} // usageType sanity check
			else {
				logWarn(LOG, "No rule generated for template %s (%d) because it doesn't have usage type", gridTemplate.getName(), gridTemplate.getId());
			}
		}
		catch (RuleGenerationException e) {
			logWarn(LOG, e, "guideline rule generation for %s (%d) failed", gridTemplate.getName(), gridTemplate.getId());
			try {
				ruleGenerator.getOutputController().writeErrorMessage("Rule generation", e.getMessage());
				ruleGenerator.getGenerateStats().incrementNumErrors();
			}
			catch (RuleGenerationException rge) {
				logWarn(LOG, rge, "Failed to report error: %s", e.getMessage());
			}
		}
		finally {
			countDownLatch.countDown();
			ruleGenerator.getGenerateStats().addPercentComplete(percentageAllocation);
			logInfo(LOG, "<--- run() for template %s", gridTemplate.getId());
		}

		return gridTemplateToReturn;
	}
}

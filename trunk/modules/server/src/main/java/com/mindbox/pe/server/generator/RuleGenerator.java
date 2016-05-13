package com.mindbox.pe.server.generator;

import static com.mindbox.pe.common.IOUtil.close;
import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.LogUtil.logInfo;
import static com.mindbox.pe.common.LogUtil.logWarn;
import static com.mindbox.pe.server.generator.RuleGeneratorHelper.ART_FILE_EXTENSION;
import static com.mindbox.pe.server.generator.RuleGeneratorHelper.getRuleFilename;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.model.TimeSliceContainer;

/**
 * Generates all ARTScript rules.
 * This is designed to run in a separate thread.
 * 
 * @since PowerEditor 1.0
 */
public class RuleGenerator {

	private static final Logger LOG = Logger.getLogger(RuleGenerator.class);

	private static final int GUIDELINE_WORK_TIMEOUT_HOURS = 12;

	private static final int MAX_GUIDELINE_WORK_THREADS = 8;

	private static Map<TemplateUsageType, List<GridTemplate>> asUsageTemplateMap(final List<GridTemplate> templates) {
		final Map<TemplateUsageType, List<GridTemplate>> map = new HashMap<TemplateUsageType, List<GridTemplate>>();
		for (GridTemplate template : templates) {
			if (template != null) {
				if (map.containsKey(template.getUsageType())) {
					map.get(template.getUsageType()).add(template);
				}
				else {
					map.put(template.getUsageType(), new ArrayList<GridTemplate>(Arrays.asList(template)));
				}
			}
		}
		return map;
	}

	private final ExecutorService workExecutorService;
	private final AeMapper aeMapper;
	private final boolean mergeFilesPerUsageType;
	private final OutputController outputController;
	private final GenerateStats generateStats;

	public RuleGenerator(final GenerateStats generateStats, final OutputController outputController, final Integer guidelineMaxThreads, final boolean mergeFilesPerUsageType)
			throws RuleGenerationException {
		this.generateStats = generateStats;
		this.outputController = outputController;
		final int maxThreads = guidelineMaxThreads == null ? MAX_GUIDELINE_WORK_THREADS : guidelineMaxThreads.intValue();
		this.workExecutorService = Executors.newFixedThreadPool(maxThreads);
		this.aeMapper = AeMapper.getGuidelineInstance();
		this.mergeFilesPerUsageType = mergeFilesPerUsageType;
		aeMapper.reInit();
		logInfo(LOG, "Created instance with max thread=[%s], mergeFilesPerUsageType=%b, oc=[%s]", maxThreads, mergeFilesPerUsageType, outputController);
	}

	public synchronized void generate(final int percentageAllocation, final GuidelineReportFilter filter) throws RuleGenerationException {
		final int basePercentageAllocation = percentageAllocation / 4;
		final List<GridTemplate> templateList = new ArrayList<GridTemplate>();
		try {
			logInfo(LOG, ">>> RuleGenerator.generate: %s, basePercentageAllocation=%d", filter, basePercentageAllocation);

			// [1] Generate time slices first
			TimeSliceContainer timeSliceContainer = null;
			if (filter.isIncludeGuidelines() || filter.isIncludeParameters()) {
				logInfo(LOG, "generating time slices...");
				timeSliceContainer = DateSynonymManager.getInstance().produceTimeSlices();

				final long startTime = System.currentTimeMillis();

				final TimeSliceGenerator timeSliceGenerator = new TimeSliceGenerator(generateStats, outputController);

				timeSliceGenerator.generate(basePercentageAllocation, timeSliceContainer);

				logInfo(LOG, "time slice generation took %d (ms)", (System.currentTimeMillis() - startTime));
			}
			else {
				generateStats.addPercentComplete(basePercentageAllocation);
			}

			if (filter.isIncludeGuidelines()) {
				// [2] Come up with a list of templates to generate rules for
				long startTime = System.currentTimeMillis();

				final List<TemplateUsageType> usageTypes = filter.getUsageTypes();
				final List<Integer> templateIDs = filter.getGuidelineTemplateIDs();
				if (usageTypes.isEmpty() && templateIDs.isEmpty()) {
					templateList.addAll(GuidelineTemplateManager.getInstance().getAllTemplates());
				}
				else {
					if (!usageTypes.isEmpty()) {
						templateList.addAll(GuidelineTemplateManager.getInstance().getTemplates(usageTypes));
					}

					if (!filter.getGuidelineTemplateIDs().isEmpty()) {
						for (int templateID : filter.getGuidelineTemplateIDs()) {
							final GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(templateID);
							if (template == null) {
								throw new RuleGenerationException("No guideline template of id " + templateID + "found!");
							}
							templateList.add(template);
						}
					}
				}

				logInfo(LOG, "Generating rules for %d templates", templateList.size());

				if (templateList.isEmpty()) {
					logInfo(LOG, "No guideline templates found!");
					generateStats.addPercentComplete(percentageAllocation);
				}
				else {
					generateGuidelinesFor(percentageAllocation, timeSliceContainer, templateList, filter);
				}

				logInfo(LOG, "Guideline rule generation took %d (ms)", (System.currentTimeMillis() - startTime));
			}
			else {
				generateStats.addPercentComplete(basePercentageAllocation * 3);
			}

			logInfo(LOG, "<<< RuleGenerator.generate");
		}
		catch (Exception ex) {
			LOG.error("Error in generation for " + filter, ex);
			throw new RuleGenerationException("Failed to complete deployment: " + ex.getMessage());
		}
		finally {
			generateStats.addPercentComplete(percentageAllocation - (basePercentageAllocation * 4));
			if (!templateList.isEmpty()) {
				mergeFilesPerUsageTypeIfNeeded(templateList);
			}
		}
	}

	private void generateGuidelinesFor(final int percentageAllocation, final TimeSliceContainer timeSliceContainer, final List<GridTemplate> templates, final GuidelineReportFilter filter)
			throws RuleGenerationException {
		logDebug(LOG, ">>> generateGuidelines: %d template(s), filter=%s", templates.size(), filter);
		final int totalGuidelineTemplateCount = templates.size();
		final int basePercentageAllocation = percentageAllocation / totalGuidelineTemplateCount;
		final CountDownLatch countDownLatch = new CountDownLatch(totalGuidelineTemplateCount);

		final List<Future<GridTemplate>> futures = new ArrayList<Future<GridTemplate>>();
		for (GridTemplate gridTemplate : templates) {
			futures.add(workExecutorService.submit(new GenerateGuidelineRulesWork(basePercentageAllocation, generateStats, gridTemplate, timeSliceContainer, filter, countDownLatch, this)));
			logInfo(LOG, "added guideline rule worker for %s", gridTemplate.getName());
		}

		logInfo(LOG, "waiting for %d guideline rule workers to complete...", futures.size());

		try {
			if (!countDownLatch.await(GUIDELINE_WORK_TIMEOUT_HOURS, TimeUnit.HOURS)) {
				logWarn(LOG, "Timed out while waiting for guideline rule workes to complete");
			}
			else {
				logInfo(LOG, "All guideline rule generation threads are done");
			}
			logDebug(LOG, "<<< generateGuidelines");
		}
		catch (InterruptedException e) {
			logWarn(LOG, "Interuppted out while waiting for guideline rule workes to complete");
		}
		finally {
			generateStats.addPercentComplete(percentageAllocation - (basePercentageAllocation * totalGuidelineTemplateCount));
			for (Future<?> future : futures) {
				if (!future.isDone()) {
					future.cancel(true);
				}
			}
		}
	}

	GenerateStats getGenerateStats() {
		return generateStats;
	}

	OutputController getOutputController() {
		return outputController;
	}

	private void mergeFilesPerUsageTypeIfNeeded(final List<GridTemplate> templates) throws RuleGenerationException {
		if (mergeFilesPerUsageType) {
			logInfo(LOG, "Merging rules files per usage type (# of templates=%d)...", templates.size());
			if (!templates.isEmpty()) {
				final Map<TemplateUsageType, List<GridTemplate>> templateMap = asUsageTemplateMap(templates);
				for (final Map.Entry<TemplateUsageType, List<GridTemplate>> entry : templateMap.entrySet()) {
					final File targetFile = outputController.getRuleFile(entry.getKey());
					logDebug(LOG, "Merging files for %s to %s...", entry.getKey(), targetFile);

					FileOutputStream targetFileOutputStream = null;
					FileChannel targetFileChannel = null;
					FileInputStream sourceFileInputStream = null;
					FileChannel sourceFileChannel = null;
					try {
						targetFileOutputStream = new FileOutputStream(targetFile, true);
						targetFileChannel = targetFileOutputStream.getChannel();
						for (final GridTemplate template : entry.getValue()) {
							final File sourceFile = new File(outputController.getDeployDir(), getRuleFilename(template) + ART_FILE_EXTENSION);
							if (sourceFile.exists()) {
								logInfo(LOG, "Appending %s...", sourceFile);
								try {
									sourceFileInputStream = new FileInputStream(sourceFile);
									sourceFileChannel = sourceFileInputStream.getChannel();

									// TODO GKIM 2015-01-26: transfer in small chunks
									// This can cause IOException with Invalid argument because the underlysing OS has
									// a limit on single file transfer (e.g., 2GB on 64-bit Linux)
									// Some consider this a bug. 
									// Regardless, we need to perform transfer in smaller chunks to get around this
									sourceFileChannel.transferTo(0, sourceFileChannel.size(), targetFileChannel);

									logInfo(LOG, "Appended %s to %s", sourceFile.getAbsolutePath(), targetFile.getAbsolutePath());

									sourceFileChannel.close();
									sourceFileChannel = null;
									sourceFileInputStream.close();
									sourceFileInputStream = null;

									sourceFile.delete();
									if (sourceFile.exists()) {
										sourceFile.deleteOnExit();
										logWarn(LOG, "%s was not removed; will be deleted on exist", sourceFile.getAbsolutePath());
									}
									else {
										logInfo(LOG, "Deleted %s", sourceFile.getAbsolutePath());
									}
								}
								catch (Exception e) {
									logWarn(LOG, e, "Unable to append the template rule file from %s", template);
								}
							}
							else {
								logInfo(LOG, "Skipping %s; it doesn't exist", sourceFile.getAbsolutePath());
							}
						}
					}
					catch (IOException e) {
						LOG.error("Failed to merge files for usage type " + entry.getKey(), e);
						throw new RuleGenerationException("Failed to merge rule files:" + e.getMessage());
					}
					finally {
						close(sourceFileChannel, sourceFileInputStream);
						close(targetFileChannel, targetFileOutputStream);
					}
				}
			}

		}
	}
}
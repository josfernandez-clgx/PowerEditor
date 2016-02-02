package com.mindbox.pe.server.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.ParameterTemplate;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.server.cache.DateSynonymManager;
import com.mindbox.pe.server.cache.GuidelineTemplateManager;
import com.mindbox.pe.server.cache.ParameterTemplateManager;
import com.mindbox.pe.server.model.TimeSliceContainer;

/**
 * ARTScript rule generator.
 * Responsible for handling deploy requests.
 * @since PowerEditor 1.0
 */
public class RuleGenerator {

	private final AeMapper aeMapper;
	private OutputController outputController;
	private int percentCompleteGuideline, percentCompleteParameter;
	private GenerateStats genGuidelineStats, genParameterStats, timeSliceStats;
	private final Logger logger;

	public RuleGenerator(OutputController outputController) throws RuleGenerationException {
		this.logger = Logger.getLogger(RuleGenerator.class);

		aeMapper = AeMapper.getGuidelineInstance();

		genGuidelineStats = new GenerateStats();
		genParameterStats = new GenerateStats();
		timeSliceStats = new GenerateStats();

		init_internal(outputController);
	}

	public synchronized void init(OutputController outputController) throws RuleGenerationException {
		init_internal(outputController);
	}

	private void init_internal(OutputController outputController) throws RuleGenerationException {
		aeMapper.reInit();
		this.outputController = outputController;

		genGuidelineStats.clear();
		genGuidelineStats.setDeployDir(getDeployDir());
		genParameterStats.clear();
		genParameterStats.setDeployDir(getDeployDir());

		timeSliceStats.clear();
		timeSliceStats.setDeployDir(getDeployDir());

		percentCompleteGuideline = 100;
		percentCompleteParameter = 100;

		GuidelineRuleGenerator.getInstance().init(outputController);

		ParameterGenerator.getInstance().init(outputController);

		TimeSliceGenerator.getInstance().init(outputController);
	}

	private final String getDeployDir() {
		return outputController.getDeployDir();
	}

	public List<GenerateStats> collectGenerateStats() {
		return Arrays.asList(new GenerateStats[] { getGuidelineStats(), getParameterStats(), getTimeSliceStats() });
	}

	public GenerateStats getGuidelineStats() {
		synchronized (genGuidelineStats) {
			updateGuidelineStats();
			return genGuidelineStats;
		}
	}

	public GenerateStats getParameterStats() {
		synchronized (genParameterStats) {
			updateParameterStats();
			return genParameterStats;
		}
	}

	public GenerateStats getTimeSliceStats() {
		synchronized (timeSliceStats) {
			updateTimeSliceStats();
			return timeSliceStats;
		}
	}

	public void updateTimeSliceStats() {
		synchronized (timeSliceStats) {
			timeSliceStats.setNumErrorsGenerated(TimeSliceGenerator.getInstance().getErrorCount());
			timeSliceStats.setPercentComplete(100);
		}
	}

	private void updateGuidelineStats() {
		synchronized (genGuidelineStats) {
			genGuidelineStats.setNumRulesGenerated(GuidelineRuleGenerator.getInstance().getGeneratedRuleCount());
			genGuidelineStats.setNumObjectsGenerated(GuidelineRuleGenerator.getInstance().getGeneratedObjectCount());
			genGuidelineStats.setNumErrorsGenerated(GuidelineRuleGenerator.getInstance().getErrorCount());
			genGuidelineStats.setPercentComplete(percentCompleteGuideline);
		}
	}

	private void updateParameterStats() {
		synchronized (genParameterStats) {
			genParameterStats.setNumObjectsGenerated(ParameterGenerator.getInstance().getGeneratedObjectCount());
			genParameterStats.setNumErrorsGenerated(ParameterGenerator.getInstance().getErrorCount());
			genParameterStats.setPercentComplete(percentCompleteParameter);
		}
	}

	public synchronized void generate(GuidelineReportFilter filter) throws RuleGenerationException {
		try {
			logger.info(">>> RuleGenerator.generate: " + filter);

			// generate time slices
			TimeSliceContainer timeSliceContainer = null;
			if (filter.isIncludeGuidelines() || filter.isIncludeParameters()) {
				logger.info("generating time slices...");
				timeSliceContainer = DateSynonymManager.getInstance().produceTimeSlices();
				TimeSliceGenerator.getInstance().generate(timeSliceContainer);
			}
			else {
				timeSliceStats.setPercentComplete(100);
			}
			timeSliceStats.setRunning(false);

			// process each guideline template
			if (filter.isIncludeGuidelines()) {
				percentCompleteGuideline = 0;
				logger.info("generating guideline rules");
				generateGuidelines(timeSliceContainer, filter);
			}
			else {
				genGuidelineStats.setPercentComplete(100);
			}
			genGuidelineStats.setRunning(false);

			if (filter.isIncludeParameters()) {
				percentCompleteParameter = 0;
				logger.info("generating parameters");
				generateParameters(filter);
			}
			else {
				genParameterStats.setPercentComplete(100);
			}
			genParameterStats.setRunning(false);
			logger.info("<<< RuleGenerator.generate");
		}
		catch (Exception ex) {
			logger.error("Error in generation for " + filter, ex);
			throw new RuleGenerationException("Failed to complete deployment: " + ex.getMessage());
		}
		finally {
			outputController.closeRuleWriters();
			outputController.closeParameterWriters();
		}
	}

	private void generateGuidelines(TimeSliceContainer timeSliceContainer, GuidelineReportFilter filter) throws RuleGenerationException {
		logger.debug(">>> generateGuidelines: " + filter);
		long startTime = System.currentTimeMillis();
		List<GridTemplate> templateList = new LinkedList<GridTemplate>();
		List<TemplateUsageType> usageTypes = filter.getUsageTypes();
		List<Integer> templateIDs = filter.getGuidelineTemplateIDs();
		if (usageTypes.isEmpty() && templateIDs.isEmpty()) {
			templateList.addAll(GuidelineTemplateManager.getInstance().getAllTemplates());
		}
		else {
			if (!usageTypes.isEmpty()) {
				templateList.addAll(GuidelineTemplateManager.getInstance().getTemplates(usageTypes));
			}
			
			if (!filter.getGuidelineTemplateIDs().isEmpty()) {				
				for (int templateID : filter.getGuidelineTemplateIDs()) {
					GridTemplate template = GuidelineTemplateManager.getInstance().getTemplate(templateID);
					if (template == null) {
						throw new RuleGenerationException("No guideline template of id " + templateID + "found!");
					}
					templateList.add(template);
				}
			}
		}

		if (templateList.isEmpty()) {
			logger.info("No guideline templates found!");
			percentCompleteGuideline = 100;
		}
		else {
			generateGuidelinesFor(timeSliceContainer, templateList, filter);
		}
		logger.info("Time to generate guidelines: " + (System.currentTimeMillis() - startTime)/1000.0 + " (sec)");
	}

	private void generateGuidelinesFor(TimeSliceContainer timeSliceContainer, List<GridTemplate> templates, GuidelineReportFilter filter)
			throws RuleGenerationException {
		logger.debug(">>> generateGuidelines: " + templates.size() + " template(s), filter=" + filter);
		int totalMetaCount = templates.size();
		int counter = 0;
		for (Iterator<GridTemplate> iterator = templates.iterator(); iterator.hasNext();) {
			GridTemplate gridtemplate = iterator.next();
			TemplateUsageType usageType = gridtemplate.getUsageType();
			logger.info("generate: processing guideline template: " + gridtemplate.getID() + " type=" + usageType);

			if (usageType != null) {
				// generate with optimization if optimize flag is on
				if (filter.isOptimizeRuleGeneration()) {
					GuidelineRuleGenerator.getInstance().generateOptimized(timeSliceContainer, gridtemplate, filter);
				}
				else {
					GuidelineRuleGenerator.getInstance().generate(timeSliceContainer, gridtemplate, filter);
				}
			} // usageType sanity check

			++counter;
			percentCompleteGuideline = (counter * 100) / totalMetaCount;
		}
		updateGuidelineStats();

		logger.info("generate: all guideline templates are processsed. Closing guideline streams...");
		GuidelineRuleGenerator.getInstance().writeAll();
	}

	private void generateParameters(GuidelineReportFilter filter) throws RuleGenerationException {
		ParameterTemplateManager paramTemplateManager = ParameterTemplateManager.getInstance();
		List<ParameterTemplate> paramTemplates = null;
		if (filter.getParameterTemplateIDs().isEmpty()) {
			paramTemplates = paramTemplateManager.getTemplates();
		}
		else {
			paramTemplates = new ArrayList<ParameterTemplate>();
			for (int templateID : filter.getParameterTemplateIDs()) {
				ParameterTemplate parameterTemplate = paramTemplateManager.getTemplate(templateID);
				if (parameterTemplate == null) {
					throw new RuleGenerationException("No parameter template of id " + templateID + " found!");
				}
				paramTemplates.add(parameterTemplate);
			}
		}

		if (paramTemplates == null || paramTemplates.isEmpty()) {
			logger.info("No parameter templates specified!");
			percentCompleteGuideline = 100;
		}
		else {
			int totalMetaCount = paramTemplates.size();
			int counter = 0;
			for (Iterator<ParameterTemplate> iter = paramTemplates.iterator(); iter.hasNext();) {
				ParameterTemplate element = iter.next();
				logger.info("generate: processing parameter template - " + element);
				ParameterGenerator.getInstance().generate(element, filter);

				++counter;
				percentCompleteParameter = (counter * 100) / totalMetaCount;
			}
			ParameterGenerator.getInstance().writeAll();
		}
		updateParameterStats();

		logger.info("generate: all parameter templates are processed");
	}

}
package com.mindbox.pe.server.generator;

import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.server.model.TimeSliceContainer;

public interface GuidelineRuleGenerator extends GeneratorErrorContainer {

	void generateOptimized(int percentageAllocation, TimeSliceContainer timeSliceContainer, GuidelineReportFilter filter) throws RuleGenerationException;

}
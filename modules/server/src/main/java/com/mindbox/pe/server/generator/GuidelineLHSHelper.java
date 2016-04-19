package com.mindbox.pe.server.generator;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.generator.rule.AttributePattern;
import com.mindbox.pe.server.generator.rule.CellValueValueSlot;
import com.mindbox.pe.server.generator.rule.ColumnReferencePatternValueSlot;
import com.mindbox.pe.server.generator.rule.ContextElementPatternValueSlot;
import com.mindbox.pe.server.generator.rule.FunctionCallPattern;
import com.mindbox.pe.server.generator.rule.GuidelineRule;
import com.mindbox.pe.server.generator.rule.LHSPattern;
import com.mindbox.pe.server.generator.rule.LHSPatternList;
import com.mindbox.pe.server.generator.rule.ObjectPattern;
import com.mindbox.pe.server.generator.rule.PatternValueSlot;
import com.mindbox.pe.server.generator.rule.ValueSlot;
import com.mindbox.pe.server.generator.value.LHSValueHelperFactory;
import com.mindbox.pe.server.generator.value.ValueAndComment;
import com.mindbox.pe.server.model.TimeSlice;
import com.mindbox.pe.xsd.config.RuleGenerationLHS.Pattern;

/**
 * Responsible for generating LHS of a guideline rule.
 * This is thread-safe.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
final class GuidelineLHSHelper extends AbstractGuidelineHelper {

	GuidelineLHSHelper(final BufferedGenerator bufferedGenerator) {
		super(bufferedGenerator);
	}

	synchronized void clear() {
		ruleParams = null;
	}

	public synchronized void generateLHS(GuidelineRule guidelineRule, GuidelineGenerateParams ruleParams, TimeSlice[] timeSlices, GenericEntityType messageContextType, int messageContextEntityID)
			throws RuleGenerationException {
		this.ruleParams = ruleParams;
		write(guidelineRule.getLHSPatternList(), LHSPatternList.TYPE_AND, ruleParams.getUsage(), timeSlices, messageContextType, messageContextEntityID);
	}

	private boolean hasOnlyEmptyAttributePatterns(ObjectPattern objectPattern) {
		for (int i = 0; i < objectPattern.size(); i++) {
			AttributePattern attributePattern = objectPattern.get(i);
			if (!attributePattern.canBeSkipped() || !isEmptyColumnValueSlotPattern(attributePattern)) {
				return false;
			}
		}
		return true;
	}

	private boolean hasOnlyEmptyPatterns(LHSPatternList patternList) {
		if (patternList.isEmpty()) return true;
		for (int i = 0; i < patternList.size(); ++i) {
			LHSPattern element = patternList.get(i);
			if (element instanceof LHSPatternList) {
				boolean result = hasOnlyEmptyPatterns((LHSPatternList) element);
				if (!result) return false;
			}
			else if (element instanceof FunctionCallPattern) {
				return false;
			}
			else if (element instanceof ObjectPattern) {
				// TT 2196: PE doesn't generate pattern for Not at the class level
				if (!((ObjectPattern) element).canBeSkipped()) return false;
				boolean result = hasOnlyEmptyAttributePatterns((ObjectPattern) element);
				if (!result) return false;
			}
		}
		return true;
	}

	private boolean isEmptyColumnValueSlotPattern(AttributePattern attributePattern) {
		if (attributePattern.hasValueSlot()) {
			ValueSlot valueSlot = attributePattern.getValueSlot();
			if (valueSlot instanceof CellValueValueSlot || valueSlot instanceof ColumnReferencePatternValueSlot) {
				return ruleParams.isEmptyValue(valueSlot);
			}
		}
		return false;
	}

	private boolean skipEmptyPattern(ObjectPattern objectPattern) {
		// TT 2085: if objectPattern has no attribute patterns, it came from class reference in RHS, do not skip these
		if (!objectPattern.canBeSkipped()) return false;

		// If configured to generate empty column reference condition. 
		final Pattern pattern = ConfigurationManager.getInstance().getRuleGenerationConfigHelper(ruleParams.getUsage()).getEmptyPatternConfig();
		if (pattern == null || ConfigUtil.isPatternOn(pattern)) {
			return false;
		}

		// Skip if configured to not generate and it only contains empty column reference attribute patterns 		
		for (int i = 0; i < objectPattern.size(); i++) {
			AttributePattern attributePattern = objectPattern.get(i);
			if (!isEmptyColumnValueSlotPattern(attributePattern)) {
				return false;
			}
		}
		// skip since all attribute patterns are empty
		return true;
	}

	private void write(FunctionCallPattern functionCallPattern) throws RuleGenerationException {
		logger.debug(">>> write(FunctionCallPattern): " + functionCallPattern);

		bufferedGenerator.openParan();
		bufferedGenerator.print("test ");
		bufferedGenerator.openParan();
		bufferedGenerator.print(functionCallPattern.getFunctionName());
		bufferedGenerator.nextLineIndent();

		logger.debug("    write(FunctionCallPattern): param size = " + functionCallPattern.argSize());

		processFunctionCallArgs(functionCallPattern, false);

		bufferedGenerator.closeParan();
		bufferedGenerator.closeParan();
		bufferedGenerator.nextLineOutdent();

		logger.info("<<< write(FunctionCallPattern)");
	}

	private void write(LHSPatternList patternList, int parentType, TemplateUsageType usageType, TimeSlice[] timeSlices, GenericEntityType messageContextType, int messageContextEntityID)
			throws RuleGenerationException {
		logger.debug(">>> write(CompoundLHSElement): " + patternList + ", parentType=" + parentType);
		logger.debug("    write(CompoundLHSElement): ruleParams=" + ruleParams);

		// do not generate pattern if element contains no child element
		if (patternList.isEmpty()) {
			logger.warn("   write(CompoundLHSElement): no pattern generated for " + patternList + ": it is empty");
			return;
		}
		else if (hasOnlyEmptyPatterns(patternList)) {
			logger.warn("   write(CompoundLHSElement): no pattern generated for " + patternList + ": it only contains empty patterns.");
			return;
		}

		boolean isNested = true;
		switch (patternList.getType()) {
		case LHSPatternList.TYPE_AND:
			if (parentType == LHSPatternList.TYPE_OR || parentType == LHSPatternList.TYPE_NOT) {
				bufferedGenerator.openParan();
				bufferedGenerator.print("AND");
			}
			else {
				isNested = false;
			}
			break;
		case LHSPatternList.TYPE_OR:
			bufferedGenerator.openParan();
			bufferedGenerator.print("OR");
			break;
		case LHSPatternList.TYPE_NOT:
			bufferedGenerator.openParan();
			bufferedGenerator.print("NOT");
			break;
		default:
			logger.warn("Invalid compound element type in  " + patternList);
			throw new RuleGenerationException("Invalid element type in " + patternList);
		}

		if (isNested) bufferedGenerator.nextLineIndent();

		writeLHSPatterns(patternList, usageType, timeSlices, messageContextType, messageContextEntityID);

		if (isNested) {
			bufferedGenerator.nextLineOutdent();
			bufferedGenerator.closeParan();
			bufferedGenerator.nextLine();
		}
	}

	private void write(ObjectPattern objectPattern, TimeSlice[] timeSlices, GenericEntityType messageContextType, int messageContextEntityID, boolean skipEmptyPattern) throws RuleGenerationException {
		logger.debug(">>> write(ObjectPattern) - " + objectPattern);

		// [1] write class instance line
		bufferedGenerator.openParan();
		bufferedGenerator.print("object ");
		bufferedGenerator.print(objectPattern.getVariableName());
		bufferedGenerator.nextLineIndent();
		bufferedGenerator.openParan();
		bufferedGenerator.print("instance-of ");
		bufferedGenerator.print(objectPattern.getClassName());
		bufferedGenerator.closeParan();
		bufferedGenerator.nextLine();

		// [2] add lineage pattern
		writeLineagePatternIfMatch(objectPattern.getClassName(), ruleParams.getUsage());

		// [3] write attribute patterns
		writeAttributePatterns(objectPattern, timeSlices, messageContextType, messageContextEntityID, skipEmptyPattern);

		bufferedGenerator.closeParan();
		bufferedGenerator.nextLineOutdent();
	}

	private void writeAttributePatterns(ObjectPattern objectPattern, TimeSlice[] timeSlices, GenericEntityType messageContextType, int messageContextEntityID, boolean skipEmptyPattern)
			throws RuleGenerationException {
		for (int i = 0; i < objectPattern.size(); i++) {
			if (writeSingleAttributePattern(objectPattern.get(i), timeSlices, messageContextType, messageContextEntityID, skipEmptyPattern)) {
				if (i < objectPattern.size() - 1) {
					bufferedGenerator.nextLine();
				}
			}
		}
	}

	private void writeLHSPatterns(LHSPatternList patternList, TemplateUsageType usageType, TimeSlice[] timeSlices, GenericEntityType messageContextType, int messageContextEntityID)
			throws RuleGenerationException {
		logger.debug(">>> writeElements(LHSPatternList): " + patternList + ", size=" + patternList.size());

		final Pattern emptyPatternConfig = ConfigurationManager.getInstance().getRuleGenerationConfigHelper(usageType).getEmptyPatternConfig();
		boolean skipEmptyPattern = emptyPatternConfig != null && !ConfigUtil.isPatternOn(emptyPatternConfig);
		for (int i = 0; i < patternList.size(); ++i) {
			boolean isNotLast = i < patternList.size() - 1;
			LHSPattern element = patternList.get(i);
			if (element instanceof LHSPatternList) {
				write((LHSPatternList) element, patternList.getType(), usageType, timeSlices, messageContextType, messageContextEntityID);
				if (isNotLast) bufferedGenerator.nextLine();
			}
			else if (element instanceof FunctionCallPattern) {
				write((FunctionCallPattern) element);
				if (isNotLast) bufferedGenerator.nextLine();
			}
			else if (element instanceof ObjectPattern) {
				// Skip if configured to not generate it and it only contains empty column reference condition. 
				ObjectPattern objectPattern = (ObjectPattern) element;
				if (skipEmptyPattern(objectPattern)) {
					logger.warn("   write(LHSPatternList ): no pattern generated for " + objectPattern + ": empty conditions and configured to not generate it");
				}
				else {
					write(objectPattern, timeSlices, messageContextType, messageContextEntityID, skipEmptyPattern);
					if (isNotLast) bufferedGenerator.nextLine();
				}
			}
			else {
				logger.warn("*** Ignored unknown LHS element: " + element + " at " + i + " in " + patternList);
			}
		}
	}

	private boolean writeSingleAttributePattern(AttributePattern attributePattern, TimeSlice[] timeSlices, GenericEntityType messageContextType, int messageContextEntityID, boolean skipEmptyPattern)
			throws RuleGenerationException {
		logger.debug("--> writeSingleAttributePattern: " + attributePattern + "," + skipEmptyPattern);
		// Suppress generation if configured not to generate it and this is an empty condition
		if (skipEmptyPattern && attributePattern.canBeSkipped() && isEmptyColumnValueSlotPattern(attributePattern)) {
			logger.info("    writeSingleAttributePattern: " + attributePattern + " suppress generation; empty condition and configured to not generate it");
			return false;
		}
		else if (attributePattern.hasValueSlot()) {
			if (!(attributePattern.getValueSlot() instanceof PatternValueSlot)) {
				throw new RuleGenerationException("Invalid attribute pattern; has incorrect value slot: " + attributePattern);
			}
			PatternValueSlot patternValueSlot = (PatternValueSlot) attributePattern.getValueSlot();

			// Suppress generation if it's context element and context is empty
			if (patternValueSlot instanceof ContextElementPatternValueSlot) {
				if (!ruleParams.hasGenericCategoryContext(((ContextElementPatternValueSlot) patternValueSlot).getGenericEntityType())
						&& !ruleParams.hasGenericEntityContext(((ContextElementPatternValueSlot) patternValueSlot).getGenericEntityType())) {
					return false;
				}
			}

			bufferedGenerator.openParan();
			bufferedGenerator.print(attributePattern.getAttributeName());
			bufferedGenerator.print(' ');

			// write value, including variable
			ValueAndComment valueAndComment = null;
			try {
				valueAndComment = LHSValueHelperFactory.getLHSValueHelper(ruleParams.getUsage()).generateValue(
						patternValueSlot,
						ruleParams,
						attributePattern.getVariableName(),
						timeSlices,
						messageContextType,
						messageContextEntityID);
				bufferedGenerator.print(valueAndComment.getValue());
			}
			catch (RuleGenerationException ex) {
				bufferedGenerator.print("\"***ERROR: " + ex.getMessage() + "***\"");
				throw ex;
			}
			catch (Exception ex) {
				logger.error("Failed to write single attribute attern: " + attributePattern, ex);
				bufferedGenerator.print("\"***ERROR: " + ex.getMessage() + "***\"");
				throw new RuleGenerationException(ex.getMessage());
			}
			finally {
				bufferedGenerator.closeParan();
				if (valueAndComment != null && valueAndComment.getComment() != null) {
					bufferedGenerator.nextLine();
					bufferedGenerator.print(";;;  ");
					bufferedGenerator.print(valueAndComment.getComment());
					bufferedGenerator.nextLine();
				}
			}
			return true;
		}
		else {
			bufferedGenerator.openParan();
			bufferedGenerator.print(attributePattern.getAttributeName());
			bufferedGenerator.print(" ");
			bufferedGenerator.print(attributePattern.getVariableName());
			if (!UtilBase.isEmpty(attributePattern.getValueText())) {
				bufferedGenerator.print(" ");
				bufferedGenerator.print(attributePattern.getValueText());
			}
			bufferedGenerator.closeParan();
			return true;
		}
	}

}
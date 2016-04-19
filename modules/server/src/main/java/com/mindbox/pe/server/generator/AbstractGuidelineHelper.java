package com.mindbox.pe.server.generator;

import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.LineagePatternConfigHelper;
import com.mindbox.pe.server.generator.rule.FunctionArgument;
import com.mindbox.pe.server.generator.rule.FunctionCallPattern;
import com.mindbox.pe.server.generator.rule.StaticFunctionArgument;
import com.mindbox.pe.server.generator.rule.ValueSlot;
import com.mindbox.pe.server.generator.value.RHSArgumentHelper;
import com.mindbox.pe.xsd.config.RuleGenerationLHS;
import com.mindbox.pe.xsd.config.RuleGenerationLHS.Pattern;

/**
 * Abstract guideline rule helper.
 * 
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
abstract class AbstractGuidelineHelper {

	final Logger logger;
	final BufferedGenerator bufferedGenerator;
	GuidelineGenerateParams ruleParams = null;

	AbstractGuidelineHelper(final BufferedGenerator bufferedGenerator) {
		this.bufferedGenerator = bufferedGenerator;
		logger = Logger.getLogger(getClass());
	}

	/**
	 * Clears invariants. Call this before calling other methods in this.
	 */
	synchronized void clear() {
		ruleParams = null;
	}

	synchronized final void processFunctionCallArgs(FunctionCallPattern functionCallPattern, boolean keywords) throws RuleGenerationException {
		if (functionCallPattern.isEmpty()) {
			return;
		}

		logger.debug(">>> processActionArgs: " + functionCallPattern + ", " + keywords);

		boolean isOddNumberedArg = true;
		FunctionArgument currArg = null;
		boolean isLast = false;
		for (int i = 0; i < functionCallPattern.argSize(); i++) {
			currArg = functionCallPattern.getArgAt(i);
			isLast = i >= functionCallPattern.argSize() - 1;

			if (currArg instanceof FunctionCallPattern) {
				bufferedGenerator.openParan();
				bufferedGenerator.print(((FunctionCallPattern) currArg).getFunctionName());
				bufferedGenerator.nextLineIndent();

				processFunctionCallArgs((FunctionCallPattern) currArg, false);

				bufferedGenerator.outdent();
				bufferedGenerator.closeParan();
				isOddNumberedArg = true;
				if (!isLast) {
					bufferedGenerator.nextLine();
				}
			}
			else if (currArg instanceof ValueSlot) {
				bufferedGenerator.print(' ');
				bufferedGenerator.print(new RHSArgumentHelper().generateValue((ValueSlot) currArg, ruleParams));
				isOddNumberedArg = true;
				if (!isLast) {
					bufferedGenerator.nextLine();
				}
			}
			else if (currArg instanceof StaticFunctionArgument) {
				if (keywords && isOddNumberedArg) {
					isOddNumberedArg = false;
					bufferedGenerator.print(":");
					bufferedGenerator.print(((StaticFunctionArgument) currArg).getValue());
				}
				else {
					isOddNumberedArg = true;
					bufferedGenerator.print(" ");
					bufferedGenerator.print(((StaticFunctionArgument) currArg).getValue());
					if (!isLast) {
						bufferedGenerator.nextLine();
					}
				}
			}
			else {
				bufferedGenerator.reportError("Invalid argument type: " + currArg + " (" + currArg.getClass() + ')');
			}
		}
	}

	synchronized final void writeLineagePatternIfMatch(final String className, final TemplateUsageType usageType) {
		final LineagePatternConfigHelper lineagePatternSet = ConfigurationManager.getInstance().getRuleGenerationConfigHelper(usageType).getLineagePatternConfigSet();
		if (lineagePatternSet.size() == 0) {
			return;
		}

		for (final String prefix : lineagePatternSet.getPrefix()) {
			if (className.toUpperCase().startsWith(prefix)) {
				List<RuleGenerationLHS.Pattern> configs = lineagePatternSet.getLineagePatternConfigs(prefix);
				for (Pattern pattern : configs) {
					bufferedGenerator.print(pattern.getText());
					bufferedGenerator.nextLine();
				}
			}
		}
	}

}
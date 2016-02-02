package com.mindbox.pe.server.generator;

import org.apache.log4j.Logger;

import com.mindbox.pe.server.generator.rule.FunctionArgument;
import com.mindbox.pe.server.generator.rule.FunctionCallPattern;
import com.mindbox.pe.server.generator.rule.StaticFunctionArgument;
import com.mindbox.pe.server.generator.rule.ValueSlot;
import com.mindbox.pe.server.generator.value.RHSArgumentHelper;

/**
 * Abstract guideline rule helper.
 * 
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
abstract class AbstractGuidelineHelper {

	final Logger logger;
	final GuidelineRuleGenerator ruleGenerator;
	GuidelineGenerateParams ruleParams = null;

	AbstractGuidelineHelper(GuidelineRuleGenerator ruleGenerator) {
		this.ruleGenerator = ruleGenerator;
		logger = Logger.getLogger(getClass());
	}

	/**
	 * Clears invariants. Call this before calling other methods in this.
	 */
	synchronized void clear() {
		ruleParams = null;
	}

	protected synchronized void processFunctionCallArgs(FunctionCallPattern functionCallPattern, boolean keywords) throws RuleGenerationException {
		if (functionCallPattern.isEmpty()) return;
		logger.debug(">>> processActionArgs: " + functionCallPattern + ", " + keywords);

		boolean isOddNumberedArg = true;
		FunctionArgument currArg = null;
		boolean isLast = false;
		for (int i = 0; i < functionCallPattern.argSize(); i++) {
			currArg = functionCallPattern.getArgAt(i);
			isLast = i >= functionCallPattern.argSize() - 1;

			if (currArg instanceof FunctionCallPattern) {
				ruleGenerator.openParan();
				ruleGenerator.print(((FunctionCallPattern) currArg).getFunctionName());
				ruleGenerator.nextLineIndent();

				processFunctionCallArgs((FunctionCallPattern) currArg, false);

				ruleGenerator.outdent();
				ruleGenerator.closeParan();
				isOddNumberedArg = true;
				if (!isLast) ruleGenerator.nextLine();
			}
			else if (currArg instanceof ValueSlot) {
				ruleGenerator.print(' ');
				ruleGenerator.print(new RHSArgumentHelper().generateValue((ValueSlot) currArg, ruleParams));
				isOddNumberedArg = true;
				if (!isLast) ruleGenerator.nextLine();
			}
			else if (currArg instanceof StaticFunctionArgument) {
				if (keywords && isOddNumberedArg) {
					isOddNumberedArg = false;
					ruleGenerator.print(":");
					ruleGenerator.print(((StaticFunctionArgument) currArg).getValue());
				}
				else {
					isOddNumberedArg = true;
					ruleGenerator.print(" ");
					ruleGenerator.print(((StaticFunctionArgument) currArg).getValue());
					if (!isLast) ruleGenerator.nextLine();
				}
			}
			else {
				ruleGenerator.reportError("Invalid argument type: " + currArg + " (" + currArg.getClass() + ')');
			}
		}
	}

}
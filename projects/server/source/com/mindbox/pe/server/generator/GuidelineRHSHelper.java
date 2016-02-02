package com.mindbox.pe.server.generator;

import com.mindbox.pe.server.generator.rule.FunctionCallPattern;
import com.mindbox.pe.server.generator.rule.GuidelineRule;
import com.mindbox.pe.server.parser.jtb.message.ParseException;


/**
 * Responsible for generating RHS of a guideline rule.
 * This is thread-safe.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
final class GuidelineRHSHelper extends AbstractGuidelineHelper {

	GuidelineRHSHelper(GuidelineRuleGenerator ruleGenerator) {
		super(ruleGenerator);
	}

	private String generateMessageText(int entityID) throws ParseException, RuleGenerationException {
		logger.debug(">>> generateMessageText");
		try {
			String msg = RuleGeneratorHelper.generateMessageText(ruleParams, entityID);
			return msg;
		}
		catch (Exception ex) {
			logger.error("Failed to process message for " + ruleParams + " and " + entityID, ex);
			ruleGenerator.reportError("Failed to process message for " + ruleParams + ": " + ex.getMessage());
			return "***MESSAGE-PROCESSING-ERROR: " + ex.getMessage();
		}
	}

	synchronized void generateRHS(GuidelineRule guidelineRule, GuidelineGenerateParams ruleParams, int messageContextEntityID) throws RuleGenerationException {
		FunctionCallPattern functionCallPattern = guidelineRule.getRHSFunctionCall();
		logger.debug(">>> generateRHS: " + ruleParams + " ("+ruleParams.getClass().getName()+")" + ", " + messageContextEntityID);
		this.ruleParams = ruleParams;
		
		ruleGenerator.openParan();

		ruleGenerator.print(functionCallPattern.getFunctionName());
		ruleGenerator.nextLineIndent();
		ruleGenerator.print(":message ");

		try {
			ruleGenerator.print(generateMessageText(messageContextEntityID));
		}
		catch (ParseException e) {
			ruleGenerator.print("\"*** MESSAGE SYNTAX ERROR ***\"");
			logger.error("Message Parse error", e);
			ruleGenerator.reportError("Message for " + ruleParams + " has syntax error: " + e.getMessage());
		}
		ruleGenerator.nextLine();
		
		// write arguments of the RHS action function call
		processFunctionCallArgs(functionCallPattern, true);
		
		ruleGenerator.outdent();
		ruleGenerator.closeParan();
		ruleGenerator.nextLineOutdent();

		logger.debug("<<< writeRHS");
	}
}
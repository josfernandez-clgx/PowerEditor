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

	GuidelineRHSHelper(BufferedGenerator bufferedGenerator) {
		super(bufferedGenerator);
	}

	private String generateMessageText(int entityID) throws ParseException, RuleGenerationException {
		logger.debug(">>> generateMessageText");
		try {
			return RuleGeneratorHelper.generateMessageText(ruleParams, entityID);
		}
		catch (Exception ex) {
			logger.error("Failed to process message for " + ruleParams + " and " + entityID, ex);
			bufferedGenerator.reportError("Failed to process message for " + ruleParams + ": " + ex.getMessage());
			return "***MESSAGE-PROCESSING-ERROR: " + ex.getMessage();
		}
	}

	synchronized void generateRHS(GuidelineRule guidelineRule, GuidelineGenerateParams ruleParams, int messageContextEntityID) throws RuleGenerationException {
		FunctionCallPattern functionCallPattern = guidelineRule.getRHSFunctionCall();
		logger.debug(">>> generateRHS: " + ruleParams + " (" + ruleParams.getClass().getName() + ")" + ", " + messageContextEntityID);
		this.ruleParams = ruleParams;

		bufferedGenerator.openParan();

		bufferedGenerator.print(functionCallPattern.getFunctionName());
		bufferedGenerator.nextLineIndent();
		bufferedGenerator.print(":message ");

		try {
			bufferedGenerator.print(generateMessageText(messageContextEntityID));
		}
		catch (ParseException e) {
			bufferedGenerator.print("\"*** MESSAGE SYNTAX ERROR ***\"");
			logger.error("Message Parse error", e);
			bufferedGenerator.reportError("Message for " + ruleParams + " has syntax error: " + e.getMessage());
		}
		bufferedGenerator.nextLine();

		// write arguments of the RHS action function call
		processFunctionCallArgs(functionCallPattern, true);

		bufferedGenerator.outdent();
		bufferedGenerator.closeParan();
		bufferedGenerator.nextLineOutdent();

		logger.debug("<<< writeRHS");
	}
}
package com.mindbox.pe.common.config;

import static com.mindbox.pe.model.Constants.DEFAULT_CONDITIONAL_DELIMITER;
import static com.mindbox.pe.model.Constants.DEFAULT_CONDITIONAL_FINAL_DELIMITER;

import java.io.Serializable;

/**
 * MessageConfiguration is used to configure the generation of rule messages.
 * There is one of these for each RuleGenerationConfiguration object (including
 * usageType specific objects.)
 * Also, if message configuration is overriden at the message or column level,
 * another one of these gets created.
 * 
 * This is a list of ColumnMessageFragmentDigest objects.  We are essentially
 * reusing this type since these object are largely comprised of configuration information.
 * However, the text attribute of the ColumnMessageFragmentDigest object is not
 * configuration information, and should not be referenced in this context.
 * 
 * @author Beth Marvel
 * @author Mindbox
 */
public class MessageConfiguration extends ColumnMessageConfigSet implements Serializable {

	private static final long serialVersionUID = 2004071124000000L;


	private String conditionalDelimiter;
	private String conditionalFinalDelimiter;

	public MessageConfiguration() {
		super();
	}

	/**
	 * Copy the MessageConfiguration list.  Does not copy of the ColumnMessageFragmentDigest,
	 * objects in the list, since most are replaced when a new one is parsed.
	 * However, a copy IS done of the default ColumnMessageFragmentDigest, since
	 * this object is not replaced if a new one is parsed.
	 * @param source source
	 */
	public MessageConfiguration(MessageConfiguration source) {
		super(source);
		this.conditionalDelimiter = source.conditionalDelimiter;
		this.conditionalFinalDelimiter = source.conditionalFinalDelimiter;
	}

	public final String getConditionalDelimiter() {
		return conditionalDelimiter;
	}

	public final String getConditionalFinalDelimiter() {
		return conditionalFinalDelimiter;
	}


	public final void setConditionalDelimiter(String conditionalDelimiter) {
		this.conditionalDelimiter = (conditionalDelimiter == null ? DEFAULT_CONDITIONAL_DELIMITER : conditionalDelimiter);
	}

	public final void setConditionalFinalDelimiter(String conditionalFinalDelimiter) {
		this.conditionalFinalDelimiter = (conditionalFinalDelimiter == null ? DEFAULT_CONDITIONAL_FINAL_DELIMITER : conditionalFinalDelimiter);
	}

}

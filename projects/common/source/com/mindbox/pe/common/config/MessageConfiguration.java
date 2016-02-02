package com.mindbox.pe.common.config;

import java.io.Serializable;

import com.mindbox.pe.model.AbstractMessageKeyList;
import com.mindbox.pe.model.ColumnMessageFragmentDigest;

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
public class MessageConfiguration extends AbstractMessageKeyList implements Serializable {

	private static final long serialVersionUID = 2004071124000000L;

	private static final String DEFAULT_ENUM_DELIM = ", ";
	private static final String DEFAULT_ENUM_FINAL_DELIM = " or ";
	private static final String DEFAULT_ENUM_PREFIX = "";
	private static final String DEFAULT_RANGE_STYLE = RANGE_VERBOSE_KEY;

	public static final String DEFAULT_CONDITIONAL_DELIMITER = ", ";
	public static final String DEFAULT_CONDITIONAL_FINAL_DELIMITER = ", ";

	private String conditionalDelimiter;
	private String conditionalFinalDelimiter;

	/**
	 * Default Constructor.  Adds default item.
	 */
	public MessageConfiguration() {
		// add default values.
		// Note that the object here is unusual in that it has both enum and range properties set.
		ColumnMessageFragmentDigest msg = new ColumnMessageFragmentDigest(TYPE_DEFAULT_KEY, DEFAULT_ENUM_DELIM, DEFAULT_ENUM_FINAL_DELIM,
				DEFAULT_ENUM_PREFIX);
		msg.setRangeStyle(DEFAULT_RANGE_STYLE); // does a simple set on the messageFragment object
		this.put(TYPE_DEFAULT_KEY, msg);
	}

	/**
	 * Copy the MessageConfiguration list.  Does not copy of the ColumnMessageFragmentDigest,
	 * objects in the list, since most are replaced when a new one is parsed.
	 * However, a copy IS done of the default ColumnMessageFragmentDigest, since
	 * this object is not replaced if a new one is parsed.
	 */
	public MessageConfiguration(MessageConfiguration source) {
		this.conditionalDelimiter = source.conditionalDelimiter;
		this.conditionalFinalDelimiter=source.conditionalFinalDelimiter;
		putAll(source);
		ColumnMessageFragmentDigest oldDefault = (ColumnMessageFragmentDigest) source.get(TYPE_DEFAULT_KEY);
		put(TYPE_DEFAULT_KEY, new ColumnMessageFragmentDigest(oldDefault));
	}
	
	/**
	 * @return Returns the conditionalDelimiter.
	 */
	public final String getConditionalDelimiter() {
		return conditionalDelimiter;
	}

	/**
	 * @param conditionalDelimiter The conditionalDelimiter to set.
	 */
	public final void setConditionalDelimiter(String conditionalDelimiter) {
		this.conditionalDelimiter = (conditionalDelimiter == null ? DEFAULT_CONDITIONAL_DELIMITER : conditionalDelimiter);
	}

	/**
	 * @return Returns the conditionalFinalDelimiter.
	 */
	public final String getConditionalFinalDelimiter() {
		return conditionalFinalDelimiter;
	}

	/**
	 * @param conditionalFinalDelimiter The conditionalFinalDelimiter to set.
	 */
	public final void setConditionalFinalDelimiter(String conditionalFinalDelimiter) {
		this.conditionalFinalDelimiter = (conditionalFinalDelimiter == null ? DEFAULT_CONDITIONAL_FINAL_DELIMITER : conditionalFinalDelimiter);
		;
	}

	/* ==== RANGE SPECIFIC STUFF ====== */

	/** 
	 * External method for checking RangeStyle type
	 */
	public boolean isRangeStyleVerbose() {
		return getRangeStyle().equals(RANGE_VERBOSE_KEY);
	}

	/** 
	 * External method for checking RangeStyle type
	 */
	public boolean isRangeStyleSymbolic() {
		return getRangeStyle().equals(RANGE_SYMBOLIC_KEY);
	}

	/** 
	 * External method for checking RangeStyle type
	 */
	public boolean isRangeStyleBracketed() {
		return getRangeStyle().equals(RANGE_BRACKETED_KEY);
	}

	/**
	 * Assumes checkRangeStyle is called outside of this - no error checking is done here.
	 * @param rangeStyle valid range style (verbose, bracketed, symbolic).
	 */
	public void setRangeStyle(String rangeStyle) {
		// note, we need to make a new range object here so we don't overwrite
		// copies that are being pointed to in multiple MessageConfigs
		ColumnMessageFragmentDigest rangeConfig = new ColumnMessageFragmentDigest();
		rangeConfig.setRangeStyle(rangeStyle);
		rangeConfig.setType(TYPE_RANGE_KEY);
		addRangeObject(rangeConfig); // removes any prior existing ones

	}

	/**
	 * Returns the rangeStyle for this MessageConfig.
	 * This method isn't exported since the external world should
	 * use isRangeVerbose, etc.
	 */
	private String getRangeStyle() {
		ColumnMessageFragmentDigest rangeConfig = getRangeConfiguration();
		if (rangeConfig == null) rangeConfig = ((ColumnMessageFragmentDigest) getDefaultObject());
		return rangeConfig.getRangeStyle();
	}

	/**
	 * A private auxilary method
	 * @return The singleton range configuration object
	 */
	private ColumnMessageFragmentDigest getRangeConfiguration() {
		return (ColumnMessageFragmentDigest) get(TYPE_RANGE_KEY);
	}


	/* ENUM SPECIFIC STUFF */

	/**
	 * Returns the string that goes between enum ites in a message.  
	 * If a space is desired before or after the cell text, it should be include in the text string.
	 * @param isExclusion Did the enum cell value have 'exclude selections' checked
	 * @param isMultiSelect Were there more than one cell-values selected.	 
	 * @return delimeter
	 */
	public String getEnumDelimiter(boolean isExclusion, boolean isMultiSelect) {
		String result = null;
		ColumnMessageFragmentDigest digest = getEnumConfiguration(isExclusion, isMultiSelect);
		if (digest != null) {
			result = digest.getEnumDelimiter();
		}
		if (result == null) result = this.getDefaultConfig().getEnumDelimiter();
		if (result == null) result = " "; // make sure there is at lease a space between things
		return result;
	}


	/**
	 * Returns the string that goes between the next-to-last and last-item enum item in a message.  
	 * If a space is desired before or after the cell text, it should be included in the text string.
	 * @param isExclusion Did the enum cell value have 'exclude selections' checked
	 * @param isMultiSelect Were there more than one cell-values selected.	 
	 * @return finalDelimeter
	 */
	public String getEnumFinalDelimiter(boolean isExclusion, boolean isMultiSelect) {
		String result = null;
		ColumnMessageFragmentDigest digest = getEnumConfiguration(isExclusion, isMultiSelect);
		if (digest != null) {
			result = digest.getEnumFinalDelimiter();
		}
		if (result == null) result = this.getDefaultConfig().getEnumFinalDelimiter();
		if (result == null) result = " "; // make sure there is at least a space between things
		return result;
	}


	/**
	 * Returns the string that prefixes the cell text in a message.  
	 * If a space is desired before the cell text, it should be included at the end of
	 * the text string.
	 * @param isExclusion Did the enum cell value have 'exclude selections' checked
	 * @param isMultiSelect Were there more than one cell-values selected.
	 * @return cellprefix text
	 */

	public String getEnumPrefix(boolean isExclusion, boolean isMultiSelect) {
		String result = null;
		ColumnMessageFragmentDigest digest = getEnumConfiguration(isExclusion, isMultiSelect);
		if (digest != null) {
			result = digest.getEnumPrefix();
		}
		if (result == null) result = this.getDefaultConfig().getEnumPrefix();
		if (result == null) result = (isExclusion ? "not " : " "); // make sure there is at least a space between things
		return result;
	}


	/**
	 * Returns the enum config object appropriate to the given parameters
	 * @param isExclusion: Did the enum cell value have 'exclude selections' checked
	 * @param isMultiSelect: Were there more than one cell-values selected.	 
	 * @return The appropriate enum config object
	 */
	public ColumnMessageFragmentDigest getEnumConfiguration(boolean isExclusion, boolean isMultiSelect) {
		return (ColumnMessageFragmentDigest) getEnumObject(isExclusion, isMultiSelect);
	}

	/**
	 * private auxilary method
	 * @return
	 */
	private ColumnMessageFragmentDigest getDefaultConfig() {
		return (ColumnMessageFragmentDigest) getDefaultObject();
	}

	/**
	 * Creates a new EnumConfiguration and adds it to the messageConfiguration.
	 * Note that a default cellSelection object is always created up front with system
	 * defaults.  If a config default comes in, values are overridden, thus maintaing system
	 * defaults for those that are not overridden.
	 * @param cellSelection default, enumExcludingSingle, enumIncludingMultiple, etc.
	 * @param enumDelim string that goes between enum elements
	 * @param enumFinalDelim string that goest before the last enum element
	 * @param enumPrefix string that goes before the enum cell value
	 */
	public void addEnumSelection(String cellSelection, String enumDelim, String enumFinalDelim, String enumPrefix) {
		// if we are adding the default, make sure we just override non-null values
		ColumnMessageFragmentDigest defaultConfig = null;
		if (cellSelection.equals(TYPE_DEFAULT_KEY)) defaultConfig = (ColumnMessageFragmentDigest) this.get(TYPE_DEFAULT_KEY);
		if (defaultConfig != null) {
			if (enumDelim != null) defaultConfig.setEnumDelimiter(enumDelim);
			if (enumFinalDelim != null) defaultConfig.setEnumFinalDelimiter(enumFinalDelim);
			if (enumPrefix != null) defaultConfig.setEnumPrefix(enumPrefix);
		}
		// if the default isn't there, or if we're adding a non-default, create a new one
		else {
			ColumnMessageFragmentDigest msg = new ColumnMessageFragmentDigest(cellSelection, enumDelim, enumFinalDelim, enumPrefix);
			addEnumObject(cellSelection, msg); // removes existing ones
		}
	}

	/**
	 * Given a msgDigest object, add it to the list.
	 * If the msgDigest is a default, just override defaults.
	 * @param msgDigest
	 */
	public void addMessageDigest(ColumnMessageFragmentDigest msgDigest) {
		updateMessageDigest(msgDigest);
	}

	/**
	 * Given a msgDigest object, udpates the list.
	 * If the msgDigest is a default, just override defaults.
	 * @param msgDigest
	 */
	public void updateMessageDigest(ColumnMessageFragmentDigest msgDigest) {
		String key = AbstractMessageKeyList.getKey(msgDigest);
		// For the default item, just override the existing defaults with
		// the non-null attributes of the given msgDigest.
		if (key.equals(TYPE_DEFAULT_KEY)) {
			setDefaults(msgDigest.getRangeStyle(), msgDigest.getEnumDelimiter(), msgDigest.getEnumFinalDelimiter(), msgDigest.getEnumPrefix(), msgDigest.getText());
		}
		else {
			remove(key);
			put(key, msgDigest);
		}
	}
	
	public void removeMessageDigest(ColumnMessageFragmentDigest msgDigest) {
		String key = AbstractMessageKeyList.getKey(msgDigest);
		if (!key.equals(TYPE_DEFAULT_KEY)) {
			remove(key);
		}
	}

	/**
	 * 
	 * @param rangeStyle
	 * @param enumDelim
	 * @param enumFinalDelim
	 * @param enumPrefix
	 */
	public void setDefaults(String rangeStyle, String enumDelim, String enumFinalDelim, String enumPrefix, String text) {
		ColumnMessageFragmentDigest defaultObj = (ColumnMessageFragmentDigest) this.getDefaultObject();
		defaultObj.setRangeStyle(rangeStyle);
		defaultObj.setEnumDelimiter(enumDelim);
		defaultObj.setEnumFinalDelimiter(enumFinalDelim);
		defaultObj.setEnumPrefix(enumPrefix);
		defaultObj.setText(text);
	}

}

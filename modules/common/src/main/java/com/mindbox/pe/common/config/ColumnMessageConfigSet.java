package com.mindbox.pe.common.config;

import java.io.Serializable;

import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.template.ColumnMessageFragmentDigest;
import com.mindbox.pe.xsd.config.CellSelectionType;
import com.mindbox.pe.xsd.config.MessageConfig;
import com.mindbox.pe.xsd.config.MessageConfigType;
import com.mindbox.pe.xsd.config.RangeStyleType;

public class ColumnMessageConfigSet extends AbstractMessageConfigSet<ColumnMessageFragmentDigest> implements Serializable {

	private static final long serialVersionUID = 2004071124000000L;

	public ColumnMessageConfigSet() {
		// add default values.
		// Note that the object here is unusual in that it has both enum and range properties set.
		final ColumnMessageFragmentDigest defaultMessageFragment = new ColumnMessageFragmentDigest(
				CellSelectionType.DEFAULT,
				Constants.DEFAULT_ENUM_DELIM,
				Constants.DEFAULT_ENUM_FINAL_DELIM,
				Constants.DEFAULT_ENUM_PREFIX);
		defaultMessageFragment.setRangeStyle(Constants.DEFAULT_RANGE_STYLE); // does a simple set on the messageFragment object
		setDefaultConfig(defaultMessageFragment);
	}

	public ColumnMessageConfigSet(final ColumnMessageConfigSet source) {
		super(source);
	}

	public void addColumnMessageFragmentDigest(ColumnMessageFragmentDigest source) {
		updateMessageDigest(source);
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
	public void addEnumSelection(final CellSelectionType cellSelection, String enumDelim, String enumFinalDelim, String enumPrefix) {
		// if we are adding the default, make sure we just override non-null values
		ColumnMessageFragmentDigest defaultConfig = null;
		if (cellSelection.equals(CellSelectionType.DEFAULT)) {
			defaultConfig = getDefaultConfig();
		}
		if (defaultConfig != null) {
			if (enumDelim != null) {
				defaultConfig.setEnumDelimiter(enumDelim);
			}
			if (enumFinalDelim != null) {
				defaultConfig.setEnumFinalDelimiter(enumFinalDelim);
			}
			if (enumPrefix != null) {
				defaultConfig.setEnumPrefix(enumPrefix);
			}
		}
		// if the default isn't there, or if we're adding a non-default, create a new one
		else {
			ColumnMessageFragmentDigest msg = new ColumnMessageFragmentDigest(cellSelection, enumDelim, enumFinalDelim, enumPrefix);
			updateConfig(msg.getType(), msg, msg.getCellSelection());
		}
	}

	public void addEnumSelection(final MessageConfig messageConfig) {
		if (messageConfig.getType() != MessageConfigType.ENUM) {
			throw new IllegalArgumentException("type must be enum");
		}
		addEnumSelection(messageConfig.getCellSelection(), messageConfig.getEnumDelimiter(), messageConfig.getEnumFinalDelimiter(), messageConfig.getEnumPrefix());
	}

	@Override
	protected ColumnMessageFragmentDigest createCopy(ColumnMessageFragmentDigest source) {
		return new ColumnMessageFragmentDigest(source);
	}

	/**
	 * Returns the string that goes between enum ites in a message.  
	 * If a space is desired before or after the cell text, it should be include in the text string.
	 * @param isExclusion Did the enum cell value have 'exclude selections' checked
	 * @param isMultiSelect Were there more than one cell-values selected.	 
	 * @return delimeter
	 */
	public String getEnumDelimiter(boolean isExclusion, boolean isMultiSelect) {
		String result = null;
		ColumnMessageFragmentDigest digest = getEnumConfig(isExclusion, isMultiSelect);
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
		ColumnMessageFragmentDigest digest = getEnumConfig(isExclusion, isMultiSelect);
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
		ColumnMessageFragmentDigest digest = getEnumConfig(isExclusion, isMultiSelect);
		if (digest != null) {
			result = digest.getEnumPrefix();
		}
		if (result == null) result = this.getDefaultConfig().getEnumPrefix();
		if (result == null) result = (isExclusion ? "not " : " "); // make sure there is at least a space between things
		return result;
	}

	/**
	 * This method isn't exported since the external world should
	 * use isRangeVerbose, etc.
	 * @return the rangeStyle for this MessageConfig.
	 */
	private RangeStyleType getRangeStyle() {
		return getRangeConfigOrDefaultIfNotFound().getRangeStyle();
	}

	public boolean isRangeStyleBracketed() {
		return getRangeStyle() == RangeStyleType.BRACKETED;
	}

	public boolean isRangeStyleSymbolic() {
		return getRangeStyle() == RangeStyleType.SYMBOLIC;
	}

	public boolean isRangeStyleVerbose() {
		return getRangeStyle() == RangeStyleType.VERBOSE;
	}

	public void removeMessageDigest(ColumnMessageFragmentDigest msgDigest) {
		super.removeConfig(msgDigest.getType(), msgDigest, msgDigest.getCellSelection());
	}

	@Override
	protected void setDefaults(ColumnMessageFragmentDigest source) {
		setDefaults(source.getRangeStyle(), source.getEnumDelimiter(), source.getEnumFinalDelimiter(), source.getEnumPrefix(), source.getText());
	}

	public void setDefaults(final MessageConfig messageConfig) {
		setDefaults(messageConfig.getRangeStyle(), messageConfig.getEnumDelimiter(), messageConfig.getEnumFinalDelimiter(), messageConfig.getEnumPrefix(), messageConfig.getText());
	}

	/**
	 * 
	 * @param rangeStyle rangeStyle
	 * @param enumDelim enumDelim
	 * @param enumFinalDelim enumFinalDelim
	 * @param enumPrefix enumPrefix
	 * @param text text
	 */
	public void setDefaults(final RangeStyleType rangeStyle, String enumDelim, String enumFinalDelim, String enumPrefix, String text) {
		final ColumnMessageFragmentDigest defaultObj = getDefaultConfig();
		defaultObj.setRangeStyle(rangeStyle);
		defaultObj.setEnumDelimiter(enumDelim);
		defaultObj.setEnumFinalDelimiter(enumFinalDelim);
		defaultObj.setEnumPrefix(enumPrefix);
		defaultObj.setText(text);
	}

	/**
	 * Assumes checkRangeStyle is called outside of this - no error checking is done here.
	 * @param rangeStyle valid range style (verbose, bracketed, symbolic).
	 */
	public void setRangeStyle(final RangeStyleType rangeStyle) {
		// note, we need to make a new range object here so we don't overwrite
		// copies that are being pointed to in multiple MessageConfigs
		ColumnMessageFragmentDigest rangeMessageFragement = new ColumnMessageFragmentDigest();
		rangeMessageFragement.setRangeStyle(rangeStyle);
		rangeMessageFragement.setType(MessageConfigType.RANGE);
		updateConfig(MessageConfigType.RANGE, rangeMessageFragement, null);
	}

	@Override
	protected void updateInvariants(ColumnMessageFragmentDigest target, ColumnMessageFragmentDigest source) {
		target.copyFrom(source);
	}

	/**
	 * Given a msgDigest object, udpates the list.
	 * If the msgDigest is a default, just override defaults.
	 * @param msgDigest msgDigest
	 */
	public void updateMessageDigest(ColumnMessageFragmentDigest msgDigest) {
		super.updateConfig(msgDigest.getType(), msgDigest, msgDigest.getCellSelection());
	}
}

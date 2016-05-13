package com.mindbox.pe.server.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.MessageConfiguration;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.xsd.config.LHSPatternType;
import com.mindbox.pe.xsd.config.MessageConfig;
import com.mindbox.pe.xsd.config.RuleGenerationBase;
import com.mindbox.pe.xsd.config.RuleGenerationLHS;
import com.mindbox.pe.xsd.config.RuleGenerationOverride;


/**
 * Rule Generation configuration.
 * @author Gene Kim
 * @author MindBox
 */
public final class RuleGenerationConfigHelper {

	public static final String DEFAULT_DATE_FORMAT_NAME = "gregorian";
	public static final String DEFAULT_MESSAGE_FORMAT_FUNCTION = "sprintf";
	public static final String DEFAULT_RULE_NAME_PREFIX = "GuidelineRule";
	public static final String VAUE_TYPE_UNSPECIFIED = "unspecified";
	private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
	private static final String DEFAULT_DATE_FORMAT_AE = "%m/%d/%Y";

	public static RuleGenerationConfigHelper newOverride(final RuleGenerationConfigHelper defaultConfig, final TemplateUsageType usageType, final String ruleNamePrefix,
			final RuleGenerationOverride ruleGenerationOverride) {
		final RuleGenerationConfigHelper instance = defaultConfig.copy();
		instance.resetInvariants(ruleGenerationOverride, ruleNamePrefix, usageType);
		return instance;
	}

	private String lhsDateFormat = DEFAULT_DATE_FORMAT_NAME;
	private String messageFormatConversionFunction = DEFAULT_MESSAGE_FORMAT_FUNCTION; // set defaults
	private DateFormat messageDateFormat = DEFAULT_DATE_FORMAT;
	private DateFormat messageDateRangeFormat = DEFAULT_DATE_FORMAT;
	private String messageDateFormatAe = DEFAULT_DATE_FORMAT_AE;
	private String guidelineRuleSeedName = DEFAULT_RULE_NAME_PREFIX;
	private final Map<String, String> keyMap = new HashMap<String, String>();
	private final MessageConfiguration messageConfiguration = new MessageConfiguration();
	private final List<RuleGenerationLHS.Value> lhsValueConfigList = new ArrayList<RuleGenerationLHS.Value>();
	private final Map<LHSPatternType, RuleGenerationLHS.Pattern> patternMap = new HashMap<LHSPatternType, RuleGenerationLHS.Pattern>();
	private TemplateUsageType usageType;
	private ControlPatternConfigHelper controlPatternConfigHelper;
	private LineagePatternConfigHelper lineagePatternConfigSet;
	private LinkPatternConfigHelper linkPatternConfigHelper;
	private RuleGenerationBase ruleGenerationBase;

	public RuleGenerationConfigHelper(final RuleGenerationBase ruleGenerationBase, final String ruleNamePrefix, final TemplateUsageType templateUsageType) {
		resetInvariants(ruleGenerationBase, ruleNamePrefix, templateUsageType);
	}

	public RuleGenerationConfigHelper copy() {
		return new RuleGenerationConfigHelper(ruleGenerationBase, guidelineRuleSeedName, usageType);
	}

	public String formatAsMessageDate(final Date date) {
		synchronized (messageDateFormat) {
			return messageDateFormat.format(date);
		}
	}

	public boolean generateMultiEnumAsSequenceInRHS() {
		return UtilBase.asBoolean(ruleGenerationBase.getRHS().isMultiEnumAsSequence(), true);
	}

	public ControlPatternConfigHelper getControlPatternConfig() {
		return controlPatternConfigHelper;
	}

	public RuleGenerationLHS.Pattern getEmptyPatternConfig() {
		return patternMap.get(LHSPatternType.EMPTY);
	}

	public String getGuidelineRuleSeedName() {
		return guidelineRuleSeedName;
	}

	/**
	 * 
	 * @param type
	 * @return the key attribute for <code>type</code>
	 * @since 3.0.0
	 */
	public String getKeyAttributeFor(GenericEntityType type) {
		return keyMap.get(type.toString());
	}

	public String getLhsDateFormat() {
		return lhsDateFormat;
	}

	/**
	 * Gets LHS value type configuration.
	 * @param type must be one of {@link RuleGenerationConfigHelper#VAUE_TYPE_UNSPECIFIED}
	 * @return value type config for <code>type</code>; never <code>null</code>
	 * @since PowerEditor 4.2.0
	 */
	public RuleGenerationLHS.Value getLHSValueConfig(String type) {
		synchronized (lhsValueConfigList) {
			for (int i = 0; i < lhsValueConfigList.size(); i++) {
				if (lhsValueConfigList.get(i).getType().equals(type)) {
					return lhsValueConfigList.get(i);
				}
			}
			// This check for default instance of necessary until configuration is read using digester
			if (usageType != null) {
				return ConfigurationManager.getInstance().getDefaultRuleGenerationConfigHelper().getLHSValueConfig(type);
			}
			else {
				// store a default instance if none found
				RuleGenerationLHS.Value value = new RuleGenerationLHS.Value();
				value.setType(type);
				value.setDeployValue(type.toUpperCase());
				value.setValueAsString(Boolean.FALSE);
				if (!lhsValueConfigList.contains(value)) {
					lhsValueConfigList.add(value);
				}
				return value;
			}
		}
	}

	public LineagePatternConfigHelper getLineagePatternConfigSet() {
		return lineagePatternConfigSet;
	}

	public LinkPatternConfigHelper getLinkPatternConfig() {
		return linkPatternConfigHelper;
	}

	public MessageConfiguration getMessageConfig() {
		return messageConfiguration;
	}

	public String getMessageDateFormatAe() {
		return messageDateFormatAe;
	}

	public DateFormat getMessageDateRangeFormat() {
		return messageDateRangeFormat;
	}

	public String getMessageFormatConversionFunction() {
		return messageFormatConversionFunction;
	}

	public RuleGenerationLHS.Pattern getPatternConfig(final LHSPatternType type) {
		return patternMap.get(type);
	}

	public RuleGenerationLHS.Pattern getRequestPatternConfig() {
		return patternMap.get(LHSPatternType.REQUEST);
	}

	public RuleGenerationLHS.Pattern getRulesetPatternConfig() {
		return patternMap.get(LHSPatternType.RULESET);
	}

	public boolean isPEActionOn() {
		return UtilBase.asBoolean(ruleGenerationBase.getRHS().isPEActionOn(), true);
	}

	private void resetInvariants(final RuleGenerationBase ruleGenerationBase, final String ruleNamePrefix, final TemplateUsageType templateUsageType) {
		final boolean configureDefault = templateUsageType == null;
		// load RHS configs
		if (configureDefault && ruleGenerationBase.getRHS() == null) {
			throw new IllegalArgumentException("<RuleGenerationDefault> element does not contain <RHS> tag.");
		}

		// load LHS configs
		if (configureDefault && ruleGenerationBase.getLHS() == null) {
			throw new IllegalArgumentException("<RuleGenerationDefault> element does not contain <LHS> tag.");
		}

		this.ruleGenerationBase = ruleGenerationBase;
		this.usageType = templateUsageType;
		if (!UtilBase.isEmpty(ruleNamePrefix)) {
			this.guidelineRuleSeedName = ruleNamePrefix;
		}

		if (ruleGenerationBase.getLHS() != null) {
			this.lhsValueConfigList.addAll(ruleGenerationBase.getLHS().getValue());

			this.lineagePatternConfigSet = new LineagePatternConfigHelper(ruleGenerationBase.getLHS());
			for (final RuleGenerationLHS.Pattern pattern : ruleGenerationBase.getLHS().getPattern()) {
				switch (pattern.getType()) {
				case CONTROL:
					controlPatternConfigHelper = new ControlPatternConfigHelper(pattern);
					break;
				case LINK:
					linkPatternConfigHelper = new LinkPatternConfigHelper(pattern);
					break;
				default:
					patternMap.put(pattern.getType(), pattern);
				}
			}

			// dateFormat
			if (ruleGenerationBase.getLHS().getDate() != null && !UtilBase.isEmpty(ruleGenerationBase.getLHS().getDate().getFormat())) {
				lhsDateFormat = ruleGenerationBase.getLHS().getDate().getFormat();
			}

			// load patterns
			for (final RuleGenerationLHS.Pattern pattern : ruleGenerationBase.getLHS().getPattern()) {
				if (pattern.getType() == LHSPatternType.CONTROL) {
					if (pattern.getAttribute() != null) {
						for (final RuleGenerationLHS.Pattern.Attribute attribute : pattern.getAttribute()) {
							String attrType = attribute.getType();
							String value = attribute.getValue();
							keyMap.remove(attrType);
							keyMap.put(attrType, value);
						}
					}
				}
			}
		}

		if (ruleGenerationBase.getMessageTypes() != null) {
			for (final MessageConfig messageConfig : ruleGenerationBase.getMessageTypes().getMessage()) {
				if (messageConfig.getType() == null) {
					throw new IllegalArgumentException("<Message> element in <MessageTypes> does not have type attribute.");
				}

				switch (messageConfig.getType()) {
				case CONDITIONAL:
					messageConfiguration.setConditionalDelimiter(UtilBase.isEmpty(messageConfig.getConditionalDelimiter()) ? null : messageConfig.getConditionalDelimiter());
					messageConfiguration.setConditionalFinalDelimiter(UtilBase.isEmpty(messageConfig.getConditionalFinalDelimiter()) ? null : messageConfig.getConditionalFinalDelimiter());
					break;
				case ENUM:
					if (messageConfig.getCellSelection() == null) {
						throw new IllegalArgumentException("<Message type=\"enum\" specified without a cellSelection attribute.");
					}
					messageConfiguration.addEnumSelection(messageConfig);
					break;
				case RANGE:
					if (messageConfig.getRangeStyle() == null) {
						throw new IllegalArgumentException("<Message type=\"range\"> specified without a rangeStyle attribute.");
					}
					messageConfiguration.setRangeStyle(messageConfig.getRangeStyle());
					break;
				default: // DEFAULT type
					messageConfiguration.setDefaults(messageConfig);
				}
			}
		}

		if (ruleGenerationBase.getRHS() != null) {
			if (!UtilBase.isEmpty(ruleGenerationBase.getRHS().getMessageDateFormat())) {
				this.messageDateFormat = new SimpleDateFormat(ruleGenerationBase.getRHS().getMessageDateFormat());
			}
			if (!UtilBase.isEmpty(ruleGenerationBase.getRHS().getMessageDateFormatAE())) {
				this.messageDateFormatAe = ruleGenerationBase.getRHS().getMessageDateFormatAE();
			}
			if (!UtilBase.isEmpty(ruleGenerationBase.getRHS().getMessageDateRangeFormat())) {
				this.messageDateRangeFormat = new SimpleDateFormat(ruleGenerationBase.getRHS().getMessageDateRangeFormat());
			}
			if (!UtilBase.isEmpty(ruleGenerationBase.getRHS().getMessageFormatConversionFunction())) {
				this.messageFormatConversionFunction = ruleGenerationBase.getRHS().getMessageFormatConversionFunction();
			}
		}
	}

	@Override
	public String toString() {
		return "RuleGenerationConfig[usage=" + usageType + "]";
	}
}
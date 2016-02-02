/*
 * Created on Sep 29, 2003
 */
package com.mindbox.pe.server.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.mindbox.pe.common.DomainClassProvider;
import com.mindbox.pe.common.PowerEditorXMLParser;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.MessageConfiguration;
import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.TemplateUsageType;


/**
 * Rule Generation configuration.
 * @author Gene Kim
 * @author MindBox
 */
// TBD remove Cloneable interface
public final class RuleGenerationConfiguration implements Cloneable {

	public static final String VAUE_TYPE_UNSPECIFIED = "unspecified";
	public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
	public static final String DEFAULT_DATE_FORMAT_AE = "%m/%d/%Y";
	public static final String DEFAULT_MESSAGE_FORMAT_FUNCTION = "sprintf";

	private static boolean isTrueString(String str) {
		return (str != null && (str.equalsIgnoreCase("TRUE") || str.equalsIgnoreCase("YES")));
	}

	public static RuleGenerationConfiguration newOverride(RuleGenerationConfiguration defaultConfig, TemplateUsageType usageType,
			Element ruleElement) {
		RuleGenerationConfiguration result = (RuleGenerationConfiguration) defaultConfig.clone();
		result.parseRuleElement(ruleElement, usageType);
		return result;
	}

	public static class PatternConfig implements Cloneable {

		private String className;
		private String prefix;
		private boolean generate;

		private PatternConfig(boolean generate, String className, String prefix) {
			this.generate = generate;
			this.className = className;
			this.prefix = prefix;
		}

		public boolean generatePattern() {
			return generate;
		}

		public String getPatternClassName() {
			return className;
		}

		public String getPrefix() {
			return prefix;
		}

		public boolean isPatternOn() {
			return generate;
		}

		public Object clone() {
			Object o = null;
			try {
				o = super.clone();
			}
			catch (CloneNotSupportedException e) {
				e.printStackTrace(System.err);
			}
			return o;
		}
	}

	public static final class LineagePatternConfig extends PatternConfig {

		private final String text, variable;

		LineagePatternConfig(boolean generate, String prefix, String text, String variable) {
			super(generate, "", prefix);
			this.text = text;
			this.variable = variable;
		}

		public String getText() {
			return text;
		}

		public String getVariable() {
			return variable;
		}
	}

	public static final class EmptyPatternConfig extends PatternConfig {

		private final boolean asSeq;

		private EmptyPatternConfig(boolean generate, boolean asSequence) {
			super(generate, "", "");
			this.asSeq = asSequence;
		}

		public boolean generateAsSequence() {
			return asSeq;
		}
	}

	public static final class RequestPatternConfig extends PatternConfig {

		private boolean usageTypeAsFocus;

		private RequestPatternConfig(boolean generate, String className, String prefix, boolean usageAsFocus) {
			super(generate, className, prefix);
			this.usageTypeAsFocus = usageAsFocus;
		}

		public boolean generateUsageTypeAsFocus() {
			return usageTypeAsFocus;
		}
	}

	public static class ControlPatternConfig extends PatternConfig {

		private final Map<String,String> attributes; // <String,String>
		private String[] disallowedEntities;

		/**
		 * 
		 * @param generate
		 * @param className
		 * @param attributes
		 * @param disallowedEntities can be <code>null</code>
		 * @throws ConfigurationException 
		 */
		private ControlPatternConfig(boolean generate, String className, Map<String,String> attributes, String disallowedEntities) {
			super(generate, className, null);
			this.disallowedEntities = (disallowedEntities == null ? new String[0] : disallowedEntities.split(","));
			this.attributes = attributes;
		}

		public boolean isDisallowed(GenericEntityType entityType) {
			return UtilBase.isMember(entityType.getName(), this.disallowedEntities);
		}

		/**
		 * 
		 * @return an array of disallowed entity names, if specified; <code>String[0]</code>, otherwise
		 */
		public String[] getDisallowedEntityNames() {
			return disallowedEntities;
		}

		public Map<String,String> getAttributes() {
			return attributes;
		}

		public String toString() {
			return "[Classname=" + getPatternClassName() + ", prefix=" + getPrefix() + ", attr size=" + attributes.size() + ", "
					+ attributes.toString() + "]";
		}

		public String findAttributeNameForContextElement(String elementType) {
			if (attributes.containsKey(elementType)) {
				return attributes.get(elementType);
			}
			else {
				return null;
			}
		}

		private void validate(TemplateUsageType usageType, DomainClassProvider domainClassProvider) {
			if (!isPatternOn()) return;
			String usageTypeStr = (usageType == null ? "(default)" : usageType.toString());
			// TT 1946 Validate that all required attributes are specified
			GenericEntityType[] entityTypes = GenericEntityType.getAllGenericEntityTypes();
			for (int i = 0; i < entityTypes.length; i++) {
				if (entityTypes[i].isUsedInContext() && !isDisallowed(entityTypes[i])) {
					// check attribute element is speicified
					if (!attributes.containsKey(entityTypes[i].getName())) {
						throw new ConfigurationException("<Attribute> element of type '" + entityTypes[i]
								+ "' must be specified for control pattern of " + usageTypeStr);
					}
					// check attribute element contains a valid attribute name
					DomainClass dc = domainClassProvider.getDomainClass(getPatternClassName());
					if (dc == null) {
						throw new ConfigurationException("<Pattern type='control'> for " + usageTypeStr
								+ " contains invalid value; No domain class named '" + getPatternClassName() + "' found");
					}
					DomainAttribute da = dc.getDomainAttribute(attributes.get(entityTypes[i].getName()));
					if (da == null) {
						throw new ConfigurationException("<Attribute> element of type '" + entityTypes[i] + " for " + usageTypeStr
								+ "  contains invalid value; Domain attribute of name " + attributes.get(entityTypes[i].getName())
								+ " not found for " + dc);
					}
				}
			}

		}
	}

	/**
	 * This uses generate as useTestFunction, class as testFunctionName, and prefix as variableSuffix.
	 * @author Geneho Kim
	 * @since PowerEditor 4.2.0
	 */
	public static final class LinkPatternConfig extends PatternConfig {

		private LinkPatternConfig(boolean useTestFunction, String testFunctionName, String variableSuffix) {
			super(useTestFunction, testFunctionName, variableSuffix);
		}

		public String getTestFunctionName() {
			return super.className;
		}

		public String getVariableSuffix() {
			return super.prefix;
		}

		public boolean useTestFunction() {
			return super.generate;
		}
	}

	private String lhsDateFormat;
	private boolean isPEActionOn;
	private String messageFormatConversionFunction = DEFAULT_MESSAGE_FORMAT_FUNCTION; // set defaults
	private DateFormat messageDateFormat = DEFAULT_DATE_FORMAT;
	private DateFormat messageDateRangeFormat = DEFAULT_DATE_FORMAT;
	private String messageDateFormatAe = DEFAULT_DATE_FORMAT_AE;
	private Map<String, PatternConfig> patternMap;
	private LineagePatternConfigSet lineagePatternConfigSet;
	private String guidelineRuleSeedName;
	private Map<String, String> keyMap;
	private MessageConfiguration messageConfig;
	private String objectGenInstanceCreateText;
	private List<RuleLHSValueConfig> lhsValueConfigList;
	private TemplateUsageType usageType;
	private ParameterContextConfiguration paramContextConfig;
	private boolean generateMultiEnumAsSequenceInRHS = true;

	RuleGenerationConfiguration(Element ruleElement) {
		this.keyMap = new HashMap<String, String>();
		this.patternMap = new HashMap<String, PatternConfig>();
		this.messageConfig = new MessageConfiguration();
		this.lineagePatternConfigSet = new LineagePatternConfigSet();

		this.parseRuleElement(ruleElement, usageType);
		this.lhsValueConfigList = new ArrayList<RuleLHSValueConfig>();
	}

	public void setParameterContextConfiguration(ParameterContextConfiguration paramContextConfig) {
		this.paramContextConfig = paramContextConfig;
	}

	public ParameterContextConfiguration getParameterContextConfiguration() {
		if (paramContextConfig == null) {
			paramContextConfig = new ParameterContextConfiguration();
		}
		return paramContextConfig;
	}

	public void addLHSValueConfig(RuleLHSValueConfig valueConfig) {
		if (!lhsValueConfigList.contains(valueConfig)) {
			lhsValueConfigList.add(valueConfig);
		}
	}

	/**
	 * Gets LHS value type configuration.
	 * @param type must be one of {@link RuleGenerationConfiguration#VAUE_TYPE_UNSPECIFIED}
	 * @return value type config for <code>type</code>; never <code>null</code>
	 * @since PowerEditor 4.2.0
	 */
	public RuleLHSValueConfig getLHSValueConfig(String type) {
		for (int i = 0; i < lhsValueConfigList.size(); i++) {
			if (lhsValueConfigList.get(i).getType().equals(type)) {
				return lhsValueConfigList.get(i);
			}
		}
		// This check for default instance of necessary until configuration is read using digester
		if (usageType != null) {
			return ConfigurationManager.getInstance().getRuleGenerationConfigurationDefault().getLHSValueConfig(type);
		}
		else {
			// store a default instance if none found
			RuleLHSValueConfig config = new RuleLHSValueConfig();
			config.setType(type);
			config.setDeployValue(type.toUpperCase());
			config.setValueAsString(false);
			addLHSValueConfig(config);
			return config;
		}
	}

	private void parseRuleElement(Element ruleElement, TemplateUsageType usageType) {
		this.usageType = usageType;
		boolean configureDefault = usageType == null;

		// load RuleNamePrefix (seed)
		Element rnPrefixElement = PowerEditorXMLParser.getFirstChild(ruleElement, "RuleNamePrefix");
		if (configureDefault && rnPrefixElement == null) {
			throw new IllegalArgumentException("<RuleGenerationDefault> element for does not contain <RuleNamePrefix> tag.");
		}
		if (rnPrefixElement != null) {
			// guidelineRuleSeedName
			String tmp = PowerEditorXMLParser.getValueOfFirstChild(rnPrefixElement, "Guideline");
			if (tmp == null && guidelineRuleSeedName == null)
				guidelineRuleSeedName = "GuidelineRule";
			else if (guidelineRuleSeedName == null) 
				guidelineRuleSeedName = tmp;
		}

		// load RuleMessgages configs - this tag is not required, even in the default...
		Element msgConfigElement = PowerEditorXMLParser.getFirstChild(ruleElement, "MessageTypes");
		if (msgConfigElement != null) {
			NodeList messageElementList = msgConfigElement.getElementsByTagName("Message");
			if (messageElementList != null) {
				for (int i = 0; i < messageElementList.getLength(); i++) {
					Element element = (Element) messageElementList.item(i);
					// first, look for type strings
					String typeValue = element.getAttribute("type");
					if (typeValue == null)
						throw new IllegalArgumentException("<Message> element in <MessageTypes> does not have type= attribute.");
					else if (typeValue.equals("range")) {
						// set rangeStyle
						String rangeStyle = element.getAttribute("rangeStyle");
						if (rangeStyle == null)
							throw new IllegalArgumentException("<Message type=\"range\"> specified without a rangeStyle attribute.");
						if (!MessageConfiguration.validateRangeStyle(rangeStyle))
							throw new IllegalArgumentException("Illegal message rangeStyle: " + rangeStyle);
						messageConfig.setRangeStyle(rangeStyle);
					}
					else if (typeValue.equals("enum")) {
						// set cellSelection
						String cellSelection = element.getAttribute("cellSelection");
						if (cellSelection == null)
							throw new IllegalArgumentException("<Message type=\"enum\" specified without a cellSelection attribute.");
						if (!MessageConfiguration.validateCellSelection(cellSelection))
							throw new IllegalArgumentException("Illegal message cellSelection: " + cellSelection);
						String enumDelim = element.getAttribute("enumDelimiter");
						String enumFinalDelim = element.getAttribute("enumFinalDelimiter");
						String enumPrefix = element.getAttribute("enumPrefix");
						// getAttribute doesn't distinguish between not found and ""
						// take a guess that "" means null for enumDelim and enumFinalDelim
						if (enumDelim.equals("")) enumDelim = null;
						if (enumFinalDelim.equals("")) enumFinalDelim = null;
						messageConfig.addEnumSelection(cellSelection, enumDelim, enumFinalDelim, enumPrefix);
					}
					else if (typeValue.equals("default")) {
						// set rangeStyle
						String rangeStyle = element.getAttribute("rangeStyle");
						if (rangeStyle != null && !rangeStyle.equals("") && !MessageConfiguration.validateRangeStyle(rangeStyle))
							throw new IllegalArgumentException("Illegal message rangeStyle: " + rangeStyle);
						String enumDelim = element.getAttribute("enumDelimiter");
						String enumFinalDelim = element.getAttribute("enumFinalDelimiter");
						String enumPrefix = element.getAttribute("enumPrefix");
						// getAttribute doesn't distinguish between not found and ""
						// take a guess that "" means null for enumDelim and enumFinalDelim
						if (enumDelim.equals("")) enumDelim = null;
						if (enumFinalDelim.equals("")) enumFinalDelim = null;
						if (rangeStyle.equals("")) rangeStyle = null;
						messageConfig.setDefaults(rangeStyle, enumDelim, enumFinalDelim, enumPrefix, element.getAttribute("text"));
					}

					else if (typeValue.equals("conditional")) {
						String conditionalDelimiter = element.getAttribute("conditionalDelimiter");
						String conditionalFinalDelimiter = element.getAttribute("conditionalFinalDelimiter");
						if (conditionalDelimiter.length() == 0) conditionalDelimiter = null;
						if (conditionalFinalDelimiter.length() == 0) conditionalFinalDelimiter = null;
						messageConfig.setConditionalDelimiter(conditionalDelimiter);
						messageConfig.setConditionalFinalDelimiter(conditionalFinalDelimiter);
					}
				}
			}
		}


		// load RHS configs
		Element rhsElement = PowerEditorXMLParser.getFirstChild(ruleElement, "RHS");
		if (configureDefault && rhsElement == null) {
			throw new IllegalArgumentException("<RuleGenerationDefault> element does not contain <RHS> tag.");
		}
		if (rhsElement != null) {
			// peActionOn defaults to false for the default configuration
			Element childElement = PowerEditorXMLParser.getFirstChild(rhsElement, "PEActionOn");
			if (childElement != null)
				this.isPEActionOn = "YES".equalsIgnoreCase(PowerEditorXMLParser.getValueOfFirstChild(rhsElement, "PEActionOn"));
			else if (configureDefault) this.isPEActionOn = false;

			childElement = PowerEditorXMLParser.getFirstChild(rhsElement, "MultiEnumAsSequence");
			if (childElement != null) {
				this.generateMultiEnumAsSequenceInRHS = !"NO".equalsIgnoreCase(PowerEditorXMLParser.getValueOfFirstChild(
						rhsElement,
						"MultiEnumAsSequence"));
			}

			childElement = PowerEditorXMLParser.getFirstChild(rhsElement, "MessageFormatConversionFunction");
			if (childElement != null) {
				this.messageFormatConversionFunction = PowerEditorXMLParser.getValueOfFirstChild(
						rhsElement,
						"MessageFormatConversionFunction");
			}

			childElement = PowerEditorXMLParser.getFirstChild(rhsElement, "MessageDateFormat");
			if (childElement != null) {
				this.messageDateFormat = new SimpleDateFormat(PowerEditorXMLParser.getValueOfFirstChild(rhsElement, "MessageDateFormat"));
			}

			childElement = PowerEditorXMLParser.getFirstChild(rhsElement, "MessageDateRangeFormat");
			if (childElement != null) {
				this.messageDateRangeFormat = new SimpleDateFormat(PowerEditorXMLParser.getValueOfFirstChild(
						rhsElement,
						"MessageDateRangeFormat"));
			}

			childElement = PowerEditorXMLParser.getFirstChild(rhsElement, "MessageDateFormatAE");
			if (childElement != null) {
				this.messageDateFormatAe = PowerEditorXMLParser.getValueOfFirstChild(rhsElement, "MessageDateFormatAE");
			}
		}

		// load LHS configs
		Element lhsElement = PowerEditorXMLParser.getFirstChild(ruleElement, "LHS");
		if (configureDefault && lhsElement == null) {
			throw new IllegalArgumentException("<RuleGenerationDefault> element does not contain <LHS> tag.");
		}
		if (lhsElement != null) {
			// dateFormat
			Element tmpDateFormat = PowerEditorXMLParser.getFirstChild(lhsElement, "Date");
			if (tmpDateFormat == null && this.lhsDateFormat == null)
				this.lhsDateFormat = "gregorian";
			else if (tmpDateFormat != null) this.lhsDateFormat = tmpDateFormat.getAttribute("format");

			// load patterns
			Map<String, String> keyAsStringMap = new HashMap<String, String>();
			NodeList elementList = lhsElement.getElementsByTagName("Pattern");
			if (elementList != null) {
				for (int i = 0; i < elementList.getLength(); i++) {
					Element element = (Element) elementList.item(i);
					String key = element.getAttribute("type");
					boolean generate = "YES".equalsIgnoreCase(element.getAttribute("generate"));
					String className = element.getAttribute("class");
					String prefix = element.getAttribute("prefix");
					String disallowedEntitiesStr = element.getAttribute("disallowedEntities");

					if (patternMap.remove(key) != null) { // need to remove if copied from default
					}


					if (key.equals("lineage")) {
						String text = element.getAttribute("text");
						String var = element.getAttribute("variable");
						lineagePatternConfigSet.addLineagePatternConfig(new LineagePatternConfig(generate, prefix, text, var));
					}
					else if (key.equals("empty")) {
						boolean asSeq = isTrueString(element.getAttribute("asSequence"));
						patternMap.put(key, new EmptyPatternConfig(generate, asSeq));
					}
					else if (key.equals("request")) {
						boolean usageTypeAsFocus = isTrueString(element.getAttribute("usageTypeAsFocus"));
						patternMap.put(key, new RequestPatternConfig(generate, className, prefix, usageTypeAsFocus));
					}
					else if (key.equals("control")) {
						// load in all the attributes.
						NodeList elementList2 = element.getElementsByTagName("Attribute");
						Map<String, String> attributes = new HashMap<String, String>();
						if (elementList2 != null) {
							for (int j = 0; j < elementList2.getLength(); j++) {
								Element attrElement = (Element) elementList2.item(j);
								String attrType = attrElement.getAttribute("type");
								String attrName = attrElement.getAttribute("name");
								String value = attrElement.getAttribute("value");
								String asString = attrElement.getAttribute("valueAsString");
								attributes.put(attrType, attrName);

								keyMap.remove(attrType);
								keyMap.put(attrType, value);
								keyAsStringMap.put(attrType, asString);
							}
						}
						patternMap.put(key, new ControlPatternConfig(generate, className, attributes, disallowedEntitiesStr));
					}
					else if (key.equals("link")) {
						boolean useTestFunction = isTrueString(element.getAttribute("useTestFunction"));
						patternMap.put(key, new LinkPatternConfig(
								useTestFunction,
								element.getAttribute("testFunctionName"),
								element.getAttribute("variableSuffix")));
					}
					else {
						patternMap.put(key, new PatternConfig(generate, className, prefix));
					}
				}
			}
		}
	}

	public boolean generateMultiEnumAsSequenceInRHS() {
		return generateMultiEnumAsSequenceInRHS;
	}

	public String getLhsDateFormat() {
		return lhsDateFormat;
	}

	public PatternConfig getPatternConfig(String type) {
		return patternMap.get(type);
	}

	public EmptyPatternConfig getEmptyPatternConfig() {
		return (EmptyPatternConfig) patternMap.get("empty");
	}

	public RequestPatternConfig getRequestPatternConfig() {
		return (RequestPatternConfig) patternMap.get("request");
	}


	public LineagePatternConfigSet getLineagePatternConfigSet() {
		return lineagePatternConfigSet;
	}

	public PatternConfig getRulesetPatternConfig() {
		return patternMap.get("ruleset");
	}

	public ControlPatternConfig getControlPatternConfig() {
		return (ControlPatternConfig) patternMap.get("control");
	}

	public LinkPatternConfig getLinkPatternConfig() {
		return (LinkPatternConfig) patternMap.get("link");
	}

	public MessageConfiguration getMessageConfig() {
		return messageConfig;
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

	public boolean isPEActionOn() {
		return isPEActionOn;
	}

	public String getMessageFormatConversionFunction() {
		return messageFormatConversionFunction;
	}

	public DateFormat getMessageDateFormat() {
		return messageDateFormat;
	}

	public DateFormat getMessageDateRangeFormat() {
		return messageDateRangeFormat;
	}

	public String getMessageDateFormatAe() {
		return messageDateFormatAe;
	}

	/**
	 * @return the guideline rule seed name
	 */
	public String getGuidelineRuleSeedName() {
		return guidelineRuleSeedName;
	}

	// TBD implement w/o using clone()
	public Object clone() {
		RuleGenerationConfiguration cpy = null;
		try {
			cpy = (RuleGenerationConfiguration) super.clone();

			RuleGenerationConfiguration old = (RuleGenerationConfiguration) this;

			// copy patternMap
			// we don't need to do a deep copy of the patterns, since a new one
			// is created each time we parse one.  The old gets removed, and the
			// new added
			cpy.patternMap = new HashMap<String, PatternConfig>();
			cpy.patternMap.putAll((HashMap<String, PatternConfig>) old.patternMap);

			// copy keyMap
			cpy.keyMap = new HashMap<String, String>();
			cpy.keyMap.putAll((HashMap<String, String>) old.keyMap);

			cpy.lineagePatternConfigSet = LineagePatternConfigSet.newInstance(this.lineagePatternConfigSet);

			cpy.messageConfig = new MessageConfiguration(messageConfig);

			cpy.lhsValueConfigList = new ArrayList<RuleLHSValueConfig>();

		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace(System.err);
		}

		return cpy;
	}

	public void setObjectGenInstanceCreateText(String s) {
		objectGenInstanceCreateText = s;
	}

	public String getObjectGenInstanceCreateText() {
		return objectGenInstanceCreateText;
	}

	public synchronized void validateConfiguration(DomainClassProvider domainClassProvider) {
		ControlPatternConfig controlPatternConfig = getControlPatternConfig();
		controlPatternConfig.validate(this.usageType, domainClassProvider);
	}

	public String toString() {
		return "RuleGenerationConfig[usage=" + usageType + "]";
	}
}
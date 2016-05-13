package com.mindbox.pe.server.generator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.DeployType;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.model.domain.DomainAttribute;
import com.mindbox.pe.model.domain.DomainClass;
import com.mindbox.pe.model.rule.Reference;
import com.mindbox.pe.model.template.GridTemplate;
import com.mindbox.pe.server.cache.DeploymentManager;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.config.ConfigurationManager;

/**
 * Rule generation utility class responsible for generating variable names.
 * 
 * @since PowerEditor 1.0
 */
public final class AeMapper {

	private static final ThreadLocal<DateFormat> DATE_FORMAT_THREADLOCAL = new ThreadLocal<DateFormat>() {
		protected DateFormat initialValue() {
			return new SimpleDateFormat("M/d/yy h:mm a");
		}
	};

	private static String QUOTE = "\"";
	private static final String guidelineKey = "Guideline";

	private static final Map<String, AeMapper> instanceMap = new HashMap<String, AeMapper>();

	public static String generateInstanceName(String s) {
		return generateName_SameLineage(s);
	}

	private static String generateName_SameLineage(String s) {
		return makeAEName(s);
	}

	private static String getClassAttributeVarName(DomainAttribute da) throws RuleGenerationException {
		return makeAEVariable(da.getName());
	}

	private static String getClassAttributeVarName(DomainClass dc, String attrName) throws RuleGenerationException {
		DomainAttribute domainAttribute = dc.getDomainAttribute(attrName);
		if (domainAttribute == null) {
			throw new RuleGenerationException("Attribute " + attrName + " does not exist for " + dc);
		}
		return getClassAttributeVarName(domainAttribute);
	}

	public static String getClassAttributeVarName(String className, String attrName) throws RuleGenerationException {
		DomainClass domainClass = DomainManager.getInstance().getDomainClass(className);
		if (domainClass == null) {
			throw new RuleGenerationException("Domain Class " + className + " does not exist");
		}
		if (attrName == null) {
			String varName = makeAEVariable(className);
			return varName;
		}
		else {
			return getClassAttributeVarName(domainClass, attrName);
		}
	}

	/**
	 * Equivalent to <code>getEnumAttributeIfApplicable(className, attribName, value, false)</code>.
	 * 
	 * @param className
	 * @param attribName
	 * @param value
	 * @return the enum attribute for the specified attribute and value
	 */
	public static String getEnumAttributeIfApplicable(String className, String attribName, String value) {
		return getEnumAttributeIfApplicable(className, attribName, value, false);
	}

	/**
	 * Gets the enum value if the specified class and attribute is an enum attribute. Returns
	 * <code>null</code>, if not.
	 * 
	 * @param className
	 * @param attribName
	 * @param value
	 * @param checkIfDeployValue
	 * @return the enum attribute for the specified attribute and value
	 */
	public static String getEnumAttributeIfApplicable(String className, String attribName, String value, boolean checkIfDeployValue) {
		DeployType deployType = null;
		DomainClass domainclass = DomainManager.getInstance().getDomainClass(className);
		if (domainclass != null) {
			DomainAttribute domainattribute = domainclass.getDomainAttribute(attribName);
			if (domainattribute != null) deployType = domainattribute.getDeployType();
		}

		String strippedValue = stripQuotes(value);

		// pass flag indiciating if this is for a boolean attribute
		String enumValueStr = DeploymentManager.getInstance().getEnumDeployValue(className, attribName, strippedValue, (deployType != null && deployType == DeployType.BOOLEAN), checkIfDeployValue);
		if (enumValueStr != null) {
			if (deployType == DeployType.STRING) {
				return QUOTE + enumValueStr + QUOTE;
			}
			else {
				return enumValueStr;
			}
		}
		else {
			return null;
		}
	}

	public static AeMapper getGuidelineInstance() {
		return getInstance(guidelineKey); // guideline is just a hashmap key
	}

	private static final AeMapper getInstance(String ruleType) {
		if (instanceMap.containsKey(ruleType)) {
			return instanceMap.get(ruleType);
		}
		else {
			AeMapper instance = new AeMapper();
			instanceMap.put(ruleType, instance);
			return instance;
		}
	}

	public static String getRuleset(GenerationParams abstractgenerateparms) {
		if (abstractgenerateparms instanceof GuidelineGenerateParams) {
			return getRuleset(GuidelineGenerateParams.class.cast(abstractgenerateparms).getUsage());
		}
		else {
			return getRuleset(abstractgenerateparms.getTemplate().getUsageType());
		}
	}

	public static String getRuleset(GridTemplate gridtemplate) {
		return getRuleset(gridtemplate.getUsageType());
	}

	public static String getRuleset(String templateType, TemplateUsageType usageType) {
		if (ConfigurationManager.getInstance().getRuleGenerationConfigHelper(usageType).isPEActionOn()) {
			return templateType.toUpperCase();
		}
		else {
			return "UNKNOWN";
		}
	}

	public static String getRuleset(TemplateUsageType templateUsageType) {
		return getRuleset(templateUsageType.toString(), templateUsageType);
	}

	public static String makeAEName(String s) {
		return s.toLowerCase().replace('_', '-');
	}

	public static String makeAEVariable(String s) {
		return "?" + makeAEName(s);
	}

	public static String mapAttributeName(String className, String attrName) throws RuleGenerationException {
		DomainClass domainclass = DomainManager.getInstance().getDomainClass(className);
		if (domainclass != null) {
			DomainAttribute domainattribute = domainclass.getDomainAttribute(attrName);
			if (domainattribute != null) {
				return domainattribute.getDeployLabel();
			}
			else {
				throw new RuleGenerationException("Could not find attribute " + attrName + " of class " + className);
			}
		}
		else {
			throw new RuleGenerationException("Could not find class " + className);
		}
	}

	public static String mapChannelName(String s) {
		String s1 = s.replace(' ', '-');
		return s1;
	}

	public static String mapClassName(String s) throws RuleGenerationException {
		DomainClass domainclass = DomainManager.getInstance().getDomainClass(s);
		if (domainclass != null) {
			return domainclass.getDeployLabel();
		}
		else {
			throw new RuleGenerationException("Could not find class " + s);
		}
	}

	public static String stripQuotes(String s) {
		char ac[] = s.toCharArray();
		StringBuilder stringbuffer = new StringBuilder();
		for (int i = 0; i < ac.length; i++)
			if (ac[i] != '"') stringbuffer.append(ac[i]);

		return stringbuffer.toString();
	}

	private int nextRuleNumber;

	private int mActionNum;
	private Hashtable<String, List<String>> mRuleVarNames;
	private final Logger logger;

	private AeMapper() {
		this.logger = Logger.getLogger(getClass());
		nextRuleNumber = 0;
		mActionNum = 0;
		mRuleVarNames = new Hashtable<String, List<String>>();
	}

	public String generateActionName() {
		mActionNum++;
		return "action-" + mActionNum;
	}

	public String generateAEVariable(String s, boolean sameLineage) {
		if (s == null) {
			return "*ERROR*";
		}
		if (sameLineage) {
			return makeAEVariable(generateName_SameLineage(s));
		}
		else {
			return makeAEVariable(generateName_NotSameLineage(s));
		}
	}

	private String generateName_NotSameLineage(String s) {
		String s1 = (new String(s)).toUpperCase();
		List<String> list = mRuleVarNames.get(s1);
		if (list == null) {
			list = new java.util.ArrayList<String>();
			mRuleVarNames.put(s1, list);
		}
		int i = list.size();
		String s2 = makeAEName(s) + "-" + ++i;
		printOnDebug("Creating var name=" + s2);
		list.add(s2);
		return s2;
	}

	public String generateRuleDescription(AbstractGenerateParms generateParms) {
		StringBuilder stringbuffer = new StringBuilder();
		stringbuffer.append(generateParms.getTemplate().getName());
		stringbuffer.append("; RowNum=");
		stringbuffer.append(generateParms.getRowNum());
		if (generateParms.getColumnNum() > 0) {
			stringbuffer.append("; ColNum=");
			stringbuffer.append(generateParms.getColumnNum());
		}
		stringbuffer.append("; Status=");
		stringbuffer.append(generateParms.getStatus());
		stringbuffer.append("; EffDate=");
		String effDateDesc = (generateParms.getSunrise() == null ? null : String.format(
				"%s[%s]",
				generateParms.getSunrise().toString(),
				DATE_FORMAT_THREADLOCAL.get().format(generateParms.getSunrise().getDate())));
		stringbuffer.append(effDateDesc);
		stringbuffer.append("; ExpDate=");
		String expDateDesc = (generateParms.getSunset() == null ? null : String.format(
				"%s[%s]",
				generateParms.getSunset().toString(),
				DATE_FORMAT_THREADLOCAL.get().format(generateParms.getSunset().getDate())));
		stringbuffer.append(expDateDesc);
		return stringbuffer.toString();
	}

	/**
	 * Generates a ruleName string that is guaranteed to be unique across deploys. The rulename
	 * consists of - ruleNameSeed: Specified in config file - nextRuleNumber: incremented per rule
	 * starting at 1 - date: "D" followed by dd-MM-yyyy - time: "T" followed by HH-mm-ss
	 * 
	 * @param genParams
	 * @return the rule name
	 */
	public String generateRuleName(AbstractGenerateParms genParams) {
		// rule number
		nextRuleNumber++;
		// date string
		Date date = new Date();
		String datestr = (new SimpleDateFormat("'D'dd-MM-yyyy-'T'HH-mm-ss")).format(date);
		String ruleNameSeed = getSeedName(genParams.getUsage());

		return ruleNameSeed + "-" + genParams.getID() + "-R" + genParams.getRowNum() + "-" + nextRuleNumber + "-" + datestr;
	}

	private String getSeedName(TemplateUsageType usageType) {
		String result = ConfigurationManager.getInstance().getRuleGenerationConfigHelper(usageType).getGuidelineRuleSeedName();
		return result;
	}

	/**
	 * Equivalent to
	 * <code>mapEnumValue(reference.getClassName(),reference.getAttributeName(),value)</code>.
	 * 
	 * @param reference
	 * @param value
	 * @return the enum value for the specified value and the reference
	 */
	public String mapEnumValue(Reference reference, String value) {
		return mapEnumValue(reference.getClassName(), reference.getAttributeName(), value);
	}

	/**
	 * Gets the enum value if the specified class and attribute is an enum attribute. Returns an
	 * error string, if not.
	 * 
	 * @param className
	 * @param attribName
	 * @param s2
	 */
	public String mapEnumValue(String className, String attribName, String s2) {
		DeployType deployType = null;
		DomainClass domainclass = DomainManager.getInstance().getDomainClass(className);
		if (domainclass != null) {
			DomainAttribute domainattribute = domainclass.getDomainAttribute(attribName);
			if (domainattribute != null) deployType = domainattribute.getDeployType();
		}
		printOnDebug("DeployType for " + className + "." + attribName + "=" + deployType);

		String enumValueStr = DeploymentManager.getInstance().getEnumDeployValue(className, attribName, stripQuotes(s2), true);
		if (enumValueStr != null) {
			printOnDebug("Found deployEnumValue = " + enumValueStr);
			if (deployType == DeployType.STRING) {
				return QUOTE + enumValueStr + QUOTE;
			}
			else {
				return enumValueStr;
			}
		}
		else {
			logger.warn("DeployValue not found for " + attribName);
			return null;
		}
	}

	/**
	 * Map investor name to AE value. SGS - 5/7/03
	 */
	public String mapInvestorName(String s) {
		String s1 = s.replace(' ', '-');
		return s1;
	}

	private void printOnDebug(String s) {
		logger.debug(s);
	}

	public void reInit() {
		nextRuleNumber = 0;
		mActionNum = 0;
		mRuleVarNames.clear();
	}

	public void reInitRuleVariables() {
		mRuleVarNames.clear();
	}

}
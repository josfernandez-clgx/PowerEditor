package com.mindbox.pe.server.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.GridTemplate;
import com.mindbox.pe.model.TemplateUsageType;
import com.mindbox.pe.server.cache.DeploymentManager;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.config.ConfigurationManager;
import com.mindbox.pe.server.config.ServerConfiguration;


public class RuleEngineMapper {

	private static final String RULESETS[][] = { { "Qualification", "QUALIFICATION" }, {
			"Stipulation", "STIPULATION" }, {
			"CompensatingFactor", "COMPENSATING_FACTOR" }, {
			"PointAdjustment", "PRICING_POINT_ADJUSTMENT" }, {
			"MarginAdjustment", "PRICING_MARGIN_ADJUSTMENT" }, {
			"FeeAdjustment", "PRICING_FEE_ADJUSTMENT" }, {
			"CAPAdjustment", "PRICING_CAP_ADJUSTMENT" }, {
			"RateAdjustment", "PRICE-ADD-RULES" }, {
			"MIRateAdjustment", "MI_PRICING_RATE_ADJUSTMENT" }, {
			"BaseRate", "PRICING_BASE_RATE" }, {
			"CreditRating", "CREDIT_RATING" }
	};

	private static String getRuleset(String templateType, TemplateUsageType usageType) {
		if (ConfigurationManager.getInstance().getRuleGenerationConfiguration(usageType).isPEActionOn()) {
			return templateType.toUpperCase();
		}
		else {
			String result = getRuleset_static(templateType);
			return result;
		}
	}

	private static String getRuleset_static(String pTemplateType) {
		for (int i = 0; i < RULESETS.length; i++) {
			String templateRuleset[] = RULESETS[i];
			if (!pTemplateType.equals(templateRuleset[0]))
				continue;
			return templateRuleset[1];
		}
		return "UNKNOWN";
	}

	public String generateAEVariable(String pAttribute) {
		// all PLAN_EVALUATION objects need to have same object variable (plan-eval)
		if (!pAttribute.equals("PLAN_EVALUATION")) {
			return makeAEVariable(pAttribute + "-" + ++mVarNum);
		}
		else {
			return makeAEVariable("plan-eval");
		}
	}

	public String generateRuleDescription(AbstractGenerateParms pGenParms) {
		return pGenParms.getTemplate().getName();
	}

	private String stripQuotesSpaces(String pInputString) {
		char chars[] = pInputString.toCharArray();
		StringBuffer output = new StringBuffer();
		for (int i = 0; i < chars.length; i++)
			if (chars[i] != '"' && chars[i] != ' ')
				output.append(chars[i]);

		return output.toString();
	}

	private String stripQuotes(String pInputString) {
		char chars[] = pInputString.toCharArray();
		StringBuffer output = new StringBuffer();
		for (int i = 0; i < chars.length; i++)
			if (chars[i] != '"')
				output.append(chars[i]);

		return output.toString();
	}

	public RuleEngineMapper() {
		mDeployDir = null;
		mErrorWriter = null;
		mRuleNum = 0;
		mActionNum = 0;
		mInstanceNames = new Hashtable<String, List<String>>();
		mVarNum = 0;
		init();
		initWriters();
	}

	public String mapChannelName(String pChannel) {
		return pChannel;
	}

	/**
	 * Return the mapped name of the investor.  In this case,
	 * return what was passed in.
	 * SGS - 5/7/03
	 */
	public String mapInvestorName(String pInvestor) {
		return pInvestor;
	}

	public String mapAttributeName(String pClassName, String pAttributeName) {
		DomainClass dc = DomainManager.getInstance().getDomainClass(pClassName);
		if (dc != null) {
			DomainAttribute da = dc.getDomainAttribute(pAttributeName);
			if (da != null) {
				return da.getDeployLabel();
			}
			else {
				logger.info("***ERROR : Could not locate attrib " + pAttributeName);
				return "LDO:" + makeAEName(pAttributeName);
			}
		}
		else {
			logger.info("***ERROR : Could not locate class " + pClassName);
			return "LDO:" + makeAEName(pAttributeName);
		}
	}

	private String makeAEName(String pStr) {
		return pStr.replace('_', '-');
	}

	public String generateRuleName() {
		mRuleNum++;
		return "Rule" + mRuleNum;
	}

	public String generateActionName() {
		mActionNum++;
		return "action-" + mActionNum;
	}

	public String generateInstanceName(String pClassName) {
		List<String> insts = mInstanceNames.get(pClassName);
		if (insts == null) {
			insts = new java.util.ArrayList<String>();
			mInstanceNames.put(pClassName, insts);
		}
		int numInsts = insts.size();
		String newName = makeAEName(pClassName) + "-" + ++numInsts;
		logger.info("Creating inst name=" + newName);
		insts.add(newName);
		return newName;
	}

	public String mapClassName(String pClassName) {

		DomainClass dc = DomainManager.getInstance().getDomainClass(pClassName);
		if (dc != null) {
			return dc.getDeployLabel();
		}
		else {
			logger.info("***ERROR : Could not locate class " + pClassName);
			return "LDO:" + makeAEName(pClassName);
		}
	}

	public String mapEnumValue(String pClassName, String pAttributeName, String pValue) {
		String deployType = null;
		String deployValue = null;
		DomainClass dc = DomainManager.getInstance().getDomainClass(pClassName);
		if (dc != null) {
			DomainAttribute da = dc.getDomainAttribute(pAttributeName);
			if (da != null)
				deployType = da.getDeployType().toString();
		}
		logger.info("DeployType for " + pClassName + "." + pAttributeName + "=" + deployType);
		deployValue = DeploymentManager.getInstance().getEnumDeployValue(pClassName, pAttributeName, stripQuotes(pValue), false);
		if (deployValue != null)
			logger.info("Found deployEnumValue = " + deployValue);
		else
			deployValue = stripQuotesSpaces(pValue);
		if (deployType != null && (deployType.equals("String") || deployType.equals("Date")))
			deployValue = QUOTE + deployValue + QUOTE;
		return deployValue;
	}

	public PrintWriter getRuleWriter(String pRuleset) {
		PrintWriter writer = mRuleWriters.get(pRuleset);
		if (writer != null)
			return writer;
		String filename = pRuleset + "-rules.art";
		try {
			File outfile = new File(mDeployDir, filename);
			writer = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
			mRuleWriters.put(pRuleset, writer);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return writer;
	}

	public PrintWriter getRuleWriter(AbstractGenerateParms pGenParms) {
		return getRuleWriter(getRuleset(pGenParms));
	}

	public PrintWriter getObjectWriter(String pRuleset) {
		PrintWriter writer = mObjectWriters.get(pRuleset);
		if (writer != null)
			return writer;
		String filename = pRuleset + "-instances.art";
		try {
			File outfile = new File(mDeployDir, filename);
			writer = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
			mObjectWriters.put(pRuleset, writer);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return writer;
	}

	public PrintWriter getObjectWriter(AbstractGenerateParms pGenParms) {
		return getObjectWriter(getRuleset(pGenParms));
	}

	public PrintWriter getErrorWriter() {
		PrintWriter writer = mErrorWriter;
		if (writer != null)
			return writer;
		String filename = "errors.art";
		try {
			File outfile = new File(mDeployDir, filename);
			writer = new PrintWriter(new BufferedWriter(new FileWriter(outfile)));
			mErrorWriter = writer;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return writer;
	}

	public void reInitRuleVariables() {
		mInstanceNames.clear();
		mVarNum = 0;
	}

	public void initWriters() {
		mRuleWriters = new Hashtable<String, PrintWriter>();
		mObjectWriters = new Hashtable<String, PrintWriter>();
	}

	private boolean init() {
		ServerConfiguration.DeploymentConfig deployConfig =
			ConfigurationManager.getInstance().getServerConfiguration().getDeploymentConfig();

		String deployDirBase = deployConfig.getBaseDir();
		if (deployDirBase.endsWith(File.separator) == false) {
			deployDirBase = deployDirBase + File.separator;
		}
		boolean useTimeStamp = deployConfig.useTimestampFolder();
		String deployDirName = deployDirBase;
		if (useTimeStamp) {
			Date now = new Date();
			String relDir = (new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss")).format(now);
			deployDirName = deployDirName + relDir;
		}
		logger.info("Deploy dir= " + deployDirName);
		try {
			File outDir = new File(deployDirName);
			if (!outDir.exists()) {
				boolean couldCreate = outDir.mkdirs();
				if (!couldCreate) {
					logger.info("Could not create necessary directories: " + mDeployDir);
					return false;
				}
				logger.info("Succeeded in creating dirs for: " + outDir);
			}
			mDeployDir = outDir;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void closeWriters() {
		PrintWriter writer;
		for (Enumeration<PrintWriter> writers = mRuleWriters.elements(); writers.hasMoreElements(); writer.close())
			writer = writers.nextElement();

		//PrintWriter writer;
		for (Enumeration<PrintWriter> writers = mObjectWriters.elements(); writers.hasMoreElements(); writer.close())
			writer = writers.nextElement();

		if (mErrorWriter != null)
			mErrorWriter.close();
	}

	public String getRuleset(AbstractGenerateParms pGenParms) {
		//System.out.println(">>> getRuleSet(GenerateParms) with " + pGenParms);
		String templatetype = pGenParms.getTemplate().getUsageType().toString();
		return getRuleset(templatetype, pGenParms.getTemplate().getUsageType());
	}

	public String getRuleset(GridTemplate pTemplate) {
		//System.out.println(">>> getRuleSet(template) with " + pTemplate);
		String templatetype = pTemplate.getUsageType().toString();
		return getRuleset(templatetype, pTemplate.getUsageType());
	}

	public String makeAEVariable(String pVarName) {
		return "?" + makeAEName(pVarName.toLowerCase());
	}

	private File mDeployDir;
	private Hashtable<String, PrintWriter> mRuleWriters;
	private Hashtable<String, PrintWriter> mObjectWriters;
	private PrintWriter mErrorWriter;
	private int mRuleNum;
	private int mActionNum;
	private Hashtable<String, List<String>> mInstanceNames;
	private int mVarNum;
	private static String QUOTE = "\"";

	private final Logger logger = Logger.getLogger(RuleEngineMapper.class);
}

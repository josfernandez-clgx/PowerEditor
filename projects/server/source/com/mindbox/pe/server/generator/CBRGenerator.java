/*
 * Created on 2004. 10. 22.
 */
package com.mindbox.pe.server.generator;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.mindbox.pe.common.config.UIConfiguration;
import com.mindbox.pe.model.CBRAttribute;
import com.mindbox.pe.model.CBRAttributeValue;
import com.mindbox.pe.model.CBRCase;
import com.mindbox.pe.model.CBRCaseAction;
import com.mindbox.pe.model.CBRCaseBase;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.server.cache.CBRManager;


/**
 * CBR data generator.
 *
 * @author Inna Nill
 * @since PowerEditor 4.1.0
 */
public class CBRGenerator extends AbstractBufferedGenerator {

	private static final String PROCESS_FILENAME = "cbr-data";
	private static final String UNSPECIFIED = ":UNSPECIFIED";
	private static final String PERFECT = ":PERFECT";


	private static CBRGenerator instance = null;

	public static CBRGenerator getInstance() {
		if (instance == null) {
			instance = new CBRGenerator();
		}
		return instance;
	}

	private CBRCaseBase currentCaseBase = null;
	private Map<CBRCaseBase, List<CBRAttribute>> caseBaseToAttributeMap = null;
	private Map<CBRCaseBase, List<CBRCase>> caseBaseToCaseMap = null;
	private SimpleDateFormat dateFormat = null;;

	private CBRGenerator() {
	}

	public synchronized void init(OutputController outputController) {
		super.init(outputController);
		resetStatus(outputController.getStatus());
		currentCaseBase = null;
		caseBaseToAttributeMap = new Hashtable<CBRCaseBase, List<CBRAttribute>>();
		caseBaseToCaseMap = new Hashtable<CBRCaseBase, List<CBRCase>>();
		dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		buildCBRData();
	}

	private void buildCBRData() {
		CBRManager cbrManager = CBRManager.getInstance();
		// Create case-base -- attribute mappings and case-base -- case mappings.
		List<CBRCaseBase> allCaseBases = cbrManager.getCBRCaseBases();
		List<CBRAttribute> allAttributes = cbrManager.getCBRAttributes();
		List<CBRCase> allCases = cbrManager.getCBRCases();
		for (CBRCaseBase element : allCaseBases) {
			List<CBRAttribute> attributesForCaseBase = new ArrayList<CBRAttribute>();
			for (ListIterator<CBRAttribute> iter = allAttributes.listIterator(); iter.hasNext();) {
				CBRAttribute attr = iter.next();
				if ((attr.getCaseBase() != null) && (attr.getCaseBase().equals(element))) {
					attributesForCaseBase.add(attr);
				}
			}
			caseBaseToAttributeMap.put(element, attributesForCaseBase);
			List<CBRCase> casesForCaseBase = new ArrayList<CBRCase>();
			for (ListIterator<CBRCase> iter = allCases.listIterator(); iter.hasNext();) {
				CBRCase cbrCase = iter.next();
				if ((cbrCase.getCaseBase() != null) && (cbrCase.getCaseBase().equals(element))) {
					casesForCaseBase.add(cbrCase);
				}
			}
			caseBaseToCaseMap.put(element, casesForCaseBase);
		}
	}

	public synchronized void generateCBRData() throws RuleGenerationException {
		logger.info(">>> generateCBRData");
		try {
			List<CBRCaseBase> caseBases = CBRManager.getInstance().getCBRCaseBases();
			print(";; Generated on " + UIConfiguration.FORMAT_DATE_TIME_SEC.format(new Date()) + " by PowerEditor");
			nextLine();

			nextLine();
			print(";;------- CASE BASES -------");
			nextLine();
			int count = 0;
			for (Iterator<CBRCaseBase> iter = caseBases.iterator(); iter.hasNext();) {
				CBRCaseBase element = iter.next();
				generateCaseBase(element);
				generateAttributes(element);
				generateCases(element);
				++count;
				setPercentageComplete(100 * count / caseBases.size());
			}
			setPercentageComplete(100);
		}
		catch (Exception ex) {
			logger.error("Error while generating CBR data", ex);
			reportError(ex.getMessage());
		}
		finally {
			currentCaseBase = null;
		}
	}

	private void generateCaseBase(CBRCaseBase caseBase) {
		currentCaseBase = caseBase;
		nextLine();
		openParan();
		print("cbr::define-pe-case-base ");
		printConditionalAsString(caseBase.getName());
		nextLineIndent();
		printConditional((caseBase.getCaseClass() == null ? UNSPECIFIED : caseBase.getCaseClass().getSymbol()));
		nextLine();
		printConditionalAsString(caseBase.getIndexFile());
		nextLine();
		printConditional((caseBase.getScoringFunction() == null ? UNSPECIFIED : caseBase.getScoringFunction().getSymbol()));
		nextLine();
		printConditional(caseBase.getNamingAttribute());
		nextLine();
		printConditional(caseBase.getMatchThreshold());
		nextLine();
		printConditional(caseBase.getMaximumMatches());
		nextLine();
		if (caseBase.getEffectiveDate() != null) {
			printConditionalAsString(caseBase.getEffectiveDate().getDate());
			nextLine();
		}
		if (caseBase.getExpirationDate() != null) {
			printConditionalAsString(caseBase.getExpirationDate().getDate());
			nextLine();
		}
		printConditionalAsString(caseBase.getDescription());
		nextLineOutdent();
		closeParan();
		nextLine();
		super.incrementObjectCount();
	}

	private void generateAttributes(CBRCaseBase caseBase) {
		List<CBRAttribute> attributes = caseBaseToAttributeMap.get(caseBase);
		for (Iterator<CBRAttribute> iter = attributes.iterator(); iter.hasNext();) {
			CBRAttribute element = iter.next();
			generateAttribute(element);
		}
	}

	private void generateAttribute(CBRAttribute attr) {
		nextLine();
		openParan();
		print("cbr::define-pe-attribute ");
		printConditionalAsString(attr.getName());
		nextLineIndent();
		printConditionalAsString(attr.getCaseBase().getName());
		nextLine();
		printConditional(attr.getAttributeType().getSymbol());
		nextLine();
		printConditional(attr.getMatchContribution(), true);
		nextLine();
		printConditional(attr.getMismatchPenalty(), true);
		nextLine();
		printConditional(attr.getAbsencePenalty(), true);
		nextLine();
		printConditional(attr.getLowestValue());
		nextLine();
		printConditional(attr.getHighestValue());
		nextLine();
		printConditional(attr.getMatchInterval());
		nextLine();
		printConditional(attr.getValueRange().getSymbol());
		nextLine();
		printConditionalAsString(attr.getDescription());
		nextLineOutdent();
		closeParan();
		nextLine();
		super.incrementObjectCount();
	}

	private void generateCases(CBRCaseBase caseBase) {
		List<CBRCase> cases = caseBaseToCaseMap.get(caseBase);
		for (Iterator<CBRCase> iter = cases.iterator(); iter.hasNext();) {
			CBRCase element = iter.next();
			generateCase(element);
		}
	}

	private void generateCase(CBRCase cbrCase) {
		nextLine();
		openParan();
		print("cbr::define-pe-case ");
		printConditionalAsString(cbrCase.getName());
		nextLineIndent();
		printConditionalAsString(cbrCase.getCaseBase().getName());
		nextLine();
		if (cbrCase.getEffectiveDate() != null) {
			printConditionalAsString(cbrCase.getEffectiveDate().getDate());
			nextLine();
		}
		if (cbrCase.getExpirationDate() != null) {
			printConditionalAsString(cbrCase.getExpirationDate().getDate());
			nextLine();
		}
		printConditionalAsString(cbrCase.getDescription());
		nextLine();
		List<CBRAttributeValue> attrVals = cbrCase.getAttributeValues();
		List<CBRCaseAction> actions = cbrCase.getCaseActions();
		if ((attrVals != null) && (attrVals.size() > 0)) {
			generateAttributeValues(attrVals);
		}
		if ((actions != null) && (actions.size() > 0)) {
			generateCaseActions(actions);
		}
		nextLineOutdent();
		closeParan();
		nextLine();
		super.incrementObjectCount();
	}

	private void generateAttributeValues(List<CBRAttributeValue> attrVals) {
		print(":values");
		nextLine();
		for (Iterator<CBRAttributeValue> it = attrVals.iterator(); it.hasNext();) {
			CBRAttributeValue val = (CBRAttributeValue) it.next();
			if (val.getAttribute() != null) {
				printConditionalAsString(val.getAttribute().getName());
			}
			else {
				print(UNSPECIFIED);
			}
			nextLine();
			printConditionalAsString(val.getName()); // This is the attribute-value VALUE.
			nextLine();
			printConditional(val.getMatchContribution(), true);
			nextLine();
			printConditional(val.getMismatchPenalty(), true);
			nextLine();
			printConditionalAsString(val.getDescription());
			nextLine();
		}
	}

	private void generateCaseActions(List<CBRCaseAction> actions) {
		print(":actions");
		nextLine();
		for (Iterator<CBRCaseAction> it = actions.iterator(); it.hasNext();) {
			CBRCaseAction val = (CBRCaseAction) it.next();
			printConditional(val.getSymbol());
			nextLine();
		}
	}

	private void printConditional(String value) {
		if (value == null || value.equals("")) {
			print(UNSPECIFIED);
		}
		else {
			print(value);
		}
	}

	private void printConditional(int value) {
		if (value == Constants.CBR_NULL_DATA_EQUIVALENT_VALUE) {
			print(UNSPECIFIED);
		}
		else {
			print(value);
		}
	}

	private void printConditional(double value) {
		if (value == Constants.CBR_NULL_DOUBLE_VALUE) {
			print(UNSPECIFIED);
		}
		else {
			print(value);
		}
	}

	private void printConditional(int value, boolean considerPerfect) {
		if (considerPerfect == true && value == CBRAttribute.PERFECT_VALUE) {
			print(PERFECT);
		}
		else {
			printConditional(value);
		}
	}

	private void printConditionalAsString(String value) {
		if (value == null || value.equals("")) {
			print(UNSPECIFIED);
		}
		else {
			printAsString(value);
		}
	}

	private void printConditionalAsString(Date value) {
		if (value == null) {
			print(UNSPECIFIED);
		}
		else {
			printAsString(value);
		}
	}

	private void printAsString(String value) {
		print('"' + value + '"');
	}

	private void printAsString(Date value) {
		printAsString(dateFormat.format(value));
	}

	public synchronized void writeAll() throws RuleGenerationException {
		super.writeAll();
		getOutputController().closeRuleWriters();
	}

	protected PrintWriter getPrintWriter(String status, OutputController outputController) throws RuleGenerationException {
		return outputController.getAEFileWriter(status, PROCESS_FILENAME);
	}

	protected String getErrorContext() {
		return (currentCaseBase == null ? "" : currentCaseBase.getId() + ": " + currentCaseBase.getName());
	}

}
package com.mindbox.pe.server.generator;

import static com.mindbox.pe.common.LogUtil.logInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.cbr.CBRAttribute;
import com.mindbox.pe.model.cbr.CBRAttributeValue;
import com.mindbox.pe.model.cbr.CBRCase;
import com.mindbox.pe.model.cbr.CBRCaseAction;
import com.mindbox.pe.model.cbr.CBRCaseBase;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.server.cache.CBRManager;


/**
 * CBR data generator.
 *
 * @author Inna Nill
 * @since PowerEditor 4.1.0
 */
public class CBRGenerator implements ErrorContextProvider {

	private static final Logger LOG = Logger.getLogger(CBRGenerator.class);

	private static final String UNSPECIFIED = ":UNSPECIFIED";
	private static final String PERFECT = ":PERFECT";


	private CBRCaseBase currentCaseBase = null;
	private Map<CBRCaseBase, List<CBRAttribute>> caseBaseToAttributeMap = null;
	private Map<CBRCaseBase, List<CBRCase>> caseBaseToCaseMap = null;
	private SimpleDateFormat dateFormat = null;;
	private final BufferedGenerator bufferedGenerator;

	public CBRGenerator(GenerateStats generateStats, OutputController outputController) throws RuleGenerationException {
		this.bufferedGenerator = new DefaultBufferedGenerator(generateStats, outputController, outputController.getCbrFile(), this);
		currentCaseBase = null;
		caseBaseToAttributeMap = new HashMap<CBRCaseBase, List<CBRAttribute>>();
		caseBaseToCaseMap = new HashMap<CBRCaseBase, List<CBRCase>>();
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

	private void generateAttribute(CBRAttribute attr) {
		bufferedGenerator.nextLine();
		bufferedGenerator.openParan();
		bufferedGenerator.print("cbr::define-pe-attribute ");
		printConditionalAsString(attr.getName());
		bufferedGenerator.nextLineIndent();
		printConditionalAsString(attr.getCaseBase().getName());
		bufferedGenerator.nextLine();
		printConditional(attr.getAttributeType().getSymbol());
		bufferedGenerator.nextLine();
		printConditional(attr.getMatchContribution(), true);
		bufferedGenerator.nextLine();
		printConditional(attr.getMismatchPenalty(), true);
		bufferedGenerator.nextLine();
		printConditional(attr.getAbsencePenalty(), true);
		bufferedGenerator.nextLine();
		printConditional(attr.getLowestValue());
		bufferedGenerator.nextLine();
		printConditional(attr.getHighestValue());
		bufferedGenerator.nextLine();
		printConditional(attr.getMatchInterval());
		bufferedGenerator.nextLine();
		printConditional(attr.getValueRange().getSymbol());
		bufferedGenerator.nextLine();
		printConditionalAsString(attr.getDescription());
		bufferedGenerator.nextLineOutdent();
		bufferedGenerator.closeParan();
		bufferedGenerator.nextLine();
		bufferedGenerator.getGenerateStats().incrementObjectCount();
	}

	private void generateAttributes(CBRCaseBase caseBase) {
		List<CBRAttribute> attributes = caseBaseToAttributeMap.get(caseBase);
		for (Iterator<CBRAttribute> iter = attributes.iterator(); iter.hasNext();) {
			CBRAttribute element = iter.next();
			generateAttribute(element);
		}
	}

	private void generateAttributeValues(List<CBRAttributeValue> attrVals) {
		bufferedGenerator.print(":values");
		bufferedGenerator.nextLine();
		for (Iterator<CBRAttributeValue> it = attrVals.iterator(); it.hasNext();) {
			CBRAttributeValue val = (CBRAttributeValue) it.next();
			if (val.getAttribute() != null) {
				printConditionalAsString(val.getAttribute().getName());
			}
			else {
				bufferedGenerator.print(UNSPECIFIED);
			}
			bufferedGenerator.nextLine();
			printConditionalAsString(val.getName()); // This is the attribute-value VALUE.
			bufferedGenerator.nextLine();
			printConditional(val.getMatchContribution(), true);
			bufferedGenerator.nextLine();
			printConditional(val.getMismatchPenalty(), true);
			bufferedGenerator.nextLine();
			printConditionalAsString(val.getDescription());
			bufferedGenerator.nextLine();
		}
	}

	private void generateCase(CBRCase cbrCase) {
		bufferedGenerator.nextLine();
		bufferedGenerator.openParan();
		bufferedGenerator.print("cbr::define-pe-case ");
		printConditionalAsString(cbrCase.getName());
		bufferedGenerator.nextLineIndent();
		printConditionalAsString(cbrCase.getCaseBase().getName());
		bufferedGenerator.nextLine();
		if (cbrCase.getEffectiveDate() != null) {
			printConditionalAsString(cbrCase.getEffectiveDate().getDate());
			bufferedGenerator.nextLine();
		}
		if (cbrCase.getExpirationDate() != null) {
			printConditionalAsString(cbrCase.getExpirationDate().getDate());
			bufferedGenerator.nextLine();
		}
		printConditionalAsString(cbrCase.getDescription());
		bufferedGenerator.nextLine();
		List<CBRAttributeValue> attrVals = cbrCase.getAttributeValues();
		List<CBRCaseAction> actions = cbrCase.getCaseActions();
		if ((attrVals != null) && (attrVals.size() > 0)) {
			generateAttributeValues(attrVals);
		}
		if ((actions != null) && (actions.size() > 0)) {
			generateCaseActions(actions);
		}
		bufferedGenerator.nextLineOutdent();
		bufferedGenerator.closeParan();
		bufferedGenerator.nextLine();
		bufferedGenerator.getGenerateStats().incrementObjectCount();
	}

	private void generateCaseActions(List<CBRCaseAction> actions) {
		bufferedGenerator.print(":actions");
		bufferedGenerator.nextLine();
		for (Iterator<CBRCaseAction> it = actions.iterator(); it.hasNext();) {
			CBRCaseAction val = (CBRCaseAction) it.next();
			printConditional(val.getSymbol());
			bufferedGenerator.nextLine();
		}
	}

	private void generateCaseBase(CBRCaseBase caseBase) {
		currentCaseBase = caseBase;
		bufferedGenerator.nextLine();
		bufferedGenerator.openParan();
		bufferedGenerator.print("cbr::define-pe-case-base ");
		printConditionalAsString(caseBase.getName());
		bufferedGenerator.nextLineIndent();
		printConditional((caseBase.getCaseClass() == null ? UNSPECIFIED : caseBase.getCaseClass().getSymbol()));
		bufferedGenerator.nextLine();
		printConditionalAsString(caseBase.getIndexFile());
		bufferedGenerator.nextLine();
		printConditional((caseBase.getScoringFunction() == null ? UNSPECIFIED : caseBase.getScoringFunction().getSymbol()));
		bufferedGenerator.nextLine();
		printConditional(caseBase.getNamingAttribute());
		bufferedGenerator.nextLine();
		printConditional(caseBase.getMatchThreshold());
		bufferedGenerator.nextLine();
		printConditional(caseBase.getMaximumMatches());
		bufferedGenerator.nextLine();
		if (caseBase.getEffectiveDate() != null) {
			printConditionalAsString(caseBase.getEffectiveDate().getDate());
			bufferedGenerator.nextLine();
		}
		if (caseBase.getExpirationDate() != null) {
			printConditionalAsString(caseBase.getExpirationDate().getDate());
			bufferedGenerator.nextLine();
		}
		printConditionalAsString(caseBase.getDescription());
		bufferedGenerator.nextLineOutdent();
		bufferedGenerator.closeParan();
		bufferedGenerator.nextLine();
		bufferedGenerator.getGenerateStats().incrementObjectCount();
	}

	private void generateCases(CBRCaseBase caseBase) {
		List<CBRCase> cases = caseBaseToCaseMap.get(caseBase);
		for (Iterator<CBRCase> iter = cases.iterator(); iter.hasNext();) {
			CBRCase element = iter.next();
			generateCase(element);
		}
	}

	public synchronized void generateCBRData(final int percentageAllocation) throws RuleGenerationException {
		logInfo(LOG, ">>> generateCBRData: %d", percentageAllocation);
		int percentageAdded = 0;
		try {
			final List<CBRCaseBase> caseBases = CBRManager.getInstance().getCBRCaseBases();
			final int percentageToAdd = percentageAllocation / caseBases.size();

			bufferedGenerator.startGeneration();

			bufferedGenerator.print(";; Generated on " + Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format(new Date()) + " by PowerEditor");
			bufferedGenerator.nextLine();

			bufferedGenerator.nextLine();
			bufferedGenerator.print(";;------- CASE BASES -------");
			bufferedGenerator.nextLine();
			for (CBRCaseBase caseBase : caseBases) {
				generateCaseBase(caseBase);
				bufferedGenerator.writeOut();

				generateAttributes(caseBase);
				bufferedGenerator.writeOut();

				generateCases(caseBase);
				bufferedGenerator.writeOut();

				bufferedGenerator.getGenerateStats().addPercentComplete(percentageToAdd);
				percentageAdded += percentageToAdd;
			}
		}
		catch (Exception ex) {
			LOG.error("Error while generating CBR data", ex);
			bufferedGenerator.reportError(ex.getMessage());
		}
		finally {
			bufferedGenerator.endGeneration();
			bufferedGenerator.getGenerateStats().addPercentComplete(percentageAllocation - percentageAdded);
			currentCaseBase = null;
		}
	}

	@Override
	public String getErrorContext() {
		return (currentCaseBase == null ? "" : currentCaseBase.getId() + ": " + currentCaseBase.getName());
	}

	private void printAsString(Date value) {
		printAsString(dateFormat.format(value));
	}

	private void printAsString(String value) {
		bufferedGenerator.print('"' + value + '"');
	}

	private void printConditional(double value) {
		if (value == Constants.CBR_NULL_DOUBLE_VALUE) {
			bufferedGenerator.print(UNSPECIFIED);
		}
		else {
			bufferedGenerator.print(value);
		}
	}

	private void printConditional(int value) {
		if (value == Constants.CBR_NULL_DATA_EQUIVALENT_VALUE) {
			bufferedGenerator.print(UNSPECIFIED);
		}
		else {
			bufferedGenerator.print(value);
		}
	}

	private void printConditional(int value, boolean considerPerfect) {
		if (considerPerfect == true && value == CBRAttribute.PERFECT_VALUE) {
			bufferedGenerator.print(PERFECT);
		}
		else {
			printConditional(value);
		}
	}

	private void printConditional(String value) {
		if (value == null || value.equals("")) {
			bufferedGenerator.print(UNSPECIFIED);
		}
		else {
			bufferedGenerator.print(value);
		}
	}

	private void printConditionalAsString(Date value) {
		if (value == null) {
			bufferedGenerator.print(UNSPECIFIED);
		}
		else {
			printAsString(value);
		}
	}

	private void printConditionalAsString(String value) {
		if (value == null || value.equals("")) {
			bufferedGenerator.print(UNSPECIFIED);
		}
		else {
			printAsString(value);
		}
	}

}
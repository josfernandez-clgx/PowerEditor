package com.mindbox.pe.server.generator;

import java.io.PrintWriter;

import org.apache.log4j.Logger;

import com.mindbox.pe.model.DomainAttribute;
import com.mindbox.pe.model.DomainClass;
import com.mindbox.pe.model.GenericEntity;
import com.mindbox.pe.model.GenericEntityType;
import com.mindbox.pe.model.deploy.GenerateStats;
import com.mindbox.pe.server.cache.DomainManager;
import com.mindbox.pe.server.cache.EntityManager;


/**
 * Abstract buffered generator.
 * Must call {@link #resetStatus(String)} before making calls to writer methods.
 * <p>
 * <b>Usage</b>:<br>
 * <ol>
 * <li>Call {@link #init(OutputController)} before each unique deployment process</li>
 * <li>Call {@link #resetStatus(String)} before status changes for each activations/objects</li>
 * </ol> 
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
public abstract class AbstractBufferedGenerator implements GeneratorErrorContainer {

	static final String COMMENT_PREFIX = ";; ";
	static final String LINE_SEPARATOR = System.getProperty("line.separator");
	static final String QUOTE = RuleGeneratorHelper.QUOTE;

	protected synchronized void resetTab() {
		currentTabLevel = 0;
		tabSpaces = 3;
	}

	final Logger logger;

	private StringBuffer buff = null;
	private int currentTabLevel;
	private int tabSpaces;
	private OutputController outputController = null;

	private int generatedObjectCount = 0;
	private int generatedRuleCount = 0;
	private int errorCount = 0;
	private String currentStatus;
	private int percentComplete = 0;

	protected AbstractBufferedGenerator() {
		super();
		logger = Logger.getLogger(getClass());
	}

	protected final String getInitStatus() {
		return outputController.getStatus();
	}

	protected synchronized void init(OutputController outputController) {
		percentComplete = 0;
		generatedObjectCount = 0;
		generatedRuleCount = 0;
		errorCount = 0;
		tabSpaces = 3;
		currentTabLevel = 0;
		this.outputController = outputController;
		this.buff = new StringBuffer();
		currentStatus = outputController.getStatus();
	}

	protected synchronized void resetStatus(String status) {
		currentStatus = status;
	}

	/**
	 * Changed modifier to protected for easier unit testing.
	 * @param status
	 * @param outputController
	 * @return PrintWriter
	 * @throws RuleGenerationException on error
	 */
	protected abstract PrintWriter getPrintWriter(String status, OutputController outputController) throws RuleGenerationException;

	/**
	 * Flushes buffer and writes all in the buffer.
	 * Note: <b>This clears the buffer</b>.
	 *
	 */
	public void writeAll() throws RuleGenerationException {
		if (currentStatus == null) throw new IllegalStateException("Set status first");
		if (buff != null) getPrintWriter(currentStatus, outputController).println(buff.toString());
		buff.delete(0, buff.length());
	}

	public final OutputController getOutputController() {
		return outputController;
	}

	/**
	 * @param type
	 * @param entityID
	 * @param usageType the usage type
	 * @since 3.0.0
	 */
	void writeGenericEntityPattern(GenericEntityType type, int entityID) throws RuleGenerationException {
		logger.debug("writeGenericEntityPattern(" + type + "," + entityID + ")");
		GenericEntity entity = EntityManager.getInstance().getEntity(type, entityID);
		if (entity != null) {
			print(RuleGeneratorHelper.getGenericEntityIDValue(entity));
		}
		else {
			print("ERROR-" + type + "-" + entityID + "-not-found");
			reportError("No entity of type " + type + " with id " + entityID + " exists");
		}
	}


	final String getMappedClassDeployLabel(String className) throws RuleGenerationException {
		DomainClass domainclass = DomainManager.getInstance().getDomainClass(className);
		if (domainclass != null) {
			return domainclass.getDeployLabel();
		}
		else {
			reportError("No class with name " + className + " found");
			throw new RuleGenerationException("Could not locate class " + className);
		}
	}

	final String getMappedAttributeDeployLabel(String className, String attrName) throws RuleGenerationException {
		DomainClass domainclass = DomainManager.getInstance().getDomainClass(className);
		if (domainclass != null) {
			DomainAttribute domainattribute = domainclass.getDomainAttribute(attrName);
			if (domainattribute != null) {
				return domainattribute.getDeployLabel();
			}
			else {
				reportError("Class " + className + " does not have an attribute named " + attrName);
				throw new RuleGenerationException("Could not locate attrib " + attrName + " for class " + className);
			}
		}
		else {
			reportError("No class with name " + className + " found");
			throw new RuleGenerationException("Could not locate class " + className);
		}
	}

	final void quote() {
		buff.append(QUOTE);
	}

	final void printlnComment(String comment) {
		print(COMMENT_PREFIX);
		print(comment);
		nextLine();
	}
	
	final void print(String str) {
		buff.append(str);
	}

	/**
	 * @param i
	 * @since 3.0.0
	 */
	final void print(int i) {
		buff.append(i);
	}

	final void print(double i) {
		buff.append(i);
	}

	/**
	 * @param c
	 * @since 3.0.0
	 */
	final void print(char c) {
		buff.append(c);
	}

	final void outdent(int i) {
		currentTabLevel = currentTabLevel - i;
		if (currentTabLevel < 0) currentTabLevel = 0;
	}

	final void outdent() {
		outdent(1);
	}

	final void nextLine(int count) {
		for (int j = 0; j < count; j++) {
			buff.append(LINE_SEPARATOR);
		}

		for (int k = 0; k < currentTabLevel * tabSpaces; k++) {
			buff.append(" ");
		}
	}

	final void nextLine() {
		nextLine(1);
	}

	final void nextLineIndent() {
		indent();
		nextLine();
	}

	final void nextLineOutdent() {
		outdent();
		nextLine();
	}

	final void indent() {
		currentTabLevel++;
	}

	final void openParan() {
		buff.append("(");
	}

	final void closeParan() {
		buff.append(")");
	}

	final void incrementObjectCount() {
		++generatedObjectCount;
	}

	final void incrementRuleCount() {
		++generatedRuleCount;
	}

	final void incrementRuleCount(int inc) {
		generatedRuleCount += inc;
	}

	final void incrementErrorCount(int inc) {
		errorCount += inc;
	}

	public final int getGeneratedObjectCount() {
		return generatedObjectCount;
	}

	public final int getGeneratedRuleCount() {
		return generatedRuleCount;
	}

	public final int getErrorCount() {
		return errorCount;
	}

	public final void setPercentageComplete(int percentage) {
		this.percentComplete = percentage;
	}
	
	public GenerateStats getStats() {
		GenerateStats stats = new GenerateStats();
		stats.setNumErrorsGenerated(errorCount);
		stats.setNumObjectsGenerated(generatedObjectCount);
		stats.setNumRulesGenerated(generatedRuleCount);
		stats.setDeployDir(getOutputController().getDeployDir());
		stats.setPercentComplete(percentComplete);
		return stats;
	}
	
	protected abstract String getErrorContext();

	public final void reportError(String str) throws RuleGenerationException {
		String context = getErrorContext();
		outputController.writeErrorMessage((context == null ? "" : context), str);
		++errorCount;
	}
}
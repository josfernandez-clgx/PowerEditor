package com.mindbox.pe.server.generator;

import java.io.File;

import com.mindbox.pe.model.TemplateUsageType;

/**
 * Manages various deploy generated files for a particular status.
 * Responsible for determining the deploy directory and the names of the generated files.
 * <p>
 * Create one instance for each unique deployment.
 * @author Geneho
 * @since PowerEditor 1.0
 */
public interface OutputController {

	//	/**
	//	 * Gets a PrintWriter that writes to a file whose name is &quot;<code>name</code>.art&quot; in the deploy directory.
	//	 * @param name the name of the file (without extension)
	//	 * @return the PrintWriter
	//	 * @since PowerEditor 3.3.0
	//	 */
	//	File getAEFile(String name) throws RuleGenerationException;

	File getCbrFile() throws RuleGenerationException;

	String getDeployDir();

	File getErrorFile() throws RuleGenerationException;

	File getParameterFile() throws RuleGenerationException;

	File getProcessFile() throws RuleGenerationException;

	File getRuleFile(GenerationParams generationParams) throws RuleGenerationException;

	File getRuleFile(TemplateUsageType templateUsageType) throws RuleGenerationException;

	File getRuleFile(TemplateUsageType templateUsageType, String templateName) throws RuleGenerationException;

	String getStatus();

	File getTimeSliceFile() throws RuleGenerationException;

	void writeErrorMessage(String context, String message) throws RuleGenerationException;
}
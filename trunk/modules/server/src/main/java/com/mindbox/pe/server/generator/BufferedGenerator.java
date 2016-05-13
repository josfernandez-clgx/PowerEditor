package com.mindbox.pe.server.generator;

import java.io.IOException;

import com.mindbox.pe.model.deploy.GenerateStats;


/**
 * Writer that uses internal buffer for a specific file to hold parts of generated content to avoid writing partial content.
 * 
 * <h3>Usage</h3>
 * <p>
 * <ol>
 * <li>Call {@link #startGeneration()}
 * <li>Call writer methods (e.e., {@link #indent()}, {@link #openParan()}, {@link #print(String)}, etc.)</li>
 * <li>When done with a complete unit (e.g., a rule), call {@link #writeAll()}.</li>
 * <li>When done with the file, call {@link #endGeneration()}</li>
 * </p>
 */
interface BufferedGenerator extends GeneratorErrorContainer {

	void closeParan();

	/**
	 * This must be called when done with this instance to close all resources properly.
	 */
	void endGeneration();

	GenerateStats getGenerateStats();

	OutputController getOutputController();

	void indent();

	void nextLine();

	void nextLineIndent();

	void nextLineOutdent();

	void openParan();

	void outdent();

	void print(char c);

	void print(double i);

	void print(int i);

	void print(String str);

	void printlnComment(String comment);

	void quote();

	void resetTab();

	/**
	 * This must be called before all other calls.
	 */
	void startGeneration() throws IOException;

	/**
	 * Flushes buffer and writes all in the buffer.
	 * Note: <b>This clears the buffer</b>.
	 *
	 */
	void writeOut() throws IOException;
}

package com.mindbox.pe.server.generator;

import static com.mindbox.pe.common.LogUtil.logDebug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.mindbox.pe.common.IOUtil;
import com.mindbox.pe.model.deploy.GenerateStats;

/**
 * Default implementation of {@link BufferedGenerator}.
 * <p><b>This is not thread-safe.</b></p>.
 * 
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.2.0
 */
final class DefaultBufferedGenerator implements BufferedGenerator {

	private static final Logger LOG = Logger.getLogger(DefaultBufferedGenerator.class);

	static final String COMMENT_PREFIX = ";; ";
	static final String LINE_SEPARATOR = System.getProperty("line.separator");
	static final String QUOTE = RuleGeneratorHelper.QUOTE;

	private final StringBuilder buff;
	private final GenerateStats generateStats;
	private int currentTabLevel = 0;
	private int tabSpaces = 3;
	private final File targetFile;
	private BufferedWriter writer = null;
	private final OutputController outputController;
	private final ErrorContextProvider errorContextProvider;

	//	private final 

	DefaultBufferedGenerator(final GenerateStats generateStats, final OutputController outputController, final File targetFile, final ErrorContextProvider errorContextProvider) {
		if (generateStats == null) {
			throw new IllegalArgumentException("generateStats cannot be null");
		}
		if (outputController == null) {
			throw new IllegalArgumentException("outputController cannot be null");
		}
		if (targetFile == null) {
			throw new IllegalArgumentException("targetFile cannot be null");
		}
		if (errorContextProvider == null) {
			throw new IllegalArgumentException("errorContextProvider cannot be null");
		}
		this.buff = new StringBuilder();
		this.outputController = outputController;
		this.generateStats = generateStats;
		this.targetFile = targetFile;
		this.errorContextProvider = errorContextProvider;
	}

	@Override
	public final void closeParan() {
		buff.append(")");
	}

	@Override
	public void endGeneration() {
		IOUtil.close(writer);
	}

	@Override
	public GenerateStats getGenerateStats() {
		return generateStats;
	}

	@Override
	public OutputController getOutputController() {
		return outputController;
	}

	@Override
	public final void indent() {
		currentTabLevel++;
	}

	@Override
	public final void nextLine() {
		nextLine(1);
	}

	final void nextLine(int count) {
		for (int j = 0; j < count; j++) {
			buff.append(LINE_SEPARATOR);
		}

		for (int k = 0; k < currentTabLevel * tabSpaces; k++) {
			buff.append(" ");
		}
	}

	@Override
	public final void nextLineIndent() {
		indent();
		nextLine();
	}

	@Override
	public final void nextLineOutdent() {
		outdent();
		nextLine();
	}

	@Override
	public final void openParan() {
		buff.append("(");
	}

	@Override
	public final void outdent() {
		outdent(1);
	}

	final void outdent(int i) {
		currentTabLevel = currentTabLevel - i;
		if (currentTabLevel < 0) {
			currentTabLevel = 0;
		}
	}

	@Override
	public final void print(char c) {
		buff.append(c);
	}

	@Override
	public final void print(double i) {
		buff.append(i);
	}

	@Override
	public final void print(int i) {
		buff.append(i);
	}

	@Override
	public final void print(String str) {
		buff.append(str);
	}

	@Override
	public final void printlnComment(String comment) {
		print(COMMENT_PREFIX);
		print(comment);
		nextLine();
	}

	@Override
	public final void quote() {
		buff.append(QUOTE);
	}

	@Override
	public final void reportError(String str) throws RuleGenerationException {
		final String context = errorContextProvider.getErrorContext();
		outputController.writeErrorMessage((context == null ? "" : context), str);
		generateStats.incrementNumErrors();
	}

	@Override
	public synchronized void resetTab() {
		currentTabLevel = 0;
		tabSpaces = 3;
	}

	@Override
	public void startGeneration() throws IOException {
		writer = new BufferedWriter(new FileWriter(targetFile, true));
	}

	@Override
	public void writeOut() throws IOException {
		if (writer == null) {
			throw new IllegalStateException("call startGeneration() first");
		}
		logDebug(LOG, "---> writeOut");
		synchronized (buff) {
			if (buff.length() > 0) {
				writer.write(buff.toString());
				writer.flush();
				logDebug(LOG, "writeOut: wrote %s", buff.length());
				buff.delete(0, buff.length());
			}
		}
		logDebug(LOG, "<--- writeOut");
	}
}
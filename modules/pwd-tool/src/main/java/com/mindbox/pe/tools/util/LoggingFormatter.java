/*
 * Created on 2004. 2. 2.
 *
 */
package com.mindbox.pe.tools.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 2.3.0
 */
public class LoggingFormatter extends SimpleFormatter {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final MessageFormat logFormat =
		new MessageFormat("{0,date,yyyy-MM-dd HH:mm:sss} {1} {2}: {3}" + LINE_SEPARATOR);
	private static final MessageFormat logFormat_Exception =
		new MessageFormat(
			"{0,date,yyyy-MM-dd HH:mm:sss} {1} {2}: {3}"
				+ LINE_SEPARATOR
				+ "{4}"
				+ LINE_SEPARATOR);

	public synchronized String format(LogRecord arg0) {
		if (arg0.getThrown() != null) {
			StringWriter writer = new StringWriter();
			arg0.getThrown().printStackTrace(new PrintWriter(writer));
			return logFormat_Exception.format(
				new Object[] {
					new Date(),
					arg0.getLoggerName(),
					arg0.getLevel(),
					arg0.getMessage(),
					writer.toString()});
		}
		else {
			return logFormat.format(
				new Object[] { new Date(), arg0.getLoggerName(), arg0.getLevel(), arg0.getMessage()});
		}
	}

}

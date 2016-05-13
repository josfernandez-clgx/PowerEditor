package com.mindbox.pe.client.common.formatter;

/**
 * Factory for formatter for Swing's JFormattedTextFields.
 * @author Geneho Kim
 * @author MindBox
 */
public class FormatterFactory {

	private static javax.swing.JFormattedTextField.AbstractFormatter nameFormatter = null;
	private static javax.swing.JFormattedTextField.AbstractFormatter symbolFormatter = null;
	private static javax.swing.JFormattedTextField.AbstractFormatter timeFormatter = null;
	private static javax.swing.JFormattedTextField.AbstractFormatter dateTimeFormatter = null;
	
	/**
	 * 
	 * @return the name formatter
	 */
	public static javax.swing.JFormattedTextField.AbstractFormatter getNameFormatter() {
		if (nameFormatter == null) {
			nameFormatter = new StringFormatter(new NameDocumentFilter());
		}
		return nameFormatter;
	}

	/**
	 * 
	 * @return the symbol formatter
	 * @since 3.0.0
	 */
	public static javax.swing.JFormattedTextField.AbstractFormatter getSymbolFormatter() {
		if (symbolFormatter == null) {
			symbolFormatter = new StringFormatter(new SymbolDocumentFilter());
		}
		return symbolFormatter;
	}

	/**
	 * 
	 * @return the time formatter
	 */
	public static javax.swing.JFormattedTextField.AbstractFormatter getTimeFormatter() {
		if (timeFormatter == null) {
			timeFormatter = new StringFormatter(new TimeDocumentFilter());
		}
		return timeFormatter;
	}

	private FormatterFactory() {
	}

	/**
	 * 
	 * @return the date-time formatter
	 */
	public static javax.swing.JFormattedTextField.AbstractFormatter getDateTimeFormatter() {
		if (dateTimeFormatter == null) {
			dateTimeFormatter = new StringFormatter(new DateTimeDocumentFilter());
		}
		return dateTimeFormatter;
	}
}

package com.mindbox.pe.server.generator;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Default implementation of {@link FormatterFactory}.
 * <p>This is <b>NOT</b> thread-safe. It's recommended to be used with ThreadLocal.</p>
 *
 * @since 5.9.0
 */
final class DefaultFormatterFactory implements FormatterFactory {

	private static final String DEFAULT_FLOAT_FORMATTER_STRING = "############0.0####";
	private static final String DEFAULT_CURRENCY_FORMATTER_STRING = "############0.00";

	private static DecimalFormat createDecimalFormatter(final int precision) {
		final DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(precision);
		decimalFormat.setMinimumFractionDigits(precision);
		decimalFormat.setGroupingUsed(false);
		return decimalFormat;
	}

	private final boolean ignorePrecision;
	private DecimalFormat defaultCurrencyFormatter;
	private DecimalFormat defaultFloatFormatter;
	private final Map<Integer, DecimalFormat> formatterMap = new HashMap<Integer, DecimalFormat>();
	private final SimpleDateFormat ruleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private final SimpleDateFormat ruleDateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	DefaultFormatterFactory(boolean ignorePrecision) {
		this.ignorePrecision = ignorePrecision;
	}

	@Override
	public DecimalFormat getCurrencyFormatter(Integer precision) {
		if (precision == null || ignorePrecision) {
			return getDefaultCurrentFormatter();
		}
		else {
			return getFormatter(precision);
		}
	}

	private DecimalFormat getDefaultCurrentFormatter() {
		if (defaultCurrencyFormatter == null) {
			defaultCurrencyFormatter = new DecimalFormat(DEFAULT_CURRENCY_FORMATTER_STRING);
		}
		return defaultCurrencyFormatter;
	}

	private DecimalFormat getDefaultFloatFormatter() {
		if (defaultFloatFormatter == null) {
			defaultFloatFormatter = new DecimalFormat(DEFAULT_FLOAT_FORMATTER_STRING);
		}
		return defaultFloatFormatter;
	}

	@Override
	public DecimalFormat getFloatFormatter(Integer precision) {
		if (precision == null || ignorePrecision) {
			return getDefaultFloatFormatter();
		}
		else {
			return getFormatter(precision);
		}
	}

	private DecimalFormat getFormatter(final int precision) {
		if (formatterMap.containsKey(precision)) {
			return formatterMap.get(precision);
		}

		final DecimalFormat decimalFormat = createDecimalFormatter(precision);
		formatterMap.put(precision, decimalFormat);
		return decimalFormat;
	}

	@Override
	public SimpleDateFormat getRuleDateFormat() {
		return ruleDateFormat;
	}

	@Override
	public SimpleDateFormat getRuleDateTimeFormat() {
		return ruleDateTimeFormat;
	}
}

package com.mindbox.pe.server.generator;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public interface FormatterFactory {

	DecimalFormat getCurrencyFormatter(final Integer precision);

	DecimalFormat getFloatFormatter(final Integer precision);

	SimpleDateFormat getRuleDateFormat();

	SimpleDateFormat getRuleDateTimeFormat();
}

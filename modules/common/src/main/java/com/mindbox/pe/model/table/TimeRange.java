package com.mindbox.pe.model.table;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.exceptions.InvalidDataException;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;

/**
 * @since PowerEditor 3.3.0p5
 */
public class TimeRange extends IntegerRange {

	private static final long serialVersionUID = 20070515000018L;

	public static String getDefaultValue(ColumnDataSpecDigest columnDataSpecDigest) {
		TimeRange range = new TimeRange();
		range.setLowerValue(0);
		range.setUpperValue(24 * 60 * 60);
		return range.toString();
	}

	public static Integer toTimeInteger(String timeStr) throws InvalidDataException {
		if (timeStr == null || timeStr.length() == 0) return null;
		String[] strs = timeStr.split(":");
		try {
			if (strs.length == 2) {
				int hr = Integer.parseInt(strs[0]);
				if (hr < 0 || hr > 23) return null;
				int min = Integer.parseInt(strs[1]);
				if (min < 0 || min > 59) return null;
				return new Integer(hr * 60 * 60 + min * 60);
			}
			else if (strs.length == 3) {
				int hr = Integer.parseInt(strs[0]);
				if (hr < 0 || hr > 23) return null;
				int min = Integer.parseInt(strs[1]);
				if (min < 0 || min > 59) return null;
				int sec = Integer.parseInt(strs[2]);
				if (sec < 0 || sec > 59) return null;
				return new Integer(hr * 60 * 60 + min * 60 + sec);
			}
			else {
				return null;
			}
		}
		catch (NumberFormatException ex) {
			throw new InvalidDataException();
		}
		catch (Exception ex) {
			return null;
		}
	}

	private static String toZeroFilledString(int value) {
		if (value < 10)
			return "0" + value;
		else
			return String.valueOf(value);
	}

	public static String toTimeString(Integer value) {
		if (value == null) return null;
		int hr = (int) Math.floor(value.doubleValue() / 3600);
		int min = (int) Math.floor((value.doubleValue() - (hr * 60 * 60)) / 60);
		int sec = value.intValue() % 60;

		return toZeroFilledString(hr) + ":" + toZeroFilledString(min) + ":" + toZeroFilledString(sec);
	}

	public static String getRangeValueString(TimeRange range) {
		if (range == null || (range.getLowerValue() == null && range.getUpperValue() == null)) {
			return "";
		}
		StringBuilder buff = new StringBuilder();
		if ((range.getLowerValue() == null) && (range.getUpperValue() == null)) {
			buff.append(' ');
			return buff.toString();
		}
		if (range.getLowerValue() != null) {
			buff.append((range.isLowerValueInclusive() ? '[' : '('));
			buff.append(toTimeString(range.getLowerValue()));
		}
		else {
			buff.append(' ');
		}
		buff.append('-');
		if (range.getUpperValue() != null) {
			buff.append(toTimeString(range.getUpperValue()));
			buff.append((range.isUpperValueInclusive() ? ']' : ')'));
		}
		else {
			buff.append(' ');
		}
		return buff.toString();
	}

	public static TimeRange parseTimeRangeValue(String s) {
		TimeRange timeRange = new TimeRange();
		if (s != null && s.length() > 0) {
			try {
				String strToParse = UtilBase.removeBlanks(s);

				Pattern pattern = Pattern.compile("^([\\(\\[][0-9]{0,2}:[0-9]{0,2}:[0-9]{0,2})?\\-([0-9]{0,2}:[0-9]{0,2}:[0-9]{0,2}[\\)\\]])?$");
				Matcher matcher = pattern.matcher(strToParse);
				if (matcher.matches()) {

					if (matcher.group(1) != null) {
						timeRange.setLowerValueInclusive((matcher.group(1).charAt(0) == '['));
						timeRange.setLowerValue(toTimeInteger(matcher.group(1).substring(1)));
					}
					else {
						timeRange.setLowerValue(null);
					}
					if (matcher.group(2) != null) {
						int lastIndex = matcher.group(2).length() - 1;
						timeRange.setUpperValueInclusive((matcher.group(2).charAt(lastIndex) == ']'));
						timeRange.setUpperValue(toTimeInteger(matcher.group(2).substring(0, lastIndex)));
					}
					else {
						timeRange.setUpperValue(null);
					}
				}
			}
			catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		return timeRange;
	}

	public TimeRange() {
	}

	private TimeRange(TimeRange source) {
		super(source);
	}

	@Override
	public GridCellValue copy() {
		return new TimeRange(this);
	}

	public String toString() {
		return getRangeValueString(this);
	}
}

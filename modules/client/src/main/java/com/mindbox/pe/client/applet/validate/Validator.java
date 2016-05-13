package com.mindbox.pe.client.applet.validate;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.common.validate.TextWarningConsumer;
import com.mindbox.pe.common.validate.WarningConsumer;
import com.mindbox.pe.common.validate.WarningInfo;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.comparator.AbstractDateRangeComparator;
import com.mindbox.pe.model.comparator.ActivationsComparator;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.server.parser.jtb.message.MessageParser;
import com.mindbox.pe.server.parser.jtb.message.syntaxtree.Message;

/**
 * @author Geneho Kim
 * @author MindBox
 */
public class Validator {

	private static final class DateSynonymRange {
		private DateSynonym effDS;
		private DateSynonym expDS;
		private final String status;
		private boolean newOrChanging = false;

		public DateSynonymRange(AbstractGrid<?> grid) {
			this.effDS = grid.getEffectiveDate();
			this.expDS = grid.getExpirationDate();
			this.status = grid.getStatus();
			this.newOrChanging = false;
		}

		public DateSynonymRange(DateSynonym effDS, DateSynonym expDS, String status, boolean newOrChanging) {
			this.effDS = effDS;
			this.expDS = expDS;
			this.status = status;
			this.newOrChanging = newOrChanging;
		}

		public boolean encloses(DateSynonymRange dsRange) {
			boolean startEnclosed = (effDS == null) || (effDS != null && dsRange.effDS != null && effDS.notAfter(dsRange.effDS));
			boolean endEnclosed = (expDS == null) || (expDS != null && dsRange.expDS != null && expDS.notBefore(dsRange.expDS));
			return startEnclosed && endEnclosed;
		}

		public Date getEffDate() {
			return effDS == null ? null : effDS.getDate();
		}

		public DateSynonym getEffectiveDateSynonym() {
			return effDS;
		}

		public Date getExpDate() {
			return expDS == null ? null : expDS.getDate();
		}

		public DateSynonym getExpirationDateSynonym() {
			return expDS;
		}

		public String getStatus() {
			return status;
		}

		public boolean isNewOrChanging() {
			return newOrChanging;
		}

		public boolean isSequentialTo(DateSynonymRange dsRange) {
			return expDS != null && expDS.isSameDate(dsRange.effDS) && (dsRange.expDS == null || expDS.before(dsRange.expDS));
		}

		public boolean overlapsWith(DateSynonymRange dsRange) {
			return expDS == null || dsRange.effDS == null || expDS.after(dsRange.effDS);
		}

		public void setEffectiveDateSynonym(DateSynonym ds) {
			this.effDS = ds;
		}

		public void setExpirationDateSynonym(DateSynonym ds) {
			this.expDS = ds;
		}

		@SuppressWarnings("unused")
		public void setNewOrChanging(boolean newOrChanging) {
			this.newOrChanging = newOrChanging;
		}
	}

	/**
	 * This orders date synonym ranges, oldest to newest, which is the reverse order of {@link ActivationsComparator}.
	 */
	private static class DateSynonymRangeComparator extends AbstractDateRangeComparator implements Comparator<DateSynonymRange> {

		@Override
		public int compare(DateSynonymRange o1, DateSynonymRange o2) {
			return -1 * compare(o1.getEffDate(), o1.getExpDate(), o2.getEffDate(), o2.getExpDate());
		}
	}

	private static MessageParser messageParser = null;

	private static void adjustActivationDates(List<DateSynonymRange> dsRangeList, DateSynonym effDate, DateSynonym expDate) {
		if (dsRangeList != null && !dsRangeList.isEmpty()) {
			for (int i = 0; i < dsRangeList.size(); i++) {
				DateSynonymRange dsRange = dsRangeList.get(i);
				if (ClientUtil.hasProductionRestrictions(dsRange.getStatus()) && (effDate == null || effDate.getDate().before(new Date()))) {
					break;
				}
				// target starts before new one and expires after new one starts
				if (dsRange.getEffectiveDateSynonym() == null || dsRange.getEffectiveDateSynonym().before(effDate)) {
					if (dsRange.getExpirationDateSynonym() == null || dsRange.getExpirationDateSynonym().after(effDate)) {
						dsRange.setExpirationDateSynonym(effDate);
					}
				} // target starts after new one
				else if (expDate != null && expDate.getDate() != null) {
					if (dsRange.getEffectiveDateSynonym().before(expDate)) {
						dsRange.setEffectiveDateSynonym(expDate);
					}
					if (dsRange.getExpirationDateSynonym() != null && dsRange.getExpirationDateSynonym().before(expDate)) {
						dsRange.setExpirationDateSynonym(expDate);
					}
				}
			}
		}
	}

	private static String hasOverlapOrGap(boolean allowGaps, boolean allowOverlaps, boolean ignoreEnclosedRanges, boolean checkNewOrChangedOnly, List<DateSynonymRange> dsRangeList,
			String gapMsgKey, String overlapMsgKey) {

		if (dsRangeList.size() < 2) return null;
		// sort list from oldest to newest
		Collections.sort(dsRangeList, new DateSynonymRangeComparator());
		for (int i = 1; i < dsRangeList.size(); i++) {
			DateSynonymRange range1 = dsRangeList.get(i - 1);
			DateSynonymRange range2 = dsRangeList.get(i);

			// if we are adding or changing a range, we may only want to check for problems this introduces.
			if (!checkNewOrChangedOnly || range1.isNewOrChanging() || range2.isNewOrChanging()) {
				// added check for enclosed date ranges because deleting a range covered by another range should be fine
				if (!ignoreEnclosedRanges || (!range1.encloses(range2) && !range2.encloses(range1))) {
					if ((!allowGaps && !range1.isSequentialTo(range2))) {
						return gapMsgKey;
					}
					else if (!allowOverlaps && range1.overlapsWith(range2)) {
						return overlapMsgKey;
					}
				}
			}
		}
		return null;
	}

	private static void initMessageParser(Reader reader) {
		if (messageParser == null) {
			messageParser = new MessageParser(reader);
			messageParser.disable_tracing();
		}
		else {
			messageParser.ReInit(reader);
		}
	}

	/**
	 * Validates the specified activation date range for the specified status and clone flag.
	 * @param effectiveDate effectiveDate
	 * @param expirationDate expirationDate
	 * @param status status
	 * @return error message key if invalid; <code>null</code>, otherwise
	 */
	public static String validateActivationDateRange(DateSynonym effectiveDate, DateSynonym expirationDate, String status) {
		String message = validateDateRange(effectiveDate, expirationDate);
		if (message != null) return message;
		if (!ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_EDIT_PRODUCTION_DATA) && ClientUtil.isHighestStatus(status)
				&& (expirationDate != null && expirationDate.getDate() != null && expirationDate.getDate().before(new Date()))) {
			return "InvalidExpirationDateMsg";
		}
		return null;
	}

	public static String validateDateRange(DateSynonym effectiveDate, DateSynonym expirationDate) {
		if (effectiveDate == null) {
			return "msg.warning.no.activation.date";
		}
		else if (effectiveDate != null && expirationDate != null && effectiveDate.after(expirationDate)) {
			return "InvalidActivationDateRangeMsg";
		}
		return null;
	}

	/**
	 * Ensures that no other grid in the list has the same activation dates
	 * 
	 * @param abstractgrid abstractgrid
	 * @param gridList gridList
	 * @param dsEff dsEff
	 * @param dsExp dsExp
	 * @param cloneFlag cloneFlag
	 * @return grid
	 */
	public static <G extends AbstractGrid<?>> String validateDuplicateDates(G abstractgrid, List<G> gridList, DateSynonym dsEff, DateSynonym dsExp, boolean cloneFlag) {
		String result = null;
		if (gridList != null && gridList.size() > 0) {
			for (G grid : gridList) {
				if (cloneFlag || !grid.equals(abstractgrid)) {
					boolean sameEffDate = (grid.getEffectiveDate() == null && dsEff == null) || (grid.getEffectiveDate() != null && grid.getEffectiveDate().isSameDate(dsEff));
					if (sameEffDate) {
						boolean sameExpDate = (grid.getExpirationDate() == null && dsExp == null)
								|| (grid.getExpirationDate() != null && grid.getExpirationDate().isSameDate(dsExp));
						if (sameEffDate && sameExpDate) {
							result = "InvalidActivationDateDuplicate";
							break;
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * @param attributeItemList attributeItemList
	 * @param text text
	 * @param columnCount columnCount
	 * @param warningConsumer warningConsumer
	 * @param infoConsumer infoConsumer
	 * @return <code>true</code> if <code>text</code> is valid; <code>false</code>, otherwise
	 * @since PowerEditor 3.2.0
	 */
	public static boolean validateDynamicString(List<String> attributeItemList, String text, int columnCount, WarningConsumer warningConsumer, TextWarningConsumer infoConsumer) {
		String textToValidate = text.trim();
		boolean result = validateMessage(textToValidate, false, columnCount, warningConsumer);
		if (result) {
			infoConsumer.clear();
			Pattern pattern = Pattern.compile("\\$([^\\$]+)\\$");
			Matcher matcher = pattern.matcher(textToValidate);
			int index = 0;
			List<String> valueList = null;
			while (matcher.find(index)) {
				if (valueList == null) {
					valueList = new ArrayList<String>();
					valueList.addAll(attributeItemList);
				}
				String refName = matcher.group(1);
				index = matcher.end() - 1;

				if (valueList.contains(refName)) {
					String msg = ClientUtil.getInstance().getMessage("msg.info.valid.reference.attribute.item", refName);
					infoConsumer.addWarning(WarningInfo.INFO, msg);
				}
			}
		}
		return result;
	}

	public static <G extends AbstractGrid<?>> String validateGapsAndOverlapsInDates(boolean allowGaps, G abstractgrid, List<G> gridList, DateSynonym dsEff, DateSynonym dsExp,
			boolean editFlag, boolean autoAdjustFlag) {

		boolean allowOverlaps = false;
		boolean ignoreEnclosedRanges = false;
		boolean checkNewOrChangedOnly = true;
		String result = null;
		if (gridList != null && gridList.size() > 0 && (!editFlag || gridList.size() > 1)) {
			// build list of date synonym pairs to check
			List<DateSynonymRange> listToCheck = new LinkedList<DateSynonymRange>();
			for (G grid : gridList) {
				if (editFlag && grid.equals(abstractgrid)) {
					listToCheck.add(new DateSynonymRange(dsEff, dsExp, "Draft", true)); // true marks it as new or changing
				}
				else {
					listToCheck.add(new DateSynonymRange(grid));
				}
			}
			if (!editFlag) {
				listToCheck.add(new DateSynonymRange(dsEff, dsExp, "Draft", true)); // true marks it as new or changing
			}
			if (autoAdjustFlag) {
				adjustActivationDates(listToCheck, dsEff, dsExp);
			}
			return hasOverlapOrGap(
					allowGaps,
					allowOverlaps,
					ignoreEnclosedRanges,
					checkNewOrChangedOnly,
					listToCheck,
					"msg.warning.invalid.act.date.not.sequential",
					"msg.warning.invalid.act.date.not.sequential");
		}
		return result;
	}

	public static <G extends AbstractGrid<?>> String validateGapsAndOverlapsInDatesInList(boolean allowGaps, boolean checkNewOrChangedOnly, List<G> gridList, String gapMsgKey,
			String overlapMsgKey) {
		boolean allowOverlaps = false;
		boolean ignoreEnclosedRanges = false;
		List<DateSynonymRange> listToCheck = new LinkedList<DateSynonymRange>();
		for (G grid : gridList) {
			listToCheck.add(new DateSynonymRange(grid));
		}
		return hasOverlapOrGap(allowGaps, allowOverlaps, ignoreEnclosedRanges, checkNewOrChangedOnly, listToCheck, gapMsgKey, overlapMsgKey);
	}

	// modified to ignore overlaps when removing since this shouldn't cause problems and should only fix them.
	public static <G extends AbstractGrid<?>> String validateGapsInDatesForRemoval(boolean allowGaps, G abstractgrid, List<G> gridList) {
		boolean allowOverlaps = true;
		boolean ignoreEnclosedRanges = true;
		boolean checkNewOrChangedOnly = false; // check all remaining date ranges - not just one with its neighbors.

		// First check without ignoring the removed item to see if there already exist gaps.
		// If gaps exist, allow removal since there already problems. (Expanding a gap is okay).
		if (!allowGaps) {
			List<DateSynonymRange> fullListToCheck = new LinkedList<DateSynonymRange>();
			for (G grid : gridList) {
				fullListToCheck.add(new DateSynonymRange(grid));
			}
			String initialGapOverlapMsg = hasOverlapOrGap(
					allowGaps,
					allowOverlaps,
					ignoreEnclosedRanges,
					checkNewOrChangedOnly,
					fullListToCheck,
					"msg.warning.invalid.act.date.not.sequential.gaps",
					"msg.warning.invalid.act.date.not.sequential");
			if (initialGapOverlapMsg != null) {
				allowGaps = true;
			}
		}

		// Now check the resulting set of dates.
		List<DateSynonymRange> listToCheck = new LinkedList<DateSynonymRange>();
		for (G grid : gridList) {
			if (grid.getID() != abstractgrid.getID()) {
				listToCheck.add(new DateSynonymRange(grid));
			}
		}
		return hasOverlapOrGap(
				allowGaps,
				allowOverlaps,
				ignoreEnclosedRanges,
				checkNewOrChangedOnly,
				listToCheck,
				"msg.warning.invalid.act.date.not.sequential.gaps",
				"msg.warning.invalid.act.date.not.sequential");
	}

	/**
	 * 
	 * @param message message
	 * @param isForColumn isForColumn
	 * @param columnCount columnCount
	 * @param warningConsumer warningConsumer
	 * @return <code>true</code> if the rule is valid; <code>false</code>, otherwise
	 */
	public static boolean validateMessage(String message, boolean isForColumn, int columnCount, WarningConsumer warningConsumer) {
		// Message parser disabled until implemented from JTB source
		initMessageParser(new StringReader(message));
		try {
			Message messageObject = messageParser.Message();

			return MessageVisitorValidator.getInstance().validate(messageObject, isForColumn, columnCount, warningConsumer);
		}
		catch (Throwable ex) {
			warningConsumer.addWarning(WarningInfo.ERROR, ex.getMessage());
			return false;
		}
	}
}
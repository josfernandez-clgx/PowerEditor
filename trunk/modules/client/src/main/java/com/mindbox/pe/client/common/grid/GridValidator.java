package com.mindbox.pe.client.common.grid;

import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.IClientConstants;
import com.mindbox.pe.client.applet.validate.Validator;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.model.Constants;
import com.mindbox.pe.model.grid.AbstractGrid;
import com.mindbox.pe.model.grid.AbstractGuidelineGrid;
import com.mindbox.pe.model.table.EnumValues;
import com.mindbox.pe.model.table.FloatRange;
import com.mindbox.pe.model.table.IntegerRange;
import com.mindbox.pe.model.template.AbstractTemplateCore;
import com.mindbox.pe.model.template.ColumnDataSpecDigest;
import com.mindbox.pe.model.template.GridTemplate;

public class GridValidator implements IClientConstants {

	private static <G extends AbstractGrid<?>> boolean checkActivationOverlap(List<G> list) {
		boolean flag = true;
		boolean checkNewOrChangedOnly = false;

		if (ClientUtil.getUserInterfaceConfig().getUIPolicies() != null && UtilBase.asBoolean(ClientUtil.getUserInterfaceConfig().getUIPolicies().isEnforceSequentialActivationDates(), false)) {
			String errorMessageKey = Validator.validateGapsAndOverlapsInDatesInList(
					UtilBase.asBoolean(ClientUtil.getUserInterfaceConfig().getUIPolicies().isAllowGapsInActivationDates(), false),
					checkNewOrChangedOnly,
					list,
					"msg.question.guideline.gap",
					"msg.question.guideline.overlap");
			if (errorMessageKey != null) {
				flag = ClientUtil.getInstance().showConfirmation(errorMessageKey);
			}
		}
		return flag;
	}

	private static boolean checkConsistency(AbstractGrid<?> abstractgrid, boolean flag) {
		boolean flag1 = true;
		for (int i = 0; i < abstractgrid.getNumRows(); i++) {
			for (int j = 0; j < abstractgrid.getNumRows(); j++)
				if (i != j) {
					boolean flag2 = isSubsumed(abstractgrid, i, j);
					boolean flag3 = false;
					if (!flag2 && j > i) flag3 = hasOverlap(abstractgrid, i, j);
					if (flag2 || flag3) {
						int k = 1;
						if (flag) {
							Object aobj[] = new Object[3];
							aobj[0] = new Integer(i + 1);
							aobj[1] = new Integer(j + 1);
							aobj[2] = getActivationString(abstractgrid);
							String s;
							if (flag2)
								s = ClientUtil.getInstance().getMessage("SubsumptionErrorMsg", aobj);
							else
								s = ClientUtil.getInstance().getMessage("DataOverlapErrorMsg", aobj);
							k = JOptionPane.showConfirmDialog(ClientUtil.getApplet(), s, ClientUtil.getInstance().getMessage("ValidationErrorMsgTitle"), 0);
						}
						if (k == 0)
							flag1 = false;
						else
							return false;
					}
				}

		}

		return flag1;
	}

	private static boolean checkConsistency(List<? extends AbstractGrid<?>> list, boolean flag) {
		boolean flag1 = true;
		for (int i = 0; i < list.size(); i++) {
			AbstractGrid<?> abstractgrid = (AbstractGrid<?>) list.get(i);
			flag1 = checkConsistency(abstractgrid, flag);
			if (!flag1) break;
		}

		return flag1;
	}

	private static String getActivationString(AbstractGrid<?> abstractgrid) {
		String s = "";
		if (abstractgrid.getSunrise() != null) s += ClientUtil.getInstance().getLabel("label.from") + " " + UtilBase.format(abstractgrid.getSunrise());
		String s1 = "";
		if (abstractgrid.getSunset() != null) s1 = ClientUtil.getInstance().getLabel("label.until") + UtilBase.format(abstractgrid.getSunset());
		return s + " " + s1;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static boolean hasOverlap(AbstractGrid<?> abstractgrid, int i, int j) {
		GridTemplate gridtemplate = (GridTemplate) abstractgrid.getTemplate();
		boolean flag = true;
		try {
			for (int k = 0; k < gridtemplate.getNumColumns(); k++) {
				if (!gridtemplate.isConsistencyCheckColumn(k + 1)) continue;
				// TODO Kim, 2006-09-26: refactor using interface that has a method for checking overlaps
				Object value1 = abstractgrid.getCellValueObject(i + 1, k + 1, null);
				Object value2 = abstractgrid.getCellValueObject(j + 1, k + 1, null);
				if (value1 == null || value2 == null) return false;
				if (value2 instanceof FloatRange) {
					if (((FloatRange) value2).hasOverlap((FloatRange) value1)) continue;
					flag = false;
					break;
				}
				if (value2 instanceof IntegerRange) {
					if (((IntegerRange) value2).hasOverlap((IntegerRange) value1)) continue;
					flag = false;
					break;
				}
				if (value2 instanceof EnumValues) {
					if (((EnumValues) value2).hasOverlap((EnumValues) value1)) continue;
					flag = false;
					break;
				}
				if (value1.equals(value2)) continue;
				flag = false;
				break;
			}

		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
		return flag;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static boolean isSubsumed(AbstractGrid<?> abstractgrid, int i, int j) {
		GridTemplate gridtemplate = (GridTemplate) abstractgrid.getTemplate();
		boolean flag = true;
		try {
			for (int k = 0; k < gridtemplate.getNumColumns(); k++) {
				if (!gridtemplate.isConsistencyCheckColumn(k + 1)) continue;
				// TODO Kim, 2006-09-26: refactor using interface that has a method for checking subsumed
				Object value1 = abstractgrid.getCellValueObject(i + 1, k + 1, null);
				Object value2 = abstractgrid.getCellValueObject(j + 1, k + 1, null);
				if (value1 == null || value2 == null) return false;
				if (value2 instanceof FloatRange) {
					if (((FloatRange) value2).isSubsumedBy((FloatRange) value1)) continue;
					flag = false;
					break;
				}
				if (value2 instanceof IntegerRange) {
					if (((IntegerRange) value2).isSubsumedBy((IntegerRange) value1)) continue;
					flag = false;
					break;
				}
				if (value2 instanceof EnumValues) {
					if (((EnumValues) value2).isSubsumedBy((EnumValues) value1)) continue;
					flag = false;
					break;
				}
				if (value1.equals(value2)) continue;
				flag = false;
				break;
			}

		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
		return flag;
	}

	public static boolean validate(AbstractGrid<?> abstractgrid, boolean flag) {
		boolean flag1 = checkConsistency(abstractgrid, flag);
		return flag1;
	}

	public static boolean validate(List<? extends AbstractGrid<?>> list, boolean flag) {
		boolean flag1 = checkActivationOverlap(list) && checkConsistency(list, flag); // && checkCompleteness(list);

		// check for non draft activations with past expiration dates
		boolean flag2 = validateNonDraftActivations(list);
		return flag1 && flag2;
	}

	public static boolean validateForBlanks(AbstractGrid<?> abstractgrid) {
		boolean flag = true;
		if (abstractgrid == null) return flag;

		AbstractTemplateCore<?> gridtemplate = abstractgrid.getTemplate();
		try {
			for (int i = 0; i < gridtemplate.getNumColumns(); i++) {
				ColumnDataSpecDigest columnDataSpecDigest = gridtemplate.getColumn(i + 1).getColumnDataSpecDigest();
				for (int j = 0; j < abstractgrid.getNumRows(); j++) {
					Object value1 = abstractgrid.getCellValueObject(j + 1, i + 1, null);
					if (!(columnDataSpecDigest.getType().equals(ColumnDataSpecDigest.TYPE_BOOLEAN)) && !columnDataSpecDigest.isBlankAllowed() && (value1 == null || value1.equals("Not Found"))) {
						String msg = "Blank values in some fields are not allowed.\nPlease fill them in, or data will not be saved.";
						JOptionPane.showMessageDialog(ClientUtil.getApplet(), msg, ClientUtil.getInstance().getMessage("ValidationErrorMsgTitle"), 0);
						return false;
					}
				}
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return flag;
	}

	public static boolean validateForBlanks(List<? extends AbstractGrid<?>> list) {
		boolean flag = true;
		for (int idx = 0; idx < list.size(); idx++) {
			AbstractGuidelineGrid grid = (AbstractGuidelineGrid) list.get(idx);
			flag = validateForBlanks(grid);
			if (flag == false) return flag; // Even one false can cause a return right away.
		}
		return flag;
	}

	private static boolean validateNonDraftActivations(List<? extends AbstractGrid<?>> list) {
		boolean flag1 = true;
		for (int i = 0; i < list.size(); i++) {
			AbstractGrid<?> abstractgrid = list.get(i);
			if (abstractgrid.getStatus().equalsIgnoreCase(Constants.DRAFT_STATUS) == false) {
				if (abstractgrid.getSunset() != null) {
					if (abstractgrid.getSunset().before(new Date())) {
						String msg = "Activation : " + abstractgrid.getSunrise() + " Expiration : " + abstractgrid.getSunset() + " already expired with status: " + abstractgrid.getStatus();
						msg += ".\nPlease confirm to continue saving?";
						int j = JOptionPane.showConfirmDialog(ClientUtil.getApplet(), msg, ClientUtil.getInstance().getMessage("ErrorMsgTitle"), 0);
						if (j != 0) flag1 = false;
					}
				}
			}
		}
		return flag1;
	}

	GridValidator() {
	}
}

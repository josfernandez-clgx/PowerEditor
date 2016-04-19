package com.mindbox.pe.client.common.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.InputValidationException;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.applet.guidelines.search.TemplateReportTable;
import com.mindbox.pe.client.applet.guidelines.search.TemplateReportTableModel;
import com.mindbox.pe.common.PrivilegeConstants;
import com.mindbox.pe.common.UtilBase;
import com.mindbox.pe.common.config.ConfigUtil;
import com.mindbox.pe.communication.ServerException;
import com.mindbox.pe.model.DateSynonym;
import com.mindbox.pe.model.GuidelineReportData;
import com.mindbox.pe.model.filter.AllNamedDateSynonymFilter;
import com.mindbox.pe.model.filter.GuidelineReportFilter;
import com.mindbox.pe.model.grid.ParameterGrid;
import com.mindbox.pe.model.template.ParameterTemplate;

/**
 * Date synonym edit dialog.
 *
 * @author Geneho Kim
 * @since PowerEditor  4.2.0
 */
public class DateSynonymEditDialog extends JPanel {

	private class AcceptL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (updateFromGUI()) {
				try {
					dateSynonym.copyFrom(detailPanel.getDateSynonym());
					dialog.dispose();
				}
				catch (InputValidationException ex) {
					ClientUtil.getInstance().showErrorMessage(ex.getMessage());
				}
			}
		}
	}

	private class CancelL implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			detailPanel.setDateSynonym(null);
			dateSynonym = null;
			dialog.dispose();
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;

	public static DateSynonym copyDateSynonym(DateSynonym source) {
		DateSynonym dateSynonym = new DateSynonym(-1, source.getName(), source.getDescription(), source.getDate());
		return copyDateSynonym(JOptionPane.getFrameForComponent(ClientUtil.getApplet()), dateSynonym);
	}

	private static DateSynonym copyDateSynonym(Frame owner, DateSynonym dateSynonym) {
		return editDateSynonym(owner, dateSynonym, !ConfigUtil.allowsIdenticalDateSynonymDates(ClientUtil.getUserInterfaceConfig()));
	}

	private static Date createNewDate() {
		Calendar newDate = Calendar.getInstance();

		String defaultHour = ClientUtil.getUserSession().getDefaultHour();
		String defaultMin = ClientUtil.getUserSession().getDefaultMinute();

		int hour = UtilBase.isEmpty(defaultHour) ? newDate.get(Calendar.HOUR_OF_DAY) : Integer.parseInt(defaultHour);
		int min = UtilBase.isEmpty(defaultMin) ? newDate.get(Calendar.MINUTE) : Integer.parseInt(defaultMin);

		newDate.set(Calendar.MILLISECOND, 0);
		newDate.set(Calendar.SECOND, 0);
		newDate.set(Calendar.MINUTE, min);
		newDate.set(Calendar.HOUR_OF_DAY, hour);

		return newDate.getTime();
	}

	public static DateSynonym editDateSynonym(final Frame owner, final DateSynonym dateSynonym, final boolean validateNoDuplicateDates) {
		return editDateSynonym(owner, dateSynonym, false, validateNoDuplicateDates);
	}

	private static DateSynonym editDateSynonym(final Frame owner, final DateSynonym dateSynonym, final boolean forCopy, final boolean validateNoDuplicateDates) {
		JDialog dialog = new JDialog(owner, true);
		dialog.setTitle(forCopy ? ClientUtil.getInstance().getLabel("d.title.copy.dateSynonym") : ClientUtil.getInstance().getLabel("d.title.edit.dateSynonym"));
		DateSynonymEditDialog instance = new DateSynonymEditDialog(dialog, dateSynonym, validateNoDuplicateDates);
		UIFactory.addToDialog(dialog, instance);

		dialog.setVisible(true);

		return instance.dateSynonym;
	}

	private static boolean hasProductionRestrictions(DateSynonym dateSynonym) {
		try {
			if (dateSynonym.getId() == -1 || ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_EDIT_PRODUCTION_DATA)) {
				return false;
			}
			GuidelineReportFilter filter = new GuidelineReportFilter();
			filter.setIncludeEmptyContexts(false);
			filter.setChangesOnDate(dateSynonym.getDate());
			filter.addStatus(ClientUtil.getHighestStatus());

			if (ClientUtil.getCommunicator().search(filter).size() > 0) {
				return true;
			}

			// check parameter references
			List<ParameterTemplate> parameterTemplates = EntityModelCacheFactory.getInstance().getAllParameterTemplates();
			for (Iterator<ParameterTemplate> i = parameterTemplates.iterator(); i.hasNext();) {
				ParameterTemplate template = i.next();
				List<ParameterGrid> paramGridList = ClientUtil.getCommunicator().fetchParameters(template.getID());
				for (Iterator<ParameterGrid> it = paramGridList.iterator(); it.hasNext();) {
					ParameterGrid grid = it.next();
					if (ClientUtil.isHighestStatus(grid.getStatus()) && (UtilBase.isSame(grid.getEffectiveDate(), dateSynonym) || UtilBase.isSame(grid.getExpirationDate(), dateSynonym))) {
						return true;
					}
				}
			}
		}
		catch (ServerException e) {
			ClientUtil.handleRuntimeException(e);
		}
		return false;
	}

	private static boolean hasProductionRestrictions(DateSynonym[] dateSynonyms) {
		if (ClientUtil.checkPermissionByPrivilegeName(PrivilegeConstants.PRIV_EDIT_PRODUCTION_DATA)) {
			return false;
		}
		for (int i = 0; i < dateSynonyms.length; i++) {
			if (hasProductionRestrictions(dateSynonyms[i])) {
				return true;
			}
		}

		return false;
	}

	public static DateSynonym mergeDateSynonyms(final Frame owner, final DateSynonym[] dateSynonyms, final boolean validateNoDuplicateDates) {
		if (hasProductionRestrictions(dateSynonyms)) {
			ClientUtil.getInstance().showWarning("msg.warning.merge.datesynonym.production", new Object[] { ClientUtil.getHighestStatusDisplayLabel() });
			return null;
		}
		JDialog dialog = new JDialog(owner, true);
		dialog.setTitle(ClientUtil.getInstance().getLabel("d.title.merge.dateSynonym"));
		String instructions = (ClientUtil.getInstance().getMessage("msg.inst.merge.datesynonyms") + System.getProperty("line.separator") + namesToCsv(dateSynonyms));

		DateSynonymEditDialog instance = new DateSynonymEditDialog(
				dialog,
				new DateSynonym(-1, dateSynonyms[0].getName(), dateSynonyms[0].getDescription(), dateSynonyms[0].getDate()),
				instructions,
				false,
				validateNoDuplicateDates);
		UIFactory.addToDialog(dialog, instance);

		dialog.setVisible(true);

		return instance.dateSynonym;
	}

	private static String namesToCsv(DateSynonym[] syns) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < syns.length; i++) {
			sb.append(syns[i].getName());
			if (i < syns.length - 1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	public static DateSynonym newDateSynonym(final Frame owner, final boolean validateNoDuplicateDates) {
		JDialog dialog = new JDialog(owner, true);
		dialog.setTitle(ClientUtil.getInstance().getLabel("d.title.new.dateSynonym"));

		DateSynonymEditDialog instance = new DateSynonymEditDialog(dialog, new DateSynonym(-1, "", "", createNewDate()), validateNoDuplicateDates);
		UIFactory.addToDialog(dialog, instance);

		dialog.setVisible(true);

		return instance.dateSynonym;
	}

	private final JDialog dialog;
	private final DateSynonymDetailPanel detailPanel;
	private DateSynonym dateSynonym;
	private String instructions;
	private final boolean validateNoDuplicateNames;
	private final boolean validateNoDuplicateDates;
	private final boolean hasProductionRestrictions;

	private DateSynonymEditDialog(JDialog dialog, DateSynonym dateSynonym, final boolean validateNoDuplicateDates) {
		this(dialog, dateSynonym, null, true, validateNoDuplicateDates);
	}

	private DateSynonymEditDialog(JDialog dialog, DateSynonym dateSynonym, String instructions, final boolean validateNoDuplicateNames, final boolean validateNoDuplicateDates) {
		this.dialog = dialog;
		this.dateSynonym = dateSynonym;
		this.detailPanel = new DateSynonymDetailPanel();
		this.instructions = instructions;
		this.validateNoDuplicateNames = validateNoDuplicateNames;
		this.validateNoDuplicateDates = validateNoDuplicateDates;
		this.hasProductionRestrictions = hasProductionRestrictions(dateSynonym);

		setSize(400, 250);
		DateSynonym tempDS = new DateSynonym(dateSynonym.getId(), dateSynonym.getName(), dateSynonym.getDescription(), dateSynonym.getDate());
		detailPanel.setDateSynonym(tempDS);

		if (hasProductionRestrictions) {
			if (dateSynonym.getDate().before(new Date())) {
				detailPanel.setDateFieldEnabled(false);
				this.instructions = ClientUtil.getInstance().getMessage("msg.warning.production.datesynonym.past", new Object[] { ClientUtil.getHighestStatusDisplayLabel() });
			}
			else {
				this.instructions = ClientUtil.getInstance().getMessage("msg.warning.production.datesynonym.future", new Object[] { ClientUtil.getHighestStatusDisplayLabel() });
			}
		}
		initPanel();
	}

	private void initPanel() {
		JButton createButton = new JButton("Accept");
		createButton.addActionListener(new AcceptL());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new CancelL());

		JPanel buttonPanel = UIFactory.createJPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(createButton);
		buttonPanel.add(cancelButton);

		setLayout(new BorderLayout(4, 4));
		if (!UtilBase.isEmpty(instructions)) {
			JTextArea txtArea = new JTextArea(instructions, 3, 40);
			txtArea.setLineWrap(true);
			txtArea.setWrapStyleWord(true);
			txtArea.setEditable(false);
			JScrollPane scrollPane = new JScrollPane(txtArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			add(scrollPane, BorderLayout.NORTH);
		}
		add(detailPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	private boolean updateFromGUI() {
		final DateSynonym dateSynonymFromPanel;
		try {
			dateSynonymFromPanel = detailPanel.getDateSynonym();
		}
		catch (InputValidationException ex) {
			ClientUtil.getInstance().showErrorMessage(ex.getMessage());
			return false;
		}

		if (dateSynonymFromPanel.getName() == null || dateSynonymFromPanel.getName().length() == 0) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { ClientUtil.getInstance().getLabel("label.name") });
			return false;
		}
		if (dateSynonymFromPanel.getDate() == null) {
			ClientUtil.getInstance().showWarning("msg.warning.empty.field", new Object[] { ClientUtil.getInstance().getLabel("label.date") });
			return false;
		}

		if (validateNoDuplicateDates || validateNoDuplicateNames) {
			try {
				List<DateSynonym> allDateSynonyms = ClientUtil.getCommunicator().search(new AllNamedDateSynonymFilter());
				for (Iterator<DateSynonym> i = allDateSynonyms.iterator(); i.hasNext();) {
					DateSynonym dateSynonym = i.next();
					if (!dateSynonym.equals(dateSynonymFromPanel)) {
						if (validateNoDuplicateNames && dateSynonym.getName().equals(dateSynonymFromPanel.getName())) {
							ClientUtil.getInstance().showWarning("msg.warning.duplicate.entity.name", new Object[] { "Date Synonym", dateSynonymFromPanel.getName() });
							return false;
						}
						else if (validateNoDuplicateDates && dateSynonym.getDate().equals(dateSynonymFromPanel.getDate())) {
							ClientUtil.getInstance().showWarning("msg.warning.duplicate.entity.date", new Object[] { "Date Synonym", dateSynonym.getName() });
							return false;
						}
					}
				}
			}
			catch (Exception x) {
				ClientUtil.handleRuntimeException(x);
				return false;
			}
		}

		final boolean dateHasChanged = !dateSynonym.getDate().equals(dateSynonymFromPanel.getDate());

		if (hasProductionRestrictions(dateSynonym) && dateHasChanged && dateSynonymFromPanel.getDate().before(new Date())) {
			ClientUtil.getInstance().showWarning("msg.warning.production.datesynonym.future", new Object[] { ClientUtil.getHighestStatusDisplayLabel() });
			return false;
		}

		// Validate the date change (TT-75), for edit only
		if (dateSynonym.getID() > 0 && dateHasChanged) {
			try {
				final List<GuidelineReportData> wouldBeInvalidGuidelines = ClientUtil.getCommunicator().validateDateSynonymDateChange(dateSynonym.getID(), dateSynonymFromPanel.getDate());
				if (!wouldBeInvalidGuidelines.isEmpty()) {
					final JPanel labelPanel = UIFactory.createBorderLayoutPanel(2, 2);
					labelPanel.add(new JLabel(ClientUtil.getInstance().getMessage("msg.warning.invalid.datesynonym.date", dateSynonymFromPanel.getName())), BorderLayout.CENTER);

					final TemplateReportTableModel guidelineReportTableModel = new TemplateReportTableModel();
					guidelineReportTableModel.setDataList(wouldBeInvalidGuidelines);
					final TemplateReportTable guidelineReportTable = new TemplateReportTable(guidelineReportTableModel);

					final JPanel panel = UIFactory.createBorderLayoutPanel(4, 4);
					panel.add(labelPanel, BorderLayout.NORTH);
					panel.add(new JScrollPane(guidelineReportTable), BorderLayout.CENTER);

					ClientUtil.getInstance().showAsDialog("d.title.validation.error", true, panel, true);
					return false;
				}
			}
			catch (ServerException e) {
				ClientUtil.handleRuntimeException(e);
				return false;
			}
		}

		return true;
	}
}

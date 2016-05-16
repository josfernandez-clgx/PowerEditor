package com.mindbox.pe.client.common.dialog;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;

import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.model.Constants;

import mseries.Calendar.MDateChanger;
import mseries.Calendar.MDefaultPullDownConstraints;
import mseries.Calendar.MFieldListener;
import mseries.ui.MChangeEvent;
import mseries.ui.MChangeListener;
import mseries.ui.MDateEntryField;


/**
 * Date field based on MDateEntryField.
 *
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 1.2.0
 */
public final class MDateDateField extends MDateEntryField {

	private final class AllowSecondsInDateL implements MChangeListener {
		@Override
		public void valueChanged(MChangeEvent arg0) {
			if (arg0.getType() == MChangeEvent.CHANGE) {
				Calendar cal = Calendar.getInstance();
				cal.setTime((Date) arg0.getValue());
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				((MDateDateField) arg0.getSource()).setValue(cal.getTime());
			}
		}
	}

	private final class ClearEmptyFieldL implements MFieldListener {
		@Override
		public void fieldEntered(FocusEvent arg0) {
		}

		@Override
		public void fieldExited(FocusEvent arg0) {
			String text = ((MDateDateField) arg0.getComponent()).getText();
			if (text == null || text.trim().length() == 0) {
				((MDateDateField) arg0.getComponent()).setValue(null);
				MDateDateField.this.setValue(null);
			}
		}
	}

	private final class ClearL implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			MDateDateField.this.setValue(null);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3951228734910107454L;
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

	private static SimpleDateFormat findDateFormat(boolean dateAndSeconds) {
		if (dateAndSeconds) {
			return new SimpleDateFormat(Constants.FORMAT_STR_DATE_TIME_SEC);
		}
		else {
			return new SimpleDateFormat(Constants.FORMAT_STR_DATE);
		}
	}


	private final SimpleDateFormat dateFormat;
	private final boolean forTime;
	private final boolean allowSecondsInDate;
	private JPanel panel = null;
	private JLabel timezoneLabel = null;
	private JTextField timeField = null;
	private final boolean showClearButton;
	private final JButton clearButton;

	public MDateDateField(boolean forTime) {
		this(forTime, false);
	}

	public MDateDateField(boolean forTime, boolean showClearButton) {
		this(forTime, showClearButton, false);
	}

	/**
	 * @param forTime forTime
	 * @param showClearButton showClearButton
	 * @param allowSecondsInDate allowSecondsInDate
	 * @since PowerEditor 3.2.0
	 */
	public MDateDateField(boolean forTime, boolean showClearButton, boolean allowSecondsInDate) {
		super(findDateFormat(allowSecondsInDate));
		this.forTime = forTime && !allowSecondsInDate;
		this.showClearButton = showClearButton;
		this.allowSecondsInDate = allowSecondsInDate;

		if (showClearButton) {
			clearButton = UIFactory.createJButton(null, "image.btn.small.clear", new ClearL(), "button.tooltip.clear.value");
		}
		else {
			clearButton = null;
		}

		final String formatStr = (!allowSecondsInDate) ? Constants.FORMAT_STR_DATE : Constants.FORMAT_STR_DATE_TIME_SEC;
		this.dateFormat = new SimpleDateFormat(formatStr);
		init();
	}

	public void addChangeListener(final DocumentListener documentListener, final MChangeListener mchangeListener) {
		addMChangeListener(mchangeListener);
		super.display.getDocument().addDocumentListener(documentListener);
		if (timeField != null) {
			timeField.getDocument().addDocumentListener(documentListener);
		}
	}

	public final Date formatToDate(String s) {
		Date result = null;
		if (s != null && s.length() > 0) {
			try {
				if (forTime || allowSecondsInDate) {
					result = Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().parse(s);
				}
				else {
					result = Constants.THREADLOCAL_FORMAT_DATE.get().parse(s);
				}
			}
			catch (Exception ex) {
			}
		}
		return result;
	}

	public final String formatToString(Date date) {
		return (forTime ? Constants.THREADLOCAL_FORMAT_DATE_TIME_SEC.get().format(date) : dateFormat.format(date));
	}


	public final Date getDate() {
		try {
			Date value = getValue();

			if (value != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(value);

				if (!forTime && !allowSecondsInDate) {
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
				}
				else if (!allowSecondsInDate) {
					String timeStr = getTimeField().getText();

					if ((timeStr != null) && (timeStr.length() > 0)) {
						String[] strs = timeStr.split(":");

						if (strs.length > 0) {
							cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strs[0]));
						}

						if (strs.length > 1) {
							cal.set(Calendar.MINUTE, Integer.parseInt(strs[1]));
						}

						if (strs.length > 2) {
							cal.set(Calendar.SECOND, Integer.parseInt(strs[2]));
						}
					}
					else {
						cal.set(Calendar.HOUR_OF_DAY, 0);
						cal.set(Calendar.MINUTE, 0);
						cal.set(Calendar.SECOND, 0);
					}
				}

				return cal.getTime();
			}

			return value;
		}
		catch (Exception ex) {
			return null;
		}
	}

	public JComponent getJComponent() {
		if (forTime) {
			if (panel == null) {
				timezoneLabel = new JLabel("(" + TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT) + ")");
				panel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
				panel.add(this);
				panel.add(getTimeField());
				getTimeField().setPreferredSize(new Dimension(getTimeField().getPreferredSize().width, this.getPreferredSize().height));
				panel.add(timezoneLabel);

				if (showClearButton) {
					panel.add(clearButton);
				}

				panel.setBackground(PowerEditorSwingTheme.shadowColor);
			}

			return panel;
		}
		else {
			if (showClearButton) {
				if (panel == null) {
					panel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
					panel.add(this);
					panel.add(clearButton);
					panel.setBackground(PowerEditorSwingTheme.shadowColor);
				}

				return panel;
			}

			return this;
		}
	}

	@Override
	public Dimension getMinimumSize() {
		Dimension size = getPreferredSize();
		size.height -= 2;

		return size;
	}

	private JTextField getTimeField() {
		if (timeField == null) {
			timeField = new JTextField(8);
			timeField.setText("00:00:00");
		}

		return timeField;
	}

	@Override
	public Date getValue() throws ParseException {
		return super.getValue();
	}

	private void init() {
		MDefaultPullDownConstraints c = new MDefaultPullDownConstraints();
		c.firstDay = Calendar.SUNDAY;
		c.changerStyle = MDateChanger.BUTTON;
		c.hasShadow = true;
		c.selectionClickCount = 1;

		setConstraints(c);
		setShowTodayButton(true, true);
		this.addMFieldListener(new ClearEmptyFieldL());
		if (allowSecondsInDate) {
			this.addMChangeListener(new AllowSecondsInDateL());
		}
	}

	public void removeChangeListener(final DocumentListener documentListener, final MChangeListener mchangeListener) {
		removeMChangeListener(mchangeListener);
		super.display.getDocument().removeDocumentListener(documentListener);
		if (timeField != null) {
			timeField.getDocument().removeDocumentListener(documentListener);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.setEditable(enabled);
		if (clearButton != null) {
			clearButton.setEnabled(enabled);
		}
		if (timeField != null) {
			timeField.setEnabled(enabled);
		}
	}

	@Override
	public void setValue(Date arg0) {
		super.setValue(arg0);

		if (forTime) {
			if (arg0 == null) {
				getTimeField().setText("00:00:00");
			}
			else {
				getTimeField().setText(timeFormat.format(arg0));
			}
		}
	}

}

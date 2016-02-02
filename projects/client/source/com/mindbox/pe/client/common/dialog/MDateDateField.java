package com.mindbox.pe.client.common.dialog;

import com.mindbox.pe.client.applet.PowerEditorSwingTheme;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.common.config.UIConfiguration;

import mseries.Calendar.MDateChanger;
import mseries.Calendar.MDefaultPullDownConstraints;
import mseries.Calendar.MFieldListener;

import mseries.ui.MChangeEvent;
import mseries.ui.MChangeListener;
import mseries.ui.MDateEntryField;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * Date field based on MDateEntryField.
 *
 * @author Geneho Kim
 * @author MindBox
 * @since PowerEditor 1.2.0
 */
public final class MDateDateField extends MDateEntryField {
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat(
            "HH:mm:ss");
    private final SimpleDateFormat dateFormat;
    private final boolean forTime;
    private final boolean allowSecondsInDate;    
    private JPanel panel = null;
    private JLabel timezoneLabel = null;

    private JTextField timeField = null;
    private final boolean showClearButton;
    private final JButton clearButton;
   

    /**
     *
     * @param forTime
     * @param showClearButton
     * @since PowerEditor 3.2.0
     */
    public MDateDateField(boolean forTime, boolean showClearButton, boolean allowSecondsInDate) {
        super(findDateFormat(allowSecondsInDate));
        this.forTime = forTime && !allowSecondsInDate;
        this.showClearButton = showClearButton;
        this.allowSecondsInDate = allowSecondsInDate;        

        if (showClearButton) {
            clearButton = UIFactory.createJButton(null,
                    "image.btn.small.clear", new ClearL(), "button.tooltip.clear.value");
        } else {
            clearButton = null;
        }

        
        String formatStr = (!allowSecondsInDate) ? UIConfiguration.FORMAT_STR_DATE : 
            UIConfiguration.FORMAT_STR_DATE_TIME_SEC;
        this.dateFormat = new SimpleDateFormat(formatStr);
        init();
    }

    public MDateDateField(boolean forTime) {
        this(forTime, false);
    }

   public MDateDateField(boolean forTime, boolean showClearButton) {    
       this(forTime, showClearButton, false);
   }

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

    private static SimpleDateFormat findDateFormat(boolean dateAndSeconds) {
         if (dateAndSeconds) {
            return UIConfiguration.FORMAT_DATE_TIME_SEC;
        } else {
            return UIConfiguration.FORMAT_DATE;            
        }
    }

    private JTextField getTimeField() {
        if (timeField == null) {
            timeField = new JTextField(8);
            timeField.setText("00:00:00");
        }

        return timeField;
    }

    public JComponent getJComponent() {
        if (forTime) {
            if (panel == null) {
                timezoneLabel = new JLabel("(" +
                        TimeZone.getDefault().getDisplayName(false,
                            TimeZone.SHORT) + ")");
                panel = UIFactory.createJPanel(new FlowLayout(FlowLayout.LEFT,
                            2, 2));
                panel.add(this);
                panel.add(getTimeField());
                getTimeField().setPreferredSize(new Dimension(
                        getTimeField().getPreferredSize().width,
                        this.getPreferredSize().height));
                panel.add(timezoneLabel);

                if (showClearButton) {
                    panel.add(clearButton);
                }

                panel.setBackground(PowerEditorSwingTheme.shadowColor);
            }

            return panel;
        } else {
            if (showClearButton) {
                if (panel == null) {
                    panel = UIFactory.createJPanel(new FlowLayout(
                                FlowLayout.LEFT, 2, 2));
                    panel.add(this);
                    panel.add(clearButton);
                    panel.setBackground(PowerEditorSwingTheme.shadowColor);
                }

                return panel;
            }

            return this;
        }
    }

    public final String formatToString(Date date) {
        return (forTime ? UIConfiguration.FORMAT_DATE_TIME_SEC.format(date)
                        : dateFormat.format(date));
    }

    public final Date formatToDate(String s) {
        Date result = null;
        if (s != null && s.length() > 0) {
            try {
                if (forTime || allowSecondsInDate) {
                    result = UIConfiguration.FORMAT_DATE_TIME_SEC.parse(s);
                } else {
                    result = UIConfiguration.FORMAT_DATE.parse(s);
                }
            } catch (Exception ex) {
            }
        }
        return result;
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
                } else if (!allowSecondsInDate) {
                    String timeStr = getTimeField().getText();

                    if ((timeStr != null) && (timeStr.length() > 0)) {
                        String[] strs = timeStr.split(":");

                        if (strs.length > 0) {
                            cal.set(Calendar.HOUR_OF_DAY,
                                Integer.parseInt(strs[0]));
                        }

                        if (strs.length > 1) {
                            cal.set(Calendar.MINUTE, Integer.parseInt(strs[1]));
                        }

                        if (strs.length > 2) {
                            cal.set(Calendar.SECOND, Integer.parseInt(strs[2]));
                        }
                    } else {
                        cal.set(Calendar.HOUR_OF_DAY, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                    }
                }

                return cal.getTime();
            }

            return value;
        } catch (Exception ex) {
            return null;
        }
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

    public Date getValue() throws ParseException {
        return super.getValue();
    }

    public Dimension getMinimumSize() {
        Dimension size = getPreferredSize();
        size.height -= 2;

        return size;
    }

    public void setValue(Date arg0) {
        super.setValue(arg0);

        if (forTime) {
            if (arg0 == null) {
                getTimeField().setText("00:00:00");
            } else {
                getTimeField().setText(timeFormat.format(arg0));
            }
        }
    }

    public static final class Test {
        public static void main(String[] args) {
            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(2, 2));

            JLabel label = new JLabel(" Date: ");
            panel.add(label);

            MDateDateField dateField = new MDateDateField(false, true);
            panel.add(dateField.getJComponent());
            label = new JLabel(" DateTime: ");
            panel.add(label);

            MDateDateField dateTimeField = new MDateDateField(true, true);
            panel.add(dateTimeField.getJComponent());

            JOptionPane.showMessageDialog(null, panel, "DATE TESTER",
                JOptionPane.PLAIN_MESSAGE);

            System.exit(0);
        }
    }

    private final class ClearL implements ActionListener {
        public void actionPerformed(ActionEvent arg0) {
            MDateDateField.this.setValue(null);
        }
    }

    private final class ClearEmptyFieldL implements MFieldListener  {
        public void fieldEntered(FocusEvent arg0) {
        }

        public void fieldExited(FocusEvent arg0) {
            String text = ((MDateDateField)arg0.getComponent()).getText();
            if (text == null || text.trim().length() == 0) {
                ((MDateDateField)arg0.getComponent()).setValue(null);
                MDateDateField.this.setValue(null);
            }
        }
    }

    private final class AllowSecondsInDateL implements MChangeListener {
        public void valueChanged(MChangeEvent arg0) {
            if (arg0.getType() == MChangeEvent.CHANGE) {
                Calendar cal = Calendar.getInstance();
                cal.setTime((Date)arg0.getValue());
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                ((MDateDateField)arg0.getSource()).setValue(cal.getTime());
            }
        }
    }
    
}

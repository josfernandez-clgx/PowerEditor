/*
 * Created on 2004. 12. 3.
 *
 */
package com.mindbox.pe.client.common;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import com.mindbox.pe.client.ClientUtil;
import com.mindbox.pe.client.EntityModelCacheFactory;
import com.mindbox.pe.client.applet.UIFactory;
import com.mindbox.pe.client.common.dialog.DateSynonymEditDialog;
import com.mindbox.pe.model.DateSynonym;


/**
 * Date selector field.
 * @author Geneho Kim
 * @since PowerEditor 4.2.0
 */
public class DateSelectorComboField extends JPanel implements ActionListener {

	public static class Test {

		public static void main(String[] args) {
			JFrame frame = new JFrame(/*"New Date Synonym");//*/"DateComboField Test");
			frame.setBounds(100, 100, 600, 400);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			DateSelectorComboField dateField = new DateSelectorComboField();

			frame.getContentPane().setLayout(new BorderLayout(12, 12));
			frame.getContentPane().add(dateField, BorderLayout.NORTH);
			frame.setVisible(true);
		}
	}

	private final boolean allowNewDates;

	private final JButton newButton, deleteButton;
	private DateSynonymComboBox dsCombo;
	private Date minDate, maxDate = null;



	/**
	 * Calling this is equivalent to <code>new DateSelectorComboField(true, true)</code>.
	 *
	 */
	public DateSelectorComboField() {
		this(true, true, true);
	}

	/**
	 * Creates a new date selector combo field.
	 * @param allowNewDates <code>true</code> iff this should allow users to create new date synonyms on the fly
	 * @param hasEmpty
	 */
	public DateSelectorComboField(boolean allowNewDates, boolean hasEmpty, boolean createModel) {
		UIFactory.setLookAndFeel(this);
		dsCombo = new DateSynonymComboBox(hasEmpty, createModel);
		this.allowNewDates = allowNewDates;

		newButton = UIFactory.createButton(null, "image.btn.small.new", this, "button.tooltip.new.dateSynonym", false);
		deleteButton = UIFactory.createButton(null, "image.btn.small.remove", this, "button.tooltip.delete.dateSynonym", false);

		initPanel();
	}

    public void refresh(boolean showName) {
    	dsCombo.refresh(showName);
    }
    
	public final void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == newButton) {
			createNew();
		}
		else if (arg0.getSource() == deleteButton) {
			dsCombo.setSelectedItem(null);
		}
	}

	public void addActionListener(ActionListener l) {
		dsCombo.addActionListener(l);
	}

	public void removeActionListener(ActionListener l) {
		dsCombo.removeActionListener(l);
	}



	private synchronized void createNew() {
		DateSynonym newDateSynonym = DateSynonymEditDialog.newDateSynonym(
                JOptionPane.getFrameForComponent(ClientUtil.getApplet()));
		if (newDateSynonym != null) {
			try {
				int newID = ClientUtil.getCommunicator().save(newDateSynonym, false);
				newDateSynonym.setID(newID);
				EntityModelCacheFactory.getInstance().addDateSynonym(newDateSynonym);
				dsCombo.setValue(newDateSynonym);
			}
			catch (Exception ex) {
				ClientUtil.handleRuntimeException(ex);
			}
		}
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		dsCombo.setEnabled(enabled);
		newButton.setEnabled(enabled);
		deleteButton.setEnabled(enabled);
	}

	public Date getMinimum() {
		return minDate;
	}

	public void setMinimum(Date date) {
		this.minDate = date;
	}

	public Date getMaximum() {
		return maxDate;
	}

	public void setMaximum(Date date) {
		this.maxDate = date;
	}


	private void initPanel() {
		JPanel rightPanel = UIFactory.createFlowLayoutPanel(FlowLayout.RIGHT, 1, 1);
		rightPanel.add(deleteButton);
		if (allowNewDates) {
			rightPanel.add(new JSeparator());
			rightPanel.add(newButton);
		}

		setLayout(new BorderLayout(0, 0));
		add(dsCombo, BorderLayout.CENTER);
		add(rightPanel, BorderLayout.EAST);
	}

	public synchronized Date getDate() {
		return dsCombo.getDate();
	}

	public synchronized DateSynonym getValue() {
		return dsCombo.getValue();
	}

	/**
	 * Sets the date displayed in this field.
	 * @param date if <code>null</code>, clears this field
	 */
	public synchronized void setDate(Date date) {
		dsCombo.setDate(date);
	}

	/**
	 * Sets the value of this control to the specified date synonym.
	 * Pass <code>null</code> to clear this field.
	 * @param dateSynonym the date synonym; can be <code>null</code>
	 */
	public synchronized void setValue(DateSynonym dateSynonym) {
		dsCombo.setValue(dateSynonym);
	}


}
